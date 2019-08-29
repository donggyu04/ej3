## 일반적으로 통용되는 명명 규칙을 따르라.
### 절차 규칙
 - 패키지
    - 조직 바깥에서도 사용할 패키지라면 조직의 인터넷 도메인 이름을 역순으로 사용한다. (com.navercorp)
    - 패키지 이름의 나머지는 해당 패키지를 설명하는 하나 이상의 요소로 이루어진다.
       - 일반적으로 8자 이하의 짧은 단어로 한다.(utilities -> util)
 - 클래스, 인터페이스
    - 대문자로 시작
    - 통용되는 줄임말을 제외하곤(mix, max) 약어를 사용하지 않는다.
    - 약어의 경우 첫 글자만 대문자로 하는 쪽이 훨신 많다.(Url)
 - 메서드, 필드
    - 첫글자를 소문자로 쓴다는 점만 제외하고는 클래스 명명규칙과 같다.
    - 상수 필드는 모두 대문자로 시작하며 단어 사이 구분은 언더바(_)로 한다.
 - 타입 매개변수 이름은 보통 한 문자로 표현한다.
    - T 타입
    - E 컬렉션 원소
    - K,V 맵의 키와 값
    - X 예외
    - R 메서드의 반환 타입
    - T, U, V 혹은 T1, T2, T3 타입의 시퀀스
### 문법 규칙
 - 클래스
    - 객체를 생성할 수 있는 클래스의 이름은 보통 단수 명사나 명사구(Thread, PriorityQueue)
    - 객체를 생성할 수 없는 클래스의 이름은 보통 복수형 명사(Collectors, Collections)
    - 인퍼테이스 이름은 클래스와 똑같이 짓거나(Collection, Comparator), able 혹은 ible로 끝나느 형용사(Runnable, Accessible)
 - annotation
    - 지배적인 규칙이 없어 명사, 동사, 전치사, 형용사가 두루 쓰인다.
 - 메서드
   - 어떤 동작을 수행하는 메서드의 이름은 동사나 동사구(append, drawImage)
   - boolean을 반환하는 메서드라면 보통 is, has로 시작하고 명사나 명사구, 형용사(isDigit)
   - 반환 타입이 boolean이 아니거나 해당 인스턴스의 속성을 반환한다면 명사, 명사구, get으로 시작하는 동사구(size, hashCode, getTime)
   - 객체의 타입을 바꿔서 다른 타입의 또 다른 객체를 반환하는 인스턴스 메서드의 이름은 보통 toType(toString, toArray)
   - 객체의 내용을 다른 뷰로 보여주는 메서드의 이름은 주로 asType(asList)
   - 객체의 값을 기본 타입 값으로 반환하는 메서드의 이름은 보통 typeValue(intValue)
   - 정적 팩터리의 이름은 다양하지만 from, of, valueOf, instance, getInstance, newInstance, getType, newType
   