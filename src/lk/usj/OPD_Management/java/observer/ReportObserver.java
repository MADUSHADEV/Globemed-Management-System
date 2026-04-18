package lk.usj.OPD_Management.java.observer;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

public class ReportObserver implements TreatmentObserver {
    @Override
    public void update(TreatmentPlanDTO treatmentPlan) {
        if (treatmentPlan.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            System.out.println("MEDICAL REPORTS: Generating treatment report for patient " +
                    treatmentPlan.getPatientId());
            generateMedicalReport(treatmentPlan);
        }
    }

    private void generateMedicalReport(TreatmentPlanDTO treatmentPlan) {
        System.out.println("REPORT GENERATED: Treatment ID " + treatmentPlan.getTreatmentId() +
                " - Patient: " + treatmentPlan.getPatientId() +
                " - Doctor: " + treatmentPlan.getDoctorId() +
                " - Cost: $" + treatmentPlan.getCost());
    }
}
