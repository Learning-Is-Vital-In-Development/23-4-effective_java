---
marp: true
---

# 아이템71: 필요 없는 검사 예외 사용은 피하라

---

## 목차

1. 검사 예외 사용을 피해야하는 이유
2. 검사 예외를 회피하는 방법

---

## 검사 예외 사용을 피해야하는 이유

- 검사 예외는 API 사용자에게 큰 부담감을 안겨준다.
  1. `try-catch` 문을 사용하거나 예외 전파해야 한다.
  2. 스트림 안에서 직접 사용할 수 없다.
- 검사 예외와 비검사 예외를 판단하기 애매하다면 비검사 예외를 사용하는 게 좋다.

---

## 검사 예외를 회피하는 방법

- 검사 예외를 회피하는 가장 쉬운 방법은 Optional을 적절하게 사용하는 것이다.
- 검사 예외를 던지는 메서드를 2개로 쪼개 비검사 예외로 바꿀 수 있다.
  ```java
  // AS-IS
  try {
    obj.action(args);
  } catch (TheCheckedException e) {
    ... // 예외 처리
  }
  
  // TO-BE
  if (obj.actionPermitted(args)) {
    obj.action(args);
  } else {
    ... // 예외 처리
  }
  ```
