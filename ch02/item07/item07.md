---
marp : true
---

# Item 07 : 다 쓴 객체 참조를 해제하라

## 유도진

---

## 목차
- 메모리에 따른 언어 분류
- 객체 참조 해제를 유의해야 하는 상황 및 해법
- GC와 Reference에 대하여

---

## 메모리에 따른 언어 분류
기준 : Heap 영역의 메모리를 언어 차원에서 관리하느냐?

- Managed Language : 언어 차원에서 메모리 관리가 진행되는 언어
    - **Java**, Python, JavaScripts 등 다양한 최근의 언어들


- UnManaged Language : 개발자가 직접 메모리를 관리를 해야하는 언어
    - C, C++

---

## 메모리에 따른 언어 분류

Managed Language는 메모리를 전혀 신경 쓰지 않아도 될까?
- 언어 차원에서 지원해주는 방법(자바에선 Garbage Collection)이 완벽할 수 없다.
- 개발자가 놓치는 순간 Out Of Memory를 보게 될 것이다.

간단한 스택 클래스 코드를 보자
```java
import java.utils.Arrays;

public class stack {
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private Object[] elements;
	private int size = 0;

	public Stack() {
		elements = new Object[DEFAULT_INITIAL_CAPACITY];
	}
```

---

```java

	public void push(Object e) {
		ensureCapacity();
		elements[size++] = e;
	}

	public Object pop() {
		if (size == 0) {
			throw new EmptyStackException();
		}

		return elements[--size];
	}

	/**
	 * 원소를 위한 공간을 적어도 하나 이상 확보한다.
	 * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
	 */
	private void ensureCapacity() {
		if (elements.length == size) {
			elements = Arrays.copyOf(elements, 2 * size + 1);
		}
	}

}
```
안타깝게도 이 코드에서는 메모리 누수가 발생하고 있다.

---

## 객체 참조 해제를 유의해야하는 상황 및 해법

1. 자기 메모리를 직접 관리하는 클래스라면 프로그래머라면 항상 메모리 누수에 주의해야 한다.

    위 예시의 `Stack` 클래스는 `elements`라는 자신의 원소들을 직접 관리하고 있다.
    `pop()` 코드를 다시 보자.
    ```java
    public Object pop() {
		if (size == 0) {
			throw new EmptyStackException();
		}

		return elements[--size];
	}
    ```
    반환한 elements의 참조를 제거하지 않고 있다. 따라서 GC는 해당 원소가 더 이상 사용되지 않을 것이라는 점을 몰라 GC를 하지 않고 놔두고 된다.


---

## 객체 참조 해제를 유의해야하는 상황 및 해법

1. 자기 메모리를 직접 관리하는 클래스라면 프로그래머라면 항상 메모리 누수에 주의해야 한다.

    이 경우의 해결 방법은 명시적으로 참조를 해제해주는 것이다.
    ```java
    // 제대로 구현된 pop()
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제

        return result;
    }
    ```

    위 코드에서 볼 수 있는 것과 같이 명시적으로 참조를 해제함으로써 해당 원소는 Unreachable 하게 된다.

---

## 객체 참조 해제를 유의해야하는 상황 및 해법

1. 자기 메모리를 직접 관리하는 클래스라면 프로그래머라면 항상 메모리 누수에 주의해야 한다.

    명시적으로 null 처리를 하는 것은 굉장히 예외적인 경우여야 한다. 
    일반적으로는 변수의 scope를 최소가 되게 정의하여(item57) 자연스럽게 참조 해제가 이루어지도록 해야한다.

    또, 이렇게 null처리를 하면 참조해제 되어야할 변수에 실수로 접근하는 코드가 있는 경우 NPE(NullPointException)이 발생하여 조기에 발견할 수 있다.

---

## 객체 참조 해제를 유의해야하는 상황 및 해법

2. Cache 참조

    Cache 역시 메모리 누수의 주범이다. 캐시 등록 이후 별도의 해제 동작이 없을 경우 지속적으로 참조되어 있어 메모리 누수가 발생한다. 

    만약 운이 좋게, Cache의 Key값이 Cache 외부에서 참조되는 동안에만 entry가 살아 있으면 된다면 `WeakHashMap`을 사용하자. 다 사용한 entry는 자동으로 제거가 된다.

    보통은 entry의 유효 기간을 정확히 정의하기가 어렵기 떄문에, 생성된 지 오래될수록 가치를 낮추어 제거 대상으로 간주한다. 스케쥴러를 통하거나 Cache에 새로운 entry가 추가되는 시점에 처리할 수 있다.

---

## 객체 참조 해제를 유의해야하는 상황 및 해법

3. Listener나 CallBack과 같은 것들

    이벤트 리스너나 콜백과 같이 특정 목적을 위해 등록해놓은 객체의 경우 해당 목적을 달성하거나 더 이상 사용하지 않게 될 경우 명시적으로 해제를 해주어야 한다. 해제 과정을 대게 누락하여 메모리 누수가 발생하게 된다. 

    위의 Cache와 유사하게 WeakReference를 이용하거나 별도의 해제 로직을 추가하여 관리해야 한다.

---

## GC와 Reference에 대하여

GC의 동작방식을 간략히 설명하면 다음과 같다.
1. 힙(Heap) 영역 내 객체 중에서 가비지(Garbage)를 찾아낸다.
2. 찾아낸 가비지를 제거한 후 메모리를 회수한다.

이때 가비지를 판별하기 위해 `reachability`라는 개념을 사용한다. 어떤 객체가 유효한 참조가 있으면 `reachable`로, 없으면 `unreachable`로 구별한다. 이때 unreachable 객체를 가비지로 판단하여 GC를 수행한다.

---

## GC와 Reference에 대하여

Java의 Reference는 4가지가 있다.
1. Strong Reference
2. Soft Reference
3. Weak Reference
4. Phantom Reference

우리가 일반적으로 변수를 할당하고 제거하는 행위들은 Strong Reference에 속한다.
하지만 GC 처리를 위해 다른 형태의 Reference를 사용할 수 있고 이는 `java.lang.ref`에서 제공하고 있다.

---

## GC와 Reference에 대하여

`SoftReference`와 `WeakReference`, `PhantomReference`는 관련 Reference 객체를 통해서 실제 객체에 접근하도록 감싸는 형태로 사용하게 된다. 이때 위 3가지 클래스에 의해서 생성된 객체를 `reference object`라고 하며 해당 객체에 의해 참조되고 있는 객체는 `referent`라고 부른다.

추가적인 자세한 동작 방식에 대해서는 [Naver D2의 Java Reference와 GC(2013.04.22)](https://d2.naver.com/helloworld/329631)를 보며 개념을 정리하길 바란다.

---

# Q & A

---

# 감사합니다
