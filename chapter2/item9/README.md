# Try-Finally 대신 Try-with-Resource를 사용하라.

자바 라이브러리에는 InputStream, OutputStream 그리고 java.sql.Connection과 같이 정리(close)가 가능한 리소스가 많은데, 
그런 리소스를 사용하는 클라이언트 코드가 보통 리소스 정리를 잘 안하거나 잘못하는 경우가 있다.

```java
public class FirstError extends RuntimeException {
}
```

```java
public class SecondException extends RuntimeException {
}
```

```java
public class MyResource implements AutoCloseable {

    public void doSomething() throws FirstError {
        System.out.println("doing something");
        throw new FirstError();
    }

    @Override
    public void close() throws SecondException {
        System.out.println("clean my resource");
        throw new SecondException();
    }
}
```

```java
MyResource myResource = null;
try {
    myResource = new MyResource();
    myResource.doSomething();
} finally {
    if (myResource != null) {
        myResource.close();
    }
}
```

위 코드에서는 close만 호출이 되어집니다.

2007년 Java 라이브러리 안의 close 메서드 중 2/3는 틀렸었습니다.
아래는 사용 예시이다.

```java
static String firstLineOffFile(String path) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(path));
	try {
		return br.readLine();
	} finally {
		br.close();
	}
}
```
=>
```java
static String firstLineOffFile(String path) throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
		return br.readLine();
	}
}
```

중첩 TRY CATCH

```java
static void copy(String src, String dst) throws IOException {
	InputStream in = new FileInputStream(src);
	try {
		OutputStream out = new FileOutputStream(dst);
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			int n;

			while ((n = in.read(buf)) >= 0)
				out.write(buf, 0, n);
		} finally {
			out.close();
		}
	} finally {
		in.close();

	}
}
```
=>
```java
static void copy(String src, String dst) throws IOException {
    try (InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) >= 0)
            out.write(buf, 0, n);
    }
}
```

### supress 된 Exception 까지 보여주는 장점

```java
public class SuppressedExceptionTest {

	public static void main(String[] args) {
		MyResource resource = new MyResource();

		try  {
			resource.read();
		} finally {
			resource.close();
		}
	}
	
	static class MyResource implements AutoCloseable {

		public void read() {
			throw new RuntimeException("cannot read");

		}
		@Override
		public void close() {
			throw new RuntimeException("cannot close");

		}
	}
}
```

```
Exception in thread "main" java.lang.RuntimeException: cannot close
	at item9.SuppressedExceptionTest2$MyResource.close(SuppressedExceptionTest2.java:22)
	at item9.SuppressedExceptionTest2.main(SuppressedExceptionTest2.java:11)
```

try-with-resource를 쓰면

```java
public class SuppressedExceptionTest {

	public static void main(String[] args) {
		try (MyResource resource = new MyResource()) {
			resource.read();
		}
	}


}
```

```
Exception in thread "main" java.lang.RuntimeException: cannot read
	at item9.SuppressedExceptionTest$MyResource.read(SuppressedExceptionTest.java:14)
	at item9.SuppressedExceptionTest.main(SuppressedExceptionTest.java:7)
	Suppressed: java.lang.RuntimeException: cannot close
		at item9.SuppressedExceptionTest$MyResource.close(SuppressedExceptionTest.java:19)
		at item9.SuppressedExceptionTest.main(SuppressedExceptionTest.java:8)
```

### 정리해 보면.
- AutoClosable을 구현한 클래스를 자동으로 닫음.
- 더 간결하고 안전한 코드
- supress 된 Exception까지 보여주는 장점.
- 닫아야 만하는 리소스로 작업 할 때는 항상 try-with-resources 사용하자.
