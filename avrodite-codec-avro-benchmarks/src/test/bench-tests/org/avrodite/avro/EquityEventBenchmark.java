package org.avrodite.avro;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.avrodite.fixtures.event.Equity;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.fixtures.event.EquityOrder;
import org.avrodite.fixtures.event.EventMeta;
import org.avrodite.proto.EquityEvents;
import org.junit.Test;
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
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0, jvmArgsPrepend = {"-server", "-Xms1G"})
@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 4, time = 8)
@BenchmarkMode(Mode.Throughput)
public class EquityEventBenchmark {

  final boolean nullable;
  final BenchmarkModel model;
  @Getter
  final String output;

  public EquityEventBenchmark() {
    this.nullable = true;
    this.model    = new BenchmarkModel(this.nullable);
    this.output   = this.nullable ? "/tmp/bench-results-nullable-fields.json": "/tmp/bench-results.json";
  }

  @SneakyThrows
  public static void main(String[] args) {
    new EquityEventBenchmark().run();
  }

  @SneakyThrows
  public void run(){
    new Runner(
      new OptionsBuilder()
        .include(EquityEventBenchmark.class.getSimpleName())
        .resultFormat(ResultFormatType.JSON)
        .addProfiler(GCProfiler.class)
        .result(nullable ? "/tmp/bench-results-nullable-fields.json": "/tmp/bench-results.json")
        .verbosity(VerboseMode.EXTRA)
        .forks(1)
        .build()
    ).run();
  }

  @Benchmark
  @SneakyThrows
  public GenericData.Record avroCoreNoHydration(EquityEventBenchmark benchmark) {
    benchmark.model.byteArrayOutputStream.reset();
    benchmark.model.datumWriter.write(benchmark.model.eventRecord, EncoderFactory.get().binaryEncoder(benchmark.model.byteArrayOutputStream, benchmark.model.binaryEncoder));
    GenericData.Record record = new GenericData.Record(requireNonNull(benchmark.model.eventSchema));
    benchmark.model.datumReader.read(record, DecoderFactory.get().binaryDecoder(benchmark.model.testTarget, null));
    return record;
  }

  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent avroCoreWithHydration(EquityEventBenchmark benchmark) {
    benchmark.model.byteArrayOutputStream.reset();
    benchmark.model.datumWriter.write(benchmark.model.eventRecord, EncoderFactory.get().binaryEncoder(benchmark.model.byteArrayOutputStream, benchmark.model.binaryEncoder));
    GenericData.Record record = new GenericData.Record(requireNonNull(benchmark.model.eventSchema));
    record = benchmark.model.datumReader.read(record, DecoderFactory.get().binaryDecoder(benchmark.model.testTarget, null));
    GenericData.Record targetRecord = (GenericData.Record) record.get("target");
    GenericData.Record metaRecord = (GenericData.Record) record.get("meta");
    //for fair results, include the 'effort' of hydrating a bean as per avrodite
    EquityMarketPriceEvent event = new EquityMarketPriceEvent();
    EventMeta meta = new EventMeta();
    Equity equity = new Equity();
    meta.setCorrelation(metaRecord.get("correlation").toString());
    meta.setId(metaRecord.get("id").toString());
    meta.setParentId(metaRecord.get("parentId").toString());
    equity.setPrice((double) targetRecord.get("price"));
    equity.setTicker(targetRecord.get("ticker").toString());
    equity.setVolume((long) targetRecord.get("volume"));
    equity.setVariation((double) targetRecord.get("variation"));
    event.setMeta(meta);
    event.setTarget(equity);
    List<GenericData.Record> bidRecords = (List<GenericData.Record>) record.get("bid");
    List<GenericData.Record> asksRecords = (List<GenericData.Record>) record.get("ask");
    List<EquityOrder> bid = new ArrayList<>(bidRecords.size());
    List<EquityOrder> ask = new ArrayList<>(asksRecords.size());
    for (GenericData.Record orderRecord : bidRecords) {
      EquityOrder eqOrder = new EquityOrder();
      eqOrder.setQuantity((long) orderRecord.get("quantity"));
      eqOrder.setPrice((double) orderRecord.get("price"));
      eqOrder.setCount((int) orderRecord.get("count"));
      bid.add(eqOrder);
    }
    for (GenericData.Record orderRecord : asksRecords) {
      EquityOrder eqOrder = new EquityOrder();
      eqOrder.setQuantity((long) orderRecord.get("quantity"));
      eqOrder.setPrice((double) orderRecord.get("price"));
      eqOrder.setCount((int) orderRecord.get("count"));
      ask.add(eqOrder);
    }
    event.setAsk(ask);
    event.setBid(bid);
    return event;
  }

  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent avrodite(EquityEventBenchmark benchmark) {
    EquityMarketPriceEvent event = new EquityMarketPriceEvent();
    benchmark.model.avroditeCodec.encode(benchmark.model.model, benchmark.model.bufferOut);
    benchmark.model.avroditeCodec.decode(event, benchmark.model.bufferIn);
    benchmark.model.bufferIn.rewind();
    benchmark.model.bufferOut.reset();
    return event;
  }

  @Benchmark
  @SneakyThrows
  public EquityMarketPriceEvent jacksonAvro(EquityEventBenchmark benchmark) {
    EquityMarketPriceEvent event = benchmark.model.reader.readValue(benchmark.model.byteArrayInputStream);
    benchmark.model.writer.writeValue(benchmark.model.byteArrayOutputStream, benchmark.model.model);
    benchmark.model.byteArrayOutputStream.reset();
    benchmark.model.byteArrayInputStream.reset();
    return event;
  }

  @SneakyThrows
  @Benchmark
  public EquityEvents.EquityMarketPriceEvent protocolBuffers(EquityEventBenchmark benchmark) {
    benchmark.model.protobufIs.reset();
    benchmark.model.protobufOs.reset();
    benchmark.model.nonNullableFieldsEvent.writeTo(benchmark.model.protobufOs);
    return EquityEvents.EquityMarketPriceEvent.parseFrom(benchmark.model.protobufIs);
  }

  @SneakyThrows
  @Benchmark
  public EquityMarketPriceEvent jacksonJSON(EquityEventBenchmark benchmark) {
    return benchmark.model.mapper.readValue(benchmark.model.mapper.writeValueAsBytes(benchmark.model.model), EquityMarketPriceEvent.class);
  }

}
