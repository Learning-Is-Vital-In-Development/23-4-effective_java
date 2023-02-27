---
marp: true
---

# 아이템44: 표준 함수형 인터페이스를 사용하라

---

## 목차

1. 표준 함수형 인터페이스 종류
2. 직접 함수형 인터페이스를 구현해야할 때와 주의사항

---

## 표준 함수형 인터페이스 종류
- 표준 함수형 인터페이스는 `java.util.function` 패키지에 총 43개 인터페이스가 정의되어 있다.
- 모든 걸 다 외울 필요 없이 6개의 기본 인터페이스 타입에 대해서 이해하고 이를 이용해 나머지 유추 가능하다.

---

### 1. Function
- T 타입을 받아 R 타입을 반환한다.
  ![Function](https://user-images.githubusercontent.com/61923768/221193852-6cc04e48-ccd5-44cc-aa64-6289266d5920.png)

---

### 2. Predicate
- T라는 특정 타입을 파라미터로 받아 boolean 타입을 반환한다.
  ![Predicate](https://user-images.githubusercontent.com/61923768/221194513-a77bcca1-c5a2-48ac-bc47-6919cc9072eb.png)

---

### 3. Consumer
- T 타입을 받고 반환하지 않는다.
  ![Consumer](https://user-images.githubusercontent.com/61923768/221194748-9d2e628c-7369-47e7-8519-93d0bc451bea.png)

---

### 4. Supplier
- 파라미터 없이 T 타입을 반환한다.
  ![Supplier](https://user-images.githubusercontent.com/61923768/221195025-ba6c4445-6904-4d30-bb87-52796a2a87f5.png)

---

### 5. UnaryOperator
- T 타입을 받고 T 타입을 반환한다.
  ![UnaryOperator](https://user-images.githubusercontent.com/61923768/221192789-a7c395f5-c4b5-4447-b685-f56e9c9b8b3e.png)

---

### 6. BinaryOperator
- <T, T> 2개의 타입을 받고 T 타입을 반환한다.
  ![BinaryOperator](https://user-images.githubusercontent.com/61923768/221563533-77538c6d-f61d-4d3b-93e2-da0af14a253c.png)

---

### 7~24. 기본 타입(int, long, double) 활용
- Class name에 Prefix로 `타입`을 붙여준다.  
  ex) IntConsumer: int 타입을 받아서 반환하지 않는다.
  ![IntConsumer](https://user-images.githubusercontent.com/61923768/221198626-fb8ee2ae-6ec1-4620-8f3d-aeb9f81e777e.png)

---

### 25~27. 기본 타입(int, long, double) 활용: ToResultFunction
- Class name에 Prefix로 `To타입`을 붙여준다.  
  ex) ToIntFunction: T 타입을 받아 int 타입을 반환한다.
  ![ToIntFunction](https://user-images.githubusercontent.com/61923768/221203415-15720dd1-782a-447c-966e-1d6af498c524.png)

---

### 28~33. 기본 타입(int, long, double) 활용: SrcToResultFunction
- Class name에 Prefix로 `타입To타입`을 붙여준다.  
  ex) IntToLongFunction: int 타입을 받아 long 타입을 반환한다.
  ![IntToLongFunction](https://user-images.githubusercontent.com/61923768/221204006-57c2112e-7318-4088-b75e-86362c399614.png)

---

### 34~42. 입력 파라미터 타입을 2개로 확장
- ClassName에 Prefix로 `Bi`를 붙여준다.  
- BiPredicate: <T, U> 2개의 타입을 받아 boolean 타입을 반환한다.
  ![BiPredicate](https://user-images.githubusercontent.com/61923768/221196958-d45c3395-cda6-449b-95ca-bb9f87387bff.png)
    
---
### 34~42. 입력 파라미터 타입을 2개로 확장
- BiConsumer: <T, U> 2개의 타입을 받아 반환하지 않는다.
  ![BiConsumer](https://user-images.githubusercontent.com/61923768/221196801-0b08051e-cae9-46c3-8ded-1c4abf70a153.png)

---

### 34~42. 입력 파라미터 타입을 2개로 확장
- BiConsumer + 기본타입: <T, 기본타입(Int, Long, Double)> 2개의 타입을 받아 반환하지 않는다.
  ![ObjConsumerList](https://user-images.githubusercontent.com/61923768/221201219-f4437c78-dab5-4842-bbba-a2f89e5bdb30.png)
- 
---

### 34~42. 입력 파라미터 타입을 2개로 확장
- BiConsumer + 기본타입: <T, 기본타입(Int, Long, Double)> 2개의 타입을 받아 반환하지 않는다.
  ![ObjDoubleConsumer](https://user-images.githubusercontent.com/61923768/221200842-afe9d3af-ea12-43e1-b2d7-791bc89567b4.png)
    
---

### 34~42. 입력 파라미터 타입을 2개로 확장
- BiFunction: <T, U> 2개의 타입을 받아 R 타입을 반환한다.
  ![image](https://user-images.githubusercontent.com/61923768/221201656-846360e1-d53f-4664-82a8-2cc10ba04d78.png)
    
---

### 34~42. 입력 파라미터 타입을 2개로 확장
- BiFunction + 기본타입: <T, U> 2개의 타입을 받아 기본 타입(Int, Long, Double)을 반환한다.
  ex) ToDoubleBiFunction: <T, U> 2개의 타입을 받아 Double을 반환한다.
  ![ToDoubleBiFunction](https://user-images.githubusercontent.com/61923768/221202604-bb5b54c5-c453-4edc-b39e-c1b805390d9c.png)

---

### 43. BooleanSupplier
- 파라미터 없이 Boolean 반환한다.
  ![BooleanSupplier](https://user-images.githubusercontent.com/61923768/221202973-e255dd61-ea98-4a7f-92ab-d61a2645224b.png)

---

## 직접 함수형 인터페이스를 구현해야할 때와 주의사항
- 직접 함수형 인터페이스를 구현해야할 때
  1. 기본으로 제공하는 함수형 인터페이스 중 적절한 것이 없는 경우 
  (ex 파라미터 타입이 3개 필요 등)
  2. 아래와 같은 조건에 하나 이상 만족하는 경우(고민 필요)
     1. 자주 사용되며, 그 이름이 용도를 훌륭히 설명할 때
     2. 구현하는 쪽에서 반드시 지켜야할 규약을 담고 있을 때
     3. 유용한 디폴트 메서드들을 필요할 때

---

## 직접 함수형 인터페이스를 구현해야할 때와 주의사항
- 직접 구현해야하는 함수형 인터페이스의 예시: Comparator<T>
  ![Comparator](https://user-images.githubusercontent.com/61923768/221205803-3ee4cb18-a449-4c08-b8d7-172b7129e75a.png)
- 사실상 `ToIntBiFunction`과 동일하지만 직접 구현해야한다.

---

## 직접 함수형 인터페이스를 구현해야할 때와 주의사항
- 직접 구현할 때 주의사항
  1. 함수형 인터페이스도 인터페이스이다. 아주 주의해서 설계하라.
  2. 항상 `@FunctionalInterface` 애너테이션을 사용하자.
     1. 람다용으로 설계된 것이라고 알려줄 수 있다.
     2. 추상 메서드가 오직 하나만 가지고 있어야 컴파일 되도록 해준다.
     3. 누군가의 실수로 메서드를 추가하지 못하도록 막아준다.
- 서로 다른 함수형 인터페이스를 인자를 받는 다중 정의는 피하라!
