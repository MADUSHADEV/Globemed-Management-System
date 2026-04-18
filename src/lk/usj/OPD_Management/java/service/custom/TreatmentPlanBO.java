package lk.usj.OPD_Management.java.service.custom;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import lk.usj.OPD_Management.java.service.SuperBO;

import java.time.LocalDate;
import java.util.List;

public interface TreatmentPlanBO extends SuperBO {

    // CRUD Operations
    boolean saveTreatmentPlan(TreatmentPlanDTO treatmentPlan) throws Exception;

    boolean updateTreatmentPlan(TreatmentPlanDTO treatmentPlan) throws Exception;

    boolean deleteTreatmentPlan(String treatmentId) throws Exception;

    TreatmentPlanDTO getTreatmentPlan(String treatmentId) throws Exception;

    List<TreatmentPlanDTO> getAllTreatmentPlans() throws Exception;

    // Doctor Operations
    List<TreatmentPlanDTO> getTreatmentPlansByDoctor(String doctorId) throws Exception;

    boolean createTreatmentPlan(String patientId, String doctorId, String description,
                                String medicines, String procedures, String followUps) throws Exception;

    boolean completeTreatmentPlan(String treatmentId, String doctorId) throws Exception;

    // Patient Operations
    List<TreatmentPlanDTO> getTreatmentPlansByPatient(String patientId) throws Exception;

    List<TreatmentPlanDTO> getCompletedTreatmentsByPatient(String patientId) throws Exception;

    // Receptionist Operations
    List<TreatmentPlanDTO> getCompletedTreatmentsForBilling() throws Exception;

    boolean addTreatmentCost(String treatmentId, double cost) throws Exception;

    boolean updateTreatmentStatus(String treatmentId, TreatmentPlanDTO.TreatmentStatus status) throws Exception;

    // Admin & Reporting Operations
    List<TreatmentPlanDTO> getBilledTreatments() throws Exception;

    List<TreatmentPlanDTO> getTreatmentsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception;

    List<TreatmentPlanDTO> getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception;

    // Business Logic Validations
    boolean isPatientExists(String patientId) throws Exception;

    boolean isDoctorExists(String doctorId) throws Exception;

    boolean canModifyTreatment(String treatmentId, String userId, String userRole) throws Exception;

    boolean validateTreatmentCost(double cost) throws Exception;

    // Statistics and Analytics
    int getTotalTreatmentCount() throws Exception;

    double getTotalRevenue() throws Exception;

    double getTotalRevenueByDoctor(String doctorId) throws Exception;

    int getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception;

    List<TreatmentPlanDTO> getRecentTreatmentsByDoctor(String currentDoctorId, int i);

    String generateNewTreatmentId()throws Exception;
}
