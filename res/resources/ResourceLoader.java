package resources;
import java.awt.Image;
import java.awt.Toolkit;

public class ResourceLoader {
 
    static ResourceLoader resLoad = new ResourceLoader();
     
    public static Image loadImage(String imageName)
    {	
        return Toolkit.getDefaultToolkit().getImage(resLoad.getClass().getResource(imageName));
    }
}