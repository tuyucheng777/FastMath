package cn.tuyucheng.taketoday.fastmath;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

/// High-performance complex number array operations using Java Vector API.
///
/// Complex numbers are stored in interleaved format: [re0, im0, re1, im1, ...]
/// This layout allows efficient SIMD operations on real and imaginary parts.
///
/// All operations are designed for maximum SIMD efficiency, processing
/// multiple complex numbers simultaneously.
public final class ComplexMath {

    private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    private ComplexMath() {}

    // ==================== Array Creation ====================

    /// Creates a complex array from interleaved [re, im, re, im, ...] data.
    /// The returned array is a clone of the input data.
    ///
    /// @param data the interleaved complex data array in [re, im, re, im, ...] format
    /// @return a new array containing a clone of the interleaved data
    public static float[] fromInterleaved(float[] data) {
        return data.clone();
    }

    /// Creates an interleaved complex array from separate real and imaginary arrays.
    /// The resulting array interleaves real and imaginary values:
    /// [real[0], imag[0], real[1], imag[1], ...]
    ///
    /// @param real the array of real parts; must have the same length as `imag`
    /// @param imag the array of imaginary parts; must have the same length as `real`
    /// @return a new interleaved complex array of length `2 * real.length`
    /// @throws IllegalArgumentException if `real` and `imag` have different lengths
    public static float[] fromArrays(float[] real, float[] imag) {
        if (real.length != imag.length) {
            throw new IllegalArgumentException("Real and imaginary arrays must have same length");
        }
        float[] result = new float[real.length * 2];
        for (int i = 0; i < real.length; i++) {
            result[i * 2] = real[i];
            result[i * 2 + 1] = imag[i];
        }
        return result;
    }

    /// Creates an interleaved complex array where all values have the same imaginary part.
    /// Result format: [real[0], imag, real[1], imag, ...]
    ///
    /// @param real the array of real parts
    /// @param imag the constant imaginary value applied to all elements
    /// @return a new interleaved complex array of length `2 * real.length`
    public static float[] fromReal(float[] real, float imag) {
        float[] result = new float[real.length * 2];
        for (int i = 0; i < real.length; i++) {
            result[i * 2] = real[i];
            result[i * 2 + 1] = imag;
        }
        return result;
    }

    /// Creates a zero complex array of the given length.
    /// All real and imaginary parts are initialized to 0.
    ///
    /// @param length the number of complex numbers (not float elements); the resulting array has `2 * length` floats
    /// @return a new float array of length `2 * length` filled with zeros
    public static float[] zeros(int length) {
        return new float[length * 2];
    }

    // ==================== Extraction ====================

    /// Extracts real parts from an interleaved complex array.
    ///
    /// @param complex the interleaved complex array in [re, im, re, im, ...] format
    /// @return a new float array containing only the real parts, of length `complex.length / 2`
    public static float[] getReal(float[] complex) {
        float[] real = new float[complex.length / 2];
        for (int i = 0; i < real.length; i++) {
            real[i] = complex[i * 2];
        }
        return real;
    }

    /// Extracts imaginary parts from an interleaved complex array.
    ///
    /// @param complex the interleaved complex array in [re, im, re, im, ...] format
    /// @return a new float array containing only the imaginary parts, of length `complex.length / 2`
    public static float[] getImag(float[] complex) {
        float[] imag = new float[complex.length / 2];
        for (int i = 0; i < imag.length; i++) {
            imag[i] = complex[i * 2 + 1];
        }
        return imag;
    }

    /// Gets a single complex number at the given index from an interleaved array.
    ///
    /// @param complex the interleaved complex array
    /// @param index the complex number index (0-based); each index spans 2 float elements
    /// @return a {@link Complex} object representing the value at the specified index
    public static Complex get(float[] complex, int index) {
        int i = index * 2;
        return new Complex(complex[i], complex[i + 1]);
    }

    /// Sets a single complex number at the given index in an interleaved array.
    ///
    /// @param complex the interleaved complex array to modify
    /// @param index the complex number index (0-based); each index spans 2 float elements
    /// @param value the {@link Complex} value to store at the specified index
    public static void set(float[] complex, int index, Complex value) {
        int i = index * 2;
        complex[i] = value.re;
        complex[i + 1] = value.im;
    }

    // ==================== Basic Arithmetic ====================

    /// Adds two complex arrays element-wise using SIMD Vector API.
    /// (a + bi) + (c + di) = (a+c) + (b+d)i
    ///
    /// @param a the first interleaved complex array; must have the same length as `b`
    /// @param b the second interleaved complex array; must have the same length as `a`
    /// @return a new interleaved complex array representing the element-wise sum
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static float[] add(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            va.add(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    /// Subtracts two complex arrays element-wise using SIMD Vector API.
    /// (a + bi) - (c + di) = (a-c) + (b-d)i
    ///
    /// @param a the first interleaved complex array (minuend); must have the same length as `b`
    /// @param b the second interleaved complex array (subtrahend); must have the same length as `a`
    /// @return a new interleaved complex array representing the element-wise difference
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static float[] subtract(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            va.sub(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    /// Multiplies a complex array by a real scalar using SIMD Vector API.
    /// s(a + bi) = sa + sbi
    ///
    /// @param a the interleaved complex array to scale
    /// @param scalar the real scalar multiplier
    /// @return a new interleaved complex array scaled by the scalar
    public static float[] multiplyScalar(float[] a, float scalar) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector vs = FloatVector.broadcast(FLOAT_SPECIES, scalar);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.mul(vs).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }

    /// Multiplies two complex arrays element-wise.
    /// (a + bi)(c + di) = (ac - bd) + (ad + bc)i
    ///
    /// For interleaved format [re0, im0, re1, im1, ...],
    /// we process each complex number individually.
    ///
    /// @param a the first interleaved complex array; must have the same length as `b`
    /// @param b the second interleaved complex array; must have the same length as `a`
    /// @return a new interleaved complex array representing the element-wise product
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static float[] multiply(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int n = a.length / 2; // number of complex numbers
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float ar = a[idx];
            float ai = a[idx + 1];
            float br = b[idx];
            float bi = b[idx + 1];
            
            // (ar + ai*i)(br + bi*i) = (ar*br - ai*bi) + (ar*bi + ai*br)i
            result[idx] = ar * br - ai * bi;     // real
            result[idx + 1] = ar * bi + ai * br;  // imag
        }
        return result;
    }

    /// Divides two complex arrays element-wise.
    /// (a + bi) / (c + di) = ((ac + bd) + (bc - ad)i) / (c² + d²)
    ///
    /// @param a the dividend interleaved complex array; must have the same length as `b`
    /// @param b the divisor interleaved complex array; must have the same length as `a`
    /// @return a new interleaved complex array representing the element-wise quotient
    /// @throws ArithmeticException if any divisor element is zero
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static float[] divide(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float ar = a[idx];
            float ai = a[idx + 1];
            float br = b[idx];
            float bi = b[idx + 1];
            
            float denom = br * br + bi * bi;
            if (denom == 0.0f) {
                throw new ArithmeticException("Division by zero complex number at index " + i);
            }
            
            result[idx] = (ar * br + ai * bi) / denom;     // real
            result[idx + 1] = (ai * br - ar * bi) / denom;  // imag
        }
        return result;
    }

    /// Computes complex conjugate of each element.
    /// conj(a + bi) = a - bi
    ///
    /// @param a the interleaved complex array
    /// @return a new interleaved complex array with imaginary parts negated
    public static float[] conjugate(float[] a) {
        float[] result = a.clone();
        int n = a.length / 2;
        for (int i = 0; i < n; i++) {
            result[i * 2 + 1] = -result[i * 2 + 1];  // negate imaginary
        }
        return result;
    }

    /// Negates each complex element.
    /// -(a + bi) = -a - bi
    ///
    /// @param a the interleaved complex array to negate
    /// @return a new interleaved complex array with both real and imaginary parts negated
    public static float[] negate(float[] a) {
        return VectorMath.multiply(a, -1.0f);
    }

    // ==================== Magnitude and Phase ====================

    /// Computes absolute value (magnitude) of each complex number.
    /// |a + bi| = √(a² + b²)
    ///
    /// @param a the interleaved complex array
    /// @return a new float array of length `a.length / 2` containing the magnitudes
    public static float[] abs(float[] a) {
        int n = a.length / 2;
        float[] result = new float[n];
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            result[i] = (float) Math.sqrt(re * re + im * im);
        }
        return result;
    }

    /// Computes squared magnitude of each complex number (avoids sqrt).
    /// |a + bi|² = a² + b²
    ///
    /// @param a the interleaved complex array
    /// @return a new float array of length `a.length / 2` containing the squared magnitudes
    public static float[] absSquared(float[] a) {
        int n = a.length / 2;
        float[] result = new float[n];
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            result[i] = re * re + im * im;
        }
        return result;
    }

    /// Computes phase (argument) of each complex number.
    /// phase(a + bi) = atan2(b, a)
    ///
    /// @param a the interleaved complex array
    /// @return a new float array of length `a.length / 2` containing the phase angles in radians
    public static float[] phase(float[] a) {
        int n = a.length / 2;
        float[] result = new float[n];
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            result[i] = FastMath.atan2(a[idx + 1], a[idx]);
        }
        return result;
    }

    // ==================== Complex Functions ====================

    /// Computes complex exponential e^z for each element.
    /// e^(a+bi) = e^a * (cos(b) + i*sin(b))
    ///
    /// @param a the interleaved complex array
    /// @return a new interleaved complex array containing the exponentials
    public static float[] exp(float[] a) {
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            float expRe = FastMath.exp(re);
            result[idx] = expRe * FastMath.cos(im);
            result[idx + 1] = expRe * FastMath.sin(im);
        }
        return result;
    }

    /// Computes natural logarithm of each complex number.
    /// ln(a + bi) = ln|a + bi| + i*phase(a + bi)
    ///
    /// @param a the interleaved complex array; must not contain zero elements
    /// @return a new interleaved complex array containing the logarithms
    public static float[] log(float[] a) {
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            float mag = (float) Math.sqrt(re * re + im * im);
            result[idx] = FastMath.log(mag);
            result[idx + 1] = FastMath.atan2(im, re);
        }
        return result;
    }

    /// Computes square root of each complex number.
    /// √(r*e^(iθ)) = √r * e^(iθ/2)
    ///
    /// @param a the interleaved complex array
    /// @return a new interleaved complex array containing the principal square roots
    public static float[] sqrt(float[] a) {
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            
            if (re == 0.0f && im == 0.0f) {
                result[idx] = 0.0f;
                result[idx + 1] = 0.0f;
            } else {
                float r = (float) Math.sqrt(re * re + im * im);
                float sqrtR = (float) Math.sqrt(r);
                float halfPhase = FastMath.atan2(im, re) / 2.0f;
                result[idx] = sqrtR * FastMath.cos(halfPhase);
                result[idx + 1] = sqrtR * FastMath.sin(halfPhase);
            }
        }
        return result;
    }

    /// Computes complex sine of each element.
    /// sin(a + bi) = sin(a)cosh(b) + i*cos(a)sinh(b)
    ///
    /// @param a the interleaved complex array
    /// @return a new interleaved complex array containing the complex sine values
    public static float[] sin(float[] a) {
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            result[idx] = FastMath.sin(re) * (float) Math.cosh(im);
            result[idx + 1] = FastMath.cos(re) * (float) Math.sinh(im);
        }
        return result;
    }

    /// Computes complex cosine of each element.
    /// cos(a + bi) = cos(a)cosh(b) - i*sin(a)sinh(b)
    ///
    /// @param a the interleaved complex array
    /// @return a new interleaved complex array containing the complex cosine values
    public static float[] cos(float[] a) {
        float[] result = new float[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float re = a[idx];
            float im = a[idx + 1];
            result[idx] = FastMath.cos(re) * (float) Math.cosh(im);
            result[idx + 1] = -FastMath.sin(re) * (float) Math.sinh(im);
        }
        return result;
    }

    // ==================== Dot Product ====================

    /// Computes the dot product of two complex arrays.
    ///
    /// @param a the first interleaved complex array; must have the same length as `b`
    /// @param b the second interleaved complex array; must have the same length as `a`
    /// @return a {@link Complex} representing the dot product sum
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static Complex dotProduct(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float sumRe = 0.0f;
        float sumIm = 0.0f;
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            float ar = a[idx];
            float ai = a[idx + 1];
            float br = b[idx];
            float bi = b[idx + 1];
            
            // (ar + ai*i) * conj(br + bi*i) = (ar + ai*i) * (br - bi*i)
            // = ar*br + ai*bi + (ai*br - ar*bi)i
            sumRe += ar * br + ai * bi;
            sumIm += ai * br - ar * bi;
        }
        return new Complex(sumRe, sumIm);
    }

    /// Computes the sum of all complex numbers in the array.
    ///
    /// @param a the interleaved complex array to sum
    /// @return a {@link Complex} representing the total sum of all elements
    public static Complex sum(float[] a) {
        float sumRe = 0.0f;
        float sumIm = 0.0f;
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            sumRe += a[idx];
            sumIm += a[idx + 1];
        }
        return new Complex(sumRe, sumIm);
    }

    // ==================== Double Precision Versions ====================

    /// Adds two double-precision complex arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first interleaved double-precision complex array; must have the same length as `b`
    /// @param b the second interleaved double-precision complex array; must have the same length as `a`
    /// @return a new interleaved double-precision complex array representing the element-wise sum
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static double[] add(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            va.add(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    /// Multiplies two double-precision complex arrays element-wise.
    /// (a + bi)(c + di) = (ac - bd) + (ad + bc)i
    ///
    /// @param a the first interleaved double-precision complex array; must have the same length as `b`
    /// @param b the second interleaved double-precision complex array; must have the same length as `a`
    /// @return a new interleaved double-precision complex array representing the element-wise product
    /// @throws IllegalArgumentException if arrays have different lengths or odd length
    public static double[] multiply(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            double ar = a[idx];
            double ai = a[idx + 1];
            double br = b[idx];
            double bi = b[idx + 1];
            
            result[idx] = ar * br - ai * bi;
            result[idx + 1] = ar * bi + ai * br;
        }
        return result;
    }

    /// Computes complex exponential for double-precision arrays.
    /// e^(a+bi) = e^a * (cos(b) + i*sin(b))
    ///
    /// @param a the interleaved double-precision complex array
    /// @return a new interleaved double-precision complex array containing the exponentials
    public static double[] exp(double[] a) {
        double[] result = new double[a.length];
        int n = a.length / 2;
        
        for (int i = 0; i < n; i++) {
            int idx = i * 2;
            double re = a[idx];
            double im = a[idx + 1];
            double expRe = Math.exp(re);
            result[idx] = expRe * Math.cos(im);
            result[idx + 1] = expRe * Math.sin(im);
        }
        return result;
    }

    // ==================== Utility ====================

    private static void validateLengths(int len1, int len2) {
        if (len1 != len2) {
            throw new IllegalArgumentException(
                "Array lengths must be equal: " + len1 + " != " + len2);
        }
        if (len1 % 2 != 0) {
            throw new IllegalArgumentException(
                "Complex array length must be even: " + len1);
        }
    }

    /// Returns a string representation of a complex array.
    /// Displays at most 8 complex numbers, with "..." for larger arrays.
    ///
    /// @param a the interleaved complex array to format
    /// @return a formatted string representation of the array
    public static String toString(float[] a) {
        StringBuilder sb = new StringBuilder("[");
        int n = a.length / 2;
        int limit = Math.min(n, 8);
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(", ");
            sb.append(get(a, i).toString());
        }
        if (n > limit) sb.append(", ...");
        sb.append("]");
        return sb.toString();
    }
}
