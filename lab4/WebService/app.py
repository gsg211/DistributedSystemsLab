import requests
from flask import Flask, render_template, request, redirect, url_for


# 1. Componenta de vizualizare
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

    def getHomePage(self):
        return render_template('home.html')


# 2. Componenta de comunicare API
class ApiManager:
    def __init__(self):
        # Porturile interne Docker pentru comunicare între containere
        self.AUTH_URL = "http://auth-service:8080"
        self.EXPENSE_URL = "http://expense-service:8080"

    def login(self, username, password):
        try:
            # Trimitem datele către Kotlin (folosim userName cum cere codul tău de Java)
            response = requests.post(f"{self.AUTH_URL}/login",
                                     json={"userName": username, "password": password}, timeout=5)
            if response.status_code == 200:
                # Extragem tokenString din răspunsul JSON
                return True, response.json().get('tokenString')
        except Exception as e:
            print(f"Eroare API Login: {e}")
        return False, None

    def register(self, username, fullname, password):
        try:
            # Trimitem datele pentru înregistrare
            response = requests.post(f"{self.AUTH_URL}/register",
                                     json={"userName": username, "fullName": fullname, "password": password}, timeout=5)
            return response.status_code == 201
        except Exception as e:
            print(f"Eroare API Register: {e}")
            return False

    def newExpense(self, token, data):
        try:
            headers = {"Authorization": f"Bearer {token}"}
            response = requests.post(f"{self.EXPENSE_URL}/expenses", json=data, headers=headers)
            return response.status_code == 201
        except:
            return False

    def getAllExpenses(self, token):
        try:
            headers = {"Authorization": f"Bearer {token}"}
            response = requests.get(f"{self.EXPENSE_URL}/expenses", headers=headers)
            if response.status_code == 200:
                return response.json()
        except:
            pass
        return []


# 3. Componenta principală Flask
class FlaskWebService:
    def __init__(self):
        self.app = Flask(__name__, template_folder="templates")
        self.current_token = None

        self.__htmlService = HtmlService()
        self.__apiService = ApiManager()
        self.setup_routes()

    def setup_routes(self):

        @self.app.route('/')
        @self.app.route('/login')
        def login_route():
            return self.__htmlService.getLoginPage()

        @self.app.route('/home')
        def home_page():
            # Verificăm variabila de instanță în loc de session
            if not self.current_token:
                return redirect(url_for('login_route'))
            return self.__htmlService.getHomePage()

        @self.app.route('/register', methods=['GET', 'POST'])
        def register_route():
            if request.method == 'POST':
                # Luăm datele din formular (folosim .get pentru siguranță)
                u = request.form.get('username') or request.form.get('userName')
                f = request.form.get('fullname') or request.form.get('fullName')
                p = request.form.get('password')

                success = self.__apiService.register(u, f, p)
                if success:
                    return redirect(url_for('login_route'))
                return self.__htmlService.getErrorPage("Registration Failed")
            return self.__htmlService.getRegisterPage()

        @self.app.route('/do-login', methods=['POST'])
        def do_login():
            u = request.form.get('username')
            p = request.form.get('password')

            success, token = self.__apiService.login(u, p)
            if success:
                # SALVĂM TOKEN-UL ÎN VARIABILA CLASEI
                self.current_token = token
                print(f"DEBUG: Login reusit. Token curent: {self.current_token}")
                return redirect(url_for('home_page'))

            return self.__htmlService.getErrorPage("Invalid Credentials")

        @self.app.route('/expenses')
        def expenses_route():
            if not self.current_token:
                return redirect(url_for('login_route'))

            data = self.__apiService.getAllExpenses(self.current_token)
            return self.__htmlService.getExpensesPage(data)

        @self.app.route('/add-expense', methods=['GET', 'POST'])
        def add_expense_route():
            if not self.current_token:
                return redirect(url_for('login_route'))

            if request.method == 'POST':
                expense_data = {
                    "type": request.form.get('type'),
                    "cost": float(request.form.get('cost', 0))
                }
                self.__apiService.newExpense(self.current_token, expense_data)
                return redirect(url_for('expenses_route'))
            return self.__htmlService.getNewExpensePage()

        @self.app.route('/logout')
        def logout():
            self.current_token = None
            return redirect(url_for('login_route'))

    def run(self):
        # Pornim pe portul 8080 conform ultimei tale configurații
        self.app.run(host='0.0.0.0', port=8080, debug=True)


if __name__ == "__main__":
    web_service = FlaskWebService()
    web_service.run()