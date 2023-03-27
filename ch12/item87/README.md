# **아이템 87. 커스텀 직렬화 형태를 고려해보라**

개발 일정에 쫓기는 상황에서는 API 설계에 노력을 집중하는 편이 낫다.

다음 릴리스에서 세부적인 기능을 제대로 구현하고 이번 릴리즈는 대충 동작만하게 하면 된다는 뜻이다.

하지만 클래스가 Serializable을 구현하고 기본 직렬화 형태를 사용한다면 다음 릴리즈때 버리려 한 현재의 구현에 영원히 발이 묶이게 된다.(현재의 기본 직렬화 형태를 버릴 수 없게 되기 때문이다.)

## **먼저 고민해보고 괜찮다고 판단될 때만 기본 직렬화 형태를 사용하라**

- 기본 직렬화 형태는 유연성, 성능, 정확성, 측면에서 신중히 고민한 후 합당할 때만 사용해야 한다
- **객체의 물리적 표현과 논리적 내용이 같다면 기본 직렬화 형태라도 무방하다**

- 물리적 표현이란?
    - 모든 데이터로 Class 전체를 의미한다. (코드로 어떻게 구현했는지)
- 논리적 표현이란?
    - 실제로 객체를 표현하는 데 쓰이는 데이터
- 예를들어 아래와 같이 사람의 성명을 간략히 표현한 것은 기본 직렬화 형태를 써도 될것이다.

```java
public class Name implements Serializable {
    
    /**
     * 성. null이 아니어야함
     * @serial
     */
    private final String lastName;
    
    /**
     * 이름. null이 아니어야 함.
     * @serial
     */
    private final String firstName;
    
    /**
     * 중간이름. 중간이름이 없다면 null.
     * @serial
     */
    private final String middleName;
}
```

- 기본 직렬화 형태가 적합하다고 결정했더라고 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 할 때가 많다.
- 위 예시에서는 lastName, firstName 이 null 이 아님을 readObject 메서드로 보장을 해야 됨

- @serial : private 필드의 설명을 API 문서에 포함하라고 자바독에 알려주는 역할
- @serial 태그로 기술한 내용은 API 문서에서 직렬화 형태를 설명하는 특별한 페이지에 기록된다.

## **기본 직렬화 형태에 적합하지 않은 클래스**

```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;
    
    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
}
```

논리적으로는 이클래스는 일련의 문자열을 표현한다.

물리적으로는 문자열을 이중 연결 리스트로 연결했다. 

이 클래스에 기본 직렬화 형태를 사용하면 각 노드의 양방향 연결 정보를 포함해 모든 엔트리(Entry)를 철두철미하게 기록한다.

### **객체의 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용하는 경우 생기는 문제**

## 1. **공개 API가 현재의 내부 표현 방식에 영구히 묶인다.**

- 앞의 예에서 private 클래스인 StringList.Entry가 공개 API가 되어버린다.
- 다음 릴리스에서 내부 표현 방식을 바꾸더라도 StringList 클래스는 여전히 연결 리스트로 표현된 입력도 처리할 수 있어야 한다.
- 즉 연결 리스트를 더 이상 사용하지 않더라도 관련 코드를 제거할 수 없다.

## 2. **너무 많은 공간을 차지할 수 있다.**

- 앞 예의 직렬화 형태는 연결 리스트의 모든 엔트리와 연결 정보까지 기록했지만, 엔트리와 연결 정보는 내부 구현에 해당하니 직렬화 형태에 포함할 가치가 없다.
- 이처럼 직렬화 형태가 너무 커져서 디스크에 저장하거나 네트워크로 전송하는 속도가 느려진다.

## 3. **시간이 너무 많이 걸릴 수 있다.**

- 직렬화 로직은 객체 그래프의 위상에 관한 정보가 없으니 그래프를 직접 순회해볼 수밖에 없다.
- 앞의 예제는 간단히 다음 참조를 따라가 보는 정도로 충분하다.

## 4. **스택 오버플로를 일으킬 수 있다.**

- 기본 직렬화 과정은 객체 그래프를 재귀 순회하는데 스택 오버플로 에러가 날 수 있다.

**그렇다면 합리적인 직렬화 형태로 바꾼 코드는 어떨까?**

## **합리적인 커스텀 직렬화 형태를 갖춘 StringList**

```java
public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;
    
    // 이제는 직렬화되지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }
    
    // 지정한 문자열을 이 리스트에 추가한다.
    public final void add(String s) {...}
    
    /**
     * StringList 인스턴스를 직렬화 한다.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
     	//기본 직렬화를 수행한다.
        s.defaultWriteObject();
        s.writeInt(size);
        
        // 커스텀 직렬화를 수행한다.
        // 모든 원소를 올바른 순서로 기록한다.
        for (Entry e = head; e != null; e = e.next)
            s.writeObject(e.data);
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        //기본 역직렬화를 수행한다.
        s.defaultReadObject();
        int numElements = s.readInt();
        
        // 커스텀 역직렬화 부분
        // 모든 원소를 읽어 이 리스트에 삽입한다.
        for(int i = 0; i < numElements; i++) {
            add((String) s.readObject());
        }
    }
}
```

1) writeObject()와 readObject가 직렬화 형태를 처리한다.

2) transient 한정자를 사용해 인스턴스 필드가 기본 직렬화 형태에 포함되지 않도록 한다.

- 클래스의 인스턴스가 모두 transient더라도 defaultWriteObject와 defaultReadObject를 호출해줘야 한다.
    
    (향후 릴리즈에서 transient가 아닌 필드가 추가되더라도 상호 호환되기 때문이다)
    

**달라진 점**

- 원래버전의 절반정도의 공간을 차지하며 수행속도 또한 두 배 이상 빠르다.
- 스택 오버플로 에러가 발생하지 않는다. (크기의 제한이 사라짐)

**writeObject 와 readObject 가 private 으로 되어 있음.**

- 다른 접근 지정자로 선언된 경우 호출되지 않는다. `private` 으로 선언되었다는 것은 이 클래스를 상속한 서브 클래스에서 메서드를 **재정의(override)**를 하지 못하게 한다는 것이다.
- 또한, 다른 객체는 호출할 수 없기 때문에 클래스의 무결성이 유지되며 슈퍼 클래스와 서브 클래스는 독립적으로 직렬화 방식을 유지하며 확장될 수 있다.
- 직렬화 과정에서는 내부적으로 **리플렉션(reflection)**을 통해 **readObject()**메서드를 호출하기 때문에 접근 지정자는 문제가 되지 않는다.

defaultWriteObject() 와 defaultReadObject() 는 각각 기본 serialization 을 수행한다.

따라서 custom serialization 의 전후에 반드시 호출해줘야 한다.

## **객체의 불변식이 깨지는 경우에는 직렬화를 주의해야한다.**

ex) 해시 테이블

- 물리적으로는 key-value 엔트리를 담은 해시 버킷을 차례로 나열한 형태다.
- 어떤 엔트리를 어떤 버킷에 담을지는 key에서 구한 hashcode가 결정하는데 **그 계산 방식은 구현에 따라 달라질 수 있다.**
- 따라서 해시테이블을 직렬화한 후 역직렬화하면 불변식이 심각하게 훼손된 객체들이 생겨날 수 있는 것이다.

## **객체의 논리적 상태와 무관한 필드라고 확신하면 transient 한정자를 생략하라**

- 기본 직렬화를 수용하든 하지 않든 defaultWriteObject 메서드를 호출하면 transient로 선언하지 않은 모든 인스턴스 필드가 직렬화된다.
- 따라서 transient로 선언해되 되는 인스턴스 필드에는 모두 transient를 붙여야 한다.
- JVM을 실행할 때마다 값이 달라지는 필드도 transient를 붙여야 한다.
- 커스텀 직렬화 형태를 사용한다면 앞서의 StringList 처럼 대부분의(혹은 모든) 인스턴스 필드를 transient로 선언해야 한다.

## **동기화 메커니즘을 직렬화에도 적용해야 한다.**

기본 직렬화 여부와 상관없이 객체의 전체 상태를 읽는 메서드에 적용해야하는 동기화 메커니즘을 직렬화에서도 적용해야 함

```java
private synchronized void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
}
```

## **직렬 버전 UID를 명시적으로 부여하자**

어떤 직렬화 형태를 사용하든 직렬 가능 클래스에 모두 직렬 버전 UID를 명시적으로 부여하자.

이렇게 하면 직렬 버전 UID가 일으키는 잠재적인 호환성 문제가 사라진다.

성능도 조금 빨라지는데 직렬 버전 UID를 명시하지 않으면 런타임에 이 값을 생성하느라 복잡한 연산을 수행하기 때문이다.

```java
private static final long serialVersionUID = -232923283928929;
```

## **주의할 점**

**직렬버전 UID는 클래스의 명세가 변경되면 자동 생성된 값이 바뀌기 때문에 이부분도 주의해야 한다.**

구버전으로 직렬화된 인스턴스들과 호환성을 끊으려는 경우를 제외하고는 직렬 버전 UID를 절대 수정하면 안된다.

# **정리**

- 클래스를 직렬화하기로 했다면 어떤 직렬화 형태를 사용할지 심사숙고 해야한다.
- 자바의 기본 직렬화 형태는 객체를 직렬화한 결과가 해당 객체의 논리적 표현에 부합할 때만 사용하고 그렇지 않으면 객체를 적절히 설명하는 커스텀 직렬화 형태를 고안해야 한다.
- 직렬화 형태도 공개 메서드를 설계할 때에 준하는 시간을 들여 설계 해야 한다.
- 한번 공개된 메서드는 향후 릴리즈에서 제거할 수 없듯이 직렬화 형태에 포함된 필드도 마음대로 제거할 수 없다.
- 직렬화 호환성을 유지하기 위해 영원히 지원해야 한다.
- 잘못된 직렬화 형태를 선택하면 해당 클래스의 복잡성과 성능에 영구히 부정적인 영향을 남긴다.