package org.avrodite.avro.result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter @Setter @ToString @Slf4j
public class BenchResultModel {

  private String benchmark;
  private String jmhVersion;
  private String jdkVersion;
  private String measurementTime;
  private String mode;
  private String vmName;
  private String vmVersion;
  private List<String> jvmArgs;
  private int measurementIterations;
  private Metric primaryMetric;
  private Map<String, Metric> secondaryMetrics;
  private Parameters params;

  @Getter
  @Setter
  @ToString
  public static class Metric {
    private double score;
    private double scoreError;
    private String scoreUnit;
  }

  public String jvmArgsList() {
    return String.join(", ", jvmArgs.stream().filter(s -> !s.startsWith("-D")).distinct().toArray(String[]::new));
  }

  @Getter
  @Setter
  @ToString
  public static class Parameters {
    private String nullable;
  }

}
