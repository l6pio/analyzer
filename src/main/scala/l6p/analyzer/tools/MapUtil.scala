package l6p.analyzer.tools

import l6p.analyzer.vo.http.{MethodAcc, StatusAcc, UrlAcc}

trait Merger[T] {
  def ++(b: T): T
}

object MapHelper {
  implicit class StatusMap(m1: Map[String, StatusAcc]) {
    def merge(m2: Map[String, StatusAcc]): Map[String, StatusAcc] = mergeImpl(m1, m2)
  }

  implicit class MethodMap(m1: Map[String, MethodAcc]) {
    def merge(m2: Map[String, MethodAcc]): Map[String, MethodAcc] = mergeImpl(m1, m2)
  }

  implicit class UrlMap(m1: Map[String, UrlAcc]) {
    def merge(m2: Map[String, UrlAcc]): Map[String, UrlAcc] = mergeImpl(m1, m2)
  }

  def mergeImpl[T <: Merger[T]](m1: Map[String, T], m2: Map[String, T]): Map[String, T] = {
    (m1.toList ++ m2.toList)
      .groupBy(_._1)
      .map { case (k, v) => k -> v.tail.foldLeft(v.head._2)((s, x) => s ++ x._2) }
  }
}
