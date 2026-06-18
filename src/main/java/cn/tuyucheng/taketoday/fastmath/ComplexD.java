package cn.tuyucheng.taketoday.fastmath;

/// Immutable complex number with double precision.
///
/// Complex numbers are represented as (real + i * imag).
/// This is the double-precision version for higher accuracy.
///
/// @see Complex for float-precision version
public final class ComplexD {

    /// Real part
    public final double re;
    
    /// Imaginary part
    public final double im;

    // Common constants
    public static final ComplexD ZERO = new ComplexD(0.0, 0.0);
    public static final ComplexD ONE = new ComplexD(1.0, 0.0);
    public static final ComplexD I = new ComplexD(0.0, 1.0);
    public static final ComplexD MINUS_I = new ComplexD(0.0, -1.0);
    public static final ComplexD NEGATIVE_ONE = new ComplexD(-1.0, 0.0);

    /// Creates a complex number with the given real and imaginary parts.
    ///
    /// @param real the real part
    /// @param imag the imaginary part
    public ComplexD(double real, double imag) {
        this.re = real;
        this.im = imag;
    }

    /// Creates a complex number with only real part (imaginary = 0).
    ///
    /// @param real the real part
    public ComplexD(double real) {
        this(real, 0.0);
    }

    /// Creates a complex number from polar coordinates (r, θ).
    /// Complex = r * (cos(θ) + i * sin(θ)) = r * e^(iθ)
    ///
    /// @param r the modulus (magnitude)
    /// @param theta the argument (phase angle in radians)
    /// @return ComplexD number in Cartesian form
    public static ComplexD fromPolar(double r, double theta) {
        return new ComplexD(r * Math.cos(theta), r * Math.sin(theta));
    }

    /// Creates a complex number from a real number.
    ///
    /// @param real the real value
    /// @return ComplexD with imaginary part = 0
    public static ComplexD fromReal(double real) {
        return new ComplexD(real, 0.0);
    }

    /// Creates a purely imaginary complex number.
    ///
    /// @param imag the imaginary value
    /// @return ComplexD with real part = 0
    public static ComplexD fromImaginary(double imag) {
        return new ComplexD(0.0, imag);
    }

    // ==================== Arithmetic Operations ====================

    /// Returns the sum of this complex and another.
    /// (a + bi) + (c + di) = (a+c) + (b+d)i
    ///
    /// @param other the complex number to add
    /// @return new ComplexD representing the sum
    public ComplexD add(ComplexD other) {
        return new ComplexD(this.re + other.re, this.im + other.im);
    }

    /// Returns the sum of this complex and a real number.
    ///
    /// @param real the real number to add
    /// @return new ComplexD with real part incremented
    public ComplexD add(double real) {
        return new ComplexD(this.re + real, this.im);
    }

    /// Returns the difference of this complex and another.
    /// (a + bi) - (c + di) = (a-c) + (b-d)i
    ///
    /// @param other the complex number to subtract
    /// @return new ComplexD representing the difference
    public ComplexD subtract(ComplexD other) {
        return new ComplexD(this.re - other.re, this.im - other.im);
    }

    /// Returns the difference of this complex and a real number.
    ///
    /// @param real the real number to subtract
    /// @return new ComplexD with real part decremented
    public ComplexD subtract(double real) {
        return new ComplexD(this.re - real, this.im);
    }

    /// Returns the product of this complex and another.
    /// (a + bi)(c + di) = (ac - bd) + (ad + bc)i
    ///
    /// @param other the complex number to multiply
    /// @return new ComplexD representing the product
    public ComplexD multiply(ComplexD other) {
        return new ComplexD(
            this.re * other.re - this.im * other.im,
            this.re * other.im + this.im * other.re
        );
    }

    /// Returns the product of this complex and a real scalar.
    /// s(a + bi) = sa + sbi
    ///
    /// @param scalar the real scalar multiplier
    /// @return new ComplexD scaled by the scalar
    public ComplexD multiply(double scalar) {
        return new ComplexD(this.re * scalar, this.im * scalar);
    }

    /// Returns the quotient of this complex divided by another.
    /// (a + bi) / (c + di) = ((ac + bd) + (bc - ad)i) / (c² + d²)
    ///
    /// @param other the complex number divisor
    /// @return new ComplexD representing the quotient
    /// @throws ArithmeticException if divisor is zero
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

    /// Returns the quotient of this complex divided by a real scalar.
    ///
    /// @param scalar the real scalar divisor
    /// @return new ComplexD divided by the scalar
    /// @throws ArithmeticException if scalar is zero
    public ComplexD divide(double scalar) {
        if (scalar == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        return new ComplexD(this.re / scalar, this.im / scalar);
    }

    /// Returns the negation of this complex.
    /// -(a + bi) = -a - bi
    ///
    /// @return new ComplexD with both parts negated
    public ComplexD negate() {
        return new ComplexD(-this.re, -this.im);
    }

    /// Returns the complex conjugate.
    /// conj(a + bi) = a - bi
    ///
    /// @return new ComplexD with imaginary part negated
    public ComplexD conjugate() {
        return new ComplexD(this.re, -this.im);
    }

    /// Returns the reciprocal (multiplicative inverse).
    /// 1 / (a + bi) = (a - bi) / (a² + b²)
    ///
    /// @return new ComplexD representing the reciprocal
    /// @throws ArithmeticException if this complex number is zero
    public ComplexD reciprocal() {
        double denominator = re * re + im * im;
        if (denominator == 0.0) {
            throw new ArithmeticException("Reciprocal of zero");
        }
        return new ComplexD(re / denominator, -im / denominator);
    }

    // ==================== Power and Root Operations ====================

    /// Returns the square of this complex number.
    /// (a + bi)² = (a² - b²) + 2abi
    ///
    /// @return new ComplexD representing the square
    public ComplexD square() {
        return new ComplexD(re * re - im * im, 2.0 * re * im);
    }

    /// Returns the square root of this complex number.
    /// √(r*e^(iθ)) = √r * e^(iθ/2)
    ///
    /// @return the principal square root
    public ComplexD sqrt() {
        if (re == 0.0 && im == 0.0) {
            return ZERO;
        }
        double r = abs();
        double sqrtR = Math.sqrt(r);
        double halfPhase = phase() / 2.0;
        return fromPolar(sqrtR, halfPhase);
    }

    /// Returns this complex number raised to a real power.
    /// (r\*e^(iθ))^n = r^n \* e^(i\*n\*θ)
    ///
    /// @param n the real exponent
    /// @return this complex raised to the power n
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

    /// Returns this complex number raised to an integer power.
    /// Uses fast exponentiation (binary method) for efficiency.
    ///
    /// @param n the integer exponent
    /// @return this complex raised to the power n
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

    /// Returns the complex number raised to another complex power.
    /// a^b = e^(b \* ln(a))
    ///
    /// @param exponent the complex exponent
    /// @return this complex raised to the complex exponent
    public ComplexD pow(ComplexD exponent) {
        if (re == 0.0 && im == 0.0) {
            return ZERO;
        }
        return log().multiply(exponent).exp();
    }

    /// Returns the natural logarithm of this complex number.
    /// ln(r*e^(iθ)) = ln(r) + iθ
    ///
    /// @return the natural logarithm as a complex number
    public ComplexD log() {
        return new ComplexD(Math.log(abs()), phase());
    }

    /// Returns e raised to this complex power.
    /// e^(a+bi) = e^a * (cos(b) + i*sin(b))
    ///
    /// @return the complex exponential
    public ComplexD exp() {
        double expReal = Math.exp(re);
        return new ComplexD(expReal * Math.cos(im), expReal * Math.sin(im));
    }

    // ==================== Trigonometric Functions ====================

    /// Returns the sine of this complex number.
    /// sin(a + bi) = sin(a)cosh(b) + i*cos(a)sinh(b)
    ///
    /// @return the complex sine
    public ComplexD sin() {
        return new ComplexD(
            Math.sin(re) * Math.cosh(im),
            Math.cos(re) * Math.sinh(im)
        );
    }

    /// Returns the cosine of this complex number.
    /// cos(a + bi) = cos(a)cosh(b) - i*sin(a)sinh(b)
    ///
    /// @return the complex cosine
    public ComplexD cos() {
        return new ComplexD(
            Math.cos(re) * Math.cosh(im),
            -Math.sin(re) * Math.sinh(im)
        );
    }

    /// Returns the tangent of this complex number.
    /// tan(z) = sin(z) / cos(z)
    ///
    /// @return the complex tangent
    public ComplexD tan() {
        return sin().divide(cos());
    }

    /// Returns the hyperbolic sine.
    /// sinh(a + bi) = sinh(a)cos(b) + i*cosh(a)sin(b)
    ///
    /// @return the complex hyperbolic sine
    public ComplexD sinh() {
        return new ComplexD(
            Math.sinh(re) * Math.cos(im),
            Math.cosh(re) * Math.sin(im)
        );
    }

    /// Returns the hyperbolic cosine.
    /// cosh(a + bi) = cosh(a)cos(b) + i*sinh(a)sin(b)
    ///
    /// @return the complex hyperbolic cosine
    public ComplexD cosh() {
        return new ComplexD(
            Math.cosh(re) * Math.cos(im),
            Math.sinh(re) * Math.sin(im)
        );
    }

    /// Returns the hyperbolic tangent.
    /// tanh(z) = sinh(z) / cosh(z)
    ///
    /// @return the complex hyperbolic tangent
    public ComplexD tanh() {
        return sinh().divide(cosh());
    }

    /// Returns the inverse sine (arcsin) of this complex number.
    /// asin(z) = -i * ln(iz + sqrt(1 - z²))
    ///
    /// @return the complex arcsin
    public ComplexD asin() {
        ComplexD iz = new ComplexD(-im, re);
        return iz.add(ONE.subtract(this.square()).sqrt()).log().multiply(MINUS_I);
    }

    /// Returns the inverse cosine (arccos) of this complex number.
    /// acos(z) = π/2 - asin(z)
    ///
    /// @return the complex arccos
    public ComplexD acos() {
        return asin().negate().add(Math.PI / 2);
    }

    /// Returns the inverse tangent (arctan) of this complex number.
    /// atan(z) = (i/2) * ln((i+z)/(i-z))
    ///
    /// @return the complex arctan
    public ComplexD atan() {
        ComplexD numerator = I.add(this);
        ComplexD denominator = I.subtract(this);
        return numerator.divide(denominator).log().multiply(new ComplexD(0, 0.5));
    }

    // ==================== Properties ====================

    /// Returns the absolute value (modulus/magnitude).
    /// |a + bi| = √(a² + b²)
    ///
    /// @return the magnitude of this complex number
    public double abs() {
        return Math.sqrt(re * re + im * im);
    }

    /// Returns the squared absolute value (avoids sqrt).
    /// |a + bi|² = a² + b²
    ///
    /// @return the squared magnitude
    public double absSquared() {
        return re * re + im * im;
    }

    /// Returns the argument (phase angle) in radians.
    /// θ = atan2(b, a)
    ///
    /// @return the phase angle in radians
    public double phase() {
        return Math.atan2(im, re);
    }

    /// Returns the real part.
    ///
    /// @return the real component
    public double real() {
        return re;
    }

    /// Returns the imaginary part.
    ///
    /// @return the imaginary component
    public double imag() {
        return im;
    }

    /// Returns true if this is a real number (imaginary part is zero).
    ///
    /// @return true if imaginary part equals zero
    public boolean isReal() {
        return im == 0.0;
    }

    /// Returns true if this is a purely imaginary number (real part is zero).
    ///
    /// @return true if real part equals zero and imaginary part is non-zero
    public boolean isImaginary() {
        return re == 0.0 && im != 0.0;
    }

    /// Returns true if this is zero (both real and imaginary parts are zero).
    ///
    /// @return true if both parts equal zero
    public boolean isZero() {
        return re == 0.0 && im == 0.0;
    }

    // ==================== Utility Methods ====================

    /// Returns an array [real, imaginary].
    ///
    /// @return a two-element double array
    public double[] toArray() {
        return new double[] { re, im };
    }

    /// Converts to single-precision complex number.
    /// Note: This may lose precision for large values.
    ///
    /// @return a Complex with float-precision components
    public Complex toComplex() {
        return new Complex((float) re, (float) im);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexD c)) return false;
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