package stegSource;

import java.io.IOException;
import java.util.Scanner;

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
		   System.out.println(i+" IN:"+info[i]+" "+(char)info[i]);
		   while(val != -1 && val != 0)
		   {
			   info[i] ^= val;
			   val >>= 8;
		   }		   
		   System.out.println(i+" OUT:"+info[i]+" "+(char)info[i]);
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
   
   public static short[] passCheckIMG(Mat cov, StringBuilder key)
   {
	   byte i = 0,j = 0,k;
	   short[] rez = new short[3];
	   byte[] values = new byte[11];
	   String init = "\\str";
	   System.out.println("VERIFICARE PASSWORD");
	   for(i = j = 0; j != 10; )		
	   {									
		   cov.get(0, i++, rez);
		   for(k = 0;k<3 && j != 10;k++)		// extract resolution and initialiser from image but 2 
		   {				   
			   values[j++] = (byte)rez[k];
			   if(j >= 7)							// middle positions are unoccupied, so we must discard them
				   values[j - 3] = values[j-1];			   
		   }
	   }	   	   	   
	   criptDecriptInfo(values, key, 8);		// decrypt res + init

	   for(i = 4;i<8;i++)						// verify if initialiser is correct
		   if(values[i] != init.charAt(i-4))
			   return new short[] {0};
	   
	   rez = new short[3];
	   for(j = 0; j < 4;j+=2)				// if it is, get hidden image resolution
	   {		   
		   rez[j/2] |= (values[j+1] & 0xFF);
		   rez[j/2] <<= 8;
		   rez[j/2] |= (values[j] & 0xFF);
		  // System.out.println(toBin(rez[j],16)+" "+toBin(values[j+1],8));
	   }
	   System.out.println("VERIFICARE PASSWORD FINAL");
	   return rez;
   }
   
   public static Mat ascImgLossless(Mat covCopy, Mat msgCopy, String key) // hides msgCopy (8 bits) inside covCopy(converted to 16 bits)
   {																	// using the least significant 8 bits of each channel of covCopy
	   String init = "\\str";				// ------------- image initialiser used to validate password, initLen MUST BE 4 -------------
	   Mat cov = covCopy.clone(),msg = msgCopy.clone();	
	   StringBuilder keyBuild = new StringBuilder(key);	   
	   byte val, k, initLen = (byte)init.length();
	   short i,j;
	   byte[] values = new byte[8];	   
	   short[] pixel = new short[3];
	   
	   System.out.println("ASCUNDERE IMAGINE START ");
	   if(cov.depth() == 0)
		   cov.convertTo(cov, CvType.CV_16UC3, 255);	   	// convert cover image to 16 bits depth if it isn't already
	   if(msg.depth() != 0)
		   msg.convertTo(msg, CvType.CV_8UC3, 1 / 255.0); 
	   
	   pixel[0] = (short)msg.rows();		// using short var to store and process 
	   pixel[1] = (short)msg.cols();		// the width and height of message image

	   values = resToByte(pixel);
	   System.out.println("ASC REZ START "+keyBuild);
	   for(i = j = 0 ; j < 4 ; )		
	   {	
		   criptDecriptInfo(values, keyBuild, initLen);	// encrypt first resolution and then image initialiser    
		   while(i != initLen)
		   {
			   cov.get(0, j, pixel);
			   for(k = 0;k < 3 && i != initLen;k++)		// write them on the least significant 8 bits of each channel
			   {	
				   //  System.out.println("BINAR1: "+toBin(pixel[k],16)+" j="+j+" k="+k+" i="+i+" iLen="+initLen);
				   pixel[k] &= 0xFF00;
				   pixel[k] |= (values[i++] & 0xFF);			   
			   }
			   cov.put(0, j++, pixel);  
		   }
		   i = 0;
		   values = init.getBytes();
	  }  
	   System.out.println(keyBuild);
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
				//	 System.out.println(pixel[k]+" "+toBin(pixel[k],16));
					 pixel[k] |= (values[k] & 0xFF);
					//System.out.println(pixel[k]+" "+toBin(pixel[k],16)+" "+values[k]);
					 //new Scanner(System.in).nextLine();
				 }	 
				 cov.put(i, j, pixel);
		  }
	  System.out.println("ASCUNDERE IMAGINE END ");
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
	   System.out.println("TIP FINAL::"+fin.type());
	   
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
   
   public static byte writeHechtInfo(byte[] out, byte inp, byte index) 
   {	  
	    // prepares resolution and image identifier: only the least  significant bit from [out] elements is changed.
	   	// writing [inp] byte from right to left on [out]
	    byte end = (byte)(index <= 5 ? 3 : 2);
	    
	   	for(byte k = 0;k < end;k++)
	   		out[k] = (byte) ((out[k] & maskHechtHide[2] ) | (( inp & (1 << index)) >> index++ ));	 	   		
	   	
	   	return index;
   }
   
   public static void extractHechtInfo(byte[] inp, byte[] retn)
   {
	   	// retn[0] -> index 
	   	// retn[1] -> partially extracted byte
	    byte end = (byte)(retn[0] <= 5 ? 3 : 2);
	    
	   	for(byte k = 0;k < end;k++){
	   		retn[1] |= ((inp[k] & 1) << retn[0]++);  
	  // 		System.out.println(inp[k]+" inp: "+toBin(inp[k],8)+" extr: "+toBin(retn[1],8)+" index:"+retn[0]);
	   	}
	//  	new Scanner(System.in).nextLine();
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
		 //  System.out.println("MSGVAL:"+msgVal[k]+" "+toBin(msgVal[k],8));
		   for(p = 0;p<3;p++)
		   {
			   cov.get(0, i, covVal);
			//   System.out.println("INITIAL:"+toBin(covVal[0],8)+" "+toBin(covVal[1],8)+" "+toBin(covVal[2],8)+
			//   				" "+covVal[0]+" "+covVal[1]+" "+covVal[2]);
			   
			   j = writeHechtInfo(covVal, msgVal[k], (byte)j);
			   
			 //  System.out.println("FINAAAL:"+toBin(covVal[0],8)+" "+toBin(covVal[1],8)+" "+toBin(covVal[2],8)+
			//		   " "+covVal[0]+" "+covVal[1]+" "+covVal[2]);

			   cov.put(0, i++, covVal);
		//	   new Scanner(System.in).nextLine();
		   }
		   j = 0;
	   }
	   y = 1; x = 0;
	   msgVal = new byte[3];
	   for(i = 0;i < msg.rows(); i++)
		   for(j = 0; j < msg.cols(); j++)
		   {
			   msg.get(i, j, msgVal);
			  // System.out.println("MSGVAL:"+msgVal[0]+" "+msgVal[1]+" "+msgVal[2]+" BINARY: "+toBin(msgVal[0],8)+" "+ toBin(msgVal[1],8)+" "+toBin(msgVal[2],8));
			   for(k = 0;k < 3;k++)
		   	   {
				   cov.get(y, x, covVal);
				   writeHechtMat(covVal, msgVal, k);
				   cov.put(y, x, covVal);
				   
				   if(++x == cov.cols())
				   {
					   y++;x = 0;
				   }
			//	   new Scanner(System.in).nextLine();		  
			   }
		   }   
	   return cov;
   }

   public static short[] passCheckHecht(Mat cov, StringBuilder keyBuild)
   {
	   String init = "\\hec";
	   short[] rez = new short[2];
	   short i,j;
	   byte k,p;
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
		   System.out.println(values[k]);
	   }
	   
	   return rez;
   }
   public static Mat extImgHecht(Mat covCopy, String key)
   {
	   Mat cov = covCopy.clone();
	   short i,j,x,y;
	   byte k,p;
	   short[] rez = new short[2]; 
	   byte[] msgVal = new byte[8], covVal = new byte[3];
	   StringBuilder keyBuild = new StringBuilder(key);
	  
	   
	   
   }
   public static void main(String[] args){
	   Mat msg = Highgui.imread("tiger.bmp"), cov = Highgui.imread("western.png"), ster = Highgui.imread("bridge.png"), m;
	   String key1 = "hmuieponta213", msg1 = "12345", init = "\\st";
	   StringBuilder val = new StringBuilder("hest"); 
	   byte bt = 1, x,y;
	   int i,j;
	   short[] rez = new short[3];
	   byte[] values = new byte[3];
	 //  cov.convertTo(cov, CvType.CV_16UC3);
	   Highgui.imwrite("steg1a.png", ascImgHecht(cov,msg,key1));
	   m = Highgui.imread("steg1a.png");
	   passCheckHecht(m,val);
	  // Highgui.imwrite("steg1.png", ascImgLossless(cov,msg,key1));
	   
/*	   for(i = 0;i < msg.rows(); i++)
		   for(j = 0; j < msg.cols(); j++){
			   msg.get(i, j, values);

			   msg.put(i, j, values);
		   }
	   Highgui.imwrite("haida.png", msg);
	   
	   /*
	   m = ascImgLossless(cov, msg, key1);
	   Highgui.imwrite("plsworkHidden.png", m);
	   m = Highgui.imread("plsworkHidden.png",-1);
	   m = extImgLossless(m, key1);
	   Highgui.imwrite("plswork.png", m);

	   
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
