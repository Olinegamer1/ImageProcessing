package imageTransformation;

import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;


public class NearestNeighborTransformation extends Transformation{

    public static void applyTransformation(ImageView imageField, TextField scaleValue){
        update();

        String[] scaleParams = scaleValue.getText().split(" ");
        double scaleX = Double.parseDouble(scaleParams[0]);
        double scaleY = scaleParams.length > 1 ? Double.parseDouble(scaleParams[1]) : scaleX;


        writableImage = new WritableImage((int) (photoTransformable.getWidth() * scaleX), (int) (photoTransformable.getHeight() * scaleY));
        pixelWriter = writableImage.getPixelWriter();

        for (int j = 0; j < writableImage.getHeight(); j++) {
            for (int i = 0; i < writableImage.getWidth(); i++) {
                pixelWriter.setColor(i, j, pixelReader.getColor((int) (i / scaleX), (int) (j /scaleY)));
            }
        }

        imageField.setImage(writableImage);
        photoTransformable.setImage(writableImage);
        writableImage.cancel();
    }
}
