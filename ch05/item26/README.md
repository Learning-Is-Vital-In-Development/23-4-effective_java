---
marp : true
---
# Item 26: 로 타입은 사용하지 말라

## 유도진
---
## 목차
- 용어 정의
- 로 타입을 사용하지 않아야 하는 이유
- 로 타입을 사용하는 예외 케이스
---

## 용어 정의
제너릭 타입에 대해서 이야기하기 전 용어에 대한 정의
|한글 용어|영문 용어|예|아이템|
|:---|:---|:---|:---|
|매개변수화 타입|parameterized type|`List<String>`|item 26|
|실제 타입 매개변수|actual type parameter|`String`|item 26|
|제너릭 타입|generic type|`List<E>`|item 26, 29|
|정규 타입 매개변수|formal type parameter|`E`|item 26|
|비한정적 와일드카드 타입|unbounded wildcard type|`List<?>`|item 26|
|로 타입|raw type|`List`|item 26|

---

## 용어 정의
제너릭 타입에 대해서 이야기하기 전 용어에 대한 정의
|한글 용어|영문 용어|예|아이템|
|:---|:---|:---|:---|
|한정적 타입</br>매개변수|bounded type</br>parameter|`<E extneds Number>`|item 29|
|재귀적 타입 한정|recursive type bound|`T extends Camparable<T>`|item 30|
|제너릭 메서드|generic method|`<E> List<E> asList(E[] a)`|item 30|
|한정적 와일드카드</br>타입|bounded wildcard type|`List<? extends Number>`|item 31|
|타입 토큰|type token|`String.class`|item 33|

---
## 로 타입을 사용하지 않아야 하는 이유
- 로 타입을 사용하면 제너릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.
- 아래와 같은 코드가 존재하더라도 컴파일이 진행된다.
    ```java
    // 도장(Stamp) 인스턴스만을 취급하는 컬렉션
    private final Collection stamps = ...; // Colection이라는 로타입 사용

    // 실수로 동전(Coin)을 넣는다.
    stamps.add(new Coin(...)); // "unchecked call" 경로를 내뱉는다.
    ```

---
## 로 타입을 사용하지 않아야 하는 이유
- 컬렉션에서 이 동전을 다시 꺼내는 시점에서 오류가 발생한다!
    ```java
    for (Iterator i = stamps.iterator(); i.hasNext();) {
        Stamp stamp = (Stamp) i.next(); // ClassCastException을 던진다.
        stamp.cancel();
    }
    ```

---

## 로 타입을 사용하지 않아야 하는 이유
- 그럼에도 로타입이 존재하는 이유는 제너릭 도입 이전 코드와의 호환성을 위해서
- 로타입보다 차라리 임의 객체를 허용하는 `Object`의 매개변수화 타입을 쓰자.

---

## 로 타입을 사용하지 않아야 하는 이유
- 컴파일은 되지만 런타임에서 에러가 발생한다! (ClassCastExcetpion)
    ```java
    public class SampleObject {
        public static void main(String[] args) {
            List<String> strings = new ArrayList<>();
            unsafeAdd(strings, Integer.valueOf(42));
            String s = strings.get(0);
        }

        private static void unsafeAdd(List list, Object o) {
            list.add(o);
        }
    }
    ```
---

## 로 타입을 사용하지 않아야 하는 이유
- 컴파일이 안된다!   
(incompatible types: List<String> cannot be converted to List<Object>)
    ```java
    public class SampleObject {
        public static void main(String[] args) {
            List<String> strings = new ArrayList<>();
            unsafeAdd(strings, Integer.valueOf(42));
            String s = strings.get(0);

        }
        private static void unsafeAdd(List<Object> list, Object o) {
            list.add(o);
        }
    }
    ```
---
## 로 타입을 사용하는 예외 케이스
- `class` 리터럴에는 로타입을 써야 한다.
- 자바 명세에는 `class` 리터럴에 매개변수화 타입을 사용하지 못하도록 했다.
  (배열과 기본 타입은 허용한다.)
    ```java
    // 허용되는 것
    List.class, String[].class, int.class
    // 허용 안되는 것
    List<String>.class, List<?>.class
    ```
---
## 로 타입을 사용하는 예외 케이스
- 런타임에는 제너릭 타입 정보가 지워지므로 `instanceof` 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.
    ```java
    if (o instanceof Set) { // 로타입
        Set<?> s = (Set<?>) o; // 와일드카드 타입
    }
    ```
--- 