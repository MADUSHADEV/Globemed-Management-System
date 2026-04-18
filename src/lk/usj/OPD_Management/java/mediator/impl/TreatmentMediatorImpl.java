package lk.usj.OPD_Management.java.mediator.impl;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;
import lk.usj.OPD_Management.java.mediator.TreatmentMediator;
import lk.usj.OPD_Management.java.memento.TreatmentPlanHistory;
import lk.usj.OPD_Management.java.memento.TreatmentPlanMemento;
import lk.usj.OPD_Management.java.observer.BillingObserver;
import lk.usj.OPD_Management.java.observer.ReportObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TreatmentMediatorImpl implements TreatmentMediator {
    private Map<String, TreatmentPlanDTO> treatmentPlans;
    private Map<String, TreatmentPlanHistory> treatmentHistories;
    private BillingObserver billingObserver;
    private ReportObserver reportObserver;

    public TreatmentMediatorImpl() {
        this.treatmentPlans = new HashMap<>();
        this.treatmentHistories = new HashMap<>();
        this.billingObserver = new BillingObserver();
        this.reportObserver = new ReportObserver();
    }

    @Override
    public void createTreatmentPlan(String patientId, String doctorId, String description,
                                    String medicines, String procedures, String followUps) {
        String treatmentId = "TRT-" + UUID.randomUUID().toString().substring(0, 8);

        TreatmentPlanDTO treatmentPlan = new TreatmentPlanDTO();
        treatmentPlan.setTreatmentId(treatmentId);
        treatmentPlan.setPatientId(patientId);
        treatmentPlan.setDoctorId(doctorId);
        treatmentPlan.setDescription(description);
        treatmentPlan.setMedicines(medicines);
        treatmentPlan.setProcedures(procedures);
        treatmentPlan.setFollowUps(followUps);

        // Add observers
        treatmentPlan.addObserver(billingObserver);
        treatmentPlan.addObserver(reportObserver);

        // Save initial state using Memento
        TreatmentPlanHistory history = new TreatmentPlanHistory();
        history.saveState(new TreatmentPlanMemento(treatmentPlan));

        treatmentPlans.put(treatmentId, treatmentPlan);
        treatmentHistories.put(treatmentId, history);

        System.out.println("👨‍⚕️ DOCTOR: Created treatment plan " + treatmentId + " for patient " + patientId);
    }

    @Override
    public void completeTreatment(String treatmentId) {
        TreatmentPlanDTO treatmentPlan = treatmentPlans.get(treatmentId);
        if (treatmentPlan != null) {
            // Save state before change
            treatmentHistories.get(treatmentId).saveState(new TreatmentPlanMemento(treatmentPlan));

            treatmentPlan.setStatus(TreatmentPlanDTO.TreatmentStatus.COMPLETED);
            System.out.println("✅ DOCTOR: Marked treatment " + treatmentId + " as completed");
        }
    }

    @Override
    public void addTreatmentCost(String treatmentId, double cost) {
        TreatmentPlanDTO treatmentPlan = treatmentPlans.get(treatmentId);
        if (treatmentPlan != null && treatmentPlan.getStatus() == TreatmentPlanDTO.TreatmentStatus.COMPLETED) {
            // Save state before change
            treatmentHistories.get(treatmentId).saveState(new TreatmentPlanMemento(treatmentPlan));

            treatmentPlan.setCost(cost);
            treatmentPlan.setStatus(TreatmentPlanDTO.TreatmentStatus.BILLED);
            System.out.println("💼 RECEPTIONIST: Added cost $" + cost + " to treatment " + treatmentId);
        } else {
            System.out.println("❌ RECEPTIONIST: Cannot add cost. Treatment must be completed first.");
        }
    }

    @Override
    public TreatmentPlanDTO getTreatmentPlan(String treatmentId) {
        return treatmentPlans.get(treatmentId);
    }

    @Override
    public void restoreTreatmentFromHistory(String treatmentId, int historyIndex) {
        TreatmentPlanHistory history = treatmentHistories.get(treatmentId);
        TreatmentPlanMemento memento = history.getState(historyIndex);

        if (memento != null) {
            TreatmentPlanDTO treatmentPlan = treatmentPlans.get(treatmentId);

            // Restore state from memento
            treatmentPlan.setDescription(memento.getDescription());
            treatmentPlan.setMedicines(memento.getMedicines());
            treatmentPlan.setProcedures(memento.getProcedures());
            treatmentPlan.setFollowUps(memento.getFollowUps());
            treatmentPlan.setCost(memento.getCost());
            treatmentPlan.setStatus(memento.getStatus());

            System.out.println("🔄 SYSTEM: Restored treatment " + treatmentId + " to state from " + memento.getTimestamp());
        }
    }

    @Override
    public void showTreatmentHistory(String treatmentId) {
        TreatmentPlanHistory history = treatmentHistories.get(treatmentId);
        if (history != null) {
            history.showHistory();
        }
    }

    @Override
    public void viewTreatmentReport(String treatmentId, String userRole) {
        TreatmentPlanDTO treatmentPlan = treatmentPlans.get(treatmentId);
        if (treatmentPlan != null && treatmentPlan.getStatus() == TreatmentPlanDTO.TreatmentStatus.BILLED) {
            System.out.println("👀 " + userRole.toUpperCase() + ": Viewing treatment report for " + treatmentId);
            System.out.println("   Patient: " + treatmentPlan.getPatientId());
            System.out.println("   Doctor: " + treatmentPlan.getDoctorId());
            System.out.println("   Description: " + treatmentPlan.getDescription());
            System.out.println("   Cost: $" + treatmentPlan.getCost());
            System.out.println("   Status: " + treatmentPlan.getStatus());
        } else {
            System.out.println("❌ " + userRole.toUpperCase() + ": Treatment report not available yet.");
        }
    }
}
