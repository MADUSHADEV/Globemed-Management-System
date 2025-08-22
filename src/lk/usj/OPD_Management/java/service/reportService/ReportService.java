package lk.usj.OPD_Management.java.service.reportService;


import lk.usj.OPD_Management.java.controller.reportCommon.CommonReport;
import lk.usj.OPD_Management.java.controller.reportCommon.CommonReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.FinancialReport.FinancialReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.MedicalReport.MedicalReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.TreatmentSummaryReport.TreatmentSummaryReportBuilder;
import lk.usj.OPD_Management.java.controller.reportCommon.helper.DataSet;
import lk.usj.OPD_Management.java.controller.reportCommon.helper.ReportType;

// This is the Service Layer (or "Director" in the Builder pattern).
// It coordinates the report building process.
public class ReportService {
    public CommonReport generateReport(ReportType type, DataSet data) {
        CommonReportBuilder builder = null;

        // Select the appropriate builder based on the report type
        switch (type) {
            case MEDICAL_HISTORY:
                builder = new MedicalReportBuilder();
                break;
            case FINANCIAL:
                builder = new FinancialReportBuilder();
                break;
            case TREATMENT_SUMMARY:
                builder = new TreatmentSummaryReportBuilder();
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }

        // Build the report
        builder.buildHeader();
        builder.buildContent(data);
        builder.buildFooter();

        // Return the completed report
        return builder.getReport();
    }
}
