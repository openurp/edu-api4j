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
package org.openurp.web.helper;

import java.util.Collection;

import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.struts2.helper.Params;
import org.beangle.struts2.helper.QueryHelper;
import org.openurp.base.resource.model.Building;
import org.openurp.base.resource.model.Classroom;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.std.model.Squad;
import org.openurp.base.hr.model.Teacher;

public class BaseInfoSearchHelper extends SearchHelper {

  /**
   * @param
   * @return
   */
  public Collection<Squad> searchSquad(Project project, Semester semester) {
    return entityDao.search(buildSquadQuery(project, semester));
  }

  public Collection<Teacher> searchTeacher(Project project) {
    return entityDao.search(buildTeacherQuery(project));
  }

  public Collection<Classroom> searchClassroom(Project project) {
    return entityDao.search(buildClassroomQuery(project));
  }

  /**
   * 构建一个班级查询功能 查询的参数以squad开头
   *
   * @param
   * @return
   */
  public OqlBuilder<Squad> buildSquadQuery(Project project) {
    OqlBuilder<Squad> builder = OqlBuilder.from(Squad.class, "squad");
    builder.where("squad.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.where("squad.stdType in (:stdTyps)", projectContext.getStdTypes());
    builder.where("squad.department in (:departments)", projectContext.getDeparts());
    Boolean enabled = Params.getBoolean("enabled");
    if (Boolean.TRUE.equals(enabled)) {
      builder.where("squad.beginOn <= :now and (squad.endOn is null or squad.endOn >= :now)",
          new java.util.Date());
    } else if (Boolean.FALSE.equals(enabled)) {
      builder.where("squad.beginOn > :now or squad.endOn < :now", new java.util.Date());
    }
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "squad.code";
      builder.orderBy(new Order("squad.grade", false));
      builder.orderBy(new Order("squad.code"));
    } else {
      builder.orderBy(orderByPras);
    }
    return builder;
  }

  public OqlBuilder<Squad> buildSquadQuery(Project project, Semester semester) {
    OqlBuilder<Squad> builder = OqlBuilder.from(Squad.class, "squad");
    builder.where("squad.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.where("squad.stdType in (:stdTyps)", projectContext.getStdTypes());
    builder.where("squad.department in (:departments)", projectContext.getDeparts());
    Boolean enabled = Params.getBoolean("enabled");
    if (null == enabled) {
      enabled = Boolean.TRUE;
    }
    if (Boolean.TRUE.equals(enabled)) {
      builder.where("squad.beginOn <= :endOn and (squad.endOn is null or squad.endOn >= :beginOn)",
          semester.getEndOn(), semester.getBeginOn());
    } else if (Boolean.FALSE.equals(enabled)) {
      builder.where("squad.beginOn > :endOn or squad.endOn < :beginOn", semester.getEndOn(),
          semester.getBeginOn());
    }
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "squad.code";
      builder.orderBy(new Order("squad.grade", false));
      builder.orderBy(new Order("squad.code"));
    } else {
      builder.orderBy(orderByPras);
    }
    return builder;
  }

  /**
   * 构建一个教师查询功能
   *
   * @param
   * @return
   */
  public OqlBuilder<Teacher> buildTeacherQuery(Project project) {
    OqlBuilder<Teacher> builder = OqlBuilder.from(Teacher.class, "teacher");
    builder.where(":project in elements(teacher.projects)", project);
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "teacher.staff.code";
    }
    builder.orderBy(orderByPras);
    return builder;
  }

  /**
   * 构建一个教室查询
   *
   * @param
   * @return
   */
  public OqlBuilder<Classroom> buildClassroomQuery(Project project) {
    OqlBuilder<Classroom> query = OqlBuilder.from(Classroom.class, "classroom");
    query.where(":projects in elements(classroom.projects)", project);
    QueryHelper.populateConditions(query);
    // FIMXE 暂时去除部门权限
    // String departIdSeq = Params.get("roomDepartId");
    // String resourceName = getResourceName();
    // Resource resource = funcPermissionService.getResource(resourceName);
    // if (Strings.isEmpty(departIdSeq) && null != resource) {
    // List<?> departs = projectContext.getDeparts();
    // if (!departs.isEmpty()) {
    // builder.where(
    // "exists(from classroom.departments department where department in (:departs))",departs);
    // } else {
    // builder.where("1=2");
    // }
    // }else {
    // Integer[] departIds = Strings.splitToInt(departIdSeq);
    // if (!org.beangle.commons.lang.Arrays.isEmpty(departIds)) {
    // builder.where(
    // "exists(from classroom.departments department where department.id in
    // (:departIds))",departIds);
    // }else {
    // builder.where("1=2");
    // }
    // }

    query.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "classroom.name";
    }
    query.orderBy(orderByPras);
    return query;
  }

  /**
   * 构建一个教学楼查询
   *
   * @param
   * @param moduleName
   * @return
   */
  public OqlBuilder<Building> buildBuildingQuery() {
    OqlBuilder<Building> builder = OqlBuilder.from(Building.class, "building");
    builder.where("building.department in (:departs)", projectContext.getDeparts());
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "building.code";
    }
    builder.orderBy(orderByPras);
    return builder;
  }

  /**
   * 构建一个专业查询
   *
   * @param
   * @param moduleName
   * @return
   */
  public OqlBuilder<Major> buildMajorQuery(Project project) {
    OqlBuilder<Major> builder = OqlBuilder.from(Major.class, "major");
    builder.where("major.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "major.code";
    }
    builder.orderBy(orderByPras);
    return builder;
  }

  /**
   * 构建一个专业查询
   *
   * @param
   * @param moduleName
   * @return
   */
  public OqlBuilder<Major> buildMajorQuery(Project project, Long levelId) {
    OqlBuilder<Major> builder = OqlBuilder.from(Major.class, "major");
    builder.where("major.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "major.code";
    }
    if (null != levelId) {
      builder.where("exists (from major.journals j where j.level.id=:eduId)", levelId);
    }
    builder.orderBy(orderByPras);
    return builder;
  }

  /**
   * 构建一个方向查询
   *
   * @param
   * @param moduleName
   * @return
   */
  public OqlBuilder<MajorDirection> buildDirectionQuery(Project project) {
    OqlBuilder<MajorDirection> builder = OqlBuilder.from(MajorDirection.class, "direction");
    builder.where("direction.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "direction.code";
    }
    builder.orderBy(orderByPras);
    return builder;
  }

  /**
   * 构建一个方向查询
   *
   * @param project
   * @param levelId 培养层次ID
   * @return
   */
  public OqlBuilder<MajorDirection> buildDirectionQuery(Project project, Long levelId) {
    OqlBuilder<MajorDirection> builder = OqlBuilder.from(MajorDirection.class, "direction");
    builder.where("direction.project=:project", project);
    QueryHelper.populateConditions(builder);
    builder.limit(QueryHelper.getPageLimit());
    String orderByPras = Params.get(Order.ORDER_STR);
    if (Strings.isEmpty(orderByPras)) {
      orderByPras = "direction.code";
    }
    if (null != levelId) {
      String hql = "exists (from direction.journals dd where dd.level.id = :levelId)";
      builder.where(hql, levelId);
    }
    builder.orderBy(orderByPras);
    return builder;
  }
}
