package l6p.analyzer.analyzer.http

import l6p.analyzer.vo.http.{Summary, SummaryData}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class SummaryAggregatorWF extends ProcessWindowFunction[SummaryData, Summary, String, TimeWindow] {
  override def process(key: String, context: Context, elements: Iterable[SummaryData], out: Collector[Summary]): Unit = {
    out.collect(Summary(key, elements.iterator.next()))
  }
}
