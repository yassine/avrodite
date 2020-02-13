##Benchmark Results

#### Test description

The benchmark attempts to capture the end-to-end latency for serialization/deserialization,
like so (in pseudo-code):

```
benchmark() {
  serialize(deserialize(fixture))
}
``` 

We run two tests:
1. Test 1 (T1): Here we declared all the fields of the test fixture as non nullable (See model and schema below).
2. Test 2 (T2): Here we declared all the fields of the test fixture as nullable (See model and schema below).

The tests are available under the 'avrodite-codec-avro-benchmarks'.

#### Test Environment

| Component | Description                              |
|-----------|------------------------------------------|
| Processor | Intel(R) Core(TM) i7-6900K CPU @ 3.20GHz |
| Linux     | 5.3.0-29-generic                         |
| JDK       | OpenJDK 64-Bit Server VM                 |
| JVM Args  | -Xms1G -server                           |

#### Tested Frameworks

| Framework              | Version    |
|------------------------|------------|
| Avro Core              | 1.8.1      |
| Avrodite               | 0.1.0      |
| Jackson Avro           | 2.8.5      |
| Jackson JSON           | 2.8.5      |
| Protocol Buffers       | 3.11.3     |



#### Results Summary

| Framework                  | T1 Throughput | T2 Throughput | T2 Throughput | T2 rel. perf. |
|----------------------------|---------------|---------------|---------------|---------------|
| Avrodite                   | 3577.84       | 3035.15       | 100.00%       | 100.00%       |
| Protocol Buffers           | 573.60        | 588.06        | 16.03%        | 19.37%        |
| Avro Core (No hydration)   | 505.96        | 120.07        | 14.14%        | 3.96%         |
| Avro Core (With hydration) | 357.09        | 109.59        | 9.98%         | 3.61%         |
| Jackson Avro               | 158.75        | 89.23         | 4.44%         | 2.94%         |
| Jackson JSON               | 90.34         | 89.97         | 2.52%         | 2.96%         |

##### Throughput (Test 1, Higher is better)
![Alt text](./images/bench-results.json-throughput.png?raw=true&v=9 "Throughput")


##### Throughput (Test 2, Higher is better)
![Alt text](./images/bench-results-nullable-fields.json-throughput.png?raw=true&v=5 "Throughput")


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
