/**
 * High-performance math library using Java Vector API.
 * 
 * <p>This library provides SIMD-accelerated mathematical operations
 * for optimal performance on modern CPUs. It requires Java 21+ with
 * the Vector API preview feature enabled.
 * 
 * <h2>Modules</h2>
 * <ul>
 *   <li>{@link cn.tuyucheng.taketoday.fastmath.VectorMath} - Vector array operations (SIMD)</li>
 *   <li>{@link cn.tuyucheng.taketoday.fastmath.FastMath} - Fast scalar math functions</li>
 *   <li>{@link cn.tuyucheng.taketoday.fastmath.MatrixMath} - Matrix operations</li>
 *   <li>{@link cn.tuyucheng.taketoday.fastmath.VectorOps} - 2D/3D/4D vector classes</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>{@code
 * // Vector operations on arrays
 * float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
 * float[] b = {2, 2, 2, 2, 2, 2, 2, 2};
 * float[] sum = VectorMath.add(a, b);
 * float dot = VectorMath.dotProduct(a, b);
 * 
 * // Fast scalar functions
 * float sin = FastMath.sin(0.785f);  // Fast sine
 * float sqrt = FastMath.sqrt(16.0f);  // Fast sqrt
 * 
 * // 3D vectors
 * VectorOps.Vec3 v1 = new VectorOps.Vec3(1, 2, 3);
 * VectorOps.Vec3 v2 = new VectorOps.Vec3(4, 5, 6);
 * VectorOps.Vec3 cross = v1.cross(v2);
 * 
 * // Matrices
 * float[] m = MatrixMath.identity(3);
 * float[] product = MatrixMath.multiply(a, b, 2, 2, 2);
 * }</pre>
 * 
 * <h2>Compile and Run</h2>
 * <pre>
 * javac --enable-preview --add-modules=jdk.incubator.vector *.java
 * java --enable-preview --add-modules=jdk.incubator.vector Main
 * </pre>
 * 
 * @since 1.0.0
 */
package cn.tuyucheng.taketoday.fastmath;

import jdk.incubator.vector.*;

/**
 * High-performance vector math operations using Java Vector API.
 * This class provides SIMD-accelerated operations on float and double arrays.
 * 
 * <p>Vector API uses SIMD (Single Instruction Multiple Data) instructions
 * to process multiple elements simultaneously, providing significant
 * performance improvements for mathematical operations on arrays.
 * 
 * <p>Supported operations include:
 * <ul>
 *   <li>Element-wise arithmetic: add, subtract, multiply, divide</li>
 *   <li>Mathematical functions: sin, cos, sqrt, exp, log, pow</li>
 *   <li>Reduction operations: sum, dot product, min, max</li>
 *   <li>Normalization and distance calculations</li>
 * </ul>
 */
public final class VectorMath {

    private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    // Prevent instantiation
    private VectorMath() {}

    // ==================== Float Array Operations ====================

    /**
     * Adds two float arrays element-wise.
     * Result[i] = a[i] + b[i]
     * 
     * @param a first input array
     * @param b second input array
     * @return new array containing the sum
     * @throws IllegalArgumentException if arrays have different lengths
     */
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

    /**
     * Adds a scalar to each element of a float array.
     * Result[i] = a[i] + scalar
     */
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

    /**
     * Subtracts two float arrays element-wise.
     * Result[i] = a[i] - b[i]
     */
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

    /**
     * Multiplies two float arrays element-wise.
     * Result[i] = a[i] * b[i]
     */
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

    /**
     * Multiplies a float array by a scalar.
     * Result[i] = a[i] * scalar
     */
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

    /**
     * Divides two float arrays element-wise.
     * Result[i] = a[i] / b[i]
     */
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

    /**
     * Computes the dot product of two float arrays.
     * Result = sum(a[i] * b[i])
     */
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

    /**
     * Computes the sum of all elements in a float array.
     */
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

    /**
     * Computes the L2 norm (Euclidean norm) of a float array.
     * Result = sqrt(sum(a[i] * a[i]))
     */
    public static float norm(float[] a) {
        return (float) Math.sqrt(dotProduct(a, a));
    }

    /**
     * Normalizes a float array to unit length.
     * Result[i] = a[i] / ||a||
     */
    public static float[] normalize(float[] a) {
        float norm = norm(a);
        if (norm == 0.0f) {
            return new float[a.length]; // Return zero vector
        }
        return multiply(a, 1.0f / norm);
    }

    /**
     * Computes the Euclidean distance between two float arrays.
     * Result = sqrt(sum((a[i] - b[i])^2))
     */
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

    /**
     * Computes the squared Euclidean distance between two float arrays.
     * Result = sum((a[i] - b[i])^2)
     * This is faster than distance() when you only need to compare distances.
     */
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

    /**
     * Finds the minimum value in a float array.
     */
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

    /**
     * Finds the maximum value in a float array.
     */
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

    /**
     * Computes element-wise square root of a float array.
     */
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

    /**
     * Negates each element of a float array.
     */
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

    /**
     * Computes absolute value of each element.
     */
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

    /**
     * Computes element-wise power: a^b.
     * Note: Uses scalar Math.pow for each element as Vector API doesn't have native pow.
     */
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

    /**
     * Clamps each element to the range [min, max].
     */
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

    /**
     * Linear interpolation between two arrays.
     * Result[i] = a[i] + t * (b[i] - a[i])
     */
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

    /**
     * Adds two double arrays element-wise.
     */
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

    /**
     * Adds a scalar to each element of a double array.
     */
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

    /**
     * Subtracts two double arrays element-wise.
     */
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

    /**
     * Multiplies two double arrays element-wise.
     */
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

    /**
     * Multiplies a double array by a scalar.
     */
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

    /**
     * Divides two double arrays element-wise.
     */
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

    /**
     * Computes the dot product of two double arrays.
     */
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

    /**
     * Computes the sum of all elements in a double array.
     */
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

    /**
     * Computes the L2 norm (Euclidean norm) of a double array.
     */
    public static double norm(double[] a) {
        return Math.sqrt(dotProduct(a, a));
    }

    /**
     * Normalizes a double array to unit length.
     */
    public static double[] normalize(double[] a) {
        double norm = norm(a);
        if (norm == 0.0) {
            return new double[a.length];
        }
        return multiply(a, 1.0 / norm);
    }

    /**
     * Computes the Euclidean distance between two double arrays.
     */
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

    /**
     * Finds the minimum value in a double array.
     */
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

    /**
     * Finds the maximum value in a double array.
     */
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

    /**
     * Computes element-wise square root of a double array.
     */
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

    /**
     * Negates each element of a double array.
     */
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

    /**
     * Computes absolute value of each element.
     */
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

    /**
     * Clamps each element to the range [min, max].
     */
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

    /**
     * Linear interpolation between two double arrays.
     */
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

    /**
     * Returns the preferred vector length for float operations.
     */
    public static int getPreferredFloatVectorLength() {
        return FLOAT_SPECIES.length();
    }

    /**
     * Returns the preferred vector length for double operations.
     */
    public static int getPreferredDoubleVectorLength() {
        return DOUBLE_SPECIES.length();
    }

    /**
     * Returns the vector species used for float operations.
     */
    public static VectorSpecies<Float> getFloatSpecies() {
        return FLOAT_SPECIES;
    }

    /**
     * Returns the vector species used for double operations.
     */
    public static VectorSpecies<Double> getDoubleSpecies() {
        return DOUBLE_SPECIES;
    }
}