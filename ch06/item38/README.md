---

marp: true

---

# 아이템38
# 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

---

# 타입 안전 열거 타입(jdk 1.5 이전)

열거 타입과 달리 확장할 수 있다.

하지만 열거 타입이 거의 모든 상황에서 우수하다.

~~~java
public class TypeSafe{
    private final String type;
    private TypeSafe(String type){
        this.type = type;
    }
    public String toString(){
        return type;
    }
    public static final TypeSafe ex1 = new TypeSafe("ex1");
    public static final TypeSafe ex2 = new TypeSafe("ex2");
}
~~~

---

# Enum 확장

* 대부분의 상황에서 열거 타입을 확장하는건 좋지 않다.

    * 확장된 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 성립하지 않는다.
    * 기반 타입과 확장된 타입들의 원소 모두 순회할 방법이 마땅치 않다.

* 확장성을 높이려면 고려할 요소가 늘어나 설계와 구현이 더 복잡해진다.
* 연산 코드에 대해서는 열거 타입을 확장하는데 어울린다.

---

# Enum 확장

* 열거 타입은 인터페이스를 구현할 수 있다.

~~~java
public interface Operation{
    double apply(double x, double y);
}
public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    ...

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }
}
~~~

---

# Enum 확장

* 열거 타입 BasicOperation은 확장할 수 없지만, Operation은 확장할 수 있다.
* Operation 인터페이스를 연산의 타입으로 사용하면 된다.

~~~java
public enum ExtendedOperation implements Operation {
    ...

    // Operation을 구현했기 때문에 기존 연산을 쓰던 곳이면 어디든 쓸 수 있다.
}
~~~

---

# 확장된 열거 타입 원소

* 타입 수준으로 타형성을 적용할 수 있다.

~~~java
// 한정적 타입 매개변수 사용
method(BasicOperation.class, x, y);
method(ExtendedOperation.class, x, y);

private static <T extends Enum<T> & Operation> void method(Class<T> opEnumType, double x, double y);
~~~

~~~java
// 한정적 와일드 카드 사용
method(Arrays.asList(BasicOperation.values()), x, y);
method(Arrays.asList(ExtendedOperation.values()), x, y);

private static void method(Collection<? extend Operation> opSet, double x, double y);
~~~

---

# EnumSet, EnumMap

* EnumSet과 EnumMap은 같은 열거 타입만 허용함
* BasicOperation과 ExtendedOperation 모두 Operation을 구현하지만, 서로 다른 열거 타입이기 때문에 EnumSet이나 EnumMap에 동시에 담을 수 없다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FJzsnL%2FbtrZKpmiEth%2FdFLtsUo6QmwOHKLIROwz31%2Fimg.png)

---

# 문제점

* 열거 타입끼리 구현을 상속할 수 없다.

    * 아무 상태에도 의존하지 않는 경우 디폴트 구현을 이용해 인터페이스에 추가하는 방법이 있다.
    * 공유하는 기능(위 예의 symbol)이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있다.
