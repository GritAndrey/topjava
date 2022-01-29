package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static Map<LocalDate, Integer> caloriesDateGrouped = new HashMap<>();

    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByRecursion(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        if (meals.isEmpty()) {
            return mealWithExcesses;
        }
        caloriesDateGrouped.merge(meals.get(0).getDate(), meals.get(0).getCalories(), Integer::sum);
        mealWithExcesses.addAll(filteredByRecursion(meals.subList(1, meals.size()), startTime, endTime, caloriesPerDay));
        UserMeal meal = meals.get(0);
        if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
            mealWithExcesses.add(new UserMealWithExcess(
                    meal.getDateTime(),
                    meal.getDescription(),
                    meal.getCalories(),
                    caloriesDateGrouped.get(meal.getDate()) > caloriesPerDay)
            );
        }
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesDateGrouped = new HashMap<>();
        for (UserMeal ml : meals) {
            final LocalDate mlDate = ml.getDate();
            caloriesDateGrouped.put(mlDate, caloriesDateGrouped.getOrDefault(mlDate, 0) + ml.getCalories());
        }

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealWithExcesses.add(new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesDateGrouped.get(meal.getDate()) > caloriesPerDay)
                );
            }
        }
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesDateGrouped = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate,
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExcess(m.getDateTime(), m.getDescription(), m.getCalories(),
                        caloriesDateGrouped.get(m.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
