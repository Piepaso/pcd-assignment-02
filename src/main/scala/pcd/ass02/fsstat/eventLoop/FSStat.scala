package pcd.ass02.fsstat.eventLoop

import io.vertx.core.*
import eventbus.*
import file.*
import scala.util.Random

class FSStat extends AbstractVerticle:
  var eb: EventBus = _
  var fs: FileSystem = _
  var logError = true

  override def start(): Unit =
    eb = vertx.eventBus()
    fs = vertx.fileSystem()

  def getFSReport(D: String, maxFS: Int, NB: Int): Future[(Int, Array[Long])] =
    if eb == null || fs == null then
      Future.failedFuture(new IllegalStateException("Verticle not started"))
    if maxFS < NB || NB <= 0 then
      Future.failedFuture(new IllegalArgumentException("maxFS must be >= NB, NB must be positive"))
    else
      val counts = Array.ofDim[Long](NB + 1)
      val steps = Range(1, NB + 1).map(i => (i * maxFS) / NB).zipWithIndex
      var total = 0
      val p = Promise.promise[(Int, Array[Long])]()
      val topic = Random().nextInt().toString

      eb.consumer[Long](topic, msg =>
        counts(steps.find(t => msg.body() < t._1).map(o => o._2).getOrElse(NB)) += 1
        total += 1
      )
      eb.consumer[Unit](topic+"f", msg => p.complete((total, counts)))
      vertx.deployVerticle(new FSReader(D, topic))
      p.future()

  private class FSReader(dir: String, topic: String) extends AbstractVerticle:
    override def start(): Unit =
      scanPath(dir).onComplete(_ =>
        eb.publish(topic+"f", ())
        vertx.undeploy(deploymentID())
      )

    private def scanPath(path: String): Future[Unit] =
      fs.props(path).compose(props =>
        if props.isDirectory then
          fs.readDir(path).compose(paths => Future.all(paths.stream().map(scanPath).toList).map(_ => ()))
        else
          eb.publish(topic, props.size())
          Future.succeededFuture()
      ).recover(err =>
        if logError then println("Error: " + err.getMessage)
        Future.succeededFuture()
      )