<h2>JavaSteg</h2>
Steganography tool written in Java 8 with OpenCV library whose main purpose is hiding/extracting information
in/from images. <br><b>Robust invisible watermark</b> module also provided. 
<br>Uses symmetric key encryption.<br><br>

Version <b>2.1.2</b> can hide data inside .PNG and .BMP and performs the following tasks:
<br>- <b>Hecht</b> for image messages (hide the most significant 6 bits of each byte).
<br>- <b>LSB(text)</b> for text messages, reduces the cover's quality dependant on text quantity.
<br>- <b>Lossless</b> for <b>ANY</b> file format, as a message (cover will be converted to 16 bits so no image noise will appear).
<br>- <b>Watermark</b> images using a author defined string.
<br>- Compute the probability of an image to be watermarked by this application.

<h3>Build using Maven:</h3>
<code>
mvn clean install assembly:single
</code>
<h3>Run on Linux:</h3>
<code>
java -jar JavaSteg-2.1.2.jar
</code>
