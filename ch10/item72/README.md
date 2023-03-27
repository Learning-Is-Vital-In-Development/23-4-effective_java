---

marp: true

---

# 아이템 72
# 표준 예외를 사용하라

---

# 표준 예외

* 예외를 재사용하는 것이 좋다.
* 자바 라이브러리는 대부분의 API에서 쓰기에 충분한 수의 예외를 제공한다.

---

# 표준 예외 사용

* 표준 예외를 사용하면 얻는 것이 많다
    * 다른 프로그래머들이 익히고 사용하기 쉬운 API를 만들 수 있다.
    * 가독성이 좋아진다.
    * 예외 클래스 수가 적으면 메모리 사용량도 줄고 클래스 적재하는 시간도 적게 걸린다.

---

# 대표적인 표준 예외

* IllegalArgumentException : 허용하지 않는 값이 인수로 건네졌을 때
* IllegalStateException : 객체가 메서드를 수행하기에 적절하지 않은 상태일 때
* NullPointerException : null을 허용하지 않는 메서드에 null을 건넸을 때
* IndexOutOfBoundsException : 인덱스가 범위를 넘어섰을 때
* ConcurrentModificationException : 허용하지 않는 동시 수정이 발견됐을 때
* UnsupportedOperationException : 호출한 메서드를 지원하지 않을 때

---

# 직접사용하면 안되는 예외

* Exception, RuntimeException, Throwable, Error는 직접 재사용하지 말자
* 위 클래스들을 추상 클래스라고 생각해야 한다.
* 여러 예외들을 포괄하는 클래스이므로 안정적으로 테스트하기 어렵다.

---

# 직렬화

* 더 많은 정보를 제공하길 원한다면 표준 예외를 확장해도 좋다.
* 단, 예외는 직렬화할 수 있다. 따라서 비용이 많이 든다.
    * 커스텀 예외를 새로 만들지 않아야 할 근거로 충분할 수 있다.

![](https://user-images.githubusercontent.com/46641538/157819700-8e7baf31-9102-4ada-a30a-089eab93b733.png)
