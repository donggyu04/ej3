# 20. 추상 클래스보다는 인터페이스를 우선하라

1. 기존 클래스에도 손 쉽게 새로운 인터페이스를 구현해 넣을 수 있다.
    - 인터페이스가 요구하는 메서드를 구현하고, 클래스 선언에 Implements 구문만 넣어주면 끝.
    - 이미 Java에서는 Comparable, Iterable, AutoCloseable 인터페이스가 새로 추가 됐을 때 수많은 기존 클래스들이 이 인터페이스를 구현한 채 릴리즈 됐음. (추상 클래스로는 힘든일)

2. 인터페이스는 믹스인(mixin) 정의에 안성맞춤이다.
    - 대상의 주된 기능에 선택적으로 기능을 '혼합'하는 것.
    - Comparable (클래스에 순서를 정할 수 있는 기능을 혼합 할 수 있음)
    - 클래스 계층구조에서는 mixin을 집어넣기에 합리적인 위치가 없다.


3. 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.

``` java
interface Singer {
	void sing();
}

interface SongWriter {
	void compose();
}

class SingerSongWriter implements Singer, SongWriter {

	@Override
	public void compose() {
		
	}
	
	@Override
	public void sing() {
		
	}
	
}
```
> 이런 구조를 클래스로 만드려면 가능한 조합 전부를 각가의 클래스로 정의한 고도비만 클래스가 만들어질 수 있다.

4. 인터페이스와 추상 골격 구현 클래스를 함께 제공하는 식으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다.
    - 인터페이스로는 타입을 정의하고, 필요하면 디폴트 메서드들까지 구현한다.
    - 골격 구현 클래스는 나머지 메서드들까지 구현한다.
    - 사용자는 골격 구현 클래스를 확장해서 사용함으로써 인터페이스를 구현하는데 필요한 대부분의 일을 완료할 수 있음(템플릿 메서드 패턴)



5. 골격 구현 클래스 AbstractList 사용 예
``` java
public class Main {
	public static void main(String[] args) {
		List<Integer> list = intArrayAsList(new int[] {1, 2, 3, 4, 5, 6});
	}
	
	static List<Integer> intArrayAsList(int[] arr) {
		Objects.requireNonNull(arr);
		return new AbstractList<Integer>() {
			@Override
			public Integer get(int index) {
				return arr[index];
			}
			
			@Override
			public Integer set(int index, Integer newVal) {
				int oldVal = arr[index];
				arr[index] = newVal;
				return oldVal;
			}

			@Override
			public int size() {
				return arr.length;
			}
		};
	}
}
```

> 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적합하다. 복잡한 인터페이스라면 구현하는 수고를 덜어줄 수 있는 골격 구현을 함꼐 제공하는 방법을 꼭 고려해 보자. 골격 구현은 **가능한 한** 인터페이스의 디폴트 메서드로 제공하여 그 인터페이스를 구현하는 모든 곳에서 활용하도록 하는것이 좋다.
