<h2>JavaSteg</h2>
Steganography tool written in Java 8 with OpenCV library whose main purpose is
<br>hiding/extracting information in/from images. <b>Robust invisible watermark</b> module also provided. 
<br>Uses symmetric key encryption.<br><br>

Version <b>2.1.2</b> can hide data inside .PNG and .BMP and performs the following tasks:
<br>- <b>Hecht</b> for image messages (hide the most significant 6 bits of each byte).
<br>- <b>LSB(text)</b> for text messages, reduces the cover's quality dependant on text quantity.
<br>- <b>Lossless</b> for <b>ANY</b> file format, as a message (cover will be converted to 16 bits -> no image noise).
<br>- <b>Watermark</b> images using a author defined string and compute the probability of any cover being watermarked by this app.

<h3>Build using Maven:</h3>
<code>
mvn clean install assembly:single
</code>
<h3>Run on Linux:</h3>
<code>
java -jar JavaSteg-2.1.2.jar
</code>
