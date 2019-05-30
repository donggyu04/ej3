package com.example.item5;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericBuilder<T> {
    private final Supplier<T> supplier;
    private List<Consumer<T>> setters = new ArrayList<>();

    private GenericBuilder(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public static <T> GenericBuilder<T> of(Supplier<T> instance) {
        return new GenericBuilder<>(instance);
    }
    
    public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        setters.add(setter -> consumer.accept(setter, value));
        return this;
    }
    
    public T build() {
        T instance = supplier.get();
        setters.forEach(s -> s.accept(instance));
        return instance;
    }
}
