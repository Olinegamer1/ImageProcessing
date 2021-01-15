package imageTransformation;

import javafx.concurrent.Task;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class BilinearInterpolationTransformation extends Transformation{
    public static void applyTransformation(ImageView imageField, Slider slider){
        update();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                double scale = slider.getValue();
                writableImage = new WritableImage((int) (photoTransformable.getWidth() * scale - (int) scale), (int) (photoTransformable.getHeight() * scale - (int) scale));
                pixelWriter = writableImage.getPixelWriter();

                for (int j = 0; j < writableImage.getHeight(); j++){
                    for (int i = 0; i < writableImage.getWidth(); i++){
                        int u = (int) (i / scale);
                        int v = (int) (j / scale);
                        double x = i / scale;
                        double y = j / scale;
                        int[] rgb = calculateBilinearInterpolation(pixelReader, u, v, x, y);
                        pixelWriter.setColor(i, j, checkThreshold(rgb[0], rgb[1], rgb[2]));
                    }
                }
                imageField.setImage(writableImage);
                photoTransformable.setImage(writableImage);
                writableImage.cancel();
                return null;
            }
        };
        new Thread(task).start();
    }
    private static int calculateColor (int u, int v, double x, double y, double... colors){
        return (int) ((u + 1 - x) * (v + 1 - y) * (colors[0] * 255) + (x - u) * (v + 1 - y) * (colors[1] * 255)
                + (u + 1 - x) * ( y - v) * (colors[2] * 255) + (x - u) * (y - v) * (colors[3] * 255));
    }

    private static int[] calculateBilinearInterpolation(PixelReader pixelReader, int u, int v, double x, double y) {
        int[] rgb = new int[3];
        rgb[0] = calculateRedColor(pixelReader, u, v, x, y);
        rgb[1] = calculateGreenColor(pixelReader, u, v, x, y);
        rgb[2] = calculateBlueColor(pixelReader, u, v, x, y);
        return rgb;
    }

    private static int calculateRedColor (PixelReader pixelReader, int u, int v, double x, double y) {
        return calculateColor(u, v, x, y, getCurrentColors(pixelReader, "red", u, v));
    }


    private static int calculateGreenColor (PixelReader pixelReader, int u, int v, double x, double y) {
        return calculateColor(u, v, x, y, getCurrentColors(pixelReader, "green", u, v));
    }

    private static int calculateBlueColor (PixelReader pixelReader, int u, int v, double x, double y){
        return calculateColor(u, v, x, y, getCurrentColors(pixelReader, "blue", u, v));
    }

    private static double[] getCurrentColors(PixelReader pixelReader, String string, int x, int y) {
        Color color = pixelReader.getColor(x, y);
        Color color1 = pixelReader.getColor(x + 1, y);
        Color color2 = pixelReader.getColor(x, y + 1);
        Color color3 = pixelReader.getColor(x + 1, y + 1);
        switch (string) {
            case "red" -> {
                return new double[]{color.getRed(), color1.getRed(), color2.getRed(), color3.getRed()};
            }
            case "blue" -> {
                return new double[]{color.getBlue(), color1.getBlue(), color2.getBlue(), color3.getBlue()};
            }
            case "green" -> {
                return new double[]{color.getGreen(), color1.getGreen(), color2.getGreen(), color3.getGreen()};
            }
        }
        return new double[3];
    }


}
