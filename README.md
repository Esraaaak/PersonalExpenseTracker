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


## Member 3 - Add Expense & Data Persistence

### Files Created

- `controller/AddExpenseController.java`
- `service/ExpenseStorageService.java`
- `view/AddExpense.fxml`

### Files Modified

- `service/FileStorageService.java`
- `style.css`

### Implemented Features

- Designed the Add Expense screen using JavaFX and FXML.
- Implemented expense name, amount, category, date, and notes fields.
- Added a CheckBox for recurring expenses.
- Loaded categories dynamically from the shared storage service.
- Implemented Save and Cancel actions using lambda expressions.
- Added property listeners for input fields.
- Used Stream API, map, sorted, and Collectors to process categories.
- Implemented required field and numeric amount validation.
- Added alerts and exception handling.
- Implemented permanent expense storage using Java Serialization.
- Handled missing and corrupted data files.
- Extended the shared CSS without replacing existing styles.
- Added English comments for code readability and team integration.

### Shared Storage Methods

General storage:

```java
FileStorageService.saveList(fileName, list);
FileStorageService.loadList(fileName, type);
```

Expense storage:

```java
ExpenseStorageService.loadExpenses();
ExpenseStorageService.saveExpenses(expenses);
```

### Data Files

- `users.dat` - User data
- `categories.dat` - Category data
- `expenses.dat` - Expense data

The shared storage service supports serializable objects.

### Testing Results

| Test | Result |
|---|---|
| Project compilation | PASS |
| Add Expense screen loading | PASS |
| Required fields validation | PASS |
| Loading saved categories | PASS |
| Saving expenses | PASS |
| Loading expenses after restart | PASS |
| Saving recurring expense status | PASS |
| Preserving previously saved expenses | PASS |
| Missing file handling | PASS |
| Corrupted file handling | PASS |
| Cancel without saving | PASS |
| Invalid and decimal amount verification | Pending confirmation |

### Integration Notes

- Member 1 can use FileStorageService to save and load users and categories.
- Member 2 can use ExpenseStorageService to load, update, and save expenses.
- Member 4 can use saved expenses for calculations and reports.
- Navigation integration is pending Member 5's NavigationService implementation.
- Final integration testing with other members is pending.
- Temporary test classes and sample data files are excluded from the commit.

