# Chapter 19. 타입 파라미터화 

* 자바와는 달리 raw 타입 허용 X

## 19.3 Variance Annotations

```scala
trait Queue[T] {
  def head: T
  def tail: Queue[T]
  def enqueue(x: T): Queue[T]
}
```
* 위의 Queue 를 **type constructor** 라고도 부름
  * Queue[Int], Queue[String] 과 같은 일련의 타입을 *construct* 하기 때문
* 위의 Queue 를 **generic trait** 라고도 부름
  * 또는 **generic type**
  * 반면, Queue[Int] 는 **parameterized type**
  
* S가 T의 서브타입일 때, Queue[S]가 Queue[T]의 서브타입이라면, Queue 는 타입 파라미터 T에 대해 **covariant**하다고 한다
  * 혹은 **flexible**하다고도 한다함(처음 들음..)
  * 타입 파라미터가 하나밖에 없다면 그냥 Queue 가 **covariant**하다고 해도 됨
  * `trait Queue[+T] { ... }` 처럼 표기하여 covariant 로 만들 수 있음
* Scala 에서는 default 가 **nonvariant**
  * 혹은 **rigid**하다고도 한다함(이것도 처음 들음..)
* T가 S의 서브타입일 때, Queue[S]가 Queue[T]의 서브타입이라면, Queue 는 타입 파라미터 T에 대해 **contravariant**하다고 한다
  * `trait Queue[-T] { ... }` 처럼 표기하여 contravariant 로 만들 수 있음
* *covariant, nonvariant, contravariant* 여부를 parameter의 **variance**라고 한다 
* type parameter 앞에 붙는 `+/-` 는 **variance annotation**이라고 한다
* 순수 함수형 세계에서는 많은 타입이 covariant 하다
  * mutable 변수는 covariant 할 수 없다
  * 만약 covariant 할 수 있다면 다음과 같은 코드가 컴파일 될 수 있다는 것인데, 그렇게 된다면 런타임 에러가 발생할 것이기 때문
  * ```scala
    class Cell[+T](init: T) { // +T 가 될 수 없지만 일단 컴파일 된다고 치자!
      private[this] var current = init
      def get = current
      def set(x: T) = { current = x }
    }
    
    val c1 = new Cell[String]("abc")
    val c2: Cell[Any] = c1
    val c2.set(1)
    val s: String = c1.get
    ```
  * 실제로는 +T 에서 컴파일 에러 발생
  * ```scala
    <console>:14: error: covariant type T occurs in contravariant position in type T of value x
                 def set(x: T) = { current = x }
                         ^
    ```
  * immutable 만 써도 covariant 하면 안되는 경우도 있긴 함. 아래가 그 예
    * ```scala
      class StrangeIntQueue extends Queue[Int] { // Queue 는 var 를 사용하지 않은 purely functional Queue (궁금증: var 를 써도 purely functional 하다고 할 수 있나? 함수가 pure 한거 말고, class 가 purely functional 하기 위한 조건은 무엇인가?)
        override def enqueue(x: Int) = {
          println(math.sqrt(x))
        }
      }
      ```
* Java 의 배열은 covariant 하고, Scala 의 배열은 nonvariant 하다
  * Java 의 배열이 안전하지도 않고 비용도 비싸게 굳이 covariant 한 이유는, 배열을 제네릭하게 다룰 간단한 방법이 필요했기 때문
  * `void sort(Object[] a, Comparator cmp) { ... }` 와 같은 메소드로 모든 배열을 정렬하고 싶었기 때문
  * 지금은 제네릭아 생겨, 타입 파라미터로 sort 메소드를 작성할 수 있어, covariant array 는 필요없어 졌지만, 호환성 문제로 여전히 남아있음

## 19.4 Checking Variance Annotations
* type parameter 가 method parameter 에 사용된다면, 그 method 를 포함하는 class, 혹은 trait 는 covariant 하지 않을 수 있다
  * 위에 var 의 예와 functional Queue 의 예를 보면 알 수 있음
  * 그래서 **Scala 에서 method paramter 에 사용되는 type parameter 를 covariant 로 선언하면 compile error 가 발생한다**
  * ```scala
    scala> class Queue[+T] {
         | def enqueue(x: T) = {}
         |
         | }
    <console>:12: error: covariant type T occurs in contravariant position in type T of value x
           def enqueue(x: T) = {}
                       ^
    ```
  * **method paramter 에 사용되지 않더라도 var 의 type 으로 쓰여도, 내부적으로 setter/getter 가 생성되기 때문에 마찬가지로 compile error**
  * ```scala
    scala> abstract class Queue[+T] {
         | var a: T
         | }
    <console>:12: error: covariant type T occurs in contravariant position in type T of value a_=
           var a: T
               ^
    ```
* Scala 컴파일러는 class(혹은, trait)에서 type parameter 를 사용할 수 있는 모든 위치를 **positive**, **negative**, **neutral** 로 분류
  * **+로 표기한 type parameter 는 positive 한 위치에만 사용 가능**
  * **-로 표기한 type parameter 는 negative 한 위치에만 사용 가능**
  * **variance annotation 이 없는 type parameter 는 어느 곳에서든 사용 가능**
* *positive, negative, neutral* 구분 기준
  * 최상위(method, instance variable 등?)는 positive
  * nesting levels 은 enclosing levels 과 동일하게 취급
  * 하지만 method 의 type parameter 와 value parameter(걍 인자를 말하는듯)는 outside classification 을 뒤집는다
    * + -> -
    * - -> +
    * neutral -> neutral
  * 이거 이해가 안감..(443p)
  * ```scala
    abstract class Cat[-T, +U] {
      def meow[W](volume: T, listener: Cat[U, T]): Cat[Cat[U, T], U]
    }
    ```  
## 19.5 Lower Bounds
* purely functional Queue 에서 type parameter T 는 negative position 인 `enqueue(x: T)` method 의 parameter type 으로 사용되었기 때문에, type parameter T 에 대해 covariant 할 수 없다
* 하지만 T 를 lower bounds 로 사용하여 enqueue 메소드의 type parameter 로 더욱 일반화된 타입 U를 붙이는 것
```scala
class Queue[+T] (private val leading: List[T], private val trailing: List[T]) {
  def enqueue[U >: T](x: U) = new Queue[U](leading, x :: trailing)
}
``` 
* **use-site variance(사용 위치 변성)**인 Java 와 달리 Scala 는 **declaration-site variance(선언 위치 변성)**
  * use-site variance 는 만약 사용자 쪽에서 잘못 사용하면, 중요한 인스턴스 메소드를 더 이상 사용할 수 없다
  * declaration-site variance 는 우리의 의도를 컴파일러에게 알려줄 수 있고, 컴파일러는 메소드가 실제로 사용 가능한지를 다시 한 번 확인해줄 수 있다

## 19.8 Upper Bounds
* Upper Bounds 의 사용 예로는, Ordered trait 가 mixed-in 된 객체를 정렬하는 함수를 예로 들 수 있다.
* ```scala
  def orderedMergeSort[T <: Ordered[T]](xs: List[T]): List[T] = {
    def merge(xs: List[T], ys: List[T]): List[T] =
      (xs, ys) match {
        case (Nil, _) => ys
        case (_, Nil) => xs
        case (x :: xs1, y :: ys1) =>
          if (x < y) x :: merge(xs1, ys)
          else y :: merge(xs, ys1)
      }
    val n = xs.length / 2
    if (n == 0) xs
    else {
      val (ys, zs) = xs splitAt n
      merge(orderedMergeSort(ys), orderedMergeSort(zs))
    }
  } 
  ```
* type parameter T 가 Ordered[T] 의 subtype 이라는 것을 강제함으로써(T 의 Upper Bound 로서 Ordered[T] 를 설정), `<` method 를 사용할 수 있게 된다
* 하지만 이 방법은 **Int**와 같이 Ordered[Int]를 mixin 하지 않은 기존의 타입에 대해서는 정렬을 수행할 수 없다는 단점이 존재
* 이러한 문제는 **implicit parameter**와 **context bound** 를 사용하는, 보다 좋은 방식으로 해결할 수 있다
