[#ftl]
[#assign yearDom = "" /]
[#assign termDom = ""/]
  [#if !datas.isEmpty()]
    [#list datas.keySet() as year]
      [#if year_index%3==0][#assign yearDom = yearDom + "<tr>" /][/#if]
      [#if year==""]
        [#assign allSemester]<td class='calendar-bar-td-blankBorder' onClick='jQuery(\"#allSemester\").click()'>全部学期</td>[/#assign]
        [#assign yearDom = yearDom + allSemester/]
      [#else]
        [#assign yearDom = yearDom + "<td class='calendar-bar-td-blankBorder' index='" + year_index + "'>"+year?js_string +"</td>"/]
      [/#if]
      [#if year_index%3==2][#assign yearDom = yearDom + "</tr>" /][/#if]
    [/#list]
    [#if datas?size%3!=0]
      [#list 0..2 as i]
        [#assign yearDom = yearDom + "<td class='calendar-bar-td-blankBorder'></td>" /]
        [#if (datas?size+i)%3==0]
        [#assign yearDom = yearDom + "</tr>" /]
        [/#if]
      [/#list]
    [/#if]
    [#assign termDom = ""/]

    [#if (value)??]
      [#assign defaultTerms =datas.get(value.schoolYear)/]
    [#else]
      [#assign defaultTerms = datas.values()[0]/]
    [/#if]
    [#list defaultTerms as semester]
    [#assign termDom = termDom + "<tr><td class='calendar-bar-td-blankBorder' val='" + semester.id + "'><span>" + semester.name?js_string + "</span>学期</td></tr>"/]
    [/#list]
  [/#if]
{yearDom:"${yearDom}",termDom:"${termDom}",semesters:{[#list datas.entrySet() as entry]y${entry_index}:[[#list entry.value as semester][#if (value)?? && value.id==semester.id][#assign yIndex = entry_index/][#assign tIndex = semester_index/][/#if]{id:${semester.id},schoolYear:"${entry.key?js_string}",name:"${semester.name?js_string}"}${(semester_index==((entry.value)?size-1))?string("",",")}[/#list]]${(entry_index==(datas.entrySet()?size-1))?string("",",")}[/#list]},yearIndex:"${yIndex?default("-1")}",termIndex:"${tIndex?default("-1")}",semesterId:"${semesterId!}"}
