package imageFilters;

import imageTransformation.Rotate;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import static org.apache.commons.math3.util.FastMath.toDegrees;
import static org.apache.commons.math3.util.FastMath.atan;


public class AlignText extends Filter {
    public static synchronized void applyFilter(ImageView imageField, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int j = 1;
                double[] point1 = getPoint(true);
                double[] point2 = getPoint(false);
                double angle = toDegrees(atan((point1[0] - point2[0]) / (point2[1] - point1[1])));
                Rotate.applyTransformation(0, 0, imageField, new TextField("" + angle));
                return null;
            }
        };
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> unbindIndicator(indicator));
        new Thread(task).start();
    }

    private static double[] getPoint(boolean isPoint1) {
        double[] point = new double[2];
        int j = 1;
        boolean check = true;
        while (check) {
            for (int i = 0; i < j; i++) {
                if (isPoint1){
                    if (pixelReader.getColor(i, j - i - 1).getRed() != 1) {
                        point[0] = i;
                        point[1] = j - i - 1;
                        check = false;
                    }
                } else if (pixelReader.getColor(i, (int) (photo.getHeight() - j - i - 1)).getRed() != 1) {
                    point[0] = i;
                    point[1] = (int) (photo.getHeight() - j - i - 1);
                    check = false;
                }
            }
            j += 1;
        }
        return point;
    }
}
