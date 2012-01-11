package net.uresk.simplecache

class ExpensiveObject(val id: Int, val name: String) extends Serializable

object ExpensiveObject {
  def apply(id: Int, name: String): ExpensiveObject = {
    Thread.sleep(5000)
    new ExpensiveObject(id, name)
  }
}