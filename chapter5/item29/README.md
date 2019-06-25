## 이왕이면 제네릭 타입으로 만들라.
```java
public class Stack {
   private Object[] elements;
   private int size = 0;
   private static final int DEFAULT_INITIAL_CAPACITY = 16;

   public Stack() {
      elements = new Object[DEFAULT_INITIAL_CAPACITY];
   }

   public void push(Object e) {
      ensureCapacity();
      elements[size++] = e;
   }

   public Object pop() {  // 형변환을 해줘야한다. 런타임시에 error발생의 여지가 있다.
      if (size == 0)
         throw new EmptyStackException();
      Object result = elements[--size];
      elements[size] = null;
      return result;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   private void ensureCapacity() {
      if (elements.length == size)
      elements = Arrays.copyOf(elements, 2 * size + 1);
   }
}
```

### 첫번째 해결
```java
public class Stack<E> {
   private E[] elements;
   private int size = 0;
   private static final int DEFAULT_INITIAL_CAPACITY = 16;

   // elements는 push method를 통해 넣을 수 있는건 E의 instance가 보장이된다.
   // 충분히 type safe하지만,
   // 런타임시에 Type은 E[]가 아니라 Object[]가 될 것이다.
   @SuppressWarnings("unchecked")
   public Stack() {
      elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
   }

   public void push(E e) {
      ensureCapacity();
      elements[size++] = e;
   }

   public E pop() {
      if (size == 0)
         throw new EmptyStackException();
      Object result = elements[--size];
      elements[size] = null;
      return result;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   private void ensureCapacity() {
      if (elements.length == size)
      elements = Arrays.copyOf(elements, 2 * size + 1);
   }
}
```

### 두번째 해결
```java
public class Stack<E> {
   private Object[] elements;
   private int size = 0;
   private static final int DEFAULT_INITIAL_CAPACITY = 16;

   public Stack() {
      elements = new Object[DEFAULT_INITIAL_CAPACITY];
   }

   public void push(E e) {
      ensureCapacity();
      elements[size++] = e;
   }

   // @SuppressWarnings("unchecked")의 적절한 위치
   public E pop() {
      if (size == 0)
         throw new EmptyStackException();

      // E로 push하니 적절한 cast가 가능
      @SuppressWarnings("unchecked")
      E result = (E) elements[--size];
      elements[size] = null; 
      return result;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   private void ensureCapacity() {
      if (elements.length == size)
      elements = Arrays.copyOf(elements, 2 * size + 1);
   }
}
```
 - 첫번째 방식은 배열 생성시 단 한번만 형변환을 하지만, 두번째 방식에서는 배열에서 원소를 읽을 때마다 해줘야한다.
 - 첫번째 방식은 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염을 일으킨다.