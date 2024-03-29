[#ftl]
[#macro i18nNameTitle(entity)][#if locale.language?index_of("en")!=-1][#if entity.engTitle!?trim==""]${entity.title!}[#else]${entity.engTitle!}[/#if][#else][#if entity.title!?trim!=""]${entity.title!}[#else]${entity.engTitle!}[/#if][/#if][/#macro]

[#assign displayed={} /]
[#macro displayMenu menu]
[#if !(displayed[menu.id?string]??)][#assign displayed=displayed+{menu.id?string:menu}/][#else][#return/][/#if]
<li>
[#if menu.entry??]
[@b.a href="${(menu.entry.name)!}" target="main"][@i18nNameTitle menu/][/@]
[#else]
  [@i18nNameTitle menu/]
  <ul style="padding-left: 20px;">
  [#list menu.children?sort_by("code") as child]
    [#if submenus?seq_contains(child)][@displayMenu child/][/#if]
  [/#list]
  </ul>
[/#if]
</li>
[/#macro]

[#macro menuEntry module]
  [#if (module.entry!"")?trim?length=0]<li>[@b.a cssClass="p_1" href="${(module.entry.name)!}" target="main"][@i18nNameTitle module/]</li>[/@]
  [#else]
    [@ems.guard res=module.entry.name]<li>[@b.a cssClass="p_1" href="${(module.entry.name)!}" target="main"][@i18nNameTitle module/][/@]</li>[/@]
  [/#if]
[/#macro]

<script>
  jQuery("ul.menu li a.p_1").click(function() {
    jQuery("ul.menu li.current").removeClass('current');
    jQuery(this).parent('li').addClass('current');
  });
</script>
[#if submenus?size>0]

<ul class="menu collapsible">
  [#assign nodeIndex=0]
  [#list submenus! as module]
    [#if module.children?size==0]
     [#if module.entry?default('')?starts_with("http")]<li><a class="p_1" href="${module.entry}">[@i18nNameTitle module/]</a></li>
     [#else][@menuEntry module/][/#if]
    [#else]
     [#if nodeIndex!=0]</div></ul></li>[/#if]
     [#if (module_index)==0 ]
     <li style="margin:0px;" class="expand">
     [#else]
     <li>
     [/#if]
     <a class="first_menu" href="javascript:void(0)">${module.title}</a><ul class="acitem"><div class="scroll_box">
     [#assign nodeIndex=nodeIndex+1]
    [/#if]
  [/#list]</div></ul></li>
</ul>
[#else]
without any menu!
[/#if]
