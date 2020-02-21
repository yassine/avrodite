package org.avrodite.avro.state;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.proto.EquityEvents;
import org.avrodite.proto.required.EquityEventsNonNullableFields;

public class ProtocolBuffersBenchmarkState {
  public final ByteArrayInputStream protobufIs;
  public final ByteArrayOutputStream protobufOs;
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  public final EquityEventsNonNullableFields.EquityMarketPriceEvent nonNullableFieldsEvent;
  public final EquityEvents.EquityMarketPriceEvent nullableFieldsEvent;

  public ProtocolBuffersBenchmarkState() {
    protobufIs = new ByteArrayInputStream(PROTOBUF_BYTES);
    protobufOs = new ByteArrayOutputStream(PROTOBUF_BYTES.length);

    nonNullableFieldsEvent = EquityEventsNonNullableFields.EquityMarketPriceEvent.newBuilder()
      .setMeta(
        EquityEventsNonNullableFields.EventMeta.newBuilder()
          .setCorrelation(model.getMeta().getCorrelation())
          .setId(model.getMeta().getId())
          .setParentId(model.getMeta().getParentId())
      ).setTarget(
        EquityEventsNonNullableFields.Equity.newBuilder()
          .setPrice(model.getTarget().getPrice())
          .setVolume(model.getTarget().getVolume())
          .setTicker(model.getTarget().getTicker())
          .setVariation(model.getTarget().getVariation())
      ).addAllAsk(
        model.getAsk().stream()
          .map(ask -> EquityEventsNonNullableFields.EquityOrder.newBuilder()
            .setCount(ask.getCount())
            .setQuantity(ask.getQuantity())
            .setPrice(ask.getPrice())
            .build()
          )
          .collect(toList())
      ).addAllBid(
        model.getBid().stream()
          .map(ask -> EquityEventsNonNullableFields.EquityOrder.newBuilder()
            .setCount(ask.getCount())
            .setQuantity(ask.getQuantity())
            .setPrice(ask.getPrice())
            .build()
          )
          .collect(toList())
      ).build();
    nullableFieldsEvent = EquityEvents.EquityMarketPriceEvent.newBuilder()
      .setMeta(
        EquityEvents.EventMeta.newBuilder()
          .setCorrelation(model.getMeta().getCorrelation())
          .setId(model.getMeta().getId())
          .setParentId(model.getMeta().getParentId())
      ).setTarget(
        EquityEvents.Equity.newBuilder()
          .setPrice(model.getTarget().getPrice())
          .setVolume(model.getTarget().getVolume())
          .setTicker(model.getTarget().getTicker())
          .setVariation(model.getTarget().getVariation())
      ).addAllAsk(
        model.getAsk().stream()
          .map(ask -> EquityEvents.EquityOrder.newBuilder()
            .setCount(ask.getCount())
            .setQuantity(ask.getQuantity())
            .setPrice(ask.getPrice())
            .build()
          )
          .collect(toList())
      ).addAllBid(
        model.getBid().stream()
          .map(ask -> EquityEvents.EquityOrder.newBuilder()
            .setCount(ask.getCount())
            .setQuantity(ask.getQuantity())
            .setPrice(ask.getPrice())
            .build()
          )
          .collect(toList())
      ).build();
  }

  public static final byte[] PROTOBUF_BYTES = new byte[] {
    10, 114, 10, 36, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45,
    56, 56, 98, 51, 45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 18, 36, 99, 51, 54, 53, 54,
    98, 48, 54, 45, 48, 54, 53, 100, 45, 52, 56, 49, 97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97,
    100, 55, 48, 53, 53, 53, 49, 101, 26, 36, 100, 97, 100, 101, 98, 100, 54, 101, 45, 100, 99, 56,
    100, 45, 52, 100, 97, 49, 45, 57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50, 55, 54, 48, 98, 100,
    50, 18, 31, 10, 6, 84, 73, 67, 75, 69, 82, 17, 4, 86, 14, 45, -78, 13, 95, 64, 24, -18, -53,
    -127, 60, 33, -57, -70, -72, -115, 6, -16, -122, -65, 26, 14, 8, 1, 17, -71, -9, 112, -55, 113,
    53, 95, 64, 24, -112, 78, 26, 15, 8, 2, 17, 112, -103, -45, 101, 49, 93, 95, 64, 24, -96, -100,
    1, 26, 15, 8, 3, 17, 36, 59, 54, 2, -15, -124, 95, 64, 24, -80, -22, 1, 26, 15, 8, 4, 17, -37,
    -36, -104, -98, -80, -84, 95, 64, 24, -64, -72, 2, 26, 15, 8, 5, 17, -112, 126, -5, 58, 112, -44,
    95, 64, 24, -48, -122, 3, 34, 15, 8, 1, 17, 78, -76, -85, -112, -14, -27, 94, 64, 24, -64, -102,
    12, 34, 15, 8, 2, 17, -104, 18, 73, -12, 50, -66, 94, 64, 24, -32, -89, 18, 34, 15, 8, 3, 17,
    -29, 112, -26, 87, 115, -106, 94, 64, 24, -128, -75, 24, 34, 15, 8, 4, 17, 45, -49, -125, -69,
    -77, 110, 94, 64, 24, -96, -62, 30, 34, 15, 8, 5, 17, 119, 45, 33, 31, -12, 70, 94, 64, 24, -64,
    -49, 36
  };


}
