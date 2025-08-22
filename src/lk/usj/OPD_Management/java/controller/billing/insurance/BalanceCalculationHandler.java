package lk.usj.OPD_Management.java.controller.billing.insurance;

import lk.usj.OPD_Management.java.controller.billing.BillingHandler;
import lk.usj.OPD_Management.java.controller.billing.BillingRequest;

public class BalanceCalculationHandler extends BillingHandler {
    @Override
    public void process(BillingRequest request) {
        System.out.println("Step 4: Balance Calculation Handler");

        if (request.isInsuranceApproved()) {
            System.out.println("  - Calculating final balance after insurance...");
            double insuranceCoverage = request.getTotalAmount() * 0.8; // Simulate 80% coverage
            request.setAmountPaidByInsurance(insuranceCoverage);
            request.setRemainingBalance(request.getTotalAmount() - insuranceCoverage);
            System.out.println("  - Insurance will cover $" + insuranceCoverage + ". Patient owes $" + request.getRemainingBalance());
        } else {
            System.out.println("  - SKIPPING: No insurance approval.");
        }

        passToNext(request);
    }
}
