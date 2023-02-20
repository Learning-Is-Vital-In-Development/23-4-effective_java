---
marp: true
---

## **아이템 36. 비트 필드 대신 EnumSet을 사용하라**

---

**비트 열거 상수**

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALID = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    public void applyStyles(int styles) { ... }
}
```


예전에는 열거 값들이 집합으로 사용될 경우 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해왔다. 
비트별 OR를 이용해 여러 상수를 하나의 집합으로 모을 수 있고, 이런 집합을 비트 필드라 한다.

---

```java
  public static final int STYLE_BOLD = 0x00000002;
  public static final int STYLE_ITALID = 0x00000004;
  public static final int STYLE_UNDERLINE = 0x00000008;
  public static final int STYLE_STRIKETHROUGH = 0x00000010;
```
---


# 단점

비트 필드 또한 정수 열거 상수이므로 정수 열거 상수의 단점을 그대로 지닌다

1. 비트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어렵다.

2. 비트 필드 하나에 녹아 있는 모든 원소를 순회하기도 까다롭다.

3. 최대 몇 비트가 필요한지를 처음부터 예상하고 적절한 타입을 선택해야 한다.

---

다행히도 이젠 더 나은 방안인 java.util 패키지의 **EnumSet** 클래스가 있다. 

EnumSet은 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다.

---

# EnumSet

---


1. EnumSet 클래스는 Set인터페이스를 구현하여 어떤 Set 구현체와도 함께 사용할 수 있으며 타입 안전하다.
2. EnumSet 내부는 비트 벡터로 구현되어있으며, 원소가 총 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현하여 비트필드에 비견되는 성능을 보여준다.
3. removeAll과 retainAll 과 같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현하였다.

---



```java
public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH } 

    public void applyStyles(int styles) { ... }
}
```

---


EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공하는데, 다음 코드에서는 그 중 of 메서드를 사용했다.

```java

text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC))

```

```java

EnumSet.allOf(Style.class);

```

---

EnumSet을 사용할 때는 몇 가지 **고려할 사항**이 있다.



1. 열거형 값만 포함 할 수 있으며, 모든 값은 동일한 열거형이어야 한다.

2. `null`을 추가할 수 없다.

3. 스레드에 안전하지 않으므로, 필요할 경우 외부에서 동기화한다.

4. 복사본에 fail-safe iterator(장애 발생시 작업을 중단하지 않음) 를 사용하여 컬렉션을 순회할 때, 컬렉션이 수정되어도 `ConcurrentModificationException`이 발생하지 않는다.

---


### EnumSet 사용의 장점

- EnumSet의 모든 메서드(contains 등)는 산술 비트 연산을 사용하여 구현되므로 일반적인 연산이 매우 빠르게 계산된다.

 - EnumSet은 HashSet 같은 다른 Set 구현체와 비교했을 때, 데이터가 예상 가능한 순서로 저장되어 있고, 각 계산을 하는데 하나의 비트만이 필요하므로 더 빠르다고 할 수 있다. 또한 HashSet 처럼 데이터를 저장할 버킷을 찾는 데 hashcode를 계산할 필요가 없다.

- 더욱이 EnumSet은 비트 벡터의 특성상 더 작은 메모리를 사용한다.

---

# 정리

 열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드를 사용할 이유는 없다. 

EnumSet 클래스가 비트 필드 수준의 명료함과 성능을 제공하고 열거 타입의 장점까지 주기 때문이다.

EnumSet의 유일한 단점은 불변 EnumSet을 만들 수는 없지만, Collections.unmodifiableSet으로 EnumSet을 감싸 사용할 수 있다.