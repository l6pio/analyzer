package tools

import l6p.analyzer.tools.MathUtil._
import org.scalatest._

class MathSpec extends FlatSpec {
  it should "calculate average" in {
    val list = List[Int](32, 50, 68, 86, 104)
    val avg = list.avg()
    assert(avg == 68)
  }

  it should "calculate coefficient of variance" in {
    val list = List[Int](32, 50, 68, 86, 104)
    val sd = list.sd()
    val cv = list.cv()
    assert(Math.round(sd * 100) == 2846)
    assert(Math.round(cv * 100) == 42)
  }
}
