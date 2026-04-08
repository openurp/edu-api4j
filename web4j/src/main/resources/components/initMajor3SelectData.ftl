[#ftl]
<script src="${base}/dwr/interface/departmentDao.js"></script>
<script src="${base}/dwr/interface/majorDao.js"></script>
<script src="${base}/static/scripts/common/majorSelect.js?v=202412"></script>
<script type="text/javascript">
  var levelArray = new Array();
  [#list levels as level]
  levelArray[${level_index}]={'id':'${level.id?if_exists}','name':'培养层次'};
  [/#list]
  var departArray = new Array();
  [#list departmentList as depart]
  departArray[${depart_index}]={'id':'${depart.id?if_exists}','name':'院系'};
  [/#list]
</script>
