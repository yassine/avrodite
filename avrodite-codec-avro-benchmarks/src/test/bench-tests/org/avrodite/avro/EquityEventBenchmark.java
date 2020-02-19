package org.avrodite.avro;

import static java.util.Objects.requireNonNull;
import static org.avrodite.avro.ReportUtils.report;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.avrodite.avro.state.AvroCoreBenchMarkState;
import org.avrodite.avro.state.AvroditeBenchmarkState;
import org.avrodite.avro.state.JacksonAvroBenchMarkState;
import org.avrodite.avro.state.JacksonJsonBenchmarkState;
import org.avrodite.avro.state.ProtocolBuffersBenchmarkState;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.proto.EquityEvents;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 0, jvmArgsPrepend = {"-server", "-Xms1G"})
@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 4, time = 8)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class EquityEventBenchmark {

  @Getter
  static final String OUTPUT_FILE = "avrodite-pages/data/output.json";

  @SneakyThrows
  public static void main(String[] args) {
    new EquityEventBenchmark().run();
    report(OUTPUT_FILE, "avrodite-pages");
  }

  @SneakyThrows
  public void run() {
    new Runner(
      new OptionsBuilder()
        .include(EquityEventBenchmark.class.getSimpleName())
        .resultFormat(ResultFormatType.JSON)
        .addProfiler(GCProfiler.class)
        .result(OUTPUT_FILE)
        .verbosity(VerboseMode.EXTRA)
        .forks(1)
        .build()
    ).run();
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    private AvroCoreBenchMarkState avroCore;
    private AvroditeBenchmarkState avrodite;
    private JacksonAvroBenchMarkState jacksonAvro;
    private JacksonJsonBenchmarkState jacksonJson;
    private ProtocolBuffersBenchmarkState protocolBuffers;
    private byte[] serializationData;

    @Param( {"true", "false"})
    boolean nullable;

    @Setup(Level.Trial)
    public void setup() {
      serializationData = nullable ? EquityMarketPriceEvent.NULLABLE_FIELDS_SERIALIZED_MODEL
        : EquityMarketPriceEvent.NON_NULLABLE_FIELDS_SERIALIZED_MODEL;
      avroCore = new AvroCoreBenchMarkState(serializationData, nullable);
      avrodite = new AvroditeBenchmarkState(serializationData, nullable);
      jacksonAvro = new JacksonAvroBenchMarkState(serializationData, nullable);
      jacksonJson = new JacksonJsonBenchmarkState();
      protocolBuffers = new ProtocolBuffersBenchmarkState();
    }

  }

  @Benchmark
  @SneakyThrows
  public GenericData.Record avroCoreNoHydration(BenchmarkState benchmarkState) {
    benchmarkState.avroCore.byteArrayOutputStream.reset();
    benchmarkState.avroCore.datumWriter.write(benchmarkState.avroCore.eventRecord, EncoderFactory.get().binaryEncoder(benchmarkState.avroCore.byteArrayOutputStream, benchmarkState.avroCore.binaryEncoder));
    GenericData.Record record = new GenericData.Record(requireNonNull(benchmarkState.avroCore.eventSchema));
    benchmarkState.avroCore.datumReader.read(record, DecoderFactory.get().binaryDecoder(benchmarkState.serializationData, null));
    return record;
  }

  /*
    Comparing to 'avroCoreNoHydration' this includes the mapping between the record API and the
    api model.
   */
  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent avroCoreWithHydration(BenchmarkState benchmarkState) {
    benchmarkState.avroCore.byteArrayOutputStream.reset();
    //map from model to record ->
    GenericData.Record record = benchmarkState.avroCore.eventRecord;
    benchmarkState.avroCore.toRecordData(record);
    benchmarkState.avroCore.datumWriter.write(benchmarkState.avroCore.eventRecord, EncoderFactory.get().binaryEncoder(benchmarkState.avroCore.byteArrayOutputStream, benchmarkState.avroCore.binaryEncoder));
    record = benchmarkState.avroCore.datumReader.read(record, DecoderFactory.get().binaryDecoder(benchmarkState.serializationData, null));
    //map from record to model <-
    return benchmarkState.avroCore.eventFrom(record);
  }

  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent avrodite(BenchmarkState benchmarkState) {
    EquityMarketPriceEvent event = new EquityMarketPriceEvent();
    benchmarkState.avrodite.avroditeCodec.encode(benchmarkState.avrodite.model, benchmarkState.avrodite.bufferOut);
    benchmarkState.avrodite.avroditeCodec.decode(event, benchmarkState.avrodite.bufferIn);
    benchmarkState.avrodite.bufferIn.rewind();
    benchmarkState.avrodite.bufferOut.reset();
    return event;
  }

  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent jacksonAvro(BenchmarkState benchmarkState) {
    EquityMarketPriceEvent event = benchmarkState.jacksonAvro.reader.readValue(benchmarkState.jacksonAvro.byteArrayInputStream);
    benchmarkState.jacksonAvro.writer.writeValue(benchmarkState.jacksonAvro.byteArrayOutputStream, benchmarkState.jacksonAvro.model);
    benchmarkState.jacksonAvro.byteArrayOutputStream.reset();
    benchmarkState.jacksonAvro.byteArrayInputStream.reset();
    return event;
  }

  @SneakyThrows
  @Benchmark
  public EquityEvents.EquityMarketPriceEvent protocolBuffers(BenchmarkState benchmarkState) {
    benchmarkState.protocolBuffers.protobufIs.reset();
    benchmarkState.protocolBuffers.protobufOs.reset();
    benchmarkState.protocolBuffers.nonNullableFieldsEvent.writeTo(benchmarkState.protocolBuffers.protobufOs);
    return EquityEvents.EquityMarketPriceEvent.parseFrom(benchmarkState.protocolBuffers.protobufIs);
  }

  @SneakyThrows
  @Benchmark
  public EquityMarketPriceEvent jacksonJSON(BenchmarkState benchmarkState) {
    benchmarkState.jacksonJson.os.reset();
    benchmarkState.jacksonJson.is.reset();
    benchmarkState.jacksonJson.mapper.writeValue(benchmarkState.jacksonJson.os, benchmarkState.jacksonJson.model);
    return benchmarkState.jacksonJson.mapper.readValue(benchmarkState.jacksonJson.is, EquityMarketPriceEvent.class);
  }

}
