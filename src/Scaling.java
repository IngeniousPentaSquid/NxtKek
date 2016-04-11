import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.imgscalr.*;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

import javax.imageio.ImageIO;

public class Scaling {

	//File storedImage = null;
	//BufferedImage bImage = null;
	BufferedImage thumbImage = null;
	ArrayList<File> storedImages;
	ArrayList<BufferedImage> bImages;
	ArrayList<BufferedImage> storyImages;

	public Scaling() {
		storedImages = new ArrayList<File>();
		bImages = new ArrayList<BufferedImage>();
		storyImages = new ArrayList<BufferedImage>();
	}

	/**
	 * Sets the file the class is going to work with
	 * @param img
	 */
	public void addFiles(File img) {
		storedImages.add(img);
	}
	
	public void cleanUp() {
		storedImages.clear();
		bImages.clear();
		storyImages.clear();
	}
	
	/**
	 * Creates a buffered image to use with the file.
	 * A copy of the image is used, so the original file
	 * is not edited.
	 */
	public void createBImages() {
		System.out.println("Stored IMage size:" + storedImages.size());
		try {
			for (File storedImage : storedImages) {
				BufferedImage bi = ImageIO.read(storedImage);
				ColorModel cm = bi.getColorModel();
				boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
				WritableRaster raster = bi.copyData(null);
				BufferedImage toAdd = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
				bImages.add(toAdd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resizes the image as a thumbnail, with the given width and height.
	 * Original aspect ratio is maintained in respect to the original image.
	 * The method calculates the aspect ratio, by diving original dimensions (width / height)
	 * If the aspect ratio is greater than 1, distortion will occur, thus the
	 * width will have to be adjusted to ensure original ratios work out.
	 * 
	 * The params for the resize method are (image to work with, quality, scale method, new width, new height, render options)
	 * Render options are not used.
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage resizeThumb(BufferedImage bImage, int width, int height) {
		double iWidth = bImage.getWidth();
		double iHeight = bImage.getHeight();
		double ratio = iWidth / iHeight;
		if (ratio >= 1) {
			width = (int) (height * ratio);
		}
		thumbImage = Scalr.resize(bImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height, null);
		return thumbImage;
	}

	/**
	 * Resizes the image as a main (story), with the given width and height.
	 * Original aspect ratio is maintained in respect to the original image.
	 * The method calculates the aspect ratio, by diving original dimensions (width / height)
	 * If the aspect ratio is greater than 1, distortion will occur, thus the
	 * width will have to be adjusted to ensure original ratios work out.
	 * 
	 * The params for the resize method are (image to work with, quality, scale method, new width, new height, render options)
	 * Render options are not used.
	 * @param width
	 * @param height
	 * @return
	 */
	public void resizeStory(BufferedImage bImage, int width, int height) {
		double iWidth = bImage.getWidth();
		double iHeight = bImage.getHeight();
		double ratio = iHeight / iWidth;
		double resizeHeight = width * ratio;
		ResampleOp resampleOp = new ResampleOp(width, (int) resizeHeight);
		//resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
		storyImages.add(resampleOp.filter(bImage, null));
		//storyImage = Scalr.resize(bImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, (int) iHeight, Scalr.OP_ANTIALIAS);
	}


	/**
	 * Crops the image given the arguments
	 * 
	 * @param x
	 * The x position from the top left in which to crop with
	 * @param y
	 * The y position from the top left in which to crop with
	 * @param width
	 * Crop width
	 * @param height
	 * Crop height
	 * @param type
	 * Story or Thumb Image
	 * @return
	 */
//	public BufferedImage cropImage(int x, int y, int width, int height, int type) {
//		try {
//			switch (type) {
//			case 0:
//				storyImage = Scalr.crop(storyImage, x, y, width, height);
//				break;
//			case 1:
//				thumbImage = Scalr.crop(thumbImage, x, y, width, height);
//				break;
//			}
//		} catch (IllegalArgumentException e) {
//			MainInterface.statusField.setText("Invalid Crop X");
//		}
//		return storyImage;
//	}

	/**
	 * Returns the thumbImage
	 * @return
	 */
	public BufferedImage getThumbImage() {
		return thumbImage;
	}
	
	/**
	 * Returns the main (story) image.
	 * @return
	 */
//	public BufferedImage getStoryImage() {
//		return storyImage;
//	}

}
