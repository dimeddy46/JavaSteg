package stegSource;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Watermark 
{
	 private static float standardQuant[][] = 
		 {
			{16, 11, 10, 16, 24, 40, 51, 61},
			{12, 12, 14, 19, 26, 58, 60, 55},
			{14, 13, 16, 24, 40, 57, 69, 56},
			{14, 17, 22, 29, 51, 87, 80, 62},
			{18, 22, 37, 56, 68, 109, 103, 77},
			{24, 35, 55, 64, 81, 104, 113, 92},
			{49, 64, 78, 87, 103, 121, 120, 101},
			{72, 92, 95, 98, 112, 100, 103, 99}
		};	 
	 static String stats;
	 static double probability = -1;
	 
	 private static boolean safeGuard(double[] coef)
	 {
		   double safe;
		   final double dist = 0.2;
		   // get decimals
		   safe = Math.abs(coef[0] - (coef[0] > 0? Math.floor(coef[0]): Math.ceil(coef[0])) );
		  
		   if(safe > 0.4 && safe < 0.5)
		   {			   
			   coef[0] += coef[0] > 0?  -dist: +dist;		// move away from center and don't affect rounding		   
			   return true;
		   }
		   else if(safe >= 0.5 && safe < 0.6)
		   {
			   coef[0] += coef[0] > 0?  +dist: -dist;	
			   return true;
			}
		   else return false;
	 }
	 
	public static Mat hideDCT(Mat cov, String text)
	{		   
		   int i, j, q, p, ct = 0;
		   byte bit;
		   double[] coef = new double[1];
		   boolean full = false;
		   Mat sub, quant = new Mat(8, 8, CvType.CV_32FC1);
		   
		   text = "\\*$"+text;		// delimiter for marker finding
		   for (i = 0;i<8;i++)	// initialise quantizier
			   quant.put(i,0, standardQuant[i]);
		   
		   char[] msg = new char[text.length()];	   
		   text.getChars(0, text.length(), msg, 0);
		   
		//   Core.divide(quant, new Scalar(2), quant);
		   System.out.println(quant.dump());
		   List<Mat> spl = new ArrayList<Mat>();	   
		   cov.convertTo(cov, CvType.CV_32FC3);
		   Core.split(cov, spl);
		   
		   for(i = 0;i<cov.rows()-10;i+=8)
			   for(j = 0;j<cov.cols()-10;j+=8)
			   {				   
				   sub = spl.get(0).submat(i, i+8, j, j+8);// divide image into 8x8 blocks
				   Core.dct(sub, sub);			// convert to frequency domain
				   Core.divide(sub, quant, sub);// quantise result				
				   for(q = 1;q<8;q++)			// start from 2nd row and column, because altering coefficients from
					   for(p = 1;p<8;p++)		// first ones would severely impact image quality
					   {
						   // get channel intensity
						   coef[0] = sub.get(q, p)[0];  						   

						   // check if coef is close to middle, then it's not "safe", alter it
						   // to avoid any DCT rounding problems at extraction
						   if(safeGuard(coef))
							   sub.put(q, p, coef[0]);
						   
						   coef[0] = Math.round(coef[0]);									   
						   // get coefs != 1 and 0
						   if((coef[0] <= -1 || coef[0] >= 1) && full == false && p + q != 0 )
						   {
							   // get 1 bit from current letter and repeat until all quantized DCT blocks get exahausted
							   bit = (byte) ((msg[ct / 8 % msg.length] >> ct % 8) & 1); 
							   
							   // to write 0 -> coef must be negative, 1 -> positive
							   if(bit == 0){
								   if(coef[0] > 0)
									   coef[0] = -coef[0];
							   }
							   else {
								   if(coef[0] < 0)
									   coef[0] = -coef[0];
							   }				   						   
							   sub.put(q, p, coef[0]);
							   
							   // modify only first 3 coefs from each block and don't skip to end of block
							   // because there may be other "unsafe" values
							   if(++ct % 2 == 0)		
								   full = true;							   
						   }
					   }
				   full = false;					   
				   Core.multiply(sub, quant, sub);	// restore from quantised DCT values to DCT
				   Core.idct(sub, sub);				// convert back to spatial domain	    			   
			   }				   
		   Core.merge(spl, cov);
		   return cov;			  
	}
	
	public static String extDCT(Mat cov, int startX, int startY)
	{	
		   int i, j, q, p, ct = 0;  
		   double coef;
		   BitSet msg = new BitSet();
		   Mat sub, quant = new Mat(8, 8, CvType.CV_32FC1);	
		   
		   // initialize quantizier	   
		   for (i = 0;i<8;i++)	
			   quant.put(i,0, standardQuant[i]);
		   
	//	   Core.divide(quant, new Scalar(2), quant);
		   List<Mat> spl = new ArrayList<Mat>();	   
		   cov.convertTo(cov, CvType.CV_32FC3);
		   Core.split(cov, spl);
	   
		   for(i = startX;i<cov.rows()-10;i+=8)
			   for(j = startY;j<cov.cols()-10;j+=8)
			   {			   			   
				   sub = spl.get(0).submat(i,i+8,j,j+8);		   				  			   
				   Core.dct(sub, sub);				       
				   Core.divide(sub, quant, sub);
					  
				   for(q = 1;q<8;q++)
					   for(p = 1;p<8;p++)
					   {	
						   coef = Math.round(sub.get(q, p)[0]);
						   if((coef <= -1 || coef >= 1) && p + q != 0)
						   { 	
							    if(coef > 0)		// set bits to obtain the encoded watermark
							    	msg.set(ct);	// (coef > 0 -> 1 ; coef < 0 -> 0)   
							    	
								if(++ct % 2 == 0){	// max 3 bits per block, then skip to end of block
									 p = 8; q = 8;
								}
							}						   
					    }
			   }   
		   return new String(msg.toByteArray());
	}
	public static String getMarkedString(String extr)
	{
		   String[] occ = new String[30];
		   String mark = "";
		   int space = 0, i = 0, j = 0, max = -1, crt = 0;
		   
		   while(space != 30 && i != -1 && j != -2)	// get first 30 occurences that are compressed between delimiters
		   {			   
		   	   i = extr.indexOf("\\*$", j);			// find marking if possible
			   j = extr.indexOf("\\*$", i+1);		// get mark length by checking where the next delimiter is placed
			   
			   if(j != -1 && i != -1 && j - i < 22)		
				  occ[space++] = extr.substring(i+3, j); // cut delimiter from mark and add it to occurences			   
			   j--;
		   }
		   
		   if(space != 0)
		   {
			   System.out.println("\nSTART \n");
			   for(i = 0;i<space; i++)
			   {	
				   System.out.println(occ[i]);
				   if(occ[i] == null)
					   continue;		
				   
				   for(j = 0;j < space; j++)
					   if(i != j && occ[j] != null && occ[i].equals(occ[j]))					   
						   crt++;
					   
				   if(crt > max)
				   {
				   		mark = occ[i];
				   		max = crt;
				   }
				   crt = 0;
			   }
		   }
		   return mark;
	}
	public static String getStatistics(String extr)
	{  
		   int i, crt = 0, lenExtr = extr.length(), lenMark = 0;
		   int[] freq = new int[256];
		   double prob, total = 0;
		   String formated = "TOTAL CHARACTERS EXTRACTED: "+lenExtr+"\n\n";
		   
		//   for(i = 1;i<=extr.length()/100;i++)
		//	   	System.out.println(i+" "+ extr.substring(100*(i-1), 100*i));
		//  System.out.println(i+" "+ extr.substring(100*(i-1), 100*(i-1)+ extr.length()-100*(i-1)));
		   
		   String mark = getMarkedString(extr);
		   if(mark != null)
			   lenMark = mark.length();
		   
		   if(lenMark == 0)					// couldn't get mark
		   {
			   for(i = 0;i < lenExtr;i++)			// create array of char frequency
			   {
				   crt = (int)extr.charAt(i);
				   if(crt >= 0 && crt <= 255)
					   freq[crt]++;
			   }
			   
			   for(i = 0;i<=255;i++)
			   {	
				   prob = freq[i]*1.0 / lenExtr;	// a unmarked image has lots of random values, 
				   if(prob > 0.01)					// none can pass 0.01 in frequency if not watermarked
				   {
					  formated = String.format("%sPROB: %.3f, FREQUENCY: %d, DEC: %d, ASC: %c\n", 
							  formated, prob, freq[i], i, i); 
					  total += freq[i];
				   }
			   }
		   }
		   formated = String.format("%s\nEXTRACTED MARK: %s %s\n", 
				   formated, lenMark != 0? mark : "Unknown string..", (lenExtr < 200)? "(Not accurate!)":"");
		   
		   total = lenMark == 0? total/lenExtr*100.0 : 100.0;

		   formated = String.format("%s\n****************************************\n"
		   							  + "TOTAL WATERMARK PROBABILITY: : %.2f%% %s"
		   						    + "\n****************************************", 
		   						    formated, lenExtr == 0? 0 : total, (lenExtr < 200)? "(Not accurate!)":"");
		   
		   if(total > probability)
		   {
			   probability = total;
			   stats = formated;
		   }
		   return formated;
	}
}
