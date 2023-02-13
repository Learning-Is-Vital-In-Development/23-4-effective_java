---

marp: true

---

# 아이템32
## 제네릭과 가변인수를 함께 쓸 때는 신중하라

---

# 가변인수 메서드

가변 인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 만들어진다.
-> 배열을 클라이언트에 노출하는 문제가 발생한다.
-> varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

---

# 실체화 불가 타입 varargs 매개변수

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.

다른 타입 객체를 참조하는 상황에서는 컴파일러가 자동 생성한 형변환이 실패할 수 있다.

이는 제네릭 타입 시스템이 약속한 타입 안정성의 근간이 흔들려 버린다.

**제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.**

~~~java
static void danferous(List<String>... stringLists) {
	List<Integer> intList = List.of(42);
	Object[] objects = stringLists;
	objects[0] = intList; // 힙 오염 발생
	String s = stringLists[0].get(0); // ClassCastException
	}
~~~

---

# 실체화 불가 타입 varargs 매개변수

제네릭 배열을(List<String>[]) 허용하지 않지만 제네릭 varargs 매개변수를 받는 메서드를 선언할 수 있게 허용한다.
-> 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 메서드가 실무에서 매우 유용하다

ex) Array.asList(T... a), Collections.addAll(Collection<? super T> c, T... elements) ...

---

# @SafeVarargs

자바7 전 : @SuppressWarnings("unchecked")
자바7 이후 : @SafeVarargs

@SafeVarargs는 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치이다.

---

# 메서드 안전한지 확인하기

* 메서드가 이 배열에 아무것도 저장하지 않는다.
* 배열의 참조가 밖으로 노출되지 않는다.

즉 varargs 매개변수 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달한다면 그 메서드는 안전하다.

---

# 제네릭 가변인수 배열에 다른 메서드가 접근

배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 쪽의 콜스택까지 전이할 수 있다.

~~~java
static <T> T[] toArray(T... args) {
    return args;
}
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return toArray(a, b);
    case 1: return toArray(a, b);
    case 2: return toArray(a, b);
    }
    throw new AssertionError(); // 도달할 수 없다. 
}
public static void main(String[] args) {
    String[] attributes = pickTwo("test1", "test2", "test3");
}
~~~

---

# 이중 타입추론

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcvLWda%2FbtrYRQLBhZG%2FETlm1RCwCL2ToravheXE11%2Fimg.png)

(컴파일러는 T의 타입인자가 필요한데 T는 target type을 주지 않으면 Object이다. )

~~~java
static <T> T[] toArray(T... args) {
    return args;
}

static Object[] toArray(Object[] args){
    return args;
}
~~~

pickTwo 메서드에서 toArray을 호출할 때 T는 제네릭 변수이므로 Object으로 타입추론을 한다.

---

# 이중 타입추론

pickTwo 메서드는 String 파라미터 통해 String 타입을 추론하고 형변환을 시도한다.
하지만 toArray 메서드의 반환타입이 Object[]이기에 형변환이 실패한다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdfF1hL%2FbtrYTgirXiE%2Fqfc6lOdxPhuQkUcOK47RSK%2Fimg.png)

~~~java
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return toArray(a, b);
    case 1: return toArray(a, b);
    case 2: return toArray(a, b);
    }
    throw new AssertionError();
}
public static void main(String[] args) {
    String[] attributes = pickTwo("test1", "test2", "test3");
}
~~~

---

# 이중 타입추론


~~~java
static Object[] toArray(Object[] args){
    return args;
}
static Object[] pickTwo(Object a, Object b, Object c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return toArray(new Object[]{a,b});
    case 1: return toArray(new Object[]{a,c});
    case 2: return toArray(new Object[]{b,c});
    }
    throw new AssertionError();
}
public static void main(String[] args) {
    String[] attributes = (String[])pickTwo("test1", "test2", "test3");
}
~~~



---

## 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다.

예외로는
* @SafeVarargs로 제대로 어노테이트된 또 다른 varargs 메서드에 넘기는 것
* 배열 내용의 일부 함수를 호출만 하는 일반 메서드에 넘기는 것

---

# List로 수정하기

컴파일러가 이 메서드의 타입 안정성을 검증할 수 있다.
하지만 코드가 지저분해지고 속도가 조금 느려질 수 있다.

~~~java
static <T> List<T> flatten(List<? extends T>... lists) {
	... // 수정 전
	}


static <T> List<T> flatten(List<List<? extends T>> lists) {
	... // 수정 후
	}
~~~

---

# List로 수정하기

~~~java
static <T> List<T> pickTwo(T a, T b, T c) {
	switch(ThreadLocalRandom.current().nextInt(3)) {
	case 0: return List.of(a, b);
	case 1: return List.of(a, b);
	case 2: return List.of(a, b);
	}
	throw new AssertionError();
	}
public static void main(String[] args) {
	List<String> attributes = pickTwo("test1", "test2", "test3");
	}
~~~