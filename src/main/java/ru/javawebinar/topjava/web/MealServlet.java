package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.storage.MemoryMealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    public static final int CALORIES_PER_DAY = 2000;
    private static final Logger LOG = LoggerFactory.getLogger(MealServlet.class);
    private static final MealStorage storage = new MemoryMealStorage();

    static {
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, 0));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, 1));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, 2));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, 3));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, 4));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, 5));
        storage.save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, 6));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("action");
        if (operation == null) {
            LOG.info("Get all meals from storage");
            request.setAttribute("meals", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), CALORIES_PER_DAY));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
        switch (Objects.requireNonNull(operation)) {
            case "delete":
                String mealID = request.getParameter("id");
                LOG.info("Delete meal: {} ", mealID);
                storage.delete(Integer.parseInt(mealID));
                response.sendRedirect("meals");
                return;
            case "update":
                mealID = request.getParameter("id");
                LOG.info("Update meal: {} ", mealID);
                Meal meal = storage.get(Integer.parseInt(mealID));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("editMeal.jsp").forward(request, response);
                break;
            case "add":
                meal = new Meal(LocalDateTime.now(), "", 0);
                LOG.info("Add new");
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("editMeal.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect("meals");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("date") + 'T' + request.getParameter("time"));
        final String description = request.getParameter("description");
        final int calories = Integer.parseInt(request.getParameter("calories"));
        String mealID = (request.getParameter("id"));
        final Meal meal = new Meal(dateTime, description, calories, mealID == null || mealID.isEmpty() ? null : Integer.parseInt(mealID));
        storage.save(meal);
        response.sendRedirect("meals");
    }
}
