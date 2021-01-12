package imageTransformation;

import image.PhotoTransformable;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

abstract class Transformation {
    protected static PhotoTransformable photoTransformable = PhotoTransformable.getInstance();

    protected static PixelReader pixelReader;
    protected static WritableImage writableImage;
    protected static PixelWriter pixelWriter;

    protected static void update(){
        pixelReader = photoTransformable.getPixelReader();
    }

    protected static Color checkThreshold(int... colors){
        int red = checkOverFlow(colors[0]);
        int green = checkOverFlow(colors[1]);
        int blue = checkOverFlow(colors[2]);

        return Color.rgb(red, green, blue);
    }

    private static int checkOverFlow(int color){
        return Math.min(255, Math.max(0, color));
    }
}
