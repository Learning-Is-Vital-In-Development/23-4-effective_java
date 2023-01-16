# 아이템 11 : equals를 재정의하려거든 hashCode도 재정의하라

## 배경 지식
### 논리적 비교 vs 물리적 비교
`논리적 비교`는 객체가 저장하고 있는 데이터가 동일한지를 뜻하며, `물리적 비교`는 두 객체가 같은 참조값을 가지고 있는지를 뜻하는 용어입니다.

### equals 메서드
`equals` 메서드는 `java.lang.Object`에 속해있는 메서드로써, 내부 로직은 다음과 같이 구성되어 있습니다.
```java
public class Object {
    ...
    public boolean equals(Object obj) {
        return (this == obj);
    }
}
```
이처럼 `equals` 메서드는 기본적으로는 물리적 비교를 하고 있는 것을 확인할 수 있습니다.  
그러나 `equals`를 재정의 함으로써 논리적 비교를 하게끔 변경할 수도 있습니다. (또, 사용되고 있는 방식입니다. (예: `String` 클래스의 `equals` 메서드는 문자열의 데이터가 같은지를 비교합니다.)) 

### hashCode 메서드
`hashCode` 메서드 또한 `java.lang.Object`에 속해있는 메서드입니다. 객체의 메모리 번지를 이용해서 해시코드를 만들어 리턴합니다.
<details>
<summary>해시코드란?</summary>
<div markdown="1">
해시코드는 객체를 식별하는 하나의 정수값을 의미합니다.
</div>
</details>  

***

## 객체 비교 과정
자바의 컬렉션 프레임워크 중 하나인 `HashSet`, `HashMap`, `Hashtable`은 다음 방법으로 두 객체가 동등한지 비교합니다. (참고: <a href="https://tecoble.techcourse.co.kr/post/2020-07-29-equals-and-hashCode/">우아한테크코스 블로그</a>)
<img src="https://tecoble.techcourse.co.kr/static/c248e8d79140c18ed9895d1c95dd7ad0/54e75/2020-07-29-equals-and-hashcode.png">  

* `equals` 비교에 사용되는 정보가 도중에 변경되지 않았다면 애플리케이션이 실행되는 동안 그 객체의 `hashCode` 메서드는 항상 동일한 값을 반환합니다.
* `hashCode` 리턴 값이 같아야 `equals` 메서드를 실행해 볼 수 있습니다. 즉, `equals` 메서드가 동일하다고 판정을 내렸다면 두 객체의 `hashCode` 리턴 값은 같습니다.
* 두 객체가 다른 객체여도, 두 객체의 `hashCode`가 같을 수 있습니다. 그러나 사진에서 보듯, 다른 객체에 대해서는 다른 `hashCode`를 반환해야 해시테이블의 성능이 좋습니다.

***

## 만약 equals 메서드만 재정의하고 hashCode 메서드를 재정의하지 않았다면?
`equals` 메서드를 논리적 비교로 하게끔 재정의하였으나, `hashCode` 메서드를 재정의하지 않았다면 다음과 같은 문제가 발생합니다.
* 위의 두 번째 원칙에 따르면, `equals` 메서드가 동일하다고 판정을 내렸을 경우 두 객체의 `hashCode` 리턴 값 또한 같아야 합니다. 그러나 지금은 `hashCode` 메서드를 재정의하지 않은 상태입니다. 때문에 두 객체의 `hashCode`는 객체의 메모리 번지를 이용하여 비교하기 때문에, `hashCode`는 이 둘이 다르다고 나오게 됩니다.

### 해결법1: 모든 객체에서 같은 해시코드를 반환하도록 설정
가장 간단한 방식으로, 모든 객체에서 같은 해시코드를 반환하도록 해 보겠습니다.
```java
@Override
public int hashCode() {
    return 42;
}
```
이 방법은 다음의 문제가 있기에 실제로 사용되는 방식은 아닙니다.
* 우선, 객체 비교 시 같은 해시코드가 나오므로 동일하다고 판정됩니다.
* 그러나 모든 객체에게 똑같은 값만 내어주므로 모든 객체가 해시테이블의 버킷 하나에 담기게 됩니다. 이는 해시의 특징인 빠른 검색 (`O(1)`)을 느리게 (`O(n)`) 만드는 행위입니다. (각기 다른 키(객체)에 대하여 해시함수가 동일한 값을 줄 경우, 키에 해당하는 공간을 배열로 만들어 보관하게 됩니다. 따라서 이 배열에서 탐색하게 되므로 순차 탐색이 되는 것 입니다.)  
(자바 8 이후부터는 같은 키에 객체가 8개 이상 들어갈 경우 배열에서 트리 형태로 전환되어 시간복잡도가 `O(logn)`으로 개선되었습니다.)
* 이 문제는 위의 원칙 중 마지막 원칙에 해당하는 내용입니다.

### 해결법2: 더 좋은 hashCode 메서드 작성
대략적으로 진행되는 재정의 된 `hashCode` 메서드의 내부 흐름입니다.
```java
@Override
public int hashCode() {
    // 책 예시: PhoneNumber 객체는 line, prefix, areaCode 값으로 비교합니다.
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}
```
* 핵심 필드에 대한 해시 코드를 계산하는 방식으로 진행됩니다.
* 필드가 `기본 타입`일 시 `해당_타입.hashCode(필드)`로 필드의 해시코드를 얻습니다.
* 필드가 `참조 타입`이며 이 클래스의 `equals` 메서드가 이 필드의 `equals`를 호출해 비교할 경우, 필드의 `hashCode`를 호출합니다.
* 필드가 `배열`일 경우 핵심 원소 각각을 별도 필드처럼 다룹니다. 모든 원소가 핵심 원소면 `Arrays.hashCode`를 사용합니다.
* 숫자를 곱하는 이유는 검사되는 필드가 서로 비슷할 때의 해시 효과를 높이기 위함입니다.

### 해결법3: 한 줄짜리 hashCode 메서드 작성
`java.util.Objects` 클래스는 임의의 개수만큼 객체를 받아 해시코드를 계산해주는 정적 메서드인 `hash`를 제공합니다.
```java
@Override
public int hashCode() {
    // 책 예시: PhoneNumber 객체는 line, prefix, areaCode 값으로 비교합니다.
    return Objects.hash(line, prefix, areaCode);
}
```
* 이 방식은 해결법2의 로직을 한 줄로 구현할 수 있습니다.
* 그러나 속도는 더 느립니다. 입력 인수를 담기 위한 배열이 만들어지고, 입력 중 기본 타입이 있다면 박싱 및 언박싱 과정이 필요하기 때문입니다.
* 따라서 성능에 민감하지 않은 상황에서만 사용하는 것이 좋습니다.

### 해결법4: 해시코드를 지연 초기화하는 hashCode 메서드 작성
클래스가 불변이거나 해시코드를 계산하는 비용이 크다면 매번 계산하기보다는 캐싱하는 게 효율적일 수 있습니다. 지연 초기화를 통해 처음 `hashCode()`가 호출될 때 계산하는 식으로 진행할 수 있습니다.
```java
private int hashCode;

@Override
public int hashCode() {
    int result = hashCode;
    if (result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}
```
***

## 주의점 및 기타 정보
* 해시코드를 계산할 때 핵심 필드를 생략하면 해시 품질이 나빠질 수 있습니다. (ex: 이전에 `String` 클래스는 최대 16 문자만 뽑아내서 사용했으나, `URL`과 같은 계층적인 이름을 대량으로 주입할 시 중복되는 해시가 나올 수 있습니다. 때문에 속도가 `O(n)`으로 변할 수 있습니다.)
* 반대로 `equals` 메서드에서 사용되지 않는 나머지 필드들은 반드시 제외해야 합니다. `equals` 메서드에서 사용되지 않았는데 포함되었을 경우 원래의 equals 의도와 다른 결과가 나올 수 있기 때문입니다.
* `hashCode`가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 않는 것이 좋습니다. 이에 따라 클라이언트가 이 값에 의지하지 않게 되며, 계산 방식을 바꾸게 할 수 있습니다.
* 스프링의 경우에는 `@EqualsAndHashCode`가 그 역할을 대신합니다.

## 참고 사이트
<ul>
<li><a href="https://tecoble.techcourse.co.kr/post/2020-07-29-equals-and-hashCode/">우아한 테크코스 블로그</li>
<li><a href="https://jojoldu.tistory.com/134">티스토리 블로그</a></li>
<li><a href="https://velog.io/@indongcha/hashCode%EC%99%80-31">벨로그 - hashCode와 31</li>
</ul>
