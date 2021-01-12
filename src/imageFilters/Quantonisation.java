package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Quantonisation extends Filter {
    public static synchronized void applyFilter (Slider quantSlider, ImageView imageField, ProgressIndicator indicator){
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        int value = (int) Math.pow(2, quantSlider.getValue());
        int part = 255 / value;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int j = 0; j < photo.getHeight(); j++) {
                    for (int i = 0; i < photo.getWidth(); i++) {
                        Color color = pixelReader.getColor(i, j);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, calculateQuant(color, part));
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

    private static Color calculateQuant (Color color, int part){
        int counter = 0;
        int gray = lum(color);

        while (counter <= 255){
            if (gray <= counter){
                return Color.rgb(counter, counter, counter);
            } else {
                counter += part;
            }
        }
        return Color.rgb(255, 255, 255);
    }
}
