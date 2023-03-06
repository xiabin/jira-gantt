package com.sensorsdata.jiraplugin.entity;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table("config")
public interface GanttConfig extends Entity {
    String getSpacekey();
    void setSpaceKey(String spaceKey);

    @NotNull
    Long getStartDateCustomFieldId();
    void setStartDateCustomFieldId(Long startDateCustomFieldId);

    @NotNull
    Long getEndDateCustomFieldId();
    void setEndDateCustomFieldId(Long endDateCustomFieldId);

    @NotNull
    String getJqlQuery();
    void setJqlQuery(String jqlQuery);

    @NotNull
    String getName();
    void setName(String name);

}
