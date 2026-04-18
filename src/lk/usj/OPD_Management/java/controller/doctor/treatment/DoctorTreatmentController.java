package lk.usj.OPD_Management.java.controller.doctor.treatment;

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

public class DoctorTreatmentController implements Initializable {

    @FXML private TextField txtPatientId;
    @FXML private TextField txtDoctorId;
    @FXML private TextArea txtDescription;
    @FXML private TextArea txtMedicines;
    @FXML private TextArea txtProcedures;
    @FXML private TextArea txtFollowUps;
    @FXML private Button btnCreateTreatment;
    @FXML private Button btnCompleteTreatment;
    @FXML private Button btnRefresh;

    @FXML private TableView<TreatmentPlanDTO> tblTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colPatientId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDescription;
    @FXML private TableColumn<TreatmentPlanDTO, String> colStatus;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colCreatedDate;
    @FXML private TableColumn<TreatmentPlanDTO, Double> colCost;

    @FXML private Label lblSelectedTreatment;
    @FXML private TextArea txtSelectedDetails;

    private TreatmentPlanBO treatmentPlanBO;
    private String currentDoctorId = "DOC001"; // This should come from session/login

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.TREATMENT_PLAN);

            // Set current doctor ID
            txtDoctorId.setText(currentDoctorId);
            txtDoctorId.setEditable(false);

            // Initialize table columns
            setupTableColumns();

            // Load doctor's treatments
            loadDoctorTreatments();

            // Setup table selection listener
            setupTableSelectionListener();

            System.out.println("👨‍⚕️ DOCTOR: Treatment management interface initialized");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", e.getMessage());
        }
    }

    private void setupTableColumns() {
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void setupTableSelectionListener() {
        tblTreatments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayTreatmentDetails(newSelection);
            }
        });
    }

    @FXML
    private void btnCreateTreatmentOnAction(ActionEvent actionEvent) {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Create treatment plan
            boolean success = treatmentPlanBO.createTreatmentPlan(
                    txtPatientId.getText().trim(),
                    currentDoctorId,
                    txtDescription.getText().trim(),
                    txtMedicines.getText().trim(),
                    txtProcedures.getText().trim(),
                    txtFollowUps.getText().trim()
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Treatment plan created successfully!");
                clearInputFields();
                loadDoctorTreatments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create treatment plan");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error creating treatment: " + e.getMessage());
        }
    }

    @FXML
    private void btnCompleteTreatmentOnAction(ActionEvent actionEvent) {
        try {
            TreatmentPlanDTO selectedTreatment = tblTreatments.getSelectionModel().getSelectedItem();

            if (selectedTreatment == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a treatment to complete");
                return;
            }

            if (selectedTreatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED ||
                    selectedTreatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
                showAlert(Alert.AlertType.WARNING, "Invalid Action", "Treatment is already completed or billed");
                return;
            }

            // Confirm completion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Completion");
            confirmAlert.setHeaderText("Complete Treatment Plan");
            confirmAlert.setContentText("Are you sure you want to mark this treatment as completed?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                boolean success = treatmentPlanBO.completeTreatmentPlan(selectedTreatment.getTreatmentId(), currentDoctorId);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Treatment marked as completed!");
                    loadDoctorTreatments();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete treatment");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error completing treatment: " + e.getMessage());
        }
    }

    @FXML
    private void btnRefreshOnAction(ActionEvent actionEvent) {
        loadDoctorTreatments();
    }

    private void loadDoctorTreatments() {
        try {
            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getTreatmentPlansByDoctor(currentDoctorId);
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(treatments);
            tblTreatments.setItems(treatmentList);

            System.out.println("👨‍⚕️ DOCTOR: Loaded " + treatments.size() + " treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading treatments: " + e.getMessage());
        }
    }

    private void displayTreatmentDetails(TreatmentPlanDTO treatment) {
        lblSelectedTreatment.setText("Treatment: " + treatment.getTreatmentId());

        StringBuilder details = new StringBuilder();
        details.append("Patient ID: ").append(treatment.getPatientId()).append("\n");
        details.append("Description: ").append(treatment.getDescription()).append("\n\n");
        details.append("Medicines:\n").append(treatment.getMedicines()).append("\n\n");
        details.append("Procedures:\n").append(treatment.getProcedures()).append("\n\n");
        details.append("Follow-ups:\n").append(treatment.getFollowUps()).append("\n\n");
        details.append("Status: ").append(treatment.getStatus()).append("\n");
        details.append("Cost: $").append(treatment.getCost()).append("\n");
        details.append("Created: ").append(treatment.getCreatedDate());

        txtSelectedDetails.setText(details.toString());
    }

    private boolean validateInputs() {
        if (txtPatientId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Patient ID is required");
            return false;
        }

        if (txtDescription.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Treatment description is required");
            return false;
        }

        return true;
    }

    private void clearInputFields() {
        txtPatientId.clear();
        txtDescription.clear();
        txtMedicines.clear();
        txtProcedures.clear();
        txtFollowUps.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
