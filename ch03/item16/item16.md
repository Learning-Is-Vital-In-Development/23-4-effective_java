# 아이템 16 : public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 퇴보된 클래스 작성법
```java
public class User {
    public String firstName;
    public String lastName;
}
```
만약 위와 같은 방식으로 클래스를 작성한다면 바로 알 수 있는 문제들이 있습니다.  
* 데이터에 직접적으로 접근이 가능합니다. 때문에 캡슐화를 지킬 수 없습니다.
* 필드가 `final`이 아니기 때문에 불변을 보장할 수 없습니다. (`final`이 붙어도 불변을 보장할 수 있을 뿐이지, 나머지 단점들은 극복할 수 없습니다.)
* 외부에서 필드에 접근할 때 별도의 작업을 수행할 수 없습니다. 단지 값을 가져오거나 정의할 수만 있습니다.
* API를 수정하지 않고는 내부 표현을 바꿀 수 없습니다.

### API를 수정하지 않고는 내부 표현을 바꿀 수 없다?!
API를 수정하지 않고는 내부 표현을 바꿀 수 없다는 말은 다음 코드를 보면 됩니다.
기존 코드가 이렇게 되어 있는데,
```java
public class User {
    public String firstName;
    public String lastName;
    public User(String firstName, String lastName) { /* 생성자 로직 */ }
}

User user = new User("hyunjoon", "choi");
printName(user.firstName, user.lastName); // 이름 출력 메서드 있다고 가정
doSomtingMethod(user.firstName, user.lastName); // 별도의 메서드
```
이때 `printName` 메서드가 `firstName`과 `lastName`이 합쳐진 형태의 파라미터를 원한다고 가정한다면, 다음과 같이 API(프레임워크 or 라이브러리 - `User`)의 필드를 직접 수정해야 합니다. 때문에 API를 수정하지 않고는 내부 표현을 바꿀 수 없다고 한 것입니다.
```java
public class User {
    public String name;
    public User(String name) { /* 생성자 로직 */ }
}
User user = new User("hyunjoon choi");
printName(user.name);
doSomethingMethod(user.firstName, user.lastName); // 문제 발생!
```
하지만 `doSomethingMethod`에서는 변경되기 이전의 필드를 가지고 있기에 문제가 발생합니다. 이처럼 `User`를 사용하는 모든 코드에 영향을 끼칠 수 있습니다.

### 개선 작업
다음 코드로 변경한다면 문제들을 개선할 수 있습니다.
```java
public class User {
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) { /* 생성자 로직 */ }

    public String getFirstName() { /* getter */ }
    public String getLastName() { /* getter */ }

    public String setName(String firstName) { /* setter */ }
    public String setNation(String lastName) { /* setter */ }

    // 요구사항을 충족하기 위한 메서드
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
User user = new User("hyunjoon", "choi");
printName(user.getFullName());
doSomethingMethod(user.getFirstName(), user.getLastName());
```
* 필드들이 모두 `private`로 되었기 때문에 캡슐화가 제공되었습니다.
* 만약에 클래스가 `public`이라면, 접근자를 제공하여 클래스 내부 표현 방식을 언제든지 바꿀 수 있는 유연성을 제공합니다.
* 접근자 메서드에서 다른 로직을 추가하는 등을 통해 필드에 접근할 때 별도의 작업을 수행할 수도 있습니다.
* * *
## 만약 클래스의 접근 제한이 다르다면?
클래스가 `default`이거나 `private` 중첩 클래스, `protected` 클래스라면 데이터 필드를 직접 노출해도 큰 문제는 없습니다.
* `default` 클래스는 해당 클래스를 포함하는 패키지 안에서만 접근 가능합니다.
* `protected` 클래스
* `private` 중첩 클래스는 해당 클래스를 포함하는 외부 클래스에서만 접근 가능합니다.

예시로 `private` 중첩 클래스는 다음과 같게 됩니다.
```java
public class Outer {

    private static class Inner {
        public String firstName;
        public String lastName;
    }

    public Inner getInner() { // Outer 외부에서는 Inner 클래스 내부 조작이 불가능합니다.
        Inner inner = new Inner();
        inner.firstName = "hyunjoon";
        inner.lastName = "choi";
        return inner;
    }
}
```
* 따라서 `Outer` 외부의 코드는 손댈 필요 없이 데이터 표현 방식을 바꿀 수 있습니다. (`Outer`에서만 변경하면 되기 때문입니다.)
* `default` 클래스 또한 패키지 바깥 코드는 변경할 필요 없이 데이터 표현 방식을 바꿀 수 있으며 (클라이언트가 같은 패키지 안이어야 하며, 더 외부에 있는 별도의 클라이언트에서는 코드 변경이 일어나지 않습니다.), `protected` 클래스도 상속받은 클래스 또는 같은 패키지를 제외한 외부에서는 코드 변경이 일어나지 않게 됩니다.
* 이들은 클래스 선언이나 이를 사용하는 클라이언트 코드 면에서 더 깔끔하다고 할 수 있습니다.
* * *
## 궁금한 점 & 느낀점 & 결론
* `getter`, `setter`를 사용했다고 해서 캡슐화를 제공한다고 할 수 있을까? 예전에 오브젝트에서 `getter` `setter`가 캡슐화를 의미하지는 않는다고 비슷한 말을 봤던 것 같은데..
* 어렵게 느껴지는 이유는 아무래도 이것을 활용해 애플리케이션을 많이 만들어보지 않았기 때문이라고 생각한다. 여기서 그치지 말고 적용해보면서 지금까지 작성한 게 과연 맞는건지 검증하는 작업이 반드시 필요함을 느꼈다.
* 결론은 `public` 클래스에서는 필드를 `public`으로 정의하는 것은 좋은 선택이 아니지만, `default` 클래스나 `private` 중첩 클래스에서는 가끔 `public`으로 정의하는 게 더 나은 경우가 있다는 것이다.
