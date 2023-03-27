---
marp: true
backgroundColor: #FFFFF0
theme: gaia
_class: lead
---

# Serializable을 구현할지는 신중히 결정하라

### 최 혁

---

## Serializable 구현 문제점

1. __Serializable을 구현하면 릴리스한 뒤에는 수정하기 어렵다.__

- 직렬화 클래스가 공개되면 직렬화된 바이트 스트림 인코딩(직렬화 상태)도 하나의 공개 API가 된다!
    - private 인스턴스 필드마저 API로 공개하게 된다.(캡슐화가 깨진다)
    - 직렬화 클래스 내부 구현을 바꾸면 이전 직렬화된 스트림 호환이 깨진다.

- serialVersionUID를 명시하지 않으면 쉽게 호환성이 깨진다.

---

2. __버그와 보안 구멍이 생길 위험이 높아진다.__

객체를 생성자로 만드는 것이 아닌, 역직렬화이 '숨은 생성자'로 생성하기에 외부의 접근에 쉽게 노출된다.

3. __해당 클래스의 신버전을 릴리스할 때마다 테스트할 것이 늘어난다.__

신버전, 구버전간의 직렬화, 역직렬화가 가능한지 테스트해야 한다. 

__참고__
역사적으로 BigInteger와 Instant같은 값타입 클래스와 컬렉션 클래스는 Serializable을 구현했고, 스레드 풀처럼 '동작'하는 객체를 표현하는 클래스는 대부분 Serializable을 구현하지 않았다.

---

## (번외)serialVersionUID를 구현하면 해결될까?

```java
public class Apple implements Serializable{
    private String color;
}
```

위 클래스를 직렬화한 데이터를 db에 저장한 이후 클래스에 필드를 추가했다.
```java
private int size;
```
이후 db에 저장했던 데이터를 역직렬화하면 에러가 발생한다.

이유는 자동생성한 serialVersionUID 다르기 때문이다.

---

__serialVersionUID는 말 그대로 직렬화 버전 UID이다. 직렬화와 역직렬화시 serialVersionUID를 동일하게 설정하면 같은 버전의 클래스로 판단하여 에러를 일으키지 않는다...?__

```java
public class Apple implements Serializable{
    private Color color;
}
```

위와 같이 클래스 필드 타입을 변경 후 실행하면 타입 에러가 발생한다.!!

__이는 같은 버전의 클래스라 하더라도 역직렬화시 타입 체크를 엄격하게 가져간다는 뜻이다.__

---

### serialVersionUID를 명시한다고 에러로부터 자유로울 수 없다.

- serialVersionUID를 명시하지 않으면 직렬화 클래스 수정 시 에러가 발생한다.

- serialVersionUID를 명시하면 직렬화 클래스 필드가 추가되거나 제거될 때 에러가 발생하지 않는다.

- 다만, 같은 버전의 직렬화 클래스라도 필드 타입이 바뀔 경우는 예외가 발생한다.

---

## 결론

__한 클래스의 여러 버전이 상호작용할 일이 없고 서버가 신뢰할 수 없는 데이터에 노출될 가능성이 없는 등, 보호된 환경에서만 쓰일 클래스가 아니라면 Serializable 구현은 신중해야 한다!__

(참고자료)
https://techblog.woowahan.com/2550/
https://techblog.woowahan.com/2551/