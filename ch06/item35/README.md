# 아이템 35 : ordinal 메서드 대신 인스턴스 필드를 사용하라

## ordinal 메서드 
`ordinal` 메서드는 자바의 열거 타입에서 특정 열거 타입 상수가 몇 번째 위치인지를 반환하는 데 사용됩니다.  
예시로 다음과 같이 `Nation` 열거 클래스가 있다고 가정하겠습니다.
```java
public enum Nation {

    KOREA, USA, JAPAN, CHINA, RUSSIA;

    public int getIndex() {
        return ordinal() + 1;
    }
}

Nation nation = Nation.JAPAN;
System.out.println(nation.getIndex()); // -> 2
```
위의 예시에서 `ordinal` 메서드를 사용할 경우, `JAPAN`이 위치한 인덱스인 2를 리턴합니다.
### ordinal을 직접 사용할 경우의 문제점
개발자가 직접 `ordinal` 메서드를 사용할 경우 다음과 같은 문제가 있습니다.

* 상수 간의 순서를 바꿀 경우 해당 상수가 가지고 있던 인덱스가 달라지기에 위험합니다.
* 사용 중인 인덱스를 가진 상수를 추가할 수 있는 방법이 없습니다. 한 인덱스에는 하나의 상수만 지정되기 때문입니다.
* 만약 값이 5개까지 있는데, 8번 인덱스에 특정 열거 타입 상수를 지정하려면 필요없는 더미 객체를 3개 추가해야 합니다. 이는 배열과 같은 원리로 생각하면 될 듯 합니다. (`int[] numbers = new int[4]`인데 `numbers[7]`을 얻을 수 없듯이)
### 해결법
해결법은 열거 타입 상수에 연결된 인덱스를 `ordinal` 메서드로 얻지 않으면 됩니다. 즉, `ordinal` 메서드 사용을 지양해야 합니다. 그 대신 인스턴스 필드에 직접 데이터를 넣어둘 수 있습니다.
```java
public enum Nation {

    KOREA(0), USA(1), JAPAN(1), CHINA(2), RUSSIA(3);

    private final int index;
    Nation(int idx) {
        this.index = idx;
    }
    public int getIndex() {
        return index;
    }
}

Nation usa = Nation.USA;
Nation japan = Nation.JAPAN;
System.out.println(usa.getIndex()); // --> 1
System.out.println(japan.getIndex()); // --> 1 (중복 가능)
```
## 여담
`ordinal` 메서드는 원래 `EnumSet`, `EnumMap`과 같은 열거 타입 기반 범용 자료구조에 쓸 목적으로 설계되었습니다. 아래 예시는 `EnumMap`에서의 `put` 메서드 로직입니다.
```java
public V put(K key, V value) {
    typeCheck(key);

    int index = key.ordinal(); // 내부 인덱스를 key의 ordinal로 사용
    Object oldValue = vals[index];
    vals[index] = maskNull(value);
    if (oldValue == null)
        size++;
    return unmaskNull(oldValue);
}
```
위의 예시와 같이, `EnumMap`에서의 `put` 메서드를 사용할 경우 `key`로 쓰이는 열거 타입의 `ordinal` 메서드를 인덱스로 사용하는 것을 확인할 수 있습니다.  
이렇게 내부 자료구조로 사용할 경우가 아닐 경우에는 직접적으로 메서드를 사용하지 않는 것이 좋습니다.
## 참고
* [[Java] EnumMap에 대한 관찰](https://velog.io/@kasania/Java-EnumMap)
