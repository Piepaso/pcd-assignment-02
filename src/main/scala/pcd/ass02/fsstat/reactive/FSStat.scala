package pcd.ass02.fsstat.reactive

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import pcd.ass02.fsstat.Report

import java.io.File
import java.nio.file.Files
import java.nio.file.Files.isSymbolicLink

object FSStat:

  def getFSReport(D: String, maxFS: Long, NB: Int): Observable[Report] =
    scanPath(File(D))
      .subscribeOn(Schedulers.io())
      .scan(Report(maxFS, NB), (report, file) => report + file.length())

  private def scanPath(file: File): Observable[File] =
    if Files.isSymbolicLink(file.toPath) then
      Observable.empty()
    else
      Option(file.listFiles) match
        case Some(children) => Observable.fromArray(children*).flatMap(scanPath)
        case None if file.isFile => Observable.just(file)
        case _ => Observable.empty()