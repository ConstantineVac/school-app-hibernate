<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cities Search and Insert</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/school/static/css/citiesmenu.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/school/static/js/citiesmenu.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

</head>
<body>
<div class="center">
    <p>Hello ${sessionScope.loginName}</p>
</div>

<div class="center">
    <!-- Cities Search -->
    <div class="search-wrapper">
        <div class="bot-gap">
            <span class="title">Cities Search</span>
        </div>
        <div class="bot-gap">
            <form method="POST" action="${pageContext.request.contextPath}/schoolapp/searchCity">
                <input name="name" type="text" class="search rounded" placeholder="Insert city name" autofocus/>
                <br><br>
                <button class="search-btn rounded color-btn" type="submit">Search</button>
            </form>

        </div>
    </div>

    <!-- Cities Insert -->
    <div class="insert-wrapper">
        <div class="bot-gap">
            <span class="title">Cities Insert</span>
        </div>
        <div class="bot-gap">
            <form method="POST" action="${pageContext.request.contextPath}/schoolapp/cityInsert">
                <input name="name" type="text" value="${requestScope.insertedCity.name}" class="insert rounded" placeholder="City name" autofocus required/><br>
                <br>
                <button class="search-btn rounded color-btn" type="submit">Insert</button>
            </form>

        </div>
    </div>
</div>

<div class="center">
    <c:if test="${requestScope.sqlError}">
        <p>${requestScope.message}</p>
    </c:if>
</div>

<div class="center">
    <c:if test="${requestScope.citiesNotFound}">
        <p>No cities found</p>
    </c:if>

    <p>${requestScope.error}</p>
</div>
<div class="center">
    <a href="${pageContext.request.contextPath}/school/static/templates/controlPanel.jsp" class="back-btn">
        <i class="fas fa-arrow-left"></i> Back to Control Panel
    </a>
</div>

</body>
</html>