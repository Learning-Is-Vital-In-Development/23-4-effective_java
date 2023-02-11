# 아이템34: int 상수 대신 열거 타입을 사용하라

### 목차

1. int 상수
2. 열거 타입
3. 열거 타입 메서드, 필드 추가
4. 더 다양한 기능 추가
5. 정리

## int 상수

- 자바에서 열거 타입을 지원하기 전 사용하던 방식

  ```java
  // 정수 열거 패턴
  public static final int APPLE_FUJI = 0;
  public static final int APPLE_PIPPIN = 1;
  public static final int APPLE_GRANNY_SMITH = 2;

  public static final int ORANGE_NAVEL = 0;
  public static final int ORANGE_TEMPLE = 1;
  public static final int ORANGE_BLOOD = 2;
  ```

- 타입 안전을 보장할 방법이 없으며 표현력이 좋지 않다.
- 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다
  - 평범한 상수를 나열한 것이기 때문에 클라이언트 파일에 이미 컴파일된 값이 새겨지면 이후 상수의 값이 바뀌었을 때, 클라이언트 파일 또한 재 컴파일 해야 한다.
- 정수 상수는 문자열로 출력하기가 다소 까다롭다.

### 열거타입

- 열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다.
- 열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.
- 열거 타입은 밖에서 접근 가능한 생성자를 제공하지 않기 때문에 사실상 final이다.
- 열거 타입 선언으로 만들어진 인스턴스는 딱 하나씩만 존재한다. 다시 말해 열거 타입은 싱글턴을 인반화한 형태라 할 수 있고 반대로 싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있다.
- 열거 타입은 컴파일 타입 안정성을 제공한다.
- 열거 타입에는 각자의 이름공간이 있어서 이름이 같은 상수도 공존이 가능하다.
- 열거 타입에는 임의의 메서드나 필드를 추가할 수 있고 임의의 인터페이스를 구현하게 할 수도 있다.

### 열거 타입 메서드, 필드 추가

- 각 상수와 연관된 데이터를 해당 상수 자체에 내장 시키고 싶은 경우

  ```java
  public enum Week {
  	SUN(2, 9_160),
  	MON(8, 9_160),
  	TUE(8, 9_160),
  	WEN(8, 9_160),
  	THU(7, 9_160),
  	FRI(7, 9_160),
  	SAT(2, 9_160);

      private final int workHour;
      private final int basicWage;

      Week(int workHour, int basicWage) {
          this.workHour = workHour;
          this.basicWage = basicWage;
      }

      public int getWorkHour() {
          return workHour;
      }

      public int getBasicWage() {
          return hourlyWage;
      }

      public int getDailyWage() {
          return workHour * basicWage;
      }
  }
  ```

  - 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.
  - 열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 `values`를 제공한다.

### 더 다양한 기능 추가

- 열거 타입의 각 상수마다 동작이 달라져야 하는 상황이 있을 수 있다.

  - switch문으로 상수 값에 따라 분기하는 방법
    - 새로운 상수를 추가할 때마다 해당 case문을 추가해야 하며, 혹시 깜빡한다면 런타임 오류가 발생한다.
  - 열거 타입에 추상 메서드를 선언하고 각 상수에서 자신에 맞게 재정의하는 방법 → 상수별 메서드 구현

    ```java
    public enum Operation {
        PLUS{public double apply(double x, double y) { return x + y; }},
        MINUS{public double apply(double x, double y) { return x - y; }},
        TIMES{public double apply(double x, double y) { return x * y; }},
        DIVIDE{public double apply(double x, double y) { return x / y; }};

    		public abstract double apply(double x, double y);
    ```

    메서드가 상수 선언 바로 옆에 붙어 있어 새로운 상수를 추가할 때 해당 메서드를 재정의 해야 한다는 사실을 잊기 어렵다. 또한 재정의 하지 않았을 경우 컴파일 에러로 알려준다.

  - 상수별 메서드 구현 방식은 열거 타입 상수끼리 코드를 공유하기 어렵다는 단점이 있다. 이에 대한 해결법으로 전략 열거 타입 패턴이 있다. 공유하는 같은 동작을 전략 열거 타입에 위임하는 방식이다.

    ```java
    enum PayrollDay {
        MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
        THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
        SATURDAY(WEEKEND), SUNDAY(WEEKEND);

        private final PayType payType;

        PayrollDay(PayType payType) { this.payType = payType; }
        // PayrollDay() { this(PayType.WEEKDAY); } // (역자 노트) 원서 4쇄부터 삭제

        int pay(int minutesWorked, int payRate) {
            return payType.pay(minutesWorked, payRate);
        }

        // 전략 열거 타입
        enum PayType {
            WEEKDAY {
                int overtimePay(int minsWorked, int payRate) {
                    return minsWorked <= MINS_PER_SHIFT ? 0 :
                            (minsWorked - MINS_PER_SHIFT) * payRate / 2;
                }
            },
            WEEKEND {
                int overtimePay(int minsWorked, int payRate) {
                    return minsWorked * payRate / 2;
                }
            };

            abstract int overtimePay(int mins, int payRate);
            private static final int MINS_PER_SHIFT = 8 * 60;

            int pay(int minsWorked, int payRate) {
                int basePay = minsWorked * payRate;
                return basePay + overtimePay(minsWorked, payRate);
            }
        }
    }
    ```

### 정리

- **필요한 원소를 컴파일타임에 전부 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자!**
- 열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.
  - 나중에 상수가 추가되더라도 괜찮다.
