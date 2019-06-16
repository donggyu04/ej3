# 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라.

1. 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지(자기사용) 문서로 남겨야 한다. (18장에서 add를 재정의 하는것이 addAll에 영향을 준다는 것을 명시 해야함)

2. 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 protected메서드 형태로 공개해야 할 수도 있다.

3. 상속용 클래스를 시험하는 방법은 직접 하위 클래스를 만들어보는 것이 '유일'하다, 상속용으로 설계한 클래스는 배포 전에 **반드시 하위 클래스를 만들어 검증**해야 한다.

4. 상속용 클래스의 생성자는 직접적으로든 재정의 가능 메서드를 호출해서는 안 된다. (프로그램이 오작동 할 수 있음)

``` java
public class Main {
	public static void main(String[] args) {
		Sub sub = new Sub();
		sub.overrideMe();
	}
}

class Super {
	public Super() {
		overrideMe();
	}
	
	public void overrideMe() {
		
	}
}

class Sub extends Super{
	private final Instant instant;
	
	public Sub() {
		instant = new Instant();
	}
	
	@Override
	public void overrideMe() {
		instant.printHi();
	}
}

class Instant {
	public void printHi() {
		System.out.println("Hi!");
	}
}
```

5. clone, readObject 모두 직접적으로든 간접적으로든 재정의 가능 메서드를 호출해서는 안 된다.

6. 상속용으로 설계하지 않은 클래스는 상속을 금지하라.
    - 클래스를 final 클래스로 선언.
    - 생성자를 private, package-private으로 선언하고 public 정적 팩터리를 만들어 제공.

7. 구체 클래스에 꼭 상속을 허용하려면 **클래스 내부에서는 재정의 가능 메서드를 사용하지 않게 만들고** 이를 문서화 하라.
    - private 도우미 메서드 정의 (클래스 내부에서 재정의 가능 메서드를 호출하는 곳에서 private 도우미 메서드를 호출하도록 수정)






