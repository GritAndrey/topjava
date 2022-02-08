package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryMealStorage implements MealStorage {
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(Meal meal) {
        if (meal.getId() == null) {
            meal.setId(counter.incrementAndGet());
            storage.put(meal.getId(), meal);
        }
        return meal;
    }

    @Override
    public Meal get(Integer id) {
        return storage.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Integer id) {
        storage.remove(id);
    }
}
