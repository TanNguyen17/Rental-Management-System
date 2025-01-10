package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImp;
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
import javafx.scene.chart.*;
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
        // Set up the axes
        xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        yAxis = new NumberAxis();
        yAxis.setLabel("Income ($)");

        // Create the LineChart
        lineChart.setTitle("Monthly Income Overview");

        // Create data series
        XYChart.Series<String, Double> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Monthly Income");

        // Generate fake Payment data
        Map<String, Double> monthlyIncome = new LinkedHashMap<>();

        monthlyIncome.put("January", 617.9);
        monthlyIncome.put("February", 771.13);
        monthlyIncome.put("March", 809.64);
        monthlyIncome.put("April", 190.41);
        monthlyIncome.put("May", 956.14);
        monthlyIncome.put("June", 640.38);
        monthlyIncome.put("July", 968.2);
        monthlyIncome.put("August", 276.3);
        monthlyIncome.put("September", 463.96);
        monthlyIncome.put("October", 907.36);
        monthlyIncome.put("November", 276.3);
        monthlyIncome.put("December", 635.45);


        // Populate the series with data
        for (Map.Entry<String, Double> entry : monthlyIncome.entrySet()) {
            incomeSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add series to the chart
        lineChart.getData().add(incomeSeries);
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