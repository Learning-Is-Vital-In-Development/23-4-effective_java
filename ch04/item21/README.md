---
marp : true
---
# Item 21: 인터페이스는 구현하는 쪽을 생각해 설계하라

## 유도진
---
## 목차
- 인터페이스는 구현하는 쪽을 생각해 설계하라
- 저자가 하고 싶은 진짜 이야기는?
---
## 인터페이스는 구현하는 쪽을 생각해 설계하라

- 생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어려운 법이다.
- 기존 인터페이스에 디폴트 메서드로 새 메서드를 추가하는 일은 꼭 필요한 경우가 아니라면 피해야 한다.
- 인터페이스를 설계할 때는 여전히 세심한 주의를 기울여야 한다.
- 인터페이스를 릴리스한 후라도 결함을 수정하는 게 가능한 경우도 있겠지만, 절대 그 가능성에 기대서는 안된다.

---
## 인터페이스는 구현하는 쪽을 생각해 설계하라

- `removeIf` 메서드
    ```java
    interface Collection {
        default boolean removeIf(Predicate<? super E> filter) {
            Objects.requireNonNull(filter);
            boolean removed = false;
            final Iterator<E> each = iterator();
            while (each.hasNext()) {
                if (filter.test(each.next())) {
                    each.remove();
                    removed = true;
                }
            }
            return removed;
        }
    }
    ```
  
---
## 인터페이스는 구현하는 쪽을 생각해 설계하라

- `Collections.synchronizedCollection` 클래스
    ```java
    public Collections {
        static class SynchronizedCollection<E> implements Collection<E>, Serializable {
            @SuppressWarnings("serial") // Conditionally serializable
            final Collection<E> c;  // Backing Collection

            @SuppressWarnings("serial") // Conditionally serializable
            final Object mutex;     // Object on which to synchronize

            @Override
            public boolean removeIf(Predicate<? super E> filter) {
                synchronized (mutex) {return c.removeIf(filter);}
            }
        }
    }
    ```
- `org.apache.commons.collections4.collections.SynchronizedCollection`은 재정의 되어 있지 않다.
---
## 저자가 하고 싶은 진짜 이야기는?
- 인터페이스를 구현체 변경 없이 수정할 수 있는 방법(deafult method)가 생겼지만 가능하면 수정하지 않아야 한다.
- 만약 새로운 무언가가 필요하다면, 새로운 인터페이스를 고려해보라.
- 인터페이스를 수정했다면 꼭 구현체들에 대해서 테스트를 하라.