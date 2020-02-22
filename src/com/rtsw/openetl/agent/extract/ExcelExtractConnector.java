package com.rtsw.openetl.agent.extract;

import com.rtsw.openetl.agent.api.AgentListener;
import com.rtsw.openetl.agent.api.ExtractConnector;
import com.rtsw.openetl.agent.common.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 *
 * @author RT Software Studio
 */
public class ExcelExtractConnector implements ExtractConnector {

    private static final String ID = ExcelExtractConnector.class.getName();

    private Report report = new Report(ID);

    private String source;

    private String filenamePattern;

    private  String sheetPattern;

    private boolean header = true;

    private boolean recursive = false;

    private AgentListener agentListener;

    @Override
    public String getId() {
        return (ID);
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        report.start();

        // required
        source = configuration.get("source", null);
        if (source == null) {
            throw new Exception("missing required parameter 'source'");
        }

        // required
        filenamePattern = configuration.get("filename_pattern", null);
        if (filenamePattern == null) {
            throw new Exception("missing required parameter 'filename_pattern'");
        }

        // optional
        sheetPattern = configuration.get("sheet_pattern", null);

        // optional
        header = configuration.get("header", true);

        // optional
        recursive = configuration.get("recursive", false);

    }

    @Override
    public void extract(AgentListener agentListener) {

        // event listener
        this.agentListener = agentListener;

        File base = new File(source);

        // check that source exists
        if (!base.exists()) {
            report.error(String.format("Source '%s' does not exist", source));
            return;
        }

        // check that source is readable
        if (!base.canRead()) {
            report.error(String.format("Source '%s' is not readable", source));
            return;
        }

        // check that source is directory
        if (!base.isDirectory()) {
            report.error(String.format("Source '%s' is not a directory", source));
            return;
        }

        agentListener.onStart();

        if (recursive) {
            doTravel(base);
        } else {
            for (File file : base.listFiles()) {
                doFile(file);
            }
        }

        agentListener.onEnd();

    }

    @Override
    public void clean() {
        report.end();
    }

    @Override
    public Report report() {
        return (report);
    }

    private void doTravel(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                doTravel(f);
            }
        } else {
            doFile(file);
        }
    }

    private void doFile(File file) {
        if (!file.getName().matches(filenamePattern)) {
            return;
        }
        if (file.getName().toLowerCase().endsWith(".xls")) {
            doXLS(file);
            return;
        }
        if (file.getName().toLowerCase().endsWith(".xlsx")) {
            doXLSX(file);
            return;
        }
        report.error(String.format("Unsupported Excel file '%s", file.getAbsolutePath()));
    }

    private void doXLS(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(in);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheetPattern != null && sheet.getSheetName() != null) {
                    if (!sheet.getSheetName().matches(sheetPattern)) {
                        continue;
                    }
                }
                int j = 0;
                Table table = null;
                for (Row row : sheet) {
                    if (j == 0) {
                        List<Column> columns = new ArrayList<>();
                        if (header) {
                            int k = 0;
                            for (Cell cell : row) {
                                switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                                    case NUMERIC: columns.add(new Column("" + cell.getNumericCellValue(), "String", "java.lang.String")); break;
                                    case STRING: columns.add(new Column(cell.getStringCellValue(), "String", "java.lang.String")); break;
                                    case BOOLEAN: columns.add(new Column("" + cell.getBooleanCellValue(), "String", "java.lang.String")); break;
                                    case BLANK: columns.add(new Column("" + k, "String", "java.lang.String")); break;
                                }
                                k++;
                            }
                            table = new Table(sheet.getSheetName() == null ? "" + i : sheet.getSheetName(), columns);
                            agentListener.onTable(table);
                        } else {
                            for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                                columns.add(new Column("" + k, "String", "java.lang.String"));
                            }
                            table = new Table(sheet.getSheetName() == null ? "" + i : sheet.getSheetName(), columns);
                            agentListener.onTable(table);
                            List<Object> items = new ArrayList<>();
                            for (Cell cell : row) {
                                items.add(getItemFromCell(cell, formulaEvaluator));
                            }
                            agentListener.onRow(table, new com.rtsw.openetl.agent.common.Row(items));
                        }
                        report.column(columns.size());
                    } else {
                        List<Object> items = new ArrayList<>();
                        for (Cell cell : row) {
                            items.add(getItemFromCell(cell, formulaEvaluator));
                        }
                        agentListener.onRow(table, new com.rtsw.openetl.agent.common.Row(items));
                    }
                    report.row();
                    j++;
                }
                report.table();
            }
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                report.warning(e.getMessage());
            }
        }
    }

    private void doXLSX(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheetPattern != null && sheet.getSheetName() != null) {
                    if (!sheet.getSheetName().matches(sheetPattern)) {
                        continue;
                    }
                }
                int j = 0;
                Table table = null;
                for (Iterator<Row> i1 = sheet.iterator(); i1.hasNext(); ) {
                    Row row = i1.next();
                    if (j == 0) {
                        List<Column> columns = new ArrayList<>();
                        if (header) {
                            int k = 0;
                            for (Cell cell : row) {
                                switch (cell.getCellType()) {
                                    case NUMERIC: columns.add(new Column("" + cell.getNumericCellValue(), "String", "java.lang.String")); break;
                                    case STRING: columns.add(new Column(cell.getStringCellValue(), "String", "java.lang.String")); break;
                                    case BOOLEAN: columns.add(new Column("" + cell.getBooleanCellValue(), "String", "java.lang.String")); break;
                                    case BLANK: columns.add(new Column("" + k, "String", "java.lang.String")); break;
                                }
                                k++;
                            }
                            table = new Table(sheet.getSheetName() == null ? "" + i : sheet.getSheetName(), columns);
                            agentListener.onTable(table);
                        } else {
                            for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                                columns.add(new Column("" + k, "String", "java.lang.String"));
                            }
                            table = new Table(sheet.getSheetName() == null ? "" + i : sheet.getSheetName(), columns);
                            agentListener.onTable(table);
                            List<Object> items = new ArrayList<>();
                            for (Cell cell : row) {
                                items.add(getItemFromCell(cell));
                            }
                            agentListener.onRow(table, new com.rtsw.openetl.agent.common.Row(items));
                        }
                        report.column(columns.size());
                    } else {
                        List<Object> items = new ArrayList<>();
                        for (Cell cell : row) {
                            items.add(getItemFromCell(cell));
                        }
                        agentListener.onRow(table, new com.rtsw.openetl.agent.common.Row(items));
                    }
                    report.row();
                    j++;
                }
                report.table();
            }
        } catch (Exception e) {
            report.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                report.warning(e.getMessage());
            }
        }
    }

    private Object getItemFromCell(Cell cell, FormulaEvaluator formulaEvaluator) {
        switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
            case NUMERIC: return("" + cell.getNumericCellValue());
            case STRING: return(cell.getStringCellValue());
            case BOOLEAN: return("" + cell.getBooleanCellValue());
            case BLANK: return("");
            default: return(cell.toString());
        }
    }

    private Object getItemFromCell(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC: return("" + cell.getNumericCellValue());
            case STRING: return(cell.getStringCellValue());
            case BOOLEAN: return("" + cell.getBooleanCellValue());
            case BLANK: return("");
            default: return(cell.toString());
        }
    }

}
