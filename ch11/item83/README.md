# 아이템 83: 지연 초기화는 신중히 사용하라

## 지연 초기화 (Lazy initialization)
지연 초기화 방법은 필드의 초기화 시점을 그 값이 처음 필요할 때 까지 늦추는 기법입니다.
* 정적 필드와 인스턴스 필드 모두에 사용할 수 있습니다.
* 주로 최적화 용도로 사용됩니다.
  * 최적화는 성능 향상을 목적으로 시스템을 개선하는 것을 의미합니다.
* 클래스와 인스턴스 초기화 때 발생하는 순환 문제를 해결하는 효과도 있습니다.

### 지연 초기화의 주의점
* 클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만, 지연 초기화하는 필드에 접근하는 비용은 커집니다.
* 지연 초기화하려는 필드들 중 초기화가 이뤄지는 비율에 따라, 실제 초기화에 드는 비용에 따라, 초기화된 각 필드를 얼마나 빈번히 호출하느냐에 따라 지연 초기화가 실제로는 성능을 저하시킬 수도 있습니다.
* 멀티 스레드 환경에서는 지연 초기화를 하기 까다롭습니다. 해당 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화해야 합니다.
  * 동기화를 적용하지 않을 경우에는 여러 스레드에서 동시에 접근할 경우 초기화에서의 순환 문제가 발생할 수 있습니다.

### 지연 초기화가 적절한 경우
* 해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면 해당 필드를 초기화하는 비용이 크다면 지연 초기화가 제 역할을 할 수 있습니다.
  * 정말 그렇게 되고 있는지는 적용 전후의 성능을 측정해봐야 합니다.

## 인스턴스 필드 초기화 방법 - synchronized 붙이기
대부분의 상황에서는 일반적인 초기화가 지연 초기화보다 더 낫습니다.  
우선, 일반적인 인스턴스 필드를 초기화하는 방법을 보겠습니다. (`final` 사용)
```java
private final FieldType field = computeFieldValue();
```
그런데, 위의 [지연 초기화의 주의점]에서 "멀티 스레드 환경에서는 지연 초기화를 하기 까다롭고, 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화해야 한다"고 하였습니다.  
즉, 지연 초기화의 단점을 극복하려면 `synchronized` 키워드를 붙여서 선언하면 됩니다.
```java
private FieldType field;

private synchronized FieldType getField() {

    if (field == null)
        field = computeFieldValue();
    return field;
}
```

## 정적 필드 지연 초기화 방법 - 지연 초기화 홀더 클래스
정적 필드에서는 동기화를 사용하지 않고도 멀티 스레드 환경에서 안전하도록 만들 수 있습니다. 이는 클래스가 처음 쓰일 때 초기화된다는 특성을 이용한 방식입니다.
```java
private static class FieldHolder {
    static final FieldType field = computeFieldValue();
}
private static FieldType getField() { return FieldHolder.field; }
```
* 위 코드는 처음 `getField()` 메서드가 호출되면, `FieldHolder.field`가 읽히면서 `FieldHolder` 클래스 초기화를 촉발시키게끔 합니다. `getField()`가 필드에 접근하면서 동기화를 하지 않으니 성능이 느려지지 않습니다.
* 이 방식은 일반적인 VM이 오직 클래스를 초기화 할 때만 필드 접근을 초기화하고, 초기화가 끝난 이후에는 VM이 동기화 코드를 제거하여 그 다음부터는 아무런 검사나 동기화 없이 필드에 접근하는 것을 이용한 것입니다.

## 인스턴스 필드 초기화 방법 - 이중 검사 (double-check)
```java
private volatile FieldType field;

private FieldType getField() {

    FieldType result = field;
    if (result != null) // 첫 번쩨 검사 (락 사용 안 함)
        return result;
    synchronized(this) {
        if (field == null) // 두 번째 검사 (락 사용)
            field = computeFieldValue();
        return field;
    }
}
```
* 초기화된 필드에 접근할 때 동기화 비용을 없애줍니다.
* 첫 번째로 동기화 없이 검사하고, 두 번째로 동기화하여 검사합니다.
* 두 번째 검사에서도 필드가 초기화되지 않았을 때만 필드를 초기화합니다.
* 필드가 초기화 된 후로는 동기화하지 않으므로 해당 필드는 `volatile`로 선언해야 합니다.
* 정적 필드에도 적용할 수는 있지만, 지연 초기화 홀더 클래스 방식이 더 좋습니다.
* `result` 지역 변수는 필드가 이미 초기화된 상황에서는 그 필드를 딱 한 번만 읽도록 보장하는 역할을 합니다. 반드시 필요한 것은 아니지만 성능을 높여주고, 더 우아한 방법입니다.

## 인스턴스 필드 초기화 방법 - 단일 검사 (single-check)
가끔 반복해서 초기화해도 상관없는 인스턴스 필드를 지연 초기화할 때가 있습니다. 그런 경우에는 이중 검사에서 두 번째 검사를 생략할 수 있습니다.
```java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result == null)
        field = result = computeFieldValue();
    return result;
}
```
* 모든 스레드가 필드의 값을 다시 계산해도 상관없고 필드의 타입이 `long`과 `double`을 제외한 기본 타입이면 단일 검사의 필드 선언에서 `volatile` 한정자를 없애도 됩니다.
  * 어떤 환경에서는 필드 접근 속도를 높여주지만, 초기화가 스레드당 최대 한 번 더 이뤄질 수 있습니다. 보통은 사용하지 않습니다.
  * `volatile` 한정자는 변수를 메인 메모리에 저장하겠다는 것을 의미합니다.
