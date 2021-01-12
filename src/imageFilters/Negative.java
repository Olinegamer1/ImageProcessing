package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Negative extends Filter {
    public static synchronized void applyFilter (Slider negativeThresholdSlider, ImageView imageField, ProgressIndicator indicator){
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
                        pixelWriter.setColor(i, j, negativeThreshold(color, negativeThresholdSlider));
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

    private static Color negativeThreshold(Color color, Slider negativeThresholdSlider ){
        int[] rgb = getRgbFormatColor(color);
        for (int channel = 0; channel < rgb.length; channel++){
            rgb[channel] = Math.max(255 - rgb[channel], (int) negativeThresholdSlider.getValue());
        }

        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }
}
