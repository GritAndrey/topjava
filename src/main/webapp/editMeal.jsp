<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Create/update meal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>

    <form method="post" action="meals">
        <input class="form-control" type="hidden" name="id" value="${meal.id}">
        <dl>
            <dt>DateTime:</dt>
            <dd><input
                    class="form-control"
                    type="date"
                    value="${meal.dateTime.toLocalDate()}"
                    name="date"
                    required></dd>
            <dd><input
                    class="form-control"
                    type="time"
                    value="${meal.dateTime.toLocalTime()}"
                    name="time"
                    required></dd>
        </dl>
        <dl>
            <dt>Description:</dt>
            <dd><input
                    class="form-control"
                    type="text"
                    value="${meal.description}"
                    name="description"
                    required></dd>
        </dl>
        <dl>
            <dt>Calories:</dt>
            <dd><input
                    class="form-control"
                    type="number"
                    name="calories"
                    value="${meal.calories == "0" ? "" : meal.calories}"
                    min="1"
                    required>
        </dl>
        <button class="btn-primary" type="submit">Save</button>
        <button class="btn-secondary" onclick="window.history.back()" type="button">Cancel</button>
    </form>
</div>
</body>
</html>