package net.uresk.simplecache

import collection.mutable.{HashMap, Map}

class CacheRegion[K, V <: Serializable](val name: String, val maxItems: Long, val evictionStrategy: (CacheNode[K], CacheNode[K]) => Boolean) {

  private val PCT_TO_EVICT = .05

  var items: Long = 0

  var size: Long = 0

  var snipedEntries: Long = 0 // Keep track of # of nodes that get sniped by the GC

  val values: Map[K, CacheNode[K]] = new HashMap[K, CacheNode[K]]()

  // If we have more than 1k items, clear a % of them.
  // This is because making space is expensive (O(n))
  def clearCount(): Int = items match {
    case i if i > 1000 => (items * PCT_TO_EVICT).asInstanceOf[Int]
    case _ => 1
  }

  def makeSpace() = {
    values.values.toList.sortWith(evictionStrategy).take(clearCount).foreach(node => evict(node.key))
  }

  def put(key: K,  value: V)(implicit m: Manifest[V]) = synchronized {
    if(items >= maxItems){
      makeSpace()
    }
    items += 1
    val bytes = util.Marshal.dump(value)
    size += bytes.length
    values.get(key) match {
      case Some(node) =>  node.update(bytes)
      case None => values.put(key, CacheNode(key, bytes))
    }
  }

  def get(key: K)(implicit m: reflect.Manifest[V]): Option[V] = values.get(key) match {
    case Some(node) => {
      node.get match {
        case Some(data) => Some(util.Marshal.load[V](data))
        case None => {
          snipedEntries += 1
          evict(key)
          None
        }
      }
    }
    case None => None
  }

  def evict(key: K): Unit = Option(values.remove(key)) match{
    case Some(value) => {
      items -= 1
      size -= value.size
    }
  }

  def evictAll(): Unit = {
    items = 0
    size = 0
    values.clear()
  }

  def readThroughCache(key: K)(op: () => Option[V])(implicit m: Manifest[V]): Option[V] = {
    get(key) match {
      case Some(value) => Some(value)
      case None => op.apply() match {
        case Some(rootValue) => {
          put(key, rootValue)
          Some(rootValue)
        }
        case None => None
      }
    }
  }

  def pin(key: K) = {

  }

  def unpin(key: K) = {

  }

}

//object EvictionStrategy{
//  def leastUsedStrategy(l: CacheNode[_, _], r: CacheNode[_, _]): Boolean = l.hits < r.hits
//  def leastRecentlyUsedStrategy(l: CacheNode[_, _], r: CacheNode[_, _]): Boolean = l.hits < r.hits
//  def oldestStrategy(l: CacheNode[_, _], r: CacheNode[_, _]): Boolean = l.hits < r.hits
//}