# 2. 생성자에 매개변수가 많다면 빌더를 고려하자.

식품 포장의 영양 정보를 표현하는 클래스를 만들어보자.

1. 점층적 생성자 패턴(Telescoping constructor pattern)
   - 원하는 매개변수를 가지도록 생성자를 오버로딩 하는 방법.
   - 인스턴스 생성 시 원하는 매개변수를 모두 포함한 생성자중 가장 짧은 것을 골라 호출한다.
   - ``` NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);```
   - 매개변수 개수가 많아지면 클라이언트 코드를 작성하기가 힘들고, 버그 발생 가능성이 커진다.

2. 자바빈즈 패턴(JavaBeans pattern)
   - 매개변수가 없는 생성자로 객체를 만든 후 setter를 통해 값을 설정하는 방식.
   - ```java
     NutritionFacts cocaCola = new NutritionFacts();
     cocaCola.setServingSize(240);
     cocaCola.setServings(8);
     cocaCola.setCalories(100);
     cocaCola.setSodium(35);
     cocaCola.setCarbohydrate(27);
      ```
   - 객체 하나를 만들기 위해 여러개의 메서드를 호출 해야한다.
   - 객체가 완전히 생성되기 전까지 일관성(consistency)가 무너진 상태에 놓인다.
   - 스레드 안정성을 얻으러면 추가 작업 필요(freeze)

3. 빌더 패턴(Builder pattern)
   - 위 두가지 패턴의 장점만 취함.
   - ``` NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).carbohydrate(27).build(); ```
   - 계층적으로 설계된 클래스와 함께 쓰기에 좋다.

> **생성자나 정적 팰터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.** 매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다. 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바빈즈보다 훨씬 안전하다.