package l6p.analyzer

import l6p.analyzer.Const.{MaxSessionDuration, TimelineWindow, TriggerPeriod, WatermarkInterval}
import l6p.analyzer.tools.Logging
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.triggers.ContinuousEventTimeTrigger
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase

object App extends Logging {
  def main(args: Array[String]): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setAutoWatermarkInterval(WatermarkInterval)

    val mongoUrl = s"mongodb://${sys.env("MONGODB_USER")}:${sys.env("MONGODB_PASS")}@${sys.env("MONGODB_HOST")}"
    val kafkaEndpoint = sys.env("KAFKA_ENDPOINT")
    val kafkaTopic = sys.env("KAFKA_TOPIC")

    println(s"MongoDB Address: $mongoUrl")
    println(s"Kafka Endpoint: $kafkaEndpoint")
    println(s"Kafka Topic: $kafkaTopic")

    implicit val kafkaSource: FlinkKafkaConsumerBase[LogItem] = KafkaSource(kafkaEndpoint, kafkaTopic)

    val streams: DataStreams = StreamGenerator()
    httpStream(mongoUrl, streams)

    env.execute()
  }

  def httpStream(mongoUrl: String, streams: DataStreams): Unit = {
    import l6p.analyzer.analyzer.http.{SummaryAggregator, SummaryAggregatorWF, TimelineAggregatorWF}
    import l6p.analyzer.sink.http.{SummarySink, TimelineSink}

    streams.HttpStream.
      keyBy(_.Name).
      window(EventTimeSessionWindows.withGap(MaxSessionDuration)).
      trigger(ContinuousEventTimeTrigger.of[TimeWindow](TriggerPeriod)).
      aggregate(new SummaryAggregator, new SummaryAggregatorWF).
      name("http summary").
      addSink(new SummarySink(mongoUrl))

    streams.HttpStream.
      keyBy(_.Name).
      window(TumblingEventTimeWindows.of(TimelineWindow)).
      trigger(ContinuousEventTimeTrigger.of[TimeWindow](TriggerPeriod)).
      process(new TimelineAggregatorWF).
      name("http timeline").
      addSink(new TimelineSink(mongoUrl))
  }
}
