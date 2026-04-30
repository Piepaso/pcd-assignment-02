package pcd.ass02.fsstat.eventLoop

import io.vertx.core.*
import eventbus.*
import file.*

import scala.util.Random
import pcd.ass02.fsstat.Report
import pcd.ass02.fsstat.reactive.SimpleBarChart

import java.awt.BorderLayout
import javax.swing.{JFrame, SwingUtilities, WindowConstants}

class FSStat extends AbstractVerticle:
  private var eb: EventBus = _
  private var fs: FileSystem = _
  var logError = true

  override def start(): Unit =
    eb = vertx.eventBus()
    fs = vertx.fileSystem()

  def getFSReport(D: String, maxFS: Long, NB: Int): Future[Report] =
    if eb == null || fs == null then
      Future.failedFuture(new IllegalStateException("Verticle not started"))
    if maxFS < NB || NB <= 0 then
      Future.failedFuture(new IllegalArgumentException("maxFS must be >= NB, NB must be positive"))
    else
      
      var report = Report(maxFS, NB)
      val p = Promise.promise[Report]()
      val topicId = Random().nextInt().toString

      //TO REMOVE

      val frame = new JFrame("FS Stat Report")
      val chart = SimpleBarChart(maxFS, NB)
      frame.setLayout(new BorderLayout())
      frame.add(chart, BorderLayout.CENTER)
      frame.setSize(1200, 800)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setVisible(true)

      //END TO REMOVE


      eb.consumer[Long](topicId, msg =>
        report += msg.body()
        SwingUtilities.invokeLater(() => chart.updateData(report))  // TO REMOVE
      )
      eb.consumer[Unit](topicId + "done", msg => p.complete(report))
      vertx.deployVerticle(new FSReader(D, topicId))
      p.future()


  private class FSReader(dir: String, topicId: String) extends AbstractVerticle:

    override def start(): Unit =
      scanPath(dir).onComplete(_ =>
        eb.publish(topicId + "done", ())
        vertx.undeploy(deploymentID())
      )

    private def scanPath(path: String): Future[Unit] =
      fs.lprops(path).compose(fileProperties =>
        if fileProperties.isDirectory && !fileProperties.isSymbolicLink then
          fs.readDir(path).compose(paths =>
            Future.all(paths.stream().map(scanPath).toList).map(_ => ()))
        else if fileProperties.isRegularFile then
          eb.publish(topicId, fileProperties.size())
          Future.succeededFuture()
        else
          Future.succeededFuture()
      ).recover(err =>
        if logError then println("Error: " + err.getMessage)
        Future.succeededFuture()
      )