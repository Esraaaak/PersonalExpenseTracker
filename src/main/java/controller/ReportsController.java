
package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import model.Expense;
import service.ReportService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class ReportsController {

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Label totalLabel;

    @FXML
    private Label averageLabel;

    @FXML
    private Label highestCategoryLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> categoryBarChart;

    private final ReportService reportService = new ReportService();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM");

    @FXML
    private void initialize() {

        try {
            List<Expense> expenses = reportService.getAllExpenses();

            TreeSet<YearMonth> months = new TreeSet<>();

            for (Expense expense : expenses) {
                if (expense.getDate() != null) {
                    months.add(YearMonth.from(expense.getDate()));
                }
            }

            months.add(YearMonth.now());

            for (YearMonth month : months.descendingSet()) {
                monthComboBox.getItems().add(month.format(FORMATTER));
            }

            monthComboBox.setOnAction(event -> updateReport());

            if (!monthComboBox.getItems().isEmpty()) {
                monthComboBox.setValue(
                        monthComboBox.getItems().get(0));
                updateReport();
            }

        } catch (Exception e) {
            statusLabel.setText("Unable to load expenses.");
        }
    }

    private void updateReport() {

        String selectedMonth = monthComboBox.getValue();

        if (selectedMonth == null) {
            statusLabel.setText("Please select a valid month.");
            return;
        }

        try {
            YearMonth month = YearMonth.parse(
                    selectedMonth, FORMATTER);

            double total = reportService.getMonthlyTotal(month);
            double average = reportService.getAverageExpense(month);

            Map<String, Double> categoryTotals =
                    reportService.getCategoryTotals(month);

            totalLabel.setText(String.format("%.2f", total));
            averageLabel.setText(String.format("%.2f", average));

            highestCategoryLabel.setText(
                    reportService.getHighestSpendingCategory(month));

            categoryPieChart.getData().clear();
            categoryBarChart.getData().clear();

            XYChart.Series<String, Number> series =
                    new XYChart.Series<>();

            series.setName("Expenses");

            for (Map.Entry<String, Double> entry
                    : categoryTotals.entrySet()) {

                categoryPieChart.getData().add(
                        new PieChart.Data(
                                entry.getKey(), entry.getValue()));

                series.getData().add(
                        new XYChart.Data<>(
                                entry.getKey(), entry.getValue()));
            }

            categoryBarChart.getData().add(series);

            if (categoryTotals.isEmpty()) {
                statusLabel.setText(
                        "No expenses found for this month.");
                highestCategoryLabel.setText("N/A");
            } else {
                statusLabel.setText(
                        "Report updated successfully.");
            }

        } catch (Exception e) {
            statusLabel.setText("Unable to generate report.");
        }
    }
}
