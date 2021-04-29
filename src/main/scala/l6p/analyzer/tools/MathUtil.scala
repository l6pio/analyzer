package l6p.analyzer.tools

object MathUtil {
  implicit class IntList(list: List[Int]) {
    def avg(): Double = {
      val len = list.length
      if (len == 0) {
        return 0
      }
      list.sum / list.length
    }

    def sd(): Double = {
      val len = list.length
      if (len < 2) {
        return 0
      }

      val avg = list.avg()
      val sum = list.map(v => Math.pow(v - avg, 2)).sum
      Math.sqrt(sum / (list.length - 1))
    }

    def cv(): Double = {
      val avg = list.avg()
      if (avg > 0) {
        val sd = list.sd()
        sd / avg
      } else {
        0
      }
    }

    def percentile(p: Int): Int = {
      val len = list.length
      if (len == 0) {
        return 0
      }

      val sortedList = list.sorted
      sortedList((Math.ceil(len * (p / 100.0)) - 1).toInt)
    }
  }
}
