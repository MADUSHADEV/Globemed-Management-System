package lk.usj.OPD_Management.java.mediator;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

public interface TreatmentMediator {
    void createTreatmentPlan(String patientId, String doctorId, String description,
                             String medicines, String procedures, String followUps);
    void completeTreatment(String treatmentId);
    void addTreatmentCost(String treatmentId, double cost);
    TreatmentPlanDTO getTreatmentPlan(String treatmentId);
    void restoreTreatmentFromHistory(String treatmentId, int historyIndex);
    void showTreatmentHistory(String treatmentId);
    void viewTreatmentReport(String treatmentId, String userRole);
}
