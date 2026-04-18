package lk.usj.OPD_Management.java.dao.custom;

import lk.usj.OPD_Management.java.dao.CrudDAO;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

import java.time.LocalDate;
import java.util.List;

public interface TreatmentPlanDAO extends CrudDAO<TreatmentPlanDTO, String> {

    // Extended search operations
    List<TreatmentPlanDTO> getTreatmentsByPatient(String patientId) throws Exception;

    List<TreatmentPlanDTO> getTreatmentsByDoctor(String doctorId) throws Exception;

    List<TreatmentPlanDTO> getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception;

    List<TreatmentPlanDTO> getTreatmentsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception;

    // Advanced queries
    List<TreatmentPlanDTO> getCompletedTreatments() throws Exception;

    List<TreatmentPlanDTO> getBilledTreatments() throws Exception;

    List<TreatmentPlanDTO> getPendingTreatments() throws Exception;

    // Statistics queries
    int getPatientTreatmentCount(String patientId) throws Exception;

    int getDoctorTreatmentCount(String doctorId) throws Exception;

    double getTotalCostByPatient(String patientId) throws Exception;

    double getTotalCostByDoctor(String doctorId) throws Exception;

    // Recent treatments
    List<TreatmentPlanDTO> getRecentTreatments(int limit) throws Exception;

    List<TreatmentPlanDTO> getRecentTreatmentsByDoctor(String doctorId, int limit) throws Exception;

    List<TreatmentPlanDTO> getRecentTreatmentsByPatient(String patientId, int limit) throws Exception;
}