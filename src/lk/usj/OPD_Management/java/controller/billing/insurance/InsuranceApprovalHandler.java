package lk.usj.OPD_Management.java.controller.billing.insurance;

import lk.usj.OPD_Management.java.controller.billing.BillingHandler;
import lk.usj.OPD_Management.java.controller.billing.BillingRequest;

public class InsuranceApprovalHandler extends BillingHandler {
    @Override
    public void process(BillingRequest request) {
        System.out.println("Step 3: Insurance Approval Handler");

        // Only run if the insurance has been successfully verified.
        if (request.isInsuranceVerified()) {
            System.out.println("  - Submitting for insurance approval...");
            // Simulate a successful approval
            request.setInsuranceApproved(true);
            System.out.println("  - Insurance coverage approved.");
        } else {
            System.out.println("  - SKIPPING: Insurance was not verified.");
        }

        passToNext(request);
    }
}
