package be.mpiette.mandel.calculation;

public class Complex {

	public final double real;
	public final double imaginary;

	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public Complex add(Complex c) {
		return new Complex(real + c.real, imaginary + c.imaginary);
	}

	@Override public String toString() {
		return real + " + " + imaginary + "i";
	}
}
