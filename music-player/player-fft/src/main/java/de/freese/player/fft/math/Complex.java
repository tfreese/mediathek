package de.freese.player.fft.math;

import java.util.Objects;

/**
 * Class representing a complex number and its operations.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class Complex {
    /**
     * Converts an array of values to an array of Complex values.<br>
     * The imaginary component always 0.
     */
    public static Complex[] toComplex(final int[] values) {
        int length = values.length;

        if (length % 2 != 0) {
            length--;
        }

        final Complex[] complexes = new Complex[length];

        for (int i = 0; i < complexes.length; i++) {
            complexes[i] = new Complex(values[i], 0D);
        }

        return complexes;
    }

    private final double im;
    private final double re;

    public Complex(final double real, final double imag) {
        super();

        re = real;
        im = imag;
    }

    /**
     * return abs/modulus/magnitude
     */
    public double abs() {
        return Math.hypot(re, im);
    }

    /**
     * return a new Complex object whose value is the conjugate of this
     */
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    /**
     * return a new Complex object whose value is the complex cosine of this
     */
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    /**
     * Divides this complex number by a scalar.
     *
     * @param n The divisor which is a real number.
     *
     * @return The quotient.
     */
    public Complex divide(final double n) {
        return new Complex(this.re / n, this.im / n);
    }

    /**
     * return a / b
     */
    public Complex divide(final Complex b) {
        return times(b.reciprocal());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final Complex complex)) {
            return false;
        }

        return Double.compare(im, complex.im) == 0 && Double.compare(re, complex.re) == 0;
    }

    /**
     * return a new Complex object whose value is the complex exponential of this
     */
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    @Override
    public int hashCode() {
        return Objects.hash(im, re);
    }

    /**
     * return a new Complex object whose value is (this - b)
     */
    public Complex minus(final Complex b) {
        return new Complex(re - b.re, im - b.im);
    }

    /**
     * return angle/phase/argument, normalized to be between -pi and pi
     */
    public double phase() {
        return Math.atan2(im, re);
    }

    /**
     * return a new Complex object whose value is (this + b)
     */
    public Complex plus(final Complex b) {
        return new Complex(re + b.re, im + b.im);
    }

    /**
     * return a new Complex object whose value is the reciprocal of this
     */
    public Complex reciprocal() {
        final double scale = re * re + im * im;

        return new Complex(re / scale, -im / scale);
    }

    /**
     * return a new object whose value is (this * alpha)
     */
    public Complex scale(final double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    /**
     * return a new Complex object whose value is the complex sine of this
     */
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    /**
     * return a new Complex object whose value is the complex tangent of this
     */
    public Complex tan() {
        return sin().divide(cos());
    }

    /**
     * return a new Complex object whose value is (this * b)
     */
    public Complex times(final Complex b) {
        final double real = re * b.re - im * b.im;
        final double imag = re * b.im + im * b.re;

        return new Complex(real, imag);
    }

    @Override
    public String toString() {
        final String toString;

        if (Double.compare(im, 0D) == 0) {
            toString = re + "";
        }
        else if (Double.compare(re, 0D) == 0) {
            toString = im + "i";
        }
        else if (im < 0D) {
            toString = re + " - " + (-im) + "i";
        }
        else {
            toString = re + " + " + im + "i";
        }

        return toString;
    }
}
