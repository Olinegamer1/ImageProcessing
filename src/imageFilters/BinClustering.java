package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

public class BinClustering extends Filter {
    public static void applyFilter( ImageView imageField, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
           protected Void call() throws InterruptedException {
                Task<Void> task1 = new Task<>() {
                    @Override
                    protected Void call() {
                        GrayScale.applyFilter(imageField, indicator);
                        return null;
                    }
                };
                indicator.progressProperty().bind(task1.progressProperty());
                task1.setOnSucceeded(event -> unbindIndicator(indicator));
                new Thread(task1).join();

                int[] histogram = getHistogram();

                int m1 = 64;
                int m2 = 192;
                int n1 = 0;
                int n2 = 0;
                int border = 0;

                while (m1 != n1 & m2 != n2) {
                    border = getBorder(border, m2, m1);
                    int sum1 = getSum(border, histogram, true);
                    int sum2 = getSum(border, histogram, false);
                    n1 = getN(n1, sum1, border, histogram, true);
                    n2 = getN(n2, sum2, border, histogram, false);

                    if (m1 != n1 & m2 != n2) {
                        int tmp1 = n1;
                        int tmp2 = n2;
                        n1 = 0;
                        n2 = 0;
                        m1 = tmp1;
                        m2 = tmp2;
                    }
                }

                for (int j = 0; j < photo.getHeight(); j++) {
                    for (int i = 0; i < photo.getWidth(); i++) {
                        if (pixelReader.getColor(i, j).getRed() * 255 < border){
                            pixelWriter.setColor(i, j, Color.rgb(0, 0, 0));
                        } else {
                            pixelWriter.setColor(i, j, Color.rgb(255, 255, 255));
                        }
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

    private static int getBorder(int border, int m2, int m1) {
        for (int i = 0; i < 256; i++) {
            if (FastMath.abs(m2 - i) <= FastMath.abs(m1 - i)) {
                border = i;
                break;
            }
        }
        return border;
    }

    private static int getN(int n, int sum, int border, int[] histogram, boolean isN1) {
        int i = isN1 ? 1 : border;
        int condition = isN1 ? border : 256;
        for (; i < condition; i++) {
            sum = sum - histogram[i];
            if (sum <= 0) {
                n = i;
                break;
            }
        }
        return n;
    }

    private static int getSum(int border, int[] histogram, boolean isSum1) {
        int sum = 0;
        int i = isSum1 ? 0 : border;
        for (; i < 256; i++) {
            sum += histogram[i];
        }
        return sum / 2;
    }
}
