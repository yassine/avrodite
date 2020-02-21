package org.avrodite.fixtures.event;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EquityMarketPriceEvent extends AbstractEvent<EventMeta, Equity> {

  private List<EquityOrder> bid;
  private List<EquityOrder> ask;

  public static EquityMarketPriceEvent create(){
    double price = 124.214;

    EquityMarketPriceEvent event = new EquityMarketPriceEvent();
    EventMeta meta = new EventMeta();
    Equity equity = new Equity();
    meta.setCorrelation("dadebd6e-dc8d-4da1-9682-9f2b02760bd2");
    meta.setId("2a49ab61-20dd-4e38-88b3-c095cd025c78");
    meta.setParentId("c3656b06-065d-481a-95fc-22cad705551e");
    equity.setPrice(price);
    equity.setTicker("TICKER");
    equity.setVolume(125855214L);
    equity.setVariation(-0.0112);
    event.setMeta(meta);
    event.setTarget(equity);
    event.setBid(IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        EquityOrder order = new EquityOrder();
        order.setPrice(price * (1 - i * 0.005) );
        order.setCount(i);
        order.setQuantity(100000 * (1 + i));
        return order;
      }).collect(Collectors.toList())
    );
    event.setAsk(IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        EquityOrder order = new EquityOrder();
        order.setPrice(price * (1 + i * 0.005) );
        order.setCount(i);
        order.setQuantity(10000 * (1 + i - 1));
        return order;
      }).collect(Collectors.toList())
    );

    return event;
  }

  public static final byte[] NULLABLE_FIELDS_SERIALIZED_MODEL = new byte[] {
    0, 0, 72, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45, 56, 56,
    98, 51, 45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 0, 72, 99, 51, 54, 53, 54, 98, 48,
    54, 45, 48, 54, 53, 100, 45, 52, 56, 49, 97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97, 100, 55,
    48, 53, 53, 53, 49, 101, 0, 72, 100, 97, 100, 101, 98, 100, 54, 101, 45, 100, 99, 56, 100, 45,
    52, 100, 97, 49, 45, 57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50, 55, 54, 48, 98, 100, 50, 0, 0,
    12, 84, 73, 67, 75, 69, 82, 0, 4, 86, 14, 45, -78, 13, 95, 64, 0, -36, -105, -125, 120, 0, -57,
    -70, -72, -115, 6, -16, -122, -65, 0, 10, 0, 2, 0, 78, -76, -85, -112, -14, -27, 94, 64, 0, -128,
    -75, 24, 0, 4, 0, -104, 18, 73, -12, 50, -66, 94, 64, 0, -64, -49, 36, 0, 6, 0, -29, 112, -26,
    87, 115, -106, 94, 64, 0, -128, -22, 48, 0, 8, 0, 45, -49, -125, -69, -77, 110, 94, 64, 0, -64,
    -124, 61, 0, 10, 0, 119, 45, 33, 31, -12, 70, 94, 64, 0, -128, -97, 73, 0, 0, 10, 0, 2, 0, -71,
    -9, 112, -55, 113, 53, 95, 64, 0, -96, -100, 1, 0, 4, 0, 112, -103, -45, 101, 49, 93, 95, 64, 0,
    -64, -72, 2, 0, 6, 0, 36, 59, 54, 2, -15, -124, 95, 64, 0, -32, -44, 3, 0, 8, 0, -37, -36, -104,
    -98, -80, -84, 95, 64, 0, -128, -15, 4, 0, 10, 0, -112, 126, -5, 58, 112, -44, 95, 64, 0, -96,
    -115, 6, 0
  };

  public static final byte[] NON_NULLABLE_FIELDS_SERIALIZED_MODEL = new byte[] {
    72, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45, 56, 56, 98, 51,
    45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 72, 99, 51, 54, 53, 54, 98, 48, 54, 45, 48,
    54, 53, 100, 45, 52, 56, 49, 97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97, 100, 55, 48, 53, 53, 53,
    49, 101, 72, 100, 97, 100, 101, 98, 100, 54, 101, 45, 100, 99, 56, 100, 45, 52, 100, 97, 49, 45,
    57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50, 55, 54, 48, 98, 100, 50, 12, 84, 73, 67, 75, 69, 82,
    4, 86, 14, 45, -78, 13, 95, 64, -36, -105, -125, 120, -57, -70, -72, -115, 6, -16, -122, -65, 10,
    2, 78, -76, -85, -112, -14, -27, 94, 64, -128, -75, 24, 4, -104, 18, 73, -12, 50, -66, 94, 64,
    -64, -49, 36, 6, -29, 112, -26, 87, 115, -106, 94, 64, -128, -22, 48, 8, 45, -49, -125, -69, -77,
    110, 94, 64, -64, -124, 61, 10, 119, 45, 33, 31, -12, 70, 94, 64, -128, -97, 73, 0, 10, 2, -71,
    -9, 112, -55, 113, 53, 95, 64, -96, -100, 1, 4, 112, -103, -45, 101, 49, 93, 95, 64, -64, -72, 2,
    6, 36, 59, 54, 2, -15, -124, 95, 64, -32, -44, 3, 8, -37, -36, -104, -98, -80, -84, 95, 64, -128,
    -15, 4, 10, -112, 126, -5, 58, 112, -44, 95, 64, -96, -115, 6, 0
  };

  public static final String SERIALIZED_MODEL_SCHEMA = "{\n" +
    "  \"type\" : \"record\",\n" +
    "  \"name\" : \"EquityMarketPriceEvent__BA13F0BA\",\n" +
    "  \"namespace\" : \"org.avrodite.fixtures.event\",\n" +
    "  \"fields\" : [ {\n" +
    "    \"name\" : \"meta\",\n" +
    "    \"type\" : [ {\n" +
    "      \"type\" : \"record\",\n" +
    "      \"name\" : \"EventMeta__7C3A6F98\",\n" +
    "      \"fields\" : [ {\n" +
    "        \"name\" : \"id\",\n" +
    "        \"type\" : [ \"string\", \"null\" ]\n" +
    "      }, {\n" +
    "        \"name\" : \"parentId\",\n" +
    "        \"type\" : [ \"string\", \"null\" ]\n" +
    "      }, {\n" +
    "        \"name\" : \"correlation\",\n" +
    "        \"type\" : [ \"string\", \"null\" ]\n" +
    "      } ],\n" +
    "      \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EventMeta\"\n" +
    "    }, \"null\" ]\n" +
    "  }, {\n" +
    "    \"name\" : \"target\",\n" +
    "    \"type\" : [ {\n" +
    "      \"type\" : \"record\",\n" +
    "      \"name\" : \"Equity__862C9147\",\n" +
    "      \"fields\" : [ {\n" +
    "        \"name\" : \"ticker\",\n" +
    "        \"type\" : [ \"string\", \"null\" ]\n" +
    "      }, {\n" +
    "        \"name\" : \"price\",\n" +
    "        \"type\" : [ \"double\", \"null\" ]\n" +
    "      }, {\n" +
    "        \"name\" : \"volume\",\n" +
    "        \"type\" : [ \"long\", \"null\" ]\n" +
    "      }, {\n" +
    "        \"name\" : \"variation\",\n" +
    "        \"type\" : [ \"double\", \"null\" ]\n" +
    "      } ],\n" +
    "      \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.Equity\"\n" +
    "    }, \"null\" ]\n" +
    "  }, {\n" +
    "    \"name\" : \"bid\",\n" +
    "    \"type\" : [ {\n" +
    "      \"type\" : \"array\",\n" +
    "      \"items\" : {\n" +
    "        \"type\" : \"record\",\n" +
    "        \"name\" : \"EquityOrder__D2EFF3F5\",\n" +
    "        \"fields\" : [ {\n" +
    "          \"name\" : \"count\",\n" +
    "          \"type\" : [ \"int\", \"null\" ]\n" +
    "        }, {\n" +
    "          \"name\" : \"price\",\n" +
    "          \"type\" : [ \"double\", \"null\" ]\n" +
    "        }, {\n" +
    "          \"name\" : \"quantity\",\n" +
    "          \"type\" : [ \"long\", \"null\" ]\n" +
    "        } ],\n" +
    "        \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EquityOrder\"\n" +
    "      }\n" +
    "    }, \"null\" ]\n" +
    "  }, {\n" +
    "    \"name\" : \"ask\",\n" +
    "    \"type\" : [ {\n" +
    "      \"type\" : \"array\",\n" +
    "      \"items\" : \"EquityOrder__D2EFF3F5\"\n" +
    "    }, \"null\" ]\n" +
    "  } ],\n" +
    "  \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EquityMarketPriceEvent\"\n" +
    "}";

  public static final String NON_NULLABLE_FIELDS_SERIALIZED_MODEL_SCHEMA = "{\n" +
    "  \"type\" : \"record\",\n" +
    "  \"name\" : \"EquityMarketPriceEvent__BA13F0BA\",\n" +
    "  \"namespace\" : \"org.avrodite.fixtures.event\",\n" +
    "  \"fields\" : [ {\n" +
    "    \"name\" : \"meta\",\n" +
    "    \"type\" : {\n" +
    "      \"type\" : \"record\",\n" +
    "      \"name\" : \"EventMeta__7C3A6F98\",\n" +
    "      \"fields\" : [ {\n" +
    "        \"name\" : \"id\",\n" +
    "        \"type\" : \"string\"\n" +
    "      }, {\n" +
    "        \"name\" : \"parentId\",\n" +
    "        \"type\" : \"string\"\n" +
    "      }, {\n" +
    "        \"name\" : \"correlation\",\n" +
    "        \"type\" : \"string\"\n" +
    "      } ],\n" +
    "      \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EventMeta\"\n" +
    "    }\n" +
    "  }, {\n" +
    "    \"name\" : \"target\",\n" +
    "    \"type\" : {\n" +
    "      \"type\" : \"record\",\n" +
    "      \"name\" : \"Equity__862C9147\",\n" +
    "      \"fields\" : [ {\n" +
    "        \"name\" : \"ticker\",\n" +
    "        \"type\" : \"string\"\n" +
    "      }, {\n" +
    "        \"name\" : \"price\",\n" +
    "        \"type\" : \"double\"\n" +
    "      }, {\n" +
    "        \"name\" : \"volume\",\n" +
    "        \"type\" : \"long\"\n" +
    "      }, {\n" +
    "        \"name\" : \"variation\",\n" +
    "        \"type\" : \"double\"\n" +
    "      } ],\n" +
    "      \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.Equity\"\n" +
    "    }\n" +
    "  }, {\n" +
    "    \"name\" : \"bid\",\n" +
    "    \"type\" : {\n" +
    "      \"type\" : \"array\",\n" +
    "      \"items\" : {\n" +
    "        \"type\" : \"record\",\n" +
    "        \"name\" : \"EquityOrder__D2EFF3F5\",\n" +
    "        \"fields\" : [ {\n" +
    "          \"name\" : \"count\",\n" +
    "          \"type\" : \"int\"\n" +
    "        }, {\n" +
    "          \"name\" : \"price\",\n" +
    "          \"type\" : \"double\"\n" +
    "        }, {\n" +
    "          \"name\" : \"quantity\",\n" +
    "          \"type\" : \"long\"\n" +
    "        } ],\n" +
    "        \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EquityOrder\"\n" +
    "      }\n" +
    "    }\n" +
    "  }, {\n" +
    "    \"name\" : \"ask\",\n" +
    "    \"type\" : {\n" +
    "      \"type\" : \"array\",\n" +
    "      \"items\" : \"EquityOrder__D2EFF3F5\"\n" +
    "    }\n" +
    "  } ],\n" +
    "  \"org.avrodite.avro.javaType\" : \"org.avrodite.fixtures.event.EquityMarketPriceEvent\"\n" +
    "}";

}
