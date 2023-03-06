

## ****아이템 49. 매개변수가 유효한지 검사하라****

**오류는 가능한 한 빨리 발생한 곳에서 잡아야 한다.** 오류를 발생한 즉시 잡지 못하면 해당 오류를 감지하기 어려워지고, 오류 발생 지점을 찾기 어려워진다.

메서드나 생성자로 넘어온 매개변수가 가질 수 있는 제약조건의 예

- 인덱스 값은 음수면 안된다.
- 객체 참조는 null 이 아니어야 한다

매개변수의 제약조건이 있다면  

- 문서화
- 메서드 시작 부분에서

검사해야 한다

## 매개변수 검사를 제대로 하지 못하면 발생하는 문제

1. 메서드가 수행 중간에 모호한 예외를 던지며 실패할 수 있다.

2. 메서드가 잘 수행되지만 잘못된 결과를 반환할 수 있다.

3. 메서드는 문제없이 수행됐지만, 미래의 알 수 없는 시점에 메서드와 관련 없는 오류를 낼 수 있다. (실패 원자성을 어기는 상황)

## 문서화

만약 public , protected 메서드가 예외를 던진다면 이를 문서화해야 한다

`@throws` 자바독 태그를 사용해 매개변수 값이 잘못됐을 때 던지는 예외를 문서화하자. 

일반적으로는 

- **IllegalArgumentException**
- **IndexOutOfBoundsException**
- **NullpointerException**

중 하나가 될 것이다.

매개변수의 제약을 문서화한다면 그 제약을 어겼을 때 발생하는 예외도 함께 기술해야 한다. 이런 간단한 방법으로 API 사용자가 제약을 지킬 가능성을 크게 높일 수 있다.

보통 아래와 같은 방식으로 제약을 기술한다.

```java
/**
* (현재 값 mod m) 값을 반환한다. 이 메서드는
* 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다.
*
*@param m 계수(양수여야 한다.)
*@return 현재 값 mod m
*@throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
*/
public BigIntegermod(BigInteger m) {
if (m.signum() <= 0)
thrownew ArithmeticException("계수(m)은 양수여야 합니다. " + m);
 ...// 계산
}
```

모든 public 메서드에 적용되는 예외인 경우, 클래스 레벨 주석을 기술하는 것이 훨씬 깔끔한 방법이다.

# Null을 검사하는 여러가지 방법

- Java7

java.util.Objects.requireNonNull 메서드를 사용하면 

유연하고 편하게 null 검사를 수행할 수 있다.

```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```

- Java9

Objects에 범위 검사 

`checkFromIndexSize`

`checkFromToIndex`

`checkIndex`

등의 메서드는 null 검사 메서드만큼 유연하지는 않지만, 

예외 메시지를 지정할 필요가 없고, 

리스트와 배열이고, 닫힌 범위가 아니라면 아주 유용하고 편리하게 사용할 수 있다.

# assert

공개되지 않은 메서드는 개발자가 메서드 호출을 통제할 수 있다. 

따라서 매개변수 유효성을 검증하고 오직 유효한 값만이 메서드에 넘겨진다는 것을 보증해야 한다.

단언문 assert를 통해 실행되는 문장이 참이라는 것을 보증할 수 있다.

```java
privatestaticvoidsort(long a[].int offset,int length){
assert a !=null;
assert offset >= 0 && offset <= a.length;
assert length >= 0 && length <= a.length - offset;
    ...// 계산 수행
}
```

assert는 Java4부터 지원하며 자신이 단언한 조건이 무조건 참이라고 선언한다. 

**실행되는 문장이 참(true)이라면 그냥 지나가고,** 

**거짓(false)이라면 AssertionError가 발생한다.**

assert는 런타임에 아무런 효과가 없고 성능 저하도 없다.

보통 개발 중에 테스팅하는 목적으로 사용된다.

### 그외

- 나중에 쓰기 위해 저장하는 매개변수 유효성 검사
    - 메서드에서 직접 사용하지는 않지만 나중에 사용하기 위해서 **필드로 저장하는 경우**
    - ex) 검사가 잘 안되서 List 에 null 이 들어갈 경우
- 메서드 몸체 실행전에 매개변수 유효성 검사
    - **유효성 검사의 비용이 지나치게 높거나 실용적이지 않을 때,**
        
        **혹은 계산 과정에서 암묵적으로 검사가 수행되는 경우이다.**
        
    - Collections.sort(List)는 두 원소를 비교할 수 있는 타입인지 정렬 과정에서 비교한다. 비교할 수 없는 타입이라면 `ClassCastException`이 발생