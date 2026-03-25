import requests
from flask import Flask, jsonify, render_template, request, redirect, url_for, session
app = Flask(__name__, template_folder="templates")


class HtmlService:
    def getLoginPage(self):
        return render_template('login.html')

    def getRegisterPage(self):
        return render_template('register.html')

    def getExpensesPage(self, expenses_list):
        return render_template('expenses.html', expenses=expenses_list)

    def getErrorPage(self, message):
        return render_template('error.html', error=message)

    def getNewExpensePage(self):
        return render_template('add_expense.html')

class ApiManager:
    def __init__(self):
        self.__sessionTokenString = ""
        self.AUTH_URL = "http://auth_service:5003"
        self.EXPENSE_URL = "http://expenses_service:5004"

    def login(self, username, password):
        response = requests.post(f"{self.AUTH_URL}/login",
                                 json={"username": username, "password": password})
        if response.status_code == 200:
            self.__sessionTokenString = response.json().get('token')
            return True, self.__sessionTokenString
        return False, None

    def register(self, username, fullname, password):
        response = requests.post(f"{self.AUTH_URL}/register",
                                 json={"username": username, "fullname": fullname, "password": password})
        return response.status_code == 201

    def newExpense(self, token, data):
        headers = {"Authorization": f"{token}"}
        response = requests.post(f"{self.EXPENSE_URL}/expenses", json=data, headers=headers)
        return response.status_code == 201

    def getAllExpenses(self, token):
        headers = {"Authorization": f"{token}"}
        response = requests.get(f"{self.EXPENSE_URL}/expenses", headers=headers)
        if response.status_code == 200:
            return response.json()
        return []


class FlaskWebService:
    def __init__(self):
        self.app = Flask(__name__)
        self.__htmlService = HtmlService()
        self.__apiService = ApiManager()
        self.setup_routes()

    def setup_routes(self):
        @self.app.route('/')
        def login_route():
            return self.__htmlService.getLoginPage()

        @self.app.route('/register', methods=['GET', 'POST'])
        def register_route():
            if request.method == 'POST':
                success = self.__apiService.register(
                    request.form['username'],
                    request.form['fullname'],
                    request.form['password']
                )
                return redirect(url_for('login_route')) if success else self.__htmlService.getErrorPage(
                    "Registration Failed")
            return self.__htmlService.getRegisterPage()

        @self.app.route('/do-login', methods=['POST'])
        def do_login():
            success, token = self.__apiService.login(request.form['username'], request.form['password'])
            if success:
                session['token'] = token
                return redirect(url_for('expenses_route'))
            return self.__htmlService.getErrorPage("Invalid Credentials")

        @self.app.route('/expenses')
        def expenses_route():
            if 'token' not in session: return redirect(url_for('login_route'))
            data = self.__apiService.getAllExpenses(session['token'])
            return self.__htmlService.getExpensesPage(data)

        @self.app.route('/add-expense', methods=['GET', 'POST'])
        def add_expense_route():
            if 'token' not in session: return redirect(url_for('login_route'))
            if request.method == 'POST':
                expense_data = {"type": request.form['type'], "cost": float(request.form['cost'])}
                self.__apiService.newExpense(session['token'], expense_data)
                return redirect(url_for('expenses_route'))
            return self.__htmlService.getNewExpensePage()

    def run(self):
        self.app.run(host='0.0.0.0', port=5001)


if __name__ == "__main__":
    web_service = FlaskWebService()
    web_service.run()