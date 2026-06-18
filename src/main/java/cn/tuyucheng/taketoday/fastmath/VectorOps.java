package cn.tuyucheng.taketoday.fastmath;

/**
 * Simple immutable vector classes for 2D, 3D, and 4D vectors.
 * These are convenient for graphics, physics, and geometry applications.
 * 
 * <p>All operations return new vector instances (immutable pattern).
 */
public final class VectorOps {

    private VectorOps() {}

    // ==================== Vec2 (2D Vector) ====================

    public static final class Vec2 {
        public final float x, y;

        public Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vec2() {
            this(0, 0);
        }

        public Vec2(float[] arr) {
            this(arr[0], arr[1]);
        }

        public float[] toArray() {
            return new float[] { x, y };
        }

        public Vec2 add(Vec2 v) {
            return new Vec2(x + v.x, y + v.y);
        }

        public Vec2 subtract(Vec2 v) {
            return new Vec2(x - v.x, y - v.y);
        }

        public Vec2 multiply(float s) {
            return new Vec2(x * s, y * s);
        }

        public Vec2 divide(float s) {
            return new Vec2(x / s, y / s);
        }

        public float dot(Vec2 v) {
            return x * v.x + y * v.y;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y);
        }

        public float lengthSquared() {
            return x * x + y * y;
        }

        public Vec2 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec2();
        }

        public float distance(Vec2 v) {
            return subtract(v).length();
        }

        public float distanceSquared(Vec2 v) {
            return subtract(v).lengthSquared();
        }

        public Vec2 negate() {
            return new Vec2(-x, -y);
        }

        public Vec2 lerp(Vec2 v, float t) {
            return new Vec2(
                x + t * (v.x - x),
                y + t * (v.y - y)
            );
        }

        /**
         * 2D cross product (returns scalar z-component).
         */
        public float cross(Vec2 v) {
            return x * v.y - y * v.x;
        }

        /**
         * Perpendicular vector (rotated 90 degrees counter-clockwise).
         */
        public Vec2 perpendicular() {
            return new Vec2(-y, x);
        }

        /**
         * Angle between this vector and the x-axis.
         */
        public float angle() {
            return (float) Math.atan2(y, x);
        }

        /**
         * Angle between two vectors.
         */
        public float angleBetween(Vec2 v) {
            return (float) Math.acos(dot(v) / (length() * v.length()));
        }

        public static Vec2 fromAngle(float angle) {
            return new Vec2((float) Math.cos(angle), (float) Math.sin(angle));
        }

        @Override
        public String toString() {
            return String.format("Vec2(%.4f, %.4f)", x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vec2 v)) return false;
            return Float.compare(x, v.x) == 0 && Float.compare(y, v.y) == 0;
        }

        @Override
        public int hashCode() {
            return 31 * Float.floatToIntBits(x) + Float.floatToIntBits(y);
        }
    }

    // ==================== Vec3 (3D Vector) ====================

    public static final class Vec3 {
        public final float x, y, z;

        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3() {
            this(0, 0, 0);
        }

        public Vec3(float[] arr) {
            this(arr[0], arr[1], arr[2]);
        }

        public float[] toArray() {
            return new float[] { x, y, z };
        }

        public Vec3 add(Vec3 v) {
            return new Vec3(x + v.x, y + v.y, z + v.z);
        }

        public Vec3 subtract(Vec3 v) {
            return new Vec3(x - v.x, y - v.y, z - v.z);
        }

        public Vec3 multiply(float s) {
            return new Vec3(x * s, y * s, z * s);
        }

        public Vec3 multiply(Vec3 v) {
            return new Vec3(x * v.x, y * v.y, z * v.z);
        }

        public Vec3 divide(float s) {
            return new Vec3(x / s, y / s, z / s);
        }

        public float dot(Vec3 v) {
            return x * v.x + y * v.y + z * v.z;
        }

        public Vec3 cross(Vec3 v) {
            return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
            );
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public float lengthSquared() {
            return x * x + y * y + z * z;
        }

        public Vec3 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec3();
        }

        public float distance(Vec3 v) {
            return subtract(v).length();
        }

        public float distanceSquared(Vec3 v) {
            return subtract(v).lengthSquared();
        }

        public Vec3 negate() {
            return new Vec3(-x, -y, -z);
        }

        public Vec3 lerp(Vec3 v, float t) {
            return new Vec3(
                x + t * (v.x - x),
                y + t * (v.y - y),
                z + t * (v.z - z)
            );
        }

        /**
         * Reflect this vector around a normal.
         */
        public Vec3 reflect(Vec3 normal) {
            return subtract(normal.multiply(2.0f * dot(normal)));
        }

        /**
         * Angle between two vectors.
         */
        public float angleBetween(Vec3 v) {
            float d = dot(v) / (length() * v.length());
            return (float) Math.acos(FastMath.clamp(d, -1.0f, 1.0f));
        }

        /**
         * Project this vector onto another vector.
         */
        public Vec3 project(Vec3 v) {
            return v.multiply(dot(v) / v.lengthSquared());
        }

        /**
         * Returns a Vec2 with x and y components.
         */
        public Vec2 xy() {
            return new Vec2(x, y);
        }

        @Override
        public String toString() {
            return String.format("Vec3(%.4f, %.4f, %.4f)", x, y, z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vec3 v)) return false;
            return Float.compare(x, v.x) == 0 &&
                   Float.compare(y, v.y) == 0 && 
                   Float.compare(z, v.z) == 0;
        }

        @Override
        public int hashCode() {
            int result = Float.floatToIntBits(x);
            result = 31 * result + Float.floatToIntBits(y);
            result = 31 * result + Float.floatToIntBits(z);
            return result;
        }
    }

    // ==================== Vec4 (4D Vector) ====================

    public static final class Vec4 {
        public final float x, y, z, w;

        public Vec4(float x, float y, float z, float w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public Vec4() {
            this(0, 0, 0, 0);
        }

        public Vec4(float[] arr) {
            this(arr[0], arr[1], arr[2], arr[3]);
        }

        public Vec4(Vec3 v, float w) {
            this(v.x, v.y, v.z, w);
        }

        public float[] toArray() {
            return new float[] { x, y, z, w };
        }

        public Vec4 add(Vec4 v) {
            return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
        }

        public Vec4 subtract(Vec4 v) {
            return new Vec4(x - v.x, y - v.y, z - v.z, w - v.w);
        }

        public Vec4 multiply(float s) {
            return new Vec4(x * s, y * s, z * s, w * s);
        }

        public float dot(Vec4 v) {
            return x * v.x + y * v.y + z * v.z + w * v.w;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z + w * w);
        }

        public float lengthSquared() {
            return x * x + y * y + z * z + w * w;
        }

        public Vec4 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec4();
        }

        private Vec4 divide(float s) {
            return new Vec4(x / s, y / s, z / s, w / s);
        }

        /**
         * Returns a Vec3 with x, y, z components (homogeneous division).
         */
        public Vec3 xyz() {
            return new Vec3(x / w, y / w, z / w);
        }

        /**
         * Returns a Vec3 without w component.
         */
        public Vec3 xyzNoDivide() {
            return new Vec3(x, y, z);
        }

        @Override
        public String toString() {
            return String.format("Vec4(%.4f, %.4f, %.4f, %.4f)", x, y, z, w);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vec4 v)) return false;
            return Float.compare(x, v.x) == 0 &&
                   Float.compare(y, v.y) == 0 && 
                   Float.compare(z, v.z) == 0 && 
                   Float.compare(w, v.w) == 0;
        }

        @Override
        public int hashCode() {
            int result = Float.floatToIntBits(x);
            result = 31 * result + Float.floatToIntBits(y);
            result = 31 * result + Float.floatToIntBits(z);
            result = 31 * result + Float.floatToIntBits(w);
            return result;
        }
    }

    // ==================== Common Constants ====================

    public static final Vec2 VEC2_ZERO = new Vec2(0, 0);
    public static final Vec2 VEC2_UNIT_X = new Vec2(1, 0);
    public static final Vec2 VEC2_UNIT_Y = new Vec2(0, 1);

    public static final Vec3 VEC3_ZERO = new Vec3(0, 0, 0);
    public static final Vec3 VEC3_UNIT_X = new Vec3(1, 0, 0);
    public static final Vec3 VEC3_UNIT_Y = new Vec3(0, 1, 0);
    public static final Vec3 VEC3_UNIT_Z = new Vec3(0, 0, 1);

    public static final Vec4 VEC4_ZERO = new Vec4(0, 0, 0, 0);
    public static final Vec4 VEC4_UNIT_X = new Vec4(1, 0, 0, 0);
    public static final Vec4 VEC4_UNIT_Y = new Vec4(0, 1, 0, 0);
    public static final Vec4 VEC4_UNIT_Z = new Vec4(0, 0, 1, 0);
    public static final Vec4 VEC4_UNIT_W = new Vec4(0, 0, 0, 1);
}