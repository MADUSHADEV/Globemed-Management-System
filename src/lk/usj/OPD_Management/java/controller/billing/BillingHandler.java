package lk.usj.OPD_Management.java.controller.billing;

// The abstract Handler class.
public abstract class BillingHandler {
    protected BillingHandler next;

    // Sets the next handler in the chain.
    public void setNext(BillingHandler next) {
        this.next = next;
    }

    // The method to process the request.
    public abstract void process(BillingRequest request);

    // Helper method to safely pass the request to the next handler.
    protected void passToNext(BillingRequest request) {
        if (next != null) {
            next.process(request);
        }
    }
}