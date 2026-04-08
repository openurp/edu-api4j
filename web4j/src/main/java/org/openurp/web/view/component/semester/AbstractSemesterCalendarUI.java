/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.web.view.component.semester;

import com.opensymphony.xwork2.util.ValueStack;
import freemarker.ext.beans.HashAdapter;
import org.apache.commons.beanutils.BeanUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Strings;
import org.beangle.struts2.view.component.Form;
import org.beangle.struts2.view.component.UIBean;
import org.beangle.struts2.view.template.Theme;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.SchoolYear;
import org.openurp.base.edu.model.Semester;
import org.openurp.web.view.component.semester.ui.SemesterCalendarUI;
import org.openurp.web.view.component.semester.ui.SemesterUIFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

//FIXME 多学期选择
public abstract class AbstractSemesterCalendarUI extends UIBean {
  protected String name, label, title, check, multi, format;

  /**
   * UI类型
   * TODO 可通过用户设置写入Cookie,记录用户习惯
   */
  protected String uiType;

  protected List<Integer> indexes = CollectUtils.newArrayList();

  protected List<Rule> tRules = CollectUtils.newArrayList();

  protected List<Rule> yRules = CollectUtils.newArrayList();

  /**
   * 系统默认类型
   */
  public static final SemesterCalendarConfig config = new SemesterCalendarConfig();

  /**
   * 事件列表
   * TODO 目前仅支持onChange,initCallback
   */
  protected String onChange, onClick, onKeyup, onKeypress, onKeydown, onFocus, onBlur, beforeInit,
    initCallback;

  protected Object empty, required, value, items;

  protected Object yearRules;

  protected Object termRules;

  protected int valueIndex, termIndex;

  protected Project project;

  protected Semester defaultValue;

  protected Map<String, List<Semester>> semesterTree = new TreeMap<String, List<Semester>>(
    new Comparator<String>() {
      public int compare(String o1, String o2) {
        if (o1.equals(o2)) {
          return 0;
        }
        if (o1.equals("")) {
          return -1;
        }
        if (o2.equals("")) {
          return 1;
        }
        return o1.compareTo(o2);
      }
    });

  public AbstractSemesterCalendarUI(ValueStack stack) {
    super(stack);
    try {
      loadProperties();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void evaluateParams() {
    if (null == this.id) generateIdIfEmpty();
    if (null != label) label = getText(label, label);
    if (null != title) {
      title = getText(title);
    } else {
      title = label;
    }
    try {
      if (null != yearRules) {
        yRules = Rule.getRules((String) yearRules, config.paramTypeMap);
      }
      if (null != termRules) {
        tRules.addAll(Rule.getRules((String) termRules, config.paramTypeMap));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    required = "true".equals(required + "");
    empty = !"false".equals(empty + "");
    SemesterCalendarUI semesterCalendarUI = null;
    if (Strings.isBlank(uiType)) {
      uiType = config.defaultUiType;
    } else {
      semesterCalendarUI = SemesterUIFactory.get(uiType);
    }
    if (null != items) {
      if (items instanceof Collection<?>) {
        List<Semester> c = CollectUtils.newArrayList((Collection<Semester>) items);
        Collections.sort(c);
        boolean termFormat = null != format && format.contains("T");
        boolean yearFormat = null != format && format.matches("^yy(-T)?$");
        boolean formatIsBlank = Strings.isBlank(format);
        if (!formatIsBlank && null != value) {
          Semester colneValue;
          try {
            colneValue = (Semester) (value = BeanUtils.cloneBean(value));
            if (yearFormat) {
              colneValue.setYear(new SchoolYear());
              colneValue.getYear().setName(editSchoolYear(colneValue.getSchoolYear()));
            }
            if (termFormat) {
              colneValue.setName(editTerm(colneValue.getName()));
            }
          } catch (Exception e1) {
            throw new RuntimeException(e1);
          }
        }
        for (Semester semester : c) {
          Semester colneSemester = null;
          try {
            colneSemester = termFormat ? (Semester) BeanUtils.cloneBean(semester) : semester;
          } catch (Exception e1) {
            throw new RuntimeException(e1);
          }
          String key = "";
          try {
            if (!formatIsBlank) {
              key += yearFormat ? editSchoolYear(colneSemester.getSchoolYear())
                : colneSemester.getSchoolYear();
              if (termFormat) {
                colneSemester.setName(editTerm(colneSemester.getName()));
              }
            } else {
              key += colneSemester.getSchoolYear();
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }

          List<Semester> terms = semesterTree.get(key);
          if (null == terms) {
            terms = CollectUtils.newArrayList();
          }
          semesterTree.put(key, terms);
          terms.add(colneSemester);
        }
      }
    }
    if (null != semesterCalendarUI) {
      Object newItems = semesterCalendarUI.adapteItems(semesterTree);
      if (null != newItems) {
        items = newItems;
      }
    }
    if (null != value) {
      if (value instanceof Semester) {
        Semester semester = (Semester) value;
        listTree:
        for (String schoolYear : semesterTree.keySet()) {
          if (schoolYear.equals(semester.getSchoolYear())) {
            List<Semester> semesters = semesterTree.get(schoolYear);
            for (Semester semester2 : semesters) {
              if (semester.equals(semester2)) {
                defaultValue = semester2;
                break listTree;
              }
              termIndex++;
            }
          }
          valueIndex++;
        }
      } else if (value instanceof HashAdapter) {
        HashAdapter semester = (HashAdapter) value;
        listHashAdapter:
        for (String schoolYear : semesterTree.keySet()) {
          if (schoolYear.equals(semester.get("schoolYear"))) {
            List<Semester> semesters = semesterTree.get(schoolYear);
            for (Semester semester2 : semesters) {
              if (semester.equals(semester2)) {
                defaultValue = semester2;
                break listHashAdapter;
              }
              termIndex++;
            }
          }
          valueIndex++;
        }
      }
    } else {
      if (Boolean.FALSE.equals(empty)) {
        valueIndex = 0;
        termIndex = 0;
        if (!semesterTree.entrySet().isEmpty()) {
          defaultValue = semesterTree.entrySet().iterator().next().getValue().get(0);
        }
      } else {
        valueIndex = -1;
        termIndex = -1;
      }

    }
    Form myform = findAncestor(Form.class);
    if (null != myform) {
      if ("true".equals(required)) myform.addCheck(id, "require()");
      if (null != check) myform.addCheck(id, check);
    }
    if (Strings.isNotBlank(uiType)) {
      SemesterUIFactory.get(uiType);
    }
    if (null == project) {
      project = (Project) getRequest().getAttribute("project");
    }
  }

  private String editSchoolYear(String schoolYear) throws Exception {
    int j = 0;
    StringBuilder schoolYearBuilder = new StringBuilder();
    for (int i = 0; i < schoolYear.length(); i++) {
      if (j < indexes.size()) {
        if (i == indexes.get(j)) {
          i += 2;
          j++;
        }
        schoolYearBuilder.append(schoolYear.charAt(i));
      } else {
        schoolYearBuilder.append(schoolYear.charAt(i));
      }
    }
    String result = schoolYearBuilder.toString();
    for (Rule rule : yRules) {
      result = rule.invoke(result);
    }
    return result;
  }

  private String editTerm(String term) throws Exception {
    for (Rule rule : tRules) {
      term = rule.invoke(term);
    }
    return term;
  }

  private static class Rule {
    private Method method;

    private Object[] params;

    public Rule(Method method, Object[] params) {
      this.method = method;
      this.params = params;
    }

    public static Rule getRule(Method method, Object[] params) {
      return new Rule(method, params);
    }

    public static List<Rule> getRules(String rulesStr, Map<String, Class<?>> paramTypeMap) throws Exception {
      List<Rule> result = CollectUtils.newArrayList();
      String[] rules = rulesStr.split(":");
      for (String s : rules) {
        String[] entry = s.substring(0, s.length() - 1).split("\\(");
        String name = entry[0];
        String[] paramsStr = entry[1].split(",");
        Class<?> c = paramTypeMap.get(name);
        Object[] params = null;
        Class<?>[] parameterTypes = null;
        if (null == c) {
          parameterTypes = new Class[0];
          params = paramsStr;
        } else if (int.class == c) {
          parameterTypes = new Class[paramsStr.length];
          params = new Integer[paramsStr.length];
          for (int i = 0; i < paramsStr.length; i++) {
            parameterTypes[i] = c;
            params[i] = Integer.valueOf(paramsStr[i]);
          }
        } else {
          parameterTypes = new Class[paramsStr.length];
          for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = c;
          }
          params = paramsStr;
        }
        Method m = String.class.getDeclaredMethod(name, parameterTypes);
        result.add(Rule.getRule(m, params));
      }
      return result;
    }

    public String invoke(Object obj) throws Exception {
      return this.method.invoke(obj, params) + "";
    }
  }

  private static class SemesterCalendarConfig {
    private String defaultUiType = "";
    private long timeToLiveMills;
    private long loadAt = -1;

    private SemesterCalendarConfig() {
      paramTypeMap = CollectUtils.newHashMap();
      paramTypeMap.put("concat", String.class);
      paramTypeMap.put("replace", CharSequence.class);
      paramTypeMap.put("replaceAll", String.class);
      paramTypeMap.put("replaceFirst", String.class);
      paramTypeMap.put("substring", int.class);
      paramTypeMap.put("subSequence", int.class);
      paramTypeMap.put("charAt", int.class);
    }

    protected Map<String, Class<?>> paramTypeMap = CollectUtils.newHashMap();

    private void updateConfig(Long timeToLiveMills, String defaultUiType) {
      if (null == timeToLiveMills) {
        timeToLiveMills = TimeUnit.MINUTES.toMillis(30);
      }
      this.timeToLiveMills = timeToLiveMills;
      if (null == defaultUiType) {
        this.defaultUiType = "";
      } else {
        try {
          SemesterUIFactory.get(defaultUiType);
          this.defaultUiType = defaultUiType;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      loadAt = System.currentTimeMillis();
    }

    private boolean isExpired() {
      return System.currentTimeMillis() - (loadAt + timeToLiveMills) > 0;
    }
  }

  private void loadProperties() throws Exception {
    if (!config.isExpired()) {
      return;
    }
    Properties properties = new Properties();
    InputStream is = getClass().getResourceAsStream("/eams-ui-default.properties");
    if (null != is) {
      properties.load(is);
    }
    is = getClass().getResourceAsStream("/eams-ui.properties");
    if (null != is) {
      properties.load(is);
    }

    Long timeToLiveMills = null;
    String defaultUiType = null;
    for (Entry<Object, Object> property : properties.entrySet()) {
      String key = (String) property.getKey();
      String value = (String) property.getValue();
      if (key.equals("semesterCalendar.properties.timeToLiveSeconds")) {
        timeToLiveMills = Long.parseLong(value.trim()) * 1000;
      } else if (key.equals("semesterCalendar.type")) {
        defaultUiType = value.trim();
      } else if (key.equals("semesterCalendar.year.indexes")) {
        String[] indexes = value.split(",");
        for (String index : indexes) {
          this.indexes.add(Integer.valueOf(index));
        }
      } else if (key.contains("semesterCalendar.term.method.")) {
        String name = key.replace("semesterCalendar.term.method.", "");
        String[] paramsStr = value.split(",");
        Class<?> clazz = config.paramTypeMap.get(name);
        Object[] params = null;
        Class<?>[] parameterTypes = null;
        if (null == clazz) {
          parameterTypes = new Class[0];
          params = paramsStr;
        } else if (int.class == clazz) {
          parameterTypes = new Class[paramsStr.length];
          params = new Integer[paramsStr.length];
          for (int i = 0; i < paramsStr.length; i++) {
            parameterTypes[i] = int.class;
            params[i] = Integer.valueOf(paramsStr[i]);
          }
        } else {
          parameterTypes = new Class[paramsStr.length];
          for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = String.class;
          }
          params = paramsStr;
        }
        Method m = String.class.getDeclaredMethod(name, parameterTypes);
        tRules.add(Rule.getRule(m, params));
      }

    }
    config.updateConfig(timeToLiveMills, defaultUiType);
  }

  public boolean isEmptyTree() {
    if (null == items) {
      return true;
    }
    if (items instanceof Collection<?>) {
      return ((Collection<?>) items).isEmpty();
    }
    return semesterTree.isEmpty();
  }

  public Map<String, List<Semester>> getSemesterTree() {
    if (semesterTree.isEmpty()) {
      evaluateParams();
    }
    return semesterTree;
  }

  public String getTemplateName() {
    return Theme.getTemplateName(this.getClass());
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Object getRequired() {
    return required;
  }

  public void setRequired(Object required) {
    this.required = required;
  }

  public String getCheck() {
    return check;
  }

  public void setCheck(String check) {
    this.check = check;
  }

  public Object getEmpty() {
    return empty;
  }

  public void setEmpty(Object empty) {
    this.empty = empty;
  }

  public String getMulti() {
    return multi;
  }

  public void setMulti(String multi) {
    this.multi = multi;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setItems(Object items) {
    this.items = items;
  }

  public Object getItems() {
    return items;
  }

  public void setSemesterTree(Map<String, List<Semester>> semesterTree) {
    this.semesterTree = semesterTree;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public List<Integer> getIndexes() {
    return indexes;
  }

  public void setIndexes(List<Integer> indexes) {
    this.indexes = indexes;
  }

  public void setYearRules(Object yearRules) {
    this.yearRules = yearRules;
  }

  public void setTermRules(Object termRules) {
    this.termRules = termRules;
  }

  public String getOnChange() {
    return onChange;
  }

  public void setOnChange(String onChange) {
    this.onChange = onChange;
  }

  public int getValueIndex() {
    return valueIndex;
  }

  public void setValueIndex(int valueIndex) {
    this.valueIndex = valueIndex;
  }

  public int getTermIndex() {
    return termIndex;
  }

  public void setTermIndex(int termIndex) {
    this.termIndex = termIndex;
  }

  public Semester getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Semester defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getInitCallback() {
    return initCallback;
  }

  public void setInitCallback(String initCallback) {
    this.initCallback = initCallback;
  }

  public String getUiType() {
    return uiType;
  }

  public void setUiType(String uiType) {
    this.uiType = uiType;
  }

  public String getOnClick() {
    return onClick;
  }

  public void setOnClick(String onClick) {
    this.onClick = onClick;
  }

  public String getOnKeyup() {
    return onKeyup;
  }

  public void setOnKeyup(String onKeyup) {
    this.onKeyup = onKeyup;
  }

  public String getOnKeypress() {
    return onKeypress;
  }

  public void setOnKeypress(String onKeypress) {
    this.onKeypress = onKeypress;
  }

  public String getOnKeydown() {
    return onKeydown;
  }

  public void setOnKeydown(String onKeydown) {
    this.onKeydown = onKeydown;
  }

  public String getOnFocus() {
    return onFocus;
  }

  public void setOnFocus(String onFocus) {
    this.onFocus = onFocus;
  }

  public String getOnBlur() {
    return onBlur;
  }

  public void setOnBlur(String onBlur) {
    this.onBlur = onBlur;
  }

  public String getBeforeInit() {
    return beforeInit;
  }

  public void setBeforeInit(String beforeInit) {
    this.beforeInit = beforeInit;
  }
}
