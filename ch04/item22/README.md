---

marp: true

---

# 아이템 22
# 인터페이스는 타입을 정의하는 용도로만 사용하라

---

# 상수 인터페이스

상수 인터페이스는 메서드 없이, 상수를 뜻하는 static final 필드로만 가득 찬 인터페이스를 말한다.

~~~java
public interface PhysicalConstants {
    // 아보가드로 수 (1/몰)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;

}
~~~

---

## 상수 인터페이스 안티패턴은 인터페이스를 잘못 사용한 예다.

* 상수 인터페이스를 구현하는 것은 내부 구현을 API로 노출하는 행위다.
* 상수들을 사용하지 않더라도 호환성을 위해 여전히 상수 인터페이스를 구현하고 있어야 한다.
* final이 아닌 클래스가 상수 인터페이스를 구현한다면 모든 하위 클래스의 이름공간이 그 인터페이스가 정의한 상수들로 오염된다.

---

## 상수 인터페이스

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdVUXOI%2FbtrX2W7lfSZ%2FJaCI5pjf1LkDee8v4CjIF0%2Fimg.png)

---

# 상수 공개하기

---

## 클래스나 인터페이스 자체에 추가하기

특정 클래스나 인터페이스와 강하게 연관된 상수라면 그 클래스나 인터페이스 자체에 추가해야한다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fopskq%2FbtrX2W7lkUE%2FQVKclbBxMKLUokpdBIKQ60%2Fimg.png)

---

## 열거타입

열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 만들어 공개하면 된다.(아이템 34)

~~~java
public enum Planet {
	MERCURY(3.302e+23,2.439e6),
	VENUS(4.869e+24,6.052e6),
	EARTH(5.975e+24, 6.378e6),
	MARS(6.419e+23,3.393e6),
	JUPITER(1.899e+27,7.149e7),
	SATURN(5.685e+26,6.027e7),
	URAUS(8.683e+25,2.556e7),
	NEPTUNE(1.024e+26,2.477e7);

	private final double mass;
	private final double radius;
	private final double surfaceGravity;
        // 생성자, getter, setter
  
	public double surfaceWeight(double mass) {
		return mass * surfaceGravity;
	}
}
~~~

---

## 유틸리티 클래스

인스턴스화할 수 없는 유틸리티 클래스에 담아 공개하자

~~~java
public interface PhysicalConstants {
    private PhysicalConstants() {} // 인스턴스화 방지

    // 아보가드로 수 (1/몰)
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    public static final double BOLTZMANN_CONSTANT = 1.380_648)52e-23;

    // 전자 질량 (kg)
    public static final double ELECTRON_MASS = 9.109_383_56e-31;

}
~~~

---

## 유틸리티 클래스

유틸리티 클래스의 상수를 빈번히 사용한다면 정적 임포트(static import)하여 클래스 이름은 생략할 수 있다.

~~~java
import static PhysicalConstants.*;

public class Object {
    double atoms(double mols){
        return AVOGADROS_NUMBER * mols;
    }
}
~~~

---

## 정리

인터페이스는 타입을 정의하는 용도로만 사용해야 한다. 상수 공개용 수단으로 사용하지 말자.