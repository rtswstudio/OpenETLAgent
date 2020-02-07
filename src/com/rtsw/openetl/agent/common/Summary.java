package com.rtsw.openetl.agent.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RT Software Studio
 */
public class Summary {

    private String title;

    private String description;

    private List<Report> reports;

    public Summary() {
        this.reports = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

}
