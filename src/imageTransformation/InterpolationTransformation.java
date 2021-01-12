package imageTransformation;

import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class InterpolationTransformation extends Transformation{
//    public static void applyTransformation(ImageView imageField, Slider slider){
//        update();
//
//        double scale = slider.getValue();
//        writableImage = new WritableImage((int) (photoTransformable.getWidth() * scale), (int) (photoTransformable.getHeight() * scale));
//        pixelWriter = writableImage.getPixelWriter();
//
//        for(int j = 0; j < photoTransformable.getHeight() - 1; j++){
//            for (int i = 0; i < photoTransformable.getWidth() - 1 ; i++){
//                pixelWriter.setColor((int) (i * scale), (int) (j * scale), pixelReader.getColor(i, j));
//            }
//        }
//
//        PixelReader reader = writableImage.getPixelReader();
//
//        for(int j = 0; j < photoTransformable.getHeight() - 1; j++){
//            for (int i = 0; i < photoTransformable.getWidth() - 1 ; i++){
//
//            }
//        }
//        imageField.setImage(writableImage);
//        photoTransformable.setImage(writableImage);
//        writableImage.cancel();
//    }
//
//    private static void g(PixelReader reader, int x, int y, double scale){
//        int intScale = (int) scale;
//        int red, green, blue;
//
//         red = (int) (reader.getColor(intScale * x, intScale * y).getRed() + reader.getColor(intScale * x +)) / scale;
//
//        reader.getColor(intScale * x + 1,)
//    }
}
