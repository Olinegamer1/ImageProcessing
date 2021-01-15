package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Solar extends Filter {
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
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, changeSolar(color));
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

    private static Color changeSolar(Color color){
        double a = - 255.0d / (128.0d * 127.0d);
        double b = -255.0d * a;

        int[] rgb = getRgbFormatColor(color);

        int red = (int) ((rgb[0] * a + b) * rgb[0]);
        int green = (int) ((rgb[1] * a + b) * rgb[1]);
        int blue = (int) ((rgb[2] * a + b) * rgb[2]);

        return Color.rgb(red, green, blue);
    }
}
