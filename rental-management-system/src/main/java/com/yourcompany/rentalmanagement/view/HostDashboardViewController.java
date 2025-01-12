package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.yourcompany.rentalmanagement.dao.impl.PaymentDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.components.LoadingSpinner;
import com.yourcompany.rentalmanagement.view.components.Toast;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HostDashboardViewController implements Initializable {

    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private PaymentDaoImpl paymentDaoImpl = new PaymentDaoImpl();
    private ObservableList<Property> properties;
    private UserSession userSession = UserSession.getInstance();
    private List<Double> expectedRevenue;
    private List<Double> actualRevenue;
    Map<Long, List<Long>> stayDurationsByProperty = new HashMap<>();
    Map<Long, Double> incomeByProperty = new HashMap<>();
    Map<Long, Double> averageStayByProperty = new HashMap<>();
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private LoadingSpinner loadingSpinner;

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

    @FXML
    private VBox vBox;

    private void initializeLineChart() {
        xAxis.setCategories(FXCollections.observableArrayList(months));
        xAxis.setLabel("Month");
        xAxis.setSide(Side.BOTTOM); // Ensure the label is at the bottom

        yAxis.setLabel("Income ($)");
        yAxis.setSide(Side.TOP);

        // Create the LineChart
        lineChart.setTitle("Monthly Revenue Overview");
    }

    private void initializePieChart() {
        pieChart.setTitle("Property Status");
    }

    private void initializeTable() {
        // Bind data to the table
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalIncome.setCellValueFactory(cellData -> new SimpleDoubleProperty(0.0).asObject());
        stayLength.setCellValueFactory(cellData -> new SimpleDoubleProperty(0.0).asObject());

        propertyTable.setItems(FXCollections.observableArrayList());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLoadingSpinner();
        new Thread(() -> {
            initializeLineChart();
            initializePieChart();
            initializeTable();
            loadingData();
            Platform.runLater(() -> {
                if (!expectedRevenue.isEmpty() && !actualRevenue.isEmpty()) {
                    XYChart.Series<String, Double> expectedRevenueSeries = new XYChart.Series<>();
                    expectedRevenueSeries.setName("Expected Revenue");

                    XYChart.Series<String, Double> actualRevenueSeries = new XYChart.Series<>();
                    actualRevenueSeries.setName("Actual Revenue");
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
            });
            Platform.runLater(() -> {
                if (!properties.isEmpty()) {
                    Map<Property.propertyStatus, Integer> statusCounts = new HashMap<>();
                    for (Property property : properties) {
                        statusCounts.put(property.getStatus(), statusCounts.getOrDefault(property.getStatus(), 0) + 1);
                    }
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    for (Map.Entry<Property.propertyStatus, Integer> entry : statusCounts.entrySet()) {
                        pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
                    }
                    pieChart.setData(pieChartData);
                }
            });
            Platform.runLater(() -> {
                if (!stayDurationsByProperty.isEmpty() && !incomeByProperty.isEmpty()) {
                    stayDurationsByProperty.forEach((propertyId, durations) -> {
                        double averageStay = durations.stream()
                                .mapToLong(Long::longValue)
                                .average()
                                .orElse(0.0);
                        averageStayByProperty.put(propertyId, averageStay);
                    });

                    totalIncome.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                            Math.round(incomeByProperty.getOrDefault(cellData.getValue().getId(), 0.0) * 100.0) / 100.0).asObject());
                    stayLength.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                            Math.round(averageStayByProperty.getOrDefault(cellData.getValue().getId(), 0.0) * 100.0) / 100.0).asObject());
                    propertyTable.setItems(properties);
                }
            });
        }).start();
    }

    private void loadingData() {
        System.out.println("Loading data...");
        loadingSpinner.show();
        try {

            long hostId = userSession.getCurrentUser().getId();
            expectedRevenue = paymentDaoImpl.getMonthlyPayment(hostId, "expected");
            actualRevenue = paymentDaoImpl.getMonthlyPayment(hostId, "actual");
            properties = FXCollections.observableArrayList(propertyDaoImpl.getAllPropertiesByHostID(hostId));
            stayDurationsByProperty = propertyDaoImpl.getStayDurationsByProperty(hostId);
            incomeByProperty = propertyDaoImpl.calculateTotalIncomeByProperty(hostId);

            Platform.runLater(() -> showSuccess("Data loaded successfully!"));
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            Platform.runLater(() -> showError("Error loading properties: " + e.getMessage()));
        } finally {
            loadingSpinner.hide();
        }
    }

    private void setupLoadingSpinner() {
        try {
            loadingSpinner = new LoadingSpinner();
            loadingSpinner.prefWidthProperty().bind(borderPane.widthProperty());
            loadingSpinner.prefHeightProperty().bind(borderPane.heightProperty());

            Platform.runLater(() -> {
                borderPane.getChildren().add(loadingSpinner);
                BorderPane.setAlignment(loadingSpinner, Pos.CENTER);
                loadingSpinner.setViewOrder(-1);
                loadingSpinner.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
            });
        } catch (Exception e) {
            System.err.println("Error setting up loading spinner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        Platform.runLater(() -> Toast.showSuccess((Stage) borderPane.getScene().getWindow(), message));
    }

    private void showError(String message) {
        Platform.runLater(() -> Toast.showError((Stage) borderPane.getScene().getWindow(), message));
    }
}
