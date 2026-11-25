
package service;

import model.Expense;
import java.io.IOException;
import java.util.List;
//basmah
public class ExpenseStorageService {

    // File used to store expense records.
    private static final String FILE_NAME = "expenses.dat";

    // Save all expense records.
    public static void saveExpenses(List<Expense> expenses)
            throws IOException {

        FileStorageService.saveList(FILE_NAME, expenses);
    }

    // Load previously saved expense records.
    public static List<Expense> loadExpenses() {

        try {
            return FileStorageService.loadList(
                    FILE_NAME, Expense.class);

        } catch (IOException | ClassNotFoundException e) {

            throw new IllegalStateException(
                    "Unable to load expenses.", e);
        }
    }
}
