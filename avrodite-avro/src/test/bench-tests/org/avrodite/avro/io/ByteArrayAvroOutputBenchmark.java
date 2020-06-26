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
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 4, time = 5)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class ByteArrayAvroOutputBenchmark {

  public static void main(String[] args) throws Exception {
    new Runner(
      new OptionsBuilder()
        .include(ByteArrayAvroOutputBenchmark.class.getSimpleName())
        .verbosity(VerboseMode.NORMAL)
        .forks(1)
        .build()
    ).run();
  }

  private static final ByteArrayAvroOutput BUFFER = new ByteArrayAvroOutput(new byte[64]);

  @Benchmark
  public void writeInt() {
    BUFFER.writeInt(2130443870);
    BUFFER.rewind();
  }

  @Benchmark
  public void writeLong() {
    BUFFER.writeLong(8304547843870452158L);
    BUFFER.rewind();
  }

  @Benchmark
  public void writeDouble() {
    BUFFER.writeDouble(.3999686902644679);
    BUFFER.rewind();
  }

  @Benchmark
  public void writeString() {
    BUFFER.writeString("abcdefghijklmnopqrstuvwxyz");
    BUFFER.rewind();
  }

}
