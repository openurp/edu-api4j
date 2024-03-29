<script type='text/javascript' src='${base}/dwr/interface/studentServiceDwr.js'></script>
<script src='scripts/stdStatusAlter2Select.js'></script>
<script type="text/javascript">
  var alterTypeArray = new Array();
  [#list alterationTypeList?if_exists?sort_by("code") as type]
  alterTypeArray[${type_index}]={'id':'${type.id?if_exists}','name':'[@i18nName type/]'};
  [/#list]
  var sas = new StdStatusAlter2Select("record.alterationType.id","record.alterationReason.id",${alterTypeNullable?default('false')},${alterReasonNullable?default('true')});
  sas.init(alterTypeArray);
</script>
