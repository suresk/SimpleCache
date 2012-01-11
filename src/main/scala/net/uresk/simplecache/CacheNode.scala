package net.uresk.simplecache

import ref.SoftReference

class CacheNode[K](val key: K, var item: SoftReference[Array[Byte]], var lastUpdate: Long, var hits: Long, var lastHit: Long, var size: Int) {

  def get(): Option[Array[Byte]] = item.get match {
    case Some(i) => {
      hits += 1
      lastHit = System.currentTimeMillis()
      Some(i)
    }
    case None => None
  }

  def update(newItem: Array[Byte]) = {
    item = new SoftReference(newItem)
    lastUpdate = System.currentTimeMillis()
    size = newItem.length
  }

}

object CacheNode {
  def apply[K](key: K, item: Array[Byte]): CacheNode[K] = new CacheNode(key, new SoftReference(item), System.currentTimeMillis(), 0L, 0L, item.length)
}