# 21. 인터페이스는 구현하는 쪽을 생각해 설계하라.

1. 생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어려운 일이다.
    - Java 8 이전에는 기존 구현체를 깨뜨리지 않고 인터페이스에 메서드를 추가할 방법이 없었음. (compime 에러가 무조건 발생하므로)
    - 위의 제약을 해결하기 위해서 디폴트 메서드가 Java 8에서 추가 됨.
    - 디폴트 메서드를 재 정의하지 않은 클래스에서는 모두 인터페이스의 디폴트 구현이 쓰이게 됨
    - 디폴트 메서드를 구현클래스에 대해 아무것도 모른 채 합의 없이 '무작정' 삽입 될 뿐이다.

2. 디폴트 메서드는 (컴파일에 성공하더락도) 기존 구현체에 런타임 오류를 일으킬 수 있다.
    - Java 8은 Collection 인터페이스에 디폴트 메서드를 추가했고, 그 결과 기존에 짜여진 많은 자바 코드들이 영향을 받은 것으로 알려짐 (apache SyncronizedCollection.removeIf())
    - 기존 인터페이스에 디폴트 메서드를 추가하는 건 꼭 필요한 경우가 아니라면 심사숙고 해야한다.
    - 새로운 인터페이스 설계시에는 표준적인 메서드 구현을 제공하는데 아주 유용한 수단이다.
    
3. 디폴트 메서드가 추가됨으로 인하여 기존에 만들어진 클래스가 의도치 않게 동작할 수 있는 예
``` java
class LicensePrefixPrinter implements Printer {
	@Override
	public void printHello() {
		System.out.println("[Apache License] Hello");
	}

	@Override
	public void printHi() {
		System.out.println("[Apache License] Hi");
	}
}

interface Printer {
	public void printHello();
	public void printHi();

	// Added this default method after LicensePrefixPrinter release.
	default public void printNice() {
		System.out.println("Nice");
	}
}
```

> 인터페이스를 릴리즈 한 후라도 결함을 수정하는게 가능한 경우도 있겠지만, 절대 그 가능성에 기대서는 안 된다. (반드시 사전 테스트를 거쳐야 함)


