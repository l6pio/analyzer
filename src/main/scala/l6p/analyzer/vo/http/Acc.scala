package l6p.analyzer.vo.http

import l6p.analyzer.tools.Merger

case class Acc(
  count: Int,
  failed: Int,
  durations: List[Int],
  byStatus: Map[String, StatusAcc],
  byMethod: Map[String, MethodAcc],
  byUrl: Map[String, UrlAcc],
)

case class StatusAcc(count: Int) extends Merger[StatusAcc] {
  override def ++(b: StatusAcc): StatusAcc = StatusAcc(count + b.count)
}

case class MethodAcc(count: Int, failed: Int) extends Merger[MethodAcc] {
  override def ++(b: MethodAcc): MethodAcc = MethodAcc(count + b.count, failed + b.failed)
}

case class UrlAcc(count: Int, failed: Int, duration: List[Int]) extends Merger[UrlAcc] {
  override def ++(b: UrlAcc): UrlAcc = UrlAcc(
    count + b.count,
    failed + b.failed,
    duration ++ b.duration,
  )
}
