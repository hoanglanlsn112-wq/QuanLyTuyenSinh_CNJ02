import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
public class ConvertWebp {
    public static void main(String[] args) throws Exception {
        File in = new File("C:/Users/Admin/.gemini/antigravity/brain/0c1bdc2a-9310-477b-ae65-2f9d8dfca964/.user_uploaded/media_1787728834279.webp");
        BufferedImage img = ImageIO.read(in);
        if (img != null) {
            ImageIO.write(img, "png", new File("resources/logo-eaut.png"));
            System.out.println("Converted successfully.");
        } else {
            System.out.println("Failed to read webp.");
        }
    }
}
