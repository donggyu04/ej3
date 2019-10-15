# Serializable을 구현할지는 신중히 결정하라

## Serializable

클래스의 인스턴스를 직렬화 할 수 있게 하려면 클래스 선언에 implements Serializable을 추가하면 된다. 

```java
public class Member implements Serializable {
    private String name;
    private String email;
    private int age;
 
    public Member(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
    @Override
    public String toString() {
        return String.format("Member{name='%s', email='%s', age='%s'}", name, email, age);
    }
}
```

아주 손쉬워 보이지만 길게 보면 아주 값비싼 일이다. 

## 역직렬화시 클래스 구조 변경 문제

예제에서 Member 클래스가 있습니다. 이 클래스의 객체를 직렬화 시켜보겠습니다.
아래에의 문자열은 직렬화된 Member 클래스의 객체 문자열입니다. 테스트에 용의 하도록 Base64로 인코딩하였습니다.

```java
Base64.getEncoder().encodeToString(serializedMember);
```

```java
rO0ABXNyABp3b293YWhhbi5ibG9nLmV4YW0xLk1lbWJlcgAAAAAAAAABAgAESQADYWdlSQAEYWdlMkwABWVtYWlsdAASTGphdmEvbGFuZy9TdHJpbmc7TAAEbmFtZXEAfgABeHAAAAAZAAAAAHQAFmRlbGl2ZXJ5a2ltQGJhZW1pbi5jb210AAnquYDrsLDrr7w=
```

이 문자열을 바로 역직렬화 시키면 바로 Member 객체로 변환합니다. (테스트할 때에는 반드시 패키지도 동일해야 합니다.)

Member 클래스의 구조 변경에 대한 문제를 확인해보겠습니다.

```java
    public class Member implements Serializable {
        private String name;
        private String email;
        private int age;
        // phone 속성을 추가
        private String phone;
      // 생략
    }
```

이전에 자바 직렬화된 데이터를 역직렬화 시켜 보겠습니다.

```java
 java.io.InvalidClassException: woowahan.blog.exam1.Member; local class incompatible: stream classdesc serialVersionUID = -8896802588094338594, local class serialVersionUID = 7127171927132876450
 ```

이렇게 클래스의 멤버 변수 하나만 추가되어도 java.io.InvalidClassException 예외가 발생합니다.
예외 메시지를 읽어보면 serialVersionUID의 정보가 일치하지 않기 때문에 발생한 것을 알 수 있습니다.
우리는 Member 클래스에서는 serialVersionUID 의 값을 -8896802588094338594 정보로 설정해준 적도 없으며, 7127171927132876450으로 변경한 적도 없습니다. 어떻게 된 일일까요?


그래서 자바 직렬화 스펙을 확인해보았습니다.
```
 It may be declared in the original class but is not required. 
    The value is fixed for all compatible classes. 
    If the SUID is not declared for a class, the value defaults to the hash for that class. 
```

- SUID(serialVersionUID) 필수 값은 아니다.
- 호환 가능한 클래스는 SUID값이 고정되어 있다.
- SUID가 선언되어 있지 않으면 클래스의 기본 해쉬값을 사용한다. (해쉬값 알고리즘은 링크에서 확인이 가능합니다.)

serialVersionUID 를 직접 기술하지 않아도 내부 적으로 serialVersionUID 정보가 추가되며,
내부 값도 자바 직렬화 스펙 그대로 자동으로 생성된 클래스의 해쉬 값을 이라는 것을 확인할 수 있었습니다

즉 serialVersionUID 정보를 기술하지 않는다고 해서 사용하지 않는 것이 아니 다라는 것이 확인되었습니다.

그럼 어떤 형태가 좋을까요?

```java
public class Member implements Serializable {
        private static final long serialVersionUID = 1L;
      
        private String name;
        private String email;
        private String phone;
      // 생략
    }
```

“조금이라도 역직렬화 대상 클래스 구조가 바뀌면 에러 발생해야 된다.” 정도의 민감한 시스템이 아닌 이상은 클래스를 변경할 때에
직접 serialVersionUID 값을 관리해주어야 클래스 변경 시 혼란을 줄일 수 있습니다.


클래스가 Serializable을 구현하면 직렬화된 바이트 스트림 인코딩(직렬화 형태)도 하나의 공개 API 된다. 따라서 커스텀 직렬화 형태를 설계하지 않고 자바의 기본 방식을 사용하면 직렬화 형태는 최소 적용 당시 클래스의 내부 구현 방식에 영원히 묶여 버린다. 

## Serializable 구현의 문제점

- Serializable을 구현하면 릴리스한 뒤에는 수정하기 어렵다.
- 버그와 보안 구멍이 생길 위험이 높아진다.
- 해당 클래스의 신버전을 릴리스할 때 테스트할 것이 늘어난다는 점이다.

## 정리

긴 시간 동안 외부에 저장하는 의미 있는 데이터들은 자바 직렬화를 사용하지 말자...