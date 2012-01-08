package net.uresk.simplecache

object CacheUtils {
  def deepCopy[A](a: A)(implicit m: reflect.Manifest[A]): A = util.Marshal.load[A](util.Marshal.dump(a))
}