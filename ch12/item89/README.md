# ****아이템 89. 인스턴스 수를 통제해야 한다면 readResolve보다는 열거 타입을 사용하라****

## **과연 싱글턴일까?**

앞선 아이템 3에서는 아래와 같은 싱글턴 패턴 예제를 보았다. `public static final` 필드를 사용하는 방식이다. 생성자는 `private` 접근 지정자로 선언하여 외부로부터 감추고 `INSTANCE`를 초기화할 때 딱 한 번만 호출된다.

```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { }
    public void leaveTheBuilding() { ... }
}
```

**이 클래스에 Serializable을 구현한다면 어떻게 될까?**

```java
public class Elvis implements Serializable {

    private static final Elvis INSTANCE = new Elvis();
    private Elvis() {}
    public static Elvis getInstance() { return INSTANCE; }
}
```

하지만 이 클래스는 `Serializable`을 구현하게 되는 순간 싱글턴이 아니게 된다.

기본 직렬화를 쓰지 않거나 명시적인 `readObject` 메서드를 제공하더라도 소용이 없다. 

어떤 `readObject` 메서드를 사용하더라도 **초기화될 때 만들어진 인스턴스와 다른 인스턴스를 반환하게 된다.**

## **싱글턴 속성을 유지하는 방법 - readResolve() 메서드**

`readResolve` 메서드를 사용하면 역직렬화 과정에서 `readObject` 메서드가 만들어낸 인스턴스를 다른 것으로 대체할 수 있다.

- 만일 역직렬화 과정에서 자동으로 호출되는 `readObject` 메서드가 있더라도 `readResolve` 메서드에서 반환한 인스턴스로 대체
- 이때 새로 생성된 객체의 참조(readObject 메서드를 통해 만들어진 인스턴스)는 유지되지 않아 GC 대상이 된다.

```java
public class Elvis implements Serializable {

	private static final Elvis INSTANCE = new Elvis();

	private Elvis() {}

	public static Elvis getInstance() { return INSTANCE; }

	private Object readResolve() {
    // 기존에 생성된 인스턴스를 반환한다.
		return INSTANCE;
	}
}
```

여기서 살펴본 `Elvis` 인스턴스의 직렬화 형태는 아무런 실 데이터를 가질 필요가 없으니 모든 인스턴스 필드는 `transient` 로 선언해야 한다. 

그러니까 `readResolve` 메서드를 인스턴스의 통제 목적으로 이용한다면 모든 필드는 `transient`로 선언해야 한다.

만일 그렇지 않으면 역직렬화(Deserialization) 과정에서 역직렬화된 인스턴스를 가져올 수 있다. 즉, 싱글턴이 깨지게 된다.

# **해결책은 enum**

 `enum`을 사용하면 모든 것이 해결된다

또한, `readResolve` 메서드를 사용해 순간적으로 만들어진 역직렬화 된 인스턴스에 접근하지 못하게 하는 방법은 깨지기 쉽고, 신경을 많이 써야 된다.

자바가 선언한 상수 외에 다른 객체가 없음을 보장해주기 때문이다. 

(단, 리플렉션의 `AccessibleObject.setAccessible`이용한 방법은 제외)

```java
public enum Elvis {
  INSTANCE;
  private String[] referenceField = {"One","Two", "Tre"};
  public String[] getReferenceField() { ... }
}
```

## 정리

- 불변식을 지키기 위해 인스턴스를 통제해야 한다면 가능한 한 열거 타입을 사용하자
- 만약 enum 타입을 사용하는 것이 불가능하며, 인스턴스 수를 제한해야 한다면, `readResolve` 를 작성 해 넣어야 ㄷ하고, 그 클래스에서 모든 참조타입 인스턴스 필드를 transient 로 선언해야 한다