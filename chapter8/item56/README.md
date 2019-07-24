## 공개된 API 요소에는 항상 문서화 주석을 작성하라
### 공개 API를 올바르게 문서화 하기
- 모든 클래스, 인터페이스, 메서드, 필드에 문서화 주석을 달아야 한다
- 직렬화 할 수 있는 클래스라면 직렬화 형태도 적어야한다
- Thread safe 여부를 반드시 포함해야 한다
- 기본 생성자에는 문서화 주석을 달 수 없음으로 공개 클래스에는 절대 기본 생성자를 사용하면 안된다 
    - 기본생성자는 파라미터 0개인 생성자를 말하는게 아니라 컴파일러가 자동으로 만들어주는 생성자 
- 어떻게(How)가 아니라 무엇을(What)을 기술해야 한다 
- 제네릭에 대한 문서화를 할때는 모든 타입 매개변수에 주석을 달아야한다
    ````java
    /**
    * An object that ...
    * ....
    * 
    * @Param <K> the type of keys maintained by this map
    * @Param <V> the type of mapped values
    */
    public interface Map<K, V> {
      
    }
    ````
- Enum을 문서화 할 때는 상수에도 주석을 달아야한다 
    ````java
    /**
    *  An instrument section of a symphony orchestra.
    */
    public enum OrchestraSection {
        /** Woodwinds, such as flute...*/
        WOODWIND,
    
        /** Brass instruments...*/
        BRASS,
    
        /** String instruments...*/
        STRING,
    }
    ````
- 어노테이션을 문서화 할때는 모든 멤버에 주석을 달아야 한다
- 패키지를 설명하는 주석은 `package-info.java`에 작성한다
    - 자바9 부터 지원하는 모듈 시스템을 사용한다면 `module-info.java`

