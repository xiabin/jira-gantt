package com.sensorsdata.jiraplugin.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.sensorsdata.jiraplugin.entity.GanttConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Config extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Config.class);


    @ComponentImport
    private final ActiveObjects ao;

    public Config(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<GanttConfig> ganttConfigList = Arrays.asList(ao.find(GanttConfig.class));

        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){

    }

}
