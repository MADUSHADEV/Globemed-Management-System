package lk.usj.OPD_Management.java.controller.receptionist.treatment;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReceptionistTreatmentBillingController implements Initializable {

    @FXML private TableView<TreatmentPlanDTO> tblCompletedTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colPatientId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDoctorId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDescription;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colCompletedDate;

    @FXML private Label lblSelectedTreatment;
    @FXML private TextField txtCost;
    @FXML private Button btnAddCost;
    @FXML private Button btnRefresh;
    @FXML private TextArea txtTreatmentDetails;

    @FXML private TableView<TreatmentPlanDTO> tblBilledTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colBilledTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colBilledPatientId;
    @FXML private TableColumn<TreatmentPlanDTO, Double> colBilledCost;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colBilledDate;

    private TreatmentPlanBO treatmentPlanBO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.TREATMENT_PLAN);

            // Initialize table columns
            setupTableColumns();

            // Load completed treatments
            loadCompletedTreatments();
            loadBilledTreatments();

            // Setup table selection listener
            setupTableSelectionListener();

            System.out.println("💼 RECEPTIONIST: Treatment billing interface initialized");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Completed treatments table
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCompletedDate.setCellValueFactory(new PropertyValueFactory<>("completedDate"));

        // Billed treatments table
        colBilledTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colBilledPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colBilledCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colBilledDate.setCellValueFactory(new PropertyValueFactory<>("billedDate"));
    }

    private void setupTableSelectionListener() {
        tblCompletedTreatments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayTreatmentForBilling(newSelection);
            }
        });
    }

    @FXML
    private void btnAddCostOnAction(ActionEvent actionEvent) {
        try {
            TreatmentPlanDTO selectedTreatment = tblCompletedTreatments.getSelectionModel().getSelectedItem();

            if (selectedTreatment == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a treatment to add cost");
                return;
            }

            if (!validateCostInput()) {
                return;
            }

            double cost = Double.parseDouble(txtCost.getText().trim());

            // Confirm billing
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Billing");
            confirmAlert.setHeaderText("Add Treatment Cost");
            confirmAlert.setContentText("Add cost of $" + cost + " to treatment " + selectedTreatment.getTreatmentId() + "?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                boolean success = treatmentPlanBO.addTreatmentCost(selectedTreatment.getTreatmentId(), cost);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Cost added successfully! Billing and reports have been notified.");
                    txtCost.clear();
                    loadCompletedTreatments();
                    loadBilledTreatments();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add treatment cost");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding cost: " + e.getMessage());
        }
    }

    @FXML
    private void btnRefreshOnAction(ActionEvent actionEvent) {
        loadCompletedTreatments();
        loadBilledTreatments();
    }

    private void loadCompletedTreatments() {
        try {
            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getCompletedTreatmentsForBilling();
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(treatments);
            tblCompletedTreatments.setItems(treatmentList);

            System.out.println("💼 RECEPTIONIST: Loaded " + treatments.size() + " completed treatments for billing");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading completed treatments: " + e.getMessage());
        }
    }

    private void loadBilledTreatments() {
        try {
            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getBilledTreatments();
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(treatments);
            tblBilledTreatments.setItems(treatmentList);

            System.out.println("💼 RECEPTIONIST: Loaded " + treatments.size() + " billed treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading billed treatments: " + e.getMessage());
        }
    }

    private void displayTreatmentForBilling(TreatmentPlanDTO treatment) {
        lblSelectedTreatment.setText("Treatment: " + treatment.getTreatmentId());

        StringBuilder details = new StringBuilder();
        details.append("Patient ID: ").append(treatment.getPatientId()).append("\n");
        details.append("Doctor ID: ").append(treatment.getDoctorId()).append("\n");
        details.append("Description: ").append(treatment.getDescription()).append("\n\n");
        details.append("Medicines:\n").append(treatment.getMedicines()).append("\n\n");
        details.append("Procedures:\n").append(treatment.getProcedures()).append("\n\n");
        details.append("Follow-ups:\n").append(treatment.getFollowUps()).append("\n\n");
        details.append("Completed Date: ").append(treatment.getCompletedDate());

        txtTreatmentDetails.setText(details.toString());
    }

    private boolean validateCostInput() {
        String costText = txtCost.getText().trim();

        if (costText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Treatment cost is required");
            return false;
        }

        try {
            double cost = Double.parseDouble(costText);
            if (cost < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Treatment cost cannot be negative");
                return false;
            }
            if (cost > 999999.99) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Treatment cost is too high");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid cost amount");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
