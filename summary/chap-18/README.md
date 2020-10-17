# Chapter 18. 변경 가능한 객체


## 01. 무엇이 객체를 변경 가능하게 하는가?


- 순수 함수형 객체
    - 필드에 접근하거나 메소드를 호출하면 항상 동일한 결과가 나온다.
- 변경 가능한 객체
    - 필드에 접근하거나 메소드를 호출하면 이전에 어떤 연산자를 실행했는가에 따라 결과가 달라질 수 있다.

- var를 포함하더라도 순수 함수일 수 있다.
    - ex> 비용이 아주 많이 드는 연산의 결과를 필드에 캐시하는 클래스

        ```scala
        package proginscala.chap_18._1_mutable

        class Keyed {
          def computeKey: Int = 0
        }
        ```

        ```scala
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
        ```

        ```scala
        console
        import proginscala.chap_18._1_mutable._
        val m = new MemoKeyed()
        // 0
        println(m.computeKey)
        ```

        - Keyed 대신 MemoKeyed 사용해 속도를 올릴 수 있다. computeKey 결과를 두 번째 요청받으면, keyCache 에 저장된 값을 반환한다.
        - Keyed가 순수 함수형이면, 비록 재할당하는 변수가 있더라도 MemoKeyed 도 순수 함수형이다.

## 02. 재할당 가능한 변수와 프로퍼티


- 재할당 가능한 변수에 대한 2가지 기본 연산

    ```scala
    class Time {
    	var hour = 12
    	var minute = 0
    }
    ```

    - 값을 읽는 것, getter
        - hour
    - 새로운 값을 할당하는 것, setter
        - hour_=
    - var를 getter, setter 확장하는 경우, private[this] 붙는다.
        - 그 필드를 포함하는 객체에서만 접근 가능하다.
        - 변수 할당과 접근 연산을 원하는 대로 변화시킬 수 있다.
    - 게터와 세터는 var 와 같은 가시성을 제공.

    ```scala
    class Time {
      private[this] var h = 12
      private[this] var m = 0

      def hour: Int = h
      def hour_=(x: Int) = { h = x }

      def minute: Int = m
      def minute_=(x: Int) = { m = x }
    }
    ```

- 필드 초기화에 = _ 사용
    - 스칼라에서는 = _ 초기화를 생략할 수 없다.
        - 생략하면 초기화하지 않고, 추상 변수를 선언해버린다.

## 05. 시뮬레이션 API


```scala
abstract class Simulation {
  type Action = () => Unit

  case class WorkItem(time: Int, action: Action)

  private var curtime = 0
  def currentTime: Int = curtime

  private var agenda: List[WorkItem] = List()

  private def insert(ag: List[WorkItem], item: WorkItem): List[WorkItem] = {
    if (ag.isEmpty || item.time < ag.head.time) item :: ag
    else ag.head :: insert(ag.tail, item)
  }

  def afterDelay(delay: Int)(block: => Unit) = {
    val item = WorkItem(currentTime + delay, () => block)
    agenda = insert(agenda, item)
  }

  private def next() = {
    (agenda: @unchecked) match {
      case item :: rest =>
        agenda = rest
        curtime = item.time
        item.action()
    }
  }

  def run() = {
    afterDelay(0) {
      println("*** simulation started, time = " + currentTime + " ***")
    }
    while (!agenda.isEmpty) next()
  }
}
```

- Action을(타입 멤버) 구체적인 time에 수행한다.
- curtime: 시뮬레이션의 현재 시간, currentTime: 현재 시간 반환하는 공개 접근자 메소드
    - 클래스 밖에서 시간 변경하는 것을 확실히 막기 위해 사용된다.
- WorkItem: 지정된 시간에 실행할 필요가 있는 액션
- agenda: 아직 실행하지 않은 모든 잔여 WorkItem. 각 액션의 실행 시간에 따라 정렬한다.
- afterDelay: agenda에 WorkItem 추가.
    - agenda에 액션(block으로 지정) 끼워 넣고, curtime 에서 delay 시간 뒤에 그 액션을 스케줄링 한다.
    - 이처럼 커링 함수를 활용하면 메소드 호출을 내장 문법처럼 보이게 할 수 있다.
- run: 첫 번째 WorkItem 가져오면서 제거 및 실행 동작을 반복한다. 남은 항목이 없으면 반복을 중단한다.

## 06. 회로 시뮬레이션


```scala
abstract class BasicCircuitSimulation extends Simulation {
  // 추상 메소드들
  def InverterDelay: Int
  def AndGateDelay: Int
  def OrGateDelay: Int

  class Wire {
    private var sigVal = false
    private var actions: List[Action] = List()

    def getSignal = sigVal

    def setSignal(s: Boolean) = if (s != sigVal) {
      sigVal = s
      actions foreach (_ ())
    }

    def addAction(a: Action) = {
      actions = a :: actions
      a()
    }
  }

  def inverter(input: Wire, output: Wire) = {
    def invertAction() = {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) {
        output setSignal !inputSig
      }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) = {
    def andAction() = {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) {
        output setSignal (a1Sig & a2Sig)
      }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  def orGate(o1: Wire, o2: Wire, output: Wire) = {
    def orAction() = {
      val o1Sig = o1.getSignal
      val o2Sig = o2.getSignal
      afterDelay(OrGateDelay) {
        output setSignal (o1Sig | o2Sig)
      }
    }
    o1 addAction orAction
    o2 addAction orAction
  }

  def probe(name: String, wire: Wire) = {
    def probeAction() = println(name + " " + currentTime + " new-value = " + wire.getSignal)
    wire addAction probeAction
  }
}
```

- InverterDelay, AndGateDelay, OrGateDelay: 기본 게이트의 지연 시간을 나타내는 추상화 메소드.
    - 지연은 시뮬레이션할 회로 기술에 따라 다르다. 구체적인 정의는 서브클래스에 위임.
- Wire(선) 클래스
    - getSignal: 현재 선의 신호를 반환
    - setSignal: 선의 신호를 설정
    - addAction: 액션에 구체적인 프로시저 a 추가
        - 선의 신호가 바뀔 때마다 그 선과 엮여있는 모든 액션 프로시저 실행
        - 어떤 액션을 선에 추가하면 그 액션 한 번 실행
        - 그 후 선의 신호가 변할 때마다 액션을 실행
- inverter 메소드: 입력 선에 invertAction 추가
    - 출력값을 입력의 반대 값으로 설정
- andGate, orGate 메소드: 입력 선 모두에 Action 추가
    - 출력 값을 두 입력 선 연산 결과로 설정
- probe 메소드: 선에 probeAction 추가
    - 선의 신호 변화시 실행된다. 선 이름, curTime, 새로운 선 신호값 출력

```scala
abstract class CircuitSimulation extends BasicCircuitSimulation {
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) = {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }

  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire) = {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}
```

- 반가산기, 전가산기

```scala
object MySimulation extends CircuitSimulation {
  override def InverterDelay = 1
  override def AndGateDelay = 3
  override def OrGateDelay = 5
}
```

- 게이트 지연 시간 객체

```scala
// sbt shell
compile
console
import proginscala.chap_18._5_simulation._
val input1, input2, sum, carry = new MySimulation.Wire
MySimulation.probe("sum", sum)
MySimulation.probe("carry", carry)

MySimulation.halfAdder(input1, input2, sum, carry)

input1 setSignal true
// *** simulation started, time = 0 ***
// sum 8 new-value = true
MySimulation.run()

input2 setSignal true
// *** simulation started, time = 8 ***
// carry 11 new-value = true
// sum 15 new-value = false
MySimulation.run()
```

- 4개의 선을 만들고 sum, carry 에 probe를 붙이자.
- 선을 연결하는 반가산기 정의
- 입력 신호를 하나씩 차례로 true로 설정하면서 시뮬레이션 실행.

https://www.notion.so/Chapter-18-92d30f749cee4a86a3ae6431d4c9feb5
