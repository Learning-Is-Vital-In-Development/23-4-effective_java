# 아이템 40: @Override 애너테이션을 일관되게 사용하라

## Override 애너테이션
`@Override` 애너테이션은 하위 타입에서 상위 타입의 메서드를 재정의하였다는 것을 명시할 때 사용됩니다.
```java
public class Parent {

    public void info() {
        System.out.println("this is Parent class");
    }
}
public class Child extends Parent {

    @Override
    public void info() {
        System.out.println("this is Child class");
    }
}
```
## 주의점
`@Override` 애너테이션으로 재정의하기 위해서는 다음과 같이 지켜야 하는 규칙들이 있습니다.
* 메서드의 인자의 갯수와 타입이 완전히 일치해야 합니다.
* 메서드의 리턴 타입 또한 같아야 합니다.
* 인자의 이름은 달라져도 됩니다.

## 주의점을 지키지 않은 경우
예시로 다음과 같은 코드를 보겠습니다.
```java
class User {

    private string name;
    
    // 기타 메서드들 존재 (hashCode 등등)
    public boolean equals(User another) {
        return another.name == name;
    }
}
```
위에서는 `User` 클래스를 작성할 때, equals 메서드를 name 필드 값으로 비교하기 위해 작성해두고 있습니다. 그러나 이 경우는 주의점을 지키지 않아 의도치 않은 결과를 가져올 수 있습니다.
### 지키지 않은 이유
자바에서의 모든 객체는 `Object` 클래스의 하위 객체입니다. 그리고 `Object` 클래스에서는 `equals` 메서드가 존재합니다. 만약 name 필드 값으로 비교를 하고 싶다면, 이 메서드를 오버라이딩 해야 합니다. `Object` 클래스의 `equals` 메서드는 다음과 같습니다.
```java
public boolean equals(Object obj) {
    return (this == obj);
}
```
즉, `User` 클래스의 `equals` 메서드는 인자의 타입이 `Object`가 아니라 `User`를 집어넣었기 때문에 `Override` (재정의)가 일어나지 않고 오히려 `Overloading` (다중정의 - 동일한 이름을 갖는 함수를 다양한 타입에 대해 처리하도록 여러 개로 정의한 것)가 일어나게 된 것입니다. 만약 이런 상황에서 `@Override`를 명시한다면 컴파일 에러가 발생합니다.  
올바른 작성법은 다음과 같습니다.
```java
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof User)) {
        return false;
    }
    User user = (User) obj;
    return user.name == name;
}
```
## 예외 사항
정리하자면, `@Override`를 붙일 경우 잘못된 사용일 때 컴파일 에러를 던지기 때문에 상위 클래스의 메서드를 재정의하려는 모든 메서드에 애너테이션을 다는 것이 좋습니다. 그러나 달지 않아도 되는 유일한 때가 있는데, 구체 클래스에서 상위 클래스의 추상 메서드를 재정의하거나 인터페이스를 재정의할 때는 붙이지 않아도 됩니다. `@Override`를 붙이는 이유는 재정의를 제대로 했는지 검증하기 위함인데, 구체 클래스인데 상위 클래스의 추상 메서드를 재정의하지 않은 것이 남아있을 경우에는 컴파일러가 알려주기 때문입니다. 이는 인터페이스를 재정의했을 때도 동일합니다.
