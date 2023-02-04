# 아이템 20 - 추상 클래스보다는 인터페이스를 우선하라

자바의 다중 구현 메커니즘은 인터페이스와 추상 클래스가 있다.
이 둘은 유사하지만, 공통점과 차이점을 이해하고 적절히 사용해야 한다.

## 인터페이스의 장점

- 인터페이스는 클래스를 설계하는데 유용하다.
  - 구현해야 할 메서드를 정의하면, 인터페이스를 구현하는 클래스는 인터페이스에 정의된 메서드를 자유롭게 구현할 수 있다.
- 인터페이스는 다중 상속을 지원한다.

## 인터페이스와 추상 골격(skeletal) 클래스

인터페이스와 추상 골격 클래스는 유사하지만, 추상 골격 클래스는 일부 메서드를 구현하며 인터페이스는 구현하지 않는 차이점이 있다.

## 템플릿 메서드 패턴

템플릿 메서드 패턴은 추상 클래스에서 구현되며, 이 패턴을 사용하면 클래스의 구조를 정의하면서도 특정 메서드를 구체적으로 구현하는 클래스를 만들 수 있습니다. 이 패턴을 사용하면 서브클래스를 만들 때 클래스의 구조를
변경하지 않고도 특정 메서드를 구체적으로 구현할 수 있습니다.

## 디폴트 메서드와 Object 메서드

디폴트 메서드는 인터페이스에서 기본 구현을 제공하는 메서드를 의미한다.

이를 통해 인터페이스를 구현하는 클래스가 필요없는 기능을 구현할 필요가 없어진다.

하지만, Object 메서드는 java.lang.Object 클래스에 정의된 메서드들로, 이 메서드들은 디폴트 메서드로 제공할 수 없다.

## 디자인 패턴 찍먹 - 상속과 합성을 곁들인..

디자인 패턴에서 가장 중요한 패턴이 무엇이냐고 묻는다면, 바로 추상 팩토리 (메서드) 패턴이다. 그 다음은 커맨드 패턴이다.

### 상속과 합성

왜 템플릿 메서드 패턴 얘기 안하고, 추상 팩토리 메서드 패턴얘기냐 할 수 있지만, 사실 템플릿 메서드의 특수화된 케이스가 팩토리 메서드 패턴이기 때문이다. 따라서 템플릿 메서드 패턴을 이해하면 팩토리 메서드 패턴도
이해할 수 있다.

템플릿 메서드 패턴은 상속을 기반으로 한다. 이에 대척하는 패턴은 바로 전략 패턴이다. 전략 패턴은 합성을 기반으로 한다.

> 잠깐 정리하자면
> 템플릿 메서드 --(특수화)-> 팩토리 메서드 --(집합)-> 추상 팩토리 메서드
> - 템플릿 메서드 패턴: 알고리즘 구조를 정의하고, 구체적인 구현을 서브클래스에서 하도록 한다.
> - 팩토리 메서드 패턴: 객체 생성을 서브클래스에서 하도록 한다.
> - 추상 팩토리 패턴: 서로 관련이 있는 객체들을 생성하는 팩토리를 정의하고, 서브클래스에서 구체적인 팩토리를 생성하도록 한다.
> - 전략 패턴: 알고리즘을 캡슐화하고, 서브클래스에서 알고리즘을 선택하도록 한다.

템플릿 메서드 패턴은 상속 중 유일하게 추상메서드를 통하여 부모-자식 간 의존성을 역전 시킨다.

### 템플릿 메서드 패턴의 주의할 점

템플릿 메서드 패턴은 상속을 이용했기 때문에 자식이 늘어나는 만큼 조합의 수가 늘어나게 되고, 이를 조합 폭발이라고 한다.

#### 예제

```java
public abstract class Pizza {
    public void prepare() {
        System.out.println("prepare");
    }

    public void bake() {
        System.out.println("bake");
    }

    public void cut() {
        System.out.println("cut");
    }

    public void box() {
        System.out.println("box");
    }
}

public class CheesePizza extends Pizza {
    @Override
    public void prepare() {
        System.out.println("prepare cheese pizza");
    }
}

public class ClamPizza extends Pizza {
    @Override
    public void prepare() {
        System.out.println("prepare clam pizza");
    }
}

public class PizzaStore {
    public Pizza orderPizza(String type) {
        Pizza pizza = createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }

    protected abstract Pizza createPizza(String type);
}

public class NYPizzaStore extends PizzaStore {
    @Override
    protected Pizza createPizza(String type) {
        if (type.equals("cheese")) {
            return new CheesePizza();
        } else if (type.equals("clam")) {
            return new ClamPizza();
        } else {
            return null;
        }
    }
}

public class ChicagoPizzaStore extends PizzaStore {
    @Override
    protected Pizza createPizza(String type) {
        if (type.equals("cheese")) {
            return new CheesePizza();
        } else if (type.equals("clam")) {
            return new ClamPizza();
        } else {
            return null;
        }
    }
}
```

어마어마하게 늘어나겠죠?

### 전략 패턴의 주의할 점

전략 패턴은 합성을 이용했기 때문에 자식이 늘어나는 만큼 조합의 수가 늘어나지 않는다. 하지만 런타임에 필요한 객체를 주입해야 하기 때문에, 객체를 생성하는 시점에 알아야 하는 경우의 수만큼 주입을 해줘야 한다.
이를 조합 폭발이라고 한다.

#### 예제

```java
public class Pizza {
    private final Dough dough;
    private final Sauce sauce;
    private final Cheese cheese;
    private final Veggies[] veggies;
    private final Pepperoni pepperoni;
    private final Clams clam;

    public Pizza(Dough dough, Sauce sauce, Cheese cheese, Veggies[] veggies, Pepperoni pepperoni, Clams clam) {
        this.dough = dough;
        this.sauce = sauce;
        this.cheese = cheese;
        this.veggies = veggies;
        this.pepperoni = pepperoni;
        this.clam = clam;
    }
}

public class CheesePizza extends Pizza {
    public CheesePizza(Dough dough, Sauce sauce, Cheese cheese, Veggies[] veggies, Pepperoni pepperoni, Clams clam) {
        super(dough, sauce, cheese, veggies, pepperoni, clam);
    }
}

public class ClamPizza extends Pizza {
    public ClamPizza(Dough dough, Sauce sauce, Cheese cheese, Veggies[] veggies, Pepperoni pepperoni, Clams clam) {
        super(dough, sauce, cheese, veggies, pepperoni, clam);
    }
}

public class PizzaStore {
    public Pizza orderPizza(String type) {
        Pizza pizza = createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }

    protected abstract Pizza createPizza(String type);
}

public class NYPizzaStore extends PizzaStore {
    @Override
    protected Pizza createPizza(String type) {
        if (type.equals("cheese")) {
            return new CheesePizza();
        } else if (type.equals("clam")) {
            return new ClamPizza();
        } else {
            return null;
        }
    }
}

public class ChicagoPizzaStore extends PizzaStore {
    @Override
    protected Pizza createPizza(String type) {
        if (type.equals("cheese")) {
            return new CheesePizza();
        } else if (type.equals("clam")) {
            return new ClamPizza();
        } else {
            return null;
        }
    }
}
```

어질어질하지만 얘는 그래도 해결가능해보이죠..?

### 정리

추가 공부는 상속 라인은 팩토리 메서드, 추상 팩토리, 빌더 패턴 쪽으로 보시면 될 것 같고, 조합 라인은 커맨드 패턴, 상태 패턴 쪽으로 보시면 될 것 같습니다.
