package l6p.analyzer.analyzer.http

import l6p.analyzer.HttpLog
import l6p.analyzer.tools.MapHelper._
import l6p.analyzer.tools.MathUtil._
import l6p.analyzer.vo.http._
import org.apache.flink.api.common.functions.AggregateFunction

class SummaryAggregator extends AggregateFunction[HttpLog, Acc, SummaryData] {
  override def createAccumulator(): Acc = Acc(
    0, 0,
    List(),
    Map().withDefaultValue(StatusAcc(0)),
    Map().withDefaultValue(MethodAcc(0, 0)),
    Map().withDefaultValue(UrlAcc(0, 0, List()))
  )

  override def add(log: HttpLog, acc: Acc): Acc = {
    val isFailed = log.Status == 0 || (log.Status / 100 == 4) || (log.Status / 100 == 5)

    val newByStatus = acc.byStatus.updated(log.Status.toString,
      StatusAcc(acc.byStatus(log.Status.toString).count + 1)
    )

    val newByMethod = acc.byMethod.updated(log.Method,
      MethodAcc(
        acc.byMethod(log.Method).count + 1,
        acc.byMethod(log.Method).failed + (if (isFailed) 1 else 0),
      )
    )

    val newByUrl = acc.byUrl.updated(log.Url,
      UrlAcc(
        acc.byUrl(log.Url).count + 1,
        acc.byUrl(log.Url).failed + (if (isFailed) 1 else 0),
        acc.byUrl(log.Url).duration :+ log.Duration
      )
    )

    Acc(
      acc.count + 1,
      acc.failed + (if (isFailed) 1 else 0),
      acc.durations :+ log.Duration,
      newByStatus,
      newByMethod,
      newByUrl
    )
  }

  override def merge(a: Acc, b: Acc): Acc = {
    Acc(
      a.count + b.count,
      a.failed + b.failed,
      a.durations ++ b.durations,
      a.byStatus.merge(b.byStatus),
      a.byMethod.merge(b.byMethod),
      a.byUrl.merge(b.byUrl),
    )
  }

  override def getResult(acc: Acc): SummaryData = {
    val byPercentile = List(90, 75, 50).map(p =>
      ByPercentileData(p, acc.durations.percentile(p))
    )

    val byMethod = acc.byMethod.toList.map(v => {
      val method = v._1
      val acc = v._2
      ByMethodData(method, acc.count, acc.failed)
    })

    val byStatus = acc.byStatus.toList.map(v => {
      val status = v._1
      val acc = v._2
      ByStatusData(status, acc.count)
    })

    val byUrl = acc.byUrl.toList.map(v => {
      val url = v._1
      val acc = v._2
      ByUrlData(
        url, acc.count, acc.failed,
        math.round(acc.duration.avg()).toInt,
        acc.duration.cv()
      )
    })

    SummaryData(
      acc.count, acc.failed,
      byPercentile, byMethod, byStatus, byUrl
    )
  }
}
