# 아이템 2 : 생성자에 매개변수가 많다면 빌더를 고려하라
***
정적 팩터리와 생성자는 아래 객체와 같이 선택적 매개변수가 많을 때 적절히 대응하기 힘들다.
```java
public class Object {
	private final int requiredField1; //필수
	private final int requiredField2; //필수
	private final int optionalField1; //선택
	private final int optionalField2; //선택
	private final int optionalField3; //선택
}
```

###

### 1. 점층적 생성자 패턴(Telescoping Constructor Pattern)
***

필수 매개변수만 받는 생성자를 시작으로 매개변수를 하나씩 늘려가며 선택적 매개변수를 전부 다 받는 생성자까지 늘려가는 방식이다.



```java
public class Object {
	private final int requiredField1;
	private final int requiredField2;
	private final int optionalField1;
	private final int optionalField2;
	private final int optionalField3;


	public Object(int requiredField1, int requiredField2){
		this(requiredField1, requiredField2, 0);
	}
	
	public Object(int requiredField1, int requiredField2, int optionalField1){
		this(requiredField1, requiredField2, optionalField1, 0);
	}
	
	public Object(int requiredField1, int requiredField2, int optionalField1, int optionalField2){
		this(requiredField1, requiredField2, optionalField1, optionalField2, 0);
    }
	
	public Object(int requiredField1, int requiredField2, int optionalField1, int optionalField2, int optionalField3){
		this.requiredField1 = requiredField1;
		this.requiredField2 = requiredField2;
		this.optionalField1 = optionalField1;
		this.optionalField2 = optionalField2;
		this.optionalField3 = optionalField3;
    }
}
```

#### 단점
1. 초기화하고 싶은 필드만 있는 생성자가 없다면, 설정을 원하지 않는 필드에 매개변수로 값을 설정해줘야한다.
2. 매개변수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.
3. 매개변수의 순서를 바꿔 설정하면 찾기 어려운 버그로 이어질 수 있다.

###

### 2. 자바빈즈 패턴(JavaBeans Pattern)
***

자바빈즈 패턴은 매개변수가 없는 생성자로 객체를 만든 후, 세터(setter) 메서드들을 호출해 원하는 매개변수의 값을 설정하는 방식이다.

```java
public class Object {
	private int requiredField1 = -1; 
	private int requiredField2 = -1;
	private int optionalField1 = 0;
	private int optionalField2 = 0;
	private int optionalField3 = 0;
	
	public Object() {}
    
    public void setRequiredField1(int val){this.requiredField1 = val;}
    public void setRequiredField2(int val){this.requiredField2 = val;}
    public void setOptionalField1(int val){this.optionalField1 = val;}
    public void setOptionalField2(int val){this.optionalField2 = val;}
    public void setOptionalField3(int val){this.optionalField3 = val;}
	
}
```

#### 장점
1. 점층적 생성자 패턴의 단점들이 더 이상 보이지 않는다.
2. 초기화하고 싶은 필드만 선택적으로 설정할 수 있다.
3. 가독성이 좋아졌다.

#### 단점
1. 객체 하나를 만들려면 메서드를 여러 개 호출해야 한다.
2. 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.
3. 일관성이 무너지면서 클래스를 불변으로 만들 수 없다. 
4. 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 해줘야만 한다.

```
불변(immutable 혹은 immutability)은 어떠한 변경도 허용하지 않는다를 의미한다.
자바빈즈 패턴에서는 세터(setter) 메서드가 있어서 객체의 값이 언제든지 변경되는 가변 객체를 생성한다.
```


```
스레드 안전성(Thread Safe)
하나의 메서드가 한 Thread로부터 호출되어 실행 중일 때,
다른 Thread가 동일한 메서드를 호출하여 동시에 실행되더라도 각 Thread에서 메서드의 결과가 바르게 나오는 것.
```
[스레드 안정성(Thread Safe) 참고 페이지](https://velog.io/@cateto/Java-Thread-Safe란)


```
freezing
예로 자바스크립트의 Object.freeze() 메서드가 있다.

Object.freeze() 메서드는 객체를 더 이상 변경할 수 없는 상태로 만든다.
즉, 객체에 새로운 속성을 추가하거나 속성을 제거하는 것을 방지하여 불변성을 얻을 수 있다.
```
[Object.freeze() 메서드 참고 페이지](https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Object/freeze)



###

### 3. 빌더 패턴(Builder Pattern)
***

점층적 생성자 패턴의 안전성과 자바빈즈 패턴의 가독성을 겸비하고 있다.

빌더 패턴은 클라이언트가 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻는다.

그 후 빌더 객체에서 제공하는 메서드 체인(혹은 플루언트 API)을 이용하여 원하는 선택 매개변수들을 설정한다.

마지막으로 매개변수가 없는 build 메서드를 호출하여 클라이언트가 원하는 객체를 얻는 방식이다.

```
메서드 체인(Method Chaining)
메서드 체인은 여러 메서드 호출을 연결해 하나의 실행문으로 표현하는 문법 형태

예제 : new StringBuilder().append("문자열").append("연결").toString();
```


```java
public class Object {
	private final int requiredField1;
	private final int requiredField2;
	private final int optionalField1;
	private final int optionalField2;
	private final int optionalField3;

	public static class Builder() {
		private final int requiredField1;
		private final int requiredField2;

		private int optionalField1 = 0;
		private int optionalField2 = 0;
		private int optionalField3 = 0;

		public Builder(int requiredField1, int requiredField2) {
			this.requiredField1 = requiredField1;
			this.requiredField2 = requiredField2;
		}

		public Builder optionalField1(int optionalField1) {
			this.optionalField1 = optionalField1;
			return this;
		}

		public Builder optionalField2(int optionalField2) {
			this.optionalField2 = optionalField2;
			return this;
		}

		public Builder optionalField2(int optionalField2) {
			this.optionalField2 = optionalField2;
			return this;
		}
		
		public Object build(){
			return new Object(this);
        }
	}
	
	private Object(Builder builder){
		this.requiredField1 = builder.requiredField1;
		this.requiredField2 = builder.requiredField2;
		this.optionalField1 = builder.optionalField1;
		this.optionalField2 = builder.optionalField2;
		this.optionalField3 = builder.optionalField3;
    }

}
```

빌더 패턴을 통해 코드를 쓰기 쉽고, 알기 쉬워졌다.

위 빌더 패턴에서는 유효성 검사가 생략되어있다.

잘못된 매개변수를 최대한 일찍 발견하려면 빌더의 생성자와 메서드에서 입력 매개변수를 검사하고,
build 메서드가 호출하는 생성자에서 여러 매개변수에 걸친 불변식을 검사하자.

검사해서 잘못된 점을 발견하면 어떤 매개변수가 잘못되었는지를 자세히 알려주는 메시지를 담아 IllegalArgumentException을 던지면 된다.


```
불변식(Invariant)이란?
불변식은 프로그램이 실행되는 동안, 혹은 정해진 기간 동안 반드시 만족해야 하는 조건을 말한다.
다시 말해 변경을 허용할 수는 있으나 주어진 조건 내에서만 허용한다는 뜻이다.

예시 : 리스트의 크기는 반드시 0 이상이어야 하며, 음수가 된다면 불변식이 깨진것이다.
```

***

### 빌더 패턴에 장점만 있는 것은 아니다.

객체를 만들려면, 그에 앞서 빌더부터 만들어야 한다.
빌더 생성 비용이 크지는 않지만 성능에 민감한 상황에서는 문제가 될 수 있다.
또한 점층적 생성자 패턴보다는 코드가 장황해서 매개변수가 4개 이상은 되어야 값어치를 한다.
(Lombok의 @Builder 를 통해서 간편하게 만들 수 있다.)

하지만 프로젝트를 진행에 시간이 지날수록 객체의 매개변수가 많아지는 경향이 있으므로 처음부터 빌더 배턴을 적용하는편이 나을 때가 많다.

```
핵심정리
생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.
매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다.
빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바빈즈보다 훨씬 안전하다.
```




