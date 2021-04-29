package l6p.analyzer

import l6p.analyzer.Const.{MaxOutOfOrder, WatermarkInterval}
import org.apache.flink.api.common.eventtime._
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase
import org.apache.flink.util.Collector

case class DataStreams
(
  MainStream: DataStream[LogItem],
  HttpStream: DataStream[HttpLog]
)

class PeriodicWatermarkGenerator extends WatermarkGenerator[LogItem] {
  private var currentMaxTimestamp = 0L
  private var autoAscendTimestamp = 0L

  override def onEvent(event: LogItem, eventTimestamp: Long, output: WatermarkOutput): Unit = {
    currentMaxTimestamp = Math.max(currentMaxTimestamp, eventTimestamp)
    autoAscendTimestamp = currentMaxTimestamp
  }

  override def onPeriodicEmit(output: WatermarkOutput): Unit = {
    val maxTimestamp = Math.max(currentMaxTimestamp, autoAscendTimestamp)
    output.emitWatermark(new Watermark(maxTimestamp - MaxOutOfOrder))
    autoAscendTimestamp += WatermarkInterval
  }
}

object StreamGenerator {
  def apply()(implicit env: StreamExecutionEnvironment, source: FlinkKafkaConsumerBase[LogItem]): DataStreams = {
    val httpStreamTag = OutputTag[HttpLog]("http")

    val mainStream: DataStream[LogItem] = env.
      addSource(source).
      assignTimestampsAndWatermarks(
        WatermarkStrategy.forGenerator[LogItem](
          (_: WatermarkGeneratorSupplier.Context) => new PeriodicWatermarkGenerator
        ).withTimestampAssigner(new SerializableTimestampAssigner[LogItem] {
          override def extractTimestamp(t: LogItem, l: Long): Long = t.Timestamp
        })
      ).
      process((item: LogItem, ctx: ProcessFunction[LogItem, LogItem]#Context, out: Collector[LogItem]) => {
        ctx.output(httpStreamTag, HttpLog(
          item.Name, item.Timestamp,
          item.Data.get("url").toString, item.Data.get("method").toString, item.Data.get("status").toInt,
          item.Data.get("trace").asMap().get("firstResponseDelta").toInt
        ))
      })

    val httpDataStream: DataStream[HttpLog] = mainStream.getSideOutput(httpStreamTag)
    DataStreams(mainStream, httpDataStream)
  }
}

case class HttpLog
(
  Name: String, Timestamp: Long,
  Url: String, Method: String, Status: Int,
  Duration: Int,
)
