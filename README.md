<div align="center">
  <img src="docs/img.png" alt="FastMath Logo" width="200" height="200"/>
  <h1>FastMath</h1>
  <p><strong>简单而强大的 Java SIMD 数学编程库</strong></p>

  <p>
    <a href="#"><img src="https://img.shields.io/badge/Java-26-blue.svg" alt="Java Version"/></a>
    <a href="#"><img src="https://img.shields.io/badge/Maven-3.8+-green.svg" alt="Maven"/></a>
    <a href="#"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/></a>
    <a href="#"><img src="https://img.shields.io/badge/JUnit-5.12.2-orange.svg" alt="JUnit"/></a>
  </p>
</div>

# FastMath

基于 Java Vector API (SIMD) 的高性能数学库，提供向量化的数组运算、快速标量数学函数、矩阵运算、复数运算和 FFT 功能。

## 特性

- **SIMD 加速数组运算** — 利用 Java Vector API 对 float/double 数组进行向量化处理，一条指令同时处理多个数据元素
- **快速标量数学函数** — 使用查找表和 IEEE 754 位操作的近似实现，以少量精度换取显著性能提升
- **矩阵运算** — 支持矩阵创建、加减乘、矩阵向量乘法、转置、行列式、逆矩阵等
- **复数运算** — 提供单精度 (`Complex`) 和双精度 (`ComplexD`) 不可变复数类型，以及基于交错存储格式的批量复数数组操作 (`ComplexMath`)
- **快速傅里叶变换** — 基于 Cooley-Tukey 算法的迭代原地 FFT 实现，支持实数 FFT (RFFT)、卷积和相关性计算
- **2D/3D/4D 向量** — 提供不可变的 `Vec2`、`Vec3`、`Vec4` 类型，适用于图形和物理计算
- **JMH 基准测试** — 内置性能基准测试框架，可对比标量实现与 SIMD 实现的性能差异

## 系统要求

- **JDK 26+**（Vector API 需要最新 JDK 版本）
- Maven 3.8+

## 快速开始

### 前置要求

- Java 26+
- Maven 3.8+

### 安装依赖

```xml
<dependency>
    <groupId>io.github.tuyucheng777</groupId>
    <artifactId>fastmath</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 模块概览

### VectorMath — SIMD 向量数组运算

```java
float[] a = {1, 2, 3, 4, 5, 6, 7, 8};
float[] b = {2, 2, 2, 2, 2, 2, 2, 2};

float[] sum      = VectorMath.add(a, b);        // 逐元素加法
float[] product  = VectorMath.multiply(a, b);   // 逐元素乘法
float  dot       = VectorMath.dotProduct(a, b);  // 点积
float  norm      = VectorMath.norm(a);           // L2 范数
float[] sqrtArr  = VectorMath.sqrt(a);           // 逐元素平方根
float[] normalized = VectorMath.normalize(a);    // 归一化
float  dist      = VectorMath.distance(a, b);    // 欧几里得距离
float[] clamped  = VectorMath.clamp(a, 0, 10);  // 范围裁剪
float[] lerped   = VectorMath.lerp(a, b, 0.5f); // 线性插值
```

支持的操作：`add`、`subtract`、`multiply`、`divide`、`dotProduct`、`sum`、`norm`、`normalize`、`distance`、`distanceSquared`、`min`、`max`、`sqrt`、`negate`、`abs`、`pow`、`clamp`、`lerp`。所有操作同时提供 float 和 double 版本。

### FastMath — 快速标量数学函数

```java
float sinVal  = FastMath.sin(0.785f);    // 查找表近似 sin
float cosVal  = FastMath.cos(0.785f);    // 查找表近似 cos
float expVal  = FastMath.exp(2.0f);      // IEEE 754 近似 e^x
float logVal  = FastMath.log(10.0f);     // IEEE 754 近似 ln(x)
float sqrtVal = FastMath.sqrt(16.0f);    // Newton-Raphson 近似 √x
float invSqrt = FastMath.invSqrt(4.0f);  // Quake III 快速逆平方根 1/√x
float angle   = FastMath.atan2(1.0f, 1.0f); // 快速 atan2
float clamped = FastMath.clamp(x, 0, 10);    // 范围裁剪
float lerped  = FastMath.lerp(a, b, 0.5f);   // 线性插值
```

三角函数：`sin`、`cos`、`tan`、`asin`、`acos`、`atan`、`atan2`
指数/对数：`exp`、`log`、`log10`、`log2`、`pow`
根/逆：`sqrt`、`invSqrt`
实用：`abs`、`floor`、`ceil`、`round`、`min`、`max`、`clamp`、`lerp`、`smoothstep`、`sign`

> 大多数函数的相对误差小于 1e-6 或更优。

### Complex / ComplexD — 复数

```java
Complex a = new Complex(3, 4);       // 3 + 4i
Complex b = Complex.fromPolar(5, 0.927f); // 5 * e^(i*0.927)

Complex c = a.add(b);                // 加法
Complex d = a.multiply(b);           // 乘法
Complex e = a.conjugate();           // 3 - 4i
Complex f = a.sqrt();                // 平方根
Complex g = a.exp();                 // e^(a+bi)
Complex h = a.pow(3);                // 快速幂（整数指数）

float magnitude = a.abs();          // 5.0
float phase     = a.phase();        // 0.927 rad
```

`ComplexD` 提供相同的操作，使用 double 精度。

### ComplexMath — 批量复数数组运算

```java
float[] a = ComplexMath.fromArrays(realArr, imagArr);  // 从独立数组创建
float[] result = ComplexMath.add(a, b);                // 逐元素加法
float[] product = ComplexMath.multiply(a, b);          // 逐元素复数乘法
float[] conjugated = ComplexMath.conjugate(a);         // 共轭
float[] magnitudes = ComplexMath.abs(a);               // 模
float[] phases = ComplexMath.phase(a);                 // 相位
float[] exponential = ComplexMath.exp(a);              // e^z
Complex dot = ComplexMath.dotProduct(a, b);            // 复数点积
```

复数以交错格式存储：`[re0, im0, re1, im1, ...]`，适合高效 SIMD 处理。

### FFT — 快速傅里叶变换

```java
float[] signal = new float[1024 * 2]; // 512 个复数（交错格式）
// ... 填充信号数据 ...

float[] spectrum     = FFT.fft(signal);     // 前向 FFT（原地）
float[] spectrumCopy = FFT.fftCopy(signal); // 前向 FFT（返回新数组）
float[] reconstructed = FFT.ifft(spectrum);  // 逆 FFT

// 实数 FFT（利用对称性，只返回一半频谱）
float[] realSignal = new float[1024];
float[] halfSpectrum = FFT.rfft(realSignal);
float[] recovered = FFT.irfft(halfSpectrum, 1024);

// 卷积
float[] convResult = FFT.convolve(a, b);

// 工具方法
int fftSize = FFT.nextPowerOfTwo(1000); // 1024
boolean isPow2 = FFT.isPowerOfTwo(256);  // true
```

FFT 长度必须是 2 的幂。使用 `FFT.nextPowerOfTwo()` 获取有效长度。同时提供 double 精度版本。

### MatrixMath — 矩阵运算

```java
float[] identity = MatrixMath.identity(3);               // 3x3 单位矩阵
float[] product = MatrixMath.multiply(a, b, 2, 2, 2);    // 2x2 矩阵乘法
float[] result = MatrixMath.multiplyVector(m, v, 3, 3);   // 矩阵-向量乘法
float[] transposed = MatrixMath.transpose(m, 2, 3);       // 转置
float det = MatrixMath.determinant2x2(m);                  // 2x2 行列式
float[] inv = MatrixMath.inverse2x2(m);                    // 2x2 逆矩阵
float norm = MatrixMath.frobeniusNorm(m, 3, 3);            // Frobenius 范数
```

矩阵使用一维数组按行优先存储：`element(i, j) = array[i * cols + j]`。

### VectorOps — 2D/3D/4D 向量

```java
VectorOps.Vec3 v1 = new VectorOps.Vec3(1, 2, 3);
VectorOps.Vec3 v2 = new VectorOps.Vec3(4, 5, 6);

VectorOps.Vec3 sum = v1.add(v2);           // 加法
VectorOps.Vec3 cross = v1.cross(v2);        // 叉积
float dot = v1.dot(v2);                     // 点积
VectorOps.Vec3 normalized = v1.normalize(); // 归一化
VectorOps.Vec3 reflected = v1.reflect(n);   // 反射
VectorOps.Vec3 projected = v1.project(v2);  // 投影
float angle = v1.angleBetween(v2);          // 角度
```

支持 `Vec2`、`Vec3`、`Vec4`，均为不可变类型。

## 项目结构

```
src/main/java/cn/tuyucheng/taketoday/fastmath/
├── FastMath.java         — 快速标量数学函数
├── VectorMath.java       — SIMD 向量数组运算
├── MatrixMath.java       — 矩阵运算
├── Complex.java          — 单精度复数（不可变）
├── ComplexD.java         — 双精度复数（不可变）
├── ComplexMath.java      — 批量复数数组运算
├── VectorOps.java        — 2D/3D/4D 向量类
├── FFT.java              — 快速傅里叶变换
└── benchmark/
    ├── BenchmarkRunner.java      — JMH 基准测试入口
    └── VectorMathBenchmark.java  — 基准测试用例

src/test/java/cn/tuyucheng/taketoday/fastmath/
├── FastMathTest.java     — FastMath/VectorMath/MatrixMath/VectorOps 测试
├── ComplexTest.java      — Complex/ComplexD/ComplexMath/FFT 测试
```

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 26+ |
| Java Vector API | jdk.incubator.vector (Preview) |
| JUnit 5 | 5.10.2 |
| JMH | 1.37 |
| Maven | 3.8+ |

## 编译参数

编译和运行时需要启用 Vector API 预览特性：

```bash
javac --enable-preview --add-modules=jdk.incubator.vector ...
java --enable-preview --add-modules=jdk.incubator.vector ...
```

Maven 已在 `pom.xml` 中配置了这些参数，直接使用 `mvn` 命令即可。