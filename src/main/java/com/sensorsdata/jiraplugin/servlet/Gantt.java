package com.sensorsdata.jiraplugin.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.query.Query;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.sensorsdata.jiraplugin.entity.GanttConfig;
import com.sensorsdata.jiraplugin.entity.GanttCustomFiled;
import com.sensorsdata.jiraplugin.entity.GanttIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Gantt extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Gantt.class);

    @JiraImport
    private TemplateRenderer templateRenderer;

    @JiraImport
    private PageBuilderService pageBuilderService;

    @JiraImport
    private SearchService searchService;


    @JiraImport
    private IssueService issueService;

    @JiraImport
    private JiraAuthenticationContext authenticationContext;

    @ComponentImport
    private final ActiveObjects ao;
    public Gantt(TemplateRenderer templateRenderer, PageBuilderService pageBuilderService, SearchService searchService, IssueService issueService, JiraAuthenticationContext authenticationContext, ActiveObjects ao) {
        this.templateRenderer = templateRenderer;
        this.pageBuilderService = pageBuilderService;
        this.searchService = searchService;
        this.authenticationContext = authenticationContext;
        this.issueService = issueService;
        this.ao = ao;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, RuntimeException {

        Locale userLocale = ComponentAccessor.getJiraAuthenticationContext().getLocale();

        ApplicationUser user = authenticationContext.getLoggedInUser();
        log.info("user is {}", user.toString());
        Map<String, Object> context = new HashMap<>();
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        String key = req.getParameter("key");
        log.info("Key is {}", key);



        String summary = req.getParameter("summary");
        log.info("summary is {}", summary);

        Integer config = Integer.valueOf(req.getParameter("config"));
        if (config == null || config <= 0) {
            // ???????????????????????????
            throw new IllegalArgumentException("config ?????????");
        }
        GanttConfig ganttConfig = ao.get(com.sensorsdata.jiraplugin.entity.GanttConfig.class, config);

        MutableIssue mutableIssue = issueService.getIssue(user, key).getIssue();

        Long startDateNameCustomFiledId = ganttConfig.getStartDateCustomFieldId();


        Long endDateNameCustomFiledId = ganttConfig.getEndDateCustomFieldId();

        Long startDateTimestamp = Long.valueOf(req.getParameter("startDate"));
        // ????????????????????????
        if (startDateTimestamp == null || startDateTimestamp <= 0) {
            // ???????????????????????????
            throw new IllegalArgumentException("startDate ?????????");
        }

        log.info("startDate is {}", startDateTimestamp);

        Long endDateTimestamp = Long.valueOf(req.getParameter("endDate"));
        // ????????????????????????
        if (endDateTimestamp == null || endDateTimestamp <= 0) {
            // ???????????????????????????
            throw new IllegalArgumentException("endDate ?????????");
        }

        log.info("startDateString is {}", endDateTimestamp);

        CustomField startCustomField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(startDateNameCustomFiledId);
        CustomField endCustomField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(endDateNameCustomFiledId);
        log.info("startCustomField is {} , endCustomField is {} ", startCustomField, endCustomField);

        if (startCustomField != null && endCustomField != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yy", userLocale);
            Instant instant = Instant.ofEpochMilli(startDateTimestamp);
            LocalDate startDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            instant = Instant.ofEpochMilli(endDateTimestamp);
            LocalDate endDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            String startFormattedDate = startDate.format(formatter);
            String endFormattedDate = endDate.format(formatter);

            issueInputParameters.addCustomFieldValue(startCustomField.getId(), startFormattedDate);
            issueInputParameters.addCustomFieldValue(endCustomField.getId(), endFormattedDate);
        } else {
            log.info("startCustomField or endCustomField is null");
        }

        if(summary != null && !summary.isEmpty()){
            issueInputParameters.setSummary(summary);
        }

        IssueService.UpdateValidationResult result = issueService.validateUpdate(user, mutableIssue.getId(), issueInputParameters);

        resp.setContentType("application/json;charset=UTF-8");

        JSONObject json = new JSONObject();
        if (result.getErrorCollection().hasAnyErrors()) {
            try {
                json.put("status", false);
                json.put("errors", result.getErrorCollection().getErrorMessages().stream().findFirst());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            issueService.update(user, result);

            try {
                json.put("status", true);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        PrintWriter out = resp.getWriter();
        out.print(json.toString());
        out.flush();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        Map<String, Object> context = new HashMap<>();
        Integer id = Integer.valueOf(request.getParameter("id"));

        // ????????????????????????
        if (id == null || id <= 0 ) {
            // ???????????????????????????
            throw new IllegalArgumentException("id ????????????");
        }

        GanttConfig ganttConfig = ao.get(GanttConfig.class,id);
        if(ganttConfig == null){
            throw new IllegalArgumentException("ganttConfig is null");
        }
        long startDateCustomFieldId = ganttConfig.getStartDateCustomFieldId();
        long endDateCustomFieldId = ganttConfig.getEndDateCustomFieldId();
        String jql = ganttConfig.getJqlQuery();
        log.info("startDateCustomFieldId is {}", startDateCustomFieldId);
        log.info("endDateCustomFieldId is {}", endDateCustomFieldId);
        log.info("jql is {}", jql);



        pageBuilderService.assembler().resources().requireWebResource("com.sensorsdata.jiraplugin.gantt:gantt-resources");
        List<Issue> issues = null;
        try {
            issues = getIssuesByJql(jql);
        } catch (JqlParseException e) {
            e.printStackTrace();
        }

        List<GanttIssue> ganttIssueList = new ArrayList<>();


        HashMap<String, String> childParentMap = this.getParentChildRelations(issues,ganttConfig.getLinkTypeId());
        log.info("issues is {}", issues.toString());
        String baseBrowseUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl"); // ?????? Jira ??????????????? URL

        for (Issue issue : issues) {

            log.info("key is {}", issue.getKey());
            GanttIssue ganttIssue = new GanttIssue();
            ganttIssue.setKey(issue.getKey());
            ganttIssue.setSummary(issue.getSummary());

            ganttIssue.setId(issue.getId());
            ApplicationUser assignee = issue.getAssignee();
            if (assignee != null) {
                ganttIssue.setAssignee(issue.getAssignee().getDisplayName());
            }
            String issueBrowseUrl = baseBrowseUrl + "/browse/" + issue.getKey(); // ???????????????????????????

            ganttIssue.setIssueBrowseUrl(issueBrowseUrl);
            ganttIssue.setStartDate(new GanttCustomFiled());
            ganttIssue.setEndDate(new GanttCustomFiled());
            ganttIssue.getStartDate().setCustomFiledId(startDateCustomFieldId);
            ganttIssue.getEndDate().setCustomFiledId(endDateCustomFieldId);

            CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(ganttIssue.getStartDate().getCustomFiledId());
            if (customField != null) {
                Object customFieldValue = issue.getCustomFieldValue(customField);
                ganttIssue.getStartDate().setValue(customFieldValue.toString());
            }

            if (ganttIssue.getStartDate().getValue().equals(null)) {
                throw new NullPointerException(String.format("customField is  undefine %l", ganttIssue.getStartDate().getCustomFiledId()));
            }

            customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(ganttIssue.getEndDate().getCustomFiledId());
            if (customField != null) {
                Object customFieldValue = issue.getCustomFieldValue(customField);
                ganttIssue.getEndDate().setValue(customFieldValue.toString());
            }


            if (ganttIssue.getEndDate().getValue().equals(null)) {
                throw new NullPointerException(String.format("customField is  undefine %l", ganttIssue.getEndDate().getCustomFiledId()));
            }

            if (childParentMap.containsKey(ganttIssue.getKey())) {
                ganttIssue.setDependency(childParentMap.get(ganttIssue.getKey()));
            } else {
                ganttIssue.setDependency("root");
            }
            ganttIssueList.add(ganttIssue);
        }

        //???????????????
        List<GanttIssue> ganttIssueTree = this.buildTree(ganttIssueList);

        ganttIssueList = this.flattenTree(ganttIssueTree);

        context.put("ganttIssueList", ganttIssueList);
        context.put("ganttIssueListJson", new Gson().toJson(ganttIssueList));

        context.put("name", ganttConfig.getName());


        String templatePath = "/templates/gantt.vm";
        templateRenderer.render(templatePath, context, response.getWriter());
    }


    /**
     * Retrieve issues using simple JQL query project="TUTORIAL"
     * Pagination is set to unlimited
     *
     * @return List of issues
     */
    private List<Issue> getIssuesByJql(String jqlString) throws JqlParseException {

        ApplicationUser user = authenticationContext.getLoggedInUser();
        JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
        JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class);
        Query query = jqlQueryParser.parseQuery(jqlString);
        PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();

        SearchResults searchResults = null;
        try {
            searchResults = searchService.search(user, query, pagerFilter);
        } catch (SearchException e) {
            e.printStackTrace();
        }
        List<Issue> issueList = (searchResults != null) ? searchResults.getResults() : null;
        return issueList;
    }


    private HashMap<String, String> getParentChildRelations(List<Issue> issues,Long linkTypeId) {

        HashMap<String, String> childParentMap = new HashMap<>();
        IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();

        for (Issue issue : issues) {
            List<IssueLink> inwardLinks = issueLinkManager.getInwardLinks(issue.getId());
            if (inwardLinks.size() > 0) {
                log.info("issue is {} inwardLinks is {}", issue.toString(), inwardLinks.toString());
            }
            for (IssueLink link : inwardLinks) {
                log.info("{} link type name is {}", issue.toString(), link.getIssueLinkType().getName());
                if (link.getIssueLinkType().getId().equals(linkTypeId)) {
                    Issue linkedIssue = link.getSourceObject();
                    childParentMap.put(issue.getKey(), linkedIssue.getKey());
                }
            }
        }
        log.info("childParentMap is {}", childParentMap.toString());
        return childParentMap;
    }

    private List<GanttIssue> buildTree(List<GanttIssue> ganttIssueList) {
        Map<String, List<GanttIssue>> parentChildMap = new HashMap<>();

        // ???????????????????????????????????????map
        for (GanttIssue ganttIssue : ganttIssueList) {
            List<GanttIssue> children = parentChildMap.computeIfAbsent(ganttIssue.getDependency(), k -> new ArrayList<>());
            children.add(ganttIssue);
        }
        List<GanttIssue> rootList = parentChildMap.get("root");

        // ???????????????
        for (GanttIssue node : rootList) {
            this.buildTreeRecursively(node, parentChildMap);
        }
        return rootList;
    }


    private void buildTreeRecursively(GanttIssue parenGanttIssue, Map<String, List<GanttIssue>> parentChildMap) {
        List<GanttIssue> children = parentChildMap.get(parenGanttIssue.getKey());
        if (children != null) {
            parenGanttIssue.setIsParent(true);
            parenGanttIssue.setChildren(children);
            for (GanttIssue child : children) {
                buildTreeRecursively(child, parentChildMap);
            }
        }else{
            parenGanttIssue.setIsParent(false);
        }
    }

    public static List<GanttIssue> flattenTree(List<GanttIssue> ganttIssueList) {
        List<GanttIssue> flattened = new ArrayList<>();
        for (GanttIssue node : ganttIssueList) {
            flattened.add(node);
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                Collections.sort(node.getChildren(), Comparator.comparingLong(GanttIssue::getId));
                flattened.addAll(flattenTree(node.getChildren()));
            }
        }
        return flattened;
    }


}

