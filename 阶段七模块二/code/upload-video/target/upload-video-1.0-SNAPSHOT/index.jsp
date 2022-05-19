<%--
  Created by IntelliJ IDEA.
  User: lujin
  Date: 2022/5/16
  Time: 14:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传视频</title>
</head>
<body>
<form action="/upload" method="post" enctype="multipart/form-data">
    <input id="uploadVideos" type="file" name="videoList" multiple="multiple">
    <br>
    <button type="submit">提交</button>
</form>
</body>
</html>
