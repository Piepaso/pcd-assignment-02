package pcd.ass02.fsstat.virtualThreads

@main def TestFSStat(): Unit =
  val D = "/"
  val maxFS = 1000
  val NB = 10
  var time = 0L

  time = System.currentTimeMillis()
  val report = FSStat.getFSReport(D, maxFS, NB)
  time = System.currentTimeMillis() - time
  println(s"\nTime taken: $time ms\n")
  println(report)

