---
marp: true
---

# 아이템46: 스트림에서는 부작용 없는 함수를 사용하라

---

### 목차

1. 스트림 패러다임
2. 수집기(Collector)
3. Collectors의 메서드
4. 정리

---

### 스트림 패러다임

---

스트림은 함수형 프로그램에 기초한 패러다임이기 때문에 스트림이 제공하는 표현력, 속도, 병렬성을 얻으려면 이 패러다임을 함께 받아들여야 한다.

스트림 패러다임의 핵심은 계산을 일련의 변환으로 재구성 하는 부분이다.

이때 각 변환 단계는 이전 단계의 결과를 받아 처리하는 순수함수여야 한다.

---

💡 순수함수란 오직 입력만이 결과에 영향을 미치는 함수를 말한다.

---

**스트림 패러다임을 이해하지 못한 API만 사용한 코드**

```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()) {
	words.forEach(word -> {
		freq.merge(word.toLowerCase(), 1L, Long::sum);
	});
}
```

- 해당 코드는 모든 작업이 종단 연산인 `forEach`에서 일어나는데, 이때 외부 상태를 수정하는 람다를 실행하면서 문제가 생긴다.
- `forEach` 연산은 종단 연산 중 기능이 가장 적고 ‘덜’ 스트림 답다.
- `forEach` 가 그저 스트림이 수행한 연산을 보여주는 일 이상을 하는 것은 바람직하지 않다.

---

**스트림을 제대로 활용한 코드**

```java
Map<String, Long> freq;
try(Stream<String> words = new Scanner(file).tokens()){
	freq = words
		.collect(groupingBy(String::toLowerCase, counting()));
```

- 이 코드는 코드 수집기(`collect`)를 사용하는데 스트림을 사용하려면 반드시 익혀야 하는 개념이다.

---

### 수집기(Collector)

---

Collector는 스트림의 원소들을 객체 하나에 취합하는 전략을 캡슐화한 블랙박스 객체라고 생각하라.

수집기를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.

수집기는 총 세가지로, `toList()`, `toSet()`, `toCollection(collectionFactory)`가 있다.

이들은 차례로 리스트, 집합, 프로그래머가 지정한 컬렉션 타입을 반환한다.

---

수집기를 활용한 스트림 파이프라인

```java
List<String> topTen = freq.ketSet().stream()
	.sorted(comparing(freq::get),reversed())
	.limit(10)
	.collect(toList());
```

---

### Collectors의 메서드

---

Collectors의 대부분의 메서드는 스트림을 맵으로 취합하는 기능으로 스트림의 각 원소가 키 하나와 값 하나에 연관되어 있기 때문에 진짜 컬렉션에 취합하는 것보다 훨씬 복잡하다.

`toMap(keyMapper, valueMapper)`은 가장 간단한 맵 수집기로 스트림 원소를 키에 매핑하는 함수와 값에 매핑하는 함수를 인수로 받는다.

```java
private static final Map<String, Operation> stringToEnum =
	Stream.of(values().collect(
		toMap(Object::toString, e -> e));
```

이 형태는 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다.

---

병합 함수를 포함한 인수 3개를 받는 `toMap`은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다.

```java
Map<Artist, Album> topHits = albums.collect(
	toMap(Album::artist, a -> a, maxBy(comparing(Album::sales))));
```

---

네 번째 인수로 맵 팩터리를 받는 `toMap`은 네 번째 인수를 활용해 `EnumMap`이나 `TreeMap`처럼 원하는 특정 맵 구현체를 직접 지정할 수 있다.

---

Collectors가 제공하는 또 다른 함수에는`gropingBy`가 있다.

이 함수는 입력으로 분류 함수를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다.

분류 함수는 입력받은 원소가 속한 카테고리를 반환한다. 그리고 이 카테고리가 해당 원소의 맵 키로 쓰인다.

---

`groupingBy`는 가장 간단한 형태인 분류 함수를 인수로 받아 맵을 반환하는 것부터 분류함수와 다운스트림 수집기를 인수로 받는 형태, 여기에 더해 맵 팩터리를 받는 형태까지 있다.

---

Collectors에 정의되어 있지만 수집과는 관련이 없는 함수가 있다.

---

그중 `minBy`와 `maxBy`는 비교자를 인수로 받아 스트림에서 값이 가장 작은 혹은 가장 큰 원소를 찾아 반환한다.

---

마지막으로 `joining`은 문자열 등의 `CharSequence` 인스턴스의 스트림에만 적용할 수 있다. 이 중 매개변수가 없는 `joining`은 단순히 원소들을 연결하는 수집기를 반환한다.

한편 인수 하나짜리 `joining`은 `CharSequence` 타입의 구분문자를 매개 변수로 받아 연결 부위에 이 구분문자를 삽입해 문자열을 반환한다.

---

### 정리

- 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다.
- 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 한다.
- 종단 연산 중 `forEach`는 스트림이 수행한 계산 결과를 보고할 때만 사용하고 계산 자체에는 이용하지 말아야 한다.
- 스트림을 올바르게 사용하게 만들어주는 수집기인 `toList`, `toSet`, `toMap`, `groupingBy`, `joining`을 잘 알아두자
