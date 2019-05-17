package stegSource;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;

public class Watermark {
	 static float standardQuant[][] = {
			{16, 11, 10, 16, 24, 40, 51, 61},
			{12, 12, 14, 19, 26, 58, 60, 55},
			{14, 13, 16, 24, 40, 57, 69, 56},
			{14, 17, 22, 29, 51, 87, 80, 62},
			{18, 22, 37, 56, 68, 109, 103, 77},
			{24, 35, 55, 64, 81, 104, 113, 92},
			{49, 64, 78, 87, 103, 121, 120, 101},
			{72, 92, 95, 98, 112, 100, 103, 99}
	};	 

	public static Mat hideDCT(Mat cov, String text)	
	   {		   
		   int i, j, q, p, ct = 0;
		   char bit;
		   double coef;
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
						   // round the channel from q and p coordinates
						   coef = Math.round(sub.get(q, p)[0]); 
						   
						   // get coefs != 1 and 0
						   if(coef != 0 && coef != 1 && p + q != 0 && ct/8 != msg.length ) 
						   {		
							   // get 1 bit from current letter	
							   bit = (char) ((msg[ct/8] >> ct % 8) & 1); 
							   System.out.println("BIT:"+(byte)bit);
							   // to write 0 -> coef must be negative, 1 -> positive
							   if(bit == 0){
								   if(coef > 0)
									   coef = -coef;
							   }
							   else {
								   if(coef < 0)
									   coef = -coef;
							   }
							   sub.put(q, p, coef);
							   System.out.println(i+" "+j+" "+q+" "+p);
							   // modify only first 5 coefs from each block
							   if(++ct % 5 == 0){		
								   p = 8; q = 8;
							   }
						   }
					   }		    
				   Core.multiply(sub, quant, sub);	// restore from quantised DCT values to DCT
				   Core.idct(sub, sub);		// convert back to spatial domain	    
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
		   
		   for (i = 0;i<8;i++)	// initialise quantizier
			   quant.put(i,0, standardQuant[i]);
		   
		   List<Mat> spl = new ArrayList<Mat>();	   
		   cov.convertTo(cov, CvType.CV_32FC3);
		   Core.split(cov, spl);
	   
		   for(i = 0;i<10;i+=8)
			   for(j = 0;j<cov.cols()-10;j+=8)
			   {			   			   
				   sub = spl.get(0).submat(i,i+8,j,j+8);		   				  			   
				   Core.dct(sub, sub);				       
				   Core.divide(sub, quant, sub);
		   
				   for(q = 1;q<8;q++)
					   for(p = 1;p<8;p++)
					   {	
						   coef = Math.round(sub.get(q, p)[0]);
						   if(coef != 0 && coef != 1 && p + q != 0)
						   { 	
							    if(coef > 0){
							    	msg.set(ct);	// set bits to obtain a byte array
							    	System.out.println("SET"+(7*((ct / 8)+1)-ct % 8));
							    }						// with encoded watermark
								if(++ct % 5 == 0){		// max 5 bits per block
									 p = 8; q = 8;
								}
							}						   
					    }							   
			   }
		   byte[] bla = msg.toByteArray();
		   System.out.println(msg.length());
		   for(byte da: bla)
			   System.out.println(OpenCV.toBin(da,8));
			  return new String(msg.toByteArray());			  
	   }
}
