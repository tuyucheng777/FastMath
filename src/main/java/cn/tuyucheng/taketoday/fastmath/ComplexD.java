package cn.tuyucheng.taketoday.fastmath;

/**
 * Immutable complex number with double precision.
 * 
 * <p>Complex numbers are represented as (real + i * imag).
 * This is the double-precision version for higher accuracy.
 * 
 * @see Complex for float-precision version
 */
public final class ComplexD {

    /** Real part */
    public final double re;
    
    /** Imaginary part */
    public final double im;

    // Common constants
    public static final ComplexD ZERO = new ComplexD(0.0, 0.0);
    public static final ComplexD ONE = new ComplexD(1.0, 0.0);
    public static final ComplexD I = new ComplexD(0.0, 1.0);
    public static final ComplexD MINUS_I = new ComplexD(0.0, -1.0);
    public static final ComplexD NEGATIVE_ONE = new ComplexD(-1.0, 0.0);

    public ComplexD(double real, double imag) {
        this.re = real;
        this.im = imag;
    }

    public ComplexD(double real) {
        this(real, 0.0);
    }

    public static ComplexD fromPolar(double r, double theta) {
        return new ComplexD(r * Math.cos(theta), r * Math.sin(theta));
    }

    public static ComplexD fromReal(double real) {
        return new ComplexD(real, 0.0);
    }

    public static ComplexD fromImaginary(double imag) {
        return new ComplexD(0.0, imag);
    }

    // ==================== Arithmetic Operations ====================

    public ComplexD add(ComplexD other) {
        return new ComplexD(this.re + other.re, this.im + other.im);
    }

    public ComplexD add(double real) {
        return new ComplexD(this.re + real, this.im);
    }

    public ComplexD subtract(ComplexD other) {
        return new ComplexD(this.re - other.re, this.im - other.im);
    }

    public ComplexD subtract(double real) {
        return new ComplexD(this.re - real, this.im);
    }

    public ComplexD multiply(ComplexD other) {
        return new ComplexD(
            this.re * other.re - this.im * other.im,
            this.re * other.im + this.im * other.re
        );
    }

    public ComplexD multiply(double scalar) {
        return new ComplexD(this.re * scalar, this.im * scalar);
    }

    public ComplexD divide(ComplexD other) {
        double denominator = other.re * other.re + other.im * other.im;
        if (denominator == 0.0) {
            throw new ArithmeticException("Division by zero complex number");
        }
        return new ComplexD(
            (this.re * other.re + this.im * other.im) / denominator,
            (this.im * other.re - this.re * other.im) / denominator
        );
    }

    public ComplexD divide(double scalar) {
        if (scalar == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        return new ComplexD(this.re / scalar, this.im / scalar);
    }

    public ComplexD negate() {
        return new ComplexD(-this.re, -this.im);
    }

    public ComplexD conjugate() {
        return new ComplexD(this.re, -this.im);
    }

    public ComplexD reciprocal() {
        double denominator = re * re + im * im;
        if (denominator == 0.0) {
            throw new ArithmeticException("Reciprocal of zero");
        }
        return new ComplexD(re / denominator, -im / denominator);
    }

    // ==================== Power and Root Operations ====================

    public ComplexD square() {
        return new ComplexD(re * re - im * im, 2.0 * re * im);
    }

    public ComplexD sqrt() {
        if (re == 0.0 && im == 0.0) {
            return ZERO;
        }
        double r = abs();
        double sqrtR = Math.sqrt(r);
        double halfPhase = phase() / 2.0;
        return fromPolar(sqrtR, halfPhase);
    }

    public ComplexD pow(double n) {
        if (re == 0.0 && im == 0.0) {
            return n > 0 ? ZERO : throwDivZero();
        }
        double r = abs();
        double theta = phase();
        return fromPolar(Math.pow(r, n), n * theta);
    }

    private static ComplexD throwDivZero() {
        throw new ArithmeticException("Zero to negative power");
    }

    public ComplexD pow(int n) {
        if (n == 0) return ONE;
        if (n == 1) return this;
        if (n < 0) return pow(-n).reciprocal();

        ComplexD result = ONE;
        ComplexD base = this;
        while (n > 0) {
            if ((n & 1) == 1) {
                result = result.multiply(base);
            }
            base = base.square();
            n >>= 1;
        }
        return result;
    }

    public ComplexD pow(ComplexD exponent) {
        if (re == 0.0 && im == 0.0) {
            return ZERO;
        }
        return log().multiply(exponent).exp();
    }

    public ComplexD log() {
        return new ComplexD(Math.log(abs()), phase());
    }

    public ComplexD exp() {
        double expReal = Math.exp(re);
        return new ComplexD(expReal * Math.cos(im), expReal * Math.sin(im));
    }

    // ==================== Trigonometric Functions ====================

    public ComplexD sin() {
        return new ComplexD(
            Math.sin(re) * Math.cosh(im),
            Math.cos(re) * Math.sinh(im)
        );
    }

    public ComplexD cos() {
        return new ComplexD(
            Math.cos(re) * Math.cosh(im),
            -Math.sin(re) * Math.sinh(im)
        );
    }

    public ComplexD tan() {
        return sin().divide(cos());
    }

    public ComplexD sinh() {
        return new ComplexD(
            Math.sinh(re) * Math.cos(im),
            Math.cosh(re) * Math.sin(im)
        );
    }

    public ComplexD cosh() {
        return new ComplexD(
            Math.cosh(re) * Math.cos(im),
            Math.sinh(re) * Math.sin(im)
        );
    }

    public ComplexD tanh() {
        return sinh().divide(cosh());
    }

    public ComplexD asin() {
        ComplexD iz = new ComplexD(-im, re);
        return iz.add(ONE.subtract(this.square()).sqrt()).log().multiply(MINUS_I);
    }

    public ComplexD acos() {
        return asin().negate().add(Math.PI / 2);
    }

    public ComplexD atan() {
        ComplexD numerator = I.add(this);
        ComplexD denominator = I.subtract(this);
        return numerator.divide(denominator).log().multiply(new ComplexD(0, 0.5));
    }

    // ==================== Properties ====================

    public double abs() {
        return Math.sqrt(re * re + im * im);
    }

    public double absSquared() {
        return re * re + im * im;
    }

    public double phase() {
        return Math.atan2(im, re);
    }

    public double real() {
        return re;
    }

    public double imag() {
        return im;
    }

    public boolean isReal() {
        return im == 0.0;
    }

    public boolean isImaginary() {
        return re == 0.0 && im != 0.0;
    }

    public boolean isZero() {
        return re == 0.0 && im == 0.0;
    }

    // ==================== Utility Methods ====================

    public double[] toArray() {
        return new double[] { re, im };
    }

    public Complex toComplex() {
        return new Complex((float) re, (float) im);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexD)) return false;
        ComplexD c = (ComplexD) o;
        return Double.compare(re, c.re) == 0 && Double.compare(im, c.im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }

    @Override
    public String toString() {
        if (im == 0.0) return String.format("%.6f", re);
        if (re == 0.0) return String.format("%.6fi", im);
        if (im < 0) return String.format("%.6f - %.6fi", re, -im);
        return String.format("%.6f + %.6fi", re, im);
    }
}