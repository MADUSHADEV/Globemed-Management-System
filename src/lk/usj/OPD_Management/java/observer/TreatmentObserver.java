package lk.usj.OPD_Management.java.observer;

import lk.usj.OPD_Management.java.dto.TreatmentPlanDTO;

public interface TreatmentObserver {
    void update(TreatmentPlanDTO treatmentPlan);
}