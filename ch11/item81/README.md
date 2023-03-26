# 아이템 81: wait과 notify보다는 동시성 유틸리티를 애용하라
## wait과 notify 지양하기
`wait`과 `notify`는 자바에서 스레드 간 통신을 위해 사용되는 메서드입니다. 그러나 올바르게 사용하기가 까다롭고, 지금은 고수준의 동시성 유틸리티가 충분히 대신 처리해줄 수 있기 때문입니다.  
즉, `wait`과 `notify`를 직접 사용하기보다는 고수준 동시성 유틸리티를 사용하는 것이 좋습니다.

## 고수준 동시성 유틸리티
자바에서의 고수준 동시성 유틸리티를 사용하는 방법 중에서는 `java.util.concurrent`를 사용하는 방식이 있습니다. 종류는 다음과 같습니다.
* 실행자 프레임워크 - 아이템 80의 내용입니다.
* 동시성 컬렉션 (`concurrent collection`)
* 동기화 장치 (`synchronizer`)

### 동시성 컬렉션
동시성 컬렉션은 `List`, `Map` 등과 같은 표준 컬렉션 인터페이스에 동시성을 추가한 고성능 컬렉션입니다.
* 높은 동시성에 도달하기 위해 동기화를 각자의 내부에서 수행합니다.
* 그렇기 때문에 동시성을 무력화하는 것은 불가능하며, 외부에서 락을 추가로 사용하면 오히려 속도가 느려집니다.
* 이를 개선하고자, 여러 기본 동작을 하나의 원자적 동작으로 묶는 '상태 의존적 수정' 메서드들이 추가되었습니다. 자바 8에서는 일반 컬렉션 인터페이스에도 디폴트 메서드 형태로 추가되었습니다.
예시로 `Map`의 `putIfAbsent(key, value)` 메서드를 보겠습니다.
```java
private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

public static String intern(String s) {
  String previousValue = map.putIfAbsent(s, s);
  return previousValue == null ? s : previousValue;
}
```
* 주어진 키에 매핑된 값이 없을 때만 새 값을 집어넣습니다.
* 기존 값이 있었다면 해당 값을 반환하고, 없었다면 `null`을 반환합니다.
* 덕분에 스레드 안전한 정규화 맵을 쉽게 구현할 수 있습니다.
`ConcurrentHashMap`은 `get`과 같은 검색 기능에 최적화되어 있기 때문에, `get`을 먼저 호출하여 필요할 때만 `putIfAbsent`를 호출하면 더 빠릅니다.
```java
public static String intern(String s) {
    String result = map.get(s);
    if (result == null) {
        result = map.putIfAbsent(s, s);
        if (result == null)
            result = s;
    }
    return result;
}
```
* 동시성 컬렉션은 동기화된 컬렉션보다 매우 성능이 좋습니다. `Collections.synchronizedMap`보다 `ConcurrentHashMap`을 사용하는 것이 좋습니다.
## 동기화 장치
동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 하여, 서로 작업을 조율할 수 있도록 해 줍니다. 대표적인 동기화 장치는 `CountDownLatch`와 `Semaphore`가 있습니다. 가장 강력한 동기화 장치는 `Phaser`입니다.
```java
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
    // executor: 동작들을 실행할 실행자
    // concurrency: 동작을 몇 개나 동시에 수행할 수 있는지를 뜻하는 동시성 수준
  
    CountDownLatch ready = new CountDownLatch(concurrency); // 이 생성자의 인자값이 래치의 countDown 메서드를 몇 번 호출해야 대기 중인 스레드들을 깨우는지 결정한다.
    // ready는 작업자 스레드들이 준비가 완료됐음을 타이머 스레드에 통지할 때 사용한다.
  
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(concurrency);
  
    for (int i = 0; i < concurrency; i++) {
        executor.execute(() -> {
          // 타이머에게 준비를 마쳤음을 알린다.
          ready.countDown(); // 이후 타이머 스레드가 시작 시각을 기록한다.
          try {
              // 모든 작업자 스레드가 준비될 때까지 기다린다.
              start.await(); // 기다리던 작업자 스레드들을 깨운다. 그 직후 타이머 스레드는 세 번째 래치인 done이 열리기를 기다린다.
              action.run();
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt(); // InterruptedException을 캐치한 스레드는 Thread.currentThread().interrupt() 관용구를 사용해 인터럽트를 되살리고 자신은 run 메서드에서 빠져나온다.
          } finally {
              // 타이머에게 작업을 마쳤음을 알린다. 타이머 스레드는 done 래치가 열리자마자 깨어나 종료 시각을 기록한다.
              done.countDown();
          }
        });
    }
  
    ready.await(); // 모든 작업자가 준비될 때까지 기다린다.
    long startNanos = System.nanoTime();
    start.countDown(); // 작업자들을 깨운다.
    done.await(); // 모든 작업자가 일을 끝마치기를 기다린다.
    return System.nanoTime() - startNanos;
}
```
* `CountDownLatch`는 하나 이상의 스레드가 또 다른 하나 이상의 스레드 작업이 끝날 때 까지 기다리게 합니다.
* 시간 간격을 잴 때는 `System.currentTimeMillis`가 아니라 `System.nanoTime`을 사용하는 것이 더 좋습니다. 더 정확하고 정밀하며 시스템의 실시간 시간 보정에 영향을 받지 않습니다.
## 레거시 코드
`wait`메서드를 사용하는 표준 방식은 이렇습니다.
```java
synchronized (obj) {
    while (<조건이 충족되지 않았다>) // 이 반복문은 wait 호출 전후로 조건이 만족하는지를 검사하는 역할을 한다.
        obj.wait(); // 락을 놓고, 깨어나면 다시 잡는다.
    ... // 조건이 충족됐을 때의 동작을 수행한다.
}
```
* `wait` 메서드는 스레드가 어떤 조건이 충족되기를 기다리게 할 때 사용합니다.
* 락 객체의 `wait` 메서드는 반드시 그 객체를 잠근 동기화 영역 안에서 호출해야 합니다.
* `wait`메서드를 사용할 때는 반드시 대기 반복문(wait loop) 관용구를 사용해야 합니다.
* 대기 전에 조건을 검사하여 조건이 이미 충족되었다면 `wait`를 건너뛰게 하는 것은 응답 불가 상태를 예방하는 조치입니다.
* 대기 후에 조건을 검사하여 조건이 충족되지 않았다면 다시 대기하게 하는 것은 안전 실패를 막는 조치입니다.
## 그 외
* `notify`와 `notifyAll` 중에서는 `notifyAll`을 사용하는 것이 좋습니다. 깨어나야 하는 모든 스레드가 깨어남을 보장할 것이기 때문입니다.
* 모든 스레드가 같은 조건을 기다리고, 조건이 한 번 충족될 때마다 단 하나의 스레드만 혜택을 받을 수 있다면 `notify`를 사용하여 최적화할 수 있습니다.
* `notify` 대신 `notifyAll`을 사용하면 관련 없는 스레드가 실수로 혹은 악의적으로 `wait`를 호출하는 공격으로부터 보호할 수 있습니다.
