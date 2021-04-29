package l6p.analyzer.tools

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  val log: Logger = LoggerFactory.getLogger(getClass)
}
