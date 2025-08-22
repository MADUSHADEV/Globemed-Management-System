package lk.usj.OPD_Management.java.controller.reportCommon.FinancialReport;

import lk.usj.OPD_Management.java.controller.reportCommon.CommonReport;
import lk.usj.OPD_Management.java.controller.reportCommon.CommonReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.helper.DataSet;

public class FinancialReportBuilder implements CommonReportBuilder {

    private CommonReport report;

    public FinancialReportBuilder() {
        this.report = new CommonReport();
    }


    @Override
    public void buildHeader() {
        report.addContent("****************************************");
        report.addContent("       GLOBEMED - FINANCIAL REPORT");
        report.addContent("****************************************");
    }

    @Override
    public void buildContent(DataSet data) {
        report.addContent("Patient Name: " + data.get("patientName"));
        report.addContent("Account #: " + data.get("accountId"));
        report.addContent("\n--- Billing Summary ---");
        report.addContent("Total Charges: $" + data.get("totalCharges"));
        report.addContent("Insurance Paid: $" + data.get("insurancePaid"));
        report.addContent("Patient Balance: $" + data.get("patientBalance"));
    }

    @Override
    public void buildFooter() {
        report.addContent("\n--- Please remit payment to GlobeMed Central Billing ---");
        report.addContent("****************************************");
    }

    @Override
    public CommonReport getReport() {
        return this.report;
    }
}
