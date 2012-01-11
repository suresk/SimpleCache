import net.uresk.simplecache.{CacheNode, ExpensiveObject, CacheRegion}

def x(l: CacheNode[String], r: CacheNode[String]) = true

val region: CacheRegion[String, ExpensiveObject] = new CacheRegion("first", 500, x)

def output() = region.readThroughCache("spencer"){() =>
  Some(new ExpensiveObject(500, "Spencer"))
}

println(output())
println(output())
println(output())
println(output())
println(output())
println(output())
println(output())
println(output())
println(output())

println(region.size)