package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;


public class Sobel extends Filter{
    private final static double[][] SOBEL_X = {{-1, -2, -1}, {0, 0, 0}, {1,2 ,1}};
    private final static double[][] SOBEL_Y = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};

    private static synchronized double[][] getHigherSobelFilter(double[][] Kernel) {

        double[][] baseMatrix = new double[][] {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}};

        int KernelSize = Kernel[0].length;
        int HalfKernelSize = KernelSize / 2;
        int OutSize = KernelSize + 2;

        double[][] Out = new double[OutSize][OutSize];
        double[][] InMatrix = new double[OutSize][OutSize];

        for (int x = 0; x < baseMatrix[0].length; x++)
            System.arraycopy(baseMatrix[x], 0, InMatrix[HalfKernelSize + x], HalfKernelSize, baseMatrix[1].length);

        for (int x = 0; x < OutSize; x++)
            for (int y = 0; y < OutSize; y++)
                for (int Kx = 0; Kx < KernelSize; Kx++)
                    for (int Ky = 0; Ky < KernelSize; Ky++)
                    {
                        int X = x + Kx - HalfKernelSize;
                        int Y = y + Ky - HalfKernelSize;

                        if (X >= 0 && Y >= 0 && X < OutSize && Y < OutSize)
                            Out[x][y] += InMatrix[X][Y] * Kernel[KernelSize - 1 - Kx][KernelSize - 1 - Ky];
                    }
        return Out;
    }

    private static double[][] getSobelKernel(double sizeKernel, double[][] kernel) {
        int size = sizeKernel % 2 == 1 ? (int) sizeKernel : (int) sizeKernel -1;
        double[][] sobelKernel = kernel;

        while (sobelKernel.length < size){
            sobelKernel = getHigherSobelFilter(sobelKernel);
        }

        return sobelKernel;
    }


    public static void applyFilter(ImageView imageField, Slider slider, ProgressIndicator indicator) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int value = (int) slider.getValue();
                int size = value % 2 == 1 ? value : value - 1;
                double[][] sobelX = getSobelKernel(size, SOBEL_X);
                double[][] sobelY = getSobelKernel(size, SOBEL_Y);

                for (int j = 0; j < photo.getHeight() - size; j++) {
                    for (int i = 0; i < photo.getWidth() - size; i++) {
                        int[] sumX = new int[3];
                        int[] sumY = new int[3];
                        for (int x = 0; x < size; x++) {
                            for (int y = 0; y < size; y++) {
                                sumColor(sumX, multiplyKernel(sobelX, x, y, i, j));
                                sumColor(sumY, multiplyKernel(sobelY, x, y, i, j));
                            }
                        }
                        int[] rgb = differenceBrightness(sumX, sumY);
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

    private static int[] differenceBrightness(int[] x, int[] y){
        int[] rgb = new int[3];
        for (int i = 0; i < x.length; i++){
            rgb[i] = (int) Math.sqrt(Math.pow(x[i], 2) + Math.pow(y[i], 2));
        }
        return rgb;
    }
}
