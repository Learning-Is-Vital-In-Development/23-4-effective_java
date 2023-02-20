# 아이템 37: ordinal 인덱싱 대신 EnumMap을 사용하라

Enum 타입의 인스턴스는 서로 구분되는 고유한 값으로 자동으로 할당된다. 이러한 값은 `ordinal()`메서드를 통해 가져올 수 있으며, 0부터 시작하여 순차적으로 증가하는 값을 반환한다.

`ordinal()` 값은 Enum 타입에 새로운 상수가 추가되거나 순서가 변경될 때마다 변경되어 배열과 같은 인덱스를 사용하는 코드에서 예기치 않은 문제를 발생시킬 수 있다.

또한 `ordinal()` 값을 인덱스로 사용하면 배열을 사용하는 코드에서는 인덱스가 음수이거나 값이 너무 큰 경우 `ArrayIndexOutOfBoundsException`이 발생한다.

이러한 문제를 피하기 위해 `EnumMap`을 사용하는 것이 좋은데, `EnumMap`은 `Enum` 타입의 상수를 키로 사용하는 맵이다.배열 대신 EnumMap을 사용하여 인덱싱 할 수 있으며, Enum 타입에 새로운 상수를 추가하거나 순서를 변경하여도 코드를 변경할 필요가 없다.

`EnumMap`은 배열과 마찬가지로 상수에 대응하는 값에 빠르게 액세스 할 수 있으며, `EnumMap`의 내부 구현은 상수 순서에 대한 전용 배열을 사용하여 빠른 속도를 제공한다. 내부적으로 해싱을 이용하고 있다.

```java
public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements java.io.Serializable, Cloneable {

    /**
     * The {@code Class} object for the enum type of all the keys of this map.
     */
    private final Class<K> keyType;

    /**
     * All of the values comprising K.  (Cached for performance.)
     */
    private transient K[] keyUniverse;

    /**
     * Array representation of this map.  The ith element is the value
     * to which universe[i] is currently mapped, or null if it isn't
     * mapped to anything, or NULL if it's mapped to null.
     */
    private transient Object[] vals;
}
```

또한, EnumMap을 사용하면 Map 인터페이스를 활용하여 다양한 기능을 사용할 수 있으므로, 코드의 유연성과 가독성이 증가합니다. 스트림, 람다, 메서드 참조등을 모두 사용할 수 있다.

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);
        
        private final Phase from;
        private final Phase to;
        
        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화합니다.
        // 이전 상태에서 '이후 상태에서 전이로의 맵'에 대응 시키는 맵
        private static final Map<Phase, Map<Phase, Transition>> m = Stream.of(values())
            // 전이를 이전 상태를 기준으로 묶는 `groupingBy`
            .collect(groupingBy(t -> t.from, () -> new EnumMap<>(Phase.class),
                // 이후 상태를 전이에 대응 시키는 `toMap`
                toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));
        
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```

새로운 상태인 PLASMA를 추가해보자

```java
public enum Phase {
    // 상태목록
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        // 전이 목록
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
        
        private final Phase from;
        private final Phase to;
        
        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화합니다.
        // 이전 상태에서 '이후 상태에서 전이로의 맵'에 대응 시키는 맵
        private static final Map<Phase, Map<Phase, Transition>> m = Stream.of(values())
            // 전이를 이전 상태를 기준으로 묶는 `groupingBy`
            .collect(groupingBy(t -> t.from, () -> new EnumMap<>(Phase.class),
                // 이후 상태를 전이에 대응 시키는 `toMap`
                toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));
        
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```
상태 목록과 전이 목록만을 수정하면 되는 것을 알 수 있다.
