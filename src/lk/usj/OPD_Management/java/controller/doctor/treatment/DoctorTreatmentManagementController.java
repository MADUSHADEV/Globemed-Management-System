package lk.usj.OPD_Management.java.controller.doctor.treatment;

import javafx.collections.FXCollections;
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

public class DoctorTreatmentManagementController implements Initializable {

    public Button btnCompleteTreatment;
    public Button btnCreateTreatment;
    public Button btnRefresh;
    @FXML private TableView<TreatmentPlanDTO> tblTreatments;
    @FXML private TableColumn<TreatmentPlanDTO,String> colTreatmentId;
    @FXML private TableColumn<TreatmentPlanDTO,String> colPatientId;
    @FXML private TableColumn<TreatmentPlanDTO,String> colDescription;
    @FXML private TableColumn<TreatmentPlanDTO,String> colStatus;
    @FXML private TableColumn<TreatmentPlanDTO,LocalDateTime> colCreatedDate;
    @FXML private TableColumn<TreatmentPlanDTO,Double> colCost;

    @FXML private TextField txtPatientId;
    @FXML private TextField txtDoctorId;
    @FXML private TextArea txtDescription;
    @FXML private TextArea txtMedicines;
    @FXML private TextArea txtProcedures;
    @FXML private TextArea txtFollowUps;

    private TreatmentPlanBO treatmentPlanBO;
    private String doctorId;
    private boolean ready;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            treatmentPlanBO = BOFactory.getBOFactory().getTreatmentPlanBO();
            if (treatmentPlanBO == null) throw new IllegalStateException("TreatmentPlanBO null");
            initTable();
            ready = true;
        } catch (Exception e) {
            e.printStackTrace();
            ready = false;
        }
    }

    public boolean prepare(String doctorId) {
        if (!ready) return false;
        this.doctorId = doctorId;
        if (txtDoctorId != null) {
            txtDoctorId.setText(doctorId);
        }
        refreshTable();
        return true;
    }

    private void initTable() {
        colTreatmentId.setCellValueFactory(new PropertyValueFactory<>("treatmentId"));
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void refreshTable() {
        if (treatmentPlanBO == null || doctorId == null) return;
        try {
            List<TreatmentPlanDTO> list = treatmentPlanBO.getTreatmentPlansByDoctor(doctorId);
            tblTreatments.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            Common.showError("Load failed: " + e.getMessage());
        }
    }

    @FXML
    private void btnCreateTreatmentOnAction(ActionEvent event) {
        if (treatmentPlanBO == null) { Common.showError("Service unavailable"); return; }
        if (txtPatientId.getText().isBlank() || txtDescription.getText().isBlank()) {
            Common.showWarning("Patient ID & Description required");
            return;
        }
        try {
            TreatmentPlanDTO dto = new TreatmentPlanDTO();
            dto.setTreatmentId(treatmentPlanBO.generateNewTreatmentId());
            dto.setPatientId(txtPatientId.getText().trim());
            dto.setDoctorId(doctorId);
            dto.setDescription(txtDescription.getText().trim());
            dto.setMedicines(txtMedicines.getText().trim());
            dto.setProcedures(txtProcedures.getText().trim());
            dto.setFollowUps(txtFollowUps.getText().trim());
            dto.setStatus(TreatmentPlanDTO.TreatmentStatus.PLANNED);
            dto.setCreatedDate(LocalDateTime.now());
            dto.setCost(0.0);
            if (treatmentPlanBO.saveTreatmentPlan(dto)) {
                Common.showMessage("Created");
                clearInputs();
                refreshTable();
            } else {
                Common.showError("Save failed");
            }
        } catch (Exception e) {
            Common.showError("Create failed: " + e.getMessage());
        }
    }

    @FXML
    private void btnCompleteTreatmentOnAction(ActionEvent event) {
        TreatmentPlanDTO sel = tblTreatments.getSelectionModel().getSelectedItem();
        if (sel == null) { Common.showWarning("Select a treatment"); return; }
        try {
            sel.setStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED);
            // Ideally call update method; fallback: save (if save handles upsert)
            treatmentPlanBO.saveTreatmentPlan(sel);
            Common.showMessage("Completed");
            refreshTable();
        } catch (Exception e) {
            Common.showError("Complete failed: " + e.getMessage());
        }
    }

    @FXML
    private void btnRefreshOnAction(ActionEvent event) {
        refreshTable();
    }

    private void clearInputs() {
        txtPatientId.clear();
        txtDescription.clear();
        txtMedicines.clear();
        txtProcedures.clear();
        txtFollowUps.clear();
    }
}