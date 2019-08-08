package be.mpiette.mandel.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageExporter {

	private final File outputFile;
	public final int width, height;
	private final BufferedImage image;

	public ImageExporter(File outputFile, int width, int height) {
		this.outputFile = outputFile;
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public void setRGB(int x, int y, int rgb) {
		image.setRGB(x, y, rgb);
	}

	public void flush() throws IOException {
		ImageIO.write(image, "png", outputFile);
	}

}
