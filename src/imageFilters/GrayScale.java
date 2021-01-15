package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class GrayScale extends Filter {
    public static synchronized void applyFilter(ImageView imageField, ProgressIndicator indicator) {
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
                        int gray = lum(color);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, Color.rgb(gray, gray, gray));
                    }
                }
                imageField.setImage(wImage);
                photo.setImage(imageField.getImage());
                wImage.cancel();
                return null;
            }
        };
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> unbindIndicator(indicator));
        new Thread(task).start();
    }
}
