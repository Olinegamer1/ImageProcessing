package imageTransformation;

import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.paint.Color;


import static org.apache.commons.math3.util.FastMath.toRadians;
import static org.apache.commons.math3.util.FastMath.cos;
import static org.apache.commons.math3.util.FastMath.sin;
import static org.apache.commons.math3.util.FastMath.max;
import static org.apache.commons.math3.util.FastMath.min;

//Позже будет приведено в порядок, пока что так
public class Rotate extends Transformation {
    protected static PixelReader pixelReader;
    protected static PixelWriter pixelWriter;

    public static void applyTransformation(int constantX, int constantY, ImageView imageField, TextField textField){
        if(imageField.getImage() == null) {
            return;
        }

        Image photoTransformable = imageField.getImage();
        pixelReader = photoTransformable.getPixelReader();

        double value = Double.parseDouble(textField.getText());
        double radians = toRadians(-value);
        int size = photoTransformable.getWidth() > photoTransformable.getHeight() ? (int) (2 * photoTransformable.getWidth()) : (int) (2 * photoTransformable.getHeight());

        WritableImage writableImage = new WritableImage(size * 2, size * 2);
        pixelWriter = writableImage.getPixelWriter();

        for (int j = 1; j < photoTransformable.getHeight(); j++) {
            for (int i = 1; i < photoTransformable.getWidth(); i++) {
                double x = constantX + (i - constantX) * cos(radians) - (j - constantY) * sin(radians);
                double y = constantY + (i - constantX) * sin(radians) + (j - constantY) * cos(radians);
                pixelWriter.setColor((int) (x + size), (int) (y + size), pixelReader.getColor(i, j));
            }
        }

        double x1_ = constantX + (-constantX) * cos(radians) - (-constantY) * sin(radians);
        double y1_ = constantY + (-constantX) * sin(radians) + (-constantY) * cos(radians);
        double x2_ = constantX + (-constantX) * cos(radians) - (photoTransformable.getHeight() - constantY) * sin(radians);
        double y2_ = constantY + (-constantX) * sin(radians) + (photoTransformable.getHeight() - constantY) * cos(radians);
        double x3_ = constantX + (photoTransformable.getHeight() - constantX) * cos(radians) - (photoTransformable.getHeight() - constantY) * sin(radians);
        double y3_ = constantY + (photoTransformable.getHeight() - constantX) * sin(radians) + (photoTransformable.getHeight() - constantY) * cos(radians);
        double x4_ = constantX + (photoTransformable.getHeight() - constantX) * cos(radians) - (-constantY) * sin(radians);
        double y4_ = constantY + (photoTransformable.getHeight() - constantX) * sin(radians) + (-constantY) * cos(radians);
        int cropx1 = (int) (min(min(x1_, x2_),min(x3_, x4_)) + size);
        int cropy1 = (int) (min(min(y1_, y2_), min(y3_, y4_)) + size);
        int cropx2 = (int) (max(max(x1_, x2_), max(x3_, x4_)) + size);
        int cropy2 = (int) (max(max(y1_, y2_), max(y3_, y4_)) + size);

        WritableImage writableImage1 = new WritableImage(cropx2 - cropx1, cropy2 - cropy1);
        PixelWriter pixelWriter = writableImage1.getPixelWriter();
        pixelReader  = writableImage.getPixelReader();

        for (int j = cropy1 + 1; j < cropy2 - 1; j++){
            for (int i = cropx1 + 1; i < cropx2 - 1; i++){
                pixelWriter.setColor(i - cropx1, j - cropy1, pixelReader.getColor(i, j));
                if (pixelReader.getColor(i, j).equals(Color.web("0x00000000")) & !pixelReader.getColor(i, j - 1).equals(Color.web("0x00000000")) & !pixelReader.getColor(i, j + 1).equals(Color.web("0x00000000"))){
                    pixelWriter.setColor(i - cropx1, j - cropy1, pixelReader.getColor(i, j - 1));
                }
            }
        }

        imageField.setImage(writableImage1);
        imageField.setImage(writableImage1);
        writableImage.cancel();
    }
}
