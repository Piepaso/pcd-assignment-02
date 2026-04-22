package pcd.ass02.fsstat.eventLoop

object TestFSStat:
  def main(args: Array[String]): Unit =
    val D = "../../"
    val maxFS = 1000
    val NB = 10
    val startTime = System.currentTimeMillis()
    FSStat.getFSReport(D, maxFS, NB).onSuccess((total, counts) =>
      val endTime = System.currentTimeMillis()
      println(s"Time taken: ${endTime - startTime} ms")
      println(s"Total files: $total")
      println("Counts per size range:")
      counts.zipWithIndex.foreach((count, index) =>
        val rangeStart = if index == 0 then 0 else (index * maxFS) / NB + 1
        val rangeEnd = ((index + 1) * maxFS) / NB
        println(s"Size <= $rangeEnd bytes: $count")
      )
    )