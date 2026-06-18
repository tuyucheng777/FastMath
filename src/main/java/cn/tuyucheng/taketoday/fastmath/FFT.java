package cn.tuyucheng.taketoday.fastmath;

/**
 * Fast Fourier Transform (FFT) implementation using Cooley-Tukey algorithm.
 * 
 * <p>This class provides high-performance FFT and IFFT operations for complex arrays.
 * Uses iterative in-place implementation for maximum efficiency.
 * 
 * <p>FFT size must be a power of 2. Use {@link #nextPowerOfTwo(int)} to find
 * the next valid size.
 * 
 * <h2>Algorithm</h2>
 * <p>Implements the Cooley-Tukey radix-2 decimation-in-time algorithm.
 * Time complexity: O(n log n)
 * 
 * <h2>Example usage:</h2>
 * <pre>{@code
 * // Create a signal
 * float[] signal = new float[1024]; // interleaved [re, im, re, im, ...]
 * // ... fill signal ...
 * 
 * // Forward FFT
 * float[] spectrum = FFT.fft(signal);
 * 
 * // Inverse FFT
 * float[] reconstructed = FFT.ifft(spectrum);
 * }</pre>
 * 
 * @see ComplexMath for complex array operations
 */
public final class FFT {

    private FFT() {}

    // Precomputed twiddle factors cache
    private static final int MAX_CACHE_SIZE = 24; // Supports up to 2^24 = 16M points
    private static final float[][] TWIDDLE_CACHE = new float[MAX_CACHE_SIZE + 1][];

    static {
        // Precompute twiddle factors for common sizes
        for (int i = 0; i <= 16; i++) {
            int n = 1 << i;
            TWIDDLE_CACHE[i] = computeTwiddleFactors(n);
        }
    }

    /**
     * Computes the FFT of a complex array.
     * 
     * <p>The input array must have length that is a power of 2.
     * The array is modified in place and also returned.
     * 
     * @param data interleaved complex array [re0, im0, re1, im1, ...]
     * @return the same array with FFT result
     * @throws IllegalArgumentException if length is not a power of 2
     */
    public static float[] fft(float[] data) {
        validatePowerOfTwo(data.length);
        return fftInPlace(data, false);
    }

    /**
     * Computes the Inverse FFT of a complex array.
     * 
     * <p>The result is scaled by 1/n so that IFFT(FFT(x)) = x.
     * 
     * @param data interleaved complex array [re0, im0, re1, im1, ...]
     * @return the same array with IFFT result
     * @throws IllegalArgumentException if length is not a power of 2
     */
    public static float[] ifft(float[] data) {
        validatePowerOfTwo(data.length);
        return ifftInPlace(data);
    }

    /**
     * Computes the FFT with output in a new array.
     * 
     * @param data interleaved complex array
     * @return new array with FFT result
     */
    public static float[] fftCopy(float[] data) {
        validatePowerOfTwo(data.length);
        float[] result = data.clone();
        return fftInPlace(result, false);
    }

    /**
     * Computes the IFFT with output in a new array.
     * 
     * @param data interleaved complex array
     * @return new array with IFFT result
     */
    public static float[] ifftCopy(float[] data) {
        validatePowerOfTwo(data.length);
        float[] result = data.clone();
        return ifftInPlace(result);
    }

    /**
     * Computes real-valued FFT (RFFT).
     * 
     * <p>For real input, the FFT is conjugate symmetric:
     * X[k] = conj(X[n-k]), so only half the spectrum is needed.
     * 
     * @param data real-valued input array (length must be power of 2)
     * @return complex spectrum of length n/2 + 1 complex numbers (n+2 floats)
     */
    public static float[] rfft(float[] data) {
        int n = data.length;
        validatePowerOfTwo(n);
        
        // Convert real array to complex (imaginary = 0)
        float[] complex = new float[n * 2];
        for (int i = 0; i < n; i++) {
            complex[i * 2] = data[i];
        }
        
        // Compute FFT
        fftInPlace(complex, false);
        
        // Return only first n/2 + 1 complex values (due to symmetry)
        int outputLen = n / 2 + 1;
        float[] result = new float[outputLen * 2];
        System.arraycopy(complex, 0, result, 0, result.length);
        return result;
    }

    /**
     * Computes inverse real-valued FFT.
     * 
     * @param spectrum complex spectrum (from rfft)
     * @param outputLen expected output length
     * @return real-valued time domain signal
     */
    public static float[] irfft(float[] spectrum, int outputLen) {
        int n = outputLen;
        float[] complex = new float[n * 2];
        
        // Copy spectrum
        int halfLen = spectrum.length / 2;
        System.arraycopy(spectrum, 0, complex, 0, spectrum.length);
        
        // Fill in conjugate symmetric values
        for (int i = 1; i < halfLen; i++) {
            int srcIdx = i * 2;
            int dstIdx = (n - i) * 2;
            complex[dstIdx] = complex[srcIdx];            // real
            complex[dstIdx + 1] = -complex[srcIdx + 1];  // -imag
        }
        
        // IFFT
        ifftInPlace(complex);
        
        // Extract real part
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            result[i] = complex[i * 2];
        }
        return result;
    }

    // ==================== Core FFT Implementation ====================

    /**
     * In-place FFT implementation using iterative Cooley-Tukey algorithm.
     */
    private static float[] fftInPlace(float[] data, boolean inverse) {
        int n = data.length / 2;  // number of complex elements
        
        // Bit-reversal permutation
        bitReverse(data);
        
        // Get or compute twiddle factors
        float[] twiddle = getTwiddleFactors(n);
        
        // Cooley-Tukey iterative FFT
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        
        for (int s = 1; s <= logN; s++) {
            int m = 1 << s;  // 2^s
            int m2 = m >> 1; // m/2
            
            float wmRe = twiddle[s * 2 - 2];      // cos(2π/m)
            float wmIm = inverse ? twiddle[s * 2 - 1] : -twiddle[s * 2 - 1]; // ±sin(2π/m)
            
            for (int k = 0; k < n; k += m) {
                float wRe = 1.0f;
                float wIm = 0.0f;
                
                for (int j = 0; j < m2; j++) {
                    int idx1 = (k + j) * 2;
                    int idx2 = (k + j + m2) * 2;
                    
                    // t = w * data[k + j + m/2]
                    float tRe = wRe * data[idx2] - wIm * data[idx2 + 1];
                    float tIm = wRe * data[idx2 + 1] + wIm * data[idx2];
                    
                    // data[k + j + m/2] = data[k + j] - t
                    data[idx2] = data[idx1] - tRe;
                    data[idx2 + 1] = data[idx1 + 1] - tIm;
                    
                    // data[k + j] = data[k + j] + t
                    data[idx1] = data[idx1] + tRe;
                    data[idx1 + 1] = data[idx1 + 1] + tIm;
                    
                    // w = w * wm (complex multiply)
                    float newWRe = wRe * wmRe - wIm * wmIm;
                    float newWIm = wRe * wmIm + wIm * wmRe;
                    wRe = newWRe;
                    wIm = newWIm;
                }
            }
        }
        
        return data;
    }

    /**
     * In-place IFFT implementation.
     */
    private static float[] ifftInPlace(float[] data) {
        int n = data.length / 2;
        
        // FFT with conjugated twiddle factors
        fftInPlace(data, true);
        
        // Scale by 1/n
        float scale = 1.0f / n;
        for (int i = 0; i < data.length; i++) {
            data[i] *= scale;
        }
        
        return data;
    }

    /**
     * Bit-reversal permutation in place.
     */
    private static void bitReverse(float[] data) {
        int n = data.length / 2;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        
        for (int i = 0; i < n; i++) {
            int j = reverseBits(i, logN);
            if (j > i) {
                // Swap complex numbers at i and j
                int idx1 = i * 2;
                int idx2 = j * 2;
                float tempRe = data[idx1];
                float tempIm = data[idx1 + 1];
                data[idx1] = data[idx2];
                data[idx1 + 1] = data[idx2 + 1];
                data[idx2] = tempRe;
                data[idx2 + 1] = tempIm;
            }
        }
    }

    /**
     * Reverses the lower logN bits of x.
     */
    private static int reverseBits(int x, int logN) {
        int result = 0;
        for (int i = 0; i < logN; i++) {
            result = (result << 1) | (x & 1);
            x >>= 1;
        }
        return result;
    }

    // ==================== Twiddle Factor Management ====================

    /**
     * Gets twiddle factors from cache or computes them.
     * Returns [cos(2π/m), sin(2π/m)] for m = 2, 4, 8, ..., n.
     */
    private static float[] getTwiddleFactors(int n) {
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        if (logN < TWIDDLE_CACHE.length && TWIDDLE_CACHE[logN] != null) {
            return TWIDDLE_CACHE[logN];
        }
        return computeTwiddleFactors(n);
    }

    /**
     * Computes twiddle factors: cos(2π/m) and sin(2π/m) for m = 2, 4, 8, ..., n.
     */
    private static float[] computeTwiddleFactors(int n) {
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        float[] twiddle = new float[logN * 2];
        
        for (int i = 0; i < logN; i++) {
            int m = 2 << i;  // 2^(i+1)
            double angle = 2.0 * Math.PI / m;
            twiddle[i * 2] = (float) Math.cos(angle);
            twiddle[i * 2 + 1] = (float) Math.sin(angle);
        }
        
        return twiddle;
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if n is a power of 2.
     */
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Returns the smallest power of 2 >= n.
     */
    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        int highestBit = Integer.highestOneBit(n);
        if (highestBit == n) return n;
        return highestBit << 1;
    }

    /**
     * Returns log2(n) for power of 2 values.
     */
    public static int log2(int n) {
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException("n must be a power of 2");
        }
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    private static void validatePowerOfTwo(int n) {
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException(
                "FFT size must be a power of 2, got " + n + 
                ". Use nextPowerOfTwo(" + n/2 + ") = " + nextPowerOfTwo(n/2));
        }
    }

    // ==================== Double Precision FFT ====================

    /**
     * Double-precision FFT.
     */
    public static double[] fft(double[] data) {
        validatePowerOfTwo(data.length);
        return fftInPlaceD(data, false);
    }

    /**
     * Double-precision IFFT.
     */
    public static double[] ifft(double[] data) {
        validatePowerOfTwo(data.length);
        int n = data.length / 2;
        fftInPlaceD(data, true);
        double scale = 1.0 / n;
        for (int i = 0; i < data.length; i++) {
            data[i] *= scale;
        }
        return data;
    }

    private static double[] fftInPlaceD(double[] data, boolean inverse) {
        int n = data.length / 2;
        bitReverseD(data);
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        
        for (int s = 1; s <= logN; s++) {
            int m = 1 << s;
            int m2 = m >> 1;
            double wmRe = Math.cos(2.0 * Math.PI / m);
            double wmIm = inverse ? Math.sin(2.0 * Math.PI / m) : -Math.sin(2.0 * Math.PI / m);
            
            for (int k = 0; k < n; k += m) {
                double wRe = 1.0;
                double wIm = 0.0;
                
                for (int j = 0; j < m2; j++) {
                    int idx1 = (k + j) * 2;
                    int idx2 = (k + j + m2) * 2;
                    
                    double tRe = wRe * data[idx2] - wIm * data[idx2 + 1];
                    double tIm = wRe * data[idx2 + 1] + wIm * data[idx2];
                    
                    data[idx2] = data[idx1] - tRe;
                    data[idx2 + 1] = data[idx1 + 1] - tIm;
                    data[idx1] = data[idx1] + tRe;
                    data[idx1 + 1] = data[idx1 + 1] + tIm;
                    
                    double newWRe = wRe * wmRe - wIm * wmIm;
                    double newWIm = wRe * wmIm + wIm * wmRe;
                    wRe = newWRe;
                    wIm = newWIm;
                }
            }
        }
        
        return data;
    }

    private static void bitReverseD(double[] data) {
        int n = data.length / 2;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        
        for (int i = 0; i < n; i++) {
            int j = reverseBits(i, logN);
            if (j > i) {
                int idx1 = i * 2;
                int idx2 = j * 2;
                double tempRe = data[idx1];
                double tempIm = data[idx1 + 1];
                data[idx1] = data[idx2];
                data[idx1 + 1] = data[idx2 + 1];
                data[idx2] = tempRe;
                data[idx2 + 1] = tempIm;
            }
        }
    }

    // ==================== Convolution ====================

    /**
     * Computes convolution using FFT.
     * conv(a, b) = IFFT(FFT(a) * FFT(b))
     * 
     * @param a first signal (interleaved complex)
     * @param b second signal (interleaved complex)
     * @return convolution result
     */
    public static float[] convolve(float[] a, float[] b) {
        int lenA = a.length / 2;
        int lenB = b.length / 2;
        int resultLen = lenA + lenB - 1;
        int fftSize = nextPowerOfTwo(resultLen);
        
        // Zero-pad to FFT size
        float[] paddedA = new float[fftSize * 2];
        float[] paddedB = new float[fftSize * 2];
        System.arraycopy(a, 0, paddedA, 0, a.length);
        System.arraycopy(b, 0, paddedB, 0, b.length);
        
        // FFT both
        fftInPlace(paddedA, false);
        fftInPlace(paddedB, false);
        
        // Multiply in frequency domain
        for (int i = 0; i < fftSize; i++) {
            int idx = i * 2;
            float ar = paddedA[idx];
            float ai = paddedA[idx + 1];
            float br = paddedB[idx];
            float bi = paddedB[idx + 1];
            paddedA[idx] = ar * br - ai * bi;
            paddedA[idx + 1] = ar * bi + ai * br;
        }
        
        // IFFT
        ifftInPlace(paddedA);
        
        // Return result (trimmed to actual length)
        float[] result = new float[resultLen * 2];
        System.arraycopy(paddedA, 0, result, 0, result.length);
        return result;
    }

    /**
     * Computes correlation using FFT.
     * corr(a, b) = IFFT(FFT(a) * conj(FFT(b)))
     */
    public static float[] correlate(float[] a, float[] b) {
        int lenA = a.length / 2;
        int lenB = b.length / 2;
        int resultLen = lenA + lenB - 1;
        int fftSize = nextPowerOfTwo(resultLen);
        
        float[] paddedA = new float[fftSize * 2];
        float[] paddedB = new float[fftSize * 2];
        System.arraycopy(a, 0, paddedA, 0, a.length);
        System.arraycopy(b, 0, paddedB, 0, b.length);
        
        fftInPlace(paddedA, false);
        fftInPlace(paddedB, false);
        
        // Multiply with conjugate of b
        for (int i = 0; i < fftSize; i++) {
            int idx = i * 2;
            float ar = paddedA[idx];
            float ai = paddedA[idx + 1];
            float br = paddedB[idx];
            float bi = -paddedB[idx + 1]; // conjugate
            paddedA[idx] = ar * br - ai * bi;
            paddedA[idx + 1] = ar * bi + ai * br;
        }
        
        ifftInPlace(paddedA);
        
        float[] result = new float[resultLen * 2];
        System.arraycopy(paddedA, 0, result, 0, result.length);
        return result;
    }
}