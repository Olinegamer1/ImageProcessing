package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Histogram extends Filter{
    public static void drawHistogram (Canvas canvas) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int[] histogram = getHistogram();
                GraphicsContext gc = initGraphicContext(canvas);

                int maxHeight = (int) ((maxVal(histogram) / canvas.getHeight()) + 1);

                for (int i = 0; i < histogram.length; i++){
                    drawLine(gc, i, histogram[i], maxHeight);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private static void drawLine(GraphicsContext gc, int step, int heightColumn, int maxHeight) {
        gc.setStroke(Color.rgb(step, step, step));
        gc.strokeLine(256-step, 0, 256-step, (double) heightColumn / maxHeight);
    }

    private static GraphicsContext initGraphicContext(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(159, 166, 215, 0.01));
        gc.fillRect(0, 0, 256, 200);
        gc.setLineWidth(2.0);
        return gc;
    }

    private static int maxVal (int[] histogram){
        int max = Integer.MIN_VALUE;

        for (int i : histogram){
            if (i > max) max = i;
        }
        return max;
    }
}
