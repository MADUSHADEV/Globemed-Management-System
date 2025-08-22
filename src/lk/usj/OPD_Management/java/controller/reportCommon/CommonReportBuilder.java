package lk.usj.OPD_Management.java.controller.reportCommon;

import lk.usj.OPD_Management.java.controller.reportCommon.helper.DataSet;

public interface CommonReportBuilder {
    void buildHeader();
    void buildContent(DataSet data);
    void buildFooter();
    CommonReport getReport();
}
