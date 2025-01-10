package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.*;

public class HostDashboardViewController implements Initializable {
    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private PaymentDaoImpl paymentDaoImpl = new PaymentDaoImpl();
    private ObservableList<Property> properties;
    private UserSession userSession = UserSession.getInstance();

    @FXML
    private LineChart<String, Double> lineChart;

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

    private void initializeLineChart() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        // Set up the axes
        xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(
                months
        ));
        xAxis.setLabel("Month");

        yAxis = new NumberAxis();
        yAxis.setLabel("Income ($)");

        // Create the LineChart
        lineChart.setTitle("Monthly Revenue Overview");

        // Create data series
        XYChart.Series<String, Double> expectedRevenueSeries = new XYChart.Series<>();
        expectedRevenueSeries.setName("Expected Revenue");

        XYChart.Series<String, Double> actualRevenueSeries = new XYChart.Series<>();
        actualRevenueSeries.setName("Actual Revenue");

        // Fetch total income
        List<Double> expectedRevenue = paymentDaoImpl.getMonthlyPayment(userSession.getCurrentUser().getId(), "expected");
        List<Double> actualRevenue = paymentDaoImpl.getMonthlyPayment(1, "actual");

        // Populate the series with data
        for (int i = 0; i < months.length; i++) {
            double income = (i < expectedRevenue.size()) ? expectedRevenue.get(i) : 0.0;
            expectedRevenueSeries.getData().add(new XYChart.Data<>(months[i], income));
        }

        for (int i = 0; i < months.length; i++) {
            double income = (i < actualRevenue.size()) ? actualRevenue.get(i) : 0.0;
            actualRevenueSeries.getData().add(new XYChart.Data<>(months[i], income));
        }

        // Add series to the chart
        lineChart.getData().addAll(actualRevenueSeries, expectedRevenueSeries);
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableData();
        initializePieChart();
        initializeLineChart();
    }
}