package org.avrodite.avro.io;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 0, jvmArgsPrepend = {"-server", "-Xms8G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 5)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class ByteArrayAvroInputBenchmark {
  // 230443870 -> [-68, -83, -30, -37, 1]
  public static void main(String[] args) throws Exception {
    new Runner(
      new OptionsBuilder()
        .include(ByteArrayAvroInputBenchmark.class.getSimpleName())
        .verbosity(VerboseMode.NORMAL)
        .forks(1)
        .build()
    ).run();
  }

  private static final ByteArrayAvroInput INT_INPUT  = new ByteArrayAvroInput(new byte[]{ -68, -83, -30, -37, 1 });
  private static final ByteArrayAvroInput LONG_INPUT = new ByteArrayAvroInput(new byte[]{ -82, -44, -123, -117, -59, -25, -31, -71, -11, 1 });


  @Benchmark
  public void readLong() {
    LONG_INPUT.readLong();
    LONG_INPUT.rewind();
  }

  @Benchmark
  public void readInt() {
    INT_INPUT.readInt();
    INT_INPUT.rewind();
  }

}
