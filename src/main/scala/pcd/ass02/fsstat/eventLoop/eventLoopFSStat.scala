package pcd.ass02.fsstat.eventLoop

import io.vertx.core.*
import eventbus.EventBus
import file.*
import scala.collection.mutable.ListBuffer

private val fileTopic: String = "f"
private val finishTopic: String = "e"

class Listener extends AbstractVerticle:
  private val allFiles = ListBuffer[(String, Long)]()

  override def start(): Unit =
    val eb = vertx.eventBus()
    eb.consumer[(String, Long)](fileTopic, msg => allFiles.addOne(msg.body()))
    eb.consumer[Unit](finishTopic, _ => printReport())

  private def printReport(): Unit =
    println(s"\n--- FINAL REPORT (${allFiles.size} files) ---")
    println("Top 5 biggest files: ")
    allFiles.sortBy(_._2).reverse.take(5).foreach(println)
    println("\n Mean file size: " + allFiles.map(_._2).sum / allFiles.size)
    vertx.close()

class FSReader(dir: String) extends AbstractVerticle:
  private var eb: EventBus = _
  private var fs: FileSystem = _

  override def start(): Unit =
    eb = vertx.eventBus()
    fs = vertx.fileSystem()
    getFSReport(dir).onSuccess(size => eb.publish(finishTopic, ()))

  private def getFSReport(path: String): Future[Unit] =
    fs.props(path).compose(props =>
      if props.isDirectory then
        fs.readDir(path).compose(paths => Future.all(paths.stream().map(getFSReport).toList).map(_ => ()))
      else
        eb.publish(fileTopic, (path, props.size()))
        Future.succeededFuture()
    )

@main def eventLoopFSStat(): Unit =
  val vertx = Vertx.vertx()
  vertx.deployVerticle(Listener()).onSuccess(_ =>
    vertx.deployVerticle(FSReader("/home/paso/Documents"))
  )
