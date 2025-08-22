package lk.usj.OPD_Management.java.controller.reportCommon.TreatmentSummaryReport;

import lk.usj.OPD_Management.java.controller.reportCommon.CommonReport;
import lk.usj.OPD_Management.java.controller.reportCommon.CommonReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.helper.DataSet;

public class TreatmentSummaryReportBuilder implements CommonReportBuilder {

    private CommonReport report;


    public TreatmentSummaryReportBuilder() {
        this.report = new CommonReport();
    }

    @Override
    public void buildHeader() {
        report.addContent("----------------------------------------");
        report.addContent("      GLOBEMED - TREATMENT SUMMARY");
        report.addContent("----------------------------------------");
    }

    @Override
    public void buildContent(DataSet data) {
        report.addContent("Patient Name: " + data.get("patientName"));
        report.addContent("Visit Date: " + data.get("visitDate"));
        report.addContent("\n--- Procedures Performed ---");
        report.addContent((String) data.get("procedures"));
        report.addContent("\n--- Prescribed Medications ---");
        report.addContent((String) data.get("medications"));
    }

    @Override
    public void buildFooter() {
        report.addContent("\n--- Please remit payment to GlobeMed Central Billing ---");
        report.addContent("----------------------------------------");
    }

    @Override
    public CommonReport getReport() {
        return this.report;
    }
}
