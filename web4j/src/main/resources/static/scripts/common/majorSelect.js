    /*******************************************************
    *   学生类别和部门,专业,方向的三级级联下拉框
    *   用法:
    *    //准备学生类别{id,name}
    *    var levelArray = new Array(); 
    *    //准备部门{id,name}
    *    var departArray = new Array();
    *    var dd = new Major3Select("levelOfMajor","department","major","direction",false,true,true,true);    
    *    dd.init(levelArray,departArray);
    *
    *******************************************************/
  dwr.engine.setAsync(false);
    // 缺省值
    var defaultValues=new Object();
    // 页面上所有的三级级联选择
    var selects= new Array();
    // 当前操作影响的选择
    var mySelects=new Array();
    // 当前的三级级联选择
    var projectMajor3Select=null;
    
    function addTitle(selectId) {
      jQuery('option' ,jQuery('#' + selectId)).each(function(index, option) {
        jQuery(option).attr('title', jQuery(option).html());
      });
    }
    
    // 初始化项目选择框
    function initProjectSelect(projects) {
        if(0 == jQuery('#' + this.projectSelectId).length) {
          return;
        }
        defaultValues[this.projectSelectId] = jQuery('#' + this.projectSelectId).val();
        dwr.util.removeAllOptions(this.projectSelectId);
        
        dwr.util.addOptions(this.projectSelectId,[{'id':'','name':'...'}],'id','name');
        dwr.util.addOptions(this.projectSelectId, projects,'id','name');
        
        setSelected(jQuery('#' + this.projectSelectId), defaultValues[this.projectSelectId]);
        
        jQuery('#' + this.projectSelectId).change(function(event) {
          notifyProjectChange(event);
        });
        
        notifyProjectChange_2(this);
        
        if(0 != jQuery('#' + this.levelSelectId).length && 0 != jQuery('#' + this.departSelectId).length) {
          jQuery('#' + this.levelSelectId).change(function(event) {
            notifyMajorChange(event);
            notifyDirectionChange(event);
          });
          jQuery('#' + this.departSelectId).change(function(event) {
            notifyMajorChange(event);
            notifyDirectionChange(event);
          });
      }
    }
    
    // 初始化培养层次选择框
    function notifyProjectChange(event) {
       var target = event.target;
         mySelects = getMySelects(target.id);
         notifyProjectChange_2(mySelects[0]);
    }
    // 初始化培养层次和部门选择框
    function notifyProjectChange_2(curSelect) { 
        if (0 == jQuery('#' + curSelect.levelSelectId).length) {
          return;
        }
        defaultValues[curSelect.levelSelectId] = jQuery('#' + curSelect.levelSelectId).val();    
        dwr.util.removeAllOptions(curSelect.levelSelectId);
        if(curSelect.levelNullable) {
            dwr.util.addOptions(curSelect.levelSelectId, [{'id':'','name':'...'}],'id','name');
        }
        
        if (0 != jQuery('#' + curSelect.departSelectId).length) {
          defaultValues[curSelect.departSelectId] = jQuery('#' + curSelect.departSelectId).val();        
          dwr.util.removeAllOptions(curSelect.departSelectId);
          if(curSelect.departNullable){
            dwr.util.addOptions(curSelect.departSelectId,[{'id':'','name':'...'}],'id','name');
          }
        }
        var pselect= jQuery('#' + curSelect.projectSelectId);
        if (pselect.val() != "") {
             projectMajor3Select = curSelect;
             projectMajorDwr.levelAndDeparts(pselect.val(),projectMajor3Select.resourceName,setEducationAndDepartOptions);
        }
    }
    // 设置培养层次和部门
    function setEducationAndDepartOptions(datas){
      data = datas[0];
        for (var i = 0; i < data.length; i++) {
          dwr.util.addOptions(projectMajor3Select.levelSelectId,[{'id':data[i][0],'name':data[i][1]}],'id','name');
        }
        if (defaultValues[projectMajor3Select.levelSelectId] != null) {
            setSelected(jQuery('#' + projectMajor3Select.levelSelectId), defaultValues[projectMajor3Select.levelSelectId]);
        }
      data = datas[1];
        for (var i = 0; i < data.length; i++) {
          dwr.util.addOptions(projectMajor3Select.departSelectId,[{'id':data[i][0],'name':data[i][1]}],'id','name');
        }
        if (defaultValues[projectMajor3Select.departSelectId] != null) {
            setSelected(jQuery('#' + projectMajor3Select.departSelectId), defaultValues[projectMajor3Select.departSelectId]);
        }
     }

    // 初始化专业选择框
    function initMajorSelect() {
       if(0 == jQuery('#' + this.majorSelectId).length) {
         return;
       }
       defaultValues[this.majorSelectId] = jQuery('#' + this.majorSelectId).val();
       dwr.util.removeAllOptions(this.majorSelectId);
       jQuery('#' + this.majorSelectId).change(function(event) {
         notifyDirectionChange(event);
       });

       var spanVal= jQuery('#' + this.levelSelectId).val();
       var departmentVal = jQuery('#' + this.departSelectId).val(); 
       var projectVal = jQuery('#' + this.projectSelectId).val();

       if(this.majorNullable) {
           dwr.util.addOptions(this.majorSelectId,[{'id':'','name':'...'}],'id','name');
       }
       projectMajor3Select = this;
       projectMajorDwr.majors(projectVal, spanVal,departmentVal,setMajorOptions);
    }
    
    // 初始化专业方向选择框
    function initDirectionSelect() {
       if(0 == jQuery('#' + this.directionSelectId).length) {
         return;
       }
       defaultValues[this.directionSelectId] = jQuery('#' + this.directionSelectId).val();
       dwr.util.removeAllOptions(this.directionSelectId);
       var majorVal = jQuery('#' + this.majorSelectId).val();
       var levelVal = jQuery('#' + this.levelSelectId).val();
       var departVal = jQuery('#' + this.departSelectId).val();
       if(this.directionNullable) {
           dwr.util.addOptions(this.directionSelectId,[{'id':'','name':'...'}],'id','name');
       }
       if(majorVal != "") {
           projectMajor3Select = this;
           projectMajorDwr.directions(levelVal,departVal,majorVal,setDirectionOptions);
        }
    }

    // 通知专业变化,填充专业选择列表
    function notifyMajorChange(event) {
       var target = event.target;
       mySelects= getMySelects(target.id);
       for(var i = 0; i < mySelects.length; i++) {
        
         var edu = jQuery('#' + mySelects[i].levelSelectId);
         var dep = jQuery('#' + mySelects[i].departSelectId);
         var pro = jQuery('#' + mySelects[i].projectSelectId);
         if(0 == edu.length || 0 == dep.length) {
           continue;
         }
         dwr.util.removeAllOptions(mySelects[i].majorSelectId);
         if(mySelects[i].majorNullable){
             dwr.util.addOptions(mySelects[i].majorSelectId,[{'id':'','name':'...'}],'id','name');
         }
         projectMajor3Select = mySelects[i];
         projectMajorDwr.majors(pro.val(), edu.val(), dep.val(),setMajorOptions);
       }
    }

    // 通知专业方向变化，填充专业方向列表
    function notifyDirectionChange(event) {
      var target =  event.target;
      mySelects = getMySelects(target.id);
      for(var i = 0; i < mySelects.length; i++) {
           //alert("removeAllOptions of :"+mySelects[i].directionSelectId);
         if(null == mySelects[i].directionSelectId){
             continue;
         }
         dwr.util.removeAllOptions(mySelects[i].directionSelectId);
         if (mySelects[i].directionNullable) {
             dwr.util.addOptions(mySelects[i].directionSelectId, [{'id':'','name':'...'}],'id','name');
         }
         var levelVal = jQuery('#' +  mySelects[i].levelSelectId).val();
         var departVal = jQuery('#' +  mySelects[i].departSelectId).val();
         var majorVal = jQuery('#' + mySelects[i].majorSelectId).val();
         if(majorVal != "") {
            projectMajor3Select = mySelects[i];
            projectMajorDwr.directions(levelVal,departVal,majorVal,setDirectionOptions);
         }
       }
    }

    function setMajorOptions(data) {
       var displayCode = projectMajor3Select.displayCode;
       var name='';
       for(var i = 0; i < data.length; i++) {
            if(displayCode)name=data[i][1]+' '+data[i][2];
            else name= data[i][2];
            dwr.util.addOptions(projectMajor3Select.majorSelectId,[{'id':data[i][0],'name':name}],'id','name');
       }
       if(defaultValues[projectMajor3Select.majorSelectId] != null){
           setSelected(jQuery('#' + projectMajor3Select.majorSelectId), defaultValues[projectMajor3Select.majorSelectId]);
       }
       addTitle(projectMajor3Select.majorSelectId);
    }

    function setDirectionOptions(data) {
       var displayCode = projectMajor3Select.displayCode;
       var name='';
       for(var i = 0; i < data.length; i++) {
          if(displayCode)name=data[i][1]+' '+data[i][2];
          else name= data[i][2];
          dwr.util.addOptions(projectMajor3Select.directionSelectId,[{'id':data[i][0],'name':name}],'id','name');
       }
       if(defaultValues[projectMajor3Select.directionSelectId] != null) {    
           setSelected(jQuery('#' + projectMajor3Select.directionSelectId), defaultValues[projectMajor3Select.directionSelectId]);
       }
       addTitle(projectMajor3Select.directionSelectId);
    }
    
    function setSelected(obj, value) {
      jQuery('option', obj).each(function(index, option) {
        if(jQuery(option).val() == value) {
          jQuery(option).prop('selected', true);
        } else {
          jQuery(option).prop('selected',false);
        }
      });
    }
    
    /**
     *  三级级联选择的模型
     *@param levelSelectId 学生类别下拉框的id
     *@param departSelectId 部门下拉框id
     *@param majorSelectId 专业下拉框id
     *@param directionSelectId 方向下拉框id
     *@param levelNullable 培养类型是允许为空
     *@param departNullable 部门是否允许为空
     *@param majorNullable 专业是否允许为空
     *@param directionNullable 专业方向是否为空
     */
    function Major3Select(projectSelectId,levelSelectId,departSelectId,majorSelectId,directionSelectId,levelNullable,departNullable,majorNullable,directionNullable){
     // alert(levelSelectId+":"+departSelectId+":"+majorSelectId+":"+directionSelectId);
      this.projectSelectId  = projectSelectId;
    this.levelSelectId  = levelSelectId;
    this.departSelectId    = departSelectId;
    this.majorSelectId    = majorSelectId;
    this.directionSelectId  = directionSelectId;
    this.levelNullable  = levelNullable;
    this.departNullable    = departNullable;
    this.majorNullable    = majorNullable;
    this.directionNullable  = directionNullable;
    this.initProjectSelect  = initProjectSelect;
    this.initMajorSelect  = initMajorSelect;
    this.initDirectionSelect = initDirectionSelect;
    this.init = init;
    this.displayCode=true;
    selects[selects.length] = this;
    }
    
    function init(projects, resourceName) {
       projectMajor3Select = this;
       this.resourceName = resourceName;
       this.initProjectSelect(projects);
       this.initMajorSelect();
       this.initDirectionSelect();
    }
    
    function getMySelects(id){
        var mySelects = new Array();
        for(var i=0;i<selects.length;i++){
            if(selects[i].projectSelectId == id
                || selects[i].levelSelectId == id
                || selects[i].departSelectId == id
                || selects[i].majorSelectId == id
                || selects[i].directionSelectId == id
            ) {
               mySelects[mySelects.length]=selects[i];
            }
        }
        return mySelects;
    }