package com.sensorsdata.jiraplugin.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.sensorsdata.jiraplugin.entity.GanttConfig;
import com.sensorsdata.jiraplugin.entity.Todo;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Test extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Test.class);


    @ComponentImport
    private final ActiveObjects ao;


    public Test(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World1</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final GanttConfig ganttConfig = ao.create(GanttConfig.class); // (2)
        ganttConfig.setJqlQuery(new LocalDateTime().toString()); // (3)
        ganttConfig.save(); // (4)
//        log.info(ganttConfig.getJqlQuery());


        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World1</body></html>");
    }

}
