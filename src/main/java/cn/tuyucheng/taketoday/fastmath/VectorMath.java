/// High-performance math library using Java Vector API.
///
/// This library provides SIMD-accelerated mathematical operations
/// for optimal performance on modern CPUs. It requires Java 21+ with
/// the Vector API preview feature enabled.
///
/// ## Modules
/// - {@link cn.tuyucheng.taketoday.fastmath.VectorMath} - Vector array operations (SIMD)
/// - {@link cn.tuyucheng.taketoday.fastmath.FastMath} - Fast scalar math functions
/// - {@link cn.tuyucheng.taketoday.fastmath.MatrixMath} - Matrix operations
/// - {@link cn.tuyucheng.taketoday.fastmath.VectorOps} - 2D/3D/4D vector classes
///
/// ## Usage
/// ```java
/// // Vector operations on arrays
/// float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
/// float[] b = {2, 2, 2, 2, 2, 2, 2, 2};
/// float[] sum = VectorMath.add(a, b);
/// float dot = VectorMath.dotProduct(a, b);
///
/// // Fast scalar functions
/// float sin = FastMath.sin(0.785f);  // Fast sine
/// float sqrt = FastMath.sqrt(16.0f);  // Fast sqrt
///
/// // 3D vectors
/// VectorOps.Vec3 v1 = new VectorOps.Vec3(1, 2, 3);
/// VectorOps.Vec3 v2 = new VectorOps.Vec3(4, 5, 6);
/// VectorOps.Vec3 cross = v1.cross(v2);
///
/// // Matrices
/// float[] m = MatrixMath.identity(3);
/// float[] product = MatrixMath.multiply(a, b, 2, 2, 2);
/// ```
///
/// ## Compile and Run
/// ```bash
/// javac --enable-preview --add-modules=jdk.incubator.vector *.java
/// java --enable-preview --add-modules=jdk.incubator.vector Main
/// ```
///
/// @since 1.0.0
package cn.tuyucheng.taketoday.fastmath;

import jdk.incubator.vector.*;

/// High-performance vector math operations using Java Vector API.
/// This class provides SIMD-accelerated operations on float and double arrays.
///
/// Vector API uses SIMD (Single Instruction Multiple Data) instructions
/// to process multiple elements simultaneously, providing significant
/// performance improvements for mathematical operations on arrays.
///
/// Supported operations include:
/// - Element-wise arithmetic: add, subtract, multiply, divide
/// - Mathematical functions: sin, cos, sqrt, exp, log, pow
/// - Reduction operations: sum, dot product, min, max
/// - Normalization and distance calculations
public final class VectorMath {

    private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    // Prevent instantiation
    private VectorMath() {}

    // ==================== Float Array Operations ====================

    /// Adds two float arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input float array; must have the same length as `b`
    /// @param b the second input float array; must have the same length as `a`
    /// @return a new float array where each element is the sum of corresponding elements in `a` and `b`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
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
        
        // Handle tail
        for (; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    /// Adds a scalar value to each element of a float array using SIMD Vector API.
    ///
    /// @param a the input float array
    /// @param scalar the scalar value to add to each element
    /// @return a new float array where each element is incremented by `scalar`
    public static float[] add(float[] a, float scalar) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector vs = FloatVector.broadcast(FLOAT_SPECIES, scalar);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.add(vs).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + scalar;
        }
        return result;
    }

    /// Subtracts two float arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input float array (minuend); must have the same length as `b`
    /// @param b the second input float array (subtrahend); must have the same length as `a`
    /// @return a new float array where each element is `a[i] - b[i]`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
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

    /// Multiplies two float arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input float array; must have the same length as `b`
    /// @param b the second input float array; must have the same length as `a`
    /// @return a new float array where each element is the product of corresponding elements in `a` and `b`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float[] multiply(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            va.mul(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] * b[i];
        }
        return result;
    }

    /// Multiplies each element of a float array by a scalar using SIMD Vector API.
    ///
    /// @param a the input float array
    /// @param scalar the scalar multiplier to apply to each element
    /// @return a new float array where each element is scaled by `scalar`
    public static float[] multiply(float[] a, float scalar) {
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

    /// Divides two float arrays element-wise using SIMD Vector API.
    ///
    /// @param a the dividend input float array; must have the same length as `b`
    /// @param b the divisor input float array; must have the same length as `a`
    /// @return a new float array where each element is `a[i] / b[i]`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float[] divide(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            va.div(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] / b[i];
        }
        return result;
    }

    /// Computes the dot product of two float arrays using SIMD Vector API.
    ///
    /// @param a the first input float array; must have the same length as `b`
    /// @param b the second input float array; must have the same length as `a`
    /// @return the dot product value as a single float
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float dotProduct(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float sum = 0.0f;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            sumVec = sumVec.add(va.mul(vb));
        }
        
        sum = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /// Computes the sum of all elements in a float array using SIMD Vector API.
    ///
    /// @param a the input float array whose elements are to be summed
    /// @return the sum of all elements in the array as a single float
    public static float sum(float[] a) {
        float sum = 0.0f;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            sumVec = sumVec.add(va);
        }
        
        sum = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    /// Computes the L2 norm (Euclidean norm) of a float array.
    ///
    /// @param a the input float array
    /// @return the Euclidean norm (magnitude) of the array
    public static float norm(float[] a) {
        return (float) Math.sqrt(dotProduct(a, a));
    }

    /// Normalizes a float array to unit length (L2 norm = 1).
    /// If the input array has zero norm, returns a zero array.
    ///
    /// @param a the input float array to normalize
    /// @return a new float array normalized to unit length, or a zero array if the input has zero norm
    public static float[] normalize(float[] a) {
        float norm = norm(a);
        if (norm == 0.0f) {
            return new float[a.length]; // Return zero vector
        }
        return multiply(a, 1.0f / norm);
    }

    /// Computes the Euclidean distance between two float arrays using SIMD Vector API.
    ///
    /// @param a the first float array; must have the same length as `b`
    /// @param b the second float array; must have the same length as `a`
    /// @return the Euclidean distance between the two arrays
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float distance(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float sumSquares = 0.0f;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            FloatVector diff = va.sub(vb);
            sumVec = sumVec.add(diff.mul(diff));
        }
        
        sumSquares = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            float diff = a[i] - b[i];
            sumSquares += diff * diff;
        }
        return (float) Math.sqrt(sumSquares);
    }

    /// Computes the squared Euclidean distance between two float arrays using SIMD Vector API.
    /// This is faster than `distance()` when you only need to compare relative distances,
    /// as it avoids the costly `sqrt` operation.
    ///
    /// @param a the first float array; must have the same length as `b`
    /// @param b the second float array; must have the same length as `a`
    /// @return the squared Euclidean distance between the two arrays
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float distanceSquared(float[] a, float[] b) {
        validateLengths(a.length, b.length);
        float sumSquares = 0.0f;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            FloatVector diff = va.sub(vb);
            sumVec = sumVec.add(diff.mul(diff));
        }
        
        sumSquares = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            float diff = a[i] - b[i];
            sumSquares += diff * diff;
        }
        return sumSquares;
    }

    /// Finds the minimum value in a float array using SIMD Vector API.
    ///
    /// @param a the input float array to search
    /// @return the smallest value found in the array
    public static float min(float[] a) {
        float min = Float.MAX_VALUE;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        if (upperBound > 0) {
            FloatVector minVec = FloatVector.broadcast(FLOAT_SPECIES, Float.MAX_VALUE);
            for (; i < upperBound; i += FLOAT_SPECIES.length()) {
                FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
                minVec = minVec.min(va);
            }
            min = minVec.reduceLanes(VectorOperators.MIN);
        }
        
        for (; i < a.length; i++) {
            if (a[i] < min) min = a[i];
        }
        return min;
    }

    /// Finds the maximum value in a float array using SIMD Vector API.
    ///
    /// @param a the input float array to search
    /// @return the largest value found in the array
    public static float max(float[] a) {
        float max = -Float.MAX_VALUE;
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        if (upperBound > 0) {
            FloatVector maxVec = FloatVector.broadcast(FLOAT_SPECIES, -Float.MAX_VALUE);
            for (; i < upperBound; i += FLOAT_SPECIES.length()) {
                FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
                maxVec = maxVec.max(va);
            }
            max = maxVec.reduceLanes(VectorOperators.MAX);
        }
        
        for (; i < a.length; i++) {
            if (a[i] > max) max = a[i];
        }
        return max;
    }

    /// Computes element-wise square root of a float array using SIMD Vector API.
    ///
    /// @param a the input float array; each element must be non-negative
    /// @return a new float array where each element is the square root of the corresponding input element
    public static float[] sqrt(float[] a) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.sqrt().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = (float) Math.sqrt(a[i]);
        }
        return result;
    }

    /// Negates each element of a float array using SIMD Vector API.
    ///
    /// @param a the input float array
    /// @return a new float array where each element is the negation of the corresponding input element
    public static float[] negate(float[] a) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.neg().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = -a[i];
        }
        return result;
    }

    /// Computes the absolute value of each element in a float array using SIMD Vector API.
    ///
    /// @param a the input float array
    /// @return a new float array where each element is the absolute value of the corresponding input element
    public static float[] abs(float[] a) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.abs().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = Math.abs(a[i]);
        }
        return result;
    }

    /// Computes element-wise power
    /// Note: Uses scalar `Math.pow` for each element since Vector API has no native `pow` operation.
    /// Special case: when `b` equals 2.0f, delegates to element-wise multiplication for better performance.
    ///
    /// @param a the base input float array
    /// @param b the exponent value applied to each element
    /// @return a new float array where each element is `a[i]` raised to the power `b`
    public static float[] pow(float[] a, float b) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector vb = FloatVector.broadcast(FLOAT_SPECIES, b);
        
        // For integer powers, we can use vectorized multiplication
        if (b == 2.0f) {
            return multiply(a, a);
        }
        
        // Fallback to scalar for general case
        for (i = 0; i < a.length; i++) {
            result[i] = (float) Math.pow(a[i], b);
        }
        return result;
    }

    /// Clamps each element of a float array to the range [min, max] using SIMD Vector API.
    ///
    /// @param a the input float array to clamp
    /// @param min the lower bound for clamping
    /// @param max the upper bound for clamping
    /// @return a new float array where each element is clamped to [`min`, `max`]
    public static float[] clamp(float[] a, float min, float max) {
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector vmin = FloatVector.broadcast(FLOAT_SPECIES, min);
        FloatVector vmax = FloatVector.broadcast(FLOAT_SPECIES, max);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            va.max(vmin).min(vmax).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = Math.max(min, Math.min(max, a[i]));
        }
        return result;
    }

    /// Linear interpolation between two float arrays using SIMD Vector API.
    ///
    /// @param a the start float array; must have the same length as `b`
    /// @param b the end float array; must have the same length as `a`
    /// @param t the interpolation factor, typically in [0, 1]; 0 returns `a`, 1 returns `b`
    /// @return a new float array with linearly interpolated values
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static float[] lerp(float[] a, float[] b, float t) {
        validateLengths(a.length, b.length);
        float[] result = new float[a.length];
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(a.length);
        FloatVector vt = FloatVector.broadcast(FLOAT_SPECIES, t);
        
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, b, i);
            va.add(vb.sub(va).mul(vt)).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + t * (b[i] - a[i]);
        }
        return result;
    }

    // ==================== Double Array Operations ====================

    /// Adds two double arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input double array; must have the same length as `b`
    /// @param b the second input double array; must have the same length as `a`
    /// @return a new double array where each element is the sum of corresponding elements
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
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

    /// Adds a scalar value to each element of a double array using SIMD Vector API.
    ///
    /// @param a the input double array
    /// @param scalar the scalar value to add to each element
    /// @return a new double array where each element is incremented by `scalar`
    public static double[] add(double[] a, double scalar) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector vs = DoubleVector.broadcast(DOUBLE_SPECIES, scalar);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.add(vs).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + scalar;
        }
        return result;
    }

    /// Subtracts two double arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input double array (minuend); must have the same length as `b`
    /// @param b the second input double array (subtrahend); must have the same length as `a`
    /// @return a new double array where each element is `a[i] - b[i]`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double[] subtract(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            va.sub(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    /// Multiplies two double arrays element-wise using SIMD Vector API.
    ///
    /// @param a the first input double array; must have the same length as `b`
    /// @param b the second input double array; must have the same length as `a`
    /// @return a new double array where each element is the product of corresponding elements
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double[] multiply(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            va.mul(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] * b[i];
        }
        return result;
    }

    /// Multiplies each element of a double array by a scalar using SIMD Vector API.
    ///
    /// @param a the input double array
    /// @param scalar the scalar multiplier to apply to each element
    /// @return a new double array where each element is scaled by `scalar`
    public static double[] multiply(double[] a, double scalar) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector vs = DoubleVector.broadcast(DOUBLE_SPECIES, scalar);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.mul(vs).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }

    /// Divides two double arrays element-wise using SIMD Vector API.
    ///
    /// @param a the dividend input double array; must have the same length as `b`
    /// @param b the divisor input double array; must have the same length as `a`
    /// @return a new double array where each element is `a[i] / b[i]`
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double[] divide(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            va.div(vb).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] / b[i];
        }
        return result;
    }

    /// Computes the dot product of two double arrays using SIMD Vector API.
    ///
    /// @param a the first input double array; must have the same length as `b`
    /// @param b the second input double array; must have the same length as `a`
    /// @return the dot product value as a single double
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double dotProduct(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double sum = 0.0;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector sumVec = DoubleVector.zero(DOUBLE_SPECIES);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            sumVec = sumVec.add(va.mul(vb));
        }
        
        sum = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /// Computes the sum of all elements in a double array using SIMD Vector API.
    ///
    /// @param a the input double array whose elements are to be summed
    /// @return the sum of all elements in the array as a single double
    public static double sum(double[] a) {
        double sum = 0.0;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector sumVec = DoubleVector.zero(DOUBLE_SPECIES);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            sumVec = sumVec.add(va);
        }
        
        sum = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    /// Computes the L2 norm (Euclidean norm) of a double array.
    ///
    /// @param a the input double array
    /// @return the Euclidean norm (magnitude) of the array
    public static double norm(double[] a) {
        return Math.sqrt(dotProduct(a, a));
    }

    /// Normalizes a double array to unit length (L2 norm = 1).
    /// If the input array has zero norm, returns a zero array.
    ///
    /// @param a the input double array to normalize
    /// @return a new double array normalized to unit length, or a zero array if the input has zero norm
    public static double[] normalize(double[] a) {
        double norm = norm(a);
        if (norm == 0.0) {
            return new double[a.length];
        }
        return multiply(a, 1.0 / norm);
    }

    /// Computes the Euclidean distance between two double arrays using SIMD Vector API.
    ///
    /// @param a the first double array; must have the same length as `b`
    /// @param b the second double array; must have the same length as `a`
    /// @return the Euclidean distance between the two arrays
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double distance(double[] a, double[] b) {
        validateLengths(a.length, b.length);
        double sumSquares = 0.0;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector sumVec = DoubleVector.zero(DOUBLE_SPECIES);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            DoubleVector diff = va.sub(vb);
            sumVec = sumVec.add(diff.mul(diff));
        }
        
        sumSquares = sumVec.reduceLanes(VectorOperators.ADD);
        
        for (; i < a.length; i++) {
            double diff = a[i] - b[i];
            sumSquares += diff * diff;
        }
        return Math.sqrt(sumSquares);
    }

    /// Finds the minimum value in a double array using SIMD Vector API.
    ///
    /// @param a the input double array to search
    /// @return the smallest value found in the array
    public static double min(double[] a) {
        double min = Double.MAX_VALUE;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        if (upperBound > 0) {
            DoubleVector minVec = DoubleVector.broadcast(DOUBLE_SPECIES, Double.MAX_VALUE);
            for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
                DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
                minVec = minVec.min(va);
            }
            min = minVec.reduceLanes(VectorOperators.MIN);
        }
        
        for (; i < a.length; i++) {
            if (a[i] < min) min = a[i];
        }
        return min;
    }

    /// Finds the maximum value in a double array using SIMD Vector API.
    ///
    /// @param a the input double array to search
    /// @return the largest value found in the array
    public static double max(double[] a) {
        double max = -Double.MAX_VALUE;
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        if (upperBound > 0) {
            DoubleVector maxVec = DoubleVector.broadcast(DOUBLE_SPECIES, -Double.MAX_VALUE);
            for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
                DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
                maxVec = maxVec.max(va);
            }
            max = maxVec.reduceLanes(VectorOperators.MAX);
        }
        
        for (; i < a.length; i++) {
            if (a[i] > max) max = a[i];
        }
        return max;
    }

    /// Computes element-wise square root of a double array using SIMD Vector API.
    ///
    /// @param a the input double array; each element must be non-negative
    /// @return a new double array where each element is the square root of the corresponding input element
    public static double[] sqrt(double[] a) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.sqrt().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = Math.sqrt(a[i]);
        }
        return result;
    }

    /// Negates each element of a double array using SIMD Vector API.
    ///
    /// @param a the input double array
    /// @return a new double array where each element is the negation of the corresponding input element
    public static double[] negate(double[] a) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.neg().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = -a[i];
        }
        return result;
    }

    /// Computes the absolute value of each element in a double array using SIMD Vector API.
    ///
    /// @param a the input double array
    /// @return a new double array where each element is the absolute value of the corresponding input element
    public static double[] abs(double[] a) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.abs().intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = Math.abs(a[i]);
        }
        return result;
    }

    /// Clamps each element of a double array to the range [min, max] using SIMD Vector API.
    ///
    /// @param a the input double array to clamp
    /// @param min the lower bound for clamping
    /// @param max the upper bound for clamping
    /// @return a new double array where each element is clamped to [`min`, `max`]
    public static double[] clamp(double[] a, double min, double max) {
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector vmin = DoubleVector.broadcast(DOUBLE_SPECIES, min);
        DoubleVector vmax = DoubleVector.broadcast(DOUBLE_SPECIES, max);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            va.max(vmin).min(vmax).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = Math.max(min, Math.min(max, a[i]));
        }
        return result;
    }

    /// Linear interpolation between two double arrays using SIMD Vector API.
    ///
    /// @param a the start double array; must have the same length as `b`
    /// @param b the end double array; must have the same length as `a`
    /// @param t the interpolation factor, typically in [0, 1]; 0 returns `a`, 1 returns `b`
    /// @return a new double array with linearly interpolated values
    /// @throws IllegalArgumentException if `a` and `b` have different lengths
    public static double[] lerp(double[] a, double[] b, double t) {
        validateLengths(a.length, b.length);
        double[] result = new double[a.length];
        int i = 0;
        int upperBound = DOUBLE_SPECIES.loopBound(a.length);
        DoubleVector vt = DoubleVector.broadcast(DOUBLE_SPECIES, t);
        
        for (; i < upperBound; i += DOUBLE_SPECIES.length()) {
            DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i);
            DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, b, i);
            va.add(vb.sub(va).mul(vt)).intoArray(result, i);
        }
        
        for (; i < a.length; i++) {
            result[i] = a[i] + t * (b[i] - a[i]);
        }
        return result;
    }

    // ==================== Utility Methods ====================

    private static void validateLengths(int len1, int len2) {
        if (len1 != len2) {
            throw new IllegalArgumentException(
                "Array lengths must be equal: " + len1 + " != " + len2);
        }
    }

    /// Returns the preferred vector lane length for float SIMD operations on this platform.
    ///
    /// @return the number of float elements processed per SIMD instruction
    public static int getPreferredFloatVectorLength() {
        return FLOAT_SPECIES.length();
    }

    /// Returns the preferred vector lane length for double SIMD operations on this platform.
    ///
    /// @return the number of double elements processed per SIMD instruction
    public static int getPreferredDoubleVectorLength() {
        return DOUBLE_SPECIES.length();
    }

    /// Returns the vector species used for float SIMD operations.
    ///
    /// @return the {@link VectorSpecies} for float elements
    public static VectorSpecies<Float> getFloatSpecies() {
        return FLOAT_SPECIES;
    }

    /// Returns the vector species used for double SIMD operations.
    ///
    /// @return the {@link VectorSpecies} for double elements
    public static VectorSpecies<Double> getDoubleSpecies() {
        return DOUBLE_SPECIES;
    }
}