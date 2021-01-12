package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

public class Kirsch extends Filter{
    private final static double[][][] KIRSCH_KERNELS = {
            {{5, 5, 5}, {-3, 0, -3}, {-3, -3, -3}},
            {{-3, 5, 5}, {-3, 0, 5}, {-3, -3, -3}},
            {{-3, -3, 5}, {-3, 0, 5}, {-3, -3, 5}},
            {{-3, -3, -3}, {-3, 0, 5}, {-3, 5, 5}},
            {{-3, -3, -3}, {-3, 0, -3}, {5, 5, 5}},
            {{-3, -3, -3}, {5, 0, -3}, {5, 5, -3}},
            {{5, -3, -3}, {5, 0, -3}, {5, -3, -3}},
            {{5, 5, -3}, {5, 0, -3}, {-3, -3, -3}}};

    public static synchronized void applyFilter(ImageView imageField, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int j = 0; j < photo.getHeight() - 2; j++) {
                    for (int i = 0; i < photo.getWidth() - 2; i++) {
                        int[][] maximumGradient = new int[8][3];
                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                for (int z = 0; z < 7; z++) {
                                    sumColor(maximumGradient[z], multiplyKernel(KIRSCH_KERNELS[z], x, y, i, j));
                                }
                            }
                        }
                        int[] rgb = maxColor(maximumGradient);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, checkThreshold(rgb[0], rgb[1], rgb[2]));
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

    private static int[] maxColor(int[][] colors){
        int[] rgb = new int[3];
        int[] prevRGB = new int[3];
        for (int[] color : colors) {
            for (int j = 0; j < color.length; j++) {
                if (prevRGB[j] < color[j]) {
                    prevRGB[j] = color[j];
                    rgb[j] = prevRGB[j];
                }
            }
        }
        return rgb;
    }
}
