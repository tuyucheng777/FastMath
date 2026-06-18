package cn.tuyucheng.taketoday.fastmath;

/// Simple immutable vector classes for 2D, 3D, and 4D vectors.
/// These are convenient for graphics, physics, and geometry applications.
///
/// All operations return new vector instances (immutable pattern).
public final class VectorOps {

    private VectorOps() {}

    // ==================== Vec2 (2D Vector) ====================

    /// Immutable 2D vector with float precision.
    ///
    /// All operations return new Vec2 instances (immutable pattern).
    public static final class Vec2 {
        /// X component of the vector.
        public final float x;
        /// Y component of the vector.
        public final float y;

        /// Creates a 2D vector with the given components.
        ///
        /// @param x the x component
        /// @param y the y component
        public Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        /// Creates a zero 2D vector (0, 0).
        public Vec2() {
            this(0, 0);
        }

        /// Creates a 2D vector from a float array.
        ///
        /// @param arr array containing `[x, y]`; length must be at least 2
        public Vec2(float[] arr) {
            this(arr[0], arr[1]);
        }

        /// Returns the components as a float array `[x, y]`.
        ///
        /// @return a two-element float array containing `[x, y]`
        public float[] toArray() {
            return new float[] { x, y };
        }

        /// Adds another vector to this one.
        ///
        /// @param v the vector to add
        /// @return a new Vec2 representing the element-wise sum
        public Vec2 add(Vec2 v) {
            return new Vec2(x + v.x, y + v.y);
        }

        /// Subtracts another vector from this one.
        ///
        /// @param v the vector to subtract
        /// @return a new Vec2 representing the element-wise difference
        public Vec2 subtract(Vec2 v) {
            return new Vec2(x - v.x, y - v.y);
        }

        /// Multiplies this vector by a scalar.
        ///
        /// @param s the scalar multiplier
        /// @return a new Vec2 with each component scaled by `s`
        public Vec2 multiply(float s) {
            return new Vec2(x * s, y * s);
        }

        /// Divides this vector by a scalar.
        ///
        /// @param s the scalar divisor; must not be zero
        /// @return a new Vec2 with each component divided by `s`
        public Vec2 divide(float s) {
            return new Vec2(x / s, y / s);
        }

        /// Computes the dot product with another vector.
        ///
        /// @param v the other vector
        /// @return the dot product value, `x * v.x + y * v.y`
        public float dot(Vec2 v) {
            return x * v.x + y * v.y;
        }

        /// Returns the length (magnitude) of this vector.
        ///
        /// @return `√(x² + y²)`
        public float length() {
            return (float) Math.sqrt(x * x + y * y);
        }

        /// Returns the squared length of this vector (avoids sqrt).
        ///
        /// @return `x² + y²`
        public float lengthSquared() {
            return x * x + y * y;
        }

        /// Returns a normalized (unit length) version of this vector.
        /// Returns a zero vector if the length is zero.
        ///
        /// @return this vector normalized to unit length, or zero vector if length is zero
        public Vec2 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec2();
        }

        /// Computes the Euclidean distance to another vector.
        ///
        /// @param v the other vector
        /// @return the Euclidean distance between the two vectors
        public float distance(Vec2 v) {
            return subtract(v).length();
        }

        /// Computes the squared Euclidean distance to another vector (avoids sqrt).
        ///
        /// @param v the other vector
        /// @return the squared distance
        public float distanceSquared(Vec2 v) {
            return subtract(v).lengthSquared();
        }

        /// Returns the negation of this vector.
        ///
        /// @return a new Vec2 with all components negated
        public Vec2 negate() {
            return new Vec2(-x, -y);
        }

        /// Linear interpolation between this vector and another.
        ///
        /// @param v the target vector
        /// @param t interpolation factor `[0, 1]`; 0 returns this, 1 returns `v`
        /// @return interpolated vector: `this + t * (v - this)`
        public Vec2 lerp(Vec2 v, float t) {
            return new Vec2(
                x + t * (v.x - x),
                y + t * (v.y - y)
            );
        }

        /// 2D cross product (returns scalar z-component).
        ///
        /// @param v the other vector
        /// @return the z-component of the 3D cross product, `x * v.y - y * v.x`
        public float cross(Vec2 v) {
            return x * v.y - y * v.x;
        }

        /// Perpendicular vector (rotated 90 degrees counter-clockwise).
        ///
        /// @return a new Vec2 rotated 90° counter-clockwise: `(-y, x)`
        public Vec2 perpendicular() {
            return new Vec2(-y, x);
        }

        /// Angle between this vector and the x-axis.
        ///
        /// @return the angle in radians, `atan2(y, x)`
        public float angle() {
            return (float) Math.atan2(y, x);
        }

        /// Angle between two vectors.
        ///
        /// @param v the other vector
        /// @return the angle in radians between this vector and `v`
        public float angleBetween(Vec2 v) {
            return (float) Math.acos(dot(v) / (length() * v.length()));
        }

        /// Creates a unit vector from an angle.
        ///
        /// @param angle the angle in radians
        /// @return a unit vector pointing in the given direction
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

    /// Immutable 3D vector with float precision.
    ///
    /// All operations return new Vec3 instances (immutable pattern).
    public static final class Vec3 {
        /// X component of the vector.
        public final float x;
        /// Y component of the vector.
        public final float y;
        /// Z component of the vector.
        public final float z;

        /// Creates a 3D vector with the given components.
        ///
        /// @param x the x component
        /// @param y the y component
        /// @param z the z component
        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /// Creates a zero 3D vector (0, 0, 0).
        public Vec3() {
            this(0, 0, 0);
        }

        /// Creates a 3D vector from a float array.
        ///
        /// @param arr array containing `[x, y, z]`; length must be at least 3
        public Vec3(float[] arr) {
            this(arr[0], arr[1], arr[2]);
        }

        /// Returns the components as a float array `[x, y, z]`.
        ///
        /// @return a three-element float array containing `[x, y, z]`
        public float[] toArray() {
            return new float[] { x, y, z };
        }

        /// Adds another vector to this one.
        ///
        /// @param v the vector to add
        /// @return a new Vec3 representing the element-wise sum
        public Vec3 add(Vec3 v) {
            return new Vec3(x + v.x, y + v.y, z + v.z);
        }

        /// Subtracts another vector from this one.
        ///
        /// @param v the vector to subtract
        /// @return a new Vec3 representing the element-wise difference
        public Vec3 subtract(Vec3 v) {
            return new Vec3(x - v.x, y - v.y, z - v.z);
        }

        /// Multiplies this vector by a scalar.
        ///
        /// @param s the scalar multiplier
        /// @return a new Vec3 with each component scaled by `s`
        public Vec3 multiply(float s) {
            return new Vec3(x * s, y * s, z * s);
        }

        /// Element-wise multiplication with another vector.
        ///
        /// @param v the vector to multiply element-wise
        /// @return a new Vec3 with each component multiplied
        public Vec3 multiply(Vec3 v) {
            return new Vec3(x * v.x, y * v.y, z * v.z);
        }

        /// Divides this vector by a scalar.
        ///
        /// @param s the scalar divisor; must not be zero
        /// @return a new Vec3 with each component divided by `s`
        public Vec3 divide(float s) {
            return new Vec3(x / s, y / s, z / s);
        }

        /// Computes the dot product with another vector.
        ///
        /// @param v the other vector
        /// @return the dot product value, `x * v.x + y * v.y + z * v.z`
        public float dot(Vec3 v) {
            return x * v.x + y * v.y + z * v.z;
        }

        /// Computes the cross product with another vector.
        ///
        /// @param v the other vector
        /// @return a new Vec3 representing the cross product
        public Vec3 cross(Vec3 v) {
            return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
            );
        }

        /// Returns the length (magnitude) of this vector.
        ///
        /// @return `√(x² + y² + z²)`
        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        /// Returns the squared length of this vector (avoids sqrt).
        ///
        /// @return `x² + y² + z²`
        public float lengthSquared() {
            return x * x + y * y + z * z;
        }

        /// Returns a normalized (unit length) version of this vector.
        /// Returns a zero vector if the length is zero.
        ///
        /// @return this vector normalized to unit length, or zero vector if length is zero
        public Vec3 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec3();
        }

        /// Computes the Euclidean distance to another vector.
        ///
        /// @param v the other vector
        /// @return the Euclidean distance between the two vectors
        public float distance(Vec3 v) {
            return subtract(v).length();
        }

        /// Computes the squared Euclidean distance to another vector (avoids sqrt).
        ///
        /// @param v the other vector
        /// @return the squared distance
        public float distanceSquared(Vec3 v) {
            return subtract(v).lengthSquared();
        }

        /// Returns the negation of this vector.
        ///
        /// @return a new Vec3 with all components negated
        public Vec3 negate() {
            return new Vec3(-x, -y, -z);
        }

        /// Linear interpolation between this vector and another.
        ///
        /// @param v the target vector
        /// @param t interpolation factor `[0, 1]`; 0 returns this, 1 returns `v`
        /// @return interpolated vector: `this + t * (v - this)`
        public Vec3 lerp(Vec3 v, float t) {
            return new Vec3(
                x + t * (v.x - x),
                y + t * (v.y - y),
                z + t * (v.z - z)
            );
        }

        /// Reflect this vector around a normal.
        ///
        /// @param normal the surface normal vector; should be unit length
        /// @return the reflected vector
        public Vec3 reflect(Vec3 normal) {
            return subtract(normal.multiply(2.0f * dot(normal)));
        }

        /// Angle between two vectors.
        ///
        /// @param v the other vector
        /// @return the angle in radians between this vector and `v`
        public float angleBetween(Vec3 v) {
            float d = dot(v) / (length() * v.length());
            return (float) Math.acos(FastMath.clamp(d, -1.0f, 1.0f));
        }

        /// Project this vector onto another vector.
        ///
        /// @param v the vector to project onto
        /// @return the projection of this vector onto `v`
        public Vec3 project(Vec3 v) {
            return v.multiply(dot(v) / v.lengthSquared());
        }

        /// Returns a Vec2 with x and y components.
        ///
        /// @return a new Vec2 containing just the x and y components
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

    /// Immutable 4D vector with float precision.
    ///
    /// All operations return new Vec4 instances (immutable pattern).
    public static final class Vec4 {
        /// X component of the vector.
        public final float x;
        /// Y component of the vector.
        public final float y;
        /// Z component of the vector.
        public final float z;
        /// W component of the vector.
        public final float w;

        /// Creates a 4D vector with the given components.
        ///
        /// @param x the x component
        /// @param y the y component
        /// @param z the z component
        /// @param w the w component
        public Vec4(float x, float y, float z, float w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        /// Creates a zero 4D vector (0, 0, 0, 0).
        public Vec4() {
            this(0, 0, 0, 0);
        }

        /// Creates a 4D vector from a float array.
        ///
        /// @param arr array containing `[x, y, z, w]`; length must be at least 4
        public Vec4(float[] arr) {
            this(arr[0], arr[1], arr[2], arr[3]);
        }

        /// Creates a 4D vector from a 3D vector and a w component.
        ///
        /// @param v the 3D vector providing x, y, z components
        /// @param w the w component (homogeneous coordinate)
        public Vec4(Vec3 v, float w) {
            this(v.x, v.y, v.z, w);
        }

        /// Returns the components as a float array `[x, y, z, w]`.
        ///
        /// @return a four-element float array containing `[x, y, z, w]`
        public float[] toArray() {
            return new float[] { x, y, z, w };
        }

        /// Adds another vector to this one.
        ///
        /// @param v the vector to add
        /// @return a new Vec4 representing the element-wise sum
        public Vec4 add(Vec4 v) {
            return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
        }

        /// Subtracts another vector from this one.
        ///
        /// @param v the vector to subtract
        /// @return a new Vec4 representing the element-wise difference
        public Vec4 subtract(Vec4 v) {
            return new Vec4(x - v.x, y - v.y, z - v.z, w - v.w);
        }

        /// Multiplies this vector by a scalar.
        ///
        /// @param s the scalar multiplier
        /// @return a new Vec4 with each component scaled by `s`
        public Vec4 multiply(float s) {
            return new Vec4(x * s, y * s, z * s, w * s);
        }

        /// Computes the dot product with another vector.
        ///
        /// @param v the other vector
        /// @return the dot product value, `x * v.x + y * v.y + z * v.z + w * v.w`
        public float dot(Vec4 v) {
            return x * v.x + y * v.y + z * v.z + w * v.w;
        }

        /// Returns the length (magnitude) of this vector.
        ///
        /// @return `√(x² + y² + z² + w²)`
        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z + w * w);
        }

        /// Returns the squared length of this vector (avoids sqrt).
        ///
        /// @return `x² + y² + z² + w²`
        public float lengthSquared() {
            return x * x + y * y + z * z + w * w;
        }

        /// Returns a normalized (unit length) version of this vector.
        /// Returns a zero vector if the length is zero.
        ///
        /// @return this vector normalized to unit length, or zero vector if length is zero
        public Vec4 normalize() {
            float len = length();
            return len > 0 ? divide(len) : new Vec4();
        }

        /// Divides this vector by a scalar (internal use).
        ///
        /// @param s the scalar divisor; must not be zero
        /// @return a new Vec4 with each component divided by `s`
        private Vec4 divide(float s) {
            return new Vec4(x / s, y / s, z / s, w / s);
        }

        /// Returns a Vec3 with x, y, z components (homogeneous division by w).
        ///
        /// @return a new Vec3 with `x/w`, `y/w`, `z/w`
        public Vec3 xyz() {
            return new Vec3(x / w, y / w, z / w);
        }

        /// Returns a Vec3 without w component (no homogeneous division).
        ///
        /// @return a new Vec3 with `x`, `y`, `z` values unchanged
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
