package org.avrodite.avro.io;

import java.util.concurrent.TimeUnit;
import org.avrodite.tools.avro.event.EquityMarketPriceEvent;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 0, jvmArgsPrepend = {"-server", "-Xms8G"})
@Warmup(iterations = 0, time = 10)
@Measurement(iterations = 10, time = 8)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class EventBenchmark {

  public static void main(String[] args) throws Exception {
    new Runner(
      new OptionsBuilder()
        .include(EventBenchmark.class.getSimpleName())
        .addProfiler(GCProfiler.class)
        .verbosity(VerboseMode.NORMAL)
        .build()
    ).run();
  }
/*
  static EquityMarketPriceEvent_AvroCodec_23136173 codec = new EquityMarketPriceEvent_AvroCodec_23136173();
  static ByteArrayAvroInput input = new ByteArrayAvroInput(new byte[]{ 72, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45, 56, 56, 98, 51, 45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 72, 99, 51, 54, 53, 54, 98, 48, 54, 45, 48, 54, 53, 100, 45, 52, 56, 49, 97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97, 100, 55, 48, 53, 53, 53, 49, 101, 72, 100, 97, 100, 101, 98, 100, 54, 101, 45, 100, 99, 56, 100, 45, 52, 100, 97, 49, 45, 57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50, 55, 54, 48, 98, 100, 50, 12, 84, 73, 67, 75, 69, 82, 4, 86, 14, 45, -78, 13, 95, 64, -36, -105, -125, 120, -57, -70, -72, -115, 6, -16, -122, -65, 10, 2, 78, -76, -85, -112, -14, -27, 94, 64, -128, -75, 24, 4, -104, 18, 73, -12, 50, -66, 94, 64, -64, -49, 36, 6, -29, 112, -26, 87, 115, -106, 94, 64, -128, -22, 48, 8, 45, -49, -125, -69, -77, 110, 94, 64, -64, -124, 61, 10, 119, 45, 33, 31, -12, 70, 94, 64, -128, -97, 73, 0, 10, 2, -71, -9, 112, -55, 113, 53, 95, 64, -96, -100, 1, 4, 112, -103, -45, 101, 49, 93, 95, 64, -64, -72, 2, 6, 36, 59, 54, 2, -15, -124, 95, 64, -32, -44, 3, 8, -37, -36, -104, -98, -80, -84, 95, 64, -128, -15, 4, 10, -112, 126, -5, 58, 112, -44, 95, 64, -96, -115, 6, 0 });
  static ByteArrayAvroOutput output = new ByteArrayAvroOutput(new byte[280]);
  static EquityMarketPriceEvent event = EquityMarketPriceEvent.Companion.createEvent();

  @Benchmark
  public void benchmark() {
    codec.encode(event, output);
    codec.decode(input);
    input.rewind();
    output.rewind();
  }

 */


}
