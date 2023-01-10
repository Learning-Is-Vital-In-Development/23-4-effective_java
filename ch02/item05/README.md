# 아이템 5 : 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

클래스를 생성하는 방식 중에는 여러 방식이 있고, 그 중에는 정적 유틸리티 방식과 싱글턴 방식이 있는데, 이 방식들은 클래스가 특정 자원을 사용할 경우 적절하지 않습니다.

웹 애플리케이션을 만들 때 자주 쓰이는 서비스와 리포지토리를 예로 들겠습니다. 이때 서비스는 리포지토리를 의존합니다.

## 기존 방식
### 정적 유틸리티 방식
```java
public class Service {
    private static final MemberRepository memberRepository = new MemoryMemberRepository();

    private Service() {}

    ...
}
```
* 정적 유틸리티는 `정적 메서드와 정적 필드만을 담은 클래스를 만들 때 사용하는 방식`입니다. (아이템4)
* 정적 유틸리티의 예시로는 `java.lang.Math` 클래스가 있습니다. 우리는 `Math` 클래스를 통해 `Math.PI`, `Math.abs(-5)` 등 `이미 정해져 있는` 연산을 할 수 있습니다.
* 그런데, 만약 해당 클래스가 변경 가능성이 있는 다른 클래스를 의존하고 있다면 정적 유틸리티 방식은 적합하지 않게 됩니다. 이 예시에서는 테스트 할 때 직접 서비스에서 리포지토리를 갈아끼워야 하는 변경이 일어나게 되고, 이는 곧 유연하지 않음을 의미합니다.
* 정적 유틸리티 클래스가 다른 클래스를 의존하면서 변경의 여지가 있을 경우, `SOLID 원칙` 중 `OCP 원칙`과 `DIP 원칙`을 준수하지 못하게 됩니다.

### 싱글턴 방식
```java
public class Service {
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    private Service {}
    public static Service INSTANCE = new Service();

    ...
}
```
* 싱글턴 방식은 `인스턴스를 하나만 생성하고 싶을 때 사용되는 방식`입니다. (아이템3)
* 이 방식 또한 필요한 클래스의 인스턴스를 직접 설정한다는 점에서 적합하지 않습니다. 정적 인스턴스 방식과 동일하게 `OCP 원칙`과 `DIP 원칙`을 준수하지 못합니다.
* 정적 유틸리티와 싱글턴 방식에서 `final`을 지우고 `setter` 메서드로 설정하는 방법이 있지만, 이 방법은 효율적인 방법이 아닙니다. 특히, `멀티 스레드` 환경에서는 오류를 발생하기 쉽습니다. 한 스레드가 `setter`를 호출하여 의존하는 인스턴스를 변경할 경우 다른 스레드에서는 의도치 않은 인스턴스를 의존하게 될 수도 있기 때문입니다.
* * *
## 개선 방식 - 의존 객체 주입
이때, 의존 객체 주입 방식을 활용하면 효과적으로 개선할 수 있습니다. 의존 객체 주입의 예시를 보겠습니다.
```java
public class Service {
    private final MemberRepository memberRepository;
    public class Service(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    ...
}

// 사용 예
Service memoryService = new Service(new MemoryMemberRepository());
Service h2Service = new Service(new H2MemberRepository());
```
* 의존 객체 주입 방식은 `인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식`입니다. (`setter 수정자 주입` 방식도 있으나, 언급한 문제점이 있고, 필요한 자원을 주입받지 않은 시점에서도 객체를 생성할 수 있다는 단점이 있습니다.)
* 생성 시점에 필요한 인스턴스를 주입받기 때문에 `Service`는 다양한 `MemberRepository`를 가질 수 있습니다.
* `OCP 원칙`과 `DIP 원칙` 또한 지킬 수 있게 되었습니다. `MemberRepository` 인스턴스를 변경하려고 할 시 `Service`에서는 변경이 일어나지 않게 되며, 추상적인 `MemberRepository`만 의존하여 `DIP 원칙`을 지킬 수 있습니다.
* 따라서 테스트할 때도 위의 두 방식보다 유연해졌으며, 객체 간의 결합도 또한 낮아졌습니다.
* 의존 객체 주입은 생성자 / 정적 팩터리 / 빌더에 응용될 수 있습니다.
* 외부에서 두 객체간의 관계를 결정해주는 방식입니다.
### 만약 필요한 의존 자원이 많다면?
의존 객체 주입 방식을 통해 개선되었지만, 의존하게 되는 자원이 많을 경우에는 코드가 길어지고 어지러워질 수도 있습니다. 그래서 대표적인 스프링과 같은 의존 객체 주입 프레임워크를 사용하면 이 문제 또한 해결할 수 있습니다.
* 의존성 주입은 스프링의 3가지 핵심 프로그래밍 모델 중 하나입니다.
* 스프링에서는 `@Autowired`를 통해 이 문제를 해결하도록 해 줍니다.
