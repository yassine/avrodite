package org.avrodite.tools.core.bean;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.avrodite.tools.core.utils.ReflectionUtils.getFields;
import static org.avrodite.tools.core.utils.ReflectionUtils.getSuperTypes;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.avrodite.tools.core.utils.TypeUtils;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

@UtilityClass
public class DiscoveryUtils {

  /**
   * A bean can depend on another bean by composition. If we have (and we will)
   * to create an avro schema for each bean for example, then whenever we want to reference the schema
   * of one of its dependencies, that schema shall be already defined in the schema definition context.
   * <p>
   * Here we're guaranteeing that, by processing each bean as per the order of the returned list,
   * at any index dependencies of a given bean have been already processed (in other words, they are
   * located at lower indices).
   *
   * @param discoveredClasses the set of classes to initialize the Object graph
   * @param whiteList         A set of whitelist packages, only classes of these packages are added to the Object graph
   * @param blackList         A set of classes to exclude from the Object graph
   * @return A list of TypeInfo where : if (i < j) then (type at index i is not dependent on type at index j).
   */
  public static List<TypeInfo> getTypeSequence(Set<Class<?>> discoveredClasses, Set<String> whiteList, Set<Class<?>> blackList) {
    DefaultDirectedGraph<TypeInfo, DefaultEdge> typeInfoGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    // add the supertypes to the party if they fulfill the scope requirements
    discoveredClasses.stream()
      .map(clazz -> new TypeInfo(clazz, clazz, clazz.getName()))
      .filter(typeInfoGraph::addVertex)
      .forEach(clazzTypeInfo ->
        getSuperTypes(clazzTypeInfo.rawType()).stream()
          .filter(typeInfo -> blackList.stream().noneMatch(typeInfo.rawType()::equals))
          .filter(typeInfo -> whiteList.stream().anyMatch(typeInfo.rawType().getPackage().getName()::startsWith))
          .forEach(typeInfoGraph::addVertex)
      );
    // add the fields types to the party if they fulfill the scope requirements
    new HashSet<>(typeInfoGraph.vertexSet()).forEach(clazzTypeInfo ->
          getFields(clazzTypeInfo.rawType()).stream()
            .filter(field -> whiteList.stream().anyMatch(pkg -> field.getDeclaringClass().getPackage().getName().startsWith(pkg)))
            .map(field -> resolve(clazzTypeInfo.rawType()).inlyingType(clazzTypeInfo.type()).resolveFieldType(field))
            .map(TypeUtils::typeTarget)
            .filter(genericInfo -> discoveredClasses.contains(genericInfo.rawType()))
            .filter(genericInfo -> !isAbstract(genericInfo.rawType().getModifiers()))
            .filter(genericInfo -> !isInterface(genericInfo.rawType().getModifiers()))
            .filter(typeInfo -> blackList.stream().noneMatch(typeInfo.rawType()::equals))
            .filter(genericInfo -> !genericInfo.equals(clazzTypeInfo))
            .forEach(typeInfo -> {
              typeInfoGraph.addVertex(typeInfo);
              typeInfoGraph.addEdge(typeInfo, clazzTypeInfo);
            })
        );

    return stream(spliteratorUnknownSize(new TopologicalOrderIterator<>(typeInfoGraph), ORDERED), false).collect(toList());
  }

}
