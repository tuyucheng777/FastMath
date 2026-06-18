package cn.tuyucheng.taketoday.fastmath;

import jdk.incubator.vector.*;

/// High-performance matrix operations using Java Vector API.
/// Supports both float and double matrices in row-major order.
///
/// All matrices are represented as 1D arrays in row-major order:
/// element at row i, column j is at index `[i * cols + j]`.
///
/// Supported operations:
/// - Matrix creation and initialization
/// - Matrix addition, subtraction, multiplication
/// - Matrix-vector multiplication
/// - Transpose, trace, determinant
/// - Matrix norms
public final class MatrixMath {

    private static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Double> DOUBLE_SPECIES = DoubleVector.SPECIES_PREFERRED;

    private MatrixMath() {}

    // ==================== Matrix Creation ====================

    /// Creates a zero matrix of given dimensions.
    ///
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new float array of length `rows * cols` filled with zeros
    public static float[] zeros(int rows, int cols) {
        return new float[rows * cols];
    }

    /// Creates an identity matrix of size n x n.
    ///
    /// @param n the size of the identity matrix
    /// @return a new float array of length `n * n` with 1.0f on the diagonal
    public static float[] identity(int n) {
        float[] result = new float[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = 1.0f;
        }
        return result;
    }

    /// Creates a matrix filled with a constant value.
    ///
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @param value the constant value to fill every element
    /// @return a new float array of length `rows * cols` filled with `value`
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

    /// Creates a diagonal matrix from a vector.
    ///
    /// @param diag the diagonal values; length determines the matrix size
    /// @return a new `n x n` float matrix with `diag` values on the diagonal and zeros elsewhere
    public static float[] diagonal(float[] diag) {
        int n = diag.length;
        float[] result = new float[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = diag[i];
        }
        return result;
    }

    // ==================== Matrix Arithmetic ====================

    /// Adds two matrices element-wise.
    ///
    /// @param a the first matrix in row-major order; length must be `rows * cols`
    /// @param b the second matrix in row-major order; length must be `rows * cols`
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new float array of length `rows * cols` representing the element-wise sum
    public static float[] add(float[] a, float[] b, int rows, int cols) {
        return VectorMath.add(a, b);
    }

    /// Subtracts two matrices element-wise.
    ///
    /// @param a the first matrix (minuend) in row-major order; length must be `rows * cols`
    /// @param b the second matrix (subtrahend) in row-major order; length must be `rows * cols`
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new float array of length `rows * cols` representing the element-wise difference
    public static float[] subtract(float[] a, float[] b, int rows, int cols) {
        return VectorMath.subtract(a, b);
    }

    /// Multiplies a matrix by a scalar.
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @param scalar the scalar value to multiply every element by
    /// @return a new float array of length `rows * cols` scaled by `scalar`
    public static float[] multiply(float[] a, int rows, int cols, float scalar) {
        return VectorMath.multiply(a, scalar);
    }

    /// Multiplies two matrices: C = A * B.
    ///
    /// Uses SIMD Vector API for vectorized dot products along each row-column pair.
    /// Transposes B internally for better cache locality.
    ///
    /// @param a matrix A in row-major order (m x k elements)
    /// @param b matrix B in row-major order (k x n elements)
    /// @param m the number of rows of A
    /// @param k the number of columns of A (also rows of B)
    /// @param n the number of columns of B
    /// @return matrix C in row-major order (m x n elements)
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

    /// Matrix-vector multiplication: y = A * x.
    ///
    /// Uses SIMD Vector API for vectorized dot products.
    ///
    /// @param a matrix A in row-major order (m x n elements)
    /// @param x vector x (n elements)
    /// @param m the number of rows of A
    /// @param n the number of columns of A (also length of x)
    /// @return vector y of length m, where `y[i] = dot(row_i_of_A, x)`
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

    /// Element-wise (Hadamard) product of two matrices.
    ///
    /// @param a the first matrix in row-major order; length must be `rows * cols`
    /// @param b the second matrix in row-major order; length must be `rows * cols`
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new float array of length `rows * cols` representing the element-wise product
    public static float[] hadamardProduct(float[] a, float[] b, int rows, int cols) {
        return VectorMath.multiply(a, b);
    }

    // ==================== Matrix Transformations ====================

    /// Transposes a matrix.
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new float array in row-major order representing the transposed matrix (cols x rows)
    public static float[] transpose(float[] a, int rows, int cols) {
        float[] result = new float[cols * rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j * rows + i] = a[i * cols + j];
            }
        }
        
        return result;
    }

    /// Computes the trace (sum of diagonal elements) of a square matrix.
    ///
    /// @param a the square matrix in row-major order; length must be `n * n`
    /// @param n the size of the square matrix
    /// @return the trace value, `sum(a[i * n + i])` for `i = 0..n-1`
    public static float trace(float[] a, int n) {
        float sum = 0.0f;
        for (int i = 0; i < n; i++) {
            sum += a[i * n + i];
        }
        return sum;
    }

    /// Extracts a row from a matrix.
    ///
    /// @param a the matrix in row-major order; length must be `rows * cols`
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @param row the row index to extract (0-based)
    /// @return a new float array of length `cols` containing the specified row
    public static float[] getRow(float[] a, int rows, int cols, int row) {
        float[] result = new float[cols];
        System.arraycopy(a, row * cols, result, 0, cols);
        return result;
    }

    /// Extracts a column from a matrix.
    ///
    /// @param a the matrix in row-major order; length must be `rows * cols`
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @param col the column index to extract (0-based)
    /// @return a new float array of length `rows` containing the specified column
    public static float[] getColumn(float[] a, int rows, int cols, int col) {
        float[] result = new float[rows];
        for (int i = 0; i < rows; i++) {
            result[i] = a[i * cols + col];
        }
        return result;
    }

    // ==================== Matrix Norms ====================

    /// Computes the Frobenius norm of a matrix.
    /// `||A||_F = sqrt(sum of all elements squared)`
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return the Frobenius norm value
    public static float frobeniusNorm(float[] a, int rows, int cols) {
        return VectorMath.norm(a);
    }

    /// Computes the L1 norm (maximum column sum) of a matrix.
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return the L1 norm, the maximum absolute column sum
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

    /// Computes the infinity norm (maximum row sum) of a matrix.
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return the infinity norm, the maximum absolute row sum
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

    /// Computes the determinant of a 2x2 matrix.
    ///
    /// @param a the 2x2 matrix in row-major order; length must be 4
    /// @return the determinant value, `a[0]*a[3] - a[1]*a[2]`
    public static float determinant2x2(float[] a) {
        return a[0] * a[3] - a[1] * a[2];
    }

    /// Computes the determinant of a 3x3 matrix.
    ///
    /// @param a the 3x3 matrix in row-major order; length must be 9
    /// @return the determinant value
    public static float determinant3x3(float[] a) {
        return a[0] * (a[4] * a[8] - a[5] * a[7])
             - a[1] * (a[3] * a[8] - a[5] * a[6])
             + a[2] * (a[3] * a[7] - a[4] * a[6]);
    }

    /// Computes the determinant of a 4x4 matrix.
    ///
    /// @param a the 4x4 matrix in row-major order; length must be 16
    /// @return the determinant value
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

    /// Computes the inverse of a 2x2 matrix.
    ///
    /// @param a the 2x2 matrix in row-major order; length must be 4
    /// @return the inverse matrix in row-major order; length 4
    /// @throws ArithmeticException if the matrix is singular (determinant is zero)
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

    /// Computes the inverse of a 3x3 matrix.
    ///
    /// @param a the 3x3 matrix in row-major order; length must be 9
    /// @return the inverse matrix in row-major order; length 9
    /// @throws ArithmeticException if the matrix is singular (determinant is zero)
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

    /// Creates a zero matrix of given dimensions (double precision).
    ///
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new double array of length `rows * cols` filled with zeros
    public static double[] zerosDouble(int rows, int cols) {
        return new double[rows * cols];
    }

    /// Creates an identity matrix of size n x n (double precision).
    ///
    /// @param n the size of the identity matrix
    /// @return a new double array of length `n * n` with 1.0 on the diagonal
    public static double[] identityDouble(int n) {
        double[] result = new double[n * n];
        for (int i = 0; i < n; i++) {
            result[i * n + i] = 1.0;
        }
        return result;
    }

    /// Multiplies two matrices: C = A * B (double precision).
    ///
    /// Uses SIMD Vector API for vectorized dot products.
    /// Transposes B internally for better cache locality.
    ///
    /// @param a matrix A in row-major order (m x k elements)
    /// @param b matrix B in row-major order (k x n elements)
    /// @param m the number of rows of A
    /// @param k the number of columns of A (also rows of B)
    /// @param n the number of columns of B
    /// @return matrix C in row-major order (m x n elements)
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

    /// Transposes a matrix (double precision).
    ///
    /// @param a the double-precision matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a new double array in row-major order representing the transposed matrix (cols x rows)
    public static double[] transposeDouble(double[] a, int rows, int cols) {
        double[] result = new double[cols * rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j * rows + i] = a[i * cols + j];
            }
        }
        
        return result;
    }

    /// Matrix-vector multiplication: y = A * x (double precision).
    ///
    /// Uses SIMD Vector API for vectorized dot products.
    ///
    /// @param a matrix A in row-major order (m x n elements)
    /// @param x vector x (n elements)
    /// @param m the number of rows of A
    /// @param n the number of columns of A (also length of x)
    /// @return vector y of length m, where `y[i] = dot(row_i_of_A, x)`
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

    /// Prints a matrix for debugging.
    ///
    /// @param a the matrix in row-major order
    /// @param rows the number of rows
    /// @param cols the number of columns
    /// @return a formatted string with each element as `%10.4f`, rows separated by newlines
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