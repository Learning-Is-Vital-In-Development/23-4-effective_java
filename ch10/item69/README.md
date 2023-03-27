---
marp: true
---

# 아이템69: 예외는 진짜 예외 상황에서만 사용하라

---

## 목차

1. 예외를 사용하면 안되는 상황
2. API에 대해서 적용
3. (추가) 내 코드에 적용해보기

---

## 예외를 사용하면 안되는 상황

- 예외를 흐름 제어에 사용하지 마라.
  ```java
  try {
	int i = 0;
	while(true)
		range[i++].climb();
  } catch (ArrayIndexOutOfBoundsException e) {}
  ```
---

## 예외를 사용하면 안되는 상황

- range 인덱스를 벗어날 때 발생하는 `ArrayIndexOutOfBoundsExceptionz`를 이용하여 반복문을 종료시키고 있다.  
  이 코드는 가독성도 나쁘며, `try-catch` 블록 안에 넣으면 JVM이 적용할 수 있는 최적화가 제한된다.
  ```java
  for(Mountain m : range)
	m.climb();
  ```

---

## API에 대해서 적용

- API에도 동일하게 적용 가능하다. API가 잘 설계되면 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없어야 한다.
  ```java
  for(Iterator<Foo> i = collection.iterator(); i.hasNext();)
	m.climb();
  ```

---

## API에 대해서 적용

- 만약 Iterator가 hasNext()라는 메서드를 제공하지 않았다면 아래와 같이 짜야했을 것이다.
  ```java
  try {
	Iterator<Foo> i = collection.iterator();
	while(true) {
		Foo foo = i.next();
		...
    }
  } catch (NoSuchElementException e) {}
  ```

---

## (추가) 내 코드에 적용해보기

- 요구사항
  - 신고 대상은 총 3가지(상품, 댓글, 사람)이다.
  - 상품과 댓글 신고 시에는 작성자에 대한 신고가 추가로 발생한다.
  - 신고는 신고자와 피신고자가 유니크해야 한다. 즉, 동일한 신고자가 동일한 피신고자를 2번 이상 신고 할 수 없다.

- 문제 상황
  - `Transaction` 안에서 상품이나 댓글 신고 시 발생하는 작성자에 대한 추가 신고에서 `신고 중복 예외`가 발생하여 상품, 댓글 신고가 롤백되는 상황

---

## (추가) 내 코드에 적용해보기

- 기존 조치 사항
  - 작성자 신고 로직 외부에 `try-catch` 문을 이용해 흐름을 제어 
  ```java
  public class ReportExecuteForProduct implements ReportExecuteStrategy {
    @Override
    @Transactional
    public void execute(User reporter, long productId, String reason) {
        // product에 대한 신고 및 페널티 정책 처리
        // ...

        // 상품의 작성자에 대해서 추가적인 신고
        try {
            long writerId = product.getWriterId();
            reportExecuteForUser.execute(reporter, writerId, reason); 
        } catch (IllegalArgumentException ignored) {
        }
    }
  }
  ```

---

## (추가) 내 코드에 적용해보기

- 개선 방향
  1. 중복 체크를 미리해서 흐름 제어를 하기!
  ```java
  public class ReportExecuteForProduct implements ReportExecuteStrategy {
    @Override
    @Transactional
    public void execute(User reporter, long productId, String reason) {
        // product에 대한 신고 및 페널티 정책 처리
        // ...

        // 상품의 작성자에 대해서 추가적인 신고
        long writerId = product.getWriterId();
        if (reportRepository.existsByReporterIdAndTypeAndTypeId(
        		reporter.getId(),
                Report.Type.USER,
                writerId
        )) return;

        reportExecuteForUser.execute(reporter, writerId, reason);
    }
  }
  ```

---

## (추가) 내 코드에 적용해보기

- 개선 방향
  2. 멱등성 보장을 위해 신고가 되어 있다면 중복 생성이 아닌 기 신고된 데이터를 확인 후 신고되었다고 반환하기
  ```java
  // 상품에 대한 신고 처리
  public class ReportExecuteForProduct implements ReportExecuteStrategy {
    @Override
    @Transactional
    public void execute(User reporter, long productId, String reason) {
        // product에 대한 신고 및 페널티 정책 처리
        // ...

        // 상품의 작성자에 대해서 추가적인 신고
        long writerId = product.getWriterId();
        reportExecuteForUser.execute(reporter, writerId, reason);
    }
  }
  ```
 
---

## (추가) 내 코드에 적용해보기

- 개선 방향
  2. 멱등성 보장을 위해 신고가 되어 있다면 중복 생성이 아닌 기 신고된 데이터를 확인 후 신고되었다고 반환하기
  ```java
  // 유저에 대한 신고 처리
  public class ReportExecuteForUser implements ReportExecuteStrategy {
    @Override
    @Transactional
    public void execute(User reporter, long reportedUserId, String reason) {
        if (reportRepository.existsByReporterIdAndTypeAndTypeId(
        		reporter.getId(),
                Report.Type.USER,
                reportedUserId
        )) return;

        // 신고 처리 및 신고에 따른 페널치 정책 처리
        // ...
    }
  }
  ``` 
