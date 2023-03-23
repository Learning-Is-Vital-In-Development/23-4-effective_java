---

marp: true

---

# 아이템 74
# 메서드가 던지는 모든 예외를 문서화하라

---

# 문서화

* 메서드가 던지는 예외는 그 메서드를 올바로 사용하는 데 아주 중요한 정보다.
* 각 메서드가 던지는 예외 하나하나를 문서화하는 데 충분한 시간을 쏟아야 한다.(아이템 56)

---

# 체크 예외

* 체크 예외는 항상 따로따로 선언해야 한다.
* 공통 상위 클래스 하나로 선언한다면 메서드 사용자가 각 예외에 대해 대응하기 어렵다.
* 상위 예외 클래스가 다른 예외들까지 삼켜버려 API 사용성을 떨어뜨린다.
* @throws 태그를 이용하여 각 예외들이 발생하는 상황을 문서화해야 한다.

~~~java
// Exception, Throwable과 같이 공통 상위 클래스 하나로 선언하면 안된다.
public void method() throw Exception {}
// 각 예외를 따로따로 선언함으로써 프로그래머가 각 예외에 대처할 수 있는 힌트를 준다.
public void method() throw SQLException, ClassNotFoundExcpetion {}
~~~

---

# 언체크 예외

* 언체크 예외도 체크 예외처럼 정성껏 문서화 해두면 좋다.
* 잘 문서화된 언체크 예외 문서는 메서드를 성공적으로 수행하기 위한 전제조건이다.
* 특히 인터페이스 메서드에서 비검사 예외를 문서로 남기는 일이 중요하다.
    * 인터페이스를 구현한 모든 구현체가 일관되게 동작하도록 해주기 때문이다.

---

# @throws

* 메서드가 던지는 체크 예외만 @throws 태그로 문서화하자.
* 체크, 언체 예외이냐에 따라서 API 사용자가 해야 할 일이 달라지므로 구분해주는 것이 좋다.

~~~java
/**
* @throws SQLException : ...
* @throws ClassNotFoundException : ...
*/
// NullPointerException은 언체크 예외이기 때문에 @throws 태그로 문서화하지 않았다. 
public void method() throw SQLException, ClassNotFoundException, NullPointerException{}
~~~

---

# @throws-언체크 예외

* 현실적으로 모든 언체크 예외를 문서화하는 것은 불가능하다.
* 클래스를 수정해서 새로운 언체크 예외를 던지더라도 소스, 바이너리 호환성이 그대로 유지되어 문서에서 나와있지 않은 새로운 예외를 던지게 될 수 있다.

---

# 클래스 설명

* 한 클래스의 여러 메서드에서 동일한 언체크 예외를 던진다면 클래스 설명에 추가해주면 좋다.

~~~java
/**
* NullPointerException : ...
*/
public class Object {
    /**
    * @throws SQLException : ...
    * @throws ClassNotFoundException : ...
    */
    // NullPointerException은 언체크 예외이기 때문에 @throws 태그로 문서화하지 않았다. 
    public void method() throw SQLException, ClassNotFoundException, NullPointerException{}
}
~~~