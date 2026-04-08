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
package org.openurp.web.action;

import com.opensymphony.xwork2.util.ValueStack;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.bean.comparators.PropertyComparator;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.beangle.ems.dictionary.model.CodeMeta;
import org.beangle.security.Securities;
import org.beangle.security.core.context.SecurityContext;
import org.beangle.security.core.userdetail.DefaultAccount;
import org.beangle.security.core.userdetail.Profile;
import org.beangle.struts2.annotation.Action;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.model.Department;
import org.openurp.base.resource.model.Building;
import org.openurp.base.resource.model.Classroom;
import org.openurp.base.std.model.Student;
import org.openurp.edu.web.action.RestrictionSupportAction;
import org.openurp.service.EamsException;
import org.openurp.service.security.EamsUserCategory;
import org.openurp.web.view.component.semester.SemesterCalendar;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Action("/dataQuery")
public class DataQueryAction extends RestrictionSupportAction {
  public String index() {
    String view = get("dataType");
    if (view == null) {
      view = "project";
    }
    DataType type = getDataType(view);
    getDatas(type);
    return forward(getView(view));
  }

  protected void getDatas(DataType type) {
    put("entityId", getLong("entityId"));
    if (type == DataType.PROJECT) {
      List<Project> projects = CollectUtils.newArrayList(0);
      DefaultAccount account = (DefaultAccount) SecurityContext.get().getSession().getPrincipal();
      Profile[] profiles = account.getProfiles();
      if (null == profiles || profiles.length == 0) {
        if (account.getCategoryId() == EamsUserCategory.STD_USER.intValue()) {
          OqlBuilder<Project> builder = OqlBuilder.from(Student.class.getName(), "std");
          builder.where("std.code = :usercode", Securities.getUsername());
          builder.select("select distinct std.project");
          projects = entityDao.search(builder);
          if (projects.isEmpty()) {
            throw new EamsException("用户缺少数据级权限配置");
          }
        } else {
          OqlBuilder<Project> builder = OqlBuilder.from(Teacher.class.getName(), "teacher");
          builder.where("teacher.staff.code = :me", Securities.getUsername())
              .join("teacher.projects", "project").where("project.endOn is null");
          builder.select("project");
          projects = entityDao.search(builder);
          if (null != projects) {
            throw new EamsException("用户缺少数据级权限配置");
          }
        }
      } else {
        projects = getProjects();
      }
      put("datas", projects);
      put("entityId", projects.get(0).getId());
    } else if (type == DataType.PROJECTID) {
      put("datas", projectContext.getProject().getId());
    } else if (type == DataType.CURRPROJECT) {
      put("datas", projectContext.getProject());
    } else if (type == DataType.DEPARTMENT) {
      put("datas", getDeparts());
    } else if (type == DataType.COLLEGE) {
      put("datas", getColleges());
    } else if (type == DataType.MAJOR) {
      put("datas", getMajors());
    } else if (type == DataType.STDTYPE) {
      put("datas", getStdTypes());
    } else if (type == DataType.EDUCATION) {
      put("datas", getLevels());
    } else if (type == DataType.SEMESTER) {
      put("datas", getSemesters(getInt("projectId")));
    } else if (type == DataType.MAJORCASCADE) {
      put("datas", getMajorsCasCade());
    } else if (type == DataType.DIRECTIONCASCADE) {
      put("datas", getDirectionCascade());
    } else if (type == DataType.BUILDINGCASCADE) {
      put("datas", getBuildingCascade());
    } else if (type == DataType.SEMESTERCALENDAR) {
      Integer projectId = getIntId("project");
      if (null == projectId) {
        projectId = projectContext.getProject().getId();
      }
      Map<String, List<Semester>> semesterCalendar = getSemesterCalendar(projectId);
      Integer semesterId = getInt("value");
      if (null != semesterId) {
        Semester semester = entityDao.get(Semester.class, semesterId);
        List<Semester> semesters = semesterCalendar.get(semester.getSchoolYear());
        if (null != semesters) {
          for (Semester s : semesters) {
            if (semester.equals(s)) {
              put("value", s);
              break;
            }
          }
        }
      }
      put("semesterId", semesterId);
      put("datas", semesterCalendar);
      put("tagId", get("tagId"));
      put("uiType", StringUtils.lowerCase(get("uiType")));
    } else if (type == DataType.BASECODE) {
      put("datas", getBasecodes());
    } else {
      put("datas", getDatas());
    }
  }

  protected List<Semester> getSemesters(Integer projectId) {
    if (projectId == null) return Collections.emptyList();
    Project p = entityDao.get(Project.class, projectId);
    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "s")
        .where("s.calendar = :calendar", p.getCalendar()).orderBy("s.beginOn")
        .where("s.year.archived=false").cacheable();
    return entityDao.search(builder);
  }

  protected Map<String, List<Semester>> getSemesterCalendar(Integer projectId) {
    HttpServletRequest request = getRequest();
    SemesterCalendar calendar = new SemesterCalendar((ValueStack) request.getAttribute("struts.valueStack"),
        false);
    calendar.setEmpty(get("empty"));
    calendar.setFormat(get("format"));
    calendar.setItems(getSemesters(projectId));
    calendar.setMulti(get("multi"));
    calendar.setYearRules(get("yearRules"));
    calendar.setTermRules(get("termRules"));
    return calendar.getSemesterTree();
  }

  @SuppressWarnings("unchecked")
  protected List<?> getBasecodes() {
    String simpleName = get("className");
    if (Strings.isNotBlank(simpleName)) {
      Iterator<CodeMeta> it = entityDao.get(CodeMeta.class, "name", simpleName).iterator();
      if (it.hasNext()) {
        try {
          return entityDao.getAll((Class<Entity<?>>) Class.forName(it.next().getClassName()));
        } catch (ClassNotFoundException e) {
          return CollectUtils.newArrayList();
        }
      }
    }
    return CollectUtils.newArrayList();
  }

  @SuppressWarnings("unchecked")
  protected Object getDatas() {
    String simpleName = get("className");
    if (Strings.isNotBlank(simpleName)) {
      Iterator<CodeMeta> it = entityDao.get(CodeMeta.class, "name", simpleName).iterator();
      if (it.hasNext()) {
        try {
          entityDao.getAll((Class<Entity<?>>) Class.forName(it.next().getClassName()));
        } catch (ClassNotFoundException e) {
          return CollectUtils.newArrayList();
        }
      }
    }
    return CollectUtils.newArrayList();
  }

  protected List<Major> getMajors() {
    List<Department> departs = getDeparts();
    if (CollectUtils.isEmpty(departs)) {
      return CollectUtils.newArrayList();
    }
    OqlBuilder<Major> builder = OqlBuilder
        .from(Major.class)
        .where("exists(from major.journals md where md.depart in (:departs))", departs)
        .where("major.beginOn <= :now and (major.endOn is null or major.endOn >= :now)", new java.util.Date());
    return entityDao.search(builder);
  }

  private List<Major> getMajorsCasCade() {
    Integer departId = getInt("departId");
    Integer levelId = getInt("levelId");
    OqlBuilder<Major> builder = OqlBuilder.from(Major.class, "major").where(
        "major.beginOn <= :now and (major.endOn is null or major.endOn >= :now)", new java.util.Date());
    if (null != levelId) {
      builder.where("exists(from major.journals md where md.level.id =:levelId)", levelId);
    }
    if (null != departId) {
      builder.where("exists(from major.journals md where md.depart.id =:departId)", departId);
      return entityDao.search(builder);
    } else {
      return CollectUtils.newArrayList();
    }
  }

  private List<Building> getBuildingCascade() {
    OqlBuilder<Building> builder = OqlBuilder.<Building>from(Classroom.class.getName(), "cl").where(
        "cl.room.building.beginOn <= :now and (cl.room.building.endOn is null or cl.room.building.endOn >= :now)",
        new java.util.Date());

    Integer campusId = getInt("campusId");
    if (campusId == null) {
      return CollectUtils.newArrayList();
    } else {
      builder.where("cl.room.building.campus.id =:campusId", campusId);
      builder.select("distinct cl.room.building");
      List<Building> buildings = entityDao.search(builder);
      Collections.sort(buildings, new PropertyComparator("code"));
      return buildings;
    }

  }

  private List<MajorDirection> getDirectionCascade() {
    OqlBuilder<MajorDirection> builder = OqlBuilder.from(MajorDirection.class, "direction").where(
        "direction.beginOn <= :now and (direction.endOn is null or direction.endOn >= :now)",
        new java.util.Date());
    Integer majorId = getInt("majorId");
    if (majorId == null) {
      return CollectUtils.newArrayList();
    } else {
      builder.where("direction.major.id =:majorId", majorId);
      return entityDao.search(builder);
    }
  }

  protected DataType getDataType() {
    String dataType = get("dataType");
    return getDataType(dataType);
  }

  protected DataType getDataType(String dataType) {
    if (null == dataType) {
      dataType = get("dataType");
    }
    if (Strings.isBlank(dataType)) {
      return DataType.PROJECT;
    }
    return DataType.valueOf(dataType.toUpperCase());
  }

  protected String getView(String view) {
    String upperView = view.toUpperCase();
    if (DataType.PROJECT.toString().equals(upperView)) {
      return "project";
    } else if (DataType.DEPARTMENT.toString().equals(upperView)) {
      return "department";
    } else if (DataType.MAJOR.toString().equals(upperView)) {
      return "major";
    } else if (DataType.STDTYPE.toString().equals(upperView)) {
      return "stdtype";
    } else if (DataType.EDUCATION.toString().equals(upperView)) {
      return "level";
    } else if (DataType.COLLEGE.toString().equals(upperView)) {
      return "college";
    } else if (DataType.SEMESTER.toString().equals(upperView)) {
      return "semester";
    } else if (DataType.SEMESTERCALENDAR.toString().equals(upperView)) {
      return "semesterCalendar";
    } else if (DataType.BASECODE.toString().equals(upperView)) {
      return "baseCode";
    } else if (DataType.PROJECTID.toString().equals(upperView)) {
      return "projectId";
    } else if (DataType.CURRPROJECT.toString().equals(upperView)) {
      return view;
    } else if (DataType.MAJORCASCADE.toString().equals(upperView)) {
      return "majorCascade";
    } else if (DataType.DIRECTIONCASCADE.toString().equals(upperView)) {
      return "directionCascade";
    } else if (DataType.BUILDINGCASCADE.toString().equals(upperView)) {
      return "buildingCascade";
    } else {
      return view;
    }
  }

  static enum DataType {
    PROJECT, DEPARTMENT, MAJOR, STDTYPE, EDUCATION, COLLEGE, SEMESTER, SEMESTERCALENDAR, BASECODE, PROJECTID, CURRPROJECT, MAJORCASCADE, DIRECTIONCASCADE, BUILDINGCASCADE;

    public String toString() {
      return this == STDTYPE ? "stdType" : super.toString();
    }
  }
}
