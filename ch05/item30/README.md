# Item 30 - 이왕이면 제네릭 메서드로 만들라

아이템 29에 이어서 메서드도 제네릭으로 만들라는 내용으로, 제네릭 메서드 사용과 이점에 대한 설명이다.

일반 메서드는 매개변수나 반환값이 고정되어 있어서, 다양한 타입에 대한 처리가 어렵거나 불가능하다.

제네릭 메서드는 매개변수나 반환값의 타입을 제네릭으로 선언하여 다양한 타입에 대한 처리가 가능하다.

## 제네릭 메서드

제네릭 메서드는 지네릭 타입 변수를 전달하는 메서드이다.

제네릭 타입의 선언과 유사하지만, 타입 변수의 범위는 선언된 메서드에 한하게 된다.

```java
// Util 클래스의 compare 제네릭 메서드
public class Util {

    public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
        return p1.getKey().equals(p2.getKey()) &&
               p1.getValue().equals(p2.getValue());
    }
}
```

이를 호출하는 방법은 아래와 같다.

```java
class Main {

    public static void main(String[] args) {
        Pair<Integer, String> p1 = new Pair<>(1, "apple");
        Pair<Integer, String> p2 = new Pair<>(2, "pear");
        boolean same = Util.compare(p1, p2);
    }
}
```

예전에 작성한 [블로그 글](https://velog.io/@doxxx93/JAVA-Generics)
