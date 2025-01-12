package com.yourcompany.rentalmanagement.view;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import com.yourcompany.rentalmanagement.controller.PaymentController;
import com.yourcompany.rentalmanagement.dao.impl.HostDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.OwnerDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.RentalAgreementDaoImpl;
import com.yourcompany.rentalmanagement.dao.impl.TenantDaoImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StatisticalReportController implements Initializable {

    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private HostDaoImpl hostDaoImpl = new HostDaoImpl();
    private OwnerDaoImpl ownerDaoImpl = new OwnerDaoImpl();
    private TenantDaoImpl tenantDaoImpl = new TenantDaoImpl();
    private RentalAgreementDaoImpl rentalAgreementDaoImpl = new RentalAgreementDaoImpl();
    private PaymentController paymentController = new PaymentController();

    @FXML
    private Label hostLabel;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Label ownerLabel;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label revenueLable;

    @FXML
    private Label propertyLabel;

    @FXML
    private Label tenantLabel;

    @FXML
    private Text totalHost;

    @FXML
    private Text totalOwner;

    @FXML
    private Text totalProperty;

    @FXML
    private Text totalRevenue;

    @FXML
    private Text totalTenant;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    private void loadTable() {
        totalProperty.setText(String.valueOf(propertyDaoImpl.getTotalResidentialPropertyCount() + propertyDaoImpl.getTotalCommercialPropertyCount()));
        totalHost.setText(String.valueOf(hostDaoImpl.getTotalUsers()));
        totalOwner.setText(String.valueOf(ownerDaoImpl.getTotalUsers()));
        totalTenant.setText(String.valueOf(tenantDaoImpl.getTotalUsers()));
        totalRevenue.setText(paymentController.getTotalIncome() + " USD");
    }

    private void loadLineChart() {
        // Set up the X-axis and Y-axis
        xAxis = new CategoryAxis();
        xAxis.setLabel("Year");

        yAxis = new NumberAxis();
        yAxis.setLabel("Total Rental Agreements");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0); // Start from 0
        yAxis.setTickUnit(0);

        // Fetch data for residential and commercial rental agreements
        Map<Integer, Long> residentialData = rentalAgreementDaoImpl.getYearlyRentalAgreements("residential");
        Map<Integer, Long> commercialData = rentalAgreementDaoImpl.getYearlyRentalAgreements("commercial");

        // Determine the range of years for the X-axis
        Set<Integer> years = new TreeSet<>();
        years.addAll(residentialData.keySet());
        years.addAll(commercialData.keySet());

        // Convert years to a sorted list of strings for the X-axis
        ObservableList<String> yearLabels = FXCollections.observableArrayList();
        for (Integer year : years) {
            yearLabels.add(year.toString());
        }
        xAxis.setCategories(yearLabels);

        // Create data series for the line chart
        XYChart.Series<String, Number> residentialSeries = new XYChart.Series<>();
        residentialSeries.setName("Residential");

        XYChart.Series<String, Number> commercialSeries = new XYChart.Series<>();
        commercialSeries.setName("Commercial");

        // Populate data series with the yearly data
        for (Integer year : years) {
            String yearLabel = year.toString();
            Long residentialCount = residentialData.getOrDefault(year, 0L);
            Long commercialCount = commercialData.getOrDefault(year, 0L);

            residentialSeries.getData().add(new XYChart.Data<>(yearLabel, residentialCount));
            commercialSeries.getData().add(new XYChart.Data<>(yearLabel, commercialCount));
        }

        // Clear previous data and add new series
        lineChart.getData().clear();
        lineChart.getData().addAll(residentialSeries, commercialSeries);

        // Set the colors for the series
        residentialSeries.getNode().setStyle("-fx-stroke: #1E3058;"); // Primary color
        commercialSeries.getNode().setStyle("-fx-stroke: #DCE8F8;"); // Secondary color

        // Style the data points
        for (XYChart.Data<String, Number> data : residentialSeries.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-background-color: #1E3058;");
            }
        }

        for (XYChart.Data<String, Number> data : commercialSeries.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-background-color: #DCE8F8;");
            }
        }
    }

    private void loadPieChart() {
        long totalCommercialProperties = propertyDaoImpl.getTotalCommercialPropertyCount();
        long totalResidentialProperties = propertyDaoImpl.getTotalResidentialPropertyCount();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Commercial Properties", totalCommercialProperties),
                new PieChart.Data("Residential Properties", totalResidentialProperties)
        );

        pieChart.setData(pieChartData);

        // Set colors for pie chart slices
        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #1E3058;"); // Commercial in primary color
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #DCE8F8;"); // Residential in secondary color

        // Add color change listener in case of updates
        pieChart.getData().forEach(data -> {
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setStyle("-fx-pie-color: derive(#1E3058, 20%);");
            });
            data.getNode().setOnMouseExited(event -> {
                if (data == pieChartData.get(0)) {
                    data.getNode().setStyle("-fx-pie-color: #1E3058;");
                } else {
                    data.getNode().setStyle("-fx-pie-color: #DCE8F8;");
                }
            });
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Add the stylesheet
        VBox root = (VBox) totalProperty.getParent().getParent().getParent();
        root.getStylesheets().add(getClass().getResource("/css/statistical-report.css").toExternalForm());

        loadTable();
        loadLineChart();
        loadPieChart();
    }
}
