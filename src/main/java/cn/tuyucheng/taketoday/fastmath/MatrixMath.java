package cn.tuyucheng.taketoday.fastmath;

import jdk.incubator.vector.*;

/**
 * High-performance matrix operations using Java Vector API.
 * Supports both float and double matrices in row-major order.
 * 
 * <p>All matrices are represented as 1D arrays in row-major order:
 * element at row i, column j is at index [i * cols + j].
 * 
 * <p>Supported operations:
 * <ul>
 *   <li>Matrix creation and initialization</li>
 *   <li>Matrix addition, subtraction, multiplication</li>
 *   <li>Matrix-vector multiplication</li>
 *   <li>Transpose, trace, determinant</li>
 *   <li>Matrix norms</li>
 * </ul>
 */
public final class MatrixMath {

    private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    private MatrixMath() {}

    // ==================== Matrix Creation ====================

    /**
     * Creates a zero matrix of given dimensions.
     */
    public static float[] zeros(int rows, int cols) {
        return new float[rows * cols];
    }

    /**
     * Creates an identity matrix of size n x n.
     */
    public static float[] identity(int n) {
        float[] result = new float[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = 1.0f;
        }
        return result;
    }

    /**
     * Creates a matrix filled with a constant value.
     */
    public static float[] filled(int rows, int cols, float value) {
        float[] result = new float[rows * cols];
        if (value != 0.0f) {
            int i = 0;
            int upperBound = FLOAT_SPECIES.loopBound(result.length);
            FloatVector v = FloatVector.broadcast(FLOAT_SPECIES, value);
            
            for (; i < upperBound; i += FLOAT_SPECIES.length()) {
                v.intoArray(result, i);
            }
            
            for (; i < result.length; i++) {
                result[i] = value;
            }
        }
        return result;
    }

    /**
     * Creates a diagonal matrix from a vector.
     */
    public static float[] diagonal(float[] diag) {
        int n = diag.length;
        float[] result = new float[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = diag[i];
        }
        return result;
    }

    // ==================== Matrix Arithmetic ====================

    /**
     * Adds two matrices element-wise.
     * @param a first matrix (rows x cols)
     * @param b second matrix (rows x cols)
     * @param rows number of rows
     * @param cols number of columns
     * @return new matrix (rows x cols)
     */
    public static float[] add(float[] a, float[] b, int rows, int cols) {
        return VectorMath.add(a, b);
    }

    /**
     * Subtracts two matrices element-wise.
     */
    public static float[] subtract(float[] a, float[] b, int rows, int cols) {
        return VectorMath.subtract(a, b);
    }

    /**
     * Multiplies a matrix by a scalar.
     */
    public static float[] multiply(float[] a, int rows, int cols, float scalar) {
        return VectorMath.multiply(a, scalar);
    }

    /**
     * Multiplies two matrices: C = A * B
     * @param a matrix A (m x k)
     * @param b matrix B (k x n)
     * @param m rows of A
     * @param k columns of A / rows of B
     * @param n columns of B
     * @return matrix C (m x n)
     */
    public static float[] multiply(float[] a, float[] b, int m, int k, int n) {
        float[] result = new float[m * n];
        
        // Transpose B for better cache locality
        float[] bT = transpose(b, k, n);
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                float sum = 0.0f;
                int idx = 0;
                int upperBound = FLOAT_SPECIES.loopBound(k);
                FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
                
                // Vectorized dot product
                for (; idx < upperBound; idx += FLOAT_SPECIES.length()) {
                    FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i * k + idx);
                    FloatVector vb = FloatVector.fromArray(FLOAT_SPECIES, bT, j * k + idx);
                    sumVec = sumVec.add(va.mul(vb));
                }
                
                sum = sumVec.reduceLanes(VectorOperators.ADD);
                
                // Tail
                for (; idx < k; idx++) {
                    sum += a[i * k + idx] * bT[j * k + idx];
                }
                
                result[i * n + j] = sum;
            }
        }
        
        return result;
    }

    /**
     * Matrix-vector multiplication: y = A * x
     * @param a matrix A (m x n)
     * @param x vector x (n elements)
     * @param m rows of A
     * @param n columns of A
     * @return vector y (m elements)
     */
    public static float[] multiplyVector(float[] a, float[] x, int m, int n) {
        float[] result = new float[m];
        
        for (int i = 0; i < m; i++) {
            float sum = 0.0f;
            int j = 0;
            int upperBound = FLOAT_SPECIES.loopBound(n);
            FloatVector sumVec = FloatVector.zero(FLOAT_SPECIES);
            
            for (; j < upperBound; j += FLOAT_SPECIES.length()) {
                FloatVector va = FloatVector.fromArray(FLOAT_SPECIES, a, i * n + j);
                FloatVector vx = FloatVector.fromArray(FLOAT_SPECIES, x, j);
                sumVec = sumVec.add(va.mul(vx));
            }
            
            sum = sumVec.reduceLanes(VectorOperators.ADD);
            
            for (; j < n; j++) {
                sum += a[i * n + j] * x[j];
            }
            
            result[i] = sum;
        }
        
        return result;
    }

    /**
     * Element-wise (Hadamard) product of two matrices.
     */
    public static float[] hadamardProduct(float[] a, float[] b, int rows, int cols) {
        return VectorMath.multiply(a, b);
    }

    // ==================== Matrix Transformations ====================

    /**
     * Transposes a matrix.
     * @param a matrix (rows x cols)
     * @param rows number of rows
     * @param cols number of columns
     * @return transposed matrix (cols x rows)
     */
    public static float[] transpose(float[] a, int rows, int cols) {
        float[] result = new float[cols * rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j * rows + i] = a[i * cols + j];
            }
        }
        
        return result;
    }

    /**
     * Computes the trace (sum of diagonal elements) of a square matrix.
     */
    public static float trace(float[] a, int n) {
        float sum = 0.0f;
        for (int i = 0; i < n; i++) {
            sum += a[i * n + i];
        }
        return sum;
    }

    /**
     * Extracts a row from a matrix.
     */
    public static float[] getRow(float[] a, int rows, int cols, int row) {
        float[] result = new float[cols];
        System.arraycopy(a, row * cols, result, 0, cols);
        return result;
    }

    /**
     * Extracts a column from a matrix.
     */
    public static float[] getColumn(float[] a, int rows, int cols, int col) {
        float[] result = new float[rows];
        for (int i = 0; i < rows; i++) {
            result[i] = a[i * cols + col];
        }
        return result;
    }

    // ==================== Matrix Norms ====================

    /**
     * Computes the Frobenius norm of a matrix.
     * ||A||_F = sqrt(sum of all elements squared)
     */
    public static float frobeniusNorm(float[] a, int rows, int cols) {
        return VectorMath.norm(a);
    }

    /**
     * Computes the L1 norm (maximum column sum) of a matrix.
     */
    public static float l1Norm(float[] a, int rows, int cols) {
        float maxSum = 0.0f;
        for (int j = 0; j < cols; j++) {
            float colSum = 0.0f;
            for (int i = 0; i < rows; i++) {
                colSum += Math.abs(a[i * cols + j]);
            }
            maxSum = Math.max(maxSum, colSum);
        }
        return maxSum;
    }

    /**
     * Computes the infinity norm (maximum row sum) of a matrix.
     */
    public static float infinityNorm(float[] a, int rows, int cols) {
        float maxSum = 0.0f;
        for (int i = 0; i < rows; i++) {
            float rowSum = 0.0f;
            for (int j = 0; j < cols; j++) {
                rowSum += Math.abs(a[i * cols + j]);
            }
            maxSum = Math.max(maxSum, rowSum);
        }
        return maxSum;
    }

    // ==================== Determinant ====================

    /**
     * Computes the determinant of a 2x2 matrix.
     */
    public static float determinant2x2(float[] a) {
        return a[0] * a[3] - a[1] * a[2];
    }

    /**
     * Computes the determinant of a 3x3 matrix.
     */
    public static float determinant3x3(float[] a) {
        return a[0] * (a[4] * a[8] - a[5] * a[7])
             - a[1] * (a[3] * a[8] - a[5] * a[6])
             + a[2] * (a[3] * a[7] - a[4] * a[6]);
    }

    /**
     * Computes the determinant of a 4x4 matrix.
     */
    public static float determinant4x4(float[] a) {
        float det = 0.0f;
        float[] sub = new float[9];
        
        for (int j = 0; j < 4; j++) {
            // Get submatrix
            int subIdx = 0;
            for (int row = 1; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    if (col != j) {
                        sub[subIdx++] = a[row * 4 + col];
                    }
                }
            }
            
            float sign = (j % 2 == 0) ? 1.0f : -1.0f;
            det += sign * a[j] * determinant3x3(sub);
        }
        
        return det;
    }

    // ==================== Inverse (Small Matrices) ====================

    /**
     * Computes the inverse of a 2x2 matrix.
     */
    public static float[] inverse2x2(float[] a) {
        float det = determinant2x2(a);
        if (det == 0.0f) {
            throw new ArithmeticException("Matrix is singular");
        }
        
        float invDet = 1.0f / det;
        return new float[] {
            a[3] * invDet, -a[1] * invDet,
            -a[2] * invDet, a[0] * invDet
        };
    }

    /**
     * Computes the inverse of a 3x3 matrix.
     */
    public static float[] inverse3x3(float[] a) {
        float det = determinant3x3(a);
        if (det == 0.0f) {
            throw new ArithmeticException("Matrix is singular");
        }
        
        float invDet = 1.0f / det;
        return new float[] {
            (a[4] * a[8] - a[5] * a[7]) * invDet,
            (a[2] * a[7] - a[1] * a[8]) * invDet,
            (a[1] * a[5] - a[2] * a[4]) * invDet,
            (a[5] * a[6] - a[3] * a[8]) * invDet,
            (a[0] * a[8] - a[2] * a[6]) * invDet,
            (a[2] * a[3] - a[0] * a[5]) * invDet,
            (a[3] * a[7] - a[4] * a[6]) * invDet,
            (a[1] * a[6] - a[0] * a[7]) * invDet,
            (a[0] * a[4] - a[1] * a[3]) * invDet
        };
    }

    // ==================== Double Matrix Operations ====================

    /**
     * Creates a zero matrix of given dimensions (double).
     */
    public static double[] zerosDouble(int rows, int cols) {
        return new double[rows * cols];
    }

    /**
     * Creates an identity matrix of size n x n (double).
     */
    public static double[] identityDouble(int n) {
        double[] result = new double[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = 1.0;
        }
        return result;
    }

    /**
     * Multiplies two matrices: C = A * B (double).
     */
    public static double[] multiply(double[] a, double[] b, int m, int k, int n) {
        double[] result = new double[m * n];
        double[] bT = transposeDouble(b, k, n);
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                int idx = 0;
                int upperBound = DOUBLE_SPECIES.loopBound(k);
                DoubleVector sumVec = DoubleVector.zero(DOUBLE_SPECIES);
                
                for (; idx < upperBound; idx += DOUBLE_SPECIES.length()) {
                    DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i * k + idx);
                    DoubleVector vb = DoubleVector.fromArray(DOUBLE_SPECIES, bT, j * k + idx);
                    sumVec = sumVec.add(va.mul(vb));
                }
                
                sum = sumVec.reduceLanes(VectorOperators.ADD);
                
                for (; idx < k; idx++) {
                    sum += a[i * k + idx] * bT[j * k + idx];
                }
                
                result[i * n + j] = sum;
            }
        }
        
        return result;
    }

    /**
     * Transposes a matrix (double).
     */
    public static double[] transposeDouble(double[] a, int rows, int cols) {
        double[] result = new double[cols * rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j * rows + i] = a[i * cols + j];
            }
        }
        
        return result;
    }

    /**
     * Matrix-vector multiplication: y = A * x (double).
     */
    public static double[] multiplyVector(double[] a, double[] x, int m, int n) {
        double[] result = new double[m];
        
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            int j = 0;
            int upperBound = DOUBLE_SPECIES.loopBound(n);
            DoubleVector sumVec = DoubleVector.zero(DOUBLE_SPECIES);
            
            for (; j < upperBound; j += DOUBLE_SPECIES.length()) {
                DoubleVector va = DoubleVector.fromArray(DOUBLE_SPECIES, a, i * n + j);
                DoubleVector vx = DoubleVector.fromArray(DOUBLE_SPECIES, x, j);
                sumVec = sumVec.add(va.mul(vx));
            }
            
            sum = sumVec.reduceLanes(VectorOperators.ADD);
            
            for (; j < n; j++) {
                sum += a[i * n + j] * x[j];
            }
            
            result[i] = sum;
        }
        
        return result;
    }

    // ==================== Utility ====================

    /**
     * Prints a matrix for debugging.
     */
    public static String toString(float[] a, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%10.4f ", a[i * cols + j]));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}