# Chapter 22. 리스트 구현

* 

## 22.1 List 클래스 개괄
* List 는 scala 패키지에 있는 sealed abstract 클래스다
  * List[+T] 로 정의되어, 곧 리스트는 covariant 하다
* List 의 서브 타입으로 final case class 인 `::`(Cons)와 case object 인 `Nil`이 있다
  * Cons 는 Construct 의 약자
  * Nil 은 List[Nothing]의 서브 타입이라 모든 List 타입에 대입 가능하다
* List 의 세 가지 메소드인 `isEmpty`, `head`, `tail` 만으로 List 의 모든 연산이 구현 가능하다
* 패턴 매칭에서 중위 연산자는 자동으로 피연산자를 인자로 넣어 apply 메소드를 호출하는 것으로 변환 됨
  * 그리고 중위 연산자로 명시된 이름을 가진 case class 가 있어야 함
  * **예를 들어, 패턴 매칭 시 case x :: xs 라고 쓰면 사실 ::(x, xs) 로 변환 되고, :: 는 case class**
* `::`는 두 가지
  * List 의 메소드
  * scala.:: case 클래스
* :: 의 구현
```scala
final case class ::[T](hd: T, tl: List[T]) extends List[T] {
  def head = hd
  def tail = tl
  override def isEmpty: Boolean = false
}
```
* 그런데 사실 스칼라의 다음 2가지 성질로 인해 더 단순하게 만들 수 있다
  * case class 의 class parameter 앞에는 암시적으로 val 이 붙는다
  * 파라미터 없는 method 가 val 로 override 가능하다
```scala
final case class ::[T](head: T, tail: List[T]) extends List[T] {
  override def isEmpty: Boolean = false
}
```
* :: 메서드는 lower-bound 를 사용하여 정의되어 있음
```scala
def ::[U > T](x: U): List[U] = new scala.::(x, this)
```
* 그래서 다음과같은 결과가 나옴(다른 타입을 인자로 호출하면 자동으로 공통 부모 클래스로 타입이 변환)
```scala
scala> abstract class Fruit
defined class Fruit

scala> class Apple extends Fruit
defined class Apple

scala> class Orange extends Fruit
defined class Orange

scala> val apples = new Apple :: Nil
apples: List[Apple] = List(Apple@13f40d71)

scala> val fruits = new Orange :: apples
fruits: List[Fruit] = List(Orange@2b682e9, Apple@13f40d71)
```

## 22.2 ListBuffer 클래스
* 정수 List xs 의 모든 원소를 1씩 증가시키기 위해서 다음과같은 재귀 함수를 생각할 수 있음
```scala
def incAll(xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case x :: xs1 => x + 1 :: incAll(xs1)
}
```
* 하지만 tail recursion 이 아니기 때문에 여러번 호출된다면 stack overflow 가 발생
* 한가지 해결책은 for 를 사용하는 것
```scala
var result = List[Int]()
for (x <- xs) result = result ::: List(x + 1)
```
* 하지만 이는 매우 비효율적이므로 ListBuffer 를 쓰면 총 O(N) 에 가능
```scala
val buf = new ListBuffer[Int]
for (x <- xs) buf += x + 1
buf.toList
```

## 22.3 실제 List 클래스
* 실제 List 의 map 메서드는 while, for 같은 루프를 사용한다
* 그 이유는 꼬리 재귀로 구현 한다고 하더라도 재귀 구현은 일반적으로 더 느리고 확장성이 떨어지기 때문
* 그래서 실제 map 메서드를 보면 다음과 같이 ListBuffer 와 while loop 을 사용한다
  * 참고로 2.13.3 기준으로는 구현이 조금 달라져있다..
```scala
final override def map[U](f: T => U): List[U] = {
  val b = new ListBuffer[U]
  var these = this
  while (!these.isEmpty) {
    b += f(these.head)
    these = these.tail
  }
  b.toList
}
```
* 그런데 BufferList 의 toList 의 시간복잡도가 상수 시간인 이유는 다음과 같음
* 사실 Cons(::) 클래스는 다음과 같이 tail 이 mutable 임!!
```scala
final case class ::[U](hd: U, private[scala] var tl: List[U]) extends List[U] {
  def head = hd
  def tail = tl
  override def isEmpty: Boolean = false
}
```
* 그래서 ListBuffer 의 += 메서드의 구현이 다음과 같음 
```scala
override def += (x: T) = {
  if (exported) copy()
  if (start.isEmpty) {
    last0 = new scala.::(x, Nil)
    start = last0
  } else {
    val last1 = last0
    last0 = new scala.::(x, Nil)
    last1.tl = last0
  }
}
```
* exported 는 empty 가 아닌 상태에서 toList 가 호출되었는지 여부
* 즉, 이미 toList 가 호출되었으면 copy 를 하여 O(N) 이 소요
* ListBuffer 의 대부분의 사용 시나리오가 원소를 삽입하여 딱 한 번 toList 를 호출하기에 이걸로 충분
 
## 22.4 외부에서 볼 때는 함수형
* List 는 외부에서는 완전히 functional 하지만 내부에서는 ListBuffer 를 사용해 명령형으로 되어있음
* 이것은 Scala 의 전형적인 전략중 하나
* 순수하지 않은 연산의 효과가 미치는 범위를 주의깊게 제한함으로써 함수적 순수성을 효율적으로 달성하려는 것
* List 이 immutable 하고 ListBuffer 를 사용하게 한 이유는 자바의 String 과 StringBuffer 와 일치
```scala
val ys = 1 :: xs
val zs = 2 :: xs

ys.drop(2).tail = Nil // scala 에서는 error
```
* 위와 같은 코드를 실행하면 xs 와 zs 도 짧아 질 수 있기 때문에 추적하기가 어려워짐
