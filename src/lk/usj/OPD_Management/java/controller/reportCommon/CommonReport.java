package lk.usj.OPD_Management.java.controller.reportCommon;


/**
 * The final complex object that is being built.
 * Product class CommonReport.
 */
public class CommonReport {
    private final StringBuilder content = new StringBuilder();

    public void addContent(String text) {
        content.append(text).append("\n");
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
