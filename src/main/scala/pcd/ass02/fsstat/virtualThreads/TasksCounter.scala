package pcd.ass02.fsstat.virtualThreads

import java.util.concurrent.locks.ReentrantLock

class TasksCounter(var activeTasks: Int):
  private val lock = ReentrantLock()
  private val zeroTaskActive = lock.newCondition()

  def increment(n: Int): Unit =
    lock.lock()
    try
      activeTasks += n
    finally
      lock.unlock()

  def decrement(): Unit =
    lock.lock()
    try
      activeTasks -= 1
      if activeTasks == 0 then zeroTaskActive.signalAll()
    finally
      lock.unlock()

  def waitForZero(): Unit =
    lock.lock()
    try
      while activeTasks > 0 do zeroTaskActive.await()
    finally
      lock.unlock()
