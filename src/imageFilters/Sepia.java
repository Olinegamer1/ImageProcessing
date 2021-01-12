package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Sepia extends Filter {
    public static synchronized void applyFilter (ImageView imageField, ProgressIndicator indicator) {
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
                        pixelWriter.setColor(i, j, calculateSepia(color)) ;
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

    private static Color calculateSepia (Color color){
        int[] rgb = getRgbFormatColor(color);

        int red = Math.min(255, (int) (rgb[0] * 0.393 + rgb[1] * 0.769 + rgb[2] * 0.189));
        int green = Math.min(255, (int) (rgb[0] * 0.349 + rgb[1] * 0.686 + rgb[2] * 0.168));
        int blue = Math.min(255, (int) (rgb[0] * 0.272 + rgb[1] * 0.534 + rgb[2] * 0.131));

        return Color.rgb(red, green, blue);
    }
}
