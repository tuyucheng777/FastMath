package cn.tuyucheng.taketoday.fastmath.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/// Main class to run JMH benchmarks.
///
/// Run from command line:
/// `java --enable-preview --add-modules=jdk.incubator.vector -cp target/classes:target/test-classes cn.tuyucheng.taketoday.fastmath.benchmark.BenchmarkRunner`
///
/// Or build and run the JAR:
/// `mvn clean package`
/// `java --enable-preview --add-modules=jdk.incubator.vector -jar target/benchmarks.jar`
public class BenchmarkRunner {

    static void main() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(VectorMathBenchmark.class.getSimpleName())
                // Uncomment to run specific benchmarks:
                // .include(".*AddFloat.*")
                // .include(".*DotProduct.*")
                // .include(".*Sin.*")
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(1))
                .forks(2)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }
}