package cn.tuyucheng.taketoday.fastmath;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Complex numbers and FFT.
 */
class ComplexTest {

    private static final float EPSILON = 1e-5f;
    private static final double EPSILON_D = 1e-10;

    // ==================== Complex Tests ====================

    @Test
    void testComplexCreation() {
        Complex c = new Complex(3.0f, 4.0f);
        assertEquals(3.0f, c.re, EPSILON);
        assertEquals(4.0f, c.im, EPSILON);
    }

    @Test
    void testComplexFromPolar() {
        Complex c = Complex.fromPolar(5.0f, 0.0f);
        assertEquals(5.0f, c.re, EPSILON);
        assertEquals(0.0f, c.im, EPSILON);
        
        Complex c2 = Complex.fromPolar(1.0f, (float) Math.PI / 2);
        assertEquals(0.0f, c2.re, 1e-4f);
        assertEquals(1.0f, c2.im, EPSILON);
    }

    @Test
    void testComplexAdd() {
        Complex a = new Complex(3, 4);
        Complex b = new Complex(1, 2);
        Complex result = a.add(b);
        assertEquals(4.0f, result.re, EPSILON);
        assertEquals(6.0f, result.im, EPSILON);
    }

    @Test
    void testComplexSubtract() {
        Complex a = new Complex(3, 4);
        Complex b = new Complex(1, 2);
        Complex result = a.subtract(b);
        assertEquals(2.0f, result.re, EPSILON);
        assertEquals(2.0f, result.im, EPSILON);
    }

    @Test
    void testComplexMultiply() {
        // (3 + 4i)(1 + 2i) = 3 + 6i + 4i + 8i² = 3 + 10i - 8 = -5 + 10i
        Complex a = new Complex(3, 4);
        Complex b = new Complex(1, 2);
        Complex result = a.multiply(b);
        assertEquals(-5.0f, result.re, EPSILON);
        assertEquals(10.0f, result.im, EPSILON);
    }

    @Test
    void testComplexMultiplyScalar() {
        Complex a = new Complex(3, 4);
        Complex result = a.multiply(2.0f);
        assertEquals(6.0f, result.re, EPSILON);
        assertEquals(8.0f, result.im, EPSILON);
    }

    @Test
    void testComplexDivide() {
        // (3 + 4i) / (1 + i) = ((3 + 4i)(1 - i)) / ((1 + i)(1 - i))
        // = (3 - 3i + 4i + 4) / 2 = (7 + i) / 2 = 3.5 + 0.5i
        Complex a = new Complex(3, 4);
        Complex b = new Complex(1, 1);
        Complex result = a.divide(b);
        assertEquals(3.5f, result.re, EPSILON);
        assertEquals(0.5f, result.im, EPSILON);
    }

    @Test
    void testComplexConjugate() {
        Complex a = new Complex(3, 4);
        Complex conj = a.conjugate();
        assertEquals(3.0f, conj.re, EPSILON);
        assertEquals(-4.0f, conj.im, EPSILON);
    }

    @Test
    void testComplexAbs() {
        Complex a = new Complex(3, 4);
        assertEquals(5.0f, a.abs(), EPSILON);
    }

    @Test
    void testComplexPhase() {
        Complex a = new Complex(1, 1);
        assertEquals(Math.PI / 4, a.phase(), 1e-4);
        
        Complex b = new Complex(0, 1);
        assertEquals(Math.PI / 2, b.phase(), 1e-4);
    }

    @Test
    void testComplexSquare() {
        // (3 + 4i)² = 9 + 24i - 16 = -7 + 24i
        Complex a = new Complex(3, 4);
        Complex result = a.square();
        assertEquals(-7.0f, result.re, EPSILON);
        assertEquals(24.0f, result.im, EPSILON);
    }

    @Test
    void testComplexSqrt() {
        // sqrt(3 + 4i) should give something whose square is 3 + 4i
        Complex a = new Complex(3, 4);
        Complex sqrt = a.sqrt();
        Complex squared = sqrt.square();
        assertEquals(a.re, squared.re, 1e-4);
        assertEquals(a.im, squared.im, 1e-4);
    }

    @Test
    void testComplexPow() {
        // (1 + i)^2 = 2i
        Complex a = new Complex(1, 1);
        Complex result = a.pow(2);
        assertEquals(0.0f, result.re, 1e-4);
        assertEquals(2.0f, result.im, EPSILON);
    }

    @Test
    void testComplexExp() {
        // e^(iπ) = -1
        Complex c = new Complex(0, (float) Math.PI);
        Complex result = c.exp();
        assertEquals(-1.0f, result.re, 1e-2);  // FastMath.exp has ~2% error
        assertEquals(0.0f, result.im, 1e-2);
    }

    @Test
    void testComplexLog() {
        Complex a = new Complex(1, 0);
        Complex result = a.log();
        assertEquals(0.0f, result.re, EPSILON);
        assertEquals(0.0f, result.im, EPSILON);
    }

    @Test
    void testComplexSin() {
        // sin(i) = i * sinh(1)
        Complex a = new Complex(0, 1);
        Complex result = a.sin();
        assertEquals(0.0f, result.re, 1e-4);
        assertEquals(Math.sinh(1), result.im, 1e-4);
    }

    @Test
    void testComplexCos() {
        // cos(i) = cosh(1)
        Complex a = new Complex(0, 1);
        Complex result = a.cos();
        assertEquals(Math.cosh(1), result.re, 1e-4);
        assertEquals(0.0f, result.im, 1e-4);
    }

    @Test
    void testComplexConstants() {
        assertEquals(0.0f, Complex.ZERO.re, EPSILON);
        assertEquals(0.0f, Complex.ZERO.im, EPSILON);
        assertEquals(1.0f, Complex.ONE.re, EPSILON);
        assertEquals(0.0f, Complex.ONE.im, EPSILON);
        assertEquals(0.0f, Complex.I.re, EPSILON);
        assertEquals(1.0f, Complex.I.im, EPSILON);
    }

    // ==================== ComplexD Tests ====================

    @Test
    void testComplexDAdd() {
        ComplexD a = new ComplexD(3, 4);
        ComplexD b = new ComplexD(1, 2);
        ComplexD result = a.add(b);
        assertEquals(4.0, result.re, EPSILON_D);
        assertEquals(6.0, result.im, EPSILON_D);
    }

    @Test
    void testComplexDMultiply() {
        ComplexD a = new ComplexD(3, 4);
        ComplexD b = new ComplexD(1, 2);
        ComplexD result = a.multiply(b);
        assertEquals(-5.0, result.re, EPSILON_D);
        assertEquals(10.0, result.im, EPSILON_D);
    }

    @Test
    void testComplexDAbs() {
        ComplexD a = new ComplexD(3, 4);
        assertEquals(5.0, a.abs(), EPSILON_D);
    }

    // ==================== ComplexMath Tests ====================

    @Test
    void testComplexMathAdd() {
        float[] a = {1, 2, 3, 4};  // [1+2i, 3+4i]
        float[] b = {1, 1, 1, 1};  // [1+i, 1+i]
        float[] result = ComplexMath.add(a, b);
        assertArrayEquals(new float[]{2, 3, 4, 5}, result, EPSILON);
    }

    @Test
    void testComplexMathMultiplyScalar() {
        float[] a = {1, 2, 3, 4};
        float[] result = ComplexMath.multiplyScalar(a, 2.0f);
        assertArrayEquals(new float[]{2, 4, 6, 8}, result, EPSILON);
    }

    @Test
    void testComplexMathMultiply() {
        // (1+2i)(1+i) = -1 + 3i
        // (3+4i)(1+i) = -1 + 7i
        float[] a = {1, 2, 3, 4};
        float[] b = {1, 1, 1, 1};
        float[] result = ComplexMath.multiply(a, b);
        assertArrayEquals(new float[]{-1, 3, -1, 7}, result, EPSILON);
    }

    @Test
    void testComplexMathDivide() {
        // (1+2i)/(1+i) = (3+i)/2
        float[] a = {1, 2};
        float[] b = {1, 1};
        float[] result = ComplexMath.divide(a, b);
        assertEquals(1.5f, result[0], EPSILON);
        assertEquals(0.5f, result[1], EPSILON);
    }

    @Test
    void testComplexMathConjugate() {
        float[] a = {1, 2, 3, 4};
        float[] result = ComplexMath.conjugate(a);
        assertArrayEquals(new float[]{1, -2, 3, -4}, result, EPSILON);
    }

    @Test
    void testComplexMathAbs() {
        float[] a = {3, 4, 0, 1};  // [|3+4i|, |i|]
        float[] result = ComplexMath.abs(a);
        assertEquals(2, result.length);
        assertEquals(5.0f, result[0], EPSILON);
        assertEquals(1.0f, result[1], EPSILON);
    }

    @Test
    void testComplexMathExp() {
        // e^(iπ) = -1
        float[] a = {0, (float) Math.PI};
        float[] result = ComplexMath.exp(a);
        assertEquals(-1.0f, result[0], 1e-2);  // FastMath.exp has ~2% error
        assertEquals(0.0f, result[1], 1e-2);
    }

    @Test
    void testComplexMathDotProduct() {
        // (1+2i) * conj(1+i) = (1+2i)(1-i) = 1 + i + 2 = 3 + i
        float[] a = {1, 2};
        float[] b = {1, 1};
        Complex result = ComplexMath.dotProduct(a, b);
        assertEquals(3.0f, result.re, EPSILON);
        assertEquals(1.0f, result.im, EPSILON);
    }

    @Test
    void testComplexMathFromArrays() {
        float[] real = {1, 3};
        float[] imag = {2, 4};
        float[] result = ComplexMath.fromArrays(real, imag);
        assertArrayEquals(new float[]{1, 2, 3, 4}, result, EPSILON);
    }

    // ==================== FFT Tests ====================

    @Test
    void testFFTIsPowerOfTwo() {
        assertTrue(FFT.isPowerOfTwo(1));
        assertTrue(FFT.isPowerOfTwo(2));
        assertTrue(FFT.isPowerOfTwo(4));
        assertTrue(FFT.isPowerOfTwo(1024));
        assertFalse(FFT.isPowerOfTwo(3));
        assertFalse(FFT.isPowerOfTwo(6));
        assertFalse(FFT.isPowerOfTwo(0));
        assertFalse(FFT.isPowerOfTwo(-1));
    }

    @Test
    void testFFTNextPowerOfTwo() {
        assertEquals(1, FFT.nextPowerOfTwo(1));
        assertEquals(2, FFT.nextPowerOfTwo(2));
        assertEquals(4, FFT.nextPowerOfTwo(3));
        assertEquals(4, FFT.nextPowerOfTwo(4));
        assertEquals(8, FFT.nextPowerOfTwo(5));
        assertEquals(1024, FFT.nextPowerOfTwo(1000));
    }

    @Test
    void testFFTLog2() {
        assertEquals(0, FFT.log2(1));
        assertEquals(1, FFT.log2(2));
        assertEquals(2, FFT.log2(4));
        assertEquals(10, FFT.log2(1024));
    }

    @Test
    void testFFTOfZero() {
        float[] data = {0, 0, 0, 0, 0, 0, 0, 0};
        float[] result = FFT.fftCopy(data);
        for (int i = 0; i < data.length; i++) {
            assertEquals(0.0f, result[i], EPSILON);
        }
    }

    @Test
    void testFFTOfConstant() {
        // FFT of constant is DC component only
        float[] data = {2, 0, 2, 0, 2, 0, 2, 0}; // [2+0i, 2+0i, 2+0i, 2+0i]
        float[] result = FFT.fftCopy(data);
        assertEquals(8.0f, result[0], EPSILON);  // DC = sum = 8
        assertEquals(0.0f, result[1], EPSILON);
        // All other components should be 0
        for (int i = 2; i < result.length; i++) {
            assertEquals(0.0f, result[i], 1e-4);
        }
    }

    @Test
    void testFFTOfImpulse() {
        // FFT of [1, 0, 0, 0] should be [1, 0, 0, 0] (constant)
        float[] data = {1, 0, 0, 0, 0, 0, 0, 0}; // [1+0i, 0+0i, 0+0i, 0+0i]
        float[] result = FFT.fftCopy(data);
        for (int i = 0; i < result.length; i += 2) {
            assertEquals(1.0f, result[i], 1e-4);
            assertEquals(0.0f, result[i + 1], 1e-4);
        }
    }

    @Test
    void testFFTRoundTrip() {
        // FFT followed by IFFT should recover original signal
        float[] original = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] copy = original.clone();
        
        FFT.fft(copy);
        FFT.ifft(copy);
        
        for (int i = 0; i < original.length; i++) {
            assertEquals(original[i], copy[i], 1e-4, "Mismatch at index " + i);
        }
    }

    @Test
    void testFFTDoubleRoundTrip() {
        double[] original = {1, 2, 3, 4, 5, 6, 7, 8};
        double[] copy = original.clone();
        
        FFT.fft(copy);
        FFT.ifft(copy);
        
        for (int i = 0; i < original.length; i++) {
            assertEquals(original[i], copy[i], 1e-10, "Mismatch at index " + i);
        }
    }

    @Test
    void testFFTSineWave() {
        // Create a sine wave at frequency 1 for 8-point FFT
        int n = 8;
        float[] data = new float[n * 2];
        for (int i = 0; i < n; i++) {
            data[i * 2] = (float) Math.sin(2 * Math.PI * i / n);  // real
            data[i * 2 + 1] = 0;  // imag
        }
        
        float[] spectrum = FFT.fftCopy(data);
        
        // For frequency 1 sine, we expect energy at bins 1 and 7 (n-1)
        // Due to symmetry: X[k] = conj(X[n-k])
        float mag1 = (float) Math.sqrt(spectrum[2] * spectrum[2] + spectrum[3] * spectrum[3]);
        float mag7 = (float) Math.sqrt(spectrum[14] * spectrum[14] + spectrum[15] * spectrum[15]);
        
        assertEquals(mag1, mag7, 1e-4);  // symmetric
        assertTrue(mag1 > 1.0f);  // should have significant energy
    }

    @Test
    void testRFFTRoundTrip() {
        float[] original = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] spectrum = FFT.rfft(original);
        float[] reconstructed = FFT.irfft(spectrum, original.length);
        
        for (int i = 0; i < original.length; i++) {
            assertEquals(original[i], reconstructed[i], 1e-4, "Mismatch at index " + i);
        }
    }

    @Test
    void testConvolution() {
        // Convolve [1, 2, 3] with [0, 1, 0]
        // Result should be [0, 1, 2, 3, 0]
        float[] a = {1, 0, 2, 0, 3, 0, 0, 0};  // [1, 2, 3, 0] (padded)
        float[] b = {0, 0, 1, 0, 0, 0, 0, 0};  // [0, 1, 0, 0] (padded)
        
        float[] result = FFT.convolve(
            new float[]{1, 0, 2, 0, 3, 0},  // [1+0i, 2+0i, 3+0i]
            new float[]{0, 0, 1, 0}          // [0+0i, 1+0i]
        );
        
        // Result length should be 3 + 2 - 1 = 4 complex numbers
        assertEquals(8, result.length);
    }

    @Test
    void testFFTInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> FFT.fft(new float[6]));
        assertThrows(IllegalArgumentException.class, () -> FFT.fft(new float[3]));
    }

    @Test
    void testComplexMathInvalidLengths() {
        assertThrows(IllegalArgumentException.class, 
            () -> ComplexMath.add(new float[4], new float[6]));
        assertThrows(IllegalArgumentException.class, 
            () -> ComplexMath.add(new float[3], new float[3]));  // odd length
    }

    @Test
    void testComplexDivideByZero() {
        float[] a = {1, 2};
        float[] b = {0, 0};
        assertThrows(ArithmeticException.class, () -> ComplexMath.divide(a, b));
    }
}