package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class IncreaseDecreaseContrast extends Filter {
    public static synchronized void applyFilter(Slider contrastRight, Slider contrastLeft, ImageView imageField, boolean decrease, ProgressIndicator indicator) {
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
                        pixelWriter.setColor(i, j, calculateIncreaseOrDecreaseContrast(color, contrastRight, contrastLeft, decrease));
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

    private static Color calculateIncreaseOrDecreaseContrast (Color color, Slider contrastRight, Slider contrastLeft, boolean decrease){
        int[] rgb = getRgbFormatColor(color);
        int rightValue = (int) contrastRight.getValue();
        int leftValue = (int) contrastLeft.getValue();


        int red = transform(rgb[0], leftValue, rightValue, decrease);
        int green = transform(rgb[1], leftValue, rightValue, decrease);
        int blue = transform(rgb[2], leftValue, rightValue, decrease);

        return Color.rgb(red, green, blue);
    }

    private static int transform (int channelIntensity, int leftValue, int rightValue, boolean decrease) {
        if (channelIntensity < leftValue){
            return 0;
        } else if (channelIntensity > rightValue){
            return 255;
        } else if (!decrease){
            return (int) Math.round(((double)(channelIntensity - leftValue) / (rightValue - leftValue) * 255));
        } else {
            return (int) Math.round(leftValue +  (double) channelIntensity * (rightValue - leftValue) / 255);
        }
    }
}
