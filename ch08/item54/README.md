---
marp: true
---

# 아이템54: null이 아닌, 빈 컬렉션이나 배열을 반환하라

---

## 목차

1. null을 반환하면 안되는 이유
2. null을 반환해야할 때?(개인적인 생각 + kotlin)

---

## null을 반환하면 안되는 이유
- null을 반환할 경우 클라이언트는 null을 처리하기 위한 별도의 코드를 작성해야 한다.
  ```java
  List<Cheese> cheeses = shop.getCheeses();
  if (cheeses != null && cheeses.contains(Cheese.STILTON)) {}
  ```
- 위와 같이 방어적 코드를 의무적으로 작성해야 하며, 이를 놓칠 경우 오류가 발생할 수 있다.
- 객체가 0일 가능성이 거의 없다면 심지어 수년 뒤에야 오류가 발생하기도 한다.

---

## null은 반환하면 안되는 이유
- 그럼에도 종종 빈 리스트나 배열을 반환하는 것이 성능상 안 좋다고 하는 경우가 있다.
- 하지만 정확한 분석을 통해 이 할당이 성능 저하의 직접적인 원인이 아닌 이상 성능차이는 신경 수준이 못 된다.
  ```java
  public List<Cheese> getCheeses() {
    return new ArrayList<>(cheesesInStock);  
  }
  ```
---

## null은 반환하면 안되는 이유
- 그리고 가능성은 크지 않지만 사용 패턴에 따라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어뜨리는 경우 똑같은 빈 '불변' 컬렉션을 반환하면 된다. 
  ```java
  public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty()
        ? Collections.emptyList()
        : new ArrayList<>(cheesesInStock); 
  }
  ```
---

## null은 반환하면 안되는 이유
- 길이가 0짜리 배열은 우리가 원하는 반환 타입을 알려주는 역할을 한다.
- 이 방식이 성능을 떨어뜨릴 것 같다면 길이가 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다.
  ```java
  private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

  public Cheese[] getCheeses() {
  return cheessesInStock.toArray(EMPTY_CHEESE_ARRAY);
  }
  ```
---

## null을 반환해야할 때?(개인적인 생각 + kotlin)
- 데이터의 집합(배열 혹은 리스트)를 기대하는 행위에서 null은 정상적인 반환 케이스로 보기 어렵다.  
  이 경우 예외를 던지는 것이 기본적으로 적절하며, **팀 컨벤션에 따라야할 경우**에만 null을 반환하는 게 좋다.
- 코틀린의 경우 더더욱 nullSafety를 보장하기 때문에 null을 사용하지 않아야 한다.
  


