package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> usersMeals = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, SecurityUtil.authUserId()));
    }

    @Override
    public boolean delete(int id, int userId) {
        final Map<Integer, Meal> userMeals = usersMeals.get(userId);
        return userMeals != null && userMeals.remove(id) != null;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = usersMeals.computeIfAbsent(userId, u -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
            usersMeals.put(userId, userMeals);
            return meal;
        }
        return userMeals.computeIfPresent(meal.getId(), (id, old) -> meal);
    }

    @Override
    public Meal get(int id, int userId) {
        final Map<Integer, Meal> userMeals = usersMeals.get(userId);
        return userMeals == null ? null : userMeals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return predicateFilter(userId, meal -> true);
    }

    private List<Meal> predicateFilter(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = usersMeals.get(userId);
        if (userMeals == null) {
            return Collections.emptyList();
        }
        return userMeals.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

