package l6p.analyzer.sink.http

import l6p.analyzer.sink.MongoDbSink
import l6p.analyzer.vo.http._
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.bson.codecs.configuration.CodecRegistries._
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.ReplaceOptions
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{MongoCollection, Observer}

class TimelineSink(mongoUrl: String) extends MongoDbSink[Timeline](mongoUrl) {
  override def invoke(v: Timeline, c: SinkFunction.Context): Unit = {
    val col: MongoCollection[TimelineDB] = db.getCollection(v.name)

    val observer = new Observer[UpdateResult] {
      override def onError(e: Throwable): Unit = {}

      override def onComplete(): Unit = {}

      override def onNext(result: UpdateResult): Unit = {}
    }

    col.replaceOne(
      and(
        equal("dataType", 1),
        equal("from", v.from),
        equal("to", v.to),
      ),
      TimelineDB(1, v.from, v.to, v.data.count, v.data.failed),
      new ReplaceOptions().upsert(true)
    ).subscribe(observer)
  }

  override def codecRegistry(): CodecRegistry = {
    fromRegistries(
      fromProviders(classOf[TimelineDB]),
      DEFAULT_CODEC_REGISTRY
    )
  }
}
