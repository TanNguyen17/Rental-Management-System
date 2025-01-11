package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class StatisticalReportController implements Initializable {
    private PropertyDaoImpl propertyDaoImpl = new PropertyDaoImpl();
    private HostDaoImpl hostDaoImpl = new HostDaoImpl();
    private OwnerDaoImpl ownerDaoImpl = new OwnerDaoImpl();
    private TenantDaoImpl tenantDaoImpl = new TenantDaoImpl();
    private RentalAgreementDaoImpl rentalAgreementDaoImpl = new RentalAgreementDaoImpl();

    @FXML
    private Label hostLabel;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Label ownerLabel;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label profitLable;

    @FXML
    private Label propertyLabel;

    @FXML
    private Label revenueLable;

    @FXML
    private Label tenantLabel;

    @FXML
    private Text totalHost;

    @FXML
    private Text totalOwner;

    @FXML
    private Text totalProfit;

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


    private void loadTable(){
        totalProperty.setText(String.valueOf(propertyDaoImpl.getTotalPropertyCount()));
        totalHost.setText(String.valueOf(hostDaoImpl.getTotalUsers()));
        totalOwner.setText(String.valueOf(ownerDaoImpl.getTotalUsers()));
        totalTenant.setText(String.valueOf(tenantDaoImpl.getTotalUsers()));
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

        // Clear previous data and add new series to the chart
        lineChart.getData().clear();
        lineChart.getData().addAll(residentialSeries, commercialSeries);

        // Add hover effects for the data points
        residentialSeries.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip("Year: " + data.getXValue() + "\nResidential: " + data.getYValue());
            Tooltip.install(data.getNode(), tooltip);
            data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-opacity: 0.7;"));
            data.getNode().setOnMouseExited(event -> data.getNode().setStyle("-fx-opacity: 1.0;"));
        });

        commercialSeries.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip("Year: " + data.getXValue() + "\nCommercial: " + data.getYValue());
            Tooltip.install(data.getNode(), tooltip);
            data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-opacity: 0.7;"));
            data.getNode().setOnMouseExited(event -> data.getNode().setStyle("-fx-opacity: 1.0;"));
        });
    }



    private void loadPieChart() {
        // Get the total counts of commercial and residential properties
        long totalCommercialProperties = propertyDaoImpl.getTotalCommercialPropertyCount();
        long totalResidentialProperties = propertyDaoImpl.getTotalResidentialPropertyCount();

        // Create PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Commercial Properties", totalCommercialProperties),
                new PieChart.Data("Residential Properties", totalResidentialProperties)
        );

        // Set the data and customize the PieChart
        pieChart.setData(pieChartData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        loadLineChart();
        loadPieChart();
    }
}
