package stegSource;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;

public class OpenCV {
	
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static final byte[] maskHechtExtract = { 28, 96, -128};	// B(3 bit) G(2 bits) R(1 bit)
	public static final byte[] maskHechtHide =  { -8, -4, -2}; // B G R
	public static final String[] initStr = {"\\st", "\\str", "\\hec", "\\exe"};	// text, lossless, hecht, executable
	public static Random rand = new Random();
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
	  
	   public static String readFile(String fileName) throws Exception
	   {
		   File file = new File(fileName);
		   BufferedReader br = new BufferedReader(new FileReader(file));
	   
		   String st, rez = ""; 
		   while ((st = br.readLine()) != null) 
		   {
			     rez += st +'\n';
		   }
	   
		   br.close();
		   return rez;
	   } 
// -----------------------------------------------------------------------------------	   
// ----------------------------- ENCRYPTION ALGORITHM --------------------------------
// -----------------------------------------------------------------------------------
	   
   public static StringBuilder chain(StringBuilder key, int len)
   {
	   for(int i = len -1;i >= 0;i--)
	   {
		   key.setCharAt(i, (char)(key.charAt(i)+ 1));
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
		   System.out.print(i+" IN:"+info[i]+" "+(char)info[i]+" ---- ");
		   while(val != -1 && val != 0)
		   {
			   info[i] ^= val;
			   val >>= 8;
		   }		   
		   System.out.print(i+" OUT:"+info[i]+" "+(char)info[i]+" \n");
		   key.setCharAt(k, (char)(key.charAt(k) + 1));
		   if(key.charAt(k) == 127)
		   {
			   key.setCharAt(k,'!');
			   chain(key,k);
		   }
	   } 
   }

//----------------------------------------------------------------------------------- 
// ---------------------------------- TEXT STEG -------------------------------------
//-----------------------------------------------------------------------------------
  
   public static byte passCheckMSG(Mat img, String key)
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
				   System.out.println("BUFF:" +buffer+ " k"+k+" j"+j);
				   if(buffer.length() >= 8)
				   {   
					   lit = (char)toDec(buffer.substring(0, 8));
					   msg += lit;
					   buffer.delete(0, 8); 
					   System.out.println("LITERA:"+(byte)lit+" "+msg);
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
  
   public static Mat LSBAscundere(Mat imgCopy, String msg, String key, byte bt)
   {
	   Mat img = imgCopy.clone();
	   String repBin = "";
	   StringBuilder valCanal = new StringBuilder("10101010"),
	                 keyBuild = new StringBuilder(key);
	   int len, btCont = 0;
	   byte[] encrypt, pixel = new byte[3];
	   // ---- init -------- finalizer  
	   msg = "\\st" + msg + "\\";
	   encrypt = msg.getBytes();
	   len = msg.length(); 
	   
	   bt = 1;
	   while((img.rows() * img.cols() * 3) / (8.0 / bt) < len * (8.0 / bt))
		   bt++;

	   if(bt >= 5)
		   return Mat.zeros(1,1,CvType.CV_8U);
	  
	   criptDecriptInfo(encrypt, keyBuild, len);
	   System.out.println("FINAL KEY:"+keyBuild);	
	   
	   for(byte ind : encrypt){
		   repBin += toBin(ind, 8); 
		   System.out.print(toBin(ind, 8)+" ");
	   }
	   len *= 8;
	   if(bt == 3) 
		   repBin += "10";
	   	   
	   byte stPos = (byte)(8 - bt);
	   for (short i = 0; i < img.rows() && btCont < len; i++) 
	   {
			for (short j = 0; j < img.cols() && btCont < len; j++) 
			{
				img.get(i, j, pixel);
				System.out.println("GASIT:"+(pixel[0]&0xFF)+" "+(pixel[1]&0xFF)+" "+(pixel[2]&0xFF)+"\n");
				for(byte k = 0; k < 3 && btCont < len; k++)
				{									
					valCanal.delete(0, 8).append(toBin(pixel[k], 8));
					System.out.println("START:"+valCanal+" pixel:"+(pixel[k] &0xFF)+" "+repBin.substring(btCont, btCont + bt));
					valCanal.replace(stPos, 8, repBin.substring(btCont, btCont += bt));

					pixel[k] = (byte)(toDec(valCanal.toString()));
					
					System.out.println("END:"+valCanal+" pixel:"+(pixel[k] &0xFF));
					//scan.nextLine();
				}
				img.put(i, j, pixel);
				System.out.println("SCRIS:"+(pixel[0]&0xFF)+" "+(pixel[1]&0xFF)+" "+(pixel[2]&0xFF)+" "+ btCont+" "+len+"\n");	
			}
	   }
	return img;
   }
   
   public static String LSBExtragere(Mat img, String key, byte bt)
   {
	   bt = passCheckMSG(img,key);
	   if(bt == 0) return "\\pwdincorect";
	   System.out.println(bt);
	   
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
							{
								System.out.println(msg.length()+" "+contain);
								return msg.substring(3, msg.length() - 10 + contain);
							}
						}
					}	
				}
			}   	   
	   return "\\err";
   }

// -----------------------------------------------------------------------------------   
// ----------------------------- LOSSLESS STEGANOGRAPHY(16 BITS) ---------------------
// -----------------------------------------------------------------------------------
	
   public static byte[] resToByte(short[] rez)
   {
	   byte[] values = new byte[8];	 
	   
	   for(byte i = 0;i < 4;i+=2)				// convert resolution from short to byte to correspond with
	   {										// criptDecriptInfo parameter(can't use generics to do array operations)
		   values[i] = (byte)rez[i/2];		   
		   values[i+1] = (byte)(rez[i/2] >> 8);
		   //System.out.println("resToShort: "+(values[i]&0xFF)+" "+toBin(values[i]&0xFF,8));
	   }
	   return values;
   }
   
   public static short[] byteToRes(byte[] values)
   {	
	   byte j,i;
	   short[] rez = new short[2];
	   for(j = 0; j < 4;j+=2)				
	   {		   
		   rez[j/2] |= (values[j+1] & 0xFF);
		   rez[j/2] <<= 8;
		   rez[j/2] |= (values[j] & 0xFF);
		  // System.out.println(toBin(rez[j],16)+" "+toBin(values[j+1],8));
	   }
	   return rez;
   }
   
   public static short[] passCheckIMG(Mat cov, StringBuilder key)
   {
	   String init = "\\str";
	   byte i, j, k, initLen = (byte) init.length();
	   short[] rez = new short[3];
	   byte[] values = new byte[4+initLen];

	   for(i = j = 0; j < 3;j++ )		
	   {									
		   cov.get(0, j, rez);
		   for(k = 0; k < (j == 2? 2: 3); k++)		// extract resolution and initialiser from image			   
			   values[i++] = (byte)rez[k];		   
	   }	   	   	   
	   criptDecriptInfo(values, key, 4+initLen);		// decrypt res + init

	   for(i = 4;i < 4+initLen;i++)						// verify if initialiser is correct
		   if(values[i] != init.charAt(i-initLen))		
			   return new short[] {0};
	   													
	   rez = byteToRes(values);							// if it is, get hidden image resolution
	   return rez;
   }
   
   public static Mat ascImgLossless(Mat covCopy, Mat msgCopy, String key) // hides msgCopy (8 bits) inside covCopy(converted to 16 bits)
   {																	// using the least significant 8 bits of each channel of covCopy
	   String init = "\\str";				// ------------- image initialiser used to validate password -------------
	   byte k, initLen = (byte)init.length();
	   Mat cov = covCopy.clone(),msg = msgCopy.clone();	
	   StringBuilder keyBuild = new StringBuilder(key);	   
	   short i,j;
	   byte[] values = new byte[4+initLen];	  
	   short[] pixel = new short[3];
	   
	   if(cov.depth() == 0)
		   cov.convertTo(cov, CvType.CV_16UC3, 255);	   	// convert cover image to 16 bits depth if it isn't already
	   
	   if(msg.depth() != 0)
		   msg.convertTo(msg, CvType.CV_8UC3, 1 / 255.0); 
	   
	   pixel[0] = (short)msg.rows();		// using short var to store and process 
	   pixel[1] = (short)msg.cols();		// the width and height of message image

	   values = resToByte(pixel);
	   for(i = 4;i < 4+initLen; i++)		// concatenate resolution and initialiser 
		   values[i] = (byte) init.charAt(i-initLen);

	   
	   criptDecriptInfo(values, keyBuild, 4+initLen); // encrypt resolution image identifier
	   
	   for(i = j = 0; j < 3; j++)		
	   {   		   
		   cov.get(0, j, pixel);
		   for(k = 0;k < (j == 2? 2:3);k++)		// write them on the least significant 8 bits of each channel
		   {										// of the first 3 pixels
			   //  System.out.println("BINAR1: "+toBin(pixel[k],16)+" j="+j+" k="+k+" i="+i+" iLen="+initLen);
			   pixel[k] &= 0xFF00;
			   pixel[k] |= (values[i++] & 0xFF);
			//   System.out.println(i+" "+j+" "+k+" "+values[i-1]);
		   }
		   cov.put(0, j, pixel);  
	   }

	  values = new byte[3];
	  criptDecriptMat(msg, keyBuild);

	  for (i = 1; i < msg.rows() + 1; i++) 
		  for (j = 0; j < msg.cols(); j++) 
		  {
				 msg.get(i-1, j, values);
				 cov.get(i,j,pixel);
				 for(k = 0;k<3;k++)
				 {	 
					 pixel[k] &= 0xFF00;
					 pixel[k] |= (values[k] & 0xFF);
				 }	 
				 cov.put(i, j, pixel);
		  }
	  return cov;
   }
   
   public static Mat extImgLossless(Mat covCopy, String key) 
   {
	   Mat cov = covCopy.clone();
	   short i,j,k;
	   short[] rez = new short[2];
	   byte[] values = new byte[4];
	   StringBuilder keyBuild = new StringBuilder(key);
	   
	   System.out.println(keyBuild);
	   rez = passCheckIMG(cov, keyBuild);
	   System.out.println(keyBuild);
	   if(rez[0] == 0) 			
		   return Mat.zeros(0, 0, CvType.CV_8UC3);	// incorrect password
	   
	   Mat fin = new Mat(rez[0],rez[1], CvType.CV_8UC3);
	   
	   values = new byte[3];
	   rez = new short[3];
	   
	   for(i = 1;i <= fin.rows(); i++)
		   for(j = 0; j < fin.cols(); j++)
		   {
			   cov.get(i, j, rez);
			   for(k = 0;k < 3;k++)
			   {	
				   values[k] = (byte)rez[k]; 
				 //  System.out.println(pixel[k]+" "+toBin(pixel[k],16)+" "+toBin(values[k],8));
				//  new Scanner(System.in).nextLine();
			   }
			   fin.put(i-1, j, values);
			  // System.out.println(values[0]+" "+values[1]+" "+values[2]);
			 //  scan.nextLine();
		   }
	   criptDecriptMat(fin,keyBuild);
	   return fin;
   }

// --------------------------------------------------------------------------------
// --------------------------- HECHT STEGANOGRAPHY --------------------------------
// --------------------------------------------------------------------------------
   
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
	    
	   	for(byte k = 0;k < end;k++){
	   		retn[1] |= ((inp[k] & 1) << retn[0]++);  
	  // 		System.out.println(inp[k]+" inp: "+toBin(inp[k],8)+" extr: "+toBin(retn[1],8)+" index:"+retn[0]);
	   	}
	//  	new Scanner(System.in).nextLine();
   }
   
   public static short[] passCheckHecht(Mat cov, StringBuilder keyBuild)
   {
	   String init = "\\hec";
	   short[] rez = new short[2];
	   byte k, p, i, initLen = (byte)init.length();
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
	   
	   for(k = 4; k < 4+initLen; k++)						// verify if initialiser is correct
		   if(values[k] != init.charAt(k-initLen))		
			   return new short[] {0};
	   
	   rez = byteToRes(values);
	   return rez;
   }
   
   public static Mat ascImgHecht(Mat covCopy, Mat msg, String key)
   {
	   Mat cov = covCopy.clone();
	   short i,j,x,y;
	   byte k,p;
	   short[] rez = new short[2]; 
	   byte[] msgVal = new byte[8], covVal = new byte[3];
	   StringBuilder keyBuild = new StringBuilder(key);
	   String init = "\\hec";
	   
	   if(msg.depth() != 0)
		   msg.convertTo(msg, CvType.CV_8UC3, 1 / 255.0); 
	   
	   rez[0] = (short) msg.rows();
	   rez[1] = (short) msg.cols();
	   
	   msgVal = resToByte(rez);		
	   for(i = 4;i<8;i++)			// concatenate resolution and initialiser 
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
	   for(i = 0;i < msg.rows(); i++)
		   for(j = 0; j < msg.cols(); j++)
		   {
			   msg.get(i, j, msgVal);
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
		   return Mat.zeros(0, 0, CvType.CV_8UC3);	// incorrect password
	   
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
   
   public static void main(String[] args) throws Exception{
	   Mat msg = Highgui.imread("Samples/tiger.bmp"), cov = Highgui.imread("Samples/house.bmp"), 
			   ster = Highgui.imread("Samples/bridge.png"), m;
	   String key1 = "hest", msg1 = "12345", init = "\\st";
	   StringBuilder val = new StringBuilder("hest"); 
	   byte bt = 1, x,y;
	   int i,j;
	   short[] rez = new short[3];
	   byte[] values = new byte[3];
	   
	   System.out.println(readFile("Samples/tex.txt"));
	   
	  // m = ascImgHecht(cov,msg,key1);
	   //Highgui.imwrite("tep1.jpg", extImgHecht(m,key1));
//	   m = ascImgLossless(cov,msg,key1);
//	   Highgui.imwrite("testInit.png", m);
//	   m = Highgui.imread("testInit.png",-1);
//	   Highgui.imwrite("testz.png", extImgLossless(m,key1));

	/*   Highgui.imwrite("steg1a.png", ascImgHecht(cov,msg,key1));
	   m = Highgui.imread("steg1a.png");
	   passCheckHecht(m,val);
	  // Highgui.imwrite("steg1.png", ascImgLossless(cov,msg,key1));
	   
		/*   for(i = 0;i<=10;i++)
	   {
		   msg.get(i, 2, values);
		   System.out.println(values[0]+" "+values[1]+" "+values[2]);
		//   new Scanner(System.in).nextLine();
	   }*/
	   
	   /*  m = LSBAscundere(m,msg,key1,bt);
	   System.out.println(key1);
	  // System.out.println(estePassCorectMSG(m,key1));
	   System.out.println(LSBExtragere(m, key1, bt));
	  
	  
	   byte[] bytes = msg.getBytes();
	   for(byte ind : bytes)
		   System.out.print(ind+ " ");
	   criptDecriptInfo_3(msg.getBytes(), valCanal);
	   for(byte ind : bytes)
		   System.out.print(ind+ " ");
	   valCanal.delete(0,4).append("keye");
	   criptDecriptInfo_3(msg.getBytes(), valCanal);
	   for(byte ind : bytes)
		   System.out.print(ind+ " ");*/
   }

}
