## Avrodite ![image alt >](./avrodite-pages/images/avrodite.png?raw=true) 
A fast, lightweight, POJO driven, Apache Avro serialization/deserialization library.

#### Motivation

While designing this library, the following concerns were in mind:

1. **POJO Driven**: This is particularly useful for an already existing application that 
have a set of plain JAVA objects as API. We tried the avro core implementation, but the library adds 
and additional layer of indirection/de-referencing. Moreover, the mapping between the Record 
objects/dictionaries and the existing api can quickly turn into a fastidious boilerplate to maintain.

2. **Generics and complex types**: Existing JAVA API models often leverage the 
typing features that the core language offers. We need a library that would support any type, 
including types with generics.

3. **Fast**: Latency is a critical requirement for modern applications and real-time 
event processors. Having a library that performs at high standards, is definitely a criteria for 
its adoption.

4. **Low memory footprint**: Holding pressure from the heap by minimizing the created objects during the 
serialization / deserialization process support applications towards meeting their SLA. We need to have 
lowest possible memory footprint and offer an API for re-using existing objects when applicable.

#### Current Status

Currently the library fulfills most of its design objectives and is performing better than many widely 
adopted libraries. It has full support for Java generics and complex types (We've stress-tested it with 
fields of this kind `List<Map<String, Event<Map<String, String>, Model<A,B>>>>` and it passes). 
From a performance perspective, it performs 3x-4x better than its closest mate, between 5x and 15x than 
Avro core Record API.
Regarding usability, the interaction with Avrodite API would consist of something like so:
```java
//API configuration would happen in your dependency injection layer 
Avrodite<AvroStandard, AvroCodec<?>> avrodite = AvroStandardV19.avrodite()
                                                      .discoverCodecsAt(yourAPIPackage)
                                                      .build();                                                               
//once you catch an avrodite instance you can get a codec for a given target as follows:
AvroCodec<Model> codec = avrodite.getCodec(Model.class);

//serialization and deserialization is then trivial
byte[] avroData = codec.encode(new Model());
Model decodedModel = codec.decode(avroData);

//When needed, you can get your schema from the codec instance like so:
Schema schema = codec.getSchema();

```

#### Modules description
The library is modular and was designed with the idea of implementing other serialization
formats in the future (JSON for example). Currently the modules consists of :

1. `avrodite-api`: The global API that projects would depends on for 
serialization/deserialization.
2. `avrodite-codec-avro`: An extension of the public API that provides AVRO 
custom API (access to Schema instances for example).
3. `avrodite-tools`: Mainly a compile-time/build phase dependency that contains the 
necessary logic for introspecting your beans and plain objects API. 
4. `avrodite-tools-avro`: An `avrodite-tools` plugin that generate custom classes for the AVRO binary 
format.
5. `avrodite-avro-maven-plugin`: A maven plugin that abstracts away from you the `avrodite-tools` 
stack to generate your codecs classes during the build phase.
6. `avrodite-codec-avro-benchmarks`: A test module that benchmarks the AVRO implementation against 
other libraries.


#### Benchmark Results Snapshot

Refer to this [document](./avrodite-pages/Benchmarks.md) for detailed results. 


##### Throughput

(Higher is better)

| Framework | T1 throughput [ ops/ms ] | T1 relative perf. |T2 throughput [ ops/ms ] | T2 relative perf. |
|-----------|------------|--------------|------------|--------------|
| avrodite | 1854 | 100.00% | 2215 | 100.00% | 
| protocolBuffers | 582 | 31.41% | 590 | 26.64% | 
| avroCoreNoHydration | 109 | 5.89% | 510 | 23.04% | 
| avroCoreWithHydration | 94 | 5.06% | 251 | 11.32% | 
| jacksonAvro | 88 | 4.77% | 146 | 6.61% | 
| jacksonJSON | 89 | 4.78% | 87 | 3.92% | 

![Alt text](./avrodite-pages/images/T1.thrpt.png?raw=true "Throughput")

##### Heap Usage

(Lower is better)

| Framework | T1 Heap Allocation Rate [ Byte/op ] | T1 relative perf. |T2 Heap Allocation Rate [ Byte/op ] | T2 relative perf. |
|-----------|------------|--------------|------------|--------------|
| avrodite | 1024 | 100.00% | 1024 | 100.00% | 
| avroCoreNoHydration | 2248 | 219.53% | 2248 | 219.53% | 
| avroCoreWithHydration | 4840 | 472.66% | 4840 | 472.66% | 
| protocolBuffers | 6088 | 594.53% | 6088 | 594.53% | 
| jacksonJSON | 9472 | 925.00% | 9472 | 925.00% | 
| jacksonAvro | 16264 | 1588.28% | 16296 | 1591.41% | 

![Alt text](./avrodite-pages/images/T1.gc.png?raw=true "Heap Usage")

#### Roadmap
The following features are planned for the future:
- Enums support (easy).
- Avro Union types (can be useful when you have a supertype with various children) (easy/medium).
- Map a model API evolution to Schema migration (most likely hard).
- Support other serialization formats: A good portion of the library (types introspection, codec compilation) can be 
re-used to support other formats such as JSON. (average difficulty, depends on format)

#### Licence
Copyright (c) 2020 Yassine Echabbi

Licensed under the Apache License, Version 2.0
