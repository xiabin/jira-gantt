package com.sensorsdata.jiraplugin.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class GanttIssue {
    private Long id;
    private String key;
    private String summary;
    private String assignee;
    private GanttCustomFiled startDate;
    private GanttCustomFiled endDate;

    private String dependency;

    private String issueBrowseUrl;

    private List<GanttIssue> children;

    private Boolean isParent;



    public void addChild(GanttIssue ganttIssue) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(ganttIssue);
    }

}
