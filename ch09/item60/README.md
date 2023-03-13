## ****아이템 60. 정확한 답이 필요하다면 float와 double은 피하라****

- 정확한 답이 필요한 계산에는 float나 double을 피하라
- 소수점 추적은 시스템에 맡기고, 코딩 시 불편함이나 성능 저하에 신경을 쓰지 않겠다면 BigDecimal을 사용하라
- 반면 성능이 중요하고 소수점을 직접 추가할 수 있고 숫자가 너무 크지 않으면 int나 long 을 사용하라.

## **float 와 double 의 문제점**

- float와 double 타입은 과학과 공학용 계산용이였음
- **넓은 범위의 수를 빠르게 정밀한 근사치 계산**
- 따라서 float와 double은 정확한 계산, 특히 금융과 관련된 계산과 적합하지 않다.

```java
@Test
void doubleTest (){
    double result = 1.39 - 0.23;
    System.out.println(result); // 1.16
    assertThat(result).isNotEqualTo(1.16);
}
```

![Untitled](https://user-images.githubusercontent.com/55054505/224598641-a9603f1e-908f-4b7a-ae1f-1b7795bea84c.png)


float과 double은 **부동소수점 방식**을 사용하고 있기 때문이다.

### 고정 소수점(fixed point) 방식

실수는 보통 `정수부`와 `소수부`로 나눌 수 있습니다.

따라서 실수를 표현하는 가장 간단한 방식은 소수부의 자릿수를 미리 정하여, 고정된 자릿수의 소수를 표현하는 것이다.

![12](https://user-images.githubusercontent.com/55054505/224598535-fccdf53c-09c5-40f2-8cdc-bbfe705c109a.png)

하지만 이 방식은 정수부와 소수부의 자릿수가 크지 않으므로, 표현할 수 있는 범위가 매우 적다

### 부동 소수점 방식

실수는 보통 정수부와 소수부로 나누지만, 가수부와 지수부로 나누어 표현할 수도 있다.

부동 소수점 방식은 하나의 실수를 `가수부`와 `지수부`로 나누어 표현하는 방식이다.

![133](https://user-images.githubusercontent.com/55054505/224598540-25a20988-3e7b-40ac-9d26-cc636d9b9762.png)

![144](https://user-images.githubusercontent.com/55054505/224598541-d51cdcd0-10c5-4fb4-8d1e-40e0b8c4b402.png)


### 부동 소수점 방식의 오차

부동 소수점 방식을 사용하면 고정 소수점 방식보다 훨씬 더 많은 범위까지 표현할 수 있다.

하지만 부동 소수점 방식에 의한 실수의 표현은 항상 오차가 존재한다는 단점을 가지고 있다.

$$
±(1.가수부)×2^{지수부-127}
$$

부동 소수점 방식에서의 오차는 공식에 의해 발생한다.

이 공식을 사용하면 표현할 수 있는 범위는 늘어나지만, 10진수를 정확하게 표현할 수는 없게 된다.

## 해결방법

**1)  BigDecimal**

```java
@Test
    void bigDecimalTest(){
        BigDecimal result = new BigDecimal("1.39").subtract(new BigDecimal("0.23"));
        System.out.println(result); // 1.16
        assertThat(result).isEqualTo(new BigDecimal("1.16"));
    }
```

![155](https://user-images.githubusercontent.com/55054505/224598529-ceb29286-0fb1-4da6-8649-82f6a6c9c427.png)

- 장점
    - 범위가 크다
    - 소수점을 직접 관리하지 않아도 된다
- 단점
    - 기본 타입에 비해 쓰기가 불편하다
    - 훨씬 느리다

**2) int 혹은 long**

- 장점
    - 사용하기가 간편하다
    - 성능이 뛰어나다
- 단점
    - BigDecimal에 비해 범위가 좁다
    - 소수점을 직접 관리해야 한다
    

**3) 결론**

- 소수점 추적을 시스템에 맡긴다
- 코딩 시의 불편함이나 성능 저하를 감수한다
- 숫자가 너무 크다 (8자리 10진수 이상인 경우)

→ BigDecimal

- 소수점을 직접 추적할 수 있다
- 성능이 중요하다
- 숫자가 너무 크지 않다

→ int나 long 사용