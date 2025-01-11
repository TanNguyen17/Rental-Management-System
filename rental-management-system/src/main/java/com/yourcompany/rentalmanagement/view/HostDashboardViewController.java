package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.util.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Side;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HostDashboardViewController implements Initializable {
    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private PaymentDaoImpl paymentDaoImpl = new PaymentDaoImpl();
    private ObservableList<Property> properties;
    private UserSession userSession = UserSession.getInstance();

    @FXML
    private BorderPane borderPane;

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
    private TableColumn<Property, Double> totalIncome;

    @FXML
    private TableColumn<Property, Double> stayLength;

    @FXML
    private TableColumn<Property, String> status;


    @FXML
    private TableView<Property> propertyTable;

    private void initializeLineChart() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(months));
        xAxis.setLabel("Month");
        xAxis.setSide(Side.BOTTOM); // Ensure the label is at the bottom
        xAxis.lookup(".axis-label").setTranslateX(150);

        yAxis = new NumberAxis();
        yAxis.setLabel("Income ($)");
        yAxis.setSide(Side.TOP);

        // Create the LineChart
        lineChart.setTitle("Monthly Revenue Overview");

        // Create data series
        XYChart.Series<String, Double> expectedRevenueSeries = new XYChart.Series<>();
        expectedRevenueSeries.setName("Expected Revenue");

        XYChart.Series<String, Double> actualRevenueSeries = new XYChart.Series<>();
        actualRevenueSeries.setName("Actual Revenue");

        // Fetch total income
//        List<Double> expectedRevenue = paymentDaoImpl.getMonthlyPayment(userSession.getCurrentUser().getId(), "expected");
        List<Double> expectedRevenue = paymentDaoImpl.getMonthlyPayment(1, "expected");
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
        properties = FXCollections.observableArrayList(propertyDaoImpl.getAllPropertiesByHostID(1));
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
        // Fetch all properties managed by the current host
        properties = FXCollections.observableArrayList(propertyDaoImpl.getAllPropertiesByHostID(1));
        //long hostId = userSession.getCurrentUser().getId();

        // Retrieve stay durations grouped by property
        Map<Long, List<Long>> stayDurationsByProperty = propertyDaoImpl.getStayDurationsByProperty(1); // change to HostID later
        Map<Long, Double> incomeByProperty = propertyDaoImpl.calculateTotalIncomeByProperty(1);

        // Calculate the average stay length for each property
        Map<Long, Double> averageStayByProperty = new HashMap<>();
        stayDurationsByProperty.forEach((propertyId, durations) -> {
            double averageStay = durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            averageStayByProperty.put(propertyId, averageStay);
        });

        // Bind data to the table
        id.setCellValueFactory(cellData ->
                new SimpleLongProperty(propertyTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalIncome.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Math.round(incomeByProperty.getOrDefault(cellData.getValue().getId(), 0.0) * 100.0) / 100.0).asObject());
        stayLength.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Math.round(averageStayByProperty.getOrDefault(cellData.getValue().getId(), 0.0) * 100.0) / 100.0).asObject());
        propertyTable.setItems(properties);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLineChart();
        initializePieChart();
        initializeTableData();
    }


}