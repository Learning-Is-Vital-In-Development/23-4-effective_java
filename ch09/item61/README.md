---
marp: true
---

# 아이템61: 박싱된 기본 타입보다는 기본 타입을 사용하라

---

## 목차

1. 자바의 기본타입과 박싱된 기본 타입
2. 박싱된 기본 타입을 쓰면 안되는 이유
3. 박싱된 기본 타입을 써야할 때

---

## 자바의 기본 타입과 박싱된 기본타입

- 자바의 기본 타입
  - 정수: byte / short / int / long
  - 실수: float / double
  - 논리: boolean
  - 문자: char

---

## 자바의 기본 타입과 박싱된 기본타입

- 자바의 박싱된 기본 타입
  - 정수: Byte / Short / Integer / Long
  - 실수: Float / Double
  - 논리: Boolean
  - 문자: Character

---

## 박싱된 기본 타입을 쓰면 안되는 이유

- 기본 타입은 값(value)와 더불어 식별성(identity)이란 속성을 갖는다.   
  즉, 같은 값을 가진 두개의 인스턴스가 식별될 수 있다.
  ```java
  Comparator<Integer> naturalOrder = (i,j) -> (i<j) ? -1 : (i == j ? 0 : 1);
  
  naturalOrder.compare(new Integer(42), new Integer(42)); // 0이 나와야하지만 1이 나온다.
  ```
  `==`의 연산이 객체 비교를 하게 되고 다르다고 판단되며 0이 반환됨.

---

## 박싱된 기본 타입을 쓰면 안되는 이유

- 바른 결과값을 얻기 위해서는
  ```java
    Comparator<Integer> naturalOrder = (iBoxed,jBoxed) -> {
      int i = iBoxed, j = jBoxed; // autoBoxing
      return (i<j) ? -1 : (i == j ? 0 : 1);
    }
    
    naturalOrder.compare(new Integer(42), new Integer(42));
  ```

---

## 박싱된 기본 타입을 쓰면 안되는 이유

> 추가로 BoxingType은 작은 값들(-128~127)에 대해서는 내부적으로 캐시처리를 하고 있어서
> 객체 생성이 아닌 `==(assign)` 혹은 `valueOf` 메서드를 이용하면 동일한 객체를 반환한다.

---

## 박싱된 기본 타입을 쓰면 안되는 이유

- 기본 타입은 언제나 유효하지만, 박싱된 타입은 `null`이라는 유효하지 않는 값을 가질 수 있다.
  ```java
  public class Unbelievable {
      static Integer i;
  
      public static void main(String[] args) {
          if (i == 42) System.out.println("믿을 수 없군!"); // NPE 발생
      }
  }
  ```
  
---

## 박싱된 기본 타입을 쓰면 안되는 이유

- 기본 타입이 시간과 메모리 사용면에서 더 효율적이다.
  ```java
  static void unnecessaryAutoBoxing(int a) {
      Long sum = 0L;

      for (long i = 0; i <= Integer.MAX_VALUE; i++) {
          sum += i;
      }
  }

  static void removeAutoBoxing(int a) {
      long sum = 0L;

      for (long i = 0; i <= Integer.MAX_VALUE; i++) {
          sum += (Long) i;
      }
  }
  ``` 
 
---

## 박싱된 기본 타입을 쓰면 안되는 이유

- 기본 타입이 시간과 메모리 사용면에서 더 효율적이다.
  ```java
  static void checkTime(Consumer<Integer> consumer) {
      var start = Instant.now();
      consumer.accept(1);
      var end = Instant.now();
      var duration = Duration.between(start, end).toMillis();
      System.out.println("소요시간: "+duration+"ms");
  }

  public static void main(String[] args) {
      System.out.println("unnecessaryAutoBoxing");
      checkTime(UnnecessaryAutoBoxing::unnecessaryAutoBoxing);
      //소요시간: 2484ms
      System.out.println("removeAutoBoxing");
      checkTime(UnnecessaryAutoBoxing::removeAutoBoxing);
      //소요시간: 674ms
  }
  ```

---

## 박싱된 기본 타입을 써야할 때

- 컬렉션의 원소나 키, 값으로 쓴다. (어쩔 수 없다.)
- 리플렉션을 통해서 메서드를 호출할 때도 기본타입을 쓴다.(어쩔 수 없다.)
