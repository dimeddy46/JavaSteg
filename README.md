<h2>JavaSteg</h2>
Steganography tool written in Java 8 with OpenCV library whose main purpose is hiding/extracting information in/from images.
<br>Robust invisible watermark module also provided. 
<br>Uses symmetric key encryption.<br><br>

<b>2.1.2</b> can hide data inside .PNG and .BMP and performs the following tasks:
<br>-<b>Hecht</b> for image messages (hide the most significant 6 bits of each byte).  
<br>-<b>LSB(text)</b> reduces the cover's quality dependant on text quantity.
<br>-<b>Lossless</b> hiding to embed <b>ANY</b> type of file inside an image (cover will be converted to 16 bits -> no image noise).
<br>-<b>Watermark</b> images using a author defined string and compute the probability of any image of being watermarked by this app.

<h3>Build using Maven:</h3>
<code>
mvn clean install assembly:single
</code>
<h3>Run on Linux:</h3>
<code>
java -jar JavaSteg-2.1.2.jar
</code>

