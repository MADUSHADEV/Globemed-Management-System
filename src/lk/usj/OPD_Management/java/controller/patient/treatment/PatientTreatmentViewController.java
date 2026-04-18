package lk.usj.OPD_Management.java.controller.patient.treatment;

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

public class PatientTreatmentViewController implements Initializable {

    @FXML private TextField txtCurrentPatientId;
    @FXML private Button btnRefresh;
    @FXML private Button btnViewHistory;

    @FXML private TableView<TreatmentPlanDTO> tblMyTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDoctorId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colDescription;
    @FXML private TableColumn<TreatmentPlanDTO, String> colStatus;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colCreatedDate;
    @FXML private TableColumn<TreatmentPlanDTO, Double> colCost;

    @FXML private Label lblSelectedTreatment;
    @FXML private TextArea txtTreatmentDetails;

    @FXML private Tab tabCurrentTreatments;
    @FXML private Tab tabCompletedTreatments;
    @FXML private TableView<TreatmentPlanDTO> tblCompletedTreatments;
    @FXML private TableColumn<TreatmentPlanDTO, String> colCompletedTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colCompletedDoctorId;
    @FXML private TableColumn<TreatmentPlanDTO, String> colCompletedDescription;
    @FXML private TableColumn<TreatmentPlanDTO, LocalDateTime> colCompletedDate;
    @FXML private TableColumn<TreatmentPlanDTO, Double> colCompletedCost;

    private TreatmentPlanBO treatmentPlanBO;
    private String currentPatientId = "PAT001"; // This should come from session/login

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            treatmentPlanBO = (TreatmentPlanBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.TREATMENT_PLAN);

            // Set current patient ID
            txtCurrentPatientId.setText(currentPatientId);
            txtCurrentPatientId.setEditable(false);

            // Initialize table columns
            setupTableColumns();

            // Load patient's treatments
            loadPatientTreatments();
            loadCompletedTreatments();

            // Setup table selection listeners
            setupTableSelectionListeners();

            System.out.println("👤 PATIENT: Treatment view interface initialized");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Current treatments table
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        // Completed treatments table
        colCompletedTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colCompletedDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colCompletedDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCompletedDate.setCellValueFactory(new PropertyValueFactory<>("completedDate"));
        colCompletedCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void setupTableSelectionListeners() {
        tblMyTreatments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayTreatmentDetails(newSelection);
            }
        });

        tblCompletedTreatments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayTreatmentDetails(newSelection);
            }
        });
    }

    @FXML
    private void btnRefreshOnAction(ActionEvent actionEvent) {
        loadPatientTreatments();
        loadCompletedTreatments();
    }

    @FXML
    private void btnViewHistoryOnAction(ActionEvent actionEvent) {
        try {
            // Switch to completed treatments tab
            TabPane tabPane = (TabPane) tabCurrentTreatments.getTabPane();
            tabPane.getSelectionModel().select(tabCompletedTreatments);

            loadCompletedTreatments();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error viewing treatment history: " + e.getMessage());
        }
    }

    private void loadPatientTreatments() {
        try {
            List<TreatmentPlanDTO> treatments = treatmentPlanBO.getTreatmentPlansByPatient(currentPatientId);

            // Filter for current treatments (not completed or billed)
            List<TreatmentPlanDTO> currentTreatments = treatments.stream()
                    .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.CREATED ||
                            t.getStatus() == TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS)
                    .collect(java.util.stream.Collectors.toList());

            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(currentTreatments);
            tblMyTreatments.setItems(treatmentList);

            System.out.println("👤 PATIENT: Loaded " + currentTreatments.size() + " current treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading treatments: " + e.getMessage());
        }
    }

    private void loadCompletedTreatments() {
        try {
            List<TreatmentPlanDTO> completedTreatments = treatmentPlanBO.getCompletedTreatmentsByPatient(currentPatientId);
            ObservableList<TreatmentPlanDTO> treatmentList = FXCollections.observableArrayList(completedTreatments);
            tblCompletedTreatments.setItems(treatmentList);

            System.out.println("👤 PATIENT: Loaded " + completedTreatments.size() + " completed treatments");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading completed treatments: " + e.getMessage());
        }
    }

    private void displayTreatmentDetails(TreatmentPlanDTO treatment) {
        lblSelectedTreatment.setText("Treatment: " + treatment.getTreatmentId());

        StringBuilder details = new StringBuilder();
        details.append("Treatment ID: ").append(treatment.getTreatmentId()).append("\n");
        details.append("Doctor: ").append(treatment.getDoctorId()).append("\n");
        details.append("Status: ").append(treatment.getStatus()).append("\n");
        details.append("Created Date: ").append(treatment.getCreatedDate()).append("\n\n");

        details.append("DESCRIPTION:\n");
        details.append(treatment.getDescription()).append("\n\n");

        details.append("PRESCRIBED MEDICINES:\n");
        details.append(treatment.getMedicines()).append("\n\n");

        details.append("PROCEDURES:\n");
        details.append(treatment.getProcedures()).append("\n\n");

        details.append("FOLLOW-UP INSTRUCTIONS:\n");
        details.append(treatment.getFollowUps()).append("\n\n");

        if (treatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED) {
            details.append("Completed Date: ").append(treatment.getCompletedDate()).append("\n");
        }

        if (treatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            details.append("Completed Date: ").append(treatment.getCompletedDate()).append("\n");
            details.append("Billed Date: ").append(treatment.getBilledDate()).append("\n");
            details.append("Total Cost: $").append(treatment.getCost()).append("\n");
        }

        txtTreatmentDetails.setText(details.toString());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}