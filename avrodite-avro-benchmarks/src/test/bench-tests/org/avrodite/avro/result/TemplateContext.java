package org.avrodite.avro.result;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TemplateContext {
  private final List<TestRepresentation> tests;
  private final BenchResultModel model;
}
