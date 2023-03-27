# 아이템 82: 스레드 안전성 수준을 문서화하라

## synchronized
자바에서 멀티 스레드에 안전하게 하기 위해서는 `synchronized` 키워드를 해당 메서드에 붙이면 된다는 말을 자주 들었을 것입니다.  
그러나 자바독이 기본 옵션에서 생성한 API 문서에는 `synchronized` 키워드가 선언될지는 구현 이슈로만 있을 뿐 API에 속하지 않습니다.  
따라서, 멀티 스레드 환경에서도 API를 안전하게 사용하려면 클래스가 지원하는 스레드 안전성 수준을 정확히 명시해야 합니다.  

## 스레드 안전성 수준
* 불변 (immutable): 해당 클래스의 인스턴스는 상수와 유사하게 취급됩니다. 즉, 외부 동기화가 필요 없습니다. (ex: `String`)
* 무조건적 스레드 안전 (unconditionally thread-safe): 해당 클래스의 인스턴스는 수정 가능하지만, 내부에서 충실히 동기화하기 때문에 동시에 접근하더라도 별도의 외부 동기화가 필요 없습니다. (ex: `ConcurrentHashMap`)
* 조건부 스레드 안전 (conditionally thread-safe): 무조건적 스레드 안전과 같으나, 일부 메서드는 동시에 사용하려면 외부 동기화 (`synchronized`)가 필요합니다. `Collections.synchronized`로 감싼 것들이 속하며, 여기에서의 반복자는 외부에서 동기화해야 합니다.
* 스레드 안전하지 않음 (not thread-safe): 해당 클래스의 인스턴스는 수정 가능하며, 동시에 사용하려면 각 메서드 호출을 클라이언트가 선택한 외부 동기화 메커니즘 (`Collections.synchronizedList` 등)으로 감싸야 합니다. (ex: `ArrayList`, `HashMap`)  
  * 스레드 안전하지 않은 클래스를 `Collections.synchronized`로 감싸면 조건부 스레드 안전으로 만들 수 있습니다. 
* 스레드 적대적 (thread-hostile): 모든 메서드 호출을 외부 동기화로 감싸더라도 멀티 스레드 환경에서 안전하지 않습니다. 정적 데이터를 아무 동기화 없이 수정하며, 우연히 만들어진 경우가 있습니다. 일반적으로 수정을 해 재배포하거나 deprecated API로 지정합니다.

스레드 적대적 상황을 제외하고는 `@Immutable`, `@ThreadSafe`, `@NotThreadSafe` 등과 같은 어노테이션을 붙일 수 있습니다.

## 조건부 스레드 안전 유의사항
조건부 스레드 안전은 일부 메서드가 동시에 사용하려면 외부 동기화가 필요한 경우라고 하였습니다.  
즉, 조건부 스레드 안전을 사용할 때는 어떤 순서로 호출할 때 외부 동기화가 필요한지, 그리고 그 순서로 호출하려면 어떤 락 혹은 드물게 락들을 얻어야 하는지 알려줘야 합니다.  
일반적으로 인스턴스 자체를 락으로 얻지만 예외도 있습니다. 아래는 `Collections.synchronizedMap`의 일부입니다.
```java
/*
Returns a synchronized (thread-safe) map backed by the specified map.
In order to guarantee serial access, it is critical that all access to the backing map is accomplished through the returned map.
It is imperative that the user manually synchronize on the returned map when traversing any of its collection views via Iterator, Spliterator or Stream:
*/
   Map m = Collections.synchronizedMap(new HashMap());
       ...
   Set s = m.keySet();  // Needn't be in synchronized block
       ...
   synchronized (m) {  // Synchronizing on m, not s!
       Iterator i = s.iterator(); // Must be in synchronized block
       while (i.hasNext())
           foo(i.next());
   }
  
```
* 클래스의 스레드 안전성은 보통 클래스의 문서화 주석에 기재합니다. 그러나 독특한 특성의 메서드라면 해당 메서드의 주석에 기재하는 것이 좋습니다.
* 위에서는 반환 타입 `Map`만으로는 명확히 알 수 없는 `Collections.synchronizedMap`을 사용했습니다. 이런 경우에는 자신이 반환하는 객체의 스레드 안전성을 위의 코드처럼 문서화해야 합니다.

## 비공개 락
* 클래스가 외부에서 사용할 수 있는 (public) 락을 제공하면, 클라이언트에서 일련의 메서드 호출을 원자적으로 수행할 수 있습니다. 그러나 이 경우에는 내부에서 처리하는 고성능 동시성 제어 메커니즘과 혼용할 수 없습니다.
  * `ConcurrentHashMap`은 내부적으로 세분화된 락을 사용해서, 병렬적인 작업을 수행할 수 있도록 설계되었습니다. 즉, 락을 획득하고 반환할 때 까지 다른 클라이언트가 대기해야 하는 방식과 충돌됩니다.
    * 아이템 81에서 언급한 것 처럼, 동시성 컬렉션은 높은 동시성에 도달하기 위해 동기화를 각자의 내부에서 수행합니다. 외부에서 락을 추가로 사용하면 오히려 속도가 느려집니다.
  * 또한, 클라이언트가 공개된 락을 오래 쥐고 놓지 않는 서비스 거부 공격 (denial-of-service attack)을 수행할 수도 있습니다.
    * 서비스 거부 공격은 주로 네트워크에서 여러 클라이언트가 동시다발적으로 서버에게 요청을 했을 때 이뤄지는 공격이지만, 한 클라이언트가 락을 계속 쥐고 있음으로써 다른 클라이언트들이 서버에 접근할 수 없도록 할 때도 사용되는 용어입니다. 
### 코드
다음은 비공개 락으로 변환함으로써 위의 문제점들을 방지할 수 있는 코드입니다.
```java
private final Object lock = new Object();

public void foo() {
    synchronized(lock) {
        ....
    }
}
```
* 비공개 락 객체는 클래스 바깥에서는 볼 수 없습니다. 따라서 클라이언트가 그 객체의 동기화에 관여할 수 없습니다.
* 락 객체를 `final`로 선언해야 우연히 락 객체가 교체되는 일을 예방해줍니다.
* 비공개 락 객체 관용구는 무조건적 스레드 안전 클래스에서만 사용할 수 있습니다. 조건부 스레드 안전 클래스에서는 특정 호출 순서에 필요한 락이 무엇인지를 클라이언트에게 알려줘야 하기 때문입니다.
