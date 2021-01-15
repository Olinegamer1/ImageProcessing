package imageFilters;

import javafx.concurrent.Task;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PseudoColoring extends Filter {
    public static synchronized void applyFilter(ImageView imageField, ColorPicker one, ColorPicker two, ColorPicker three, ColorPicker four, ColorPicker five, ColorPicker six, ProgressIndicator indicator, Slider... slidersQ ) {
        if (imageField.getImage() == null){
            return;
        }

        update();
        isVisibleIndicator(indicator);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int j = 0; j < photo.getHeight(); j++) {
                    for (int i = 0; i < photo.getWidth(); i++) {
                        Color color = pixelReader.getColor(i, j);
                        updateProgress(getCurrentProgress(), totalProgressProcessing());
                        pixelWriter.setColor(i, j, paintColors(color, slidersQ, one, two, three, four, five, six));
                    }
                }
                imageField.setImage(wImage);
                wImage.cancel();
                return null;
            }
        };
        indicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> isVisibleIndicator(indicator));
        new Thread(task).start();
    }

    private static Color paintColors (Color color, Slider[] sliders, ColorPicker one, ColorPicker two, ColorPicker three, ColorPicker four, ColorPicker five, ColorPicker six){
        int[] sliderValues = getIntValues(sliders);
        int[] rgb = getRgbFormatColor(color);
        int[] firstPicker = getRgbFormatColor(one.getValue());
        int[] secondPicker = getRgbFormatColor(two.getValue());
        int[] thirdPicker = getRgbFormatColor(three.getValue());
        int[] foursPicker = getRgbFormatColor(four.getValue());
        int[] fivesPicker = getRgbFormatColor(five.getValue());
        int[] sixesPicker = getRgbFormatColor(six.getValue());

        for (int channel = 0; channel < rgb.length; channel++){
            if (rgb[channel] < sliderValues[0]){
                rgb[channel] = firstPicker[channel];
            } else if (sliderValues[0] < rgb[channel] && rgb[channel] <= sliderValues[1]){
                rgb[channel] = secondPicker[channel];
            }else if (sliderValues[1] < rgb[channel] && rgb[channel] <= sliderValues[2]){
                rgb[channel] = thirdPicker[channel];
            }else if (sliderValues[2] < rgb[channel] && rgb[channel] <= sliderValues[3]){
                rgb[channel] = foursPicker[channel];
            }else if (sliderValues[3] < rgb[channel] && rgb[channel] <= sliderValues[4]){
                rgb[channel] = fivesPicker[channel];
            }else if (sliderValues[4] < rgb[channel] && rgb[channel] <= 255) {
                rgb[channel] = sixesPicker[channel];
            }
        }

        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

    private static int[] getIntValues(Slider[] sliders){
        int[] slidersValues = new int[sliders.length];

        for (int i = 0; i < slidersValues.length; i++){
            slidersValues[i] = (int) sliders[i].getValue();
        }

        return slidersValues;
    }
}
