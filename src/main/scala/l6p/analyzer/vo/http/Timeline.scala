package l6p.analyzer.vo.http

case class Timeline(
  name: String,
  from: Long,
  to: Long,
  data: TimelineData
)

case class TimelineData(
  count: Int,
  failed: Int,
)

case class TimelineDB(
  dataType: Int,
  from: Long,
  to: Long,
  count: Int,
  failed: Int,
)
