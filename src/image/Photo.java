package image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;


public class Photo {
    private static Photo photo;
    private Image image;

    private Photo(){}

    public static synchronized Photo getInstance() {
        if (photo == null){
            photo = new Photo();
        }
        return photo;
    }

    public synchronized Image getImage(){
        return photo.image;
    }

    public synchronized void setImage(Image image){
        photo.image = image;
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
