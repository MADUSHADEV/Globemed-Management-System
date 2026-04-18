package lk.usj.OPD_Management.java.dao.custom.Impl;

import lk.usj.OPD_Management.java.dao.custom.TreatmentPlanDAO;
import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TreatmentPlanDAOImpl implements TreatmentPlanDAO {

    private String formatTreatmentPlan(TreatmentPlanDTO t) {
        return (t.getTreatmentId() != null ? t.getTreatmentId() : "") + "#" +
               (t.getPatientId() != null ? t.getPatientId() : "") + "#" +
               (t.getDoctorId() != null ? t.getDoctorId() : "") + "#" +
               (t.getDescription() != null ? t.getDescription() : "") + "#" +
               (t.getMedicines() != null ? t.getMedicines() : "") + "#" +
               (t.getProcedures() != null ? t.getProcedures() : "") + "#" +
               (t.getFollowUps() != null ? t.getFollowUps() : "") + "#" +
               (t.getStatus() != null ? t.getStatus().name() : TreatmentPlanDTO.TreatmentStatus.CREATED.name()) + "#" +
               t.getCost() + "#" +
               (t.getCreatedDate() != null ? t.getCreatedDate().toString() : "null") + "#" +
               (t.getCompletedDate() != null ? t.getCompletedDate().toString() : "null") + "#" +
               (t.getBilledDate() != null ? t.getBilledDate().toString() : "null");
    }

    @Override
    public boolean save(TreatmentPlanDTO treatmentPlan) throws Exception {
        try {
            if (treatmentPlan == null) {
                throw new Exception("Treatment plan cannot be null");
            }

            if (treatmentPlan.getTreatmentId() == null || treatmentPlan.getTreatmentId().isEmpty()) {
                ArrayList<TreatmentPlanDTO> all = getAll();
                int maxId = 0;
                for (TreatmentPlanDTO t : all) {
                    try {
                        String idStr = t.getTreatmentId().replace("TRT-", "");
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) maxId = id;
                    } catch (Exception e) {
                        // Ignore parsing errors for custom IDs
                    }
                }
                treatmentPlan.setTreatmentId("TRT-" + String.format("%08d", maxId + 1));
            } else {
                TreatmentPlanDTO existing = search(treatmentPlan.getTreatmentId());
                if (existing != null) {
                    return update(treatmentPlan);
                }
            }

            if (treatmentPlan.getCreatedDate() == null) {
                try {
                    java.lang.reflect.Field f = treatmentPlan.getClass().getDeclaredField("createdDate");
                    f.setAccessible(true);
                    f.set(treatmentPlan, LocalDateTime.now());
                } catch (Exception e) {}
            }

            File file = new File("TreatmentPlan.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(formatTreatmentPlan(treatmentPlan));
            bw.newLine();
            bw.close();
            
            System.out.println("💾 DAO: Saved treatment plan " + treatmentPlan.getTreatmentId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(TreatmentPlanDTO treatmentPlan) throws Exception {
        try {
            if (treatmentPlan == null || treatmentPlan.getTreatmentId() == null) {
                throw new Exception("Treatment plan or ID cannot be null");
            }

            ArrayList<TreatmentPlanDTO> all = getAll();
            boolean updated = false;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getTreatmentId().equals(treatmentPlan.getTreatmentId())) {
                    all.set(i, treatmentPlan);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                throw new Exception("Treatment plan not found: " + treatmentPlan.getTreatmentId());
            }

            File file = new File("TreatmentPlan.txt");
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (TreatmentPlanDTO t : all) {
                bw.write(formatTreatmentPlan(t));
                bw.newLine();
            }
            bw.close();

            System.out.println("✏️ DAO: Updated treatment plan " + treatmentPlan.getTreatmentId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String treatmentId) throws Exception {
        try {
            if (treatmentId == null || treatmentId.isEmpty()) {
                throw new Exception("Treatment ID cannot be null or empty");
            }

            ArrayList<TreatmentPlanDTO> all = getAll();
            boolean removed = all.removeIf(t -> t.getTreatmentId().equals(treatmentId));

            if (!removed) {
                throw new Exception("Treatment plan not found: " + treatmentId);
            }

            File file = new File("TreatmentPlan.txt");
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (TreatmentPlanDTO t : all) {
                bw.write(formatTreatmentPlan(t));
                bw.newLine();
            }
            bw.close();

            System.out.println("🗑️ DAO: Deleted treatment plan " + treatmentId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TreatmentPlanDTO search(String treatmentId) throws Exception {
        try {
            if (treatmentId == null || treatmentId.isEmpty()) {
                throw new Exception("Treatment ID cannot be null or empty");
            }

            TreatmentPlanDTO treatment = getAll().stream()
                    .filter(t -> t.getTreatmentId().equals(treatmentId))
                    .findFirst()
                    .orElse(null);

            if (treatment != null) {
                System.out.println("🔍 DAO: Found treatment plan " + treatmentId);
            } else {
                System.out.println("🔍 DAO: Treatment plan not found " + treatmentId);
            }
            return treatment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<TreatmentPlanDTO> getAll() throws Exception {
        ArrayList<TreatmentPlanDTO> treatments = new ArrayList<>();
        try {
            File file = new File("TreatmentPlan.txt");
            if (!file.exists()) {
                file.createNewFile();
                return treatments;
            }
            
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] details = line.split("#", -1);
                TreatmentPlanDTO treatment = new TreatmentPlanDTO();
                
                treatment.setTreatmentId(details[0]);
                treatment.setPatientId(details[1]);
                treatment.setDoctorId(details[2]);
                treatment.setDescription(details[3]);
                treatment.setMedicines(details[4]);
                treatment.setProcedures(details[5]);
                treatment.setFollowUps(details[6]);
                
                try {
                    treatment.setStatus(TreatmentPlanDTO.TreatmentStatus.valueOf(details[7]));
                } catch (Exception e) {
                    treatment.setStatus(TreatmentPlanDTO.TreatmentStatus.CREATED);
                }
                
                try {
                    treatment.setCost(Double.parseDouble(details[8]));
                } catch (Exception e) {
                    treatment.setCost(0.0);
                }
                
                if (!details[9].equals("null") && !details[9].isEmpty()) {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("createdDate");
                        f.setAccessible(true);
                        f.set(treatment, LocalDateTime.parse(details[9]));
                    } catch (Exception e) {}
                } else {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("createdDate");
                        f.setAccessible(true);
                        f.set(treatment, null);
                    } catch (Exception e) {}
                }
                if (!details[10].equals("null") && !details[10].isEmpty()) {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("completedDate");
                        f.setAccessible(true);
                        f.set(treatment, LocalDateTime.parse(details[10]));
                    } catch (Exception e) {}
                } else {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("completedDate");
                        f.setAccessible(true);
                        f.set(treatment, null);
                    } catch (Exception e) {}
                }
                
                if (!details[11].equals("null") && !details[11].isEmpty()) {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("billedDate");
                        f.setAccessible(true);
                        f.set(treatment, LocalDateTime.parse(details[11]));
                    } catch (Exception e) {}
                } else {
                    try {
                        java.lang.reflect.Field f = treatment.getClass().getDeclaredField("billedDate");
                        f.setAccessible(true);
                        f.set(treatment, null);
                    } catch (Exception e) {}
                }
                
                treatments.add(treatment);
            }
            scanner.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return treatments;
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByPatient(String patientId) throws Exception {
        if (patientId == null || patientId.isEmpty()) {
            throw new Exception("Patient ID cannot be null or empty");
        }
        return getAll().stream()
                .filter(t -> patientId.equals(t.getPatientId()))
                .sorted((t1, t2) -> {
                    if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                    if (t1.getCreatedDate() == null) return 1;
                    if (t2.getCreatedDate() == null) return -1;
                    return t2.getCreatedDate().compareTo(t1.getCreatedDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByDoctor(String doctorId) throws Exception {
        if (doctorId == null || doctorId.isEmpty()) {
            throw new Exception("Doctor ID cannot be null or empty");
        }
        return getAll().stream()
                .filter(t -> doctorId.equals(t.getDoctorId()))
                .sorted((t1, t2) -> {
                    if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                    if (t1.getCreatedDate() == null) return 1;
                    if (t2.getCreatedDate() == null) return -1;
                    return t2.getCreatedDate().compareTo(t1.getCreatedDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByStatus(TreatmentPlanDTO.TreatmentStatus status) throws Exception {
        if (status == null) {
            throw new Exception("Status cannot be null");
        }
        return getAll().stream()
                .filter(t -> status.equals(t.getStatus()))
                .sorted((t1, t2) -> {
                    if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                    if (t1.getCreatedDate() == null) return 1;
                    if (t2.getCreatedDate() == null) return -1;
                    return t2.getCreatedDate().compareTo(t1.getCreatedDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentPlanDTO> getTreatmentsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate == null || endDate == null) {
            throw new Exception("Start date and end date cannot be null");
        }
        return getAll().stream()
                .filter(t -> {
                    if (t.getCreatedDate() == null) return false;
                    LocalDate treatmentDate = t.getCreatedDate().toLocalDate();
                    return !treatmentDate.isBefore(startDate) && !treatmentDate.isAfter(endDate);
                })
                .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
                .collect(Collectors.toList());
    }

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
        return getAll().stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.CREATED ||
                        t.getStatus() == TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS)
                .sorted((t1, t2) -> {
                    if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                    if (t1.getCreatedDate() == null) return 1;
                    if (t2.getCreatedDate() == null) return -1;
                    return t1.getCreatedDate().compareTo(t2.getCreatedDate());
                })
                .collect(Collectors.toList());
    }

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
        return getTreatmentsByPatient(patientId).stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .mapToDouble(TreatmentPlanDTO::getCost)
                .sum();
    }

    @Override
    public double getTotalCostByDoctor(String doctorId) throws Exception {
        return getTreatmentsByDoctor(doctorId).stream()
                .filter(t -> t.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED)
                .mapToDouble(TreatmentPlanDTO::getCost)
                .sum();
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatments(int limit) throws Exception {
        return getAll().stream()
                .sorted((t1, t2) -> {
                    if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                    if (t1.getCreatedDate() == null) return 1;
                    if (t2.getCreatedDate() == null) return -1;
                    return t2.getCreatedDate().compareTo(t1.getCreatedDate());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatmentsByDoctor(String doctorId, int limit) throws Exception {
        return getTreatmentsByDoctor(doctorId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentPlanDTO> getRecentTreatmentsByPatient(String patientId, int limit) throws Exception {
        return getTreatmentsByPatient(patientId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static void clearAllData() {
        try {
            File file = new File("TreatmentPlan.txt");
            if (file.exists()) {
                PrintWriter writer = new PrintWriter(file);
                writer.print("");
                writer.close();
            }
            System.out.println("🧹 DAO: Cleared all treatment data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSampleData() {
        try {
            TreatmentPlanDAOImpl dao = new TreatmentPlanDAOImpl();
            
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
            dao.save(treatment1);

            TreatmentPlanDTO treatment2 = new TreatmentPlanDTO();
            treatment2.setTreatmentId("TRT-00000002");
            treatment2.setPatientId("PAT002");
            treatment2.setDoctorId("DOC002");
            treatment2.setDescription("Diabetes management plan");
            treatment2.setMedicines("Metformin 500mg, Insulin");
            treatment2.setProcedures("Blood glucose test, HbA1c test");
            treatment2.setFollowUps("Monthly check-ups");
            treatment2.setStatus(TreatmentPlanDTO.TreatmentStatus.IN_PROGRESS);
            dao.save(treatment2);

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
            dao.save(treatment3);

            System.out.println("✅ DAO: Sample data loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ DAO Error loading sample data: " + e.getMessage());
        }
    }
}
