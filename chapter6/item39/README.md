# 39. 명명 패턴보다 애너테이션을 사용하라.

1. 예전 Junit에서는 테스트 메서드 이름을 `test`로 시작하게 끔 했다.
    - 오타로 메서드 이름을 `tsetSafety`와 같이 만들면 Junit은 해당 메서드를 무시해버림.
    - 올바른 프로그램 요소에서만 사용되리라 보증 할 방법이 없음.
    - 프로그램 요소를 매개변수를 전달 할 마땅한 방법이 없다.

2. Annotation이 `1.`의 모든 문제점을 해결해 준다.

    - `@Retention` : `SOURCE, CLASS, RUNTIME` 3가지 존재.
        - `SOURCE` : 컴파일러로부터 버려진다.
        - `CLASS`(default) : 컴파일러로부터 .class 파일에 기록 되지만 runtime시 JVM으로부터 유지되지는 않는다
        - `RUNTIME` : .class파일에 기록되고, JVM에 의해 유지된다. 리플랙티브 하게 읽을 수 있음.

    - `@Target` : Annotation 적용 대상을 지정.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```
    
3. Custom Annotation을 처리하는 코드 예
    - Java reflection을 사용.
    - `m.isAnnotationPresent(Test.class)` 를 통해 `@Test`가 메서드에 적용됐는지를 판단.
```java
public class Sample {
    @Test
    public static void m1() { }        // Test should pass
    public static void m2() { }
    @Test public static void m3() {    // Test should fail
        throw new RuntimeException("Boom");
    }
    public static void m4() { }  // Not a test
    @Test public void m5() { }   // INVALID USE: nonstatic method
    public static void m6() { }
    @Test public static void m7() {    // Test should fail
        throw new RuntimeException("Crash");
    }
    public static void m8() { }
}
```
```java
// args로 Sample 클래스의 fully qualified name을 전달.
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " failed: " + exc);
                } catch (Exception exc) {
                    System.out.println("Invalid @Test: " + m);
                }
            }
        }
        System.out.printf("Passed: %d, Failed: %d%n",
                passed, tests - passed);
    }
}
```

4. 매개변수를 받는 Annotation

- 단일 혹은 배열로 받음

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
    // Class<? extends Exception>[] value();
}
```
```java
@ExceptionTest(ArithmeticException.class)
// @ExceptionTest({IndexOutOfBoundsException.class, NullPointerException.class})
```

- 반복가능 Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```
```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
```

### 핵심정리
1. **Annotation으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다.**
2. Annotation 사용 방식에 따라 이를 선언하고 처리하는 부분에서의 코드 양이 늘거나 복잡해 질 수 있기 때문에 이 부분들을 잘 고려해서 만들자.