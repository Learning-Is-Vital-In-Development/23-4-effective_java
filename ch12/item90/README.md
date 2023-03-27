# ****아이템 90. 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라****

**Serializable을 구현하기로 결정한 순간,** 

언어의 생성자 이외의 방법으로도 인스턴스를 생성할 수 있게 된다.

- 버그 및 보안 문제가 일어날 가능성이 커진다.

직렬화 프록시 패턴 (Serialization Proxy Pattern)을 이용하면 이러한 위험을 크게 줄여줄 수 있다.

- 직렬화 프록시는 일반적으로 이전 아이템에서 나온 `readObject` 의 방어적 복사보다 강력하다.

## **직렬화 프록시 패턴**

바깥 클래스의 논리적 상태를 표현하는 중첩 클래스를 설계하여 `private static`으로 선언한다. 여기서 **중첩 클래스**가 **직렬화 프록시**다.

중첩 클래스의 생성자는 단 하나여야 하며, 

바깥 클래스를 매개변수로 받아야 한다. 

단순히 인수로 넘어온 인스턴스의 데이터를 복사한다. 

일관성 검사 또는 방어적 복사도 필요가 없다. 

다만 바깥 클래스와 직렬화 프록시 모두 `Serializable`을 구현해야 한다.

```java
class Period implements Serializable {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 2123123123;
        private final Date start;
        private final Date end;

        public SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        /**
         * Deserialize 할 때 호출된다.
         * 오브젝트를 생성한다.
         */
        private Object readResolve() {
            return new Period(start, end);
        }
    }

		// Serialize -> 프록시 인스턴스 반환
    // 결코 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없다
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // Period 자체의 역직렬화를 방지 -> 역직렬화 시도시, 에러 반환
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("프록시가 필요해요.");
    }
}
```

# **직렬화 프록시 패턴의 장점**

앞선 예제 코드에서 본 것처럼 멤버 필드를 `final`로 선언할 수 있기 때문에 진정한 불변으로 만들 수 있다. 또한 직렬화 프록시 패턴은 역직렬화한 인스턴스와 원래의 직렬화된 클래스가 달라도 정상적으로 동작한다.

대표적인 예로 `EnumSet`은 public 생성자 없이 정적 팩터리만 제공한다. 

원소 개수가 65개 이하면 `RegularEnumSet`을 사용하고 그보다 크면 `JumboEnumSet`을 사용한다.

그런데, 64개짜리 원소를 가진 `EnumSet`을 직렬화한 다음에 

원소 5개를 추가하고 역직렬화하면 어떻게 될까? 

처음엔 `RegularEnumSet` 이였다가

간단히 역직렬화할 때 `JumboEnumSet`으로 하면 된다. 

이게 가능한 이유는 `EnumSet`에는 직렬화 프록시 패턴이 적용되어 있기 때문이다.

```java
private static class SerializationProxy <E extends Enum<E>>
        implements java.io.Serializable
{
    /**
     * The element type of this enum set.
     *
     * @serial
     */
    private final Class<E> elementType;

    /**
     * The elements contained in this enum set.
     *
     * @serial
     */
    private final Enum<?>[] elements;

    SerializationProxy(EnumSet<E> set) {
        elementType = set.elementType;
        elements = set.toArray(ZERO_LENGTH_ENUM_ARRAY);
    }

    // instead of cast to E, we should perhaps use elementType.cast()
    // to avoid injection of forged stream, but it will slow the implementation
    @SuppressWarnings("unchecked")
    private Object readResolve() {
        EnumSet<E> result = EnumSet.noneOf(elementType);
        for (Enum<?> e : elements)
            result.add((E)e);
        return result;
    }

    private static final long serialVersionUID = 362491234563181265L;
}

Object writeReplace() {
    return new SerializationProxy<>(this);
}

// readObject method for the serialization proxy pattern
// See Effective Java, Second Ed., Item 78.
private void readObject(java.io.ObjectInputStream stream)
    throws java.io.InvalidObjectException {
    throw new java.io.InvalidObjectException("Proxy required");
}
```

## 직렬화 프록시 패턴의 한계

1. 클라이언트가 마음대로 확장할 수 있는 클래스에는 적용할 수 없다. 
2. 객체가 서로 참조하는 상황, 객체 그래프에 순환이 있는 클래스에는 적용할 수 없다.
3. 방어적 복사보다 상대적으로 속도도 느리다.
    - 책에서 말하길, `Period` 예제의 경우 방어적 복사를 사용하는 것보다 14% 느려졌다고 한다.
    

## 정리

확장할 수 없는 클래스라면 가능한 직렬화 프록시 패턴을 사용하자.