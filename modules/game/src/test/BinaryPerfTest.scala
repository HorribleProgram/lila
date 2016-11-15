package lila.game

import org.specs2.mutable.Specification

import lila.db.ByteArray
import chess._
import Pos._

class BinaryPerfTest extends Specification {

  sequential

  val format = BinaryFormat.unmovedRooks

  val dataset: List[UnmovedRooks] = List[Set[Pos]](
    Set(A1, H1, A8, H8),
    Set(H1, A8, H8),
    Set(A1, A8, H8),
    Set(A1, H1, H8),
    Set(A1, H1, A8),
    Set(A8, H8),
    Set(A1, H8),
    Set(A1, H1), 
    Set(H1, A8),
    Set()
  ) map UnmovedRooks.apply

  val encodedDataset: List[ByteArray] = dataset map format.write

  val iterations = 50000
  val nbRuns = 10

  type Run = () => Unit

  def readDataset() { encodedDataset foreach format.read }
  def writeDataset() { dataset foreach format.write }

  def runTests(run: Run, name: String) = {
    println(s"$name warming up")
    for (i <- 1 to iterations) run()
    println(s"$name running")
    val durations = for (i ← 1 to nbRuns) yield {
      val start = System.currentTimeMillis
      for (i <- 1 to iterations) run()
      val duration = System.currentTimeMillis - start
      println(s"$name $iterations times in $duration ms")
      duration
    }
    val totalNb = iterations * nbRuns
    val moveMicros = (1000 * durations.sum) / totalNb
    println(s"Average = $moveMicros microseconds each")
    println(s"          ${1000000 / moveMicros} $name per second")
    true === true
  }

  "unmoved rooks" should {
    "read" in {
      runTests(readDataset, "read")
    }
    "write" in {
      runTests(writeDataset, "write")
    }
  }
}

