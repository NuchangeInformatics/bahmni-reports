package org.bahmni.dhis.controller;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class DhisController {
    private static Logger logger = Logger.getLogger(DhisController.class);

    @Autowired
    private DataSource dataSource;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void printHello(HttpServletResponse response) throws Exception {
        try {
            writeExcelToResponse(response);
        } catch(Exception e) {
            logger.error(e);
            throw e;
        }
    }

    private void writeExcelToResponse(HttpServletResponse response) throws IOException, DRException, SQLException {
        JasperReportBuilder report = DynamicReports.report();

        // TODO : pick up columns from json config
        report.columns(
                Columns.column("Identifier", "patient_id", DataTypes.integerType()),
                Columns.column("Created On", "date_created", DataTypes.dateType()),
                Columns.column("Created By", "creator", DataTypes.stringType())
        )
                .title(Components.text("Rohan's dynamic report").setHorizontalAlignment(HorizontalAlignment.CENTER))
//                .pageFooter(Components.pageXofY())
                .setReportName("Test Report")
                .setDataSource("select * from patient limit 20", dataSource.getConnection()); // TODO : pick this up from a file.

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=test.xls");
        ServletOutputStream outputStream = response.getOutputStream();
        report.toCsv(outputStream);

        response.flushBuffer();
        outputStream.close();
    }

}
