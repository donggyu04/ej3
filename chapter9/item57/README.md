# 57. 지역변수의 범위를 최소화 하라.

1. 지역변수의 범위를 줄이는 가장 강력한 기법은 **"가장 처음 쓰일 때"** 선언 하기다.
2. 거의 모든 지역변수는 선언과 동시에 **초기화** 해야한다.
   - 초기화에 필요한 정보가 충분하지 않다면 충분해질 때까지 선언을 미뤄야 한다.
3. 변수를 초기화하는 표현식에서 검사 예외를 던질 가능성이 있다면 try-catch 문을 사용해야 한다.
4. 변수 값을 try 블록 바깥에서도 사용해야 한다면 (정확한 값으로 초기화를 못하더라도) try 블록 앞에 선언해야 한다.

```java
Class<? extends Set<String>> cl = null;

try {
  cl = (Class<? extends Set<String>>) Class.forName(args[0]);
} catch (ClassNotFoundException e) {
  // Handle exception..
}
```

5. 컬랙션이나 배열을 순회할때는 for-each를 사용해라.

```java
for (Element e : c) {
  ...
}
```

6. 반복자를 사용할때는 정통적인 for 문을 쓰는 것이 좋다.

```java
for (Iterator<Element> i = c.iterator(); i.hasNext();) {
  Element e = i.next();
}
```

```java
Iterator<Element> i = c.iterator();
while (i.hasNext()) {
  doSomething(i.next());
}

// has a bug!
Iterator<Element> i2 = c2.iterator();
while (i.hasNext()) {
  doSomething(i2.next());
}
```

```java
// 지역변수의 범위를 최소화하는 또 다른 반복문 관용구
for (int i = 0, n = expensiveComputation(); i < n; i++) {

}
```

7. 메서드를 작게 유지하고 한 가지 기능에 집중하여 지역변수 범위를 최소화하라.