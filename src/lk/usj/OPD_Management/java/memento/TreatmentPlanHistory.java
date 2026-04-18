package lk.usj.OPD_Management.java.memento;

import java.util.ArrayList;
import java.util.List;

public class TreatmentPlanHistory {
    private List<TreatmentPlanMemento> history;

    public TreatmentPlanHistory() {
        this.history = new ArrayList<>();
    }

    public void saveState(TreatmentPlanMemento memento) {
        history.add(memento);
        System.out.println("MEMENTO: Treatment state saved at " + memento.getTimestamp());
    }

    public TreatmentPlanMemento getState(int index) {
        if (index >= 0 && index < history.size()) {
            return history.get(index);
        }
        return null;
    }

    public TreatmentPlanMemento getLatestState() {
        if (!history.isEmpty()) {
            return history.get(history.size() - 1);
        }
        return null;
    }

    public List<TreatmentPlanMemento> getAllHistory() {
        return new ArrayList<>(history);
    }

    public void showHistory() {
        System.out.println("TREATMENT HISTORY:");
        for (int i = 0; i < history.size(); i++) {
            TreatmentPlanMemento memento = history.get(i);
            System.out.println("  [" + i + "] " + memento.getTimestamp() +
                    " - Status: " + memento.getStatus() +
                    " - Cost: $" + memento.getCost());
        }
    }
}
