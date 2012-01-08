package net.uresk.simplecache

class CacheNode[K, V <: Serializable](val key: K, val item: V, var lastUpdate: Long, var hits: Long, var lastHit: Long)(implicit m: Manifest[V]) {

  def get(): V = {
    hits += 1
    lastHit = System.currentTimeMillis()
    CacheUtils.deepCopy(item)
  }

}

object CacheNode {
  def apply[K, V <: Serializable](key: K, item: V)(implicit m: Manifest[V]): CacheNode[K, V] = new CacheNode(key, CacheUtils.deepCopy(item), System.currentTimeMillis(), 0L, 0L)
}