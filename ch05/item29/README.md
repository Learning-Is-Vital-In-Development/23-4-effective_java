## **아이템 29. 이왕이면 제네릭 타입으로 만들라.**

## 핵심 요약

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.
- 그러니 새로운 타입을 설계할 때는 형변환 없이도 사용할 수 있도록 하라.

## 왜 제네릭 타입으로 만들어야 하는가?

- Stack 코드

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

위 코드는 아이템 7에서 다룬 Stack 구현 코드이다. 현재 내부에서는 원소가 Object[] 로 관리되고 있음을 확인할 수 있다. 이는 전형적으로 제네릭 타입으로 구현할 수 있는 사례이다.

제네릭 타입으로 변경한다고 해서 현재 이 코드를 사용하는 클라이언트가 받는 영향은 없다.

- 변환 결과

```java
public class Stack {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new E[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = elements[--size];
        elements[size] = null;
        return result;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

해당 부분에서 컴파일 시 오류가 나타난다.


![ee](https://user-images.githubusercontent.com/55054505/218265229-d04f4d2b-d7ef-4b7b-b162-b873172146bd.jpg)

**아이템 28번에서 나왔듯이,**

**E 와 같은 실페화 불가 타입으로 배열을 만들 수 없다.**

그렇다면 어떻게 해결을 해야 될까?

크게 2가지 방법이 있다.

## 1. 배열 생성 금지제약을 우회하기

- 비검사 형변환 안전성 확인하기
    
    : 컴파일러가 프로그램 타입이 안전한지 증명할 방법이 없음
    
    : 그러나 개발자는 가능하다.
    
    책에서 예시를 든 Stack 같은 경우 세 가지를 통해 안전성을 검증한다.
    
    1. emelemts 접근자가 private 인지
    2. 클라이언트에게 반환이 되는지?
    3. 다른 메서드에 전달되는 일이 없는지 
    
    비검사 형변환이 안전함을 직접 증명했다면, 
    
    범위를 최소로 좁혀서 
    
    `@SuppressWarnings`을 사용해서 해당 경고를 숨겨준다.
    
    ```java
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
    ```
    
    숨김으로써 
    
    - Stack 코드를 깔끔하게 컴파일이 가능하다.
    - 명시적으로 형변환하지 않아도 ClassCastException을 걱정 없이 사용할 수 있게 된다
    

## 2. elements 필드 타입을 Object[]로 바꾸는 방법

E [] 에서 Object[] 로 바꾸는 방법이다.

```java
public class Stack<E> {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = (E) elements[--size];  // 경고
        elements[size] = null;
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

일단 바꾸게 된다면 

```java
 public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = (E) elements[--size];  // 경고
        elements[size] = null;
        return result;
    }
```

pop 부분에서 elements를 추출하는 과정에서 경고가 뜨게 된다. 

해당 부분도 1번와 같이 `@SuppressWarnings` 을 이용해 제거를 해주면 된다.

```java
public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
```

## 금지 제약 우회 방법 vs Object[]로 변경

첫 번째 방법은 가독성이 좋다. 

- 배열을 E[]으로 선언하여 E 타입 인스턴스만 받음을 알 수 있다
- 코드도 짧다.
    - 1번의 경우 타입 캐스팅을 생성자 한번에 해주면 된다.
    - 2번의 경우 매번 타입 캐스팅을 해줘야 된다는 문제가 있다.
    

하지만 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염을 일으킬 수 있어, 

힙 오염이 마음에 걸리는 개발자는 두 번째 방식을 사용하기도 한다.

## 힙오염

일반적인 ArrayList 코드 이다.

```java
ArrayList<String> arrayList = new ArrayList<>();
arrayList.add("String1");
arrayList.add("String2");

Object obj = arrayList;
```

해당 코드는 정상적으로 컴파일이 된다.

```java
ArrayList<Integer> arrayList2 = (ArrayList<Integer>)obj;
arrayList2.add(new Integer(100));
arrayList2.add(new Integer(200));
```

이 코드는 정상적으로 컴파일이 될까?

사실 개발자가 보면 잘못된 코드인 것을 알아챌 수 있지만, 컴파일러는 그러질 못한다.

### 타입 캐스팅 연산자는 컴파일러가 체크하지 않는다.

컴파일러가 컴파일을 진행하다가 타입 캐스팅 연산을 만났을 때 캐스팅 대상 객체를 캐스팅 할 수 있느냐 없느냐는 검사하지 않는다.

단지 캐스팅 했을때 대입되는 참조변수에 저장할 수 있느냐만 검사할 뿐이다.

그래서 대상 객체가 캐스팅할 수 없는 타입으로 캐스팅을 시도하면 컴파일 타임이 아니라 런타임에 익셉션이 발생하는 것이다.

따라서 위 코드에서

```java
ArrayList<Integer> arrayList2 = (ArrayList<Integer>)obj;
```

부분은 컴파일러가 따로 체크를 하지 않고 가상 머신에게 맡기는 것이다.

### 제네릭 타입 파라미터는 컴파일 시점에 지워진다.

위에서 설명했듯이 제네릭 타입 파라미터는 컴파일이 끝나면 컴파일러가 제거하고 그 자리에 Object 를 넣어준다.

즉, `ArrayList<Integer>` 이나 `ArrayList<String>` 이나 컴파일이 끝나면`ArrayList<Object>` 과 똑같아진다는 뜻이다.

따라서

```java
ArrayList<Integer> arrayList2 = (ArrayList<Integer>)obj;
```

이 코드는 지극히 타당한 코드가 되는 것이다.

또한 현재 arrayList2 가 참조하는 리스트는 내부적으로 저장하는 요소의 타입이 Object 이기 때문에문자열을 저장하든, 래퍼 객체를 저장하든 뭐든 저장이 가능한 상태이다.

따라서,

```java
ArrayList<Integer> arrayList2 = (ArrayList<Integer>)obj;
arrayList2.add(new Integer(100));
arrayList2.add(new Integer(200));
```

이 코드는 우리의 생각과 달리 문제없는 코드가 되는 것이다.

원인은 

- 원시 타입과 매개변수 타입을 동시에 사용하는 경우
- 확인되지 않은 형변환을 수행하는 경우
- 소스코드를 분리하면서 발생할 수 있다.



# 결론

- 클라이언트에서 직접 형변환해야 하는 타입보다 **제네릭 타입이 더 안전하고 쓰기 편하다.**
    - 그러니, 새로운 타입을 설계할 땐 형변환 없이도 사용할 수 있도록 제네릭을 사용하자.
- 기존 타입 중 제네릭이 있어야 하는 게 있다면, 제네릭 타입으로 변경하자.
    - **기존 클라이언트에게는 아무 영향을 주지 않으면서 새로운 사용자를 훨씬 편하게 해준다.**