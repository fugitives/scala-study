# Chapter 16. 리스트

* (아래의 예제에서 사용하는 *abcde* 라는 변수는 *List('a', 'b', 'c', 'd' ,'e')* 임)

## 16.6 List 클래스의 1차 메소드
* 함수를 인자로 받지 않는 메소드를 **first-order method**(1차 메소드)라고 함

### :::
* `:::` 연산은 두 List 를 이어 붙이는 연산 
* `::(Cons)`처럼 :(콜론)으로 끝나므로 뒤에서 부터 계산
  * 즉, *xs ::: ys ::: zs* 는 *xs ::: (ys ::: zs)*
* xs ::: ys 의 경우, xs 의 길이가 N, ys 의 길이가 M 이라면, **O(N)**

### length
* `length` 메소드는 **O(N)**
* xs.isEmpty 대신 xs.length == 0 으로 하면 혼남

### init, last
* `tail` 과 `last` 의 반대 연산이라고 볼 수 있음
* Nil 에서 `init` 호출 시 *UnsupportedOperationException: Nil.init*
* Nil 에서 `last` 호출 시 *NoSuchElementException: Nil.last*
* `init` 과 `last` 모두 **O(N)**

### reverse
* xs.reverse.init == xs.tail
* xs.reverse.tail == xs.init
* xs.reverse.last == xs.head
* xs.reverse.head == xs.last

### drop, take, splitAt
* `drop` 과 `take` 는 접미사와 접두사를 반환
* xs `take` n 에서 n >= xs.length 라면 xs 반환
* xs `drop` n 에서 n >= xs.length 라면 Nil 반환
* xs `splitAt` n == (xs `take` n, xs `drop` n)
* 하지만 두 번 순회하지 않음
* 셋 다 인자가 n 일 때 **O(n)**

### apply, indices
* `apply`는 임의 원소 선택을 위해 사용, But Array 에 비해 자주 사용되진 않음 
* `apply`의 인자가 n 일 때 **O(n)**
* xs `apply` n == (xs drop n).head
* 길이 N 인 List 에 대해 `indices`를 호출하면 Range(0, ..., N - 1) 반환

### flatten
* List 의 List 를 펼쳐줌
* List 의 원소는 무조건 다 List 여야 함
```scala
// 컴파일 에러: 1이 List 타입이 아님
// 이 경우 바깥쪽 List 는 List[Any] 로 추론
// List[List[T]] ? 여야 함
List(1, List(2, 3)).flatten
```

### zip, unzip
* `zip`은 두 리스트의 각 원소를 묶어 튜플의 리스트로 만들어줌
  * 긴쪽은 뒷 원소 버림
  * xs.zipWithIndex == xs.indices zip xs
* `unzip`은 튜플의 리스트를 두 리스트로 이루어진 튜플로 변환

### toString, mkString
* `toString`은 리스트의 표준 문자열 표현 반환
  * abcde.toString
    * res0: String = List(a, b, c, d, e)
* `mkString` 은 3가지 오버로드한 메소드 존재
  * xs `mkString` (pre, sep, post) == pre + xs(0) + sep + ... + sep + xs(xs.length - 1) + post 
  * xs `mkString` (sep) == xs `mkString` ("", sep, "")
  * xs `mkString` == xs `mkString` ""
* abcde `mkString` ("List(", ", ", ")")
  * 출력 결과: List(a, b, c, d, e)
* `mkString`의 변형인 `addString` 은 scala.StringBuilder 를 반환
  * abcde addString (new StringBuilder, "(", ";", ")")
    * res0: StringBuilder = (a;b;c;d;e)

### iterator, toArray, copyToArray
* `toArray` 와 `toList` 는 List 와 Array 간의 변환에 사용
* list `copyToArray(arr, start)` 는 arr 의 start 부터 list 의 값들을 복사한다
  * 공간이 부족하면 잘린다
  * ```scala
    scala> val arr2 = new Array[Int] (5)
    arr2: Array[Int] = Array(0, 0, 0, 0, 0)

    scala> List(1, 2, 3) copyToArray (arr2, 3)

    scala> arr2
    res7: Array[Int] = Array(0, 0, 0, 1, 2)
    ```
* `iterator` 는 iterator 를 반환한다
  * ```scala
    scala> val it = abcde.iterator
    it: Iterator[Char] = <iterator>
    
    scala> it.next
    res1: Char = a
    
    scala> it.next
    res2: Char = b
    
    scala> it.hasNext
    res3: Boolean = true
    ```
 * Merge Sort
 ```scala
def msort[A](less: (A, A) => Boolean)(list: List[A]): List[A] = {
  def merge(xs: List[A], ys: List[A]): List[A] =
    (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (less(x, y))
          x :: merge(xs1, ys)
        else
          y :: merge(xs, ys1)
    }

  val len = list.length
  if (len <= 1) list
  else {
    val (left, right) = list.splitAt(len / 2)
    merge(msort(less)(left), msort(less)(right))
  }
}
```

## 16.7 List 클래스의 고차 메소드
* 고차 메소드(higher-order methods)는 함수를 인자로 받는 메소드

### map, flatmap, foreach
* `map`과 `flatmap`은 익숙할테니 패스..
* foreach 는 인자로 넘겨준 프로시저를 List의 각 원소에 적용
  * 결과값은 Unit

### filter, partition, find, takeWhile, dropWhile, span
* `filter`는 predicate 함수의 결과가 true인 원소의 리스트 반환
* `partition`은 predicate 함수의 결과가 true인 원소에 해당하는 리스트와 아닌 리스트로 이루어진 튜플 반환
  * xs partition p == (xs filter p, xs filter (!p(_)))
* `find`은 filter 와 같지만, 첫번째 원소만 *Option* 형태로 반환
  * 없으면 *None*
* `takeWhile`은 predicate 함수의 결과가 true인 가장 긴 접두사 반환
* `dropWhile`은 predicate 함수의 결과가 true인 가장 긴 접두사를 제외하고 반환
* `span`은 predicate 함수의 결과가 true 인 가장 긴 접두사와 나머지 부분을 튜플로 반환
  * xs span p == (xs takeWhile p, xs dropWhile p)
  * take, drop, splitAt 의 관계는 takeWhile, dropWhile, span 의 관계와 같다

### forall, exists
* `forall`은 List 의 모든 원소가 특정 조건을 만족하는지 체크하는데 사용
  * 모든 원소에 대해 predicate 함수의 결과가 true 일 때만 true
* `exists`는 List 에 특정 조건을 만족하는 원소가 하나라도 있는지 체크하는데 사용
  * predicate 함수의 결과가 true 인 원소가 하나라도 있으면 true
  * 모든 원소가 0인 행이 존재하는지 검사하는 함수
    ```scala
    def hasZeroRow(m: List[List[Int]]) = 
      m exists (row => row forall (_ == 0))
    ``` 
### /:, :\
* `/:`는 foldLeft 와 같음
  * (z /: xs) (op) 는 z 를 초기값으로 xs 를 op 이항연산자로 fold
  * : 로 끝나는 메소드는 왼쪽이 파라미터 
* `:\`는 foldRight 와 같음
  * (xs :\ z) (op) 는 z 를 초기값으로 xs 를 op 이항연산자로 fold

### sortWith
* xs `sortWith` before 로 사용
  * x before y 는 x 가 y 보다 앞에 있어야하면 true 반환하도록 해야함
* 내부적으로 merge sort 수행
```scala
scala> List(1, -3, 4, 2, 6) sortWith (_ < _)
res4: List[Int] = List(-3, 1, 2, 4, 6)
```

## 16.8 List object의 메소드
* Factory method 를 다수 포함
### List.apply
```scala
scala> List.apply(1, 2, 3)
res5: List[Int] = List(1, 2, 3)

scala> List(1, 2, 3)
res6: List[Int] = List(1, 2, 3)
```

### List.range
* 특정 구간의 수를 채운 List 생성
* List.range(from, until) 또는 List.range(from, until, step) 의 형태로 사용
* [from, until) 즉 until 은 불포함
```scala
scala> List.range(1, 5, 2)
res7: List[Int] = List(1, 3)

scala> List.range(10, 1, -3)
res8: List[Int] = List(10, 7, 4)
```

### List.fill
* 특정 원소로 채운 n차원 List 생성
```scala
scala> List.fill(5) (3)
res17: List[Int] = List(3, 3, 3, 3, 3)

scala> List.fill(2, 2) (1)
res18: List[List[Int]] = List(List(1, 1), List(1, 1))
```

### List.tabulate
* 제공된 함수로 계산한 원소로 초기화한 n차원 List 생성
* List.fill(row, col)(value) == List.tabulate(row, col)((_, _) => value)
* List.fill(row, col)(value) == List.tabulate(row)(List.fill(col)(value))
  * List.fill(d1, d2, ..., dn)(value) == List.tabulate(d1)(List.fill(d2, ..., dn)(value))
  * List.fill(d2, d3, ..., dn)(value) == List.tabulate(d2)(List.fill(d3, ..., dn)(value))
  * ...
```scala
scala> List.tabulate(2, 2) (_ + _)
res19: List[List[Int]] = List(List(0, 1), List(1, 2))

scala> List.tabulate(4, 4) { (r, c) => (r + 1) * (c + 1) }
res25: List[List[Int]] = List(List(1, 2, 3, 4), List(2, 4, 6, 8), List(3, 6, 9, 12), List(4, 8, 12, 16))
```

## 16.9 여러 리스트를 함께 처리하기
### zipped
* 두 리스트를 묶어서 두 리스트의 각 원소들을 차례로 함수의 인자로 넣어줌
* 예를 들어 map 메소드를 사용하면 두 개의 인자를 받아 값을 반환하는 함수 사용 가능
```scala
scala> (List(10, 20), List(3, 4, 5)).zipped.map(_ * _)
res33: List[Int] = List(30, 80)

scala> (List("abc", "de"), List(3, 2)).zipped.exists(_.length != _)
res34: Boolean = false
```
* 만약 zip 을 쓴다면
```scala
scala> (List(10, 20) zip List(3, 4, 5)).map { case (a, b) => a * b }
res0: List[Int] = List(30, 80)
```

## 16.10 스칼라의 타입 추론 알고리즘 이해
* 스칼라는 **flow based**(흐름 기반)으로 동작
* abcde.sortWith(_ > _) 의 경우 우선 abcde 의 타입이 알려져 있는지 검사
  * 알려져 있다면, 인자의 타입 추론 가능
* msort(_ > _)(abcde) 의 경우 첫번째 파라미터 목록에서 타입을 추론할 수 없어 에러 발생
  * msort[Char](_ > _)(abcde) 로 타입 지정하면 해결
  * msort(abcde)(_ > _) 와 같이 두번째 파라미터 목록을 앞으로 옮기면 추론 가능
    * 스칼라는 타입 추론 시 첫번째 파라미터 목록부터 차례로 참고하기 때문
* 스칼라의 지역적인 흐름 기반 타입 추론 방식의 한계
