package lk.usj.OPD_Management.java.controller.doctor.treatment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.usj.OPD_Management.java.common.Common;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import lk.usj.OPD_Management.java.service.BOFactory;
import lk.usj.OPD_Management.java.service.custom.TreatmentPlanBO;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorTreatmentCreateController implements Initializable {

    @FXML
    private TextField txtTreatmentId;
    @FXML
    private TextField txtPatientId;
    @FXML
    private TextField txtPatientName;
    @FXML
    private TextField txtDoctorId;
    @FXML
    private ComboBox<String> cmbTreatmentType;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextArea txtMedicines;
    @FXML
    private TextArea txtProcedures;
    @FXML
    private TextArea txtFollowUps;
    @FXML
    private TextField txtCost;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;

    @FXML
    private Button btnSearchPatient;
    @FXML
    private Button btnCreateTreatment;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCancel;

    @FXML
    private TableView<TreatmentPlanDTO> tblRecentTreatments;
    @FXML
    private TableColumn<TreatmentPlanDTO, String> colTreatmentId;
    @FXML
    private TableColumn<TreatmentPlanDTO, String> colPatientId;
    @FXML
    private TableColumn<TreatmentPlanDTO, String> colDescription;
    @FXML
    private TableColumn<TreatmentPlanDTO, String> colStatus;
    @FXML
    private TableColumn<TreatmentPlanDTO, LocalDateTime> colCreatedDate;
    @FXML
    private TableColumn<TreatmentPlanDTO, Double> colCost;

    @FXML
    private Label lblTreatmentIdDisplay;

    private TreatmentPlanBO treatmentPlanBO;
    private String currentDoctorId = "DOC001"; // This should come from session/login


    private void initBO() {
        try {
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory()
                    .getBO(BOFactory.BOTypes.TREATMENT_PLAN);
        } catch (Exception ex) {
            Common.showError("Service init failed: " + ex.getMessage());
            treatmentPlanBO = null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            initBO();
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.TREATMENT_PLAN);

            // Add null check
            if (treatmentPlanBO == null) {
                Common.showError("Failed to initialize Treatment Plan service");
                return;
            }

            // Set current doctor ID
            txtDoctorId.setText(currentDoctorId);
            txtDoctorId.setEditable(false);

            // Generate new treatment ID
            generateNewTreatmentId();

            // Initialize combo boxes
            initializeComboBoxes();

            // Initialize table columns
            setupTableColumns();

            // Load recent treatments
            loadRecentTreatments();

            System.out.println("👨‍⚕️ DOCTOR: Treatment creation interface initialized");

        } catch (Exception e) {
            Common.showError("Initialization Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeComboBoxes() {
        // Treatment Types
        ObservableList<String> treatmentTypes = FXCollections.observableArrayList(
                "General Consultation",
                "Cardiac Consultation",
                "Neurological Consultation",
                "Orthopedic Consultation",
                "Pediatric Consultation",
                "Dermatology Consultation",
                "ENT Consultation",
                "Eye Consultation",
                "Dental Consultation",
                "Physical Therapy",
                "Laboratory Tests",
                "Radiology",
                "Surgery Consultation",
                "Follow-up Visit",
                "Emergency Treatment"
        );
        cmbTreatmentType.setItems(treatmentTypes);

        // Status Options
        ObservableList<String> statuses = FXCollections.observableArrayList(
                "PLANNED",
                "IN_PROGRESS",
                "COMPLETED",
                "CANCELLED"
        );
        cmbStatus.setItems(statuses);
        cmbStatus.setValue("PLANNED"); // Default status
    }

    private void setupTableColumns() {
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void generateNewTreatmentId() {
        try {
            // Try to get ID from BO first
            if (treatmentPlanBO != null) {
                String newId = treatmentPlanBO.generateNewTreatmentId();
                if (newId != null && !newId.isEmpty()) {
                    txtTreatmentId.setText(newId);
                    lblTreatmentIdDisplay.setText("Treatment ID: " + newId);
                    txtTreatmentId.setEditable(false);
                    return;
                }
            }

            // Fallback to timestamp-based ID
            String newId = "TP" + String.format("%04d", System.currentTimeMillis() % 10000);
            txtTreatmentId.setText(newId);
            lblTreatmentIdDisplay.setText("Treatment ID: " + newId);
            txtTreatmentId.setEditable(false);

        } catch (Exception e) {
            System.err.println("Error generating treatment ID: " + e.getMessage());
        }
    }

    @FXML
    private void btnSearchPatientOnAction(ActionEvent actionEvent) {
        String patientId = txtPatientId.getText().trim();

        if (patientId.isEmpty()) {
            Common.showWarning("Please enter Patient ID");
            return;
        }

        try {
            // Search patient logic (you'll need to implement this)
            // For now, using mock validation
            if (patientId.matches("P\\d{3}")) {
                txtPatientName.setText("Patient Name - " + patientId);
                Common.showMessage("Patient loaded successfully");
            } else {
                Common.showError("No patient found with ID: " + patientId);
                txtPatientName.clear();
            }
        } catch (Exception e) {
            Common.showError("Error searching patient: " + e.getMessage());
        }
    }

    @FXML
    private void btnCreateTreatmentOnAction(ActionEvent actionEvent) {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Confirm treatment creation
            if (!Common.askQuestion("Are you sure you want to create this treatment plan?")) {
                return;
            }

            // Create treatment plan DTO
            TreatmentPlanDTO treatmentPlan = new TreatmentPlanDTO();
            treatmentPlan.setTreatmentId(txtTreatmentId.getText().trim());
            treatmentPlan.setPatientId(txtPatientId.getText().trim());
            treatmentPlan.setDoctorId(currentDoctorId);
            treatmentPlan.setDescription(txtDescription.getText().trim());
            treatmentPlan.setMedicines(txtMedicines.getText().trim());
            treatmentPlan.setProcedures(txtProcedures.getText().trim());
            treatmentPlan.setFollowUps(txtFollowUps.getText().trim());

            // Parse cost
            try {
                double cost = Double.parseDouble(txtCost.getText().trim());
                treatmentPlan.setCost(cost);
            } catch (NumberFormatException e) {
                treatmentPlan.setCost(0.0);
            }

            // Set status from combo box
            if (cmbStatus.getValue() != null) {
                treatmentPlan.setStatus(TreatmentPlanDTO.TreatmentStatus.valueOf(cmbStatus.getValue()));
            } else {
                treatmentPlan.setStatus(TreatmentPlanDTO.TreatmentStatus.PLANNED);
            }

            treatmentPlan.setCreatedDate(LocalDateTime.now());

            // Set dates using Common utility
            if (dpStartDate.getValue() != null) {
                treatmentPlan.setStartDate(dpStartDate.getValue().toString());
            }
            if (dpEndDate.getValue() != null) {
                treatmentPlan.setEndDate(dpEndDate.getValue().toString());
            }
            // Save treatment plan
            boolean success = treatmentPlanBO.saveTreatmentPlan(treatmentPlan);

            if (success) {
                Common.showMessage("Treatment plan created successfully!");
                clearForm();
                generateNewTreatmentId();
                loadRecentTreatments();
            } else {
                Common.showError("Failed to create treatment plan");
            }

        } catch (Exception e) {
            Common.showError("Error creating treatment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btnClearOnAction(ActionEvent actionEvent) {
        if (Common.askWarning("Are you sure you want to clear all fields?")) {
            clearForm();
            generateNewTreatmentId();
        }
    }

    @FXML
    private void btnCancelOnAction(ActionEvent actionEvent) {
        if (Common.askQuestion("Are you sure you want to cancel? Any unsaved changes will be lost.")) {
            // Close window or navigate back
            if (btnCancel.getScene() != null && btnCancel.getScene().getWindow() != null) {
                btnCancel.getScene().getWindow().hide();
            }
        }
    }

    @FXML
    private void cmbTreatmentTypeOnAction(ActionEvent actionEvent) {
        String selectedType = cmbTreatmentType.getValue();
        if (selectedType != null) {
            // Set default cost based on treatment type
            setDefaultCostForTreatmentType(selectedType);
        }
    }

    private void loadRecentTreatments() {
        try {
            if (treatmentPlanBO == null) {
                Common.showError("Treatment Plan service not initialized");
                return;
            }

            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getTreatmentPlansByDoctor(currentDoctorId);

            if (treatments == null) {
                System.out.println("No treatments found for doctor: " + currentDoctorId);
                return;
            }

            // Show only the last 10 treatments
            List<TreatmentPlanDTO> recentTreatments = treatments.size() > 10
                    ? treatments.subList(0, 10)
                    : treatments;

            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(recentTreatments);
            tblRecentTreatments.setItems(treatmentList);

            System.out.println("👨‍⚕️ DOCTOR: Loaded " + recentTreatments.size() + " recent treatments");

        } catch (Exception e) {
            Common.showError("Error loading recent treatments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (txtPatientId.getText().trim().isEmpty()) {
            errors.append("• Patient ID is required\n");
        }

        if (txtPatientName.getText().trim().isEmpty()) {
            errors.append("• Patient name is required (search patient first)\n");
        }

        if (cmbTreatmentType.getValue() == null) {
            errors.append("• Treatment type is required\n");
        }

        if (txtDescription.getText().trim().isEmpty()) {
            errors.append("• Treatment description is required\n");
        }

        if (!txtCost.getText().trim().isEmpty()) {
            try {
                double cost = Double.parseDouble(txtCost.getText().trim());
                if (cost < 0) {
                    errors.append("• Cost cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid cost format\n");
            }
        }

        if (errors.length() > 0) {
            Common.showWarning("Validation Errors:\n" + errors.toString());
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtPatientId.clear();
        txtPatientName.clear();
        cmbTreatmentType.setValue(null);
        txtDescription.clear();
        txtMedicines.clear();
        txtProcedures.clear();
        txtFollowUps.clear();
        txtCost.clear();
        cmbStatus.setValue("PLANNED");
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
    }

    private void setDefaultCostForTreatmentType(String treatmentType) {
        double defaultCost = 0.0;

        switch (treatmentType) {
            case "General Consultation":
                defaultCost = 2500.0;
                break;
            case "Cardiac Consultation":
            case "Neurological Consultation":
                defaultCost = 5000.0;
                break;
            case "Surgery Consultation":
                defaultCost = 7500.0;
                break;
            case "Laboratory Tests":
                defaultCost = 1500.0;
                break;
            case "Physical Therapy":
                defaultCost = 3000.0;
                break;
            case "Eye Consultation":
            case "Dental Consultation":
                defaultCost = 3500.0;
                break;
            default:
                defaultCost = 2000.0;
                break;
        }

        txtCost.setText(String.valueOf(defaultCost));
    }

    // Method to set doctor ID from external source (session/login)
    public void setDoctorId(String doctorId) {
        this.currentDoctorId = doctorId;
        txtDoctorId.setText(doctorId);
        loadRecentTreatments();
    }
}