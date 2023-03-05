package com.sensorsdata.jiraplugin.entity;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("config")
public interface GanttConfig extends Entity {
    String getSpacekey();

    void setSpaceKey(String spaceKey);

    Long getStartDataCustomFieldId();

    void setStartDataCustomFieldId(Long startDataCustomFieldId);

    Long geEndDataCustomFieldId();

    void setEndDataCustomFieldId(Long endDataCustomFieldId);

    String getJqlQuery();

    void setJqlQuery(String jqlQuery);

}
