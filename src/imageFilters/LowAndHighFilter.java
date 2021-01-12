package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

public class LowAndHighFilter extends Filter {
    public static synchronized void applyFilter (ImageView imageField, Slider lowAndHighFilterSlider, ProgressIndicator indicator){
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int value = (int) lowAndHighFilterSlider.getValue();
                double[][] h = value < 4 ? selectorLowMatrix(value) : selectorHighMatrix(value);

                for (int j = 0; j < photo.getHeight() - 2; j++) {
                    for (int i = 0; i < photo.getWidth() - 2; i++) {
                        int[] colors = new int[3];
                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                sumColor(colors, multiplyKernel(h, x, y, i, j));
                            }
                        }
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, checkThreshold(colors[0], colors[1], colors[2]));
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

    private static double[][] selectorLowMatrix (int value) {
        double[][] h = new double[3][3];
        switch (value) {
            case 1 -> h = new double[][]{{1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0}, {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0}, {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0}};
            case 2 -> h = new double[][]{{1 / 10d, 1 / 10d, 1 / 10d}, {1 / 10d, 1 / 5d, 1 / 10d}, {1 / 10d, 1 / 10d, 1 / 10d}};
            case 3 -> h = new double[][]{{1 / 16d, 1 / 8d, 1 / 16d}, {1 / 8d, 1 / 4d, 1 / 8d}, {1 / 16d, 1 / 8d, 1 / 16d}};
        }
        return h;
    }

    private static double[][] selectorHighMatrix(int value){
        double[][] h = new double[3][3];
        switch (value) {
            case 4 -> h = new double[][]{{-1.0, -1.0, -1.0}, {-1.0, 9.0, -1.0}, {-1.0, -1.0, -1.0}};
            case 5 -> h = new double[][]{{1.0, -2.0, 1.0}, {-2.0, 5.0, -2.0}, {1.0, -2.0, 1.0}};
        }
        return h;
    }
}
