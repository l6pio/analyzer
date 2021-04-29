package l6p.analyzer.sink

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoClient, MongoDatabase}

abstract class MongoDbSink[T](mongoUrl: String) extends RichSinkFunction[T] {
  var client: MongoClient = _
  var db: MongoDatabase = _

  def codecRegistry(): CodecRegistry

  override def open(parameters: Configuration): Unit = {
    val client: MongoClient = MongoClient(this.mongoUrl)
    db = client.getDatabase("l6p-report").withCodecRegistry(codecRegistry())
  }

  override def close(): Unit = {
    client.close()
  }
}
