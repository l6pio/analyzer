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

class SummarySink(mongoUrl: String) extends MongoDbSink[Summary](mongoUrl) {
  override def invoke(v: Summary, c: SinkFunction.Context): Unit = {
    val col1: MongoCollection[SummaryDB] = db.getCollection(v.name)
    val col2: MongoCollection[UrlDB] = db.getCollection(v.name)

    val observer = new Observer[UpdateResult] {
      override def onError(e: Throwable): Unit = {}

      override def onComplete(): Unit = {}

      override def onNext(result: UpdateResult): Unit = {}
    }

    col1.replaceOne(equal("dataType", 2),
      SummaryDB(2, v.data.count, v.data.failed,
        v.data.byPercentile, v.data.byMethod, v.data.byStatus
      ),
      new ReplaceOptions().upsert(true)
    ).subscribe(observer)

    v.data.byUrl.foreach(o => {
      col2.replaceOne(
        and(
          equal("dataType", 3),
          equal("url", o.url)
        ),
        UrlDB(3, o.url, o.count, o.failed, o.avg, o.cv),
        new ReplaceOptions().upsert(true)
      ).subscribe(observer)
    })
  }

  override def codecRegistry(): CodecRegistry = {
    fromRegistries(
      fromProviders(
        classOf[SummaryDB], classOf[ByPercentileData], classOf[ByMethodData], classOf[ByStatusData],
        classOf[UrlDB]
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}
