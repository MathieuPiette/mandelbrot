package be.mpiette.mandel.parallel;

import be.mpiette.mandel.calculation.Calculator;
import be.mpiette.mandel.calculation.Complex;
import be.mpiette.mandel.color.ColorScheme;
import be.mpiette.mandel.image.ImageExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ColumnParallelCalculator implements ParallelCalculator {

	Logger LOGGER = LoggerFactory.getLogger(ColumnParallelCalculator.class);

	private final ImageExporter output;
	private final int threads;
	private final Complex topLeft;
	private final Complex bottomRight;
	private final Calculator calculator;
	private final ColorScheme colorScheme;

	private AtomicInteger numberOfOperations;

	public ColumnParallelCalculator(ImageExporter output, int threads, Complex topLeft, Complex bottomRight,
			Calculator calculator, ColorScheme colorScheme) {
		this.output = output;
		this.threads = threads;
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.calculator = calculator;
		this.colorScheme = colorScheme;
		numberOfOperations = new AtomicInteger(0);
	}

	@Override public void process() throws IOException {
		final ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			final long start = System.currentTimeMillis();
			final List<Callable<Void>> callables = new ArrayList<>(output.width);
			for (int x = 0; x < output.width; x++) {
				callables.add(getCallableForColumn(x,
						output.height,
						output,
						colorScheme,
						calculator));
			}
			executor.invokeAll(callables);
			final long end = System.currentTimeMillis();
			LOGGER.info("Computed in {} milliseconds", end - start);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
		output.flush();
	}

	@Override public int getTotalNumberOfOperations() {
		return numberOfOperations.get();
	}

	private Callable<Void> getCallableForColumn(final int x, final int height,
			final ImageExporter output, ColorScheme colors, final Calculator calculator) {
		return new Callable<Void>() {

			@Override public Void call() throws Exception {
				final double real = topLeft.real + ((bottomRight.real - topLeft.real) / output.width) * x;
				for (int y = 0; y < height; y++) {
					final double imaginary = topLeft.imaginary + ((bottomRight.imaginary - topLeft.imaginary) / output.height) * y;
					Complex c = new Complex(real, imaginary);
					int iterations = calculator.compute(c);
					numberOfOperations.addAndGet(iterations);
					output.setRGB(x, y, colors.map(iterations).getRGB());
				}
				return null;
			}
		};
	}
}
