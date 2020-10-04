package proginscala.chap_18._1_mutable

class MemoKeyed extends Keyed {
  private var keyCache: Option[Int] = None

  override def computeKey: Int = {
    println("computeKey!!")
    if (!keyCache.isDefined)
      keyCache = Some(super.computeKey)
    keyCache.get
  }
}
