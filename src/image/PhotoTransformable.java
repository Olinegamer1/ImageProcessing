package image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class PhotoTransformable {
    private static PhotoTransformable photoTransformable;
    private Image image;

    private PhotoTransformable(){}

    public static synchronized PhotoTransformable getInstance() {
        if (photoTransformable == null){
            photoTransformable = new PhotoTransformable();
        }
        return photoTransformable;
    }

    public synchronized Image getImage(){
        return photoTransformable.image;
    }

    public synchronized void setImage(Image image){
        photoTransformable.image = image;
    }

    public synchronized double getWidth() {
        return getImage().getWidth();
    }

    public synchronized double getHeight() {
        return getImage().getHeight();
    }

    public WritableImage getWritableImage() {
        return new WritableImage((int) getWidth(), (int) getHeight());
    }

    public PixelReader getPixelReader() {
        return getImage().getPixelReader();
    }
}
