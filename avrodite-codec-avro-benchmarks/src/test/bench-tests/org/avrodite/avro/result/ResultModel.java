package org.avrodite.avro.result;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ResultModel {


  private String benchmark;
  private String jmhVersion;
  private String jdkVersion;
  private String measurementTime;
  private String mode;
  private String vmName;
  private String vmVersion;
  private int measurementIterations;
  private Metric primaryMetric;
  private Map<String, Metric> secondaryMetrics;

  @Getter @Setter @ToString
  public static class Metric {
    private double score;
    private double scoreError;
    private String scoreUnit;
  }

}
