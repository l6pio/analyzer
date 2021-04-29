package l6p.analyzer.analyzer.http

import l6p.analyzer.HttpLog
import l6p.analyzer.vo.http.{Timeline, TimelineData}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class TimelineAggregatorWF extends ProcessWindowFunction[HttpLog, Timeline, String, TimeWindow] {
  override def process(key: String, ctx: Context, items: Iterable[HttpLog], out: Collector[Timeline]): Unit = {
    val isFailed = (statusCode: Int) => (statusCode == 0) || (statusCode / 100 == 4) || (statusCode / 100 == 5)

    out.collect(Timeline(
      key, ctx.window.getStart, ctx.window.getEnd,
      TimelineData(
        items.size,
        items.count(v => isFailed(v.Status)),
      )
    ))
  }
}
