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
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> filteredMeals = new ArrayList<>(meals.size());
        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>(meals.size());
        Map<LocalDate, Integer> caloriesDateGrouped = new HashMap<>(2 * meals.size());

        for (UserMeal ml : meals) {
            if (TimeUtil.isBetweenHalfOpen(ml.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredMeals.add(ml);
            }
            final LocalDate mlDate = ml.getDate();
            caloriesDateGrouped.put(mlDate, caloriesDateGrouped.getOrDefault(mlDate, 0) + ml.getCalories());
        }
        for (UserMeal filteredMeal : filteredMeals) {
            mealWithExcesses.add(new UserMealWithExcess(
                    filteredMeal.getDateTime(),
                    filteredMeal.getDescription(),
                    filteredMeal.getCalories(),
                    caloriesDateGrouped.get(filteredMeal.getDate()) > caloriesPerDay)
            );
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
