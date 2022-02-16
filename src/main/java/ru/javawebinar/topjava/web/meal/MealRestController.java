package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        final int userId = authUserId();
        log.info("Create meal: {}; user id: {}", meal, userId);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public void delete(int id) {
        final int userId = authUserId();
        log.info("Delete meal with id: {}; user id: {}", id, userId);
        service.delete(id, userId);
    }

    public Meal get(int id) {
        final int userId = authUserId();
        log.info("get meal: {}; user id: {}", id, userId);
        return service.get(id, userId);
    }

    public void update(Meal meal, int id) {
        final int userId = authUserId();
        log.info("update meal: {}; user id: {}", meal, userId);
        assureIdConsistent(meal, id);
        service.update(meal, userId);
    }

    public List<MealTo> getAll() {
        return service.getAll(authUserId(), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public List<MealTo> getAllByDate(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        return service.getAllByDate(authUserId(), startDate,startTime, endDate,endTime, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }
}