<!DOCTYPE html>
<html>
    <head>
		<title>Login success</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
		<h1>Login success : <%= ((User)session.getAttribute("user")).getUsername() %></h1>
    </body>
	<footer>
	</footer>
</html>
