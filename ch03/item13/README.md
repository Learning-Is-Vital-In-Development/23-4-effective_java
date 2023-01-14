---
marp: true

---

# 아이템 13 : clone 재정의는 주의해서 진행하라

---

## 목차

1. Cloneable은 무엇일까
2. clone() 사용방법
3. 복사 생성자, 복사 팩터리

---

## 1. Cloneable은 무엇일까?

Cloneable은 복제해도 되는 클래스임을 명시하는 용도의 믹스인 인터페이스이다.

* Object의 protected로 선언된 clone() 메서드의 동작 방식을 결정한다.
* Cloneable을 구현한 클래스의 인스턴스에서 clone()을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환한다.
    * 객체를 복사할 때는 Deep Copy가 아니라 Shallow Copy를 사용한다.
* Cloneable을 구현하지 않은 클래스의 인스턴스에서 clone()을 호출하면 CloneNotSupportedException을 던진다.

```java
class Item13 implements Cloneable {
    ...
}
```

---

## 2. clone() 사용방법

1. clone() 메서드의 일반 규약
2. 가변 상태를 참조하지 않는 클래스용 clone 메서드
3. 가변 상태를 참조하는 클래스용 clone 메서드
4. 더 나아가

---

## 2.1 clone() 메서드의 일반 규약

* x.clone() != x 참이여야 한다.
    * 복사된 객체의 주소는 원본 객체와 다르다.
* x.clone().getClass() == x.getClass() 참이여야 한다.
    * 복사된 객체는 원본 객체와 같은 클래스여야 한다.
* x.clone().equals(x) 참이여야 하지만 필수는 아니다.
    * 복사된 객체는 원본 객체와 논리적 동치여야한다.

---

## 2.2 가변 상태를 참조하지 않는 클래스용 clone 메서드

모든 필드가 기본 타입이거나 불변 객체를 참조한다면 이 객체는 원본의 완벽한 복제본이다.

하지만 쓸데 없는 복사를 지양한다면 clone 메서드를 제공하지 않는게 좋다.

```
@Override
public Item13 clone(){
	try{
	    return(Item13)super.clone();
	}catch(CloneNotSupportedException e){
	    throw new AssertionError();
	}
}
```

---

## 2.3 가변 상태를 참조하는 클래스용 clone 메서드

가변 필드를 super.clone()을 통해 복제하면, 값이아닌 원본 필드의 주소를 참조하게 된다.
(원본이나 복제 객체 중 하나를 수정하면 다른 하나도 수정되어 불변식을 해친다.)

이러한 문제를 해결하기 위해서는 가변 필드를 재귀적으로 호출해주는 것이다.

```
@Override
public Stack clone(){
	try{
	    Stack result=(Stack)super.clone();
	    result.elements=elements.clone();
	    return result;
	}catch(CloneNotSupportedException e){
	    throw new AssertionError();
	}
}
```

---

배열의 clone은 런타임 타입과 컴파일 타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다.

만약 가변 필드가 final이라면 새로운 값을 참조할 수 없으므로 위 방식을 적용할 수 없다.
그래서 일부 필드에서 final을 제거해야 할 수도 있다.

이는 가변객체를 참조하는 필드는 final로 선언하라는 일반 용법과 충돌한다.

만약 배열 안에 가변 참조 객체가 있다면 어떻게 해야할까?

---

위 방법으로는 충분하지 않다. 이런경우에는 Deep Copy를 사용해야한다.

```
Entry deepCopy(){
	return new Entry(key,value,next==null?null:next.deepCopy());
}
```

```
@Override
public HashTable clone(){
	try{
  	    HashTable result=(HashTable)super.clone();
	    result.buckets=new Entry[buckets.length];
	    for(int i=0;i<buckets.length;i++){
	        if(bucketsp[i]!=null)
	            result.buckets[i]=buckets[i].deepCopy();
	    return result;
	    }
	}...
}
```

---


이 방식으로 복제하는 방법은 그다지 좋지 않다.

재귀 호출로 리스트의 원소 수만큼 [스택 프레임](http://www.tcpschool.com/c/c_memory_stackframe)을 소비하여, 리스트가 길면 스택 오버플로를 일으킬 위험이 있기 때문이다.

그럴때는 반복자를 이용하여 구현할 수 있다.

```
Entry deepCopy(){
	Entry result=new Entry(key,value,next);
	for(Entry p=result;p.next!=null;p=p.next)
	    p.next=new Entry(p.next.key,p.next.value,p.next.next);
	return result;
}
```

---

## 2.4 더 나아가

---

### 안전하지만 느린

HashTable의 예로 들자면, 원본 버킷의 내용을 put(key, value) 메서드를 통해 복사하는 방법이 있다.

이 방법은 안전하지만, 위 방법보다는 고수준의 메서드를 사용기때문에 느리다.

---

### 예외처리

재정의한 메서드에서는 throws 절을 없애야 한다.
다른 방법으로는 RuntimeException을 상속받는 언체크 예외를 던져야 한다.

그렇지 않으면 clone 메서드를 호출 할 때마다 예외 처리를 해야하기 때문에 불편합니다.
(CloneNotSupportedException 체크 예외이다.)

---

### 상속해서 사용하기

상속해서 쓰기 위한 클래스라면 Cloneable을 구현해서는 안된다.

하지만 Object의 동작 방식을 모방하여 구현할 수 있습니다.

이와같이 Cloneable의 구현 여부를 하위 클래스에서 선택하게 해야한다.

다른 방법으로는 하위 클래스에서 clone을 동작하지 않게 만들수도 있다.
```
@Override
protected final Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
}
```

---

### 동기화

스레드 안정성을 고려한다면 clone 메서드에 대해 적절히 동기화 처리를 해야한다.

```
@Override
public synchronized Item13 clone(){
    try{
        Item13 result = (Item13) super.clone();
        ...
    } ...
}
```


---

## 3. 복사 생성자, 복사 팩터리

복사 생성자와 복사 팩터리

---

복사 생성자는 단순히 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자를 말한다.

복사 팩터리는 복사 생성자를 모방한 정적 팩터리(아이템 1)다

```java
// 복사 생성자
public Item13(Item13 item13){...}
// 복사 팩터리
public static Item13 newInstance(Item13 item13){...}
```

---

복사 생성자와 복사 팩터리는 Cloneable/clone 방식보다 나은 면이 많다.

1. 언어 모순적이고 위험천만한 객체 생성 메커니즘을 사용하지 않는다.(생성자를 사용하지 않는 방식)
2. 엉성하게 문서화된 규약에 기대지 않는다.
3. 정상적인 final 필드 용법과도 충돌하지 않는다.
4. 불필요한 검사 예외를 던지지 않는다.
5. 형변환도 필요하지 않다.
6. 해당 클래스가 구현한 '인터페이스'타입의 인스턴스를 인수로 받을 수 있다.

---

## 핵심 정리
Cloneable의 문제점을 생각했을 때.

새로운 인터페이스를 만들 때 Cloneable을 확장해서는 안되며, 새로운 클래스도 이를 구현해서는 안 된다.

final 클래스라면 Cloneable을 구현해도 위험이 크지 않지만, 성능 최적화 관점에서 검토한 후 별다른 문제가 없을 때만 드물게 허용해야한다.

기본 원칙은 '복제 기능은 생성자와 팩터리를 이용하는게 최고'라는 것이다.
단, 배열만은 clone 메서드 방식이 가장 깔끔한, 이 규칙의 합당한 예외라 할 수 있다.

---