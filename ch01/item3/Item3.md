# Item 3: private 생성자나 열거 타입으로 싱글턴임을 보증하라 
___
## 싱글턴(singleton)이란?
인스턴스를 오직 하나만 생성할 수 있는 클래스

싱글톤을 사용하는 클라이언트 코드를 테스트 하는 것은 어렵다. 싱글톤이 인터페이스를 구현한 것이 아니라면 mock으로 교체하기 어렵기 때문이다.

## 싱글턴을 만드는 방식
### 1. 모든 생성자를 private으로 감추고 유일하게 인스턴스에 접근가능한 public static final 필드를 만든다.
~~~
public class Foo{
   public static final Foo INSTANCE = new Foo();
   private Foo() {}
}
~~~
위 방법은 클래스가 초기화될 때 만들어진 인스턴스가 하나임이 보장된다.(생성자를 통한 객체 생성이 불가능하기 때문)

다만 리플렉션 API인 AccessibleObject.setAccessible을 사용해 private 생성자를 호출할 수 있다. 이를 방지하려면 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 된다.(코드로 해보자)

### 2. 정적 팩터리를 public static 맴버로 제공한다.
~~~
public class Foo{
   private static final Foo INSTANCE = new Foo();
   private Foo() {}
   public static Foo getInstance() { return INSTANCE; }
}
~~~
1번 방식과 마찬가지로 인스턴스가 하나임이 보장되나 리플렉션을 통한 예외는 똑같이 적용해야 한다.

1번 방식에 비해 장점은 다음과 같다.

- 해당 클래스가 싱글턴임이 API에 드러남, 간결함
- API를 바꾸지 않고 싱글턴이 아니게 변경 가능
- 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있음
- 정적 팩터리의 메서드 참조를 Supplier(기본 참조형 인터페이스)로 사용할 수 있음

다만 위의 장점이 필요없다면 public 필드 방식이 좋다.

1번과 2번 방법으로 만든 싱글턴 클래스를 직렬화하려면 단순히 Serializable을 구현한다고 선언하기만 하면 안 된다. 모든 인스턴스 필드를 transient 선언하고 readResolve 메서드를 제공해야 한다. 이렇게 하지 않으면 인스턴스를 역직렬화할 때마다 새로운 인스턴스가 만들어진다.

간단히 설명하면 자바는 역직렬화할 때 내부적으로 사용하는 readResolve 메서드가 있다. 이 readResolve의 구현을 정적 싱글턴 필드를 반환하게 하면 매번 역직렬화할 때마다 동일한 인스턴스를 반환하게 되어 문제를 해결할 수 있다.(12장 직렬화)

### 3. 원소가 하나인 열거 타입을 선언한다.
~~~
public enum Foo {
   INSTANCE;

   public void leaveTheBuilding() { ... }
}
~~~
1,2번 방식보다 더 간결하고, 추가 작업 없이 직렬화할 수 있고, 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다.

**대부분의 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.** (단, 싱글턴이 Enum 이외의 클래스를 상속해야 한다면 이 방법을 사용할 수 없다)