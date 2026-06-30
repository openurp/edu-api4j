[#ftl]
<div [#if tag.divId??]id="${tag.divId}"[/#if] style="background-color: var(--search-bg-color);border: 1px solid #AED0EA;color: #222222;height:28px;font-size:0.8125rem;line-height:28px;">
[@b.form name=tag.formName action=tag.action! target=tag.target! theme="html"]
<div style="margin-left:4px;margin-top:0px;float:left;height:22px;">
  ${tag.body}
</div>
[#assign submit=tag.submit!"bg.form.submit('${tag.formName}')"/]
<div style="margin-left:10px;margin-top:0px;float:left;height:22px;">
[@eams.projectUI label=tag.label!(b.text("entity.project")) onChange=tag.onChange! onSemesterChange=tag.onSemesterChange!submit empty=tag.empty semesterEmpty=tag.semesterEmpty id=tag.id name=tag.name semesterName=tag.semesterName  value=tag.value semesterValue=tag.semesterValue initCallback=tag.initCallback/]
</div>
[/@]
</div>
