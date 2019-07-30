# 다른 타입이 적절하다면 문자열 사용을 피하라

문자열은 다른 값 타입을 대신하기에 적합하지 않다. 많은 사람들이 파일, 네트워크, 키보드 입력으로부터 데이터를 받을 때 주로 문자열을 사용한다. 기본 타입이든 참조 타입이든 적절한 타입이 있다면 그것을 사용하고, 
* 받은 데이터가 수치형이라면 int, float, BigInteger 등 적당한 수치 타입으로 변환해야한다.
* 받은 데이터가 '예/아니오' 질문의 답이라면 enum이나 boolean으로 변환해야한다.
* 문자열은 열거 타입을 대신하기에 적합하지 않다.
* 문자열은 혼합 타입을 대신하기에 적합하지 않다. (ex. `String compoundKey = className + "#" + i.next();`)
* 문자열은 권한을 표현하기에 적합하지 않다.

## 갑자기 분위기 스레드 로컬

**문자열을 사용해 권한을 구분**
```java
public class ThreadLocal {
    private ThreadLocal() { } // 객체 생성 불가

    // 현재 스레드의 값을 키로 구분해 저장
    public static void set(String key, Object value);

    // 현재 스레드의 값 반환
    public static Object get(String key);
}
```

**Key 클래스로 권한을 구분**
```java
public class ThreadLocal {
    private ThreadLocal() { }   // 객체 생성 불가

    public static class Key {   // 권한
        Key() { }
    }

    // 위조 불가능한 고유 키를 생성한다.
    public static Key getKey() {
        return new Key();
    }

    public static void set(Key key, Object value);
    public static Object get(Key key);
}
```

**리팩터링하여 Key를 ThreadLocal로 변경**
```java
public final class ThreadLocal {
    public ThreadLocal();
    public void set(Object value);
    public Object get();
}
```

**매개변수화하여 타입 안정성 확보**
```java
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```

## 개인적인 의견
이번 챕터에서 다루는 내용은 너무 당연한 이야기이고, 저도 동의하는 내용이지만, 응답의 empty 유무만 확인하면 되는 경우라면 굳이 파싱하지 않아도 되지 않을까 싶습니다.

갑자기 스레드 로컬을 구현하는데 이게 무슨 전개인지 모르겠고 스레드 로컬의 구현에 대해서 깊게 다루는것도 아니고 뭔지 모르겠네요.