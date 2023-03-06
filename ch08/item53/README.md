# 아이템53: 가변인수는 신중히 사용하라

가변인수(varargs) 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있는 메서드를 정의할 때 사용한다.

인수가 0개 이상인 경우엔 바로 가변인수를 사용하면 된다.

책에서의 예시는 필수 매개변수와 가변인수를 함께 쓰는 경우가 나오는데, min 메서드를 예로 들었다.

```java
class Example {

    public static int min(int firstArg, int... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs) {
            if (arg < min) {
                min = arg;
            }
        }
        return min;
    }
}
```

위의 예시에서는 적어도 1개의 인수가 필요하기 때문에 첫 번째 인수는 필수 매개변수로 선언하고, 나머지 인수는 가변인수로 선언했다.

성능을 위해서 인수의 수에 따라 다중정의를 이용하기도 한다. 실제 우리가 제일 많이 접할 수 있는 예시는 `List` 인터페이스의 `of` 메서드이다.

이녀석은 좀 특이한 점이 많다.

```java
public interface List<E> extends Collection<E> {

    static <E> List<E> of() {
        return ImmutableCollections.emptyList();
    }

    static <E> List<E> of(E e1) {
        return new ImmutableCollections.List12<>(e1);
    }

    static <E> List<E> of(E e1, E e2) {
        return new ImmutableCollections.List12<>(e1, e2);
    }

    static <E> List<E> of(E e1, E e2, E e3) {
        return new ImmutableCollections.ListN<>(e1, e2, e3);
    }

    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
            e6, e7, e8, e9, e10);
    }

    static <E> List<E> of(E... elements) {
        switch (elements.length) { // implicit null check of elements
            case 0:
                return ImmutableCollections.emptyList();
            case 1:
                return new ImmutableCollections.List12<>(elements[0]);
            case 2:
                return new ImmutableCollections.List12<>(elements[0], elements[1]);
            default:
                return new ImmutableCollections.ListN<>(elements);
        }
    }
}
```

사실 왜 이렇게 구현되어 있는지 모르고 그냥 이렇게 되어있는 것만 알고 있었는데, 이번 기회에 성능을 위해 이렇게 구현되어 있는 것을 알게 되었다.
