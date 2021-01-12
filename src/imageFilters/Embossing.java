package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

public class Embossing extends Filter{
    private final static double[][] EMBOSSING_ONE = {{0, -1, 0}, {1, 0, -1}, {0, 1, 0}};
    private final static double[][] EMBOSSING_TWO = {{0, 1, 0}, {-1, 0, 1}, {0, -1, 0}};

    public static synchronized void applyFilter(ImageView imageField, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }
        update();
        isVisibleIndicator(indicator);

        Task<Double> task = new Task<>() {
            @Override
            protected Double call() {
                for (int j = 0; j < photo.getHeight() - 2; j++) {
                    for (int i = 0; i < photo.getWidth() - 2; i++) {
                        int[] colors = new int[3];
                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                sumColor(colors, multiplyKernel(EMBOSSING_ONE, x, y, i, j));
                            }
                        }
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, checkThreshold(colors[0] + 128, colors[1] + 128, colors[2] + 128));
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

}
