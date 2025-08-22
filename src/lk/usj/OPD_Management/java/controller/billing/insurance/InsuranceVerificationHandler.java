package lk.usj.OPD_Management.java.controller.billing.insurance;

import lk.usj.OPD_Management.java.controller.billing.BillingHandler;
import lk.usj.OPD_Management.java.controller.billing.BillingRequest;
import lk.usj.OPD_Management.java.enums.PaymentMethod;

public class InsuranceVerificationHandler extends BillingHandler {
    @Override
    public void process(BillingRequest request) {
        System.out.println("Step 2: Insurance Verification Handler");

        // **CORE LOGIC**: Only run if the patient is using insurance.
        if (request.getPaymentMethod() == PaymentMethod.INSURANCE && request.hasInsurance()) {
            System.out.println("  - Verifying insurance for " + request.getPatientName() + "...");
            // Simulate a successful verification
            request.setInsuranceVerified(true);
            System.out.println("  - Insurance Verified.");
        } else {
            System.out.println("  - SKIPPING: Patient is not using insurance.");
        }

        // Pass to the next handler regardless.
        passToNext(request);
    }
}