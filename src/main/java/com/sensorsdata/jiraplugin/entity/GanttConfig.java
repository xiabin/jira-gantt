package com.sensorsdata.jiraplugin.entity;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table("config")
public interface GanttConfig extends Entity {
    String getSpacekey();
    void setSpaceKey(String spaceKey);

    Long getStartDateCustomFieldId();
    void setStartDateCustomFieldId(Long startDateCustomFieldId);

    Long getEndDateCustomFieldId();
    void setEndDateCustomFieldId(Long endDateCustomFieldId);

    String getJqlQuery();
    void setJqlQuery(String jqlQuery);

    String getName();
    void setName(String name);

}
