---

marp: true

---

# 아이템 19
## 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

---


### 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문서로 남겨야한다.

> 재정의 가능 : public, protected 메서드 중 final이 아닌 모든 메서드

API 설명
* 어떤 순서로 호출하는지
* 각각의 호출 결과가 이어지는 처리에 어떤 영향을 주는지
* 재정의 가능 메서드를 호출할 수 있는 모든 상황
* 메서드의 내부 동작 방식

---

### @implSpec

![AbstractionCollection](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcvVh9Z%2FbtrWZWfNZxV%2FUWek1cd9cK8eIvqXykI2L1%2Fimg.png)

---

### protected 메서드로 공개

효율적인 하위 클래스를 큰 어려움 없이 만들기 위해서는 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별해서 protected 메서드 형태로 공개해야 할 수 있다.

---

![AbstractionList](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FKpDqq%2FbtrW6puIyQq%2FqNqo3OhOmOmXc7P55sVluk%2Fimg.jpg)


---

### 어떤 메서드를 protected로 노출해야할지 결정하기

* 실제 하위 클래스를 만들어 시험해보는 것이 최선이다.
* protected 메서드는 내부구현에 해당하므로 가능한 적어야한다.
* 너무 적게 노출해서 상속으로 얻는 이점마저 없애지 않도록 주의해야한다.

---

### 상속용으로 설계한 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 한다.

* 꼭 필요한 protected 멤버를 놓쳤다면 하위 클래스를 작성할 때 그 빈자리가 확연히 드러난다.
* 하위 클래스를 여러 개 만들 때까지 전혀 쓰이지 않는 protected 멤버는 사실 private이었어야 할 가능성이 크다.
* 하위 클래스는 3개 이상 작성하고 이중 하나는 제 3자가 작성해봐야 한다.
* 상속용으로 설계한 클래스에 문서화한 내부 사용 패턴, protected 메서드와 필드 구현에 대한 결정들이 클래스의 성능과 기능에 영원한 족쇄가 될 수 있다.

---

### 상속용 클래스의 생성자는 재정의 가능 메서드를 호출해서는 안 된다.

* 상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로 하위 클래스에서 재정의한 메서드가 하위 클래스의 생성자보다 먼저 호출된다.
* 재정의한 메서드가 하위 클래스의 생성자에서 초기화하는 값에 의존하면 의도대로 동작하지 않는다.
---

~~~java
public class Object {
	// 잘못된 예시 - 생성자가 재정의 가능 메서드를 호출한다.
	public Object() {
		overrideMe();
	}

	public void overrideMe() {
	}
}

public final class SubObject extends Object {
	// 초기화되지 않은 final 필드, 생성자에서 초기화한다.
	private final LocalDateTime localDateTime;

	public SubObject() {
		localDateTime = LocalDateTime.now();
	}

	// 재정의 가능 메서드, 상위 클래스의 생성자가 호출한다.
	@Override
	public void overrideMe() {
		System.out.println(localDateTime);
	}

	public static void main(String[] args) {
		SubObject subObject = new SubObject();
		subObject.overrideMe();
	}
}
~~~

---

### clone, readObject 모두 재정의 가능 메서드를 호출해서는 안 된다.

* Cloneable과 Serializable을 구현한다면 이들을 구현할 때 따르는 제약도 생성자와 비슷하다.
* readObject는 하위 클래스의 상태가 역직렬화되기 전에 재정의한 메서드를 호출한다.
* clone의 경우 하위 클래스의 clone 메서드가 복제본의 상태를 수정하기 전에 재정의한 메서드를 호출한다.

---

### Serializable을 구현한 상속용 클래스가 readResolve, writeReplace 메서드를 가진다면 protected로 선언해야한다.

* private으로 선언하면 하위 클래스에서 무시된다.

---

### 상속용으로 설계하지 않은 클래스

상속용으로 설계하지 않은 클래스는 상속을 금지해야한다.
* 클래스를 final로 선언한다.
* 모든 생성자를 private, package-private으로 선언하고 public 정적 팩터리를 만든다.

---

### 상속을 허용해야 한다면

클래스 내부에서 재정의 가능 메서드를 사용하지 않게 만들고 이 사실을 문서로 남겨야한다.

1. 각각의 재정의 가능 메서드는 자신의 본문 코드를 private '도우미 메서드(helper method)'로 옮긴다.
2. 도우미 메서드를 호출하도록 수정한다.
3. 재정의 가능 메서드를 호출하는 다른 코드들 모두 이 도우미 메서드를 직접 호출하도록 수정한다.

---

~~~java
public class Object {
	public Object() {
		// overrideMe();
		helperMethod();
	}

	public void overrideMe() {
		helperMethod();
	}
	
	private void helperMethod(){
        }
}

public final class SubObject extends Object {
	private final LocalDateTime localDateTime;

	public SubObject() {
		localDateTime = LocalDateTime.now();
	}
	
	@Override
	public void overrideMe() {
		System.out.println(localDateTime);
	}
	
	public static void main(String[] args) {
		SubObject subObject = new SubObject();
		subObject.overrideMe();
	}
}
~~~

