[#ftl/]
[#if tag.uiType?length>0]
  [#include tag.uiType+"/"+tag.templateName+".ftl" /]
[#else]
  <style>
    .calendar-bar td{font-weight:bold}
    .calendar-bar table{border-collapse:separate}
  </style>
  <li>
  [#if tag.label??]<label for="${tag.id}" class="title">[#if tag.required]<em class="required">*</em>[/#if]${tag.label}:</label>[/#if]
  <input id="${tag.id}" class="calendar-text calendar-text-state-default" title="${tag.title}" type="text" value="[#if (tag.defaultValue)??]${tag.defaultValue.schoolYear}学年${tag.defaultValue.name}学期[#elseif tag.empty]全部学期[/#if]" readonly="true" ${tag.parameterString}/>
  <div class="calendar-bar" tabIndex="-1">
    <input id="semesterCalendar_target" type="hidden" name="${tag.name}" value="${(tag.defaultValue.id)!}"/>
    <a name="prev" href="#" style="display:none"><img src="${base}/static/themes/default/images/semesterCalendar_prev.gif"/></a>
    <input id="semesterCalendar_year" class="calendar-bar-input" type="text" index="${tag.valueIndex}" value="[#if (tag.defaultValue)??]${tag.defaultValue.schoolYear}[/#if]" maxLength="4" size="4" readonly="true" style="display:none"/><span style="display:none">学年</span>
    <a name="next" href="#" style="display:none"><img src="${base}/static/themes/default/images/semesterCalendar_next.gif"/></a>

    <a name="prev" href="#" style="display:none"><img src="${base}/static/themes/default/images/semesterCalendar_prev.gif"/></a>
    <input id="semesterCalendar_term" class="calendar-bar-input" type="text" index="${tag.termIndex}" value="[#if (tag.defaultValue)??]${tag.defaultValue.name}[/#if]" size="4" readonly="true" style="display:none"/><span style="display:none">学期</span>
    <a name="next" href="#" style="display:none"><img src="${base}/static/themes/default/images/semesterCalendar_next.gif"/></a>
    <a href="#" id="allSemester" style="display:none">[#if tag.empty]<span style="padding-left:5px;">全部学期</span>[/#if]</a>

    <table id="semesterCalendar_yearTb" class="semesterCalendar_yearTb">
      <tbody>
      [#if !tag.emptyTree]
        [#list tag.semesterTree.keySet() as year]
          [#if year_index%3==0]<tr>[/#if]
          <td index="${year_index}" class="calendar-bar-td-blankBorder">${year}</td>
          [#if year_index%3==2]</tr>[/#if]
        [/#list]
        [#if tag.semesterTree?size%3!=0]
          [#list 0..2 as i]
            <td class="calendar-bar-td-blankBorder"></td>
            [#if (tag.semesterTree?size+i)%3==0]
            </tr>
            [/#if]
          [/#list]
        [/#if]
      [/#if]
      </tbody>
    </table>
    <!-- 两个table之间的空div用来解决IE6 下莫名其妙重复渲染最后一个学年td中的文字的bug-->
    <div></div>
    <table id="semesterCalendar_termTb" class="semesterCalendar_termTb">
      <tbody>
        [#if !tag.emptyTree]
          [#assign defaultTerms = tag.semesterTree.get(tag.defaultValue.schoolYear)/]
          [#list defaultTerms as semester]
        <tr>
          <td val="${semester.id}" class="calendar-bar-td-blankBorder"><span>${semester.name}</span>学期</td>
        </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
  </li>
  <script type="text/javascript">
  if((typeof App)=="undefined"){
    window.App={'contextPath':'${base}'}
  }else{
    App.contextPath="${base}";
  }
  bg.requireCss("${base}/static/themes/default/css/semesterCalendar.css");
  var semesterCalendarUrl = window.location.origin+"${base}/static/scripts/semesterCalendar.js?v=1";
  [#if !tag.emptyTree]
    jQuery(function(){
      jQuery("#${tag.id}").data("semesters", {
        [#list tag.semesterTree.entrySet() as entry]
          y${entry_index}:[
            [#list entry.value as semester]
            {id:${semester.id},schoolYear:"${entry.key?js_string}",name:"${semester.name?js_string}"}${(semester_index==((entry.value)?size-1))?string("",",")}
            [/#list]
          ]${(entry_index==(tag.semesterTree.entrySet()?size-1))?string("",",")}
        [/#list]
      });
        bg.require([semesterCalendarUrl],function(){
          jQuery("#${tag.id}").semesterCalendar({projectId:${project.id},empty:"${tag.empty?string}"[#if tag.onChange??],onChange:"${tag.onChange}"[/#if][#if (tag.value.id)??],value:"${(tag.value.id)}"[/#if]}[#if tag.initCallback??],"${tag.initCallback}"[/#if]);
        });
    });
  [#else]
      bg.require([semesterCalendarUrl],function(){
        jQuery("#${tag.id}").semesterCalendar({projectId:${project.id},empty:"${tag.empty?string}"[#if tag.onChange??],onChange:"${tag.onChange}"[/#if][#if (tag.value.id)??],value:"${(tag.value.id)}"[/#if]}[#if tag.initCallback??],"${tag.initCallback}"[/#if]);
      });
  [/#if]
  </script>
[/#if]
