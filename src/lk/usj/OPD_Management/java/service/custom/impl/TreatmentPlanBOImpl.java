package lk.usj.OPD_Management.java.service.custom.impl;

import lk.usj.OPD_Management.java.dao.DAOFactory;
import lk.usj.OPD_Management.java.dao.custom.TreatmentPlanDAO;
import lk.usj.OPD_Management.java.dao.custom.PatientDAO;
import lk.usj.OPD_Management.java.dao.custom.DoctorDAO;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import lk.usj.OPD_Management.java.service.custom.TreatmentPlanBO;
import lk.usj.OPD_Management.java.mediator.TreatmentMediator;
import lk.usj.OPD_Management.java.mediator.impl.TreatmentMediatorImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TreatmentPlanBOImpl implements TreatmentPlanBO {

    private static TreatmentPlanBOImpl instance;
    private TreatmentPlanDAO treatmentPlanDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private TreatmentMediator treatmentMediator;

    public TreatmentPlanBOImpl() {
        this.treatmentPlanDAO = (TreatmentPlanDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.TREATMENT_PLAN);
        if (treatmentPlanDAO == null) {
            throw new RuntimeException("Failed to initialize TreatmentPlanDAO");
        }

        this.patientDAO = (PatientDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.PATIENT);
        this.doctorDAO = (DoctorDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.DOCTOR);
        this.treatmentMediator = new TreatmentMediatorImpl();
    }


    public static synchronized TreatmentPlanBOImpl getInstance(){
        if(instance == null){
            instance = new TreatmentPlanBOImpl();
        }
        return instance;
    }

    // CRUD Operations
    @Override
    public boolean saveTreatmentPlan(TreatmentPlanDTO treatmentPlan) throws Exception {
        if (treatmentPlan == null) {
            throw new Exception("Treatment plan cannot be null");
        }

        // Validate patient and doctor exist
        if (!isPatientExists(treatmentPlan.getPatientId())) {
            throw new Exception("Patient not found: " + treatmentPlan.getPatientId());
        }

        if (!isDoctorExists(treatmentPlan.getDoctorId())) {
            throw new Exception("Doctor not found: " + treatmentPlan.getDoctorId());
        }

        // Generate treatment ID if not set
        if (treatmentPlan.getTreatmentId() == null || treatmentPlan.getTreatmentId().isEmpty()) {
            treatmentPlan.setTreatmentId("TRT-" + UUID.randomUUID().toString().substring(0, 8));
        }

        return treatmentPlanDAO.save(treatmentPlan);
    }

    @Override
    public boolean updateTreatmentPlan(TreatmentPlanDTO treatmentPlan) throws Exception {
        if (treatmentPlan == null || treatmentPlan.getTreatmentId() == null) {
            throw new Exception("Treatment plan or treatment ID cannot be null");
        }

        // Check if treatment exists
        TreatmentPlanDTO existingTreatment = treatmentPlanDAO.search(treatmentPlan.getTreatmentId());
        if (existingTreatment == null) {
            throw new Exception("Treatment plan not found: " + treatmentPlan.getTreatmentId());
        }

        return treatmentPlanDAO.update(treatmentPlan);
    }

    @Override
    public boolean deleteTreatmentPlan(String treatmentId) throws Exception {
        if (treatmentId == null || treatmentId.isEmpty()) {
            throw new Exception("Treatment ID cannot be null or empty");
        }

        // Check if treatment exists
        TreatmentPlanDTO existingTreatment = treatmentPlanDAO.search(treatmentId);
        if (existingTreatment == null) {
            throw new Exception("Treatment plan not found: " + treatmentId);
        }

        // Business rule: Cannot delete billed treatments
        if (existingTreatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            throw new Exception("Cannot delete billed treatment plans");
        }

        return treatmentPlanDAO.delete(treatmentId);
    }

    @Override
    public TreatmentPlanDTO getTreatmentPlan(String treatmentId) throws Exception {
        if (treatmentId == null || treatmentId.isEmpty()) {
            throw new Exception("Treatment ID cannot be null or empty");
        }

        return treatmentPlanDAO.search(treatmentId);
    }

    @Override
    public List<TreatmentPlanDTO> getAllTreatmentPlans() throws Exception {
        return treatmentPlanDAO.getAll();
    }

    // Doctor Operations
    @Override
    public List<TreatmentPlanDTO> getTreatmentPlansByDoctor(String doctorId) throws Exception {
        if (doctorId == null || doctorId.isEmpty()) {
            throw new Exception("Doctor ID cannot be null or empty");
        }

        if (!isDoctorExists(doctorId)) {
            throw new Exception("Doctor not found: " + doctorId);
        }

        return treatmentPlanDAO.getTreatmentsByDoctor(doctorId);
    }

    @Override
    public boolean createTreatmentPlan(String patientId, String doctorId, String description,
                                       String medicines, String procedures, String followUps) throws Exception {

        // Validate inputs
        if (patientId == null || patientId.isEmpty()) {
            throw new Exception("Patient ID cannot be null or empty");
        }
        if (doctorId == null || doctorId.isEmpty()) {
            throw new Exception("Doctor ID cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new Exception("Treatment description cannot be null or empty");
        }

        // Use mediator to create treatment plan
        treatmentMediator.createTreatmentPlan(patientId, doctorId, description, medicines, procedures, followUps);

        return true;
    }

    @Override
    public boolean completeTreatmentPlan(String treatmentId, String doctorId) throws Exception {
        if (treatmentId == null || treatmentId.isEmpty()) {
            throw new Exception("Treatment ID cannot be null or empty");
        }

        TreatmentPlanDTO treatment = treatmentPlanDAO.search(treatmentId);
        if (treatment == null) {
            throw new Exception("Treatment plan not found: " + treatmentId);
        }

        // Business rule: Only the assigned doctor can complete the treatment
        if (!treatment.getDoctorId().equals(doctorId)) {
            throw new Exception("Only the assigned doctor can complete this treatment");
        }

        // Business rule: Can only complete treatments that are in progress
        if (treatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED ||
                treatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            throw new Exception("Treatment is already completed or billed");
        }

        // Use mediator to complete treatment
        treatmentMediator.completeTreatment(treatmentId);

        return true;
    }

    // Patient Operations
    @Override
    public List<TreatmentPlanDTO> getTreatmentPlansByPatient(String patientId) throws Exception {
        if (patientId == null || patientId.isEmpty()) {
            throw new Exception("Patient ID cannot be null or empty");
        }

        if (!isPatientExists(patientId)) {
            throw new Exception("Patient not found: " + patientId);
        }

        return treatmentPlanDAO.getTreatmentsByPatient(patientId);
    }

    @Override
    public List<TreatmentPlanDTO> getCompletedTreatmentsByPatient(String patientId) throws Exception {
        return getTreatmentPlansByPatient(patientId).stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED ||
                        t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .collect(Collectors.toList());
    }

    // Receptionist Operations
    @Override
    public List<TreatmentPlanDTO> getCompletedTreatmentsForBilling() throws Exception {
        return treatmentPlanDAO.getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED);
    }

    @Override
    public boolean addTreatmentCost(String treatmentId, double cost) throws Exception {
        if (treatmentId == null || treatmentId.isEmpty()) {
            throw new Exception("Treatment ID cannot be null or empty");
        }

        if (!validateTreatmentCost(cost)) {
            throw new Exception("Invalid treatment cost: " + cost);
        }

        TreatmentPlanDTO treatment = treatmentPlanDAO.search(treatmentId);
        if (treatment == null) {
            throw new Exception("Treatment plan not found: " + treatmentId);
        }

        // Business rule: Can only add cost to completed treatments
        if (treatment.getStatus() != TreatmentPlanDTO.TreatmentStatus.COMPLETED) {
            throw new Exception("Can only add cost to completed treatments");
        }

        // Apply changes
        treatment.setCost(cost);
        treatment.setStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);

        // Update database
        return treatmentPlanDAO.update(treatment);
    }

    @Override
    public boolean updateTreatmentStatus(String treatmentId, TreatmentPlanDTO.TreatmentStatus status) throws Exception {
        if (treatmentId == null || treatmentId.isEmpty()) {
            throw new Exception("Treatment ID cannot be null or empty");
        }

        TreatmentPlanDTO treatment = treatmentPlanDAO.search(treatmentId);
        if (treatment == null) {
            throw new Exception("Treatment plan not found: " + treatmentId);
        }

        treatment.setStatus(status);
        return treatmentPlanDAO.update(treatment);
    }

    // Admin & Reporting Operations
    @Override
    public List<TreatmentPlanDTO> getBilledTreatments() throws Exception {
        return treatmentPlanDAO.getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate == null || endDate == null) {
            throw new Exception("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new Exception("Start date cannot be after end date");
        }

        return treatmentPlanDAO.getTreatmentsByDateRange(startDate, endDate);
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception {
        if (status == null) {
            throw new Exception("Status cannot be null");
        }

        return treatmentPlanDAO.getTreatmentsByStatus(status);
    }

    // Business Logic Validations
    @Override
    public boolean isPatientExists(String patientId) throws Exception {
        if (patientId == null || patientId.isEmpty()) {
            return false;
        }

        return patientDAO.search(patientId) != null;
    }

    @Override
    public boolean isDoctorExists(String doctorId) throws Exception {
        if (doctorId == null || doctorId.isEmpty()) {
            return false;
        }

        return doctorDAO.search(doctorId) != null;
    }

    @Override
    public boolean canModifyTreatment(String treatmentId, String userId, String userRole) throws Exception {
        TreatmentPlanDTO treatment = treatmentPlanDAO.search(treatmentId);
        if (treatment == null) {
            return false;
        }

        // Business rules for modification permissions
        switch (userRole.toUpperCase()) {
            case "DOCTOR":
                return treatment.getDoctorId().equals(userId) &&
                        treatment.getStatus() != TreatmentPlanDTO.TreatmentStatus.BILLED;
            case "RECEPTIONIST":
                return treatment.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED;
            case "ADMIN":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean validateTreatmentCost(double cost) throws Exception {
        return cost >= 0 && cost <= 999999.99; // Business rule: Cost must be between 0 and 999,999.99
    }

    // Statistics and Analytics
    @Override
    public int getTotalTreatmentCount() throws Exception {
        return treatmentPlanDAO.getAll().size();
    }

    @Override
    public double getTotalRevenue() throws Exception {
        List<TreatmentPlanDTO> billedTreatments = getBilledTreatments();
        return billedTreatments.stream()
                .mapToDouble(TreatmentPlanDTO::getCost)
                .sum();
    }

    @Override
    public double getTotalRevenueByDoctor(String doctorId) throws Exception {
        List<TreatmentPlanDTO> doctorTreatments = getTreatmentPlansByDoctor(doctorId);
        return doctorTreatments.stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .mapToDouble(TreatmentPlanDTO::getCost)
                .sum();
    }

    @Override
    public int getTreatmentCountByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception {
        return getTreatmentsByStatus(status).size();
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatmentsByDoctor(String doctorId, int limit) {
        try {
            if (doctorId == null || doctorId.isEmpty()) {
                return List.of();
            }

            List<TreatmentPlanDTO> allTreatments = treatmentPlanDAO.getTreatmentsByDoctor(doctorId);

            // Sort by created date (most recent first) and limit the results
            return allTreatments.stream()
                    .sorted((t1, t2) -> {
                        if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                        if (t1.getCreatedDate() == null) return 1;
                        if (t2.getCreatedDate() == null) return -1;
                        return t2.getCreatedDate().compareTo(t1.getCreatedDate());
                    })
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error getting recent treatments: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public String generateNewTreatmentId() {
        try {
            List<TreatmentPlanDTO> allTreatments = treatmentPlanDAO.getAll();

            if (allTreatments == null || allTreatments.isEmpty()) {
                return "TP001";
            }

            // Find the highest existing ID number
            int maxId = allTreatments.stream()
                    .mapToInt(treatment -> {
                        String id = treatment.getTreatmentId();
                        if (id != null && id.startsWith("TP") && id.length() > 2) {
                            try {
                                return Integer.parseInt(id.substring(2));
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                        return 0;
                    })
                    .max()
                    .orElse(0);

            // Generate next ID
            return String.format("TP%03d", maxId + 1);

        } catch (Exception e) {
            System.err.println("Error generating treatment ID: " + e.getMessage());
            // Return a timestamp-based ID as fallback
            return "TP" + String.format("%03d", (int)(System.currentTimeMillis() % 1000));
        }
    }
}
