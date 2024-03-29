     <tr class="darkColumn">
    <td colspan="32" align="center">
      [#if pageIndex==1]
      [@text name="page.first"/]&nbsp;&nbsp;
      [@text name="page.previous"/]&nbsp;&nbsp;
      [#else]
      <a href="#" onclick='go(1)' accesskey="f"/>[@text name="page.first"/](<U>F</U>)</a>&nbsp;&nbsp;
      <a href="#" onclick='go(${previousPageNo})' accesskey="p"/>[@text name="page.previous"/](<U>P</U>)</a>&nbsp;&nbsp;
      [/#if]
      [#if pageIndex==maxPageNo]
      [@text name="page.next"/]&nbsp;&nbsp;
      [@text name="page.last"/]&nbsp;&nbsp;
      [#else]
      <a href="#"  onclick='go(${nextPageNo})' accesskey="n"/>[@text name="page.next" /](<U>N</U>)</a>&nbsp;&nbsp;
      <a href="#" onclick='go(${maxPageNo})' accesskey="l"/>[@text name="page.last"/](<U>L</U>)</a>&nbsp;&nbsp;
      [/#if]
         &nbsp;[@msg.text name="page.size" arg0=thisPageSize?string arg1=totalSize?string/]&nbsp;
      &nbsp;[@text name="page.perPage"/]<select id="myPageSize" name="pageSize" onChange="go(1,this.value)">
       <option value="10" [#if pageSize=10]selected[/#if]>10</option>
       <option value="15" [#if pageSize=15]selected[/#if]>15</option>
       <option value="20" [#if pageSize=20]selected[/#if]>20</option>
         <option value="25" [#if pageSize=25]selected[/#if]>25</option>
       <option value="30" [#if pageSize=30]selected[/#if]>30</option>
       <option value="50" [#if pageSize=50]selected[/#if]>50</option>
       <option value="70" [#if pageSize=70]selected[/#if]>70</option>
       <option value="90" [#if pageSize=90]selected[/#if]>90</option>
       <option value="100" [#if pageSize=100]selected[/#if]>100</option>
       <option value="150" [#if pageSize=150]selected[/#if]>150</option>
       <option value="300" [#if pageSize=300]selected[/#if]>300</option>
       <option value="1000" [#if pageSize=1000]selected[/#if]>1000</option>
      </select>
      <input type="text" id="myPageNo" name="pageIndex" value="${pageIndex}" style="width:30px;background-color:#CDD6ED"/>/${maxPageNo}
      <input type="button" name="button11" value="GO" class="buttonStyle" onclick="go()"/>
    </td>
     </tr>
    <script type="text/javascript">
      //跳转到指定的页面
        function getPageNo(){
        return document.getElementById('myPageNo').value;
      }
        function getPageSize(){
        return document.getElementById('myPageSize').value;
      }
      function go(pageIndex,pageSize){
        if(null==pageIndex)
           pageIndex = getPageNo();
        var number = pageIndexValidator(pageIndex);
        if(null==pageSize)
              pageSize = getPageSize();
           if(typeof pageGoWithSize=="function")
             pageGoWithSize(number,pageSize);
        else
           pageGo(number);
      }

      function pageIndexValidator(pageIndex){
        var value = pageIndex;
        if( isNaN(parseInt(value)) ){
          value = 1;
        }else{
          value = parseInt(value);
        }
        if(value <= 0)
          value = 1;
        if(value > ${maxPageNo})
          value = ${maxPageNo};
        return value;
      }
     </script>
