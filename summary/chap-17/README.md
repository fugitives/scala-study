# Chapter 17. 컬렉션 

## 17.1 Sequences
* 순서가 있다.
### List (Immutable)
* 앞부분의 원소를 빠르게 추가하거나 삭제 가능 
* 순차라 앞부분이 아니면 느리다. 
* head, tail
* length == 0 보다 isEmpty 를 쓰자

### Array (Mutable)
* 임의 위치에 원소를 효율적으로 접근 

### ListBuffer (Mutable)
* 앞뒤 원소를 빠르게 추가 삭제
* += 뒤에 추가
* +=: 앞에 추가

### ArrayBuffer 
* 앞뒤 원소를 추가 삭제 가능
* Array 유사
* 추가 삭제는 평균 상수 시간, 때로는 선형 시간 

### StringOps
* Implicit conversion 으로 String -> StringOps 변환
* Sequence 형태의 exists method 사용 가능

## 17.2 Sets and Map
* 기본은 immutable
* mutable import 하면 mutable 사용 가능

### Set
* nums ++ List(5, 6) // immutable 
* words ++= List("do") // mutable

### Map
* nums ++ List("iii" -> 3) // immutable
* words ++= List("one" -> 1) // mutable

### Default Set and Map

* Set -> HashSet, Map -> HashMap // mutable

* Set1 ... Set4 ... HashSet // immutable 
* Map1 ... Set4 ... HashMap // immutable 

### Sorted Set and Map
* SortedSet, SortedMap trait
* TreeSet, TreeMap : red black tree, Ordered trait 사용 (implicit)

## 17.3 Selecting mutable versus immutable collections
* 크기가 작은 Set/Map 인 경우 immutable 이 공간을 적게 차지함 (4일 때까지)
* immutable collection 에서 지원하지 않는 a += b operator 는 a = a + b 로 해석
* import 를 통해서 중간에 immutable -> mutable 로 변경 가능

## 17.4 Initializing collections
* Companion object 의 apply 호출
* List(1,2,3) 
* Set('a','b','c') 

### type inference
* mutable.Set\[Any](42) // 타입 추론 힌트 줘야하는 경우 
* val treeSet = TreeSet\[String]() ++ colors // colors 를 TreeSet 으로 초기화

### list, array 
* treeSet.toList
* treeSet.toArray

### immutable <-> mutable
```scala
import scala.collection.mutable
import scala.collection.immutable.TreeSet

val treeSet = TreeSet("blue","yellow","red","green")
val mutableSet = mutable.Set.empty ++= treeSet
val immutableSet = Set.empty ++ mutableSet
```

## 17.5 Tuples
* 각 element 의 type 이 다를수 있다.
* 메소드에서 여러값을 반환할때 대부분 사용한다
* 년 월 일 등은 Date 클래스를 쓰자
* ._1, ._2 로 각 element 에 접근 가능

