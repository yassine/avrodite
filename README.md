## Avrodite   ![image alt >](./avrodite-pages/images/avrodite.png?raw=true#right) 
A fast, lightweight, java driven, Apache Avro serialization/deserialization library.

#### Motivation

While designing this library, the following concerns were in mind:

1. To be plain JAVA objects driven: This is particularly useful for an already existing application that 
have a set of plain JAVA objects as API. We tried the avro core implementation, but the library adds 
and additional layer of indirection/de-referencing. Moreover, the mapping between the Record 
objects/dictionaries and the existing api can quickly turn into a fastidious boilerplate to maintain.

2. Support for JAVA core Generics and complex types: Existing JAVA API models often leverage the 
typing features that the core language offers. We need a library that would support any type, 
including types with generics.

3. To be the fastest possible: Latency is a critical requirement for modern applications and real-time 
event processors. Having a library that performs at high standards, is definitely a criteria for 
its use.

4. Low memory footprint: Holding pressure from the heap by minimizing the created objects during the 
serialization / deserialization process can contribute to the overall application SLA. We need to have 
lowest possible memory footprint and offer an API for re-using existing objects when applicable.

#### Current Status

Currently the library fulfills most of its design objectives and is performing better than many widely 
adopted libraries. It has full support for Java generics and complex types (We've stress-tested it with 
fields of this kind `List<Map<String, Event<Map<String, String>, Model<A,B>>>>` and it passes). 
From a performance perspective, it performs 3x-4x better than its closest mate, between 5x and 15x than 
Avro core Record API.
The project provide a MAVEN plugin that will generate the necessary classes at compile time. At runtime, 
the interaction with Avrodite API would consist of something like so
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

#### Modules description
The library is modular and was designed with the idea of implementing other serialization
formats (JSON for example). Currently the modules are :

1. `avrodite-api`: This is the general API that projects would depends on for 
serialization/deserialization
2. `avrodite-codec-avro`: This is an implementation of the public API that provides AVRO support.
3. `avrodite-tools`: This would be mainly a compile-time/build phase dependency that contains the 
necessary logic for introspecting your beans and plain objects API. 
4. `avrodite-tools-avro`: An `avrodite-tools` plugin that generate custom classes for the AVRO binary 
format.
5. `avrodite-avro-maven-plugin`: A maven plugin that abstracts away from you the `avrodite-tools` 
stack to generate your codecs classes.
6. `avrodite-codec-avro-benchmarks`: A test module that benchmarks the AVRO implementation against 
other libraries.


#### Benchmark Results Snapshot

Refer to this [document](./avrodite-pages/benchmarks.md) for detailed results. 

| Framework                  | T1      | T1 relative Performance | T2      | T2 relative Performance |
|----------------------------|---------|-------------------------|---------|-------------------------|
| Avrodite                   | 2190.74 | 100.00%                 | 1806.33 | 100.00%                 |
| Protocol Buffers           | 578.50  | 26.41%                  | 570.08  | 31.56%                  |
| Avro Core (No hydration)   | 495.64  | 22.62%                  | 122.06  | 6.76%                   |
| Avro Core (With hydration) | 348.06  | 15.89%                  | 107.48  | 5.95%                   |
| Jackson Avro               | 158.93  | 7.25%                   | 91.50   | 5.07%                   |
| Jackson JSON               | 89.37   | 4.08%                   | 88.62   | 4.91%                   |
      
![Alt text](./avrodite-pages/images/bench-results.json-throughput.png?raw=true "Throughput")


#### Roadmap
The following features are planned for the future:
- Enums support (easy).
- Avro Union types (can be useful when you have a supertype with various children) (easy).
- Map API evolution to Schema migration (most likely hard).
- Support other serialization formats: A good portion of the library (types introspection, codec compilation) can be 
re-used to support other formats such as JSON. (average difficulty)

#### Licence
Copyright (c) 2020 Yassine Echabbi
Licensed under the Apache License, Version 2.0
