package org.avrodite.avro.result;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;
import tech.tablesaw.plotly.traces.BarTrace;

@Getter
@Accessors(chain = true)
public class MetricRepresentation {
  private static final String PNG_EXT = "png";
  private String id;
  private String title;
  private SortOrder sortOrder;
  private Map<String, BenchResultModel.Metric> metricByFramework;
  private Map<String, String> friendlyBenchmarkName;
  private Map<String, String> relativePerformanceMap;
  private List<BenchResultModel> sortedResults;
  private TestRepresentation testRepresentation;
  private Table dataTable;
  private Table relativeDataTable;
  private Figure figure;

  private MetricRepresentation(String id, String title, SortOrder sortOrder) {
    this.id = id;
    this.title = title;
    this.sortOrder = sortOrder;
  }

  public String metricOfFramework(String framework) {
    return String.format("%.0f", metricByFramework.get(framework).getScore());
  }

  public MetricRepresentation(String id, String metricName, SortOrder sortOrder, List<BenchResultModel> results, Function<BenchResultModel, BenchResultModel.Metric> extractor, TestRepresentation testRepresentation) {
    this(id, metricName, sortOrder);
    this.testRepresentation = testRepresentation;
    sortedResults = results.stream()
      .sorted(comparingDouble((a) -> extractor.apply(a).getScore()))
      .collect(toList());
    if (sortOrder.equals(SortOrder.DESC)) {
      sortedResults = Lists.reverse(sortedResults);
    }

    friendlyBenchmarkName = results.stream()
      .map(BenchResultModel::getBenchmark)
      .collect(toMap(v -> v, bench -> {
        String[] parts = bench.split("\\.");
        return parts[parts.length - 1];
      }));

    metricByFramework = sortedResults.stream()
      .collect(toMap(v -> friendlyBenchmarkName.get(v.getBenchmark()), extractor));

    StringColumn subject = StringColumn.create("Framework");
    DoubleColumn metricColumn = DoubleColumn.create(metricName);
    results.stream()
      .sorted(comparingDouble(v -> extractor.apply(v).getScore() * (sortOrder.equals(SortOrder.ASC) ? 1 : -1)))
      .forEach(model -> {
        String[] nameParts = model.getBenchmark().split("\\.");
        subject.append(nameParts[nameParts.length - 1]);
        metricColumn.append(extractor.apply(model).getScore());
      });

    String relativePerformanceTitle = "Relative Perf.";
    StringColumn relativePerformance = StringColumn.create(relativePerformanceTitle);
    double reference = results.stream()
      .map(extractor)
      .map(BenchResultModel.Metric::getScore)
      .reduce(sortOrder.equals(SortOrder.ASC) ? Double.MAX_VALUE : Double.MIN_VALUE,
        sortOrder.equals(SortOrder.ASC) ? Math::min : Math::max);
    relativePerformanceMap = new HashMap<>();
    results.stream()
      .sorted(comparingDouble(v -> extractor.apply(v).getScore() * (sortOrder.equals(SortOrder.ASC) ? 1 : -1)))
      .forEach(model -> {
        relativePerformance.append(String.format("%.2f%%", 100 * extractor.apply(model).getScore() / reference));
        relativePerformanceMap.put(friendlyBenchmarkName.get(model.getBenchmark()), String.format("%.2f%%", 100 * extractor.apply(model).getScore() / reference));
      });

    dataTable = Table.create(subject, metricColumn);
    if (sortOrder.equals(SortOrder.ASC)) {
      dataTable = dataTable.sortDescendingOn(metricColumn.name());
    } else {
      dataTable = dataTable.sortAscendingOn(metricColumn.name());
    }
    relativeDataTable = Table.create(subject, relativePerformance);

    Layout layout = Layout.builder().title(metricName).height(500).width(1000)
      .margin(Margin.builder().left(160).build())
      .build();
    BarTrace bar = BarTrace.builder(dataTable.categoricalColumn(subject.name()), dataTable.numberColumn(metricColumn.name()))
      .orientation(BarTrace.Orientation.HORIZONTAL)
      .build();
    figure = new Figure(layout, bar);

  }

  public List<String> frameworks() {
    return sortedResults.stream()
      .map(BenchResultModel::getBenchmark)
      .map(friendlyBenchmarkName::get)
      .collect(toList());
  }

  public String imageName() {
    return String.join(".", testRepresentation.getId(), id, PNG_EXT);
  }
}
