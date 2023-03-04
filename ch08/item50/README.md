---

marp: true

---

# 이이템 50
# 적시에 방어적 복사본을 만들라

---

# 자바는 안전한 언어다.

* 네이티브 메서드를 사용하지 않아서 C, C++에서 발생하는 메모리 충돌 오류에서 안전하다.
* 자바로 작성한 클래스는 불변식이 지켜진다.

> Java Native Method : C, C++와 같이 네이티브 프로그래밍 언어로 작성한 메서드

---

# 방어적 프로그래밍

* 다른 클래스로부터의 침범을 아무런 노력 없이 다 막을 수 있는 것은 아니다.
* 악의적인 의도를 가진 사람들이 시스템의 보안을 뚫으려는 시도가 늘고 있다.
* 평범한 프로그래머도 순전히 실수로 클래스를 오동작하게 만들 수 있다.

**따라서 클라이언트가 불번식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍해야 한다.**

---

# 문제의 코드

~~~java
public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end){
        if(start.compareTo(end)>0){
            ...
        }
        this.start = start;
        this.end = end;
    }

    public Date start(){
        return start;
    }

    public Date end(){
        return end;
    }
}
~~~

---

# 첫번째 문제

* Date 클래스는 setter 메서드를 제공하여 Period 객체의 불변성을 깨뜨린다.

~~~java
Date start = new Date();
Date end = new Date();
Period period = new Period(start, end);
end.setYear(78);
~~~


---

# 첫번째 문제 해결


* 생성자에서 받은 가변 매개변수를 각각을 방어적으로 복사하면 된다.
* 자바 8 이후의 Instant(또는 LocalDateTime, ZonedDateTime)을 사용하면 된다.

~~~java
public Period(Date start, Date end){
    // 각 매개변수의 복사본을 사용한다.
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());

    if(start.compareTo(end)>0){
        ...
    }

    // 멀티 스레드 환경에서 다른 스레드가 원본 객체를 수정할 수 있기 때문에
    // 방어적 복사본을 만든 다음에 유효성을 검사했다.
    // 원본 객체를 수정하는 공격을 검사시점/사용시점(TOCTOU) 공격이라고 한다.
}
~~~

---

# 두번째 문제

* Period 객체의 접근자 메서드를 통해 Date 객체로 접근이 가능하기 때문에 값을 언제든지 변경할 수 있다.

~~~java
Date start = new Date();
Date end = new Date();
Period period = new Period(start, end);
period.end().setYear(78);
~~~

---

# 두번쩨 문제 해결

* getter 메서드가 가변 필드의 방어적 복사본을 반환하면 된다.
* 네이티브 메서드나 리플렉션 같이 언어 외적인 수단을 사용하지 않는 이상 Period 객체의 가변 필드에 접근할 수 없다.

~~~java
public Date start(){
    return new Date(start.getTime());
}
public Date end(){
    return new Date(end.getTime());
}
~~~

---

# clone 메서드-생성자

~~~java
public Period(Date start, Date end){
    this.start = (Date)start.clone();
    this.end = (Date)end.clone();

    if(start.compareTo(end)>0){
        ...
    }
}

class HackDate extends Date implements Cloneable{
    private List<Date> hack = new ArrayList<>();
    
    public List<Date> getHack() {
        return hack;
    }
    
    @Override
    public Object clone() {
        Date hackDate = (Date) super.clone();
        hack.add(hackDate);
        return hackDate;
    }
}
~~~

---

# clone 메서드

* Date와 같이 final 선언이 되어있지 않은 클래스는 확장이 가능하므로 clone 메서드를 통해 얻은 객체는 그 클래스의 하위클래스의 객체를 반환할 수 있다.
* 매개변수가 제3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안 된다.

~~~java
Date start = new HackDate();
Date end = new HackDate();
Period period = new Period(start, end);
List<Date> hack = ((HackDate)(period.getEnd())).getHack();
hack.get(0).setYear(98);

// Period{start=Sat Mar 04 00:48:09 KST 2023, end=Wed Mar 04 00:48:09 KST 1998}
~~~

---

# 방어적 복사 목적

* 불변 객체를 만들기 위해서만이 아니다.
* 변경될 수 있는 객체가 클래스에 넘겨진 뒤에 임의로 변경되더라도 문제없이 동작할지 확신할 수 없다면 복사본을 만들어야 한다.

---

# 방어적 복사 주의점

* 방어적 복사는 성능 저하가 따르고 항상 쓸 수 있는 것이 아니다.
* 방어적 복사를 생략할 때는 해당 매개변수나 반환값을 수정하지 말아야 함을 문서화 해야 한다.
* 방어적 복사를 생략해도 되는 상황
    * 해당 클래스와 그 클라이언트가 상호 신뢰할 수 있을 때
    * 불변식이 깨지더라도 영향이 클라이언트에 국한될 때

---

# 정리

* **되도록 불변 객체들을 조합해 객체를 구성해야 방어적 복사를 할 일이 줄어든다.**
* 구성요소가 가변이라면 방어적 복사를 해야한다.
* 해당 구성요소를 수정했을 때의 책임이 클라리언트에 있음을 문서화 해야 한다.