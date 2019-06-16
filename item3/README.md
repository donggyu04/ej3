# 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라.

싱글턴 만드는 방식

1. public static final 필드 방식
   - ``` java
     public static final Elvis INSTANCE = new Elvis();
     private Elvis() {}

     // usage
     Elvis elvis = Elvis.INSTANCE;
     ```

2.  정적 팩터리 방식
    - ``` java
      private static final Elvis INSTANCE = new Elvis();
      private Elvis() {} 
      public static Elvis getInstance() { return INSTANCE; }

      // usage
      Elvis elvis = Elvis.getInstance();
      ```

3. 열거 타입 방식
    - 원소가 하나인 열거 타입을 이용.
    - ``` java
      public enum Elvis { INSTANCE; }
      Elvis elvis = Elvis.INSTANCE;
      ```
     - 대부분 상황에서 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.
     - 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법을 사용할 수 없다.(인터페이스 구현은 가능)

4. 기타
    - 생성자를 private으로 한다고 해도 reflection을 사용하여 생성자를 호출 할 수 있음.
    - Serializable을 구현하여 싱글턴 객체를 직렬화 하는 경우 readResolve()를 객체에 추가해 줘야한다. 이렇게 하지 않으면 직렬화된 인스턴스를 역 직렬화 할때마다 새로운 인스턴스가 만들어진다.
    - 열거타입 방식을 사용하면 위 두가지 상황을 막아준다.

>참고하면 좋을만한 글:  https://blog.seotory.com/post/2016/03/java-singleton-pattern