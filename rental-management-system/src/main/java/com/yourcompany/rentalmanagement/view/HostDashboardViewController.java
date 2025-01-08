package com.yourcompany.rentalmanagement.view;

import com.yourcompany.rentalmanagement.dao.impl.PropertyDaoImpl;
import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.Property;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
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
    private ObservableList<Property> properties = FXCollections.observableArrayList();
    private static final Random random = new Random();
    private Host currentHost;

    @FXML
    private LineChart<String, Number> lineChart;

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

    //Get random status - TESTING
    private Property.propertyStatus getRandomPropertyStatus() {
        Property.propertyStatus[] statuses = Property.propertyStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static RentalAgreement.rentalAgreementStatus getRandomRAStatus() {
        RentalAgreement.rentalAgreementStatus[] statuses = RentalAgreement.rentalAgreementStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private ObservableList<Property> initializeTableData(){

        id.setCellValueFactory(new PropertyValueFactory<Property, Long>("id"));
        title.setCellValueFactory(new PropertyValueFactory<Property, String>("title"));
        occupancyRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        stayLength.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        bookingRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        turnoverRate.setCellValueFactory(new PropertyValueFactory<Property, Double>("price"));
        properties = FXCollections.observableArrayList();

        for (int i = 1; i <= 10; i++) {
            Property property = new Property();

            // Set basic property details
            property.setId(i);
            property.setTitle("Property Title " + i);
            property.setPrice(1000 + (i * 100));
            property.setStatus(getRandomPropertyStatus());

            // Add the property to the list
            properties.add(property);
            System.out.println(properties.toString());
        }
        return properties;

    }

    private ObservableList<Property> fetchData(){
        for (int i = 1; i <= 10; i++) {
            Property property = new Property();

            // Set basic property details
            property.setId(i);
            property.setTitle("Property Title " + i);
            property.setPrice(1000 + (i * 100));
            property.setStatus(Property.propertyStatus.AVAILABLE);

            // Add the property to the list
            properties.add(property);
            System.out.println(properties.toString());
        }
        return properties;
    }

//    private void generateSampleData() {
//        // Create a host
//        currentHost = new Host();
//        currentHost.setUsername("John Doe");
//
//        // Generate rental agreements and payments
//        List<RentalAgreement> rentalAgreements = new ArrayList<>();
//
//        for (int i = 1; i <= 5; i++) {
//            RentalAgreement rentalAgreement = new RentalAgreement();
//            rentalAgreement.setHost(currentHost);
//            rentalAgreement.setContractDate(LocalDate.now().minusMonths(random.nextInt(12)));
//            rentalAgreement.setRentingFee(1000 + random.nextInt(1000));
//            rentalAgreement.setStatus(getRandomRAStatus());
//
//            // Create a single payment for this rental agreement
//            Payment payment = new Payment();
//            payment.setRentalAgreement(rentalAgreement); // Set the rental agreement in the payment
//            payment.setReceipt("Receipt_" + i);
//            payment.setMethod(i % 2 == 0 ? "Credit Card" : "Bank Transfer");
//            payment.setAmount(500 + random.nextInt(500));
//            payment.setStatus(i % 2 == 0 ? "DONE" : "PENDING");
//
//            rentalAgreements.add(rentalAgreement);
//        }
//
//        currentHost.setRentalAgreements(rentalAgreements);
//    }

//    private List<Payment> generatePaymentsForDemo() {
//        List<Payment> payments = new ArrayList<>();
//        Random random = new Random();
//        currentHost = new Host();
//
//        // Create 10 payments
//        for (int i = 1; i <= 10; i++) {
//            RentalAgreement rentalAgreement = new RentalAgreement();
//            host.setId(i % 2 == 0 ? 1L : 2L); // Assign host ID 1 to half of the payments
//            rentalAgreement.setHost(host);
//            rentalAgreement.setContractDate(LocalDate.now().minusMonths(random.nextInt(12)));
//            rentalAgreement.setRentingFee(1000 + random.nextInt(1000));
//            rentalAgreement.setStatus(getRandomRAStatus());
//
//            Payment payment = new Payment();
//            payment.setRentalAgreement(rentalAgreement); // Set the rental agreement in the payment
//            payment.setReceipt("Receipt_" + i);
//            payment.setMethod(i % 2 == 0 ? "Credit Card" : "Bank Transfer");
//            payment.setAmount(500 + random.nextInt(500));
//            payment.setStatus(i % 2 == 0 ? "DONE" : "PENDING");
//
//            payments.add(payment);
//        }
//        System.out.println(payments.toString());
//        System.out.println(host);
//        return payments;
//    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        propertyTable.setItems(initializeTableData());
        initializePieChart();
    }
}