---

marp: true

---

# 아이템 57
# 지역변수의 범위를 최소화하라

---

# 지역변수의 범위를 최소화하라

* 지역변수의 유효 범위를 최소로 줄이면 코드 가독성과 유지보수성이 높아지고 오류 가능성은 낮아진다.

---

# 지역변수의 범위를 줄이는 방법

1. 가장 처음 쓰일 때 선언하기
2. 선언과 동시에 초기화해야 한다.
3. 반복문을 통해 변수 범위를 최소화한다.
4. 메서드를 작게 유지하고 한 가지 기능에 집중하는 것이다.

---

# 가장 처음 쓰일 때 선언하기

* 미리 선언부터 해두면 코드가 어수선하고 가독성이 떨어진다.
* 실제 사용시에 타입과 초깃값이 기억나지 않을 수도 있다.
* 실제 사용하는 블럭 바깥에 선언된 변수는 그 블록이 끝난 뒤까지 살아 있게 된다.
  * 이 변수를 블럭 바깥에서 사용된다면 예상하지 못한 결과로 이어질 수 있다.


---

# 선언과 동시에 초기화해야 한다.

* 초기화에 필요한 정보가 충분하지 않다면 충분해질 때까지 선언을 미뤄야 한다.

~~~java
public void method(){
        // Object object = new Object();
        // ...
        // object.setField1(arg);

        Object object = new Object(arg, ...);

        // 생성자로 예를 들어보면 
        // 객체를 생성하고 나중에 setter 메서드를 통한 초기화 보다는
        // 생성자에서 값들을 넘겨주고 한번에 생성하는 것이 좋다.
        }
~~~

---

# 선언과 동시에 초기화해야 한다. try-catch

* 변수를 초기화하는 표현식에서 검사 예외(Checked Exception)를 던질 가능성이 있다면 try 블럭 안에서 초기화해야 한다.
* 그렇지 않으면 예외가 메서드까지 전파된다.

~~~java
Long username;
        try{
        username = findUsername();
        } catch(CheckedException e){
        // 예외 처리
        throw new RuntimeException();
        }
~~~

---

# 반복문을 통해 변수 범위를 최소화한다.

* 반복문에서는 반복 변수의 범위가 반복문의 몸체, 그리고 for 키워드와 몸체 사이의 괄호 안으로 제한된다.
* 반복변수의 값을 반복문이 종료된 뒤에도 써야 하는 상황이 아니라면 while 문보다는 for 문을 쓰는 편이 낫다.

~~~java
for (Iterator<Integer> i = c.iterator(); i.hasNext();) {
        Integer number = i.next();
        }

        i.hasNext(); // 컴파일 에러
~~~

---

# 반복문을 통해 변수 범위를 최소화한다.

* while을 통해 반복문을 구현하게 되었을 경우 오류가 런타임에 발생하여 찾기 어려운 버그로 남을 수 있다.

~~~java
Iterator<Integer> i = c.iterator();
        while (i.hasNext()) {
        doSomething(i.next());
        }
        ...

        Iterator<Integer> i2 = c2.iterator();
        while (i.hasNext()) { // 버그 발생
        doSomething(i2.next());
        }
~~~

---

# 반복문을 통해 변수 범위를 최소화한다.

* for 문안에 선언된 변수는 for 반복문이 종료되면 같이 끝난다.
* 앞에서의 while을 이용한 반복문이 아닌 for을 이용해 반복문을 구현함으로써 컴파일 타임에 오류를 잡을 수 있게 되었다.

~~~java
for (Iterator<Integer> i = c.iterator(); i.hasNext();) {
        Integer number = i.next();
        }
        ...

        for (Iterator<Integer> i2 = c2.iterator(); i.hasNext();) { // 컴파일 에러   
        Integer number = i2.next();
        }
~~~

---

# 메서드를 작게 유지한다.

* 한 메서드에서 여러가지 기능을 처리한다면 그중 한 기능만 관련된 지역변수라도 다른 기능을 수행하는 코드에서 접근할 수 있다.
* 이를 방지하기 위해 메서드를 한가지 기능만 하도록 쪼개는 것이 좋다.

