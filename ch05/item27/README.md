# Item 27 - 비검사 경고를 제거하라

비검사 경고는 `Unchecked warning`이다. 번역보다는 원문이 이해하기 나은 것 같아서 예시들의 원문을 첨부한다.

- `unchecked cast warnings` : 비검사 형변환 경고
- `unchecked method invocation warnings` : 비검사 메서드 호출 경고
- `unchecked parameterized vararg type warnings` : 비검사 매개변수화 가변인수 타입 경고
- `unchecked conversion warnings` : 비검사 변환 경고

제네릭 타입 추론을 활용하여 `<>` 다이아몬드 연산자를 사용하면 비검사 경고를 제거할 수 있다.

이러한 경고를 없애지 않으면 `ClassCastException`이 발생할 수 있다.

## 비검사(Unchecked)의 의미

`Unchecked`는 자바 컴파일러가 타입 안정성을 보장하기 위한 타입 정보가 충분하지 않다는 것을 의미한다.

예시들을 다루는 것은 크게 의미가 없는 것 같아 생략하겠습니다.

## 비검사 경고를 제거하는 방법

사실 책에서 다루는 내용은 문제를 해결하는 것이 아닌 경고를 숨기는 방법을 다루고 있다.

대부분 `Raw` 타입을 사용했을 때 발생하는 것을 확인할 수 있다.

타입 안정성을 확실할 수 있다면, `@SuppressWarnings("unchecked")` 어노테이션을 사용하여 경고를 숨길 수 있다.

주석으로 안전한 이유를 남겨두는 것이 좋다.
