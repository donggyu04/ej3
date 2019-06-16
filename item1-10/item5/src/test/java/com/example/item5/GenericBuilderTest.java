package com.example.item5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GenericBuilderTest {
    @Test
    public void testFactoryMethodPattern() {
        Person p = GenericBuilder.of(Person::new)
                .with(Person::setAge, 28)
                .with(Person::setName, "imb")
                .with(Person::setCompany, "NTS")
                .with(Person::setAddress, "Hanam")
                .build();

        assertEquals(p.getAge(), 28);
        assertEquals(p.getName(), "imb");
        assertEquals(p.getCompany(), "NTS");
        assertEquals(p.getAddress(), "Hanam");
    }
}
