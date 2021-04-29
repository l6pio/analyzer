package l6p.analyzer

import java.nio.charset.StandardCharsets
import java.util
import java.util.Properties

import com.jsoniter.{JsonIterator, any}
import l6p.analyzer.Const.KafkaGroupId
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaConsumerBase, KafkaDeserializationSchema}
import org.apache.kafka.clients.consumer.ConsumerRecord

object KafkaSource {
  def apply(kafkaEndpoint: String, kafkaTopic: String): FlinkKafkaConsumerBase[LogItem] = {
    val props = new Properties()
    props.setProperty("bootstrap.servers", kafkaEndpoint)
    props.setProperty("group.id", KafkaGroupId)
    new FlinkKafkaConsumer(kafkaTopic, KafkaStringSchema, props).setStartFromLatest()
  }

  object KafkaStringSchema extends KafkaDeserializationSchema[LogItem] {
    override def isEndOfStream(nextElement: LogItem): Boolean = false

    override def getProducedType: TypeInformation[LogItem] = TypeExtractor.getForClass(classOf[LogItem])

    override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): LogItem = {
      val name = new String(record.key(), StandardCharsets.UTF_8)
      val data = JsonIterator.deserialize(new String(record.value(), StandardCharsets.UTF_8)).asMap()
      val timestamp = data.get("timestamp").toLong
      LogItem(name, timestamp, data)
    }
  }
}

case class LogItem(var Name: String, var Timestamp: Long, var Data: util.Map[String, any.Any]) {
  def this() {
    this("", 0, new util.HashMap[String, any.Any]())
  }
}
