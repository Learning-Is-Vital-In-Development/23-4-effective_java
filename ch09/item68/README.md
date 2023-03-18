---
marp: true
backgroundColor: #FFFFF0
theme: gaia
_class: lead
---

## 일반적으로 통용되는 명명 규칙을 따르라

### 최 혁

---

# 목차
<br><br>

### 명명규칙을 따라야 하는 이유

### 명명 철자 규칙

### 명명 문법 규칙


---

## 왜 명명규칙을 따라야 하나요?

- __통용되는 규칙을 사용하면 이름만으로 동작을 유추할 수 있다!__

- __익숙해지면 읽기 편하다!__

- __유지보수하기 쉽다!__

- __협업에 규칙을 제공해준다!__

---

## 패키지와 모듈

- 요소의 이름은 한 단어 or 약어로 이뤄진다.

- 각 요소를 온점(.)으로 구분하여 계층적으로 짓는다.

- 요소 계층 구분
    -  시작: 조직 바깥에서도 사용될 패키지라면 조직의 인터넷 도메인 이름을 역순으로 사용한다. (com.google)
    -  이후: 패키지를 설명하는 하나 이상의 요소 (java.util.concurrent.atomic)

---

## 클래스와 인터페이스(enum, annotation 포함)

- 하나 이상의 단어로 이루어지며 각 단어는 대문자로 시작(PascalCase)
    - ex) FuthurTask, LinkedHashSet

- 여러 단어의 첫 글자만 딴 약자나 널리 통용되는 줄임말을 제외하면 단어를 줄이지 않는다
    - (약자는 논란이 있다. ex) HttpUrl? HTTPURL)

---

## 메서드와 필드

- 첫 글자를 소문자로 쓴다는 점만 빼면 클래스 명명 규칙과 동일(camelCase)
    - ex) groupingBy

- 상수 필드는 예외적으로 대문자와 밑줄(_)로 구분한다.
    - ex) MIN_VALUE

- 지역 변수는 약어를 써도 좋음(변수가 사용되는 문맥에서 유추 가능)

---

## 타입 매개변수

- 보통 한 문자로 표현
    - <T>: 임의의 타입
    - <E>: 컬렉션 원소의 타입
    - <K, V>: 맵의 키와 값
    - <R>: 메서드 반환 타입
    - <E>: 예외

---

## 클래스와 인터페이스

- 객체를 생성할 수 있는 클래스: 단수 명사 or 명사구
    - ex) Thread, PriorityQueue

- 객체를 생성할 수 없는 클래스: 복수 명사
    - ex) Collectors, Collections

- 인터페이스: 단수 명사 or able, ible로 끝난 형용사
    - ex) Collection, Comparator, Runnable, Iterable, Accessible

- 애너테이션: 명사, 동사, 전치사, 형용사 다 쓰임
    - ex) BindingAnnotation, Inject, ImplementedBy, Singleton

---

## 메서드

- 어떤 동작을 수행하는 메서드: 동사 or 동사구
    - ex) append, drawImage

- boolean 값을 반환하는 메서드: is나 has로 시작 + 명사(구) or 형용사
    - ex) isDigit, isEmpty, hasNext

- boolean이 아닌 반환값이 존재하는 메서드: 명사(구) or get + 동사구
    - ex) size, hashCode, getTime

- 객체의 타입을 바꿔 반환하는 메서드: toType
    - ex) toArray, toString, toEntity?

---

- 객체의 내용을 다른 뷰로 보여주는 메서드: asType
    - ex) asList

- 객체의 값을 기본타입 값으로 반환하는 메서드: typeValue
    - ex) intValue

- 정적 팩터리들
    - ex) from, of, getInstance, getType, newType

