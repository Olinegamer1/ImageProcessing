package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Brightness extends Filter {
    public static synchronized void applyFilter (Slider brightnessSlider, ImageView imageField, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int j = 0; j < photo.getHeight(); j++) {
                    for (int i = 0; i < photo.getWidth(); i++) {
                        Color color = pixelReader.getColor(i, j);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, changeBrightness(color, brightnessSlider.getValue()));
                    }
                }
                imageField.setImage(wImage);
                wImage.cancel();
                return null;
            }
        };
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> unbindIndicator(indicator));
        new Thread(task).start();
    }

    private static Color changeBrightness(Color color, double brightnessValue){
        int[] rgb = getRgbFormatColor(color);

        int red = checkOverFlow((int) (rgb[0] + brightnessValue));
        int green = checkOverFlow((int) (rgb[1] + brightnessValue));
        int blue = checkOverFlow((int) (rgb[2] + brightnessValue));

        return Color.rgb(red, green, blue);
    }
}
