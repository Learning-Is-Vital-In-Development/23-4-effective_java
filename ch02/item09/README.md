## **아이템 09. try-fianlly 보다는 try-with-resources 를 사용하라 #6**

- 자바에선 `InputStream`, `OutputStream`, `java.sql.Connection` 등과 같은 close 메서드를 호출해 직접 닫아줘야 하는 자원이 많음.
- 전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 `try-finally`가 있다. 예외가 발생하거나 메서드에서 반환되는 경우를 포함해서 말이다.

```java
static String firstLineOfFile(String path) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(path));
	try {
		return br.readLine();
	} finally {
		br.close();
	}
}
```

<br>
 
---
## try-fianlly 의 문제점

### 1. 자원이 여러개 일 경우 가독성이 떨어짐.

```java
public static void main(String[] args) throws IOException {
        MyResource myResource = null;
        try{
            myResource= new MyResource();
            myResource.run();
            MyResource resource = null;
            try {
                resource = new MyResource();
                resource.run();
            }finally {
                if(resource !=null){
                    resource.close();
                }
            }
        }finally {
            if(myResource !=null) {
                myResource.close();
            }
        }
    }
```

<br>
 
---
### 2. 디버깅이 힘들어진다

![Untitled](https://user-images.githubusercontent.com/55054505/212477164-6c614bf0-1f00-44e3-97eb-c4a7670583b3.png)

위 코드를 실행했을 때, 제일 나중에 발생한 예외만 Stack trace에 출력이 된다.

<br>

<br>

# try-with-resources

- 위의 이러한 문제들은 `try-with-resources` 덕에 모두 해결되었다. 이 구조를 사용하려면 해당 자원이 `AutoCloseable 인터페이스`를 구현해야 한다.
- 자바 라이브러리와 서드파티 라이브러리들의 수많은 클래스와 인터페이스가 이미 `AutoCloseable`을 구현하거나 확장해뒀다.
   

- 만약 반드시 닫아야 하는 자원을 뜻하는 클래스를 작성한다면 `AutoCloseable`을 반드시 구현해야 한다
     ![image](https://user-images.githubusercontent.com/55054505/212477204-88c64d61-8b5b-4907-83f5-30950ac67cac.png)

<br>

<br>

## try-with-resources 의 장점


 
---
### 1. 코드 간결성

```java
public static void main(String[] args) throws Exception {
	try (MyResource myResource1 = new MyResource();
       MyResource myResource2 = new MyResource()) {
            myResource1.run();
            myResource2.run();
        }
}

```

- `try-with-resources` 버전이 짧고 읽기 수월할 뿐 아니라, 문제를 진단하기도 훨씬 좋다.
- 그리고 보통의 `try-finally`처럼 `try-finally-resources`에서도 catch 절을 쓸 수 있다.
  
<br>
 
---
### 2. 디버깅에 좋음

- `try-finally` 를 사용할 때 처럼 처음에 발생한 예외가 뒤에 발생한 에러에 의해 덮히지 않음.
- 자바7에서 Throwalbe에 추가된 getSuppressed 메서드를 이용하면 프로그램 코드에서 가져올 수 도 있다.

![Untitled2](https://user-images.githubusercontent.com/55054505/212477173-9ca0ebe4-f6c9-49d9-9f22-4e458638f791.png)
