# 지연 초기화는 신중히 사용하라
**지연 초기화란?**
* 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법이다
* 주로 최적화 용도로 사용된다
* 인스턴스 초기화 때 발생하는 순환 문제를 해결하기 위해서 사용되기도 한다

**결론**
* 다른 최적화 기법과 마찬가지로 **필요할 때까지는 하지 말아라**.
* 대부분의 상황에서 일반적인 초기화가 지연 초기화보다 낫다

멀티스레드 환경에서는 지연 초기화를 하기가 까다롭다. 지연 초기화 대상 필드가 다수의 스레드에서 공유된다면 반드시 동기화해야한다. 스레드 안전하게 지연 초기화하는 방식들에 대해서 알아본다.

### 1. 인스턴스 필드를 초기화하는 일반적인 방법
```java
private final FieldType field = computeFieldValue();
```
해당 클래스 인스턴스 생성시 `computeFieldValue()` 메서드가 무조건 호출된다.

### 2. 인스턴스 필드의 지연 초기화
인스턴스 필드 초기화가 초기화 순환성을 깨뜨릴 것 같으면 synchronized 키워드를 사용한 접근자를 사용하자.
```java
private Field field;

private synchronized FieldType getField() {
    if (field == null) {
        field = computeFieldValue();
    }
    return field;
}
```
### 3. 지연 초기화 홀더 클래스 관용구
성능 때문에 정적 필드를 지연 초기화해야 한다면 **지연 초기화 홀더 클래스(lazy initialization holder class)** 관용구를 사용하자.

```java
private static class FieldHolder {
    static final Field field = computeFieldValue();
}

private static FieldType getField() {
    return FieldHolder.field;
}
```
getField가 처음 호출되는 순간 FieldHolder 클래스가 최초 로드되면서 FieldHolder.field가 초기화된다. 추가적인 검사 또는 동기화가 전혀 필요 없으니 성능상 손해가 전혀 없다는것이 장점이다.

### 4. 인스턴스 필드 지연 초기화용 이중검사 관용구
성능 때문에 인스턴스 필드를 지연 초기화해야 한다면 **이중검사(double-check)** 관용구를 사용하라.

```java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result != null) {   // 첫 번째 검사 (락 사용 안함)
        return field;
    }

    synchronized(this) {    // 두 번째 검사 (락 사용)
        if (field == null) {
            field = computeFieldValue();
        }
        return field;
    }
}
```
관용구 이름 대로 두 번 검사하는 과정을 거친다.

첫 번째는 초기화 여부를 동기화 없이 검사하고, 두 번째는 동기화하여 검사한다. 두 번째 검사에서도 필드가 초기화되지 않았을 때만 필드를 초기화한다. 필드가 초기화된 이후엔 동기화하지 않으므로 해당 필드를 반드시 volatile로 선언해야한다. 

지역 변수 `result`를 사용하면 코드가 복잡해지지만 초기화 이후에는 그 필드를 딱 한 번만 읽도록 보장한다. 지역 변수를 사용하지 않을때 보다 더 빠르다.

필드를 반복해서 초기화해도 상관없는 경우 두 번째 검사를 생략한 **단일 검사(single-check)** 관용구를 사용하자.
```java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result == null) {
        field = result = computeFieldValue();
    }
    return result;
}
```