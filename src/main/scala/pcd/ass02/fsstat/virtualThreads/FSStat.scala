package pcd.ass02.fsstat.virtualThreads

import pcd.ass02.fsstat.Report

import java.io.File
import java.nio.file.Files
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.Executors
import scala.concurrent.{Future, Promise}

object FSStat:
  private val executor = Executors.newVirtualThreadPerTaskExecutor()

  def getFSReport(D: String, maxFS: Long, NB: Int): Future[Report] =
    val reportArray = Array.fill(NB + 1)(0L)
    val steps = Range(1, NB + 1).map(i => (i * maxFS) / NB).toVector
    val tasksCounter = TasksCounter(1)
    val mutex = ReentrantLock()
    val promise = Promise[Report]()

    class ScanPathTask(file: File) extends Runnable:
      override def run(): Unit =
        try
          if Files.isSymbolicLink(file.toPath) then
            ()
          else if file.isFile then
            updateReport(file.length())
          else if file.isDirectory then
            val children = file.listFiles
            if children != null && children.nonEmpty then
              tasksCounter.increment(children.length)
              children.foreach(child => executor.submit(ScanPathTask(child)))
        catch
          case e: Exception => println(s"Error: ${e.getMessage}")
        finally
          tasksCounter.decrement()

    def updateReport(size: Long): Unit =
      val i = steps.indexWhere(size < _)
      val index = if i == -1 then NB else i
      mutex.lock()
      try
        reportArray(index) += 1
      finally
        mutex.unlock()

    executor.submit(ScanPathTask(File(D)))
    executor.submit((() =>
      tasksCounter.waitForZero()
      promise.success(Report.fromArray(reportArray, maxFS))
    ): Runnable)

    promise.future