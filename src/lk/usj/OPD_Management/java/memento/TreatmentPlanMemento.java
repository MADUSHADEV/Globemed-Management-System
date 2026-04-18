package lk.usj.OPD_Management.java.memento;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import java.time.LocalDateTime;

public class TreatmentPlanMemento {
    private final String treatmentId;
    private final String patientId;
    private final String doctorId;
    private final String description;
    private final String medicines;
    private final String procedures;
    private final String followUps;
    private final TreatmentPlanDTO.TreatmentStatus status;
    private final double cost;
    private final LocalDateTime timestamp;

    public TreatmentPlanMemento(TreatmentPlanDTO treatmentPlan) {
        this.treatmentId = treatmentPlan.getTreatmentId();
        this.patientId = treatmentPlan.getPatientId();
        this.doctorId = treatmentPlan.getDoctorId();
        this.description = treatmentPlan.getDescription();
        this.medicines = treatmentPlan.getMedicines();
        this.procedures = treatmentPlan.getProcedures();
        this.followUps = treatmentPlan.getFollowUps();
        this.status = treatmentPlan.getStatus();
        this.cost = treatmentPlan.getCost();
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getTreatmentId() { return treatmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDescription() { return description; }
    public String getMedicines() { return medicines; }
    public String getProcedures() { return procedures; }
    public String getFollowUps() { return followUps; }
    public TreatmentPlanDTO.TreatmentStatus getStatus() { return status; }
    public double getCost() { return cost; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
