package lk.usj.OPD_Management.java.observer;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

public class BillingObserver implements TreatmentObserver {
    @Override
    public void update(TreatmentPlanDTO treatmentPlan) {
        if (treatmentPlan.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED) {
            System.out.println("BILLING SYSTEM: Treatment " + treatmentPlan.getTreatmentId() +
                    " completed. Ready for cost calculation by receptionist.");
        } else if (treatmentPlan.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            System.out.println("BILLING SYSTEM: Processing payment for treatment " +
                    treatmentPlan.getTreatmentId() + " - Amount: $" + treatmentPlan.getCost());
            processInsuranceClaim(treatmentPlan);
        }
    }

    private void processInsuranceClaim(TreatmentPlanDTO treatmentPlan) {
        System.out.println("INSURANCE CLAIM: Submitted claim for patient " +
                treatmentPlan.getPatientId() + " - Amount: $" + treatmentPlan.getCost());
    }
}
