/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author duhanasir
 */



import model.Expense;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;



public class ExpenseFilterService {
    
    
       // Search expenses by name
    public static List<Expense> searchExpenses(
            List<Expense> expenses, String keyword) {

        String search = keyword == null ? "" :
                keyword.trim().toLowerCase(Locale.ROOT);

        return expenses.stream()
                .filter(expense -> expense.getName()
                .toLowerCase(Locale.ROOT).contains(search))
                .collect(Collectors.toList());
    }

    // Filter expenses by category
    public static List<Expense> filterByCategory(
            List<Expense> expenses, String category) {

        Predicate<Expense> condition = expense ->
                category == null ||
                category.equalsIgnoreCase("All") ||
                category.equalsIgnoreCase(expense.getCategory());

        return expenses.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    // Sort expenses by amount
    public static List<Expense> sortByAmount(
            List<Expense> expenses, boolean ascending) {

        Comparator<Expense> comparator =
                Comparator.comparingDouble(Expense::getAmount);

        return expenses.stream()
                .sorted(ascending ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }

    // Sort expenses by date
    public static List<Expense> sortByDate(
            List<Expense> expenses, boolean ascending) {

        Comparator<Expense> comparator =
                Comparator.comparing(Expense::getDate);

        return expenses.stream()
                .sorted(ascending ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }
}
    
    

