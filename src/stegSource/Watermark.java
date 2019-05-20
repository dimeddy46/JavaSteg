package stegSource;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Watermark {
	 private static float standardQuant[][] = {
			{16, 11, 10, 16, 24, 40, 51, 61},
			{12, 12, 14, 19, 26, 58, 60, 55},
			{14, 13, 16, 24, 40, 57, 69, 56},
			{14, 17, 22, 29, 51, 87, 80, 62},
			{18, 22, 37, 56, 68, 109, 103, 77},
			{24, 35, 55, 64, 81, 104, 113, 92},
			{49, 64, 78, 87, 103, 121, 120, 101},
			{72, 92, 95, 98, 112, 100, 103, 99}
	 };	 
	 
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
		   
		   for (i = 0;i<8;i++)	// initialise quantizier
			   quant.put(i,0, standardQuant[i]);
		   
		   char[] msg = new char[text.length()];	   
		   text.getChars(0, text.length(), msg, 0);
		   
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
						   if((coef[0] <= -1 || coef[0] >= 1) && full == false && p + q != 0 )//&& ct/8 != msg.length) 
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
							   if(++ct % 3 == 0)		
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
	
	public static String extDCT(Mat cov)
	{	
		   int i, j, q, p, ct = 0;  
		   double coef;
		   BitSet msg = new BitSet();
		   Mat sub, quant = new Mat(8, 8, CvType.CV_32FC1);	
		   
		   // initialize quantizier	   
		   for (i = 0;i<8;i++)	
			   quant.put(i,0, standardQuant[i]);
		   
		   List<Mat> spl = new ArrayList<Mat>();	   
		   cov.convertTo(cov, CvType.CV_32FC3);
		   Core.split(cov, spl);
	   
		   for(i = 0;i<cov.rows()-10;i+=8)
			   for(j = 0;j<cov.cols()-10;j+=8)
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
							    	
								if(++ct % 3 == 0){	// max 3 bits per block, then skip to end of block
									 p = 8; q = 8;
								}
							}						   
					    }
			   }   
		   return new String(msg.toByteArray());
	}
	
	public static String getStatistics(String extr)
	{  
		   int i, crt, len = extr.length();
		   int[] freq = new int[256];
		   double prob, total = 0;
		   String formated = "TOTAL CHARACTERS EXTRACTED: "+len+"\n\n";
		   
		/*   for(i = 1;i<=extr.length()/100;i++)
			   	System.out.println(i+" "+ p.substring(100*(i-1), 100*i));
		   System.out.println(i+" "+ p.substring(100*(i-1), 100*(i-1)+ p.length()-100*(i-1)));
		*/
		   for(i = 0;i<len;i++)			// create array of char frequency
		   {
			   crt = (int)extr.charAt(i);
			   if(crt >= 0 && crt <= 255)
				   freq[crt]++;
		   }
		   
		   for(i = 0;i<=255;i++)
		   {	
			   prob = freq[i]*1.0 / len;	// a unmarked image has lots of random values, 
			   if(prob > 0.01)				// none can pass 0.01 in frequency if not watermarked
			   {
				  formated = String.format("%sPROB: %.3f, FREQUENCY: %d, DEC: %d, ASC: %c\n", formated, prob, freq[i], i, i); 
				  total += freq[i];
			   }
		   }
		   formated = String.format("%s\n****************************************\n"
		   							  + "TOTAL WATERMARK PROBABILITY: : %.3f%%"
		   						    + "\n****************************************", 
		   						    formated, total/len*100);
		   return formated;
	}
}
