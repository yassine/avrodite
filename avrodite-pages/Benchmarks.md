## Benchmark Results

#### Test description

The benchmark attempts to capture the end-to-end latency for serialization/deserialization,
like so (in pseudo-code):

```
benchmark() {
  serialize(deserialize(fixture))
}
```

We run 2 tests:
1. Test 1 (T1): All fields are nullable
2. Test 2 (T2): All fields are NON nullable

The tests classes are available under the 'avrodite-codec-avro-benchmarks' module.

#### Test Environment

| Component | Description                              |
|-----------|------------------------------------------|
| Processor | Intel(R) Core(TM) i7-6900K CPU @ 3.20GHz |
| Linux     | 5.3.0-29-generic                         |
| JDK       | OpenJDK 64-Bit Server VM 12.0.2+10       |
| JVM Args  | -server, -Xms1G, -Dfile.encoding=UTF-8   |


##### Tested Frameworks

| Framework              | Version    |
|------------------------|------------|
| Avro Core              | 1.8.1      |
| Avrodite               | 0.1.0      |
| Jackson Avro           | 2.8.5      |
| Jackson JSON           | 2.8.5      |
| Protocol Buffers       | 3.11.3     |

#### Results Summary

##### Throughput
| Framework | T1 throughput [ ops/ms ] | T1 relative perf. |T2 throughput [ ops/ms ] | T2 relative perf. |
|-----------|------------|--------------|------------|--------------|
| avrodite | 1836 | 100.00% | 2260 | 100.00% | 
| protocolBuffers | 582 | 31.68% | 580 | 25.66% | 
| avroCoreNoHydration | 108 | 5.87% | 504 | 22.30% | 
| avroCoreWithHydration | 103 | 5.63% | 349 | 15.45% | 
| jacksonAvro | 86 | 4.69% | 151 | 6.67% | 
| jacksonJSON | 87 | 4.75% | 87 | 3.87% | 


##### Heap Usage

| Framework | T1 Heap Allocation Rate [ Byte/op ] | T1 relative perf. |T2 Heap Allocation Rate [ Byte/op ] | T2 relative perf. |
|-----------|------------|--------------|------------|--------------|
| avrodite | 1024 | 100.00% | 1024 | 100.00% | 
| avroCoreNoHydration | 2248 | 219.53% | 2248 | 219.53% | 
| avroCoreWithHydration | 3128 | 305.47% | 3128 | 305.47% | 
| protocolBuffers | 6088 | 594.53% | 6088 | 594.53% | 
| jacksonJSON | 10320 | 1007.81% | 10320 | 1007.81% | 
| jacksonAvro | 16264 | 1588.28% | 16296 | 1591.41% | 

#### Results Charts

### T1 throughput [ ops/ms ]
(Higher is better)

![Alt text](./images/T1.thrpt.png?raw=true "T1 throughput [ ops/ms ]")

### T1 Heap Allocation Rate [ Byte/op ]
(Lower is better)

![Alt text](./images/T1.gc.png?raw=true "T1 Heap Allocation Rate [ Byte/op ]")


### T2 throughput [ ops/ms ]
(Higher is better)

![Alt text](./images/T2.thrpt.png?raw=true "T2 throughput [ ops/ms ]")

### T2 Heap Allocation Rate [ Byte/op ]
(Lower is better)

![Alt text](./images/T2.gc.png?raw=true "T2 Heap Allocation Rate [ Byte/op ]")




##### Test Fixture (in JSON)
```json
{
  "meta" : {
    "id" : "471373af-dc25-459d-bdb5-2318edbf7074",
    "parentId" : "f453e8d8-ab9a-493e-bad3-b23eac518732",
    "correlation" : "1be7f862-37e9-4fce-901e-12435ceb2776"
  },
  "target" : {
    "ticker" : "TICKER",
    "price" : 124.214,
    "volume" : 125855214,
    "variation" : -0.0112
  },
  "bid" : [ {
    "count" : 1,
    "price" : 123.59293,
    "quantity" : 200000
  }, {
    "count" : 2,
    "price" : 122.97185999999999,
    "quantity" : 300000
  }, {
    "count" : 3,
    "price" : 122.35079,
    "quantity" : 400000
  }, {
    "count" : 4,
    "price" : 121.72972,
    "quantity" : 500000
  }, {
    "count" : 5,
    "price" : 121.10865,
    "quantity" : 600000
  } ],
  "ask" : [ {
    "count" : 1,
    "price" : 124.83506999999999,
    "quantity" : 10000
  }, {
    "count" : 2,
    "price" : 125.45614,
    "quantity" : 20000
  }, {
    "count" : 3,
    "price" : 126.07720999999998,
    "quantity" : 30000
  }, {
    "count" : 4,
    "price" : 126.69828,
    "quantity" : 40000
  }, {
    "count" : 5,
    "price" : 127.31934999999999,
    "quantity" : 50000
  } ]
}
```
#### Use Case 1 : Nullable Fields Schema
```json
{
  "type" : "record",
  "name" : "EquityMarketPriceEvent__BA13F0BA",
  "namespace" : "org.avrodite.fixtures.event",
  "fields" : [ {
    "name" : "meta",
    "type" : [ {
      "type" : "record",
      "name" : "EventMeta__7C3A6F98",
      "fields" : [ {
        "name" : "id",
        "type" : [ "string", "null" ]
      }, {
        "name" : "parentId",
        "type" : [ "string", "null" ]
      }, {
        "name" : "correlation",
        "type" : [ "string", "null" ]
      } ],
      "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EventMeta"
    }, "null" ]
  }, {
    "name" : "target",
    "type" : [ {
      "type" : "record",
      "name" : "Equity__862C9147",
      "fields" : [ {
        "name" : "ticker",
        "type" : [ "string", "null" ]
      }, {
        "name" : "price",
        "type" : [ "double", "null" ]
      }, {
        "name" : "volume",
        "type" : [ "long", "null" ]
      }, {
        "name" : "variation",
        "type" : [ "double", "null" ]
      } ],
      "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.Equity"
    }, "null" ]
  }, {
    "name" : "bid",
    "type" : [ {
      "type" : "array",
      "items" : {
        "type" : "record",
        "name" : "EquityOrder__D2EFF3F5",
        "fields" : [ {
          "name" : "count",
          "type" : [ "int", "null" ]
        }, {
          "name" : "price",
          "type" : [ "double", "null" ]
        }, {
          "name" : "quantity",
          "type" : [ "long", "null" ]
        } ],
        "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EquityOrder"
      }
    }, "null" ]
  }, {
    "name" : "ask",
    "type" : [ {
      "type" : "array",
      "items" : "EquityOrder__D2EFF3F5"
    }, "null" ]
  } ],
  "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EquityMarketPriceEvent"
}
```
#### Use Case 2 : Non Nullable Fields Schema
```
{
  "type" : "record",
  "name" : "EquityMarketPriceEvent__BA13F0BA",
  "namespace" : "org.avrodite.fixtures.event",
  "fields" : [ {
    "name" : "meta",
    "type" : {
      "type" : "record",
      "name" : "EventMeta__7C3A6F98",
      "fields" : [ {
        "name" : "id",
        "type" : "string"
      }, {
        "name" : "parentId",
        "type" : "string"
      }, {
        "name" : "correlation",
        "type" : "string"
      } ],
      "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EventMeta"
    }
  }, {
    "name" : "target",
    "type" : {
      "type" : "record",
      "name" : "Equity__862C9147",
      "fields" : [ {
        "name" : "ticker",
        "type" : "string"
      }, {
        "name" : "price",
        "type" : "double"
      }, {
        "name" : "volume",
        "type" : "long"
      }, {
        "name" : "variation",
        "type" : "double"
      } ],
      "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.Equity"
    }
  }, {
    "name" : "bid",
    "type" : {
      "type" : "array",
      "items" : {
        "type" : "record",
        "name" : "EquityOrder__D2EFF3F5",
        "fields" : [ {
          "name" : "count",
          "type" : "int"
        }, {
          "name" : "price",
          "type" : "double"
        }, {
          "name" : "quantity",
          "type" : "long"
        } ],
        "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EquityOrder"
      }
    }
  }, {
    "name" : "ask",
    "type" : {
      "type" : "array",
      "items" : "EquityOrder__D2EFF3F5"
    }
  } ],
  "org.avrodite.avro.javaType" : "org.avrodite.fixtures.event.EquityMarketPriceEvent"
}
```
