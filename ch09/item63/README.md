# 아이템 63: 문자열 연결은 느리니 주의하라
자바에서는 문자열 연결을 `+` 연산자를 활용하여 구현할 수 있습니다.
```java
public static void main(String[] args) {

    String result = "hello";
    result += " world";
    
    System.out.println(result); // "hello world"
}
```
그러나 문자열 연결을 더 많이 사용할 경우, 성능 저하가 발생됩니다. 문자열 연결 연산자로 문자열 n개를 잇는 시간은 n^2에 비례합니다.
```java
public static void main(String[] args) {

    String result = "";
    for (int i = 0; i < 1000000; i++) {
        result += "a";
    }
    return result;
}
```
대신, `StringBuilder`의 `append`를 사용하면 좋습니다.
```java
public static void main(String[] args) {

    StringBuilder result = new StringBuilder("");
    for (int i = 0; i < 1000000; i++) {
        result.append("a");
    }
    return result.toString();
}
```
## 원인 및 설명
* `String`은 불변 (immutable) 속성을 가집니다. `String` 객체의 값은 변경되지 않는 상수입니다. 만약 "a"를 가진 `String`과 "b"를 가진 `String` 객체를 결합한다면, "ab"를 가진 `String` 객체를 새로 만들어야 합니다.
* `StringBuiler`는 문자열 값을 저장하는 `char`형 배열 `value`, 현재 문자열 크기의 값을 가지는 `int` 형의 `count`를 가집니다. `value`에 남아있는 공간에 들어올 수 있는 문자열이 들어오면 삽입하고, 그렇지 않다면 `value` 크기를 두 배로 증가시키면서 기존의 문자열을 복사하고 새로운 문자열을 삽입합니다.
* 즉, `StringBuilder`는 필요할 때만 배열을 늘리기 때문에 문자열 연산의 경우 `String`보다 효율적이라고 할 수 있습니다.
* `StringBuffer`도 있는데, `StringBuilder`와의 차이점은 공통 메서드 동기화로 멀티 스레드 환경에서 사용할 수 있다는 점 입니다. 그 경우가 아니라면 `StringBuilder`를 사용합니다.
## 참고 자료
* [StringBuffer, StringBuilder 가 String 보다 성능이 좋은 이유와 원리](https://cjh5414.github.io/why-StringBuffer-and-StringBuilder-are-better-than-String/)
* [자바 StringBuffer, StringBuilder 개념부터 사용법까지](https://wakestand.tistory.com/245)
