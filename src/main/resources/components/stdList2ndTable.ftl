 [#ftl/]
 [@b.grid items=students var="std"]
   [@b.row]
     [@b.boxcol/]
     [@b.col title="attr.stdNo" proterty="code" width="8%"]
       [@b.a href="/studentSearch!info?studentId=${std.id}" target="_blank" title="查看学生基本信息"]${std.code}[/@]
     [/@]
     [@b.col title="attr.personName" proterty="name" width="8%"]
       [@b.a href="javascript:if(typeof stdIdAction !='undefined') stdIdAction('${std.id}');" title="${stdNameTitle?if_exists}" /]${std.name}[/a]
     [/@]
     [@b.col title="entity.gender" proterty="gender.name" width="5%"]
       [@i18nName std.person.gender?if_exists/]
     [/@]
     [@b.col title="std.state.grade" proterty="grade" width="8%"/]
     [@b.col title="department" proterty="secondMajor.department.name" width="15%"]
       [@i18nName std.secondMajor?if_exists.department?if_exists/]
     [/@]
     [@b.col title="major" proterty="secondMajor.name" width="13%"]
       [@i18nName std.secondMajor?if_exists/]
     [/@]
     [@b.col title="entity.class" property ="squad.name" width="20%"]
       [@i18nName std.state.squad!/]
     [/@]
     [@b.col title="方向" proterty="secondAspect.name" width="20%"]
       [@i18nName std.secondAspect?if_exists/]
     [/@]
   [/@]
 [/@]
