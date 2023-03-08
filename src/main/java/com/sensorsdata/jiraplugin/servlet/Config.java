package com.sensorsdata.jiraplugin.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.gson.Gson;
import com.sensorsdata.jiraplugin.entity.GanttConfig;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.builder.EntityManagerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Config extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Config.class);


    @ComponentImport
    private final ActiveObjects ao;
    private static final String LIST_ISSUES_TEMPLATE = "/templates/config/list.vm";
    private static final String NEW_ISSUE_TEMPLATE = "/templates/config/new.vm";
    private static final String EDIT_ISSUE_TEMPLATE = "/templates/config/edit.vm";

    @JiraImport
    private TemplateRenderer templateRenderer;

    public Config(ActiveObjects ao, TemplateRenderer templateRenderer) {
        this.ao = ao;
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = Optional.ofNullable(request.getParameter("actionType")).orElse("");

        Map<String, Object> context = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        switch (action) {
            case "new":
                templateRenderer.render(NEW_ISSUE_TEMPLATE, context, response.getWriter());
                break;
            case "edit":
                Integer id = Integer.valueOf(request.getParameter("id"));
                if (id == null || id <= 0) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    GanttConfig ganttConfig = ao.get(com.sensorsdata.jiraplugin.entity.GanttConfig.class, id);
                    context.put("ganttConfig", ganttConfig);
                    templateRenderer.render(EDIT_ISSUE_TEMPLATE, context, response.getWriter());

                }
                break;
            default:
                List<GanttConfig> ganttConfigList = Arrays.asList(ao.find(GanttConfig.class));
                context.put("ganttConfigList", ganttConfigList);
                for (GanttConfig ganttConfig : ganttConfigList) {
                    log.info("id is {}", ganttConfig.getID());
                    log.info("name is {}", ganttConfig.getName());
                    log.info("StartDateCustomFieldId is {}", ganttConfig.getStartDateCustomFieldId());
                    log.info("EndDateCustomFieldId is {}", ganttConfig.getEndDateCustomFieldId());
                    log.info("jql is {}", ganttConfig.getJqlQuery());
                }
                templateRenderer.render(LIST_ISSUES_TEMPLATE, context, response.getWriter());
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String actionType = request.getParameter("actionType");

        switch (actionType) {
            case "edit":
                handleIssueEdit(request, response);
                break;
            case "new":
                handleIssueCreation(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleIssueEdit(HttpServletRequest request, HttpServletResponse response) throws IOException {


        Integer id = Integer.valueOf(request.getParameter("id"));
        if (id == null || id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            GanttConfig ganttConfig = ao.get(com.sensorsdata.jiraplugin.entity.GanttConfig.class, id);

            String name = request.getParameter("name");
            Long startDateCustomFieldId = Long.valueOf(request.getParameter("startDateCustomFieldId"));
            Long endDateCustomFieldId = Long.valueOf(request.getParameter("endDateCustomFieldId"));
            String jql = request.getParameter("jql");

            log.info("name is {}", name);
            log.info("startDateCustomFieldId is {}", startDateCustomFieldId);
            log.info("endDateCustomFieldId is {}", endDateCustomFieldId);
            log.info("jql is {}", jql);

            ganttConfig.setName(name);
            ganttConfig.setStartDateCustomFieldId(startDateCustomFieldId);
            ganttConfig.setEndDateCustomFieldId(endDateCustomFieldId);
            ganttConfig.setJqlQuery(jql);
            ganttConfig.save();
            response.sendRedirect("config");

        }
    }

    private void handleIssueCreation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Enumeration<String> parameterNames = request.getParameterNames();

        // 遍历请求参数名，获取对应的参数值，并打印输出
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            log.info(paramName + " = " + paramValue);
        }
        String name = request.getParameter("name");
        Long startDateCustomFieldId = Long.valueOf(request.getParameter("startDateCustomFieldId"));
        Long endDateCustomFieldId = Long.valueOf(request.getParameter("endDateCustomFieldId"));
        String jql = request.getParameter("jql");

        log.info("name is {}", name);
        log.info("startDateCustomFieldId is {}", startDateCustomFieldId);
        log.info("endDateCustomFieldId is {}", endDateCustomFieldId);
        log.info("jql is {}", jql);

        final GanttConfig ganttConfig = ao.create(GanttConfig.class); // (2)
        ganttConfig.setName(name); // (3)
        ganttConfig.setStartDateCustomFieldId(startDateCustomFieldId);
        ganttConfig.setEndDateCustomFieldId(endDateCustomFieldId);
        ganttConfig.setJqlQuery(jql);
        ganttConfig.save(); // (4)
        response.sendRedirect("config");

    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String respStr;
        Integer id = Integer.valueOf(request.getParameter("id"));
        if (id == null || id <= 0) {
            respStr = "{ \"success\" : \"false\", error: \"id is empty\"}";
        } else {


            GanttConfig ganttConfig = ao.get(com.sensorsdata.jiraplugin.entity.GanttConfig.class, id);
            if (ganttConfig != null) {
                ao.delete(ganttConfig);
                respStr = "{ \"success\" : \"true\" }";
            } else {
                respStr = "{ \"success\": \"false\", error:  \"can not find by id\" }";

            }
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(respStr);
    }
}
