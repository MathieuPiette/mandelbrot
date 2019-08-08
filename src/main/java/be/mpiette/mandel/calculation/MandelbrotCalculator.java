package be.mpiette.mandel.calculation;

public class MandelbrotCalculator implements Calculator {

	private final int maxIter;

	public MandelbrotCalculator(int maxIter) {
		this.maxIter = maxIter;
	}

	public int compute(Complex c) {
		double dx = 0, dy = 0;
		int iteration = 0;
		while (dx * dx + dy * dy <= 4 && iteration < maxIter) {
			final double x_new = dx * dx - dy * dy + c.real;
			dy = 2 * dx * dy + c.imaginary;
			dx = x_new;
			iteration++;
		}
		return iteration;
	}
}
