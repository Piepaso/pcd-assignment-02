package pcd.ass02.fsstat.reactive

import javax.swing._
import java.awt._
import pcd.ass02.fsstat.Report

class SimpleBarChart(maxFS: Long, NB: Int) extends JPanel:

  private val bars: Seq[Long] = Range(1, NB + 1).map(i => (i * maxFS) / NB)
  private val labels: Seq[String] = bars.map(b => s"<= $b bytes").appended(s"> $maxFS bytes")
  private var report = Report(maxFS, NB)
  private val stopButton: JButton = new JButton("Stop scanning")

  def updateData(report: Report): Unit =
    this.report = report
    repaint()

  def addStopListener(action: () => Unit): Unit = {
    stopButton.addActionListener(_ => action())
    this.add(stopButton)
  }

  override def paintComponent(g: Graphics): Unit =
    val counts = report.getCounts

    super.paintComponent(g)
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    val padding = 50
    val chartWidth = getWidth - 2 * padding
    val chartHeight = getHeight - 2 * padding
    val maxVal = counts.max.toDouble.max(1.0) // Evita divisione per zero
    val barWidth = chartWidth / (NB + 1) - 10

    counts.zipWithIndex.foreach((value, i) =>
      val barHeight = ((value / maxVal) * chartHeight).toInt
      val x = padding + i * (barWidth + 10)
      val y = getHeight - padding - barHeight

      g2d.setColor(new Color(70, 130, 180))
      g2d.fillRect(x, y, barWidth, barHeight)

      g2d.setColor(Color.BLACK)
      g2d.drawString(value.toString, x, y - 5)
      g2d.drawString(labels(i), x, getHeight - padding + 20)
    )
    g2d.drawString(s"Total files: ${report.getTotal}", padding, 20)
