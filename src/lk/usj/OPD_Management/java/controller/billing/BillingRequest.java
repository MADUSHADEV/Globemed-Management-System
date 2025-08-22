package lk.usj.OPD_Management.java.controller.billing;

import lk.usj.OPD_Management.java.enums.PaymentMethod;

// The object that carries the data through the chain.
public class BillingRequest {
    private final String patientName;
    private final double totalAmount;
    private final PaymentMethod paymentMethod;
    private final boolean hasInsurance;

    // These fields will be modified by the handlers
    private boolean insuranceVerified = false;
    private boolean insuranceApproved = false;
    private double amountPaidByInsurance = 0.0;
    private double remainingBalance;
    private boolean isProcessed = false;

    public BillingRequest(String patientName, double totalAmount, PaymentMethod paymentMethod, boolean hasInsurance) {
        this.patientName = patientName;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.hasInsurance = hasInsurance;
        this.remainingBalance = totalAmount; // Initially, the patient owes the full amount
    }

    // Getters and Setters for all fields...
    public String getPatientName() {
        return patientName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public boolean hasInsurance() {
        return hasInsurance;
    }

    public boolean isInsuranceVerified() {
        return insuranceVerified;
    }

    public void setInsuranceVerified(boolean verified) {
        this.insuranceVerified = verified;
    }

    public boolean isInsuranceApproved() {
        return insuranceApproved;
    }

    public void setInsuranceApproved(boolean approved) {
        this.insuranceApproved = approved;
    }

    public double getAmountPaidByInsurance() {
        return amountPaidByInsurance;
    }

    public void setAmountPaidByInsurance(double amount) {
        this.amountPaidByInsurance = amount;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double balance) {
        this.remainingBalance = balance;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        this.isProcessed = processed;
    }

    @Override
    public String toString() {
        return "Billing Summary for " + patientName + ":\n" +
                "  - Total Bill: $" + totalAmount + "\n" +
                "  - Payment Method: " + paymentMethod + "\n" +
                "  - Insurance Verified: " + insuranceVerified + "\n" +
                "  - Insurance Approved: " + insuranceApproved + "\n" +
                "  - Paid by Insurance: $" + amountPaidByInsurance + "\n" +
                "  - Final Patient Balance: $" + remainingBalance + "\n" +
                "  - Is Processed: " + isProcessed;
    }
}
