package pcd.ass02.fsstat.virtualThreads

import concurrent.duration.DurationInt
import scala.concurrent.Await

@main def TestFSStat(): Unit =
  val D = "/home/"
  val maxFS = 1000
  val NB = 10
  var time = 0L

  time = System.currentTimeMillis()
  val future = FSStat.getFSReport(D, maxFS, NB)
  val report = Await.result(future, 1.minute)
  time = System.currentTimeMillis() - time
  println(s"\nTime taken: $time ms\n")
  println(report)

