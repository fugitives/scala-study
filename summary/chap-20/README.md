# Chapter 20. 추상 멤버
- 클래스나 트레이트의 멤버가 그 클래스 안에 완전한 정의를 갖고 있지 않은 경우
- 추상 멤버 : val, var, method, type

## 20.1 A quick tour of abstract members
- 추상 타입(T), 메소드(transform), val(initial), var(current)

```scala
trait Abstract {
  type T

  def transform(x: T): T

  val initial: T
  var current: T
}

class Concrete extends Abstract {
  type T = String

  def transform(x: T): String = x + x

  val initial = "hi"
  var current: String = initial
}
```

## 20.2 Type members
- 스칼라의 추상 타입은 클래스나 트레이트의 멤버로 정의 없이 선언된 타입이다
- type T = String (타입 멤버, alias)
- 실제 이름이 너무 길거나 불명확할때 사용

## 20.3 Abstract values 
- val 에 대해 이름과 타입은 주지만 값은 주지 않는다
- 변경하지 않을 값의 경우 사용
- val 과 method 는 같은 방식으로 접근 가능 e.g. obj.initial
- abstract val 로 선언된 멤버를 def 로 override 할 수 없다.(def -> val override 가능)

```scala
abstract class Fruit {
  val v: String
  def m: String
}

abstract class Apple extends Fruit { 
  val v: String
  val m: String
}

abstract class BadApple extends Fruit {
  def v: String
  def m: String
}
```

## 20.4 Abstract variables
- 암묵적으로 getter setter 가 정의됨 
```scala
trait AbstractTime {
  var hour: Int
  var minute: Int
}

trait AbstractTime {
  def hour: Int
  def hour_=(x: Int)
  def minute: Int
  def minute_=(x: Int)
}
```

## 20.5 Initializing abstract values 
- val 때때로 슈퍼클래스의 파라미터와 같은 역할 
```scala
trait RationalTrait { 
  val numerArg: Int
  val denomArg: Int 
}
```

- 초기화 방법 (anonymous class) 순서가 중하다.
```scala
new RationalTrait {
  val numerArg = 1
  val denomArg = 2
}
```
```scala
trait RationalTrait { 
  val numerArg: Int
  val denomArg: Int
  require(denomArg != 0)
  private val g = gcd(numerArg, denomArg)
  val numer = numerArg / g
  val denom = denomArg / g
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  override def toString = numer + "/" + denom
}
```
- anonymous class 로 초기화 하는 경우 파라미터가 세팅 되기 전에 초기화 하면 default 값이다

### 필드 미리 초기화 하기
```scala
// 익명 클래스에서
new {
  val numerARg = 1 * x
  val denomArg = 2 * x
} with RationalTrait 

// 객체 정의에서
object twoThirds extends {
  val numerArg = 2
  val denomArg = 3
} with RationalTrait

// 클래스 정의에서
class RationalClass(n: Int, d: Int) extends {
  val numerArg = n
  val denomArg = d
} with RationalTrait {
  def + (that: RationalClass) = new RationalClass(
    numer * that.denom + that.number * denom,
    denom * that.denom
  )
}
```

### 지연 계산 val 변수 
```scala
object Demo {
  lazy val x = { println("initializing x"); "done"}  
}

scala> Demo
val res5: Demo.type = Demo$@663622b1

scala> Demo.x
initializing x
val res6: String = done
```
- access 되는 시점에 계산된다.
- lazy val 은 두번 연산될 일은 없다.
- 한번만 계산된 값은 다음번 호출시 계산된 값이 리턴됨
```scala
trait LazyRationalTrait {
  val numberArg: Int
  val denomArg: Int
  lazy val number = numberArg / g
  lazy val denom = denomArg / g
  override def toString = numer + "/" + denom
  private lazy val g = {
    reuiqre(denomArg != 0)
    gcd(numberArg, denomArg)
  }
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
```

## 20.6 Abstract Types
- 서브클래스에서 구체적으로 정해야 하는 어떤 대상에 대한 빈 공간을 마련해 두는 것
```scala
class Food
abstract class Animal {
  type SuitableFood <: Food
  def eat(food: SuitableFood)
}

class Grass extends Food
class Cow extends Animal {
  type SuitableFood = Grass
  override def eat(food:  Grass) = {}
}
``` 

## 20.7 Path-dependent types
```scala
class Fish extends Food
val bessy: Animal = new Cow

bessy eat(new Fish)

       error: type mismatch;
        found   : Fish
        required: bessy.SuitableFood
```

- 경로에 따라 타입이 다르다
- 자바의 내부 클래스 타입 문법과 비슷하지만 경로 의존 타입은 외부 객체에 이름을 붙이는 반면, 내부 클래스 타입은 외부 클래스에 이름을 붙인다. 
```scala
class Outer {
  class Inner 
}
```

- 내부 클래스 선언 방법 java : Outer.Inner, scala : Outer#Inner

```scala
val o1 = new Outer
val o2 = new Outer
```

- o1.Inner, o2.Inner 는 경로 의존 타입

```scala
new o1.Inner

new Outer#Inner // 인스턴스 화는 경로 의존 타입만 가능
```

## 20.8 Refinement Types
- 어떤 클래스 A 가 다른 클래스 B 를 상속할때 A 가 B 의 이름에 의한 서브 타입
- Refinement Type 을 통해 구조적인 서브 타이핑 지원 (타입 간의 관계가 내부 구조에 의해서 결정)
```scala
class Pasture {
  var animals: List[Animal { type SuitableFood = Grass }] = Nil
}
```

## 20.9 Enumerations
- 경로 의존적 타입의 활용 예 
```scala
object Color extends Enumeration {
  val Red = Value
  val Green = Value
  val Blue = Value
}

object Color extends Enumeration {
  val Red, Green, Blue = Value
}
```
- Color.Value 는 경로 의존 타입 

## 20.10 Case Study : Currencies 
```scala
abstract class Currency {
  val amount: Long
  def designation: String
  override def toString = amount + " " + designation
  def + (that: Currency): Currency = that
}

abstract class Dollar extends Currency {
  def designation = "USD"
}

abstract class Euro extends Currency {
  def designation = "Euro"
}
```
- '+' 에서의 Currency 의 정확한 타입을 알지 못하므로 추상 타입을 추가. 

```scala
abstract class AbstractCurrency {
  type Currency <: AbstractCurrency
  val amount: Long
  def designation: String
  override def toString = amount + " " + designation
  def + (that: Currency): Currency = that
  def make(amount: Long): Currency
}

abstract class Dollar extends AbstractCurrency {
  type Currency = Dollar
  def designation = "USD"
}

```

```scala
def + (that: Currency): Currency = new Currency { val amount = this.amount + that.amount }
```

- 추상 타입의 인스턴스를 생성할수 없다.(factory method 로 극복)

```scala 
def make(amount: Long): Currency
```

- 최초 Currency 추가하는 방법으로 추상타입과 팩토리 메소드를 밖으로 옮긴다. 

```scala
abstract class CurrencyZone {
  type Currency <: AbstractCurrency
  def make(x: Long): Currency
  val CurrencyUnit: Currency
  abstract class AbstractCurrency { 
      val amount: Long
      def designation: String
      override def toString = amount + " " + designation
      def + (that: Currency): Currency = make(this.amount + that.amount)
      def * (x: Double): Currency = make((this.amount * x).toLong)
  }
}

object US extends CurrencyZone {
  abstract class Dollar extends AbstractCurrency {
    def designation = "USD"
  }
  type Currency = Dollar
  def make(x: Long) = new Dollar { val amount = x}
}
```

- 미국 통화 Cent 에 대해 표현하기 (dollar 대신 cent 로 저장)
```scala
val CurrencyUnit: Currency 

object US extends CurrencyZone {
  abstract class Dollar extends AbstractCurrency {
    def designation = "USD"
  }
  type Currency = Dollar
  def make(cents: Long) = new Dollar { val amount = cents}
  val Cent = make(1)
  val Dollar = make(100)
  val CurrencyUnit = Dollar
}
```

- 환율 계산은 Map 을 이용한다..
