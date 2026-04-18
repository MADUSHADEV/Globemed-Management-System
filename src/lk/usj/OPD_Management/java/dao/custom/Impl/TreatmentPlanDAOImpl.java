package lk.usj.OPD_Management.java.dao.custom.Impl;

import lk.usj.OPD_Management.java.dao.custom.TreatmentPlanDAO;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TreatmentPlanDAOImpl implements TreatmentPlanDAO {

    // In-memory storage for demonstration (replace with database in production)
    private static Map<String, TreatmentPlanDTO> treatmentStorage = new HashMap<>();
    private static int idCounter = 1;

    // CRUD Operations
    @Override
    public boolean save(TreatmentPlanDTO treatmentPlan) throws Exception {
        try {
            if (treatmentPlan == null) {
                throw new Exception("Treatment plan cannot be null");
            }

            // Generate ID if not present
            if (treatmentPlan.getTreatmentId() == null || treatmentPlan.getTreatmentId().isEmpty()) {
                treatmentPlan.setTreatmentId("TRT-" + String.format("%08d", idCounter++));
            }

            // Set creation timestamp
            if (treatmentPlan.getCreatedDate() == null) {
                treatmentPlan.setCreatedDate(LocalDateTime.now());
            }

            treatmentStorage.put(treatmentPlan.getTreatmentId(), treatmentPlan);
            System.out.println("💾 DAO: Saved treatment plan " + treatmentPlan.getTreatmentId());
            return true;

        } catch (Exception e) {
            System.err.println("❌ DAO Error saving treatment plan: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean update(TreatmentPlanDTO treatmentPlan) throws Exception {
        try {
            if (treatmentPlan == null || treatmentPlan.getTreatmentId() == null) {
                throw new Exception("Treatment plan or ID cannot be null");
            }

            if (!treatmentStorage.containsKey(treatmentPlan.getTreatmentId())) {
                throw new Exception("Treatment plan not found: " + treatmentPlan.getTreatmentId());
            }

            treatmentStorage.put(treatmentPlan.getTreatmentId(), treatmentPlan);
            System.out.println("✏️ DAO: Updated treatment plan " + treatmentPlan.getTreatmentId());
            return true;

        } catch (Exception e) {
            System.err.println("❌ DAO Error updating treatment plan: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean delete(String treatmentId) throws Exception {
        try {
            if (treatmentId == null || treatmentId.isEmpty()) {
                throw new Exception("Treatment ID cannot be null or empty");
            }

            if (!treatmentStorage.containsKey(treatmentId)) {
                throw new Exception("Treatment plan not found: " + treatmentId);
            }

            treatmentStorage.remove(treatmentId);
            System.out.println("🗑️ DAO: Deleted treatment plan " + treatmentId);
            return true;

        } catch (Exception e) {
            System.err.println("❌ DAO Error deleting treatment plan: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public TreatmentPlanDTO search(String treatmentId) throws Exception {
        try {
            if (treatmentId == null || treatmentId.isEmpty()) {
                throw new Exception("Treatment ID cannot be null or empty");
            }

            TreatmentPlanDTO treatment = treatmentStorage.get(treatmentId);
            if (treatment != null) {
                System.out.println("🔍 DAO: Found treatment plan " + treatmentId);
            } else {
                System.out.println("🔍 DAO: Treatment plan not found " + treatmentId);
            }
            return treatment;

        } catch (Exception e) {
            System.err.println("❌ DAO Error searching treatment plan: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public ArrayList<TreatmentPlanDTO> getAll() throws Exception {
        try {
            ArrayList<TreatmentPlanDTO> treatments = new ArrayList<>(treatmentStorage.values());
            System.out.println("📋 DAO: Retrieved " + treatments.size() + " treatment plans");
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting all treatment plans: " + e.getMessage());
            throw e;
        }
    }

    // Extended search operations
    @Override
    public List<TreatmentPlanDTO> getTreatmentsByPatient(String patientId) throws Exception {
        try {
            if (patientId == null || patientId.isEmpty()) {
                throw new Exception("Patient ID cannot be null or empty");
            }

            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .filter(t -> patientId.equals(t.getPatientId()))
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate())) // Latest first
                    .collect(Collectors.toList());

            System.out.println("👤 DAO: Found " + treatments.size() + " treatments for patient " + patientId);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting treatments by patient: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByDoctor(String doctorId) throws Exception {
        try {
            if (doctorId == null || doctorId.isEmpty()) {
                throw new Exception("Doctor ID cannot be null or empty");
            }

            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .filter(t -> doctorId.equals(t.getDoctorId()))
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate())) // Latest first
                    .collect(Collectors.toList());

            System.out.println("👨‍⚕️ DAO: Found " + treatments.size() + " treatments for doctor " + doctorId);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting treatments by doctor: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception {
        try {
            if (status == null) {
                throw new Exception("Status cannot be null");
            }

            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .filter(t -> status.equals(t.getStatus()))
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate())) // Latest first
                    .collect(Collectors.toList());

            System.out.println("📊 DAO: Found " + treatments.size() + " treatments with status " + status);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting treatments by status: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        try {
            if (startDate == null || endDate == null) {
                throw new Exception("Start date and end date cannot be null");
            }

            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .filter(t -> {
                        LocalDate treatmentDate = t.getCreatedDate().toLocalDate();
                        return !treatmentDate.isBefore(startDate) && !treatmentDate.isAfter(endDate);
                    })
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate())) // Latest first
                    .collect(Collectors.toList());

            System.out.println("📅 DAO: Found " + treatments.size() + " treatments between " + startDate + " and " + endDate);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting treatments by date range: " + e.getMessage());
            throw e;
        }
    }

    // Advanced queries
    @Override
    public List<TreatmentPlanDTO> getCompletedTreatments() throws Exception {
        return getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED);
    }

    @Override
    public List<TreatmentPlanDTO> getBilledTreatments() throws Exception {
        return getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);
    }

    @Override
    public List<TreatmentPlanDTO> getPendingTreatments() throws Exception {
        try {
            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.CREATED ||
                            t.getStatus() == TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS)
                    .sorted((t1, t2) -> t1.getCreatedDate().compareTo(t2.getCreatedDate())) // Oldest first for pending
                    .collect(Collectors.toList());

            System.out.println("⏳ DAO: Found " + treatments.size() + " pending treatments");
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting pending treatments: " + e.getMessage());
            throw e;
        }
    }

    // Statistics queries
    @Override
    public int getPatientTreatmentCount(String patientId) throws Exception {
        return getTreatmentsByPatient(patientId).size();
    }

    @Override
    public int getDoctorTreatmentCount(String doctorId) throws Exception {
        return getTreatmentsByDoctor(doctorId).size();
    }

    @Override
    public double getTotalCostByPatient(String patientId) throws Exception {
        try {
            double totalCost = getTreatmentsByPatient(patientId).stream()
                    .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                    .mapToDouble(TreatmentPlanDTO::getCost)
                    .sum();

            System.out.println("💰 DAO: Total cost for patient " + patientId + ": $" + totalCost);
            return totalCost;

        } catch (Exception e) {
            System.err.println("❌ DAO Error calculating total cost by patient: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public double getTotalCostByDoctor(String doctorId) throws Exception {
        try {
            double totalCost = getTreatmentsByDoctor(doctorId).stream()
                    .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                    .mapToDouble(TreatmentPlanDTO::getCost)
                    .sum();

            System.out.println("💰 DAO: Total revenue for doctor " + doctorId + ": $" + totalCost);
            return totalCost;

        } catch (Exception e) {
            System.err.println("❌ DAO Error calculating total cost by doctor: " + e.getMessage());
            throw e;
        }
    }

    // Recent treatments
    @Override
    public List<TreatmentPlanDTO> getRecentTreatments(int limit) throws Exception {
        try {
            List<TreatmentPlanDTO> treatments = treatmentStorage.values().stream()
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate())) // Latest first
                    .limit(limit)
                    .collect(Collectors.toList());

            System.out.println("🕒 DAO: Retrieved " + treatments.size() + " recent treatments");
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting recent treatments: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatmentsByDoctor(String doctorId, int limit) throws Exception {
        try {
            List<TreatmentPlanDTO> treatments = getTreatmentsByDoctor(doctorId).stream()
                    .limit(limit)
                    .collect(Collectors.toList());

            System.out.println("🕒 DAO: Retrieved " + treatments.size() + " recent treatments for doctor " + doctorId);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting recent treatments by doctor: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatmentsByPatient(String patientId, int limit) throws Exception {
        try {
            List<TreatmentPlanDTO> treatments = getTreatmentsByPatient(patientId).stream()
                    .limit(limit)
                    .collect(Collectors.toList());

            System.out.println("🕒 DAO: Retrieved " + treatments.size() + " recent treatments for patient " + patientId);
            return treatments;

        } catch (Exception e) {
            System.err.println("❌ DAO Error getting recent treatments by patient: " + e.getMessage());
            throw e;
        }
    }

    // Utility methods for development/testing
    public static void clearAllData() {
        treatmentStorage.clear();
        idCounter = 1;
        System.out.println("🧹 DAO: Cleared all treatment data");
    }

    public static void loadSampleData() {
        try {
            System.out.println("📦 DAO: Loading sample treatment data...");

            // Sample treatment 1
            TreatmentPlanDTO treatment1 = new TreatmentPlanDTO();
            treatment1.setTreatmentId("TRT-00000001");
            treatment1.setPatientId("PAT001");
            treatment1.setDoctorId("DOC001");
            treatment1.setDescription("Respiratory infection treatment");
            treatment1.setMedicines("Amoxicillin 500mg, Cough syrup");
            treatment1.setProcedures("Chest X-ray, Blood test");
            treatment1.setFollowUps("Follow-up in 1 week");
            treatment1.setStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED);
            treatment1.setCost(250.00);
            treatmentStorage.put(treatment1.getTreatmentId(), treatment1);

            // Sample treatment 2
            TreatmentPlanDTO treatment2 = new TreatmentPlanDTO();
            treatment2.setTreatmentId("TRT-00000002");
            treatment2.setPatientId("PAT002");
            treatment2.setDoctorId("DOC002");
            treatment2.setDescription("Diabetes management plan");
            treatment2.setMedicines("Metformin 500mg, Insulin");
            treatment2.setProcedures("Blood glucose test, HbA1c test");
            treatment2.setFollowUps("Monthly check-ups");
            treatment2.setStatus(TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS);
            treatmentStorage.put(treatment2.getTreatmentId(), treatment2);

            // Sample treatment 3
            TreatmentPlanDTO treatment3 = new TreatmentPlanDTO();
            treatment3.setTreatmentId("TRT-00000003");
            treatment3.setPatientId("PAT001");
            treatment3.setDoctorId("DOC001");
            treatment3.setDescription("Hypertension control");
            treatment3.setMedicines("Lisinopril 10mg, Amlodipine 5mg");
            treatment3.setProcedures("Blood pressure monitoring");
            treatment3.setFollowUps("Weekly blood pressure checks");
            treatment3.setStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);
            treatment3.setCost(180.00);
            treatmentStorage.put(treatment3.getTreatmentId(), treatment3);

            idCounter = 4;
            System.out.println("✅ DAO: Sample data loaded successfully");

        } catch (Exception e) {
            System.err.println("❌ DAO Error loading sample data: " + e.getMessage());
        }
    }
}
