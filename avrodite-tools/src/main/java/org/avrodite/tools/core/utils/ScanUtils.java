package org.avrodite.tools.core.utils;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import io.github.classgraph.ClassGraph;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScanUtils {

  public static ClassGraph classPathScannerWith(Set<Package> whitelistPackages, Set<ClassLoader> classLoaders) {
    ClassGraph classGraph = new ClassGraph();
    ofNullable(whitelistPackages)
      .ifPresent(packages -> classGraph.whitelistPathsNonRecursive(
        packages.stream()
          .map(Package::getName)
          .map(name -> name.replaceAll("\\.", "/"))
          .collect(toSet())
          .toArray(new String[0])
      ));
    ofNullable(classLoaders)
      .ifPresent(loaders -> loaders.forEach(classGraph::addClassLoader));
    return classGraph;
  }

  public static ClassGraph classPathScannerWith(Set<ClassLoader> classLoaders) {
    ClassGraph classGraph = new ClassGraph().whitelistPaths("*");
    ofNullable(classLoaders).ifPresent(loaders -> loaders.forEach(classGraph::addClassLoader));
    return classGraph;
  }

}
