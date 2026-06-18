package cn.tuyucheng.taketoday.fastmath.benchmark;

import cn.tuyucheng.taketoday.fastmath.FastMath;
import cn.tuyucheng.taketoday.fastmath.MatrixMath;
import cn.tuyucheng.taketoday.fastmath.VectorMath;
import cn.tuyucheng.taketoday.fastmath.VectorOps;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// JMH benchmarks for FastMath library.
/// Compares Vector API operations against scalar implementations.
///
/// Run with: `java --enable-preview --add-modules=jdk.incubator.vector -jar target/benchmarks.jar`
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class VectorMathBenchmark {

    // Array sizes for testing
    @Param({"64", "256", "1024", "4096", "16384", "65536"})
    public int arraySize;

    private float[] floatArrayA;
    private float[] floatArrayB;
    private float[] floatResult;
    private double[] doubleArrayA;
    private double[] doubleArrayB;
    private double[] doubleResult;

    // Matrices
    private float[] matrixA;
    private float[] matrixB;
    private float[] vector;

    // Precomputed random values
    private float randomFloat;
    private double randomDouble;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(42); // Fixed seed for reproducibility

        floatArrayA = new float[arraySize];
        floatArrayB = new float[arraySize];
        floatResult = new float[arraySize];
        doubleArrayA = new double[arraySize];
        doubleArrayB = new double[arraySize];
        doubleResult = new double[arraySize];

        for (int i = 0; i < arraySize; i++) {
            floatArrayA[i] = random.nextFloat() * 100 + 1; // Avoid 0 for division
            floatArrayB[i] = random.nextFloat() * 100 + 1;
            doubleArrayA[i] = random.nextDouble() * 100 + 1;
            doubleArrayB[i] = random.nextDouble() * 100 + 1;
        }

        // Setup matrices (64x64 for reasonable benchmark time)
        int matrixSize = 64;
        matrixA = new float[matrixSize * matrixSize];
        matrixB = new float[matrixSize * matrixSize];
        vector = new float[matrixSize];
        for (int i = 0; i < matrixSize * matrixSize; i++) {
            matrixA[i] = random.nextFloat() * 10;
            matrixB[i] = random.nextFloat() * 10;
        }
        for (int i = 0; i < matrixSize; i++) {
            vector[i] = random.nextFloat() * 10;
        }

        randomFloat = random.nextFloat() * (float) Math.PI;
        randomDouble = random.nextDouble() * Math.PI;
    }

    // ==================== Float Array Operations ====================

    @Benchmark
    public void scalarAddFloat(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            floatResult[i] = floatArrayA[i] + floatArrayB[i];
        }
        bh.consume(floatResult);
    }

    @Benchmark
    public void vectorAddFloat(Blackhole bh) {
        float[] result = VectorMath.add(floatArrayA, floatArrayB);
        bh.consume(result);
    }

    @Benchmark
    public void scalarMultiplyFloat(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            floatResult[i] = floatArrayA[i] * floatArrayB[i];
        }
        bh.consume(floatResult);
    }

    @Benchmark
    public void vectorMultiplyFloat(Blackhole bh) {
        float[] result = VectorMath.multiply(floatArrayA, floatArrayB);
        bh.consume(result);
    }

    @Benchmark
    public void scalarDotProductFloat(Blackhole bh) {
        float sum = 0;
        for (int i = 0; i < arraySize; i++) {
            sum += floatArrayA[i] * floatArrayB[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void vectorDotProductFloat(Blackhole bh) {
        float result = VectorMath.dotProduct(floatArrayA, floatArrayB);
        bh.consume(result);
    }

    @Benchmark
    public void scalarSumFloat(Blackhole bh) {
        float sum = 0;
        for (int i = 0; i < arraySize; i++) {
            sum += floatArrayA[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void vectorSumFloat(Blackhole bh) {
        float result = VectorMath.sum(floatArrayA);
        bh.consume(result);
    }

    @Benchmark
    public void scalarSqrtFloat(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            floatResult[i] = (float) Math.sqrt(floatArrayA[i]);
        }
        bh.consume(floatResult);
    }

    @Benchmark
    public void vectorSqrtFloat(Blackhole bh) {
        float[] result = VectorMath.sqrt(floatArrayA);
        bh.consume(result);
    }

    @Benchmark
    public void scalarNormFloat(Blackhole bh) {
        float sum = 0;
        for (int i = 0; i < arraySize; i++) {
            sum += floatArrayA[i] * floatArrayA[i];
        }
        float norm = (float) Math.sqrt(sum);
        bh.consume(norm);
    }

    @Benchmark
    public void vectorNormFloat(Blackhole bh) {
        float result = VectorMath.norm(floatArrayA);
        bh.consume(result);
    }

    // ==================== Double Array Operations ====================

    @Benchmark
    public void scalarAddDouble(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            doubleResult[i] = doubleArrayA[i] + doubleArrayB[i];
        }
        bh.consume(doubleResult);
    }

    @Benchmark
    public void vectorAddDouble(Blackhole bh) {
        double[] result = VectorMath.add(doubleArrayA, doubleArrayB);
        bh.consume(result);
    }

    @Benchmark
    public void scalarDotProductDouble(Blackhole bh) {
        double sum = 0;
        for (int i = 0; i < arraySize; i++) {
            sum += doubleArrayA[i] * doubleArrayB[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void vectorDotProductDouble(Blackhole bh) {
        double result = VectorMath.dotProduct(doubleArrayA, doubleArrayB);
        bh.consume(result);
    }

    // ==================== FastMath vs Math ====================

    @Benchmark
    public void mathSin(Blackhole bh) {
        float result = (float) Math.sin(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathSin(Blackhole bh) {
        float result = FastMath.sin(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void mathCos(Blackhole bh) {
        float result = (float) Math.cos(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathCos(Blackhole bh) {
        float result = FastMath.cos(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void mathSqrt(Blackhole bh) {
        float result = (float) Math.sqrt(randomFloat + 1);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathSqrt(Blackhole bh) {
        float result = FastMath.sqrt(randomFloat + 1);
        bh.consume(result);
    }

    @Benchmark
    public void mathExp(Blackhole bh) {
        float result = (float) Math.exp(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathExp(Blackhole bh) {
        float result = FastMath.exp(randomFloat);
        bh.consume(result);
    }

    @Benchmark
    public void mathLog(Blackhole bh) {
        float result = (float) Math.log(randomFloat + 1);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathLog(Blackhole bh) {
        float result = FastMath.log(randomFloat + 1);
        bh.consume(result);
    }

    @Benchmark
    public void mathPow(Blackhole bh) {
        float result = (float) Math.pow(randomFloat + 1, 2.5);
        bh.consume(result);
    }

    @Benchmark
    public void fastMathPow(Blackhole bh) {
        float result = FastMath.pow(randomFloat + 1, 2.5f);
        bh.consume(result);
    }

    // ==================== Array Trig Functions ====================

    @Benchmark
    public void mathSinArray(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            floatResult[i] = (float) Math.sin(floatArrayA[i]);
        }
        bh.consume(floatResult);
    }

    @Benchmark
    public void fastMathSinArray(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            floatResult[i] = FastMath.sin(floatArrayA[i]);
        }
        bh.consume(floatResult);
    }

    // ==================== Matrix Operations ====================

    @Benchmark
    public void scalarMatrixVectorMultiply(Blackhole bh) {
        int n = 64;
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            float sum = 0;
            for (int j = 0; j < n; j++) {
                sum += matrixA[i * n + j] * vector[j];
            }
            result[i] = sum;
        }
        bh.consume(result);
    }

    @Benchmark
    public void vectorMatrixVectorMultiply(Blackhole bh) {
        float[] result = MatrixMath.multiplyVector(matrixA, vector, 64, 64);
        bh.consume(result);
    }

    @Benchmark
    public void scalarMatrixMultiply(Blackhole bh) {
        int n = 64;
        float[] result = new float[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                float sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += matrixA[i * n + k] * matrixB[k * n + j];
                }
                result[i * n + j] = sum;
            }
        }
        bh.consume(result);
    }

    @Benchmark
    public void vectorMatrixMultiply(Blackhole bh) {
        float[] result = MatrixMath.multiply(matrixA, matrixB, 64, 64, 64);
        bh.consume(result);
    }

    // ==================== VectorOps ====================

    @Benchmark
    public void vec3DotProduct(Blackhole bh) {
        VectorOps.Vec3 a = new VectorOps.Vec3(floatArrayA[0], floatArrayA[1], floatArrayA[2]);
        VectorOps.Vec3 b = new VectorOps.Vec3(floatArrayB[0], floatArrayB[1], floatArrayB[2]);
        float result = a.dot(b);
        bh.consume(result);
    }

    @Benchmark
    public void vec3CrossProduct(Blackhole bh) {
        VectorOps.Vec3 a = new VectorOps.Vec3(floatArrayA[0], floatArrayA[1], floatArrayA[2]);
        VectorOps.Vec3 b = new VectorOps.Vec3(floatArrayB[0], floatArrayB[1], floatArrayB[2]);
        VectorOps.Vec3 result = a.cross(b);
        bh.consume(result);
    }

    @Benchmark
    public void vec3Normalize(Blackhole bh) {
        VectorOps.Vec3 a = new VectorOps.Vec3(floatArrayA[0], floatArrayA[1], floatArrayA[2]);
        VectorOps.Vec3 result = a.normalize();
        bh.consume(result);
    }
}