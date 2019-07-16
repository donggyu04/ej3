# 표준 함수형 인터페이스를 사용하라

람다를 지원하면서 코드 모범사례도 크게 바뀌었다.
상위클래스의 기본 클래스를 재정의 하여 원하는 동작을 하게 만드는 템플릿 메서드 패턴의 매력이 크게 줄어
템플릿 메소드 패턴의 매력이 감소하는 대신 함수 객체를 받아 제공하는 것이 떠오르고 있다.

다만...
필요한 용도에 맞는게 있다면 직접 구현하지 말고, 
표준 함수형 인터페이스(java.util.function)를 사용하길 바랍니다.
그럼 함수형 인터페이스란 무엇인지 알아보겠습니다.

### 함수형 인터페이스(Functional Interface)

우리는 이미 저번시간에 함수형 인터페이스에 대해서 알아보았습니다.
이번에는 java8에서 기본적으로 제공해주는 표준 함수형 인터페이스(Functional Interface)들을 알아보겠습니다.

일단..

예를 들어 Collection<E> 인터페이스에 정의되어 있는 디폴트메소드인 ```removeif```를 살펴보겠습니다.

```java
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
```

우선 매개변수의 의미부터 알아보겠습니다.

Predicated<? super E> filter에서 Predicated는 제네릭으로 선언되어 있는 함수형 인터페이스입니다.

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

위 처럼 자바에서는 메소드의 반환형과 매개변수 선언에 차이를 둔 다양한 함수형 인터페이스들을 표준으로 정의하고 있습니다.
표준으로 정의된 대표적인 인터페이스 네개와 그안에 위치한 추상메소드는 다음과 같으며,
이것들을 java.util.function 패키지에 묶여있습니다.


| 인터페이스명 | 메소드명  | 설명  | 
|---|---|---|
| Runnable | void run() | 실행할 수 있는 인터페이스 | 
| Supplier | T get() | 제공할 수 있는 인터페이스 | 
| Consumer | void accept(T t) | 소비할 수 있는 인터페이스 | 
| Function<T, R> | R apply (T t) | 입력을 받아서 출력할 수 있는 인터페이스 | 
| Predicate | Boolean test(T t | )입력을 받아 참, 거짓을 판단할 수 있는 인터페이스 | 
| UnaryOperator | T apply(T t) | 단항 연산할 수 있는 인터페이스 | 

단항은 Unary, 이항 Binary 이러한 식으로
(기본타입별 LongToIntFunction, LongBinaryOperator, BooleanSupplier 등)
위 표를 보면 43가지는 충분히 유츄가 가능합니다.

표준 함수형 인터페이스 대부분은 기본타입만 지원합니다. 
(LongToIntFunction.applyAsInt는 long 인수를 받고 int를 반환)

```
Predicated<T> ->boolean test(T t)

Supplier<T> ->T get()

Consumer<T>-> void accept(T t)

Function<T,R> -> R apply(T t)
```

1) Runnable

기존부터 존재하던 인터페이스로 스레드를 생성할때 주로사용하였으며 가장 기본적인 함수형 인터페이스다. 
void 타입의 인자없는 메서드를 갖고있다.

```java
Runnable r = () -> System.out.println("hello functional");
r.run();
```

2) Predicate<T>

하나의 인자와 리턴타입을 가진다. 
Function과 비슷해보이지만 리턴타입을 지정하는 타입 파라미터가 안보인다. 
반환타입은 boolean 타입으로 고정되어있고, Function<T, Boolean>형태라고 보면된다.

```java
public class hello {
    public static int sum(Predicate<Integer> p ,List<Integer> list) {
			int s=0;
			for(int n:list) {
				if(p.test(n))
					s+=n;
			}
			return s;
		}
	
	public static void main(String[] args) {
		List<Integer> list=Arrays.asList(1,5,7,9,11,12);
		int s;

		s=sum(n->n%2==0,list);
		System.out.println("짝수 합 : "+s);
		
        s=sum(n->n%2!=0,list);
		System.out.println("홀수 합 : "+s);
	}
}
```

위예제를 분석해보면 다음과 같다.

Predicate<Integer> p의 구현으로 ```n->n%2==0 와 n->n%2!=0``` 라는 구현이 람다식을 인자가 전달되었다.
즉, 함수형 인터페이스의 구현으로 ```n->n%2==0 와 n->n%2!=0``` 라는 람다식이 부여된것이다.

따라서 인자 n에 대해서 ```n%2==0 or n%2!=0``` 라는 매개변수가 전달되었다. 

앞서 Predicate<T>에는 boolean test(T t)라는 추상메소드가 존재하며, 
전달된 인자(n%2==0 ,n%2!=0)를 대상으로 true와 false를 판단한다.

#### Predicate<T>를 구체화하고 다양화 한 인터페이스들

Predicate<T>에서 T를 다음과 같이 기본자료형으로 결정하여 정의한 인터페이스들도 존재한다.
이들은 함수형 인터페이스지만, 제네릭은 아니다.

```java
IntPredicate -> boolean test(int value)

LongPredicate -> boolean test(long value)

DoublePredicate - > boolean test(double value)
```
​

그리고 Predicate<T>와 달리 두개의 인자를 받아서 true 또는 false를 결정할 수 있는 다음 제네릭 인터페이스도 있다.

​```BiPredicate<T,U> -> boolean test(T t,U u)```

3) Supplier<T>

Supplier<T> 인터페이스에는 다음 추상메소드가 존재한다.

```java
T get(); // 단순히 무엇인가 반환할 때
````

인자는 받지않으며 리턴타입만 존재하는 메서드를 갖고있다. 

```java
Supplier<String> s = () -> "hello supplier";
String result = s.get();
```

좀더 예를 보면 아래와 같다.
```java
    public static int sum(Predicate<Integer> p ,List<Integer> list) {
        int s=0;
        for(int n:list) {
            if(p.test(n))
                s+=n;
        }
        return s;
    }

    public static List<Integer> makeIntList(Supplier<Integer> s,int n){
        List<Integer> list=new ArrayList<>();
        
        for(int i=0;i<n;i++)
            list.add(s.get());
        
        return list;
    }

	public static void main(String[] args) {
		Supplier<Integer> spr=()->{
			Random rand=new Random();
			return rand.nextInt(50);
		};
		
		List<Integer> list=makeIntList(spr,5);
		System.out.println(list);
	}
```

4) Consumer<T>

리턴을 하지않고(void), 인자를 받는 메서드를 갖고있다. 
인자를 받아 소모한다는 뜻으로 인터페이스 명칭을 이해하면 된다.
아래와 같은 추상 메서드가 존재합니다.

```java
void accept(T t); // 전달된 인자를 기반으로 '반환' 이외의 다른 결과를 보일 때
```

따라서 전달된 인자를 가지고 어떤 결과를 보여야 할때 유용하게 사용할 수 있습니다.

```java
Consumer<String> c = str -> System.out.println(str);
c.accept("hello consumer");
c.accept("hello EJ3");
```

5) Function<T, R>

인터페이스 명칭에서부터 알 수 있듯이 전형적인 함수를 지원한다고 보면 됩니다.
하나의 인자와 리턴타입을 가지며 그걸 제네릭으로 지정해줄수있다. 
그래서 타입파라미터(Type Parameter)가 2개 입니다.

```java
R apply(T t); // 전달인자와 반환값이 모두 존재할때
```

이렇듯 Function<T,R>의 추상메소드는 전달인자와 반환값이 있는 가장 보편적인 형태입니다.

```java
Function<String,Integer> f=s->s.length();
System.out.println(f.apply("test"));

Function<String, Integer> f = str -> Integer.parseInt(str);
Integer result = f.apply("1");
```

6) UnaryOperator<T>

하나의 인자와 리턴타입을 가진다. 
그런데 제네릭의 타입 파라미터가 1개인걸 보면 감이 오는가? 
인자와 리턴타입의 타입이 같아야한다.

```java
UnaryOperator<String> u = str -> str + " operator";
String result = u.apply("hello unary");
```

7) BinaryOperator<T>

동일한 타입의 인자 2개와 인자와 같은 타입의 리턴타입을 가진다.

```java
BinaryOperator<String> b = (str1, str2) -> str1 + " " + str2;
String result = b.apply("hello", "binary");
```

8) BiPredicate<T, U>

서로 다른 타입의 2개의 인자를 받아 boolean 타입으로 반환한다.

```java
BiPredicate<String, Integer> bp = (str, num) -> str.equals(Integer.toString(num));
boolean result = bp.test("1", 1);
```

9) BiConsumer<T, U>

서로 다른 타입의 2개의 인자를 받아 소모(void)한다.

```java
BiConsumer<String, Integer> bc = (str, num) -> System.out.println(str + " :: " + num);
bc.accept("숫자", 5);
```

10) BiFunction<T, U, R>

서로 다른 타입의 2개의 인자를 받아 또 다른 타입으로 반환한다.

```java
BiFunction<Integer, String, String> bf = (num, str) -> String.valueOf(num) + str;
String result = bf.apply(5, "678");
```

11) Comparator<T>

자바의 전통적인 인터페이스 중 하나이다. 객체간 우선순위를 비교할때 사용하는 인터페이스인데 전통적으로 1회성 구현을 많이 하는 인터페이스이다. 람다의 등장으로 Comparator의 구현이 매우 간결해져 Comparable 인터페이스의 실효성이 많이 떨어진듯 하다.

```java
Comparator<String> c = (str1, str2) -> str1.compareTo(str2);
int result = c.compare("aaa", "bbb");
```

### 이제 다시 remveIf 메소드를 사용해보자

앞서 함수형 인터페이스를 설명하면서 Collection<E> 인터페이스에 정의되어 있는 디폴트메소드를 알아보았습니다.

```java
default boolean removeif(Predicate<? super E> filter)
```
​
따라서 ArraList<Intger> 인스턴스를 생성하면 , 그안에 존재하는 removeIf 메소드의 E는 Integer로 결정된다.

그렇다면 removeIf의 기능은 무엇일까?

​

"컬렉션인스턴스에 저장된 인스턴스를 다음 test메소드의 인자로 전달했을때, true가 반환되면 인스턴스를 모두 삭제한다" 

이렇게 표준 함수형 인터페이스를 사용할수 있는건 사용하고
없는건 새로 만들어 쓰도록 하자.


참고 : 
https://sjh836.tistory.com/173 [빨간색코딩]
https://multifrontgarden.tistory.com/125 [우리집앞마당]
https://m.blog.naver.com/hoyo1744/221581648297


