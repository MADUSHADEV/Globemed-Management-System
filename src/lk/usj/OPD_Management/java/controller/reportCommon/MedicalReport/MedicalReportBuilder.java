package lk.usj.OPD_Management.java.controller.reportCommon.MedicalReport;

import lk.usj.OPD_Management.java.controller.reportCommon.CommonReport;
import lk.usj.OPD_Management.java.controller.reportCommon.CommonReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.helper.DataSet;

public class MedicalReportBuilder implements CommonReportBuilder {

    private CommonReport report;

    public MedicalReportBuilder() {
        this.report = new CommonReport();
    }

    @Override
    public void buildHeader() {
        report.addContent("========================================");
        report.addContent("       GLOBEMED - MEDICAL HISTORY REPORT");
        report.addContent("========================================");
    }

    @Override
    public void buildContent(DataSet data) {
        report.addContent("Patient Name: " + data.get("patientName"));
        report.addContent("Patient ID: " + data.get("patientId"));
        report.addContent("\n--- Allergies ---");
        report.addContent((String) data.get("allergies"));
        report.addContent("\n--- Past Conditions ---");
        report.addContent((String) data.get("pastConditions"));
    }

    @Override
    public void buildFooter() {
        report.addContent("\n--- End of Report ---");
        report.addContent("Generated on: " + java.time.LocalDate.now());
        report.addContent("========================================");
    }

    @Override
    public CommonReport getReport() {
        return this.report;
    }
}
