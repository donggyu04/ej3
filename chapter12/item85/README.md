# 자바 직렬화의 대안을 찾으라

객체 직렬화란? 
- 자바 직렬화란 자바 시스템 내부에서 사용되는 객체 또는 데이터를 외부의 자바 시스템에서도 사용할 수 있도록 바이트(byte) 형태로 데이터 변환하는 기술과 바이트로 변환된 데이터를 다시 객체로 변환하는 기술(역직렬화)을 아울러서 이야기합니다.
- 시스템적으로 이야기하자면 JVM(Java Virtual Machine 이하 JVM)의 메모리에 상주(힙 또는 스택)되어 있는 객체 데이터를 바이트 형태로 변환하는 기술과 직렬화된 바이트 형태의 데이터를 객체로 변환해서 JVM으로 상주시키는 형태를 같이 이야기합니다.


## 자바 직렬화는 어떻게(how) 사용할 수 있나요?

#### 자바 직렬화 조건

자바 기본(primitive) 타입과 `java.io.Serializable` 인터페이스를 상속받은 객체는 직렬화 할 수 있는 기본 조건을 가집니다.

```java
package woowahan.blog.exam1;
    /**
    * 직렬 화할 회원 클래스
    */
    public class Member implements Serializable {
        private String name;
        private String email;
        private int age;
    
        public Member(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    
        // Getter 생략
    
        @Override
        public String toString() {
            return String.format("Member{name='%s', email='%s', age='%s'}", name, email, age);
        }
    }
```

#### 직렬화 방법

자바 직렬화는 방법은 `java.io.ObjectOutputStream` 객체를 이용합니다.

```java
Member member = new Member("김배민", "deliverykim@baemin.com", 25);
    byte[] serializedMember;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(member);
            // serializedMember -> 직렬화된 member 객체 
            serializedMember = baos.toByteArray();
        }
    }
    // 바이트 배열로 생성된 직렬화 데이터를 base64로 변환
    System.out.println(Base64.getEncoder().encodeToString(serializedMember));
}
```

위 예제에서 객체를 직렬 화하여 바이트 배열(byte []) 형태로 변환하였습니다.

## 자바 직렬화는 언제(when) 어디서(where) 사용되나요?

JVM의 메모리에서만 상주되어있는 객체 데이터를 그대로 영속화(Persistence)가 필요할 때 사용됩니다. 시스템이 종료되더라도 없어지지 않는 장점을 가지며 영속화된 데이터이기 때문에 네트워크로 전송도 가능합니다.
그리고 필요할 때 직렬화된 객체 데이터를 가져와서 역직렬 화하여 객체를 바로 사용할 수 있게 됩니다.
그런 특성을 살린 자바 직렬화는 많은 곳에서 이용됩니다. 많이 사용하는 부분 몇 개만 이야기해보겠습니다.

- 서블릿 세션 (Servlet Session)
서블릿 기반의 WAS(톰캣, 웹로직 등)들은 대부분 세션의 자바 직렬화를 지원하고 있습니다.
물론 단순히 세션을 서블릿 메모리 위에서 운용한다면 직렬화를 필요로 하지 않지만, 파일로 저장하거나 세션 클러스터링, DB를 저장하는 옵션 등을 선택하게 되면 세션 자체가 직렬화가 되어 저장되어 전달됩니다.
(그래서 세션에 필요한 객체는 java.io.Serializable 인터페이스를 구현(implements) 해두는 것을 추천합니다.)
참고로 위 내용은 서블릿 스펙에서는 직접 기술한 내용이 아니기 때문에 구현한 WAS 마다 동작은 달라질 수 있습니다.

- 캐시 (Cache)
자바 시스템에서 퍼포먼스를 위해 캐시(Ehcache, Redis, Memcached, …) 라이브러리를 시스템을 많이 이용하게 됩니다.
자바 시스템을 개발하다 보면 상당수의 클래스가 만들어지게 됩니다. 예를 들면 DB를 조회한 후 가져온 데이터 객체 같은 경우 실시간 형태로 요구하는 데이터가 아니라면 메모리, 외부 저장소, 파일 등을 저장소를 이용해서 데이터 객체를 저장한 후 동일한 요청이 오면 DB를 다시 요청하는 것이 아니라 저장된 객체를 찾아서 응답하게 하는 형태를 보통 캐시를 사용한다고 합니다.
캐시를 이용하면 DB에 대한 리소스를 절약할 수 있기 때문에 많은 시스템에서 자주 활용됩니다. (사실 이렇게 간단하진 않습니다만 간단하게 설명했습니다.)
이렇게 캐시 할 부분을 자바 직렬화된 데이터를 저장해서 사용됩니다. 물론 자바 직렬 화만 이용해서만 캐시를 저장하지 않지만 가장 간편하기 때문에 많이 사용됩니다.

- 자바 RMI(Remote Method Invocation)
최근에는 많이 사용되지 않지만 자바 직렬화를 설명할 때는 빠지지 않고 이야기되는 기술이기 때문에 언급만 하고 넘어가려고 합니다.
자바 RMI를 간단하게 이야기하자면 원격 시스템 간의 메시지 교환을 위해서 사용하는 자바에서 지원하는 기술입니다.
보통은 원격의 시스템과의 통신을 위해서 IP와 포트를 이용해서 소켓통신을 해야 하지만 RMI는 그 부분을 추상화하여 원격에 있는 시스템의 메서드를 로컬 시스템의 메서드인 것처럼 호출할 수 있습니다.
원격의 시스템의 메서드를 호출 시에 전달하는 메시지(보통 객체)를 자동으로 직렬화 시켜 사용됩니다.
그리고 전달받은 원격 시스템에서는 메시지를 역직렬화를 통해 변환하여 사용됩니다.
자세한 내용은 작은 책 한 권 정도의 양이 되기 때문에 따로 한번 찾아보시는 것을 추천드립니다.

## 직렬화 근본적인 문제

직렬화 근본적인 문제는 공격범위가 너무 넓고 지속적으로 더 넓어져 방어하기 어렵다는 점이다. 
예를 들어 `ObjectInputStream`의 `readObject` 메서드는 객체 그래프가 역직렬화된다. `readObject` 메서드는 (Serializable 인터페이스를 구현했다면) 클래스 패스 안의 거의 모든 타입의 객체를 만들어 낼 수 있는, 사실상 마법 같은 생성자다. 
바이트 스트림을 역직렬화 하는 과정에서 이 메서드는 그 타입들 안의 모든 코드를 수행할 수 있다. 
그 말인 즉슨, 그 타입들의 코드 전체가 공격 범위에 들어간다는 뜻이다. 



자바의 표준 라이브러리나 아파치 커먼즈 컬렉션 같은 서드 파티 라이브러리는 물론 애플리케이션 자신의 클래스들도 공격 범위에 포함된다. 

> 신뢰할 수 없는 스트림을 역직렬화하면 원격 코드 실행, 서비스 거부 등의 공격으로 이어질 수 있다. 
잘못한게 아무것도 없는 애플리케이션이라도 이런 공격에 취약해질 수 있다. 



결국, 직렬화 위험을 회피하는 가장 좋은 방법은 아무것도 직렬화 하지 않는 것이다. 



크로스 플랫폼 구조화된 데이터 표현의 선두주자는 `JSON`과 `프로토콜 버퍼`다. 
`JSON`은 더글라스 크록퍼드가 브라우저와 서버의 통신용으로 설계했고, 
`프로토콜 버퍼`는 구글이 서버 사이에 데이터를 교환하고 저장하기 위해 설계했다. 
보통은 이들을 언어 중립적이라고 하지만, JSON은 자바스크립트용으로, 프로토콜 버퍼는 C++용으로 만들어 졌고, 아직도 그 흔적이 남아 있다. 



둘의 가장 큰 차이는 JSON은 텍스트 기반이라 사람이 읽을 수 있고, 프로토콜 버퍼는 이진 표현이라 효율이 훨씬 높다는 점이다. 
또한 JSON은 오직 데이터를 표현하는 데만 쓰이지만 프로토콜 버퍼는 문서를 위한 스키마(타입)를 제공하고 올바로 쓰도록 강요한다. 효율은 프로토콜 버퍼가 훨씬 좋지만 텍스트 기반 표현에는 JSON 이 아주 효과적이다. 또한, 프로토콜 버퍼는 이진 표현 뿐아니라 사람이 읽을 수 있는 텍스트 표현(pbtxt)도 지원한다. 

## 핵심정리

직렬화는 위험하니 피해야 합니다.
시스템 밑바닥부터 설계를 하였다면 `JSON`과 `프로토콜 버퍼`와 같은 대안을 사용하도록 합니다.
신뢰할수 없는 데이터는 역직렬화하지 말고, 꼭 해야 한다면 객체 역직렬화 필터링을 사용하되 이 마저도 모든 공격을 막아줄수 없음을 기억해야 합니다.

