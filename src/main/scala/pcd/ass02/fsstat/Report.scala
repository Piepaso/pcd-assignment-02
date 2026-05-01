package pcd.ass02.fsstat

class Report private (maxFS: Long, NB: Int, steps: Vector[Long], counts: Vector[Long], total: Int):

  private def findIndex(size: Long): Int =
    val idx = steps.indexWhere(size < _)
    if idx == -1 then NB else idx

  def +(size: Long): Report =
    val idx = findIndex(size)
    new Report(
      maxFS,
      NB,
      steps,
      counts.updated(idx, counts(idx) + 1),
      total + 1
    )

  def getTotal: Int = total

  def getCounts: Seq[Long] = counts

  override def toString: String =
    val sb = new StringBuilder
    sb.append(s"Total files: $total\n")
    sb.append("Counts per size range:\n")
    counts.zipWithIndex.foreach:
      case (count, index) if index == NB =>
        sb.append(s"Size > $maxFS bytes: $count\n")
      case (count, index) =>
        val rangeEnd = ((index + 1) * maxFS) / NB
        sb.append(s"Size <= $rangeEnd bytes: $count\n")
    sb.toString()

object Report:
  def apply(maxFS: Long, NB: Int): Report =
    require(NB > 0, "NB must be > 0")
    val steps = Range(1, NB + 1).map(i => (i * maxFS) / NB).toVector
    new Report(maxFS, NB, steps, Vector.fill(NB + 1)(0L), 0)

  def fromArray(counts: Array[Long], maxFS: Long): Report =
    val NB = counts.length - 1
    val steps = Range(1, NB + 1).map(i => (i * maxFS) / NB).toVector
    new Report(maxFS, NB, steps, counts.toVector, counts.sum.toInt)
