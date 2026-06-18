package cn.tuyucheng.taketoday.fastmath;

/// Immutable complex number with float precision.
///
/// Complex numbers are represented as (real + i * imag), where:
/// - real - the real part
/// - imag - the imaginary part
/// - i - the imaginary unit (i² = -1)
///
/// Operations return new Complex instances (immutable pattern).
///
/// ## Example usage:
/// ```java
/// Complex a = new Complex(3, 4);       // 3 + 4i
/// Complex b = Complex.fromPolar(5, 0.927); // 5 * e^(i*0.927)
/// Complex c = a.add(b);                // Addition
/// Complex d = a.multiply(b);           // Multiplication
/// Complex e = a.conjugate();           // 3 - 4i
/// double magnitude = a.abs();          // 5.0
/// double phase = a.phase();            // 0.927 rad
/// ```
public final class Complex {

    /// Real part
    public final float re;
    
    /// Imaginary part
    public final float im;

    // Common constants
    public static final Complex ZERO = new Complex(0.0f, 0.0f);
    public static final Complex ONE = new Complex(1.0f, 0.0f);
    public static final Complex I = new Complex(0.0f, 1.0f);
    public static final Complex MINUS_I = new Complex(0.0f, -1.0f);
    public static final Complex NEGATIVE_ONE = new Complex(-1.0f, 0.0f);

    /// Creates a complex number with the given real and imaginary parts.
    ///
    /// @param real the real part
    /// @param imag the imaginary part
    public Complex(float real, float imag) {
        this.re = real;
        this.im = imag;
    }

    /// Creates a complex number with only real part (imaginary = 0).
    ///
    /// @param real the real part
    public Complex(float real) {
        this(real, 0.0f);
    }

    /// Creates a complex number from polar coordinates (r, θ).
    /// Complex = r * (cos(θ) + i * sin(θ)) = r * e^(iθ)
    ///
    /// @param r the modulus (magnitude)
    /// @param theta the argument (phase angle in radians)
    /// @return Complex number in Cartesian form
    public static Complex fromPolar(float r, float theta) {
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    /// Creates a complex number from a real number.
    ///
    /// @param real the real value
    /// @return Complex with imaginary part = 0
    public static Complex fromReal(float real) {
        return new Complex(real, 0.0f);
    }

    /// Creates a purely imaginary complex number.
    ///
    /// @param imag the imaginary value
    /// @return Complex with real part = 0
    public static Complex fromImaginary(float imag) {
        return new Complex(0.0f, imag);
    }

    // ==================== Arithmetic Operations ====================

    /// Returns the sum of this complex and another.
    /// (a + bi) + (c + di) = (a+c) + (b+d)i
    ///
    /// @param other the complex number to add
    /// @return new Complex representing the sum
    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im);
    }

    /// Returns the sum of this complex and a real number.
    ///
    /// @param real the real number to add
    /// @return new Complex with real part incremented
    public Complex add(float real) {
        return new Complex(this.re + real, this.im);
    }

    /// Returns the difference of this complex and another.
    /// (a + bi) - (c + di) = (a-c) + (b-d)i
    ///
    /// @param other the complex number to subtract
    /// @return new Complex representing the difference
    public Complex subtract(Complex other) {
        return new Complex(this.re - other.re, this.im - other.im);
    }

    /// Returns the difference of this complex and a real number.
    ///
    /// @param real the real number to subtract
    /// @return new Complex with real part decremented
    public Complex subtract(float real) {
        return new Complex(this.re - real, this.im);
    }

    /// Returns the product of this complex and another.
    /// (a + bi)(c + di) = (ac - bd) + (ad + bc)i
    ///
    /// @param other the complex number to multiply
    /// @return new Complex representing the product
    public Complex multiply(Complex other) {
        return new Complex(
            this.re * other.re - this.im * other.im,
            this.re * other.im + this.im * other.re
        );
    }

    /// Returns the product of this complex and a real scalar.
    /// s(a + bi) = sa + sbi
    ///
    /// @param scalar the real scalar multiplier
    /// @return new Complex scaled by the scalar
    public Complex multiply(float scalar) {
        return new Complex(this.re * scalar, this.im * scalar);
    }

    /// Returns the quotient of this complex divided by another.
    /// (a + bi) / (c + di) = ((ac + bd) + (bc - ad)i) / (c² + d²)
    ///
    /// @param other the complex number divisor
    /// @return new Complex representing the quotient
    /// @throws ArithmeticException if divisor is zero
    public Complex divide(Complex other) {
        float denominator = other.re * other.re + other.im * other.im;
        if (denominator == 0.0f) {
            throw new ArithmeticException("Division by zero complex number");
        }
        return new Complex(
            (this.re * other.re + this.im * other.im) / denominator,
            (this.im * other.re - this.re * other.im) / denominator
        );
    }

    /// Returns the quotient of this complex divided by a real scalar.
    ///
    /// @param scalar the real scalar divisor
    /// @return new Complex divided by the scalar
    /// @throws ArithmeticException if scalar is zero
    public Complex divide(float scalar) {
        if (scalar == 0.0f) {
            throw new ArithmeticException("Division by zero");
        }
        return new Complex(this.re / scalar, this.im / scalar);
    }

    /// Returns the negation of this complex.
    /// -(a + bi) = -a - bi
    ///
    /// @return new Complex with both parts negated
    public Complex negate() {
        return new Complex(-this.re, -this.im);
    }

    /// Returns the complex conjugate.
    /// conj(a + bi) = a - bi
    ///
    /// @return new Complex with imaginary part negated
    public Complex conjugate() {
        return new Complex(this.re, -this.im);
    }

    /// Returns the reciprocal (multiplicative inverse).
    /// 1 / (a + bi) = (a - bi) / (a² + b²)
    ///
    /// @return new Complex representing the reciprocal
    /// @throws ArithmeticException if this complex number is zero
    public Complex reciprocal() {
        float denominator = re * re + im * im;
        if (denominator == 0.0f) {
            throw new ArithmeticException("Reciprocal of zero");
        }
        return new Complex(re / denominator, -im / denominator);
    }

    // ==================== Power and Root Operations ====================

    /// Returns the square of this complex number.
    /// (a + bi)² = (a² - b²) + 2abi
    ///
    /// @return new Complex representing the square
    public Complex square() {
        return new Complex(
            re * re - im * im,
            2.0f * re * im
        );
    }

    /// Returns the square root of this complex number.
    /// √(r*e^(iθ)) = √r * e^(iθ/2)
    ///
    /// @return the principal square root
    public Complex sqrt() {
        if (re == 0.0f && im == 0.0f) {
            return ZERO;
        }
        float r = abs();
        float sqrtR = FastMath.sqrt(r);
        float halfPhase = phase() / 2.0f;
        return fromPolar(sqrtR, halfPhase);
    }

    /// Returns this complex number raised to a real power.
    /// (r*e^(iθ))^n = r^n * e^(i*n*θ)
    ///
    /// @param n the real exponent
    /// @return this complex raised to the power n
    public Complex pow(double n) {
        if (re == 0.0f && im == 0.0f) {
            return n > 0 ? ZERO : throwDivZero();
        }
        float r = abs();
        float theta = phase();
        return fromPolar((float) Math.pow(r, n), (float) (n * theta));
    }

    private static Complex throwDivZero() {
        throw new ArithmeticException("Zero to negative power");
    }

    /// Returns this complex number raised to an integer power.
    /// Optimized for integer exponents using fast exponentiation.
    ///
    /// @param n the integer exponent
    /// @return this complex raised to the power n
    public Complex pow(int n) {
        if (n == 0) return ONE;
        if (n == 1) return this;
        if (n < 0) return pow(-n).reciprocal();

        // Fast exponentiation
        Complex result = ONE;
        Complex base = this;
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
    /// a^b = e^(b * ln(a))
    ///
    /// @param exponent the complex exponent
    /// @return this complex raised to the complex exponent
    public Complex pow(Complex exponent) {
        if (re == 0.0f && im == 0.0f) {
            return ZERO;
        }
        return log().multiply(exponent).exp();
    }

    /// Returns the natural logarithm of this complex number.
    /// ln(r*e^(iθ)) = ln(r) + iθ
    ///
    /// @return the natural logarithm as a complex number
    public Complex log() {
        return new Complex(FastMath.log(abs()), phase());
    }

    /// Returns e raised to this complex power.
    /// e^(a+bi) = e^a * (cos(b) + i*sin(b))
    ///
    /// @return the complex exponential
    public Complex exp() {
        float expReal = FastMath.exp(re);
        return new Complex(
            expReal * FastMath.cos(im),
            expReal * FastMath.sin(im)
        );
    }

    // ==================== Trigonometric Functions ====================

    /// Returns the sine of this complex number.
    /// sin(a + bi) = sin(a)cosh(b) + i*cos(a)sinh(b)
    ///
    /// @return the complex sine
    public Complex sin() {
        return new Complex(
            FastMath.sin(re) * cosh(im),
            FastMath.cos(re) * sinh(im)
        );
    }

    /// Returns the cosine of this complex number.
    /// cos(a + bi) = cos(a)cosh(b) - i*sin(a)sinh(b)
    ///
    /// @return the complex cosine
    public Complex cos() {
        return new Complex(
            FastMath.cos(re) * cosh(im),
            -FastMath.sin(re) * sinh(im)
        );
    }

    /// Returns the tangent of this complex number.
    /// tan(z) = sin(z) / cos(z)
    ///
    /// @return the complex tangent
    public Complex tan() {
        return sin().divide(cos());
    }

    /// Returns the hyperbolic sine.
    /// sinh(a + bi) = sinh(a)cos(b) + i*cosh(a)sin(b)
    ///
    /// @return the complex hyperbolic sine
    public Complex sinh() {
        return new Complex(
            sinh(re) * FastMath.cos(im),
            cosh(re) * FastMath.sin(im)
        );
    }

    /// Returns the hyperbolic cosine.
    /// cosh(a + bi) = cosh(a)cos(b) + i*sinh(a)sin(b)
    ///
    /// @return the complex hyperbolic cosine
    public Complex cosh() {
        return new Complex(
            cosh(re) * FastMath.cos(im),
            sinh(re) * FastMath.sin(im)
        );
    }

    /// Returns the hyperbolic tangent.
    /// tanh(z) = sinh(z) / cosh(z)
    ///
    /// @return the complex hyperbolic tangent
    public Complex tanh() {
        return sinh().divide(cosh());
    }

    /// Returns the inverse sine (arcsin) of this complex number.
    /// asin(z) = -i * ln(iz + sqrt(1 - z²))
    ///
    /// @return the complex arcsin
    public Complex asin() {
        // asin(z) = -i * ln(iz + sqrt(1 - z²))
        Complex iz = new Complex(-im, re);
        return iz.add(ONE.subtract(this.square()).sqrt()).log().multiply(MINUS_I);
    }

    /// Returns the inverse cosine (arccos) of this complex number.
    /// acos(z) = π/2 - asin(z)
    ///
    /// @return the complex arccos
    public Complex acos() {
        // acos(z) = π/2 - asin(z)
        return asin().negate().add((float) (Math.PI / 2));
    }

    /// Returns the inverse tangent (arctan) of this complex number.
    /// atan(z) = (i/2) * ln((i+z)/(i-z))
    ///
    /// @return the complex arctan
    public Complex atan() {
        // atan(z) = (i/2) * ln((i+z)/(i-z))
        Complex numerator = I.add(this);
        Complex denominator = I.subtract(this);
        return numerator.divide(denominator).log().multiply(new Complex(0, 0.5f));
    }

    // ==================== Properties ====================

    /// Returns the absolute value (modulus/magnitude).
    /// |a + bi| = √(a² + b²)
    ///
    /// @return the magnitude of this complex number
    public float abs() {
        return FastMath.sqrt(re * re + im * im);
    }

    /// Returns the squared absolute value (avoids sqrt).
    /// |a + bi|² = a² + b²
    ///
    /// @return the squared magnitude
    public float absSquared() {
        return re * re + im * im;
    }

    /// Returns the argument (phase angle) in radians.
    /// θ = atan2(b, a)
    ///
    /// @return the phase angle in radians
    public float phase() {
        return FastMath.atan2(im, re);
    }

    /// Returns the real part.
    ///
    /// @return the real component
    public float real() {
        return re;
    }

    /// Returns the imaginary part.
    ///
    /// @return the imaginary component
    public float imag() {
        return im;
    }

    /// Returns true if this is a real number (imaginary part is zero).
    ///
    /// @return true if imaginary part equals zero
    public boolean isReal() {
        return im == 0.0f;
    }

    /// Returns true if this is a purely imaginary number (real part is zero).
    ///
    /// @return true if real part equals zero and imaginary part is non-zero
    public boolean isImaginary() {
        return re == 0.0f && im != 0.0f;
    }

    /// Returns true if this is zero.
    ///
    /// @return true if both parts equal zero
    public boolean isZero() {
        return re == 0.0f && im == 0.0f;
    }

    // ==================== Utility Methods ====================

    /// Returns an array [real, imaginary].
    ///
    /// @return a two-element float array
    public float[] toArray() {
        return new float[] { re, im };
    }

    /// Returns as double-precision complex.
    ///
    /// @return a ComplexD with double-precision components
    public ComplexD toComplexD() {
        return new ComplexD(re, im);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complex c)) return false;
        return Float.compare(re, c.re) == 0 && Float.compare(im, c.im) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Float.floatToIntBits(re) + Float.floatToIntBits(im);
    }

    @Override
    public String toString() {
        if (im == 0.0f) return String.format("%.4f", re);
        if (re == 0.0f) return String.format("%.4fi", im);
        if (im < 0) return String.format("%.4f - %.4fi", re, -im);
        return String.format("%.4f + %.4fi", re, im);
    }

    // ==================== Helper Methods ====================

    private static float sinh(float x) {
        return (float) Math.sinh(x);
    }

    private static float cosh(float x) {
        return (float) Math.cosh(x);
    }
}