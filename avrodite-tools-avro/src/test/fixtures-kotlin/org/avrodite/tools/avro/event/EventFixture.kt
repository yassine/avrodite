package org.avrodite.tools.avro.event

import java.time.Instant

abstract class AbstractEvent<M : EventMeta, T>(val meta: M, val target: T) {
  override fun toString(): String {
    return "AbstractEvent(meta=$meta, target=$target)"
  }
}

open class EventMeta(val id: String, val parentId: String, val correlation: String){
  override fun toString(): String {
    return "EventMeta(id='$id', parentId='$parentId', correlation='$correlation')"
  }
}
class EquityOrder(val count: Int, val price: Double, val quantity: Long){
  override fun toString(): String {
    return "EquityOrder(count=$count, price=$price, quantity=$quantity)"
  }
}
class Equity(val ticker: String, val price: Double, val volume: Long, val variation: Double) {
  override fun toString(): String {
    return "Equity(ticker='$ticker', price=$price, volume=$volume, variation=$variation)"
  }
}

class EquityMarketPriceEvent(meta: EventMeta, target: Equity, val instant: Instant, val bid: List<EquityOrder>, val ask: List<EquityOrder>)
  : AbstractEvent<EventMeta, Equity>(meta, target) {

  companion object {
    fun createEvent() : EquityMarketPriceEvent {
      val price = 124.214
      return EquityMarketPriceEvent(
        EventMeta(
          "2a49ab61-20dd-4e38-88b3-c095cd025c78",
          "c3656b06-065d-481a-95fc-22cad705551e",
          "dadebd6e-dc8d-4da1-9682-9f2b02760bd2"
        ),
        Equity(
          "TICKER",
          price,
          125855214L,
          -0.0112
        ),
        Instant.now(),
        IntRange(1, 5)
          .map { EquityOrder(it, price * (1 - it * 0.005), 100000L * (1 + it)) }
          .toList(),
        IntRange(1, 5)
          .map { EquityOrder(it, price * (1 + it * 0.005), 10000L * it) }
          .toList()
      )
    }
  }

  override fun toString(): String {
    return "${super.toString()} EquityMarketPriceEvent(bid=$bid, ask=$ask)"
  }
}

