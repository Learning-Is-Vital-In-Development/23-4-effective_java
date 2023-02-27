---

marp: true

---

# 아이템 43
# 람다보다는 메서드 참조를 사용하라

---

# 람다와 메서드 참조

* 람다가 익명 클래스보다 나은 점 중에서 가장 큰 특징은 간결함이다.
* 메서드 참조를 사용하면 람다보다 더 간결하게 만들 수 있다.

---

# 람다와 메서드 참조

~~~java
List<String> school = List.of("김00","심00","이00","박00","심00");
Map<String, Integer> nameToCount = new HashMap<>();
for (String key : school) {
    
    // 람다
    // nameToCount.merge(key, 1, (count, incr) -> count+incr);

    // 메서드 참조
    nameToCount.merge(key, 1, Integer::sum);
}

// {박00=1, 심00=2, 이00=1, 김00=1}
~~~

---

# 람다와 메서드 참조

* 람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다.
* 람다 대신 메서드 참조를 전달하면 똑같은 결과를 더 보기 좋게 얻을 수 있다.
* 매개변수 수가 늘어날수록 메서드 참조로 제거할 수 있는 코드양도 늘어난다.
* 매개변수의 이름이 프로그래머에게 좋은 가이드 되기도 하기 때문에 람다도 사용한다.

---

# 람다가 더 간결한 경우

* 메서드 참조가 람다보다 짧지 않고 명확하지 않으면 람다를 쓰는게 낫다.

~~~java
// 람다
service.execute(GoshThisClassNameIsHumongous::action);

// 메서드 참조
service.execute(()->action());
~~~

---

# 메서드 참조 유형

| No. | 메서드 참조 유형 | 예 | 같은 기능을 하는 람다 |
| :---: | :--- | :--- | :--- |
| 1 | 정적 | Integer:parseInt | str -> Integer.parseInt(str) |
| 2 | 한정적 (인스턴스) | Instant.now()::isAfter | Instant then = Instant.now(); <br>t -> then.isAfter(t) |
| 3 | 비한정적 (인스턴스) | String::toLowerCase | str -> str.toLowerCase() |
| 4 | 클래스 생성자 | TreeMap<K, V>::new | () -> new TreeMap<K, V>() |
| 5 | 배열 생정자 | int[]::new | len -> new int[len] |

---

# 메서드 참조 유형

1. 정적 참조
2. 한정적 참조
    * 수신 객체를 특정함
    * 함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 똑같다.
3. 비한정적 참조
    * 수신 객체를 특정하지 않는다. 적용하는 시점에 수신 객체를 알려준다.
    * 수신 객체 전달용 매개변수가 매개변수 목록의 첫 번째로 추가되며, 그 뒤로는 참조되는 메서드 선언에 정의된 매개변수들이 뒤따른다.
    * 스트림 파이프라인에서의 매핑과 필터 함수에 쓰인다.
4. 클래스 생성자
5. 배열 생성자
    * 생성자 참조는 팩터리 객체로 사용된다.


---

# 정리

* 메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.
