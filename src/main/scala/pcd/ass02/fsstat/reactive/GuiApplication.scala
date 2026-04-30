package pcd.ass02.fsstat.reactive

import javax.swing._
import java.awt._
import java.awt.BorderLayout
import pcd.ass02.fsstat.Report

object GuiApplication extends App:
  val D = "/home/"
  val maxFS = 10000L
  val NB = 10

  private val frame = new JFrame("FS Stat Report")
  private val chart = SimpleBarChart(maxFS, NB)
  frame.setLayout(new BorderLayout())
  frame.add(chart, BorderLayout.CENTER)
  frame.setSize(1200, 800)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setVisible(true)
  val startTime = System.currentTimeMillis()

  private val disposable = FSStat.getFSReport(D, maxFS, NB).subscribe(
    report => SwingUtilities.invokeLater(() => chart.updateData(report)),
    err => println(s"Error: ${err.getMessage}"),
    () => println("Report generation completed in " + (System.currentTimeMillis() - startTime) + " ms")
  )

  chart.addStopListener(() =>
    disposable.dispose()
    println("Report generation stopped by user.")
  )
