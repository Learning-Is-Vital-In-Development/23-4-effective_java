# 아이템 80: 스레드보다는 실행자, 태스크, 스트림을 애용하라

## 실행자 프레임워크

- 스레드를 추상화 한 것으로, 스레드 생성, 스레드 풀 관리 등을 자동으로 처리해주는 프레임워크
- 스레드 생성 및 관리에 필요한 많은 부분을 자동으로 처리한다.
  - 프로그래머는 실행할 작업(task)에만 집중할 수 있습니다.
- 예를 들어, Java에서는 `Executor` 인터페이스와 그 하위 인터페이스들을 통해 실행자 프레임워크를 제공하고 있다.

### 태스크

- 실행자 프레임워크를 사용하면 작업 단위를 나타내는 태스크(Task)를 만들어서 실행자에 제출한다.
- 태스크는 Runnable 또는 Callable 인터페이스를 구현한 객체로, 실행자는 이 태스크들을 수행한다.
- 태스크를 사용하면 작업 단위를 쉽게 분리하고, 작업의 결과를 받아올 수 있다.

## 스트림

- 스트림(Stream)은 데이터를 처리하기 위한 API로, 컬렉션, 배열 등의 데이터 소스를 처리할 수 있다.
- 스트림 API를 사용하면 데이터를 효율적으로 처리할 수 있다.
  - 병렬 처리가 가능하기 때문에, 멀티코어 CPU를 활용하여 빠른 처리가 가능하다.
  - 함수형 인터페이스와 람다식을 활용하여 코드를 간결하고 가독성 높게 작성할 수 있다.

