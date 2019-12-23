package com.rtsw.openetl.agent.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RT Software Studio
 */
public class Summary {

    private List<Report> reports;

    public static class Factory {

        public static String json(Summary summary) {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            int i = 0;
            for (Report report : summary.getReports()) {
                if (i > 0) {
                    sb.append(",\n");
                }
                sb.append(Report.Factory.json(report));
                i++;
            }
            sb.append("\n]");
            return (sb.toString());
        }

    }

    public Summary() {
        this.reports = new ArrayList<>();
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

}
