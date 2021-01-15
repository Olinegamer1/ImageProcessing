package controllers;

import image.PhotoTransformable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;

public class CaptureWindow{
    private static final int LINE_WIDTH = 2;
    private static Canvas canvas = null;
    private static GraphicsContext gc = null;
    private static CaptureWindow captureWindow;
    private static ImageView imageViewResizeable;
    private static final Robot robot = new Robot();
    private static final PhotoTransformable photoTransformable = PhotoTransformable.getInstance();

    private static double xPressedGlobal;
    private static double yPressedGlobal;
    private static double xNowGlobal;
    private static double yNowGlobal;

    private static int width;
    private static int height;
    private static int xPressed = 0;
    private static int yPressed = 0;
    private static int xNow = 0;
    private static int yNow = 0;

    private final Color FOREGROUND = Color.rgb(255, 167, 0);
    private final Color BACKGROUND = Color.rgb(0, 0, 0, 0.1);

    private CaptureWindow() {
    }

    public static synchronized CaptureWindow getInstance(){
        if (captureWindow == null){
            captureWindow = new CaptureWindow();
        }
        return captureWindow;
    }

    private void init(Canvas canvas){
        CaptureWindow.canvas = canvas;
        gc = canvas.getGraphicsContext2D();

        canvas.setWidth(canvas.getWidth());
        canvas.setHeight(canvas.getHeight());

        canvas.setOnMousePressed(mouseEvent -> {
            xPressed = (int) mouseEvent.getX();
            yPressed = (int) mouseEvent.getY();
            xPressedGlobal = robot.getMouseX();
            yPressedGlobal = robot.getMouseY();
        });
        canvas.setOnMouseDragged(mouseEvent -> {
            xNow = (int) mouseEvent.getX();
            yNow = (int) mouseEvent.getY();
            xNowGlobal = robot.getMouseX();
            yNowGlobal = robot.getMouseY();
            repaintCanvas();
        });
        canvas.setOnMouseReleased(mouseEvent -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            getSelectedImage();
        });

        gc.setLineDashes(10);
    }

    private void repaintCanvas() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(FOREGROUND);
        gc.setFill(BACKGROUND);
        gc.setLineWidth(LINE_WIDTH);

        if (xNow > xPressed && yNow > yPressed) {

            calculateWidthAndHeight(xNow - xPressed, yNow - yPressed);
            gc.strokeRect(xPressed, yPressed, width, height);
            gc.fillRect(xPressed, yPressed, width, height);

        } else if (xNow < xPressed && yNow < yPressed) {

            calculateWidthAndHeight(xPressed - xNow, yPressed - yNow);
            gc.strokeRect(xNow, yNow, width, height);
            gc.fillRect(xNow, yNow, width, height);

        } else if (xNow > xPressed && yNow < yPressed) {

            calculateWidthAndHeight(xNow - xPressed, yPressed - yNow);
            gc.strokeRect(xPressed, yNow, width, height);
            gc.fillRect(xPressed, yNow, width, height);

        } else if (xNow < xPressed && yNow > yPressed) {

            calculateWidthAndHeight(xPressed - xNow, yNow - yPressed);
            gc.strokeRect(xNow, yPressed, width, height);
            gc.fillRect(xNow, yPressed, width, height);
        }

    }

    public void showWindow(Canvas canvas, ImageView imageViewResizeable) {
        CaptureWindow.imageViewResizeable = imageViewResizeable;
        init(canvas);
        xNow = 0;
        yNow = 0;
        xPressed = 0;
        yPressed = 0;
        repaintCanvas();
    }

    private void calculateWidthAndHeight(int w, int h) {
        width = w;
        height = h;
    }

    private int calculateMinValue(double num, double num1){
        return (int) Math.min(num, num1);
    }

    private int calculateMaxValue(double num, double num1){
        return (int) Math.max(num, num1);
    }

    public void getSelectedImage(){
        int minXGlobal = calculateMinValue(xNowGlobal, xPressedGlobal) + LINE_WIDTH;
        int minYGlobal = calculateMinValue(yNowGlobal, yPressedGlobal) + LINE_WIDTH;
        int maxXGlobal = calculateMaxValue(xNowGlobal, xPressedGlobal) - LINE_WIDTH;
        int maxYGlobal = calculateMaxValue(yNowGlobal, yPressedGlobal) - LINE_WIDTH;
        int width = maxXGlobal - minXGlobal;
        int height = maxYGlobal - minYGlobal;

        if (width > 0 & height > 0){
            WritableImage writableImage = new WritableImage(width, height);
            robot.getScreenCapture(writableImage, minXGlobal, minYGlobal, width, height);
            //imageViewResizeable.setViewport(new Rectangle2D(0, 0, width, height));
            imageViewResizeable.setImage(writableImage);
            photoTransformable.setImage(writableImage);
        }
    }
}