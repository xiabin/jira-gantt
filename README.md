# 说明
1. 基于 jira plugin 开发
2. 核心功能点参考 bigpicture 的日常使用
3. issues 之间的父子关系 使用了 jira 的「issue link」
4. gantt 图需要用到的开始结束时间使用了两个 jira 的自定义字段
5. gantt 图使用 https://github.com/DHTMLX/gantt

# 功能点
- [x] 配置管理；配置 gantt 的「名称」「开始时间自定义字段 id」「结束时间自定义字段 id」「issue link id」「JQL」；实现对配置的增删改查
- [x] 读取配置并查询出数据并根据配置中的 issue link 构建父子关系并渲染成 gantt 图
- [x] 前端使用开源 gantt 图组件实现 「zoom in/ zoom out」
- [x] 支持 Task name 可以超链接到 jira issue 
- [x] 支持任务的修改，并同步修改 jira issue 的数据
- [x] 支持任务的拖拽修改和双击弹出的 lightbox 修改
- [x] 单个 task 变更时，其 parent 节点联动更新
- [x] lightbox 只保留保存和取消功能
- [x] 支持 单个 task 的「Expand/Collapse」
- [ ] 支持 全局的「Expand/Collapse」
- [x] 支持 展示 经办人字段
- [ ] 支持在 gantt 中添加 task，并同步添加 jira issue 并建立问题链接
- [ ] 支持 全局的「Expand/Collapse」


# Getting started
1. 参考 https://developer.atlassian.com/server/framework/atlassian-sdk/downloads/ 搭建开发环境
2. 参考 https://developer.atlassian.com/server/framework/atlassian-sdk/create-a-helloworld-plugin-project/ 创建第一次插件
3. 参考 https://developer.atlassian.com/server/jira/platform/creating-a-jira-issue-crud-servlet-and-issue-search/ 进行插件开发

# 参考文档
1. https://developer.atlassian.com/server/framework/atlassian-sdk/
2. https://developer.atlassian.com/server/framework/atlassian-sdk/product-specific-tutorials/
2. https://developer.atlassian.com/server/jira/platform/tutorials_and_guides/
4. https://codeclou.github.io/kitchen-duty-plugin-for-atlassian-jira/tutorial/06-step-03-planning-page--user-search-js-controller/
5. https://developer.atlassian.com/server/framework/atlassian-sdk/getting-started-with-active-objects/#step-12--seed-the-database-with-some-test-data
6. https://developer.atlassian.com/server/framework/atlassian-sdk/tutorials-and-guides/
7. https://docs.dhtmlx.com/gantt/desktop__howtostart_guides.html
