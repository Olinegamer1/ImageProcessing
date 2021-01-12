package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Binarization extends Filter {

    public static synchronized void applyFilter(Slider binarySlider, ImageView imageField, ProgressIndicator indicator){
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
                        pixelWriter.setColor(i, j, binThreshold(color, binarySlider));
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

    private static Color binThreshold (Color color, Slider binarySlider){
        int value = (int) binarySlider.getValue();
        int[] rgb = getRgbFormatColor(color);
        for (int channel = 0; channel < rgb.length; channel++){
            rgb[channel] = rgb[channel] >= value ? 255 : 0;
        }

        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }
}
