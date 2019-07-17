## 반환 타입으로는 스트림보다 컬렉션이 낫다.
Stream 인터페이스틑 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라, Iterable 인터페이스가 정의한 방식대로 동작한다. 그럼에도 forEach로 스트림을 반복할 수 없는 이유는 Stream이 Iterable을 확장하지 않아서다.

```java
for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) { 
    // 로직
}
^Test.java:6: error: method reference not expected here
for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
```
 - 자바 타입 추론의 한계로 발생한 이슈이므로 cast해보자.
```java
for (ProcessHandle ph : (Iteable<ProcessHandle>)ProcessHandle.allProcesses()::iterator) { 
    // 로직
}
```
 - 이를 어댑터 메서드를 사용해서 더 개선 해보자.
 
```java
     public static <E> Iterable<E> iterableOf(Stream<E> stream) {
         return new Iterable<E>() { 
         			@NotNull
         			@Override
         			public Iterator<E> iterator() { //it looks like functional interface like Supplier<Iterator<E>>
         				return stream.iterator();
         			}
         		};
  }
```
```java
  public static <E> Iterable<E> iterableOf(Stream<E> stream) {
  		Iterable<E> iterable =  () -> stream.iterator(); 
  		return iterable;
  	}
```
```java
  //method reference를 적용
     public static <E> Iterable<E> iterableOf(Stream<E> stream) {
         return stream::iterator;
  }
```
 - 스트림을 사용하고 싶은 사용자를 위해 Iterable을 Stream으로 변환해주는 어댑터도 제공해주자.
```java
   public static <E> Stream<E> streamOf(Iterable<E> iterable) {
       return StreamSupport.stream(iterable.spliterator(), false); //it is not parallel
}
```

#### 위 과정을 해줄바엔 Collection이나 그 하위 타입을 사용해 스트림과 Iterable을 쉽게 제공해주자.

#### 반환해주는 Collection 크기가 클 경우, 새로운 Collection을 구현해서 반환하자.
```java
	public class PowerSet { // 멱함수
		public static final <E> Collection<Set<E>> of(Set<E> s) {
			List<E> src = new ArrayList<>(s);
			if (src.size() > 30)
				throw new IllegalArgumentException("Set too big " + s);
			return new AbstractList<Set<E>>() {
				@Override
				public int size() {
					return 1 << src.size();
				}

				@Override
				public boolean contains(Object o) {
					return o instanceof Set && src.containsAll((Set) o);
				}

				@Override
				public Set<E> get(int index) {
					Set<E> result = new HashSet<>();
					for (int i = 0; index != 0; i++, index >>= 1)
						if ((index & 1) == 1)
							result.add(src.get(i));
					return result;
				}
			};
		}
	}
```
### 1. 웬만하면 반환 타입으로는 스트림보다는 Collection이 낫다.
### 2. Collection이 너무 클 경우 자체 Collection을 구현하라.
### 3. 위 2개가 쉽지않다면, Iterable과 Stream의 반환을 고려하라.
> 그냥 자바에서 Stream이 Iterable이 될때까지 기다리자...