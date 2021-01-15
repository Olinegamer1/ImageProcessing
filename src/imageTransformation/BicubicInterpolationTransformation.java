package imageTransformation;

import javafx.concurrent.Task;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.apache.commons.math3.linear.*;

import java.util.Arrays;

import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.floor;


public class BicubicInterpolationTransformation extends Transformation {
    public static void applyTransformation(ImageView imageField, Slider slider){
        update();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                double scale = slider.getValue();
                writableImage = new WritableImage((int) (photoTransformable.getWidth() * scale - floor(scale) - 5), (int) (photoTransformable.getHeight() * scale -   floor(scale) - 5));
                pixelWriter = writableImage.getPixelWriter();

                for (int j = 0; j < writableImage.getHeight(); j++){
                    for (int i = 0; i < writableImage.getWidth(); i++){
                        double x = i / scale;
                        double y = j / scale;
                        int u = (int) floor(i / scale);
                        int v = (int) floor(j / scale);
                        int[] rgb = new int[3];
                        for (int color = 0; color < 3; color++) {
                            double[] columnX = new double[4];
                            for (int column = 0; column < 4; column++) {
                                RealMatrix matrix = getMatrix1(v);
                                RealVector vector = getVector1(u, v, column, color, pixelReader);
                                RealVector solution = new LUDecomposition(matrix).getSolver().solve(vector);
                                double A = solution.getEntry(0);
                                double B = solution.getEntry(1);
                                double C = solution.getEntry(2);
                                double D = getCurrentColor(pixelReader, u - 1 + column, v - 1, color) - (A * pow(v - 1, 3) + B * pow(v - 1, 2) + C * (v - 1));
                                columnX[column] = A * pow(y, 3) + B * pow(y, 2) + C * y + D;
                            }
                            RealMatrix matrix = getMatrix2(u);
                            RealVector vector = getVector2(columnX);
                            RealVector solution = new LUDecomposition(matrix).getSolver().solve(vector);
                            double A = solution.getEntry(0);
                            double B = solution.getEntry(1);
                            double C = solution.getEntry(2);
                            double D = columnX[0] - (A * pow(u - 1, 3) + B * pow(u - 1, 2) + C * (u - 1));
                            rgb[color] = (int) ((A * pow(x, 3) + B * pow(x, 2) + C * x + D));
                        }
                        pixelWriter.setColor(i, j, checkThreshold(rgb[0], rgb[1], rgb[2]));
                    }
                }
                imageField.setImage(writableImage);
                photoTransformable.setImage(writableImage);
                writableImage.cancel();
                return null;
            }
        };
        new Thread(task).start();
    }



    private static RealMatrix getMatrix1(int v) {
        double[][] matrix = new double[][] {
                {pow(v, 3) - pow(v-1, 3), pow(v, 2) - pow(v-1, 2), v - (v-1)},
                {pow(v+1, 3) - pow(v-1, 3), pow(v+1, 2) - pow(v-1, 2), v+1 - (v-1)},
                {pow(v+2, 3) - pow(v-1, 3), pow(v+2, 2) - pow(v-1, 2), v+2 - (v-1)}};
        return new Array2DRowRealMatrix(matrix, false);
    }

    private static RealMatrix getMatrix2(int u) {
        double[][] matrix = new double[][] {
                {pow(u, 3) - pow(u-1, 3), pow(u, 2) - pow(u-1, 2), pow(u, 1) - pow(u-1, 1)},
                {pow(u+1, 3) - pow(u-1, 3), pow(u+1, 2) - pow(u-1, 2), pow(u+1, 1) - pow(u-1, 1)},
                {pow(u+2, 3) - pow(u-1, 3), pow(u+2, 2) - pow(u-1, 2), pow(u+2, 1) - pow(u-1, 1)}};
        return new Array2DRowRealMatrix(matrix, false);
    }

    private static RealVector getVector1(int u, int v, int column, int color, PixelReader pixelReader) {
        double[] vector = new double[] {getCurrentColor(pixelReader, u - 1 + column, v, color) -
                getCurrentColor(pixelReader, u - 1 + column, v - 1, color),
                getCurrentColor(pixelReader, u - 1 + column, v + 1, color) -
                getCurrentColor(pixelReader, u - 1 + column, v - 1,color),
                getCurrentColor(pixelReader,u - 1 + column, v + 2, color) -
                getCurrentColor(pixelReader, u - 1 + column, v - 1, color)
        };
        return new ArrayRealVector(vector, false);
    }

    private static RealVector getVector2(double[] columnX) {
        double[] vector = new double[] {columnX[1] - columnX[0], columnX[2] - columnX[0], columnX[3] - columnX[0]};
        return new ArrayRealVector(vector, false);
    }

    private static int getCurrentColor(PixelReader pixelReader, int x, int y, int color){
        switch (color){
            case 0 -> { return (int) (pixelReader.getColor(abs(x), abs(y)).getRed() * 255); }
            case 1 -> { return (int) (pixelReader.getColor(abs(x), abs(y)).getBlue() * 255); }
            case 2 -> { return  (int) (pixelReader.getColor(abs(x),abs(y)).getGreen() * 255);}
        }
        return 0;
    }
}
