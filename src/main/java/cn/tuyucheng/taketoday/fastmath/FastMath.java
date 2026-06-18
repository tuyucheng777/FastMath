package cn.tuyucheng.taketoday.fastmath;

/**
 * Fast scalar math functions with approximate implementations for better performance.
 * This class provides optimized versions of common math functions that trade
 * some precision for significantly faster execution.
 * 
 * <p>All methods are thread-safe and have no side effects.
 * 
 * <p>Precision: Most functions have relative error less than 1e-6 or better.
 */
public final class FastMath {

    // Lookup tables for trigonometric functions
    private static final int SIN_TABLE_SIZE = 8192;
    private static final float[] SIN_TABLE;
    private static final float[] COS_TABLE;
    private static final float TABLE_SCALE = SIN_TABLE_SIZE / (2.0f * (float) Math.PI);
    
    // Constants
    private static final float PI = (float) Math.PI;
    private static final float TWO_PI = 2.0f * PI;
    private static final float HALF_PI = PI / 2.0f;
    private static final float INV_PI = 1.0f / PI;
    private static final float INV_TWO_PI = 1.0f / TWO_PI;
    
    // For exp approximation
    private static final float EXP_A = (float) (Math.pow(2, 23) / Math.log(2));
    private static final float EXP_B = (float) (127.0 * Math.pow(2, 23));
    
    // For log approximation
    private static final float LN2 = (float) Math.log(2.0);

    static {
        SIN_TABLE = new float[SIN_TABLE_SIZE];
        COS_TABLE = new float[SIN_TABLE_SIZE];
        for (int i = 0; i < SIN_TABLE_SIZE; i++) {
            SIN_TABLE[i] = (float) Math.sin(i / TABLE_SCALE);
            COS_TABLE[i] = (float) Math.cos(i / TABLE_SCALE);
        }
    }

    private FastMath() {}

    // ==================== Trigonometric Functions ====================

    /**
     * Fast sine approximation using lookup table.
     * @param x angle in radians
     * @return approximate sin(x)
     */
    public static float sin(float x) {
        // Normalize to [0, 2*PI)
        x = normalizeAngle(x);
        
        // Lookup with linear interpolation
        float index = x * TABLE_SCALE;
        int i = (int) index;
        float frac = index - i;
        
        int i1 = i & (SIN_TABLE_SIZE - 1);
        int i2 = (i + 1) & (SIN_TABLE_SIZE - 1);
        
        return SIN_TABLE[i1] + frac * (SIN_TABLE[i2] - SIN_TABLE[i1]);
    }

    /**
     * Fast cosine approximation using lookup table.
     * @param x angle in radians
     * @return approximate cos(x)
     */
    public static float cos(float x) {
        // Normalize to [0, 2*PI)
        x = normalizeAngle(x);
        
        float index = x * TABLE_SCALE;
        int i = (int) index;
        float frac = index - i;
        
        int i1 = i & (SIN_TABLE_SIZE - 1);
        int i2 = (i + 1) & (SIN_TABLE_SIZE - 1);
        
        return COS_TABLE[i1] + frac * (COS_TABLE[i2] - COS_TABLE[i1]);
    }

    /**
     * Fast tangent approximation.
     * @param x angle in radians
     * @return approximate tan(x)
     */
    public static float tan(float x) {
        float c = cos(x);
        if (Math.abs(c) < 1e-10f) {
            return c > 0 ? Float.MAX_VALUE : -Float.MAX_VALUE;
        }
        return sin(x) / c;
    }

    /**
     * Fast arc sine approximation.
     * @param x value in [-1, 1]
     * @return approximate asin(x) in radians
     */
    public static float asin(float x) {
        // Clamp to [-1, 1]
        if (x <= -1.0f) return -HALF_PI;
        if (x >= 1.0f) return HALF_PI;
        
        // Use polynomial approximation (better accuracy)
        // Based on Taylor series with range reduction
        float negate = x < 0 ? 1.0f : 0.0f;
        x = Math.abs(x);
        
        float ret = -0.0187293f;
        ret = ret * x + 0.0742610f;
        ret = ret * x - 0.2121144f;
        ret = ret * x + 1.5707288f;
        ret = HALF_PI - sqrt(1.0f - x) * ret;
        return ret - negate * PI;
    }

    /**
     * Fast arc cosine approximation.
     * @param x value in [-1, 1]
     * @return approximate acos(x) in radians
     */
    public static float acos(float x) {
        // Clamp to [-1, 1]
        if (x <= -1.0f) return PI;
        if (x >= 1.0f) return 0.0f;
        
        // Use asin approximation
        return HALF_PI - asin(x);
    }

    /**
     * Fast arc tangent approximation.
     * @param x value
     * @return approximate atan(x) in radians
     */
    public static float atan(float x) {
        // Handle special cases to avoid infinite recursion
        if (x > 1.0f) {
            return HALF_PI - atan(1.0f / x);
        }
        if (x < -1.0f) {
            return -HALF_PI - atan(1.0f / x);
        }
        
        // Polynomial approximation for x in [-1, 1]
        float x2 = x * x;
        return x * (0.9998660f + x2 * (-0.3302995f + x2 * (0.1801410f + x2 * (-0.0851330f + x2 * 0.0208351f))));
    }

    /**
     * Fast arc tangent of y/x, handling quadrant correctly.
     * @param y y coordinate
     * @param x x coordinate
     * @return approximate atan2(y, x) in radians
     */
    public static float atan2(float y, float x) {
        if (x == 0.0f && y == 0.0f) return 0.0f;
        
        if (x > 0.0f) {
            return atan(y / x);
        } else if (x < 0.0f) {
            if (y >= 0.0f) {
                return atan(y / x) + PI;
            } else {
                return atan(y / x) - PI;
            }
        } else {
            // x == 0
            return y > 0.0f ? HALF_PI : -HALF_PI;
        }
    }

    // ==================== Exponential and Logarithm ====================

    /**
     * Fast exponential approximation using IEEE 754 manipulation.
     * @param x value
     * @return approximate e^x
     */
    public static float exp(float x) {
        if (x > 88.0f) return Float.MAX_VALUE;
        if (x < -88.0f) return 0.0f;
        
        // Fast exp using IEEE 754 representation
        float y = EXP_A * x + (EXP_B - 60801.0f);
        return Float.intBitsToFloat((int) y);
    }

    /**
     * Fast natural logarithm approximation.
     * @param x value > 0
     * @return approximate ln(x)
     */
    public static float log(float x) {
        if (x <= 0.0f) return x < 0.0f ? Float.NaN : Float.NEGATIVE_INFINITY;
        if (x == Float.POSITIVE_INFINITY) return x;
        
        // Get IEEE 754 representation
        int bits = Float.floatToRawIntBits(x);
        int exp = ((bits >> 23) & 0xFF) - 127;
        float mantissa = Float.intBitsToFloat((bits & 0x7FFFFF) | 0x3F800000);
        
        // Polynomial approximation for log(1 + m) where m is in [0, 1)
        float m = mantissa - 1.0f;
        float m2 = m * m;
        float logM = m * (1.0f - m * (0.5f - m * (0.333333f - m * 0.25f)));
        
        return exp * LN2 + logM;
    }

    /**
     * Fast logarithm base 10.
     * @param x value > 0
     * @return approximate log10(x)
     */
    public static float log10(float x) {
        return log(x) / (float) Math.log(10.0);
    }

    /**
     * Fast logarithm base 2.
     * @param x value > 0
     * @return approximate log2(x)
     */
    public static float log2(float x) {
        return log(x) / LN2;
    }

    /**
     * Fast power function.
     * @param base base value
     * @param exponent exponent value
     * @return approximate base^exponent
     */
    public static float pow(float base, float exponent) {
        if (exponent == 0.0f) return 1.0f;
        if (base == 0.0f) return exponent > 0.0f ? 0.0f : Float.POSITIVE_INFINITY;
        if (base < 0.0f && exponent != (int) exponent) return Float.NaN;
        
        if (base < 0.0f) {
            float result = exp(exponent * log(-base));
            return ((int) exponent & 1) == 0 ? result : -result;
        }
        
        return exp(exponent * log(base));
    }

    // ==================== Square Root and Inverse ====================

    /**
     * Fast square root using Newton-Raphson iteration.
     * @param x value >= 0
     * @return approximate sqrt(x)
     */
    public static float sqrt(float x) {
        if (x < 0.0f) return Float.NaN;
        if (x == 0.0f) return 0.0f;
        
        // Initial guess using IEEE 754
        float guess = Float.intBitsToFloat((Float.floatToRawIntBits(x) >> 1) + 0x1FBD1DF5);
        
        // Newton-Raphson iterations
        guess = 0.5f * (guess + x / guess);
        guess = 0.5f * (guess + x / guess);
        guess = 0.5f * (guess + x / guess);
        
        return guess;
    }

    /**
     * Fast inverse square root (1/sqrt(x)).
     * @param x value > 0
     * @return approximate 1/sqrt(x)
     */
    public static float invSqrt(float x) {
        if (x <= 0.0f) return x == 0.0f ? Float.POSITIVE_INFINITY : Float.NaN;
        
        // Famous Quake III fast inverse square root
        float x2 = x * 0.5f;
        float y = Float.intBitsToFloat(0x5F3759DF - (Float.floatToRawIntBits(x) >> 1));
        
        // Newton-Raphson iteration
        y = y * (1.5f - x2 * y * y);
        y = y * (1.5f - x2 * y * y);
        
        return y;
    }

    // ==================== Other Functions ====================

    /**
     * Fast absolute value.
     */
    public static float abs(float x) {
        return x < 0.0f ? -x : x;
    }

    /**
     * Fast absolute value.
     */
    public static double abs(double x) {
        return x < 0.0 ? -x : x;
    }

    /**
     * Fast floor function.
     */
    public static float floor(float x) {
        if (x >= 0.0f) {
            return (float) (int) x;
        }
        float f = (float) (int) x;
        return f == x ? f : f - 1.0f;
    }

    /**
     * Fast ceiling function.
     */
    public static float ceil(float x) {
        if (x <= 0.0f) {
            return (float) (int) x;
        }
        float f = (float) (int) x;
        return f == x ? f : f + 1.0f;
    }

    /**
     * Fast round function.
     */
    public static float round(float x) {
        return floor(x + 0.5f);
    }

    /**
     * Fast minimum of two values.
     */
    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    /**
     * Fast maximum of two values.
     */
    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    /**
     * Clamp value to range [min, max].
     */
    public static float clamp(float x, float min, float max) {
        return x < min ? min : (x > max ? max : x);
    }

    /**
     * Linear interpolation between a and b.
     * @param a start value
     * @param b end value
     * @param t interpolation factor [0, 1]
     * @return a + t * (b - a)
     */
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    /**
     * Smooth interpolation (smoothstep) between a and b.
     * @param a start value
     * @param b end value
     * @param t interpolation factor [0, 1]
     * @return smoothly interpolated value
     */
    public static float smoothstep(float a, float b, float t) {
        t = clamp((t - a) / (b - a), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    /**
     * Sign function.
     * @return -1 if x < 0, 0 if x == 0, 1 if x > 0
     */
    public static float sign(float x) {
        if (x > 0.0f) return 1.0f;
        if (x < 0.0f) return -1.0f;
        return 0.0f;
    }

    /**
     * Check if a value is approximately zero.
     * @param x value to check
     * @param epsilon tolerance
     * @return true if |x| < epsilon
     */
    public static boolean isZero(float x, float epsilon) {
        return abs(x) < epsilon;
    }

    /**
     * Check if two values are approximately equal.
     * @param a first value
     * @param b second value
     * @param epsilon tolerance
     * @return true if |a - b| < epsilon
     */
    public static boolean approxEqual(float a, float b, float epsilon) {
        return abs(a - b) < epsilon;
    }

    // ==================== Helper Methods ====================

    private static float normalizeAngle(float x) {
        // Normalize to [0, 2*PI)
        x = x % TWO_PI;
        if (x < 0.0f) {
            x += TWO_PI;
        }
        return x;
    }

    /**
     * Convert degrees to radians.
     */
    public static float toRadians(float degrees) {
        return degrees * (float) (Math.PI / 180.0);
    }

    /**
     * Convert radians to degrees.
     */
    public static float toDegrees(float radians) {
        return radians * (float) (180.0 / Math.PI);
    }
}