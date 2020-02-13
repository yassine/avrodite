## Avrodite   ![image alt >](./avrodite-pages/images/avrodite.png?raw=true#right) 
A fast, lightweight, java driven, Apache Avro serialization/deserialization library.

#### Motivation

While designing this library, the following concerns were in mind:

1. To be plain JAVA objects driven: This is particularly useful for an already existing application that have a set
of plain JAVA objects as API. We tried the avro core implementation, but the library adds and additional layer of 
indirection/de-referencing. Moreover, the mapping between the Record objects/dictionaries and the existing api
can quickly turn into a fastidious boilerplate to maintain.

2. Support for JAVA core Generics and complex types: Existing JAVA API models often leverage the typing features that
the core language offers. We need a library that would support any type, including types with generics.

3. To be the fastest possible: Latency is a critical requirement for modern applications and real-time event processors.
Having a library that performs at high standards, is definitely a criteria for its use.

4. Low memory footprint: Holding pressure from the heap by minimizing the created objects during the serialization / 
deserialization process can contribute to the overall application SLA. We need to have lowest possible memory footprint
and offer an API for re-using existing objects when applicable.

#### Current Status

Currently the library fulfills most of its design objectives and is performing better than many widely adopted
libraries. It has full support for Java generics and complex types (We've stress-tested it with fields of this kind
`List<Map<String, Event<Map<String, String>, Model<A,B>>>>` and it passes). 
From a performance perspective, it performs 5x better than its closest mate, between 10x and 30x than Avro core Record
API.
The project provide a MAVEN plugin that will generate the necessary classes at compile time. At runtime, the interaction 
with Avrodite API would consist of something like so
```java
//API configuration would happen in your dependency injection layer 
Avrodite<AvroStandard, AvroCodec<?>> avrodite = AvroStandardV19.avrodite()
                                                      .discoverCodecsAt(yourAPIPackage)
                                                      .build();                                                               
//once you catch an avrodite instance you can get a codec for a given target like so
AvroCodec<Model> codec = avrodite.getCodec(Model.class);

//serialization and deserialization is then trivial
byte[] avroData = codec.encode(new Model());
Model decodedModel = codec.decode(avroData);

//You can get you schema from the codec like so
Schema schema = codec.getSchema();

```

#### Benchmark Results Snapshot

Refer to this [document](./avrodite-pages/benchmarks.md) for detailed results. 

| Framework                  | T1 Throughput | T2 Throughput | T1 rel. perf. | T2 rel. perf. |
|----------------------------|---------------|---------------|---------------|---------------|
| Avrodite                   | 3577.84       | 3035.15       | 100.00%       | 100.00%       |
| Protocol Buffers           | 573.60        | 588.06        | 16.03%        | 19.37%        |
| Avro Core (No hydration)   | 505.96        | 120.07        | 14.14%        | 3.96%         |
| Avro Core (With hydration) | 357.09        | 109.59        | 9.98%         | 3.61%         |
| Jackson Avro               | 158.75        | 89.23         | 4.44%         | 2.94%         |
| Jackson JSON               | 90.34         | 89.97         | 2.52%         | 2.96%         |
      
![Alt text](./avrodite-pages/images/bench-results.json-throughput.png?raw=true "Throughput")


#### Roadmap
The following features
- Support enums (easy).
- Support for union types (can be useful when you have a supertype with various children) (easy).
- Map API evolution to Schema migration (most likely hard).

#### Licence
Copyright (c) 2018 Yassine Echabbi
Licensed under the Apache License, Version 2.0 (the "License")
