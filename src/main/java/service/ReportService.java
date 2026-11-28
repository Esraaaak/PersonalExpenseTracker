package service;

import model.Expense;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ReportService {

    // Load all saved expenses
    public List<Expense> getAllExpenses() {
        return ExpenseStorageService.loadExpenses();
    }

    // Get expenses for a selected month
    public List<Expense> getMonthlyExpenses(YearMonth month) {

        return getAllExpenses().stream()
                .filter(expense -> expense.getDate() != null)
                .filter(expense ->
                        YearMonth.from(expense.getDate()).equals(month))
                .collect(Collectors.toList());
    }

    // Calculate total expenses for a month
    public double getMonthlyTotal(YearMonth month) {

        return getMonthlyExpenses(month).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // Calculate average expense for a month
    public double getAverageExpense(YearMonth month) {

        List<Expense> expenses = getMonthlyExpenses(month);

        if (expenses.isEmpty()) {
            return 0.0;
        }

        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0.0);
    }

    // Calculate total amount for each category
    public Map<String, Double> getCategoryTotals(YearMonth month) {

        Map<String, Double> categoryTotals =
                getMonthlyExpenses(month).stream()
                        .filter(expense -> expense.getCategory() != null
                                && !expense.getCategory().trim().isEmpty())
                        .collect(Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.summingDouble(
                                        Expense::getAmount)
                        ));

        // Sort categories by total amount in descending order
        return categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue()
                        .reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    // Find the category with the highest spending
    public String getHighestSpendingCategory(YearMonth month) {

        return getCategoryTotals(month).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    // Get the highest individual expense for a month
    public double getHighestExpense(YearMonth month) {

        return getMonthlyExpenses(month).stream()
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);
    }

    // Get monthly expenses sorted by date
    public List<Expense> getExpensesSortedByDate(YearMonth month) {

        return getMonthlyExpenses(month).stream()
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }
}
