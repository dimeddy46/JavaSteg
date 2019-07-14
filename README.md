<h2>JavaSteg</h2>
Steganography tool written in Java with OpenCV library.
A simple tool for hiding/extracting information in/from images.<br>Uses symmetric key encryption.<br><br>

<b>v18 can perform:</b><br>"Hecht" hiding for images,<br>"LSB(text)" dependant on text quantity,<br>"Lossless" hiding to embed any type of file inside an image.
<br><br><b>Hiding produces:</b><br> .bmp .png

<h3>To build:</h3>
```
mvn clean install assembly:single
```

