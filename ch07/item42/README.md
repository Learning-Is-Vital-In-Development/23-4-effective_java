---
marp: true
---

# 아이템42: 익명 클래스보단 람다를 사용하라

---

### 목차

1. 함수 타입 표현 - 낡은 기법
2. 람다
3. 람다의 한계

---

### 함수 타입 표현 - 낡은 기법

---

예전에는 함수 타입을 표현할 때 추상 메서드를 하나만 담은 인터페이스를 사용했다. 이러한 인터페이스의 인스턴스를 함수 객체라 하여, 특정 함수나 동작을 나타내는 데 사용했다.

---

```java
Collections.sort(words, new Comparator<String>() {
	public int compare(String s1, String s2){
		return Integer.compare(s1.length(), s2.length());
	}
});
```

해당 코드에서 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻하며, 문자열을 정렬하는 구체적인 구현은 익명 클래스로 구현했다.

이러한 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.

---

### 람다

---

자바8에 와서 지금은 함수형 인터페이스라고 부르는 이 추상 메서드 한 개만을 가지고 있는 인터페이스의 인스턴스를 람다식을 사용해 만들 수 있게 되었다.

---

람다는 함수나 익명 클래스와 개념은 비슷하지만 코드는 훨씬 간결하다.

```java
//V1
Collections.sort(word,
	(s1, s2) -> Integer.compare(s1.length(), s2.length());

//V2 - 비교자 생성 메서드 사용
Collections.sort(words, comparingInt(String::length));

//V3 - List 인터페이스 sort 메서드 사용
words.sort(comparingInt(String::length));
```

---

- 람다, 매개변수(s1, s2), 반환값의 타입은 각각 `Comparator<String>`, `String`, `int`지만 코드에서 따로 언급이 없다. 우리 대신 컴파일러가 문맥을 살펴 타입을 추론해 준다.
- 타입을 명시해야 코드가 더 명확할 때 제외하고는 람다의 모든 매개변수 타입을 생략하는 것이 좋다.

---

람다를 이용하면 아이템34에서 언급했던 열거 타입의 인스턴스 필드를 이용해 상수별로 다르게 동작하는 코드를 구현하기 쉬워진다.

```java
public enum Operation {
    PLUS  ("+", (x, y) -> x + y),
    MINUS ("-", (x, y) -> x - y),
    TIMES ("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    @Override public String toString() { return symbol; }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

---

각 열거 타입 상수의 동작을 람다로 구현해 생성자로 넘기고 생성자는 이 람다를 인스턴스 필드에 저장한 후 메서드에서 필드에 저장된 람다를 호출하기만 하면 된다.

---

### 람다 한계

---

- 람다는 메서드나 클래스와 달리 이름이 없고 문서화도 못한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다. (한 줄일때 가장 좋고 세 줄을 넘어가지 않는게 좋다)
- 열거 타입 생성자에 넘겨지는 인수들의 타입은 컴파일타임에 추론되기 때문에 열거 타입 생성자 안의 람다는 열거 타입의 인스턴스 멤버에 접근할 수 없다.(인스턴스가 런타임에 생성되기 때문)
- 상수별 동작을 단 몇 줄로 구현하기 어렵거나, 인스턴스 필드나 메서드를 사용해야만 한다면 상수별 클래스 몸체를 사용해야 한다.
- 추상 클래스의 인스턴스를 만들 때 람다를 사용할 수 없기 때문에 익명 클래스를 써야 한다.
- 람다는 자신을 참조할 수 없다. 람다에서의 `this`는 바깥 인스턴스를 가리킨다. 반면 익명 클래스에서의 `this`는 익명 클래스의 인스턴스 자기 자신을 가리키기 때문에 함수 객체가 자신을 참조해야 한다면 반드시 익명 클래스를 사용해야 한다.
