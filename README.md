# Personal Expense Tracker

JavaFX + Maven group project.

## Run

Open the project in NetBeans and click **Run**.

Default login:

Username: admin
Password: admin123

## Packages

- `model` - User, Category, Expense, Budget
- `controller` - FXML controllers
- `service` - shared storage and navigation
- `src/main/resources/view` - FXML files
- `src/main/resources/style.css` - shared styles

## Important

Do not rename shared methods without telling the group:

- FileStorageService.saveList(...)
- FileStorageService.loadList(...)
- ExpenseStorageService.loadExpenses()
- ExpenseStorageService.saveExpenses(...)
- NavigationService.goTo(...)
- Expense.getCategory()

Member 1 owns:

- User.java
- Category.java
- Login.fxml
- LoginController.java
- Categories.fxml
- CategoryController.java
