package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Median extends Filter{
    public static synchronized void applyFilter (ImageView imageField, Slider medianSlider, ProgressIndicator indicator){
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int medianLength = (int) medianSlider.getValue();
                for (int j = 0; j < photo.getHeight() - medianLength; j++) {
                    for (int i = 0; i < photo.getWidth() - medianLength; i++) {
                        List<Integer> colors = new ArrayList<>();
                        for (int x = 0; x < medianLength; x++){
                            for (int y = 0; y < medianLength; y++){
                                Color color = pixelReader.getColor(i + y, j + x);
                                addChannels(colors, color);
                            }
                        }
                        colors.sort(Integer::compareTo);
                        int color = colors.get(colors.size() / 2);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, Color.rgb(color, color, color));
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

    private static void addChannels (List<Integer> colors, Color color){
        int[] rgb = getRgbFormatColor(color);

        colors.add(rgb[0]);
        colors.add(rgb[1]);
        colors.add(rgb[2]);
    }
}
