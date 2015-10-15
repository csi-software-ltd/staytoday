<%@ page contentType="text/html;charset=UTF-8" %>
<html><head>
</head>
<body>
<g:datePicker name="date_start" value="${new Date()}" precision="day" relativeYears="[0..1]" noSelection="['':'-выберите-']"/>
<g:datePicker name="date_end" value="${new Date()+1}" precision="day" relativeYears="[0..1]" noSelection="['':'-выберите-']"/>
<br><br>
<!--<ul id="ul2"></ul>
<span id="test"></span>-->

</body>
</html>
