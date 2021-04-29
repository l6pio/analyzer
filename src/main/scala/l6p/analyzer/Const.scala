package l6p.analyzer

import org.apache.flink.streaming.api.windowing.time.Time

object Const {
  val KafkaGroupId = "l6p.analyzer"
  val MaxSessionDuration: Time = Time.seconds(60 * 60)
  val TimelineWindow: Time = Time.seconds(60)
  val TriggerPeriod: Time = Time.seconds(10)
  val WatermarkInterval = 5000L
  val MaxOutOfOrder: Long = 15 * 1000L
}
