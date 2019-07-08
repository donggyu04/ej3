# 37. ordinal 인덱싱 대신 EnumMap을 사용하라.

ordianl() : `Returns the ordinal of this enumeration constant (its position in its enum declaration, where the initial constant is assigned an ordinal of zero).`


1. 정원에 심은 식물들을 배열 하나로 관리하고, 이들을 생애주기(LifeCycle) 단위로 묶어보자!

``` java
class Plant {
	enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL };
	final String name;
	final LifeCycle lifeCycle;
	
	Plant(String name, LifeCycle lifeCycle) {
		this.name = name;
		this.lifeCycle = lifeCycle;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
```

 - 생애 주기 단위로 묶고, 출력.

``` java
Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];

for (int i = 0; i < plantsByLifeCycle.length; i++) {
    plantsByLifeCycle[i] = new HashSet<>();
}

for (Plant plant : garden) {
    plantsByLifeCycle[plant.lifeCycle.ordinal()].add(plant); 
}

for (int i = 0; i < plantsByLifeCycle.length; i++) {
    System.out.printf("%s: %s\n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
}
```
```
ANNUAL: [p6, p9, p0, p3]
PERENNIAL: [p4, p1, p7]
BIENNIAL: [p2, p5, p8]
```

 - 문제점
    1. 형변환을 수행해야 한다.
    2. 배열은 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
    3. 정확한 정숫값 사용을 직접 보증해야 한다.

2. EnumMap을 사용하여 위의 코드를 보안해보자!
> EnumMap : enum을 key로 사용하도록 설계 된 아주 빠른 Map

``` java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lifeCycle: Plant.LifeCycle.values()) {
    plantsByLifeCycle.put(lifeCycle, new HashSet<>());
}

for (Plant plant : garden) {
    plantsByLifeCycle.get(plant.lifeCycle).add(plant);
}

System.out.println(plantsByLifeCycle);
```
```
{ANNUAL=[p6, p9, p0, p3], PERENNIAL=[p4, p1, p7], BIENNIAL=[p2, p5, p8]}
```

3. 두 열거타입 값을 매핑하기 위해 ordinal을 쓴 배열들의 배열이 있을때는 ordinal을 사용하지 말고 EnumMap 2개를 중첩하여 해결하자.

### 핵심정리
> 배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않으나, 대신 EnumMap을 사용하라. 다차원 관계는 EnumMap<..., EnumMap<...>>으로 표현하라.
"Application 프로그래머는 Enum.ordinal을 (웬만해서는) 사용하지 말라."


