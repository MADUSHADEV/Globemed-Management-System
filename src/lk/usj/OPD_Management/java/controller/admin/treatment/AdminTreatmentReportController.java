package lk.usj.OPD_Management.java.controller.admin.treatment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import lk.usj.OPD_Management.java.service.BOFactory;
import lk.usj.OPD_Management.java.service.custom.TreatmentPlanBO;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class AdminTreatmentReportController implements Initializable {

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<TreatmentPlanDTO.TreatmentStatus> cmbStatus;
    @FXML private TextField txtDoctorId;
    @FXML private TextField txtPatientId;
    @FXML private Button btnSearch;
    @FXML private Button btnClearFilter;
    @FXML private Button btnGenerateReport;

    @FXML private TableView<TreatmentPlanDTO> tblTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colPatientId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDoctorId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDescription;
    @FXML private TableColumn<TreatmentPlanDTO, String> colStatus;
    @FXML private TableColumn<TreatmentPlanDTO, Double> colCost;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colCreatedDate;

    @FXML private Label lblTotalTreatments;
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblCompletedTreatments;
    @FXML private Label lblPendingTreatments;

    @FXML private TextArea txtReportSummary;

    private TreatmentPlanBO treatmentPlanBO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.TREATMENT_PLAN);

            // Initialize status combo box
            setupStatusComboBox();

            // Initialize table columns
            setupTableColumns();

            // Load all treatments initially
            loadAllTreatments();

            // Update statistics
            updateStatistics();

            System.out.println("👨‍💼 ADMIN: Treatment reports interface initialized");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", e.getMessage());
        }
    }

    private void setupStatusComboBox() {
        ObservableList<TreatmentPlanDTO.TreatmentStatus> statusList = FXCollections.observableArrayList();
        statusList.addAll(TreatmentPlanDTO.TreatmentStatus.values());
        cmbStatus.setItems(statusList);
    }

    private void setupTableColumns() {
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
    }

    @FXML
    private void btnSearchOnAction(ActionEvent actionEvent) {
        try {
            List<TreatmentPlanDTO> filteredTreatments = applyFilters();
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(filteredTreatments);
            tblTreatments.setItems(treatmentList);

            updateFilteredStatistics(filteredTreatments);

            System.out.println("👨‍💼 ADMIN: Applied filters, found " + filteredTreatments.size() + " treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "Error applying filters: " + e.getMessage());
        }
    }

    @FXML
    private void btnClearFilterOnAction(ActionEvent actionEvent) {
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        cmbStatus.setValue(null);
        txtDoctorId.clear();
        txtPatientId.clear();

        loadAllTreatments();
        updateStatistics();
    }

    @FXML
    private void btnGenerateReportOnAction(ActionEvent actionEvent) {
        try {
            generateDetailedReport();
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Detailed report has been generated successfully!");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Error", "Error generating report: " + e.getMessage());
        }
    }

    private List<TreatmentPlanDTO> applyFilters() throws Exception {
        List<TreatmentPlanDTO> treatments = treatmentPlanBO.getAllTreatmentPlans();

        // Filter by date range
        if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
            treatments = treatmentPlanBO.getTreatmentsByDateRange(dpStartDate.getValue(), dpEndDate.getValue());
        }

        // Filter by status
        if (cmbStatus.getValue() != null) {
            treatments = treatments.stream()
                    .filter(t -> t.getStatus() == cmbStatus.getValue())
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by doctor
        if (!txtDoctorId.getText().trim().isEmpty()) {
            String doctorId = txtDoctorId.getText().trim();
            treatments = treatments.stream()
                    .filter(t -> t.getDoctorId().equalsIgnoreCase(doctorId))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by patient
        if (!txtPatientId.getText().trim().isEmpty()) {
            String patientId = txtPatientId.getText().trim();
            treatments = treatments.stream()
                    .filter(t -> t.getPatientId().equalsIgnoreCase(patientId))
                    .collect(java.util.stream.Collectors.toList());
        }

        return treatments;
    }

    private void loadAllTreatments() {
        try {
            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getAllTreatmentPlans();
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(treatments);
            tblTreatments.setItems(treatmentList);

            System.out.println("👨‍💼 ADMIN: Loaded " + treatments.size() + " treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading treatments: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            int totalTreatments = treatmentPlanBO.getTotalTreatmentCount();
            double totalRevenue = treatmentPlanBO.getTotalRevenue();
            int completedTreatments = treatmentPlanBO.getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED) +
                    treatmentPlanBO.getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);
            int pendingTreatments = treatmentPlanBO.getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus.CREATED) +
                    treatmentPlanBO.getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS);

            lblTotalTreatments.setText(String.valueOf(totalTreatments));
            lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));
            lblCompletedTreatments.setText(String.valueOf(completedTreatments));
            lblPendingTreatments.setText(String.valueOf(pendingTreatments));

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating statistics: " + e.getMessage());
        }
    }

    private void updateFilteredStatistics(List<TreatmentPlanDTO> treatments) {
        int totalTreatments = treatments.size();
        double totalRevenue = treatments.stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .mapToDouble(TreatmentPlanDTO::getCost)
                .sum();
        long completedTreatments = treatments.stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED ||
                        t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .count();
        long pendingTreatments = treatments.stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.CREATED ||
                        t.getStatus() == TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS)
                .count();

        lblTotalTreatments.setText(String.valueOf(totalTreatments));
        lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));
        lblCompletedTreatments.setText(String.valueOf(completedTreatments));
        lblPendingTreatments.setText(String.valueOf(pendingTreatments));
    }

    private void generateDetailedReport() throws Exception {
        StringBuilder report = new StringBuilder();
        report.append("=== TREATMENT MANAGEMENT REPORT ===\n");
        report.append("Generated on: ").append(LocalDateTime.now()).append("\n\n");

        // Overall statistics
        int totalTreatments = treatmentPlanBO.getTotalTreatmentCount();
        double totalRevenue = treatmentPlanBO.getTotalRevenue();

        report.append("OVERALL STATISTICS:\n");
        report.append("Total Treatments: ").append(totalTreatments).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");

        // Status breakdown
        report.append("\nSTATUS BREAKDOWN:\n");
        for (TreatmentPlanDTO.TreatmentStatus status : TreatmentPlanDTO.TreatmentStatus.values()) {
            int count = treatmentPlanBO.getTreatmentCountByStatus(status);
            report.append(status).append(": ").append(count).append("\n");
        }

        // Recent treatments
        List<TreatmentPlanDTO> recentTreatments = treatmentPlanBO.getAllTreatmentPlans();
        if (!recentTreatments.isEmpty()) {
            report.append("\nRECENT TREATMENTS:\n");
            recentTreatments.stream()
                    .limit(10)
                    .forEach(t -> report.append(t.getTreatmentId())
                            .append(" - Patient: ").append(t.getPatientId())
                            .append(" - Status: ").append(t.getStatus())
                            .append("\n"));
        }

        txtReportSummary.setText(report.toString());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
