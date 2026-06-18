package cn.tuyucheng.taketoday.fastmath;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FastMath library.
 */
class FastMathTest {

    private static final float EPSILON = 1e-5f;

    // ==================== VectorMath Tests ====================

    @Test
    void testVectorAdd() {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] b = {8, 7, 6, 5, 4, 3, 2, 1};
        float[] result = VectorMath.add(a, b);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] + b[i], result[i], EPSILON);
        }
    }

    @Test
    void testVectorAddScalar() {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        float scalar = 10.0f;
        float[] result = VectorMath.add(a, scalar);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] + scalar, result[i], EPSILON);
        }
    }

    @Test
    void testVectorSubtract() {
        float[] a = {10, 20, 30, 40, 50, 60, 70, 80};
        float[] b = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] result = VectorMath.subtract(a, b);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] - b[i], result[i], EPSILON);
        }
    }

    @Test
    void testVectorMultiply() {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        float[] b = {2, 2, 2, 2, 2, 2, 2, 2};
        float[] result = VectorMath.multiply(a, b);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] * b[i], result[i], EPSILON);
        }
    }

    @Test
    void testVectorMultiplyScalar() {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        float scalar = 3.0f;
        float[] result = VectorMath.multiply(a, scalar);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] * scalar, result[i], EPSILON);
        }
    }

    @Test
    void testVectorDivide() {
        float[] a = {2, 4, 6, 8, 10, 12, 14, 16};
        float[] b = {2, 2, 2, 2, 2, 2, 2, 2};
        float[] result = VectorMath.divide(a, b);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] / b[i], result[i], EPSILON);
        }
    }

    @Test
    void testDotProduct() {
        float[] a = {1, 2, 3, 4};
        float[] b = {4, 3, 2, 1};
        float expected = 1*4 + 2*3 + 3*2 + 4*1;
        assertEquals(expected, VectorMath.dotProduct(a, b), EPSILON);
    }

    @Test
    void testSum() {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        float expected = 36;
        assertEquals(expected, VectorMath.sum(a), EPSILON);
    }

    @Test
    void testNorm() {
        float[] a = {3, 4};
        assertEquals(5.0f, VectorMath.norm(a), EPSILON);
    }

    @Test
    void testNormalize() {
        float[] a = {3, 4};
        float[] normalized = VectorMath.normalize(a);
        assertEquals(1.0f, VectorMath.norm(normalized), EPSILON);
    }

    @Test
    void testDistance() {
        float[] a = {0, 0, 0};
        float[] b = {3, 4, 0};
        assertEquals(5.0f, VectorMath.distance(a, b), EPSILON);
    }

    @Test
    void testDistanceSquared() {
        float[] a = {0, 0, 0};
        float[] b = {3, 4, 0};
        assertEquals(25.0f, VectorMath.distanceSquared(a, b), EPSILON);
    }

    @Test
    void testMin() {
        float[] a = {5, 3, 8, 1, 9, 2, 7, 4};
        assertEquals(1.0f, VectorMath.min(a), EPSILON);
    }

    @Test
    void testMax() {
        float[] a = {5, 3, 8, 1, 9, 2, 7, 4};
        assertEquals(9.0f, VectorMath.max(a), EPSILON);
    }

    @Test
    void testSqrt() {
        float[] a = {1, 4, 9, 16, 25};
        float[] result = VectorMath.sqrt(a);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(Math.sqrt(a[i]), result[i], EPSILON);
        }
    }

    @Test
    void testNegate() {
        float[] a = {1, -2, 3, -4, 5, -6, 7, -8};
        float[] result = VectorMath.negate(a);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(-a[i], result[i], EPSILON);
        }
    }

    @Test
    void testAbs() {
        float[] a = {-1, 2, -3, 4, -5, 6, -7, 8};
        float[] result = VectorMath.abs(a);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(Math.abs(a[i]), result[i], EPSILON);
        }
    }

    @Test
    void testClamp() {
        float[] a = {-5, -1, 0, 1, 5, 10, 15, 20};
        float[] result = VectorMath.clamp(a, 0, 10);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(Math.max(0, Math.min(10, a[i])), result[i], EPSILON);
        }
    }

    @Test
    void testLerp() {
        float[] a = {0, 0, 0, 0};
        float[] b = {10, 10, 10, 10};
        float[] result = VectorMath.lerp(a, b, 0.5f);
        
        for (int i = 0; i < a.length; i++) {
            assertEquals(5.0f, result[i], EPSILON);
        }
    }

    @Test
    void testDoubleVectorOperations() {
        double[] a = {1, 2, 3, 4};
        double[] b = {4, 3, 2, 1};
        
        double[] sum = VectorMath.add(a, b);
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i] + b[i], sum[i], EPSILON);
        }
        
        double dot = VectorMath.dotProduct(a, b);
        assertEquals(20.0, dot, EPSILON);  // 1*4 + 2*3 + 3*2 + 4*1 = 20
    }

    @Test
    void testMismatchedLengths() {
        float[] a = {1, 2, 3};
        float[] b = {1, 2};
        
        assertThrows(IllegalArgumentException.class, () -> VectorMath.add(a, b));
        assertThrows(IllegalArgumentException.class, () -> VectorMath.dotProduct(a, b));
    }

    // ==================== FastMath Tests ====================

    @Test
    void testFastSin() {
        float angle = (float) (Math.PI / 4);
        float expected = (float) Math.sin(angle);
        float actual = FastMath.sin(angle);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastCos() {
        float angle = (float) (Math.PI / 4);
        float expected = (float) Math.cos(angle);
        float actual = FastMath.cos(angle);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastTan() {
        float angle = (float) (Math.PI / 4);
        float expected = (float) Math.tan(angle);
        float actual = FastMath.tan(angle);
        assertEquals(expected, actual, 0.01f);
    }

    @Test
    void testFastAsin() {
        float value = 0.5f;
        float expected = (float) Math.asin(value);
        float actual = FastMath.asin(value);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastAcos() {
        float value = 0.5f;
        float expected = (float) Math.acos(value);
        float actual = FastMath.acos(value);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastAtan() {
        float value = 1.0f;
        float expected = (float) Math.atan(value);
        float actual = FastMath.atan(value);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastAtan2() {
        float y = 1.0f;
        float x = 1.0f;
        float expected = (float) Math.atan2(y, x);
        float actual = FastMath.atan2(y, x);
        assertEquals(expected, actual, 0.001f);
    }

    @Test
    void testFastExp() {
        float x = 2.0f;
        float expected = (float) Math.exp(x);
        float actual = FastMath.exp(x);
        // FastMath.exp is approximate, allow ~2% error
        assertEquals(expected, actual, 0.02f * expected);
    }

    @Test
    void testFastLog() {
        float x = 10.0f;
        float expected = (float) Math.log(x);
        float actual = FastMath.log(x);
        assertEquals(expected, actual, 0.01f);
    }

    @Test
    void testFastPow() {
        float base = 2.0f;
        float exp = 10.0f;
        float expected = (float) Math.pow(base, exp);
        float actual = FastMath.pow(base, exp);
        assertEquals(expected, actual, 0.01f * expected);
    }

    @Test
    void testFastSqrt() {
        float x = 16.0f;
        assertEquals(4.0f, FastMath.sqrt(x), EPSILON);
        
        x = 2.0f;
        assertEquals(Math.sqrt(2), FastMath.sqrt(x), 0.001);
    }

    @Test
    void testFastInvSqrt() {
        float x = 4.0f;
        assertEquals(0.5f, FastMath.invSqrt(x), 0.001f);
        
        x = 1.0f;
        assertEquals(1.0f, FastMath.invSqrt(x), 0.001f);
    }

    @Test
    void testFastMathAbs() {
        assertEquals(5.0f, FastMath.abs(-5.0f), EPSILON);
        assertEquals(5.0f, FastMath.abs(5.0f), EPSILON);
        assertEquals(5.0, FastMath.abs(-5.0), EPSILON);
    }

    @Test
    void testFastMathFloor() {
        assertEquals(3.0f, FastMath.floor(3.7f), EPSILON);
        assertEquals(-4.0f, FastMath.floor(-3.7f), EPSILON);
    }

    @Test
    void testFastMathCeil() {
        assertEquals(4.0f, FastMath.ceil(3.2f), EPSILON);
        assertEquals(-3.0f, FastMath.ceil(-3.2f), EPSILON);
    }

    @Test
    void testFastMathRound() {
        assertEquals(4.0f, FastMath.round(3.5f), EPSILON);
        assertEquals(3.0f, FastMath.round(3.4f), EPSILON);
    }

    @Test
    void testFastMathMinMax() {
        assertEquals(2.0f, FastMath.min(2.0f, 5.0f), EPSILON);
        assertEquals(5.0f, FastMath.max(2.0f, 5.0f), EPSILON);
    }

    @Test
    void testFastMathClamp() {
        assertEquals(0.0f, FastMath.clamp(-5.0f, 0.0f, 10.0f), EPSILON);
        assertEquals(10.0f, FastMath.clamp(15.0f, 0.0f, 10.0f), EPSILON);
        assertEquals(5.0f, FastMath.clamp(5.0f, 0.0f, 10.0f), EPSILON);
    }

    @Test
    void testFastMathLerp() {
        assertEquals(5.0f, FastMath.lerp(0.0f, 10.0f, 0.5f), EPSILON);
        assertEquals(0.0f, FastMath.lerp(0.0f, 10.0f, 0.0f), EPSILON);
        assertEquals(10.0f, FastMath.lerp(0.0f, 10.0f, 1.0f), EPSILON);
    }

    @Test
    void testSmoothstep() {
        assertEquals(0.0f, FastMath.smoothstep(0.0f, 10.0f, -1.0f), EPSILON);
        assertEquals(1.0f, FastMath.smoothstep(0.0f, 10.0f, 11.0f), EPSILON);
    }

    // ==================== MatrixMath Tests ====================

    @Test
    void testMatrixZeros() {
        float[] m = MatrixMath.zeros(3, 3);
        assertEquals(9, m.length);
        for (float v : m) {
            assertEquals(0.0f, v, EPSILON);
        }
    }

    @Test
    void testMatrixIdentity() {
        float[] m = MatrixMath.identity(3);
        assertEquals(1.0f, m[0], EPSILON);
        assertEquals(1.0f, m[4], EPSILON);
        assertEquals(1.0f, m[8], EPSILON);
        assertEquals(0.0f, m[1], EPSILON);
        assertEquals(0.0f, m[3], EPSILON);
    }

    @Test
    void testMatrixFilled() {
        float[] m = MatrixMath.filled(2, 3, 5.0f);
        for (float v : m) {
            assertEquals(5.0f, v, EPSILON);
        }
    }

    @Test
    void testMatrixDiagonal() {
        float[] diag = {1, 2, 3};
        float[] m = MatrixMath.diagonal(diag);
        assertEquals(1.0f, m[0], EPSILON);
        assertEquals(2.0f, m[4], EPSILON);
        assertEquals(3.0f, m[8], EPSILON);
    }

    @Test
    void testMatrixMultiply() {
        float[] a = {
            1, 2,
            3, 4
        };
        float[] b = {
            5, 6,
            7, 8
        };
        float[] result = MatrixMath.multiply(a, b, 2, 2, 2);
        
        // [1 2]   [5 6]   [19 22]
        // [3 4] * [7 8] = [43 50]
        
        assertEquals(19.0f, result[0], EPSILON);
        assertEquals(22.0f, result[1], EPSILON);
        assertEquals(43.0f, result[2], EPSILON);
        assertEquals(50.0f, result[3], EPSILON);
    }

    @Test
    void testMatrixVectorMultiply() {
        float[] m = {
            1, 2, 3,
            4, 5, 6
        };
        float[] v = {1, 1, 1};
        float[] result = MatrixMath.multiplyVector(m, v, 2, 3);
        
        assertEquals(6.0f, result[0], EPSILON);  // 1*1 + 2*1 + 3*1
        assertEquals(15.0f, result[1], EPSILON); // 4*1 + 5*1 + 6*1
    }

    @Test
    void testMatrixTranspose() {
        float[] m = {
            1, 2, 3,
            4, 5, 6
        };
        float[] t = MatrixMath.transpose(m, 2, 3);
        
        assertEquals(1.0f, t[0], EPSILON);
        assertEquals(4.0f, t[1], EPSILON);
        assertEquals(2.0f, t[2], EPSILON);
        assertEquals(5.0f, t[3], EPSILON);
        assertEquals(3.0f, t[4], EPSILON);
        assertEquals(6.0f, t[5], EPSILON);
    }

    @Test
    void testMatrixTrace() {
        float[] m = {
            1, 2, 3,
            4, 5, 6,
            7, 8, 9
        };
        assertEquals(15.0f, MatrixMath.trace(m, 3), EPSILON); // 1 + 5 + 9
    }

    @Test
    void testDeterminant2x2() {
        float[] m = {1, 2, 3, 4};
        assertEquals(-2.0f, MatrixMath.determinant2x2(m), EPSILON);
    }

    @Test
    void testDeterminant3x3() {
        float[] m = {
            1, 2, 3,
            0, 1, 4,
            5, 6, 0
        };
        assertEquals(1.0f, MatrixMath.determinant3x3(m), EPSILON);
    }

    @Test
    void testInverse2x2() {
        float[] m = {1, 2, 3, 4};
        float[] inv = MatrixMath.inverse2x2(m);
        float[] product = MatrixMath.multiply(m, inv, 2, 2, 2);
        
        // A * A^-1 = I
        assertEquals(1.0f, product[0], EPSILON);
        assertEquals(0.0f, product[1], EPSILON);
        assertEquals(0.0f, product[2], EPSILON);
        assertEquals(1.0f, product[3], EPSILON);
    }

    @Test
    void testFrobeniusNorm() {
        float[] m = {
            1, 2,
            3, 4
        };
        // sqrt(1 + 4 + 9 + 16) = sqrt(30)
        assertEquals(Math.sqrt(30), MatrixMath.frobeniusNorm(m, 2, 2), EPSILON);
    }

    // ==================== VectorOps Tests ====================

    @Test
    void testVec2Operations() {
        VectorOps.Vec2 a = new VectorOps.Vec2(3, 4);
        VectorOps.Vec2 b = new VectorOps.Vec2(1, 2);
        
        assertEquals(new VectorOps.Vec2(4, 6), a.add(b));
        assertEquals(new VectorOps.Vec2(2, 2), a.subtract(b));
        assertEquals(new VectorOps.Vec2(6, 8), a.multiply(2));
        assertEquals(5.0f, a.length(), EPSILON);
        assertEquals(25.0f, a.lengthSquared(), EPSILON);
        assertEquals(1.0f, a.normalize().length(), EPSILON);
        assertEquals(11.0f, a.dot(b), EPSILON);
        assertEquals(new VectorOps.Vec2(-3, -4), a.negate());
    }

    @Test
    void testVec3Operations() {
        VectorOps.Vec3 a = new VectorOps.Vec3(1, 2, 3);
        VectorOps.Vec3 b = new VectorOps.Vec3(4, 5, 6);
        
        assertEquals(new VectorOps.Vec3(5, 7, 9), a.add(b));
        assertEquals(new VectorOps.Vec3(-3, -3, -3), a.subtract(b));
        assertEquals(new VectorOps.Vec3(2, 4, 6), a.multiply(2));
        
        VectorOps.Vec3 cross = a.cross(b);
        // [1,2,3] x [4,5,6] = [-3, 6, -3]
        assertEquals(-3.0f, cross.x, EPSILON);
        assertEquals(6.0f, cross.y, EPSILON);
        assertEquals(-3.0f, cross.z, EPSILON);
        
        assertEquals(32.0f, a.dot(b), EPSILON);
        assertEquals((float) Math.sqrt(14), a.length(), EPSILON);
    }

    @Test
    void testVec4Operations() {
        VectorOps.Vec4 a = new VectorOps.Vec4(1, 2, 3, 4);
        VectorOps.Vec4 b = new VectorOps.Vec4(4, 3, 2, 1);
        
        assertEquals(new VectorOps.Vec4(5, 5, 5, 5), a.add(b));
        assertEquals(20.0f, a.dot(b), EPSILON);
        assertEquals((float) Math.sqrt(30), a.length(), EPSILON);
    }

    @Test
    void testVec3Reflection() {
        VectorOps.Vec3 v = new VectorOps.Vec3(1, -1, 0);
        VectorOps.Vec3 n = new VectorOps.Vec3(0, 1, 0);
        VectorOps.Vec3 reflected = v.reflect(n);
        
        assertEquals(1.0f, reflected.x, EPSILON);
        assertEquals(1.0f, reflected.y, EPSILON);
        assertEquals(0.0f, reflected.z, EPSILON);
    }
}