package lk.usj.OPD_Management.java.dto;

import lk.usj.OPD_Management.java.observer.TreatmentObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TreatmentPlanDTO {
    private String treatmentId;
    private String patientId;
    private String doctorId;
    private String description;
    private String medicines;
    private String procedures;
    private String followUps;
    private TreatmentStatus status;
    private double cost;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
    private LocalDateTime billedDate;
    private List<TreatmentObserver> observers;

    public void setCreatedDate(LocalDateTime now) {

    }

    public void setPatientName(String trim) {
    }

    public void setTreatmentType(String value) {
    }

    public void setPrescription(String trim) {
    }

    public void setDuration(String trim) {
    }

    public void setPriority(String value) {
    }

    public void setStartDate(String string) {
    }

    public void setEndDate(String string) {

    }

    public enum TreatmentStatus {
        CREATED, IN_PROGRESS, COMPLETED, PLANNED, BILLED
    }

    public TreatmentPlanDTO() {
        this.observers = new ArrayList<>();
        this.status = TreatmentStatus.CREATED;
        this.createdDate = LocalDateTime.now();
    }

    // Observer Pattern Methods
    public void addObserver(TreatmentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TreatmentObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (TreatmentObserver observer : observers) {
            observer.update(this);
        }
    }

    // Status change with notification
    public void setStatus(TreatmentStatus status) {
        this.status = status;
        if (status == TreatmentStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        } else if (status == TreatmentStatus.BILLED) {
            this.billedDate = LocalDateTime.now();
        }
        notifyObservers();
    }

    // Getters and Setters
    public String getTreatmentId() { return treatmentId; }
    public void setTreatmentId(String treatmentId) { this.treatmentId = treatmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }

    public String getProcedures() { return procedures; }
    public void setProcedures(String procedures) { this.procedures = procedures; }

    public String getFollowUps() { return followUps; }
    public void setFollowUps(String followUps) { this.followUps = followUps; }

    public TreatmentStatus getStatus() { return status; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public LocalDateTime getBilledDate() { return billedDate; }
}
