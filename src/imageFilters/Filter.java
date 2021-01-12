package imageFilters;

import image.Photo;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public abstract class Filter{
    protected static Photo photo = Photo.getInstance();
    protected static PixelReader pixelReader;
    protected static WritableImage wImage;
    protected static PixelWriter pixelWriter;
    protected static double currentProgress = 0;

    protected static int lum(Color color){
        return (int) (0.3 * (color.getRed() * 255) + 0.59 * (color.getGreen() * 255) + 0.11 * (color.getBlue() * 255));
    }

    protected static int[] getRgbFormatColor(Color color){
        int[] rgb = new int[3];
        rgb[0] = (int) ( 255 * color.getRed());
        rgb[1] = (int) (255 * color.getGreen());
        rgb[2] = (int) (255 * color.getBlue());
        return rgb;
    }

    protected static void update(){
        currentProgress = 0;
        pixelReader = photo.getPixelReader();
        wImage = photo.getWritableImage();
        pixelWriter = wImage.getPixelWriter();
    }

    protected static Color checkThreshold(int... colors){
        int red = checkOverFlow(colors[0]);
        int green = checkOverFlow(colors[1]);
        int blue = checkOverFlow(colors[2]);

        return Color.rgb(red, green, blue);
    }

    protected static int[] multiplyKernel(double[][] kernel, int x, int y, int i, int j){
        int red = (int) (pixelReader.getColor(i + y, j + x).getRed() * kernel[x][y] * 255);
        int green = (int) (pixelReader.getColor(i + y, j + x).getGreen() * kernel[x][y] * 255);
        int blue = (int) (pixelReader.getColor(i + y, j + x).getBlue() * kernel[x][y] * 255);
        return new int[] {red, green, blue};
    }

    protected static void sumColor(int[] colors, int[] kernelResult){
        for(int i = 0; i < colors.length; i++){
            colors[i] += kernelResult[i];
        }
    }

    protected static int checkOverFlow(int color){
        return Math.min(255, Math.max(0, color));
    }

    protected static double totalProgressProcessing() {
        return photo.getWidth() * photo.getHeight();
    }

    protected static double getCurrentProgress() {
        return currentProgress += 1;
    }

    protected static void isVisibleIndicator(ProgressIndicator indicator){
        indicator.setVisible(!indicator.isVisible());
    }
    protected static void unbindIndicator(ProgressIndicator indicator) {
        indicator.progressProperty().unbind();
        indicator.setProgress(0);
        isVisibleIndicator(indicator);
    }
}
