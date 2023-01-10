# CH 2. 객체 생성과 파괴

## **아이템 06. 불필요한 객체 생성을 피하라 #6**

- 매번 같은 객체 생성보다는 재사용이 더 낫다.
    
    → 빠르고 세련됨. 특히 불변 객체는 언제든지 재사용이 가능
 
<br>
 
---

## **String 생성자**

```java
// #1 new 연산자를 이용한 방식 
String s = new String("message");

// #2 리터럴을 이용한 방식
Srting s = "message";
```

#1 의 경우 실행될 때 마다 새로운 String 인스턴스를 만든다.

- new를 통해 String을 생성하면 Heap 영역에 존재하게 된다.

#2 의 경우 새로운 인스턴스를 매번 만들지 않고, 하나의 String 인스턴스를 사용

- string constant pool이라는 영역에 존재하게 된다.

![images_jeb1225_post_485f7cbb-f34d-4ba3-8c11-d397b2749842_image](https://user-images.githubusercontent.com/55054505/211319423-535816e9-2e78-4e96-be13-21f2ce608470.png)



<br>

---

## **정적 팩토리 매서드**

```java
System.out.println(Boolean.valueOf("true")); // true

System.out.println(new ~~Boolean~~("true")); // true
```

생성자 대신에 정적 팩토리 매서드를 사용하자!

생성자는 호출할 때마다 새로운 객체를 만들지만, 팩토리 매서드는 그렇지 않음.

<br>

---

## **생성 비용이 비싼 경우**

```java
static boolean isRomanNumeral(String s) {
		return s.matches("^(?=.)M*(C[MD]|D?C{0,3})" +
		    "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```

String.matches 는 정규표현식으로 나타내기 쉽지만, 반복해서 나타내기 쉽지 않음

```java
private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" +
            "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumberal(String s) {
        return ROMAN.matcher(s).matches();
    }
```

Pattern 인스턴스는, 한 번 쓰고 바로 가비지컬렉션 대상이 됨.

Pattern은 입력받은 정규 표현식에 해당하는 FSM을 만들기 때문에 인스턴스 비용이 높음 

→ 직접 캐싱

- 6배 정도 빠름.

<br>

---

## **어뎁터**

어뎁터는 실제 작업은 뒷단 객체에 위임하고, 자신은 제 2의 인터페이스 역할을 해주는 객체다.

어뎁터는 뒷단 객체만 관리하면 된다. 

→ 그 외는 신경을 안써도 되기에 뒷단 객체 하나당 어뎁터 하나씩만 만들어지면 충분하다.

```java
Map<String, Integer> map = new HashMap<>();
map.put("S1",1);
map.put("S2",2);
map.put("S3",3);

Set<String> strings = map.keySet();
map.put("S4",4);    
Set<String> strings1 = map.keySet();
map.put("S5",5);

       
System.out.println(strings);  // [S3, S4, S5, S1, S2]
System.out.println(strings1);// [S3, S4, S5, S1, S2]
```

keySet() : key 값을 가져올때 사용

keySet() 이 호출 될 때마다 새로운 인스턴스가 만들어지지 않음.

<br>

---


## **오토박싱(auto boxing)**

```java
Long sum = 0L;
        
for (long i = 0 ; i <= Integer.MAX_VALUE ; i++) {
            sum += i;
}
        
System.out.println(sum);
```

박싱된 기본 타입보다는 기본 타입을 사용.

의도치 않은 오토박싱이 숨어들어 가지 않도록 주의하기.

- 객체 생성은 비싸다??
    - 작은 객체는 부담되지 않음.
    - 무거운 객체가 아니라면 객체 pool을 만들지 말자.

- 그렇다면 객체 풀은 언제 쓰는게 좋을까??
    - DB연결 비용이 비싼 경우

<br>

---

