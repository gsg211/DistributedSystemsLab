<html xmlns:jsp="http://java.sun.com/JSP/Page">
	<head>
		<title>Gasire student</title>
		<meta charset="UTF-8" />
	</head>
	<body>
		<h3>Cauta student in baza de date</h3>
		Introduceti datele despre student:
		<form action="./find-student" method="post">
			Nume: <input type="text" name="nume" />
			<button type="submit" name="submit">Trimite</button>
		</form>
	</body>
</html>