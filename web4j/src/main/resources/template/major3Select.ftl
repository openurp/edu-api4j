[#ftl]
  <script src='${base}/static/scripts/dwr/util.js'></script>
  <script src='${base}/dwr/engine.js'></script>
[#macro majorSelect id extra...]
  [#local emptyOption = b.text('filed.choose') + "..." /]

  [#local projectLabel = '项目'/]
  [#local projectId = id + "project" /]
  [#local projectName = extra['projectId']!'project.id' /]
  [#if extra['theme']??]
    [#local theme = extra['theme']/]
  [/#if]
  [#if project??]
    [#assign thisProject=project/]
  [#else]
    [#assign thisProject=projectContext.project/]
  [/#if]
  <input type="hidden" id="${projectId}" name="${projectName}" value="${thisProject.id}" />
  [#local educatonLabel = b.text('entity.EducationLevel') /]
  [#local levelId = id + "level" /]
  [#local levelName = extra['levelId']!'level.id' /]
  [#if theme??]
    [@b.select label=educatonLabel id=levelId name=levelName items=[] empty=emptyOption theme=theme/]
  [#else]
    [@b.select label=educatonLabel id=levelId name=levelName items=[] empty=emptyOption /]
  [/#if]

  [#local departLabel = b.text("common.college") /]
  [#local departId = id + 'department' /]
  [#local departName = extra['departId']!'department.id' /]
  [#if theme??]
    [@b.select label=departLabel id=departId name=departName items=[] empty=emptyOption theme=theme/]
  [#else]
    [@b.select label=departLabel id=departId name=departName items=[] empty=emptyOption /]
  [/#if]
  [#if extra['majorId']??]
    [#local majorLabel = b.text('entity.major') /]
    [#local majorId = id + 'major' /]
    [#local majorName = extra['majorId'] /]
    [#if theme??]
      [@b.select label=majorLabel id=majorId name=majorName items=[] empty=emptyOption theme=theme/]
    [#else]
      [@b.select label=majorLabel id=majorId name=majorName items=[] empty=emptyOption /]
    [/#if]
  [/#if]
  [#if extra['majorId']?? && extra['directionId']??]
    [#local directionLabel = b.text('entity.direction') /]
    [#local directionId = id + 'direction' /]
    [#local directionName = extra['directionId'] /]
    [#if theme??]
      [@b.select label=directionLabel id=directionId name=directionName items=[] empty=emptyOption theme=theme/]
    [#else]
      [@b.select label=directionLabel id=directionId name=directionName items=[] empty=emptyOption/]
    [/#if]
  [/#if]

  <script src='${base}/dwr/interface/projectMajorDwr.js'></script>
  <script src='${base}/static/scripts/common/majorSelect.js?v=20240602'></script>
  <script type="text/javascript">
    var projectArray = new Array();
    projectArray[0]={'id':"${thisProject.id}",'name':''};
  [#if !(levelNullable?exists)]
    [#assign levelNullable=false]
  [/#if]
    var sds = new Major3Select("${id}project","${id}level","${id}department",[#if extra['majorId']??]"${id}major"[#else]null[/#if],[#if extra['directionId']??]"${id}direction"[#else]null[/#if],true,true,true,true);
    sds.displayCode=false;
    sds.init(projectArray, '${request.getServletPath()}');
  </script>
[/#macro]
