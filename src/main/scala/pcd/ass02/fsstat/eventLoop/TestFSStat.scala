package pcd.ass02.fsstat.eventLoop

import io.vertx.core.Vertx

@main def TestFSStat(): Unit =
  val D = "/home/paso"
  val maxFS = 1000
  val NB = 10
  var time = 0L
  val vertx = Vertx.vertx()
  val fsStat = new FSStat
  fsStat.logError = false

  vertx.deployVerticle(fsStat).compose(_ =>
    time = System.currentTimeMillis()
    fsStat.getFSReport(D, maxFS, NB).onSuccess(report =>
      time = System.currentTimeMillis() - time
      println(s"\nTime taken: $time ms\n")
      println(report)
      vertx.close()
    )
  ).onFailure(err =>
    println(s"Error: ${err.getMessage}")
    vertx.close()
  )

