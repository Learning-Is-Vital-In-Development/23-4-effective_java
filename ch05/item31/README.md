---
marp: true
---

# 아이템 31: 한정적 와일드카드를 사용해 API 유연성을 높이라

---

### 목차

1. 한정적 와일드 카드 타입을 사용하는 원칙
2. 적용 예시

---

### 한정적 와일드 카드 타입을 사용하는 원칙

1. 유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라
2. 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다.
3. `producer`는 `extends`하고 `consumer`는 `super`를 사용하라.(PECS)
4. 매서드 선언에 타입 매개변수가 한번만 나오면 와일드카드로 대체하라.
5. 비한정 타입 매개변수라면 비한정 와일드카드로 바꾸고, 한정 타입 매개변수라면 한정 와일드카드로 바꾸면 된다.

---

### 적용 예시

```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty()
    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }
}
```

---

### 적용 예시

```java
public class Stack<E> {
    ...

    public static void main(String[] args) {
        Stack<Number> numberStack = new Stack();
        Iterable<Integer> intergers = ...;
        numberStack.pushAll(integers); // Error
        // Integer는 이미 선언된 Number의 하위 타입이지만,
        // 제너릭은 불공변임으로 이를 치환할 수 없어 인자로 받을 수 없다.
    }
}
```

---

### 적용 예시

```java
public class Stack<E> {
    ...
    // Producer - extends
    public void pushAll(Iterable<? extends E> src) {
	for (E e : src) {
		push(e);
	}
}

    public static void main(String[] args) {
        Stack<Number> numberStack = new Stack();
        Iterable<Integer> intergers = ...;
        numberStack.pushAll(integers); // 성공
    }
}
```

---

### 적용 예시

```java
public class Stack<E> {
    ...
    public void popAll(Collection<E> dst) {
        while(!isEmpty()) {
            dst.add(pop());
        }
    }

    public static void main(String[] args) {
        Collection<Object> dst = new ArrayList<>(10);
        numberStack.popAll(dst); // Error
        // 불공변성 때문에 인자로 받을 수 자체가 없다.
    }
}
```

---

### 적용 예시

```java
public class Stack<E> {
    ...
    // Consumer - super
    public void popAll(Collection<? super E> dst) {
        while(!isEmpty()) {
            dst.add(pop());
        }
    }

    public static void main(String[] args) {
        Collection<Object> dst = new ArrayList<>(10);
        numberStack.popAll(dst);
    }
}
```


---

### 적용 예시

```java
public static <E extends Comparable<E>> E max(List<E> List)
public static <E extends Comparable<? super E>> E max(List<E extends ?> List)

List<ScheduledFuture<?>> scheduledFutures = ...;

// ScheduledFuture가 Comparable<ScheduledFuture>을 구현하지 않았음.
// Delayed를 상속 받고 있으며 Delayed는 Comparable<Delayed>를 확장
// 즉, ScheduledFuture는 Delayed와도 비교가 가능하기 때문에
// 수정하기 전에는 max가 해당 리스트를 인자로 받지 못함.
```


---

### 적용 예시

```java
// index i와 j의 원소의 자리를 바꾸는 함수
public static <E> void swap(List<E> list, int i, int j)
public static void swap(List<?> list, int i, int j)

// List<?> 타입은 null 외에 다른 값을 넣을 수 없다.(런타임 오류 방지를 위해)
// 그래서 값을 다시 넣기 위해서는 명시적인 타입으로 변환해준다.(Helper)
public static void swap(List<?> list, int i, int j) {
	swapHelper(list, i , j)
}


// 명시적인 매개변수 타입 선언을 통해 타입의 안전성을 얻어 리스트에 넣을 수 있도록 처리
public static <E> void swapHelper(List<E> list, int i, int j) {
	list.set(i, list.set(j, list.get(i)));
}
```