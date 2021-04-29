package l6p.analyzer.vo.http

case class Summary(
  name: String,
  data: SummaryData
)

case class SummaryData(
  count: Int,
  failed: Int,
  byPercentile: List[ByPercentileData],
  byMethod: List[ByMethodData],
  byStatus: List[ByStatusData],
  byUrl: List[ByUrlData],
)

case class ByPercentileData(
  percentile: Int,
  duration: Int,
)

case class ByMethodData(
  method: String,
  count: Int,
  failed: Int,
)

case class ByStatusData(
  status: String,
  count: Int,
)

case class ByUrlData(
  url: String,
  count: Int,
  failed: Int,
  avg: Int,
  cv: Double,
)

case class SummaryDB(
  dataType: Int,
  count: Int,
  failed: Int,
  byPercentile: List[ByPercentileData],
  byMethod: List[ByMethodData],
  byStatus: List[ByStatusData],
)

case class UrlDB(
  dataType: Int,
  url: String,
  count: Int,
  failed: Int,
  avg: Int,
  cv: Double,
)
