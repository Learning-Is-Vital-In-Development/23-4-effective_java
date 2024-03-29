---
marp: true
---

# 아이템65: 리플렉션보다는 인터페이스를 사용하라

---

### 목차

- 리플렉션
- 리플렉션 단점
- 정리

---

### 리플렉션

---

자바 리플렉션을 이용하면 프로그램에서 임의의 클래스에 접근할 수 있다.

Class 객체가 주어지면 해당 클래스의 생성자, 메서드, 필드에 해당하는 인스턴스를 가져올 수 있고, 이어서 이 가져온 인스턴스들로는 그 클래스의 멤버 이름, 필드 타입, 메서드 시그니처를 가져올 수 있다.

나아가 생성자, 메서드, 필드 인스턴스를 이용해 연결된 실제 생성자, 메서드, 필드를 조작할 수 있으며, 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있다.

하지만 단점도 존재한다.

---

### 리플렉션 단점

---

1. **컴파일타임 타입 검사의 이점을 하나도 누릴 수 없다.**
   - 예외 검사도 마찬가지다.
   - 프로그램이 리프렉션 기능을 써서 존재하지 않는 혹은 접근할 수 없는 메서드를 호출하려 시도하면 런타임 오류가 발생한다.
2. **리플렉션을 이용하면 코드가 지저분하고 장황해진다.**

3. **성능이 떨어진다.**
   - 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느리다.

---

이러한 단점들 때문에 리플렉션은 아주 제한된 형태로만 사용해야 그 단점을 피하고 이점만 취할 수 있다.

리플렉션을 사용해야 한다면 인스턴스 생성에만 사용하고 만든 인스턴스는 인터페이스나 상위 클래스를 참조해서 사용해야 한다.

---

### 정리

리플렉션은 단순히 사용하기엔 단점이 많기 때문에 되도록 객체 생성에만 사용하고, 생성한 객체를 이용할 때에는 적절한 인터페이스나 컴파일타임에 알 수 있는 상위 클래스로 형변환해서 사용해야 한다.
