## ****아이템 45. 스트림은 주의해서 사용하라****

스트림이란 자바 8부터 **다량의 데이터 처리 작업**(순차/병렬)을 돕고자 나온 API이며, 

두 가지 핵심적인 추상 개념을 제공한다. 

스트림 API는 메서드 연쇄를 지원하는 프루언트 API(fluent API)이다.

### 스트림(stream)

: 데이터 원소의 유한 혹은 무한 시퀀스

### 스트림 파이프라인(stream pipeline)

: 원소들로 수행하는 **연산 단계를 표현**하는 개념

## 스트림 파이프라인

### **스트림 파이프라인 연산**

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산으로 끝나며, 

그 사이에 하나 이상의 중간 연산이 있을 수 있다.

```java
public class Dummy {
    public static void main(String[] args) {
        List<String> nameList = Arrays.asList("a", "b", "c");

        nameList.stream() // 소스 스트림
                .filter(s -> s.startsWith("a")) // 중간 연산
                .forEach(System.out::println); // 종단 연산
    }
}
```

**1) 중간 연산**

각 중간 연산은 **스트림을 어떠한 방식으로 변환**(transfrom)하는 역할을 수행한다. 중간 연산들은 모두 한 스트림을 다른 스트림으로 변환한다. 예를 들어, 각 원소에 함수를 적용하거나 특정 조건을 만족 못하는 원소를 걸러낼 수 있다.

중간 연산의 종류

| map | 입력 T 타입 요소를 R 타입 요소로 변환 |
| --- | --- |
| filter | 조건을 충족하는 요소를 필터링 |
| flatMap | 중첩된 구조를 한 단계 평탄화하고 단일 원소로 변환한 스트림 생성 |
| peek | 스트림 내의 각각의 요소를 대상으로 특정 연산을 수행 |
| skip | 처음 n개의 요소를 제외하는 스트림 생성 |
| limit | maxSize까지의 요소만 제공하는 스트림 생성 |
| distinct | 스트림 내의 요소의 중복 제거 |
| sorted | 스트림 내 요소를 정렬 |

**2) 종단 연산**

종단 연산은 마지막 중간 연산이 내놓은 **스트림에 최후의 연산을 가하는** 역할을 한다. 예를 들어, 원소를 정렬해 컬렉션에 담거나 특정 원소를 하나 선택하는 식이다.

종단 연산의 종류

| forEach | 스트림을 순회 |
| --- | --- |
| reduce | 연산을 이용해 모든 스트림 요소를 처리하여 하나의 결과로 만듦 |
| collect | 스트림의 연산 결과를 컬렉션 형태로 모아줌 |

### **지연 평가(lazy evaluation)**

지연 평가란, **평가가 종단 연산이 호출될 때 이뤄지며 종단 연산에 쓰이지 않는 데이터 원소는 계산 자체에 쓰이지 않는 것**을 의미한다. 

즉, 종단 연산이 없으면 스트림 파이프라인은 아무 일도 하지 않게 된다

이러한 지연 평가 특성으로 인해 무한 스트림을 다룰 수 있게 된다.

### **순차 실행**

기본적으로 스트림 파이프라인은 순차적으로 수행된다. 

파이프라인을 병렬로 실행하려면, 파이프라인을 구성하는 스트림 중 하나에서 parallel 메서드를 호출해주기만 하면 되나, 효과를 볼 수 있는 상황은 많지 않다

```java
Stream<String> parallelWords = Stream.of(wordArray).parallel();
```

## 스트림 제대로 사용하기

```java
public class Anagrams {
	  public static void main(String[] args) throws IOException {
      File dictionary = new File(args[0]);
      int minGroupSize = Interger.parseInt(args[1]);

      Map<String, Set<String>> groups = new HashMap<>();
      try(Scanner s = new Scanner(dictionary)){
      	while(s.hasNext()){
        	String word = s.next();
            groups.computeIfAbsent(alphabetize(word), (unused) -> new TreeSet<>()).add(word);
        }
      }

      for(Set<String> group : groups.values())
      	if(group.size() >= minGroupSize)
        	System.out.println(group.size() + ":" + group);
    }

	// 도우미 메서드
    public static String alphabetie(String s){
    	char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

`computeIfAbsent()` 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 키에 매핑된 값을 반환하고 없다면 건네진 함수 객체를 키에 적용하여 값을 계산한 다음 키와 값을 매핑해놓고, 계산된 값을 반환한다. 즉, 해당 메서드를 활용하며누 각 키에 다수의 값을 매핑하는 맵을 쉽게 구현할 수 있다.

**스트림을 과하게 사용 - 따라하지 말 것!**

```java
public class StreamAnagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        //사전 파일을 제대로 닫기 위해 try-with-resources 활용
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(
                    groupingBy(word -> word.chars().sorted()
                            .collect(StringBuilder::new,
                                    (sb, c) -> sb.append((char) c),
                                    StringBuilder::append).toString()))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .map(group -> group.size() + ": " + group)
                    .forEach(System.out::println);
        }
    }
}
```

스트림을 과하게 활용하여, 사전 파일 여는 부분을 제외하고 프로그램 전체가 단 하나의 표현식으로 처리되고 있다. 이처럼 스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다.

**스트림을 적절히 활용**

```java
public class HybridAnagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word)))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }
    // 도우미 메서드
    public static String alphabetie(String s){
    	char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

이 스트림의 파이프라인에는 중간 연산이 없으며, 

종단 연산에서는 모든 단어를 수집해 맵으로 모으고 있다. 

이후 맵의 `values()`가 반환한 값으로부터 새로운 `Stream<List<String>>` 스트림을 열며, 스트림의 원소는 아나그램 리스트가 된다.

또한 `alphabetize()` 과 같은 세부 구현은 주 프로그램 로직 밖으로 빼내 전체적인 가독성을 높였다. 이처럼 도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다는 스트림 파이프라인에서 훨씬 커진다.

> 람다에서는 타입 이름을 자주 생략하므로, 매개변수의 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다.
> 

**char 값을 처리할 때는 스트림을 사용하면 안된다.**

만약 `alphabetize()` 를 스트림을 사용해서 다르게 구현했다면, 명확성이 느려질뿐더러 느려진다. 

자바가 기본 타입인 char용 스트림을 지원하지 않기 때문이다.

```java
"Hello World!".chars().forEach(System.out::print);
// char가 아닌 정수값이 출력됨 : 739488237102..
```

```java
// 명시적인 형변환
"hellow World".chars().forEach(x -> System.out.println((char) x);
```

## 스트림의 적절한 활용

스트림 파이프라인은 되풀이 되는 계산을 주로 함수 객체(람다/메서드 참조)로 표현하고 반복 코드에는 코드 블록을 사용해 표현한다.

### 스트림을 적용하기 안좋은 경우

**1. 지역 변수를 읽거나 수정해야 하는 경우**

코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다. 하지만 람다에서는 사실상 final인 변수만 읽을 수 있고, 지역변수 수정은 불가능하다.

**2. return, break, continue를 수행하거나 메서드 예외를 던져야 하는 경우**

코드 블록에서는 `return` / `break` / `continue` 문으로 블록의 흐름을 제어하거나, 메서드 선언에 명시된 검사 예외를 던질 수 있다. 하지만 람다는 모두 불가능하다.

### 스트림을 적용하기 좋은 경우

1) 원소들의 시퀀스를 일관되게 변환해야 하는 경우

2) 원소들의 시퀀스를 필터링 해야 하는 경우

3) 원소들의 시퀀스를 하나의 연산을 사용해 결합해야 하는 경우(더하기, 연결하기, 최솟값 구하기 등)

4) 원소들의 시퀀스를 컬렉션에 모으는 경우(공통된 속성을 기준으로)

5) 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾을 경우

### 스트림으로 처리하기 어려운 경우

스트림 파이프라인은 **하나의 중간 연산을 거치면 원래의 값(원래의 스트림)을 잃는 구조**를 가지고 있다. 따라서 여러 단계의 파이프라인을 거칠 때 원본 스트림을 사용해야 한다면 스트림으로 처리하기 어렵다.

# 스트림과 반복 중 어느 쪽을 선택해야 할까?

스트림과 반복 중 어느 쪽을 선택해야 할지 바로 알기 어려운 경우가 많다. 

이 경우에는 스트림과 반복 두 가지 방법으로 구현해보고 더 나은 쪽을 선택하자.

**데카르트 곱 계산을 반복 방식으로 구현**

```java
// 반복 방식
privatestatic List<Card> newDeck() {
  List<Card> result =new ArrayList<>();
	for (Suit suit : Suit.values()) {
		for (Rank rank : Rank.values()) {
      result.add(new Card(suit, rank));
    }
  }
	return result;
}
```

스트림을 사용하지 않는 사람에게 가장 친숙한 방식이다. 

그렇다면 이제 같은 동작을 스트림 방식으로 구현한 코드를 살펴보자.

**데카르트 곱 계산을 스트림 방식으로 구현**

```java
// 스트림 방식
privatestatic List<Card> newDeck() {
	return Stream.of(Suit.values())
	       .flatMap(suit ->
	            Stream.of(Rank.values())
	                .map(rank ->new Card(suit, rank)))
	       .collect(toList());
}
```

스트림이나 함수형 프로그래밍이 익숙하지 않고 확신이 서지 않는다면 반복 방식을, 

스트림 방식이 더 나아 보이고 동료들도 스트림을 선호한다면 스트림 방식을 사용하자.

**즉, 정해진 정답은 없다.** 

개인 취향과 프로그래밍 환경에 따라 선택하면 된다.

# 핵심 정리

**스트림을 사용했을 때 깔끔하게 처리할 수 있는 경우**가 있고, 

**반복 방식으로 더 직관적으로 표현할 수 있는 경우**가 있다. 

많은 작업들이 이 둘을 적절히 조합해서 사용할 때 가장 멋지게 해결된다.

어느 쪽을 선택하는 정해진 규칙은 없다. 

만약, **어느 방법이 더 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 선택해 사용하자.**