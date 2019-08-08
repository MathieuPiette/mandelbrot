package be.mpiette.mandel.web;

import be.mpiette.mandel.calculation.Complex;
import be.mpiette.mandel.calculation.MandelbrotCalculator;
import be.mpiette.mandel.color.BlackAndWhiteColorScheme;
import be.mpiette.mandel.image.ImageExporter;
import be.mpiette.mandel.parallel.ColumnParallelCalculator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController public class MandelController {

	Logger LOGGER = LoggerFactory.getLogger(MandelController.class);

	private File generateOutputFile() {
		return new File(FileUtils.getTempDirectory(),
				"mandel-" + UUID.randomUUID().toString() + ".png");
	}

	@GetMapping("/") public ResponseEntity<byte[]> mandel(@RequestParam final int maxIter,
			@RequestParam final int width, @RequestParam final int height,
			@RequestParam final double zoom, @RequestParam final double centerX,
			@RequestParam final double centerY, @RequestParam final int threads)
			throws IOException {
		final File file = generateOutputFile();
		Complex center = new Complex(centerX, centerY);

		LOGGER.info("\n" + //
						"Maximum iterations: {}\n" + //
						"Width:              {} pixels\n" + //
						"Height:             {} pixels\n" + //
						"Zoom:               × {}\n" + //
						"Center:             {}\n" + //
						"Threads:            {}\n" + //
						"Output:             {}",
				maxIter,
				width,
				height,
				zoom,
				center,
				threads,
				file.getAbsolutePath());

		final HttpHeaders headers = new HttpHeaders();
		ImageExporter imageExporter = new ImageExporter(file, width, height);

		double imageRatio = (double) width / (double) height;

		Complex topLeft = new Complex(center.real - imageRatio / zoom, center.imaginary - 1.0 / zoom);
		Complex bottomRight = new Complex(center.real + imageRatio / zoom, center.imaginary + 1.0 / zoom);
		ColumnParallelCalculator calculator = new ColumnParallelCalculator(
				imageExporter,
				threads,
				topLeft,
				bottomRight,
				new MandelbrotCalculator(maxIter),
				new BlackAndWhiteColorScheme(maxIter));
		calculator.process();

		LOGGER.info("Total number of iterations = {}", calculator.getTotalNumberOfOperations());

		final byte[] media = IOUtils.toByteArray(new FileInputStream(file));
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(media.length);

		return new ResponseEntity<>(media, headers, HttpStatus.OK);
	}

	@GetMapping("/colors") public ResponseEntity<byte[]> colors() throws IOException {
		int height = 32;
		int width = 1024;

		final HttpHeaders headers = new HttpHeaders();
		final File output = new File(FileUtils.getTempDirectory(),
				"colors-" + System.currentTimeMillis() + ".png");

		List<Color> colorsList = new BlackAndWhiteColorScheme(width).generate();

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, colorsList.get(x).getRGB());
			}
		}
		ImageIO.write(image, "png", output);

		final byte[] media = IOUtils.toByteArray(new FileInputStream(output));
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(media.length);

		return new ResponseEntity<>(media, headers, HttpStatus.OK);
	}
}
