package lk.usj.OPD_Management.java.controller.billing;

public class BillGenerationHandler extends BillingHandler {
    @Override
    public void process(BillingRequest request) {
        System.out.println("Step 1: Bill Generation Handler");
        System.out.println("  - Bill generated for " + request.getPatientName() + " with a total of $" + request.getTotalAmount());

        // The bill is generated, so we pass it to the next step.
        passToNext(request);
    }
}
