package stegSource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;


public class OpenCV {
	
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static final byte[] maskHechtExtract = { 28, 96, -128};	// B(3 bit) G(2 bits) R(1 bit)
	private static final byte[] maskHechtHide =  { -8, -4, -2}; // B G R
	private static Random rand = new Random();
	private static String fileExtension;
	
	public static void setExt(String fext){
		fileExtension = fext;
	}
	
	public static String getExt(){
		return fileExtension;
	}

// -----------------------------------------------------------------------------------
// ------------------------------------- UTILS ---------------------------------------
// -----------------------------------------------------------------------------------

   public static String toBin(int dec, int bits)
   {
	   if(dec < 0)
		   dec &= 0xFF;
       String s = "";
       while (dec > 0)
       {
           s =  ( (dec % 2 ) == 0 ? "0" : "1") +s;
           dec = dec / 2;
       }
       while(s.length() < bits)
    	   s = "0" + s;   
       return s;
   }
   
   public static int toDec(String bin)
   {
	   return Integer.parseInt(bin,2);
   }
   
   public static void writeFile(String fileName, byte[] contents)
   {		
	   File file = new File(fileName);
	   try{
		   Files.write(file.toPath(),contents);	
	   }
	   catch(IOException ex){ System.out.println("write file error"); return;}
   }
   
   public static byte[] readFile(String fileName)
   {		 
	   File file = new File(fileName);
	   byte[] fileContents = null;
	   
	   try{
		  fileContents = Files.readAllBytes(file.toPath());
	   }
	   catch(IOException ex){ 
		   System.out.println("read file error");
		   return null;
	   }
	   return fileContents;
   } 
    
// -----------------------------------------------------------------------------------	   
// ----------------------------- ENCRYPTION ALGORITHM --------------------------------
// -----------------------------------------------------------------------------------
	   
   public static StringBuilder chain(StringBuilder key, int len)
   {
	   for(int i = 1;i < len;i++)
	   {
		   key.setCharAt(i, (char)(key.charAt(i)+ 1));	// key[i]++;
		   if(key.charAt(i) != 126)
			   break;
		   else 
			   key.setCharAt(i, '!');
	   }
	   return key;
   }
   
   public static void criptDecriptMat(Mat src, StringBuilder key)
   {
 	  byte v1, k = (byte)(key.length() -1);
 	  byte[] pixel = new byte[3];
 	  int val, par = 1;
 	   
 	  for(short i = 0; i< src.rows();i++)
 	  {
 		  for(short j = 0; j< src.cols();j++)
 		  {			 
 			  par *= -3;
 			  src.get(i,j,pixel);			 
 			  for(v1 = 0;v1 < 3;v1++)
 			  {	 
 				  val = key.toString().hashCode() * par;
 				  
 				  while(val != 0 && val != -1)
 				  {
 					  pixel[v1] ^= val;
 					  val >>= 8;
 				  }
 				  key.setCharAt(k, (char)(key.charAt(k) + 1));
 				  if(key.charAt(k) == 127)
 				  {
 					  key.setCharAt(k, '!');
 					  chain(key,k);
 				  }
 			  }
 			  src.put(i, j, pixel);
 		  }
 	  }
 	//  return src;
   }
   
   public static void criptDecriptInfo(byte[] info, StringBuilder key, int len)
   {
	   byte k = (byte)(key.length() - 1);
	   int val;
	   
	   for(int i = 0;i < len;i++)
	   {
		   val = key.toString().hashCode();
		//   if(i < 20)System.out.print(i+" IN:"+info[i]+" "+(char)info[i]+" ---- ");
		   while(val != -1 && val != 0)
		   {
			   info[i] ^= (byte)val;
			   val >>= 8;
		   }		   
		  // if(i < 20) System.out.print(i+" OUT:"+info[i]+" "+(char)info[i]+" KEY: "+key+" \n");	
		  
		   key.setCharAt(0, (char)(key.charAt(0) + 1)); // key[k]++;
		   if(key.charAt(0) == 126)
		   {
			   key.setCharAt(0,'!');
			   chain(key, k);
		   }
	   } 
   }

//----------------------------------------------------------------------------------- 
// ---------------------------------- TEXT STEG -------------------------------------
//-----------------------------------------------------------------------------------
   
   public static byte passCheckText(Mat img, String key)
  {
	   char lit;
	   String msg = "", init = "\\st";
	   byte bt,k, j = 0, lenInit = (byte)init.length();
	   byte[] pixel = new byte[3], 
			  verify = new byte[3];   
	   StringBuilder buffer = new StringBuilder(""),
			         keyBuild = new StringBuilder(key);	   
	   
	   for(bt = 1;bt < 5;bt++)
	   {	
		   while(msg.length() != 3)
		   {
			   img.get(0, j++, pixel);
			   for(k = 0;k < 3;k++)
			   {
				   buffer.append(toBin(pixel[k], 8).substring(8 - bt, 8));
				   if(buffer.length() >= 8)
				   {   
					   lit = (char)toDec(buffer.substring(0, 8));
					   msg += lit;
					   buffer.delete(0, 8); 
				   }
			   }
		   }		   
		   for(byte i = 0;i < lenInit;i++)
			   verify[i] = (byte)msg.charAt(i);
		   
		 criptDecriptInfo(verify, keyBuild, lenInit);
		 if(new String(verify).equals(init))
			 return bt;
		 
		 msg = ""; 
		 buffer.delete(0,buffer.length()); 
		 keyBuild.delete(0, keyBuild.length()).append(key); 
		 j = 0;
	   }
	   return 0;
  }
  
   public static Mat hideImgText(Mat cov, String msg, String key)
   {
	   String repBin = "";
	   StringBuilder valCanal = new StringBuilder("10101010"),
	                 keyBuild = new StringBuilder(key);
	   int len, btCont = 0;
	   byte[] encrypt, pixel = new byte[3];
	   
	   // ---- init ------- EOS  
	   msg = "\\st" + msg + "\\";
	   encrypt = msg.getBytes();	  
	   len = msg.length(); 
	   if(len <= 4)
		   return Mat.zeros(1, 1, CvType.CV_8U);
	   
	   byte bt = 1;
	   while((cov.rows() * cov.cols() * 3) / (8.0 / bt) < len * (8.0 / bt))	// choosing amount of LSB (bt) to write on
		   bt++;															// depending of image size

	   if(bt >= 5)								// at more than 4 LSB changed, the cover gets blurry
		   return Mat.zeros(1, 1, CvType.CV_8U);
	  
	   criptDecriptInfo(encrypt, keyBuild, len);
	   
	   for(byte ind : encrypt)	   
		   repBin += toBin(ind, 8); 
	   
	   len *= 8;
	   if(bt == 3) 
		   repBin += "10";			// number of LSB to hide on == 3? add 2 characters so the last operation gets 3 
	   	   							// bits hidden instead of 1 or 2
	   byte stPos = (byte)(8 - bt);
	   for (short i = 0; i < cov.rows() && btCont < len; i++) 
	   {
			for (short j = 0; j < cov.cols() && btCont < len; j++) 
			{
				cov.get(i, j, pixel);
				for(byte k = 0; k < 3 && btCont < len; k++)
				{									
					valCanal.delete(0, 8).append(toBin(pixel[k], 8));
					valCanal.replace(stPos, 8, repBin.substring(btCont, btCont += bt));
					pixel[k] = (byte)(toDec(valCanal.toString()));
				}
				cov.put(i, j, pixel);
			}
	   }
	return cov;
   }
   
   public static String extImgText(Mat img, String key)
   {
	   byte bt = passCheckText(img,key);
	   if(bt == 0) return "\\pwdincorect";
	   
	   char countLit = 0;
	   byte k, stPos = (byte)(8 - bt), contain;
	   byte[] pixel = new byte[3],
			  decrypt = new byte[10];
	   short i,j;
	   String msg = "", hold;
	   StringBuilder buffer = new StringBuilder(""), 
			         keyBuild = new StringBuilder(key);
	   
		for (i = 0; i < img.rows(); i++)
			for (j = 0; j < img.cols(); j++)
			{
				img.get(i, j, pixel);
				for(k = 0;k < 3;k++)
				{
					buffer.append( toBin(pixel[k], 8).substring(stPos, 8) );
					if(buffer.length() >= 8)
					{
						decrypt[countLit++] = (byte)toDec(buffer.substring(0, 8));
						buffer.delete(0,8);
						if(countLit == 10)
						{
							countLit = 0;
							criptDecriptInfo(decrypt, keyBuild, 10);
							hold = new String(decrypt); 
							hold = hold.replace("\\st", "000");
							msg += hold;
							contain = (byte)hold.lastIndexOf("\\");	
							if(contain != -1)
								return msg.substring(3, msg.length() - 10 + contain);
						}
					}	
				}
			}   	   
	   return "\\err";
   }


// --------------------------------------------------------------------------------
// --------------------------- HECHT STEGANOGRAPHY --------------------------------
// --------------------------------------------------------------------------------
   public static byte[] resToByte(short[] rez)
   {
	   byte[] values = new byte[8];	 
	   
	   for(byte i = 0;i < 4;i+=2)				// convert resolution from short to byte to correspond with
	   {										// criptDecriptInfo parameter(can't use generics to do arithmetic ops)
		   values[i] = (byte)rez[i/2];		   
		   values[i+1] = (byte)(rez[i/2] >> 8);
	   }
	   return values;
   }
   
   public static short[] byteToRes(byte[] values)
   {		   
	   short[] rez = new short[3];
	   for(byte j = 0; j < 4;j+=2)				
	   {		   
		   rez[j/2] |= (values[j+1] & 0xFF);
		   rez[j/2] <<= 8;
		   rez[j/2] |= (values[j] & 0xFF);
	   } 
	   return rez;
   }  
   
   public static void writeHechtMat(byte[] out, byte[] inp, byte index) 
   {	  
	      // converts 1 byte from [inp] to 3 bytes [out] changing only the least significant 
	   	  // bits of the [out] contents with the most significant bits from [inp]
		  // ( out[2] -> 1 bit , out[1] -> 2 bits, out[3] -> 3 bits)
		  out[2] = (byte)((out[2] & maskHechtHide[2]) | ((inp[index] & 0xFF & maskHechtExtract[2]) >> 7));
		  out[1] = (byte)((out[1] & maskHechtHide[1]) | ((inp[index] & 0xFF & maskHechtExtract[1]) >> 5));
		  out[0] = (byte)((out[0] & maskHechtHide[0]) | ((inp[index] & 0xFF & maskHechtExtract[0]) >> 2));
   }
   
   public static byte extractHechtMat(byte[] inp)
   {
	   byte out = 0;
	   out |= (byte)((inp[2] & 0xFF & 1) << 7);		// reverse the writing operation to compose 1 byte 
	   out |= (byte)((inp[1] & 0xFF & 3) << 5);		// from the 3 channels of 1 pixel
	   out |= (byte)((inp[0] & 0xFF & 7) << 2);	
	   out |= rand.nextInt(4);						// writing Hecht data causes the least signif. 2 bits to be lost, 
	   return (byte)(out & 0xFF);					// so we fill with rand instead of leaving 00 as those bits.
   }
   
   public static byte writeHechtInfo(byte[] out, byte inp, byte index) 
   {	  
	    // prepares resolution and image identifier: only the least significant bit from [out] elements is changed.
	   	// writing [inp] byte from right to left on [out]
	    byte end = (byte)(index <= 5 ? 3 : 2);
	    
	   	for(byte k = 0;k < end;k++)
	   		out[k] = (byte) ((out[k] & maskHechtHide[2] ) | (( inp & (1 << index)) >> index++ ));	 	   		
	   	
	   	return index;
   }
   
   public static void extractHechtInfo(byte[] inp, byte[] retn)
   {	
	    // gets least significant bit from [inp] elements and builds a byte
	   	// retn[0] -> index 
	   	// retn[1] -> partially extracted byte
	    byte end = (byte)(retn[0] <= 5 ? 3 : 2);
	    
	   	for(byte k = 0;k < end;k++)
	   		retn[1] |= ((inp[k] & 1) << retn[0]++);  
   }
   
   public static short[] passCheckHecht(Mat cov, StringBuilder keyBuild)
   {
	   String init = "\\hec";
	   short[] rez = new short[2];
	   byte k, p, i;
	   byte[] values = new byte[8], covVal = new byte[3], retn = new byte[2];
	   
	   i = 0;
	   for(k = 0;k < 8;k++)
	   {
		   for(p = 0;p < 3;p++)
		   {
			   cov.get(0, i++, covVal);
			   extractHechtInfo(covVal, retn);
		   }
		   values[k] = retn[1];
		   retn[0] = retn[1] = 0;
	   }	   
	   criptDecriptInfo(values, keyBuild, 8);
	   
	   for(k = 4; k < 8; k++)						// verify if initialiser is correct
		   if(values[k] != init.charAt(k-4))		
			   return new short[] {0};
	   
	   rez = byteToRes(values);
	   return rez;
   }
      
   public static Mat hideImgHecht(Mat cov, Mat msg, String key)
   {
	   short i,j,x,y;
	   byte k,p;
	   short[] rez = new short[2]; 
	   byte[] msgVal = new byte[8], covVal = new byte[3];
	   StringBuilder keyBuild = new StringBuilder(key);
	   String init = "\\hec";

	   if(msg.rows() * msg.cols() * 3 > cov.rows() * cov.cols()) // message requires 3 times cover size
		   return Mat.zeros(1, 1, CvType.CV_8UC3);
	   
	   rez[0] = (short) msg.rows();
	   rez[1] = (short) msg.cols();
	   
	   msgVal = resToByte(rez);		
	   for(i = 4;i < 8;i++)			// concatenate resolution and initialiser 
		   msgVal[i] = (byte) init.charAt(i-4);
	   
	   criptDecriptInfo(msgVal, keyBuild, 8);
	   i = j = 0;				
	   for(k = 0;k < 8;k++)	// write resolution and identifier on least significant bit of cov first line
	   {
		   for(p = 0;p<3;p++)
		   {
			   cov.get(0, i, covVal);			   
			   j = writeHechtInfo(covVal, msgVal[k], (byte)j);
			   cov.put(0, i++, covVal);
		   }
		   j = 0;
	   }
	   criptDecriptMat(msg,keyBuild);
	   
	   y = 1; x = 0;
	   msgVal = new byte[3];
	   for(i = 0;i < msg.rows(); i++)			// each msg byte is written on the cover's LSB using from:
		   for(j = 0; j < msg.cols(); j++)		// B channel -> 3 bits
		   {									// G channel -> 2 bits
			   msg.get(i, j, msgVal);			// R channel -> 1 bit
			   for(k = 0;k < 3;k++)
		   	   {
				   cov.get(y, x, covVal);
				   writeHechtMat(covVal, msgVal, k);
				   cov.put(y, x++, covVal);				   
				   if(x == cov.cols()){ y++; x = 0; }	  
			   }
		   }   
	   return cov;
   }
   
   public static Mat extImgHecht(Mat covCopy, String key)
   {
	   Mat cov = covCopy.clone();
	   short i,j,x,y;
	   byte k;
	   short[] rez = new short[2]; 
	   byte[] msgVal = new byte[3], covVal = new byte[3];
	   StringBuilder keyBuild = new StringBuilder(key);
	  
	   rez = passCheckHecht(cov, keyBuild);			// get resolution if password matches decripted image identifier
	   if(rez[0] == 0)
		   return Mat.zeros(1, 1, CvType.CV_8UC3);	// incorrect password
	   
	   Mat fin = new Mat(rez[0],rez[1], CvType.CV_8UC3);
	   
	   k = 0;
	   x = y = 0;
	   for(i = 1;i <= cov.rows(); i++)
		   for(j = 0; j < cov.cols(); j++)		
		   {
			   cov.get(i, j, covVal);
			   msgVal[k++] = extractHechtMat(covVal);
			   if(k == 3)
			   {
				   k = 0;
				   fin.put(y, x++, msgVal);
				   if(x == fin.cols()){ y++; x = 0; }	
			   }			   
		   }
	   criptDecriptMat(fin,keyBuild);
	   return fin;
   }
   
// --------------------------------------------------------------------------------
// --------------------------- LOSSLESS STEGANOGRAPHY(16 BITS) --------------------
// --------------------------------------------------------------------------------   
	
        
   public static int byteToInt(byte[] values)
   {
	   int rez = 0;
	   for(int i = 3;i >= 1;i--)
		   rez = (rez | (values[i] & 0xFF)) << 8;
	   rez |= (values[0] & 0xFF); 
	   return rez;
   }
   
   public static String getLosslessExtension(Mat cov, StringBuilder keyBuild, int len)
   {
	    byte[] values = new byte[len];
	    short[] pixel = new short[3];
	    byte k, i = 0, j = 3;  //  start from pixel 3(4B + 4B + 1B written behind)
	    
	    while(i != len)
	    { 	
		    cov.get(0, j++, pixel);
	    	for(k = 0;k<3 && i != len;k++)
	    		values[i++] += (char)(pixel[k] & 0xFF);
	    }
	    criptDecriptInfo(values, keyBuild, len);
	    return new String(values);
   }

   public static int[] passCheckLossless2(Mat cov, StringBuilder keyBuild)	// combined lossless image and file check
   {
	   String init = "lsf\\";	
	   byte i, j, k;
	   short[] rez = new short[3];
	   byte[] values = new byte[9];
	   
	   for(i = j = 0; j < 3; j++)	// extract resolution(4 bytes) and initialiser (4 bytes) from image	
	   {									
		   cov.get(0, j, rez);	   
		   for(k = 0; k < 3; k++)				   
			   values[i++] = (byte)rez[k];		   
	   }	
	   criptDecriptInfo(values, keyBuild, 9);		// decrypt res + init
	   
	   for(i = 4;i < 8;i++)							// verify if one initialiser is correct						
		   if((values[i] != init.charAt(i-4)))		
			   return new int[]{0};					// incorrect password
	   
	   return new int[]{byteToInt(values), values[8]};		// return a array of [file len, extension len]
   }
  
   public static Mat hideLosslessFile2(Mat cov, byte[] file, String key, String extension)
   {   // hide a [file] inside an image by converting the image to 16 bits(byte->word)
	   // and writing each byte from [file]  on 
	   // the 8 Least Significant Bits of cover's words
	   String init = "lsf\\" + (char)extension.length() + extension; 
	   StringBuilder keyBuild = new StringBuilder(key);
	   int i, j, total;
	   byte k;
	   short[] pixel = new short[3]; 
	   byte[] values = new byte[init.length()+7];		// will contain: fileLength + init + extLength + extension 
	   													// 			   = 4B +         4B +   1B +        xB => 
	   if(file == null) 
		   return Mat.zeros(1, 1, CvType.CV_8UC3);	   
	   		   	   
	   if(file.length > cov.cols() * cov.rows() * 3 - 12)
		   return Mat.zeros(1, 1, CvType.CV_8UC3);
	   
	   if(cov.depth() == 0)
		   cov.convertTo(cov, CvType.CV_16UC3, 255);	   	// convert cover image to 16 bits depth if it isn't already
	   
	   total = file.length;
	   for(i = 0;i < 4;i++)						// convert file length to byte[]
	   {
		   values[i] = (byte)total;				// getting last 8 bits of file length value
		   total >>= 8;							// and right shifting to proceed with next 8 bits.
	   }
	   while(i != init.length()+4)				// i = 4, copying to [values] the fileLength and initialiser 
		   values[i] = (byte)init.charAt(i++-4);
		 
	   criptDecriptInfo(values, keyBuild, init.length()+4);		// encrypt length, initialiser and file
	   criptDecriptInfo(file, keyBuild, file.length);  

	   i = 0; j = 0;
	   while(i < init.length()+4)					// write fileLength, initialiser, extLength, extension
	   {
		   cov.get(0, j, pixel);
		   for(k = 0;k < 3;k++)
			   pixel[k] = (short)((pixel[k] & 0xFF00) | (values[i++] & 0xFF));
		   cov.put(0, j++, pixel);
	   }
	   total = 0; i = 0;							// continuing to write the [file] contents on image
	   while(total < file.length)				
	   {
		   cov.get(i, j, pixel);
		   for(k = 0;k < 3 && total < file.length;k++)
			   pixel[k] = (short)((pixel[k] & 0xFF00) | (file[total++] & 0xFF));
		   
		   cov.put(i, j++, pixel);
		   if(j == cov.cols()){ j = 0; i++; }
	   }	   
	   return cov;
   }
     
   public static byte[] extLosslessFile2(Mat covCopy, String key)
   {	   
	   Mat cov = covCopy.clone();
	   StringBuilder keyBuild = new StringBuilder(key);
	   short[] pixel = new short[3]; 
	   int i,j;	   
	   byte k;
	   
	   int[] total = passCheckLossless2(cov, keyBuild);	// extracted: file size [0] and extension length [1]
	   
	   if(total[0] == 0)			//incorrect password
		   return null;	

	   byte[] fileContents = new byte[total[0]]; 
	   setExt( getLosslessExtension(cov, keyBuild, total[1]));	  

	   i = 0; 
	   j = 4 + total[1] / 3;
	   if(total[1] % 3 == 0) j--;

	   total[0] = 0;
	   while(total[0] < fileContents.length)
	   {
		   cov.get(i, j, pixel);
		   for(k = 0;k < 3 && total[0] < fileContents.length ;k++)			   	
			   fileContents[total[0]++] = (byte)(pixel[k] & 0xFF); 
		   if(++j == cov.cols()){ j = 0;i++; }
	   }	   
	   criptDecriptInfo(fileContents, keyBuild, total[0]);
	   
	   return fileContents;	   
   }

   public static void main(String[] args) throws Exception
   {
	   Mat msg = Highgui.imread("Samples/aaaLOSSLESS.png"), cov = Highgui.imread("Samples/house.bmp"), 
			   ster = Highgui.imread("Samples/bridge.png"), m;
	   String key1 = "hestin", msg1 = "12345";
	   StringBuilder val = new StringBuilder("hest"); 
	   byte bt = 1, y;
	   int i = 0,j = 0,k = 0;
	   short[] rez = new short[3];
	   byte[] values = new byte[3];
	   byte[] bit = {1,2,4,8,16,32,64,-128};
	   Mat z,zz;
	   Highgui.imwrite("test"+(k++)+".bmp",msg);
	   
	/*   msg.convertTo(msg, CvType.CV_8S);
	   z = msg.rowRange(0, 8).colRange(0, 8);
	   zz = msg.rowRange(0, 8).colRange(8, 16);
	   
	   System.out.println(z);
	   Core.subtract(z, zz, z);
	   System.out.println(z.dump());
   	 //  

/*	  Mat zr = Mat.zeros(msg.rows(),msg.cols(),CvType.CV_8UC3);
	  for(byte b = 0;b<bit.length;b++)
	  {
	  	for(i = 0;i<msg.rows();i++)
		   for(j = 0;j<msg.cols();j++)
		   {
			    msg.get(i, j, values );
	   			values[2] = (byte) (-1 * (values[2] & bit[b])); 
	   			values[1] = 0; 
	   			values[0] = 0;
	   			zr.put(i, j, values);
		   }
	   Highgui.imwrite("test"+b+".png", zr);
	  }
/*	   String file = "Samples/western.png";
	   Mat rezf = hideLosslessFile2(cov, OpenCV.readFile(file), 
				 key1, Menu.getFileExtension(file));
	  byte[] fis = extLosslessFile2(rezf, key1);
	  writeFile("stere"+getExt(), fis);
	  
/*     byte[] valz = readFile("Samples/western.png");
	   System.out.println("LUNG:"+valz.length);	   	
	   Highgui.imwrite("hidden.png",  hideLosslessFile(cov, valz, key1));
	   
	   m = Highgui.imread("hidden.png",-1);
	   if(m.rows() != 1 ){
		   values = extLosslessFile(m,key1);
		   if(values.length == 1)
		   {
			   System.out.println(values.length+" pass gresit");
			   return;
		   }
		   writeFile("mere2.png",values);
		   	
	   }
/*	   byte[] valz = readFile("Samples/Galois.exe");
	   Highgui.imwrite("Samples/tes.png", hideBinaryFile(cov, valz, key1));*/
	    

/*     m = hideImgLossless(cov,msg,key1);
	   Highgui.imwrite("testInit2.png", m);
	   m = Highgui.imread("testInit2.png",-1);
	   Highgui.imwrite("testz2.png", extImgLossless(m,key1));
	   
/*
	   m = ascImgHecht(cov,msg,key1);
	   if(m.rows() == 1){System.out.println("prea mic");return;}
	   Highgui.imwrite("steg1a.png", m);
	   
	   m = Highgui.imread("steg1a.png");
	   key1 = "meas";
	   m = extImgHecht(m,key1);
	   if(m.rows() == 1){System.out.println("NU E");return;}
	   Highgui.imwrite("stegz.png", m);*/
	  
	   /*  m = LSBAscundere(m,msg,key1,bt);
	   Highgui.imwrite("test1.png", m);
	   m = Highgui.imread("test1.png"); 
	   System.out.println(LSBExtragere(m, key1, bt));
	  	*/
   }

}
