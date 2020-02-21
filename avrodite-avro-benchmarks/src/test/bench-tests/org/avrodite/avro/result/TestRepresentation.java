package org.avrodite.avro.result;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public class TestRepresentation {

  private static final String ALLOCATION_RATE_KEY = "·gc.alloc.rate.norm";

  private String id;
  private String description;
  private MetricRepresentation throughputMetric;
  private MetricRepresentation heapAllocationRate;


  public TestRepresentation(String id, String description, BenchResultModel[] modelsArr, Predicate<BenchResultModel> selector) {
    this.id = id;
    this.description = description;
    List<BenchResultModel> models = Arrays.stream(modelsArr)
      .filter(selector)
      .collect(toList());
    throughputMetric = new MetricRepresentation(
      "thrpt",
      format("%s throughput [ ops/ms ]", id),
      SortOrder.DESC,
      models,
      BenchResultModel::getPrimaryMetric,
      this
    );
    heapAllocationRate = new MetricRepresentation(
      "gc",
      format("%s Heap Allocation Rate [ Byte/op ]", id),
      SortOrder.ASC,
      models,
      model -> model.getSecondaryMetrics().get(ALLOCATION_RATE_KEY),
      this
    );
  }


}
