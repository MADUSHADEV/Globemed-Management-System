package lk.usj.OPD_Management.java.controller.billing;

public class PaymentCompletionHandler extends BillingHandler {
    @Override
    public void process(BillingRequest request) {
        System.out.println("Step 5: Payment Completion Handler");

        double amountToPay = request.getRemainingBalance();
        System.out.println("  - Processing payment of $" + String.format("%.2f", amountToPay) + " for " + request.getPatientName() + ".");

        // Finalize the process
        request.setProcessed(true);
        System.out.println("  - Payment Complete.");

        // This is the last handler, so we don't call passToNext().
    }
}
