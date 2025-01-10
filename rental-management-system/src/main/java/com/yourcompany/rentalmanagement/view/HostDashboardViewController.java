package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class HostDashboardViewController implements Initializable {
    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private PaymentDaoImpl paymentDaoImpl = new PaymentDaoImpl();
    private ObservableList<Property> properties;
    private ObservableList<Payment> payments;
    private static final Random random = new Random();
    private Host currentHost;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private PieChart pieChart;

    @FXML
    private TableColumn<Property, Long> id;

    @FXML
    private TableColumn<Property, String> title;

    @FXML
    private TableColumn<Property, Double> occupancyRate;

    @FXML
    private TableColumn<Property, Double> stayLength;

    @FXML
    private TableColumn<Property, Double> bookingRate;

    @FXML
    private TableColumn<Property, Double> turnoverRate;

    @FXML
    private TableView<Property> propertyTable;

    private void initializePieChart() {
        // Count properties by status
        Map<Property.propertyStatus, Integer> statusCounts = new HashMap<>();
        properties = FXCollections.observableArrayList(propertyDaoImpl.getAllProperties());
        for (Property property : properties) {
            statusCounts.put(property.getStatus(), statusCounts.getOrDefault(property.getStatus(), 0) + 1);
        }

        // Create PieChart Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Property.propertyStatus, Integer> entry : statusCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
        }

        // Set data to the PieChart
        pieChart.setData(pieChartData);
        pieChart.setTitle("Property Status");
    }

    private void initializeTableData(){
        id.setCellValueFactory(cellData -> new SimpleLongProperty(properties.indexOf(cellData.getValue()) + 1).asObject());
        title.setCellValueFactory(new PropertyValueFactory<Property, String>("title"));
        occupancyRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        stayLength.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        bookingRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        turnoverRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        properties = FXCollections.observableArrayList(propertyDaoImpl.getAllProperties());
        System.out.println(properties.size());
        propertyTable.setItems(properties);
    }

    private void initializeLineChart() {
        xAxis.setLabel("Month");
        yAxis.setLabel("Income");
        lineChart.setTitle("Income Overview (Last 12 Months)");

        // Create data series for Expected and Real Income
        LineChart.Series<String, Number> expectedIncomeSeries = new LineChart.Series<>();
        expectedIncomeSeries.setName("Expected Income");

        LineChart.Series<String, Number> realIncomeSeries = new LineChart.Series<>();
        realIncomeSeries.setName("Real Income");

        // Calculate income data
        Map<String, Double> expectedIncomeData = calculateIncome(false); // All statuses
        Map<String, Double> realIncomeData = calculateIncome(true); // Only 'DONE' payments

        // Populate the series with data
        for (String month : expectedIncomeData.keySet()) {
            expectedIncomeSeries.getData().add(new LineChart.Data<>(month, expectedIncomeData.get(month)));
            realIncomeSeries.getData().add(new LineChart.Data<>(month, realIncomeData.getOrDefault(month, 0.0)));
        }

        // Add data to the Line Chart
        lineChart.getData().addAll(expectedIncomeSeries, realIncomeSeries);
    }

    /**
     * Calculate income for the past 12 months.
     *
     * @param onlyDone If true, only 'DONE' payments are considered; otherwise, all payments are included.
     * @return A map of month names to income values.
     */
    private Map<String, Double> calculateIncome(boolean onlyDone) {
        Map<String, Double> incomeData = new LinkedHashMap<>();

        // Initialize the last 12 months with zero income
        for (int i = 0; i < 12; i++) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String monthName = YearMonth.from(month).toString();
            incomeData.put(monthName, 0.0);
        }

        // Aggregate payments for each month

        return incomeData;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableData();
        initializePieChart();
    }
}