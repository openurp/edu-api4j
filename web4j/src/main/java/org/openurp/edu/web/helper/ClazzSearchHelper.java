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
package org.openurp.edu.web.helper;

import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.BitStrings;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.tuple.Pair;
import org.beangle.orm.hibernate.udt.HourMinute;
import org.beangle.orm.hibernate.udt.WeekDay;
import org.beangle.orm.hibernate.udt.WeekState;
import org.beangle.struts2.helper.Params;
import org.beangle.struts2.helper.QueryHelper;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.edu.model.WeekTimeBuilder;
import org.openurp.base.model.AuditStatus;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Squad;
import org.openurp.code.edu.model.ClazzTag;
import org.openurp.code.edu.model.ExamType;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;
import org.openurp.edu.clazz.model.Schedule;
import org.openurp.edu.clazz.model.ScheduleSuggest;
import org.openurp.edu.exam.model.ExamTaker;
import org.openurp.web.helper.SearchHelper;

import java.util.Iterator;
import java.util.List;

public class ClazzSearchHelper extends SearchHelper {

  /**
   * 构造教学任务查询Query，控制数据级权限<br>
   * 本方法是控制数据级权限的
   *
   * @param request
   * @param clazz
   * @return
   * @see buildQuery(boolean)
   */
  public OqlBuilder<Clazz> buildQuery() {
    return buildQuery(true);
  }

  /**
   * 查找教学任务task<br>
   * 1)查找行政班级中以:adminClass.name为参数<br>
   * 2)查询排课情况以:clazzActivity开头<br>
   * 3)查询排考情况以:examActivity开头<br>
   * 4)考试安排完成:clazz.schedule.isExamArrangeComplete<br>
   * 5)排考查询分组情况:arrangeInfo.examGrouped<br>
   * 6)日历以:semester开头<br>
   * 7)查询教师以:teacher<br>
   * 8)特殊的选课属性:electInfo.electCountCompare标识选课人数上限和实际人数的比较<br>
   * 9)特殊的排课属性:arrangeInfo.endWeek
   *
   * @param request
   * @param clazz
   * @return
   */
  public OqlBuilder<Clazz> buildQuery(boolean applyRestriction) {
    OqlBuilder<Clazz> query = OqlBuilder.from(Clazz.class, "clazz");
    if (applyRestriction) {
      boolean shouldApply = true;
      if (projectContext.isRoot() || projectContext.getDeparts().containsAll(projectContext.getProject().getDepartments())) {
        shouldApply = false;
      }
      if (shouldApply) {
        query.where("clazz.teachDepart in(:departs)", projectContext.getDeparts());
      }
      // projectContext.applyRestriction(query);
    }
    Integer semesterId = Params.getInt("clazz.semester.id");
    if (semesterId == null) semesterId = Params.getInt("semesterId");
    Semester semester = entityDao.get(Semester.class, semesterId);
    String clazzIdSeq = Params.get("splitIds");
    if (Strings.isNotEmpty(clazzIdSeq)) {
      query.where("clazz.id in (:clazzIds)", Strings.splitToLong(clazzIdSeq));
      query.limit(QueryHelper.getPageLimit());
      return query;
    }
    Project project = projectContext.getProject();
    query.where("clazz.project=:project", project);
    QueryHelper.populateConditions(query,
        "clazz.project.id,clazz.schedule.status,clazz.id,clazz.courseType.name");

    // 查询课程类别
    String courseTypeName = Params.get("clazz.courseType.name");
    boolean courseTypeNameNotLike = Params.getBool("fake.courseType.name.notLike");
    if (Strings.isNotBlank(Strings.trim(courseTypeName))) {
      if (courseTypeNameNotLike) {
        query.where("clazz.courseType.name not like :courseTypeName", "%" + courseTypeName + "%");
      } else {
        query.where("clazz.courseType.name like :courseTypeName", "%" + courseTypeName + "%");
      }
    }

    // 教师
    Boolean teacherIsNull = Params.getBoolean("fake.teacher.null");
    Integer teacherDepart = Params.getInt("fake.teacher.department.id");
    String teacherName = Params.get("teacher.name");
    if (Strings.isBlank(teacherName)) {
      teacherName = Params.get("clazz.teacher.name");
    }
    if ((teacherDepart != null || Strings.isNotBlank(teacherName)) && null == teacherIsNull) teacherIsNull = false;

    if (teacherIsNull != null) {
      if (Boolean.TRUE.equals(teacherIsNull)) {
        query.where("size(clazz.teachers) = 0");
      } else {
        if (teacherDepart == null && Strings.isBlank(teacherName)) {
          query.where("size(clazz.teachers) > 0");
        } else if (null != teacherDepart && Strings.isBlank(teacherName)) {
          query.where("exists (from clazz.teachers tt where tt.department = :teacherDepartment)",
              entityDao.get(Department.class, teacherDepart));
        } else if (null == teacherDepart && Strings.isNotBlank(teacherName)) {
          query.where("exists (from clazz.teachers tt where tt.name like :tname)",
              '%' + teacherName + '%');
        } else {
          query
              .where(
                  "exists (from clazz.teachers tt where tt.name like :tname adn tt.department =:teacherDepartment)",
                  '%' + teacherName + '%', entityDao.get(Department.class, teacherDepart));
        }

      }
    }

    // 审核状态
    String status = Params.get("fake.state");
    if (Strings.isNotBlank(status)) {
      query.where("clazz.status = :status", AuditStatus.valueOf(status.toUpperCase()));
    }
    // 查询课程安排情况
    Long buildingId = Params.getLong("classroom.building.id");
    String classroomName = Params.get("classroom.name");
    Integer weekday = Params.getInt("fake.time.weekday");
    String courseUnit = Params.get("courseActivity.time.beginAt");
    if (null == courseUnit) courseUnit = Params.get("fake.time.unit");
    Integer activityWeekStart = Params.getInt("fake.time.weekstart");
    Integer activityWeekEnd = Params.getInt("fake.time.weekend");

    Long activityWeekState = null;
    if (null != activityWeekStart || null != activityWeekEnd) {
      if (null == activityWeekStart) activityWeekStart = activityWeekEnd;
      if (null == activityWeekEnd) activityWeekEnd = activityWeekStart;
      if (activityWeekEnd >= activityWeekStart && activityWeekEnd < 52 && activityWeekStart > 0) {
        StringBuilder sb = new StringBuilder(Strings.repeat("0", 53));
        for (int i = activityWeekStart; i <= activityWeekEnd; i++)
          sb.setCharAt(i, '1');
        activityWeekState = BitStrings.binValueOf(sb.toString());
      }
    }

    if (null != buildingId || Strings.isNotBlank(courseUnit) || null != weekday || null != activityWeekState || Strings.isNotBlank(classroomName)) {
      StringBuilder activityQuery = new StringBuilder("exists( from clazz.schedule.activities as ca where 1=1 ");
      if (Strings.isNotBlank(courseUnit)) {
        if (courseUnit.contains(":")) {
          activityQuery.append("and ca.time.beginAt <= :beginAt and ca.time.endAt >= :beginAt ");
          query.param("beginAt", new HourMinute(courseUnit));
        } else {
          activityQuery.append("and ca.beginUnit <= :beginAt and ca.endUnit >= :beginAt ");
          query.param("beginAt", Short.valueOf(courseUnit));
        }
      }
      if (null != weekday) {
        activityQuery.append(" and ca.time.startOn in (:startOn)");
        query.param("startOn", WeekTimeBuilder.getYearStartOns(semester, WeekDay.get(weekday)));
      }

      if (null != activityWeekState) activityQuery.append(" and bitand(ca.time.weekstate,"
          + activityWeekState + ")>0");
      if (null != buildingId) {
        activityQuery.append(" and exists(from ca.rooms as cr where cr.building.id=" + buildingId + ")");
      }
      if (Strings.isNotBlank(classroomName)) {
        if (classroomName.contains(" ") || classroomName.contains(",")) {
          query.param("roomNames", Strings.split(classroomName));
          activityQuery.append(" and exists(from ca.rooms as cr where cr.name in (:roomNames))");
        } else {
          activityQuery.append(" and exists(from ca.rooms as cr where cr.name like :roomName)");
          query.param("roomName", "%" + classroomName + "%");
        }

      }
      activityQuery.append(")");
      query.where(activityQuery.toString());
    }

    // 排课状态
    String courseScheduleStatus = Params.get("clazz.schedule.status");
    if (Strings.isNotEmpty(courseScheduleStatus)) {
      Schedule.Status s = Schedule.Status.valueOf(courseScheduleStatus);
      if (s.equals(Schedule.Status.DONT_ARRANGE)) {
        query.where("clazz.schedule.creditHours =0");
      } else if (s.equals(Schedule.Status.NEED_ARRANGE)) {
        query.where("clazz.schedule.creditHours >0 and size(clazz.schedule.activities)=0");
      } else {
        query.where("clazz.schedule.creditHours >0 and size(clazz.schedule.activities)>0");
      }
    }

    // 周数
    Integer weeks = Params.getInt("fake.weeks");
    if (null != weeks) {
      query.where("week_state_weeks(clazz.schedule.weekstate)=:weeks", weeks);
    }
    // 周课时
    Float weekHours = Params.getFloat("fake.weekHours");
    if (null != weekHours) {
      query
          .where(
              "week_state_weeks(clazz.schedule.weekstate) >0 and  (clazz.schedule.creditHours / week_state_weeks(clazz.schedule.weekstate) ) = :weekHours",
              weekHours.intValue());
    }
    // 起止周
    Integer startWeek = Params.getInt("fake.week.start");
    Integer endWeek = Params.getInt("fake.week.end");

    if (null != startWeek || null != endWeek) {
      List<Integer> weekIndecies = CollectUtils.newArrayList();
      if (null == startWeek) startWeek = 1;
      if (null == endWeek) endWeek = semester.getWeeks();
      while (startWeek <= endWeek) {
        weekIndecies.add(startWeek);
        startWeek += 1;
      }
      query.where("bitand(clazz.schedule.weekstate,:weekstate)>0", WeekState.of(weekIndecies).value);
    }

    // 人数上限
    Integer limitCountStart = Params.getInt("fake.limitCount.start");
    if (null != limitCountStart) {
      query.where("clazz.enrollment.capacity >= :limitCountStart", limitCountStart);
    }
    Integer limitCountEnd = Params.getInt("fake.limitCount.end");
    if (null != limitCountEnd) {
      query.where("clazz.enrollment.capacity <= :limitCountEnd", limitCountEnd);
    }
    // 实际人数
    Integer stdCountStart = Params.getInt("fake.stdCount.start");
    if (null != stdCountStart) {
      query.where("clazz.enrollment.stdCount >= :stdCountStart", stdCountStart);
    }
    Integer stdCountEnd = Params.getInt("fake.stdCount.end");
    if (null != stdCountEnd) {
      query.where("clazz.enrollment.stdCount <= :stdCountEnd", stdCountEnd);
    }

    // 查询跨院任务
    Boolean crossdepart = Params.getBoolean("fake.crossdepart");
    if (Boolean.TRUE.equals(crossdepart)) {
      query.where("(clazz.enrollment.depart != clazz.teachDepart or clazz.enrollment.depart is null)");
    } else if (Boolean.FALSE.equals(crossdepart)) {
      query.where("clazz.enrollment.depart = clazz.teachDepart");
    }

    Boolean guapai = Params.getBoolean("fake.guapai");
    if (Boolean.TRUE.equals(guapai)) {
      query.where("exists (select tag.id from clazz.tags tag where tag.id=:guaPai)",
          ClazzTag.PredefinedTags.GUAPAI.getId());
    } else if (Boolean.FALSE.equals(guapai)) {
      query.where("not exists (select tag.id from clazz.tags tag where tag.id=:guaPai)",
          ClazzTag.PredefinedTags.GUAPAI.getId());
    }

    String squadName = Params.get("fake.squad.name");
    if (Strings.isNotBlank(squadName)) {
      OqlBuilder<Squad> squadQuery = OqlBuilder.from(Squad.class, "squad");
      squadQuery.where("squad.name like :name", "%" + squadName + "%").where(
          "squad.project = :project", project);

      List<Squad> squades = entityDao.search(squadQuery);
      if (CollectUtils.isNotEmpty(squades)) {
        StringBuilder squadCondition = new StringBuilder();
        squadCondition
            .append("exists (")
            .append("select litem.id from clazz.enrollment.restrictions lgroup join lgroup.items litem where")
            .append(" litem.meta=").append(ClazzRestrictionMeta.Squad.getId()).append(" and ")
            .append(" litem.included=true ").append(" and (");
        for (Iterator<Squad> iter = squades.iterator(); iter.hasNext(); ) {
          Squad squad = iter.next();
          squadCondition.append("locate(',").append(squad.getId().toString())
              .append(",',litem.contents) > 0").append(" or litem.contents ='").append(squad.getId().toString())
              .append("' ");

          if (iter.hasNext()) {
            squadCondition.append(" or ");
          }
        }
        squadCondition.append(")").append(")");

        query.where(squadCondition.toString());
      }
    }

    // FIXME 查询选课人数
    Integer compare = Params.getInt("electInfo.electCountCompare");
    if (null != compare) {
      String op = "";
      if (compare.intValue() == 0) {
        op = "=";
      } else if (compare.intValue() < 0) {
        op = "<";
      } else {
        op = ">";
      }
      query.where("clazz.enrollment.stdCount " + op + " clazz.enrollment.capacity");
    }

    /** 排考相关查询条件 */
    Integer examTypeId = Params.getInt("examType.id");

    // 需要补考的教学任务
    if (examTypeId != null && examTypeId.equals(ExamType.DELAY)) query.where("1=0");
    if (examTypeId != null && examTypeId.equals(ExamType.MAKEUP)) {
      query
          .where(
              "exists (from "
                  + ExamTaker.class.getName()
                  + " examTaker where examTaker.examType.id in (:examTypeIds)"
                  + " and examTaker.clazz.project.id = :projectId and examTaker.semester.id = :semesterId and examTaker.clazz = clazz)",
              new Integer[]{ExamType.MAKEUP, ExamType.DELAY}, Params.getInt("clazz.project.id"),
              semesterId);
    }

//    Integer isExamArrangeComplete = Params.getInt("isExamArrangeComplete"); // 0:未安排,1:已安排,2:全部
//    if (isExamArrangeComplete == null) {
//      isExamArrangeComplete = 3;
//    }
//    // 查找已安排了考试的教学任务
//    if (isExamArrangeComplete == 1) {
//      String activitySubQuery = "exists(from   " + ExamActivity.class.getName()
//          + "examActivity left join examActivity.examRooms examRoom where examActivity.clazz=clazz and "
//          + "examActivity.examType.id=:examTypeId";
//      List<Object> activityParams = CollectUtils.newArrayList();
//      activityParams.add(examTypeId);
//      String examState = Params.get("exam.state");
//      if (Strings.isNotEmpty(examState)) {
//        activitySubQuery += " and examActivity.state =:state";
//        activityParams.add(ExamAuditState.valueOf(examState));
//      }
//      Boolean roomIsNull = Params.getBoolean("roomIsNull");
//      if (null != roomIsNull) {
//        if (roomIsNull) {
//          activitySubQuery += " and size(examActivity.examRooms) = 0";
//        } else {
//          activitySubQuery += " and size(examActivity.examRooms) > 0";
//        }
//      }
//      String examRoom = Params.get("exam.room.name");
//      if (Strings.isNotEmpty(examRoom)) {
//        activitySubQuery += " and examRoom.room.name like :roomName";
//        activityParams.add("%" + examRoom + "%");
//      }
//      String departId = Params.get("exam.examiner.department.id");
//      if (Strings.isNotEmpty(departId)) {
//        activitySubQuery += " and examRoom.examiner.department.id = :departId";
//        activityParams.add(Long.valueOf(departId));
//      }
//      String examStartTime = Params.get("exam.startTime");
//      if (Strings.isNotBlank(examStartTime)) {
//        Timestamp startTime = Timestamp.valueOf(examStartTime + ":00");
//        activitySubQuery += " and examActivity.beginAt >= :examStartTime";
//        activityParams.add(startTime);
//      }
//      String examEndTime = Params.get("exam.endTime");
//      if (Strings.isNotBlank(examEndTime)) {
//        Timestamp endTime = Timestamp.valueOf(examEndTime + ":00");
//        activitySubQuery += " and examActivity.endAt <= :examEndTime";
//        activityParams.add(endTime);
//      }
//      Integer fromWeek = Params.getInt("exam.week");
//      if (fromWeek != null) {
//        Date[] dates = WeekTimeBuilder.getDateRange(semester, fromWeek);
//        activitySubQuery += " and examActivity.beginAt >= :examStartTime1 and examActivity.endAt <= :examEndTime1";
//        activityParams.add(dates[0]);
//        activityParams.add(dates[1]);
//      }
//
//      activitySubQuery += ")";
//      Condition activityCondition = new Condition(activitySubQuery);
//      activityCondition.params(activityParams);
//      query.where(activityCondition);
//
//    } else if (isExamArrangeComplete == 0) {
//      // 查看未安排考试的教学任务
//      query.where("not exists (from " + ExamActivity.class.getName() + " exam "
//          + "where exam.clazz=clazz and exam.examType.id=:examTypeId)", examTypeId);
//    }

    query.limit(QueryHelper.getPageLimit());
    if (Strings.isEmpty(Params.get("orderBy"))) {
      query.orderBy("clazz.crn");
    } else {
      if ("fake.weekHours asc".equals(Params.get("orderBy"))) {
        query
            .orderBy("(clazz.course.creditHours / (clazz.schedule.lastWeek + 1 - clazz.schedule.firstWeek)) asc");
      } else if ("fake.weekHours desc".equals(Params.get("orderBy"))) {
        query
            .orderBy("(clazz.course.creditHours / (clazz.schedule.lastWeek + 1 - clazz.schedule.firstWeek)) desc");
      } else if ("clazz.schedule.weeks asc".equals(Params.get("orderBy"))) {
        query.orderBy("(clazz.schedule.lastWeek - clazz.schedule.firstWeek + 1) asc");
      } else if ("clazz.schedule.weeks desc".equals(Params.get("orderBy"))) {
        query.orderBy("(clazz.schedule.lastWeek - clazz.schedule.firstWeek + 1) desc");
      } else if ("clazz.fake.state asc".equals(Params.get("orderBy"))) {
        query.orderBy("str(clazz.status) asc");
      } else if ("clazz.fake.state desc".equals(Params.get("orderBy"))) {
        query.orderBy("str(clazz.status) desc");
      } else {
        query.orderBy(Order.parse(Params.get("orderBy")));
      }
    }
    // 预排课情况/排课建议设置情况
    Boolean isPreScheduled = Params.getBoolean("fake.arrangeSuggest.status");
    if (isPreScheduled != null) {
      if (isPreScheduled) {
        query.where("exists(from " + ScheduleSuggest.class.getName()
            + " suggest where suggest.clazz = clazz)");
      } else {
        query.where("not exists(from " + ScheduleSuggest.class.getName()
            + " suggest where suggest.clazz = clazz)");
      }
    }

    /**
     * 查找存在符合查询条件的授课对象组的教学任务
     */
    List<Pair<ClazzRestrictionMeta, Object>> limitItemConditions = CollectUtils.newArrayList();
    Integer levelId = Params.getInt("limitGroup.level.id");
    if (null != levelId) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Level,
          levelId));
    }
    Integer stdTypeId = Params.getInt("limitGroup.stdType.id");
    if (null != stdTypeId) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.StdType, stdTypeId));
    }
    Integer departId = Params.getInt("limitGroup.depart.id");
    if (null != departId) {
      limitItemConditions
          .add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Department, departId));
    }
    Integer majorId = Params.getInt("limitGroup.major.id");
    if (null != majorId) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Major, majorId));
    }
    Integer directionId = Params.getInt("limitGroup.direction.id");
    if (null != directionId) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Direction,
          directionId));
    }
    Integer genderId = Params.getInt("limitGroup.gender.id");
    if (null != genderId) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Gender, genderId));
    }
    String grade = Params.get("limitGroup.grade");
    if (StringUtils.isNotBlank(grade)) {
      limitItemConditions.add(new Pair<ClazzRestrictionMeta, Object>(ClazzRestrictionMeta.Grade, grade));
    }
    if (CollectUtils.isNotEmpty(limitItemConditions)) {
      query.where(limitGroupCondition(limitItemConditions));
    }
    return query;
  }

  /**
   * 根据用户页面上提交的查询条件查询教学任务<br>
   * 查询出的教学任务默认按照任务序号升序
   *
   * @return
   */
  public List<Clazz> searchClazz() {
    return entityDao.search(buildQuery());
  }

  private String limitGroupCondition(List<Pair<ClazzRestrictionMeta, Object>> limitItemConditions) {
    StringBuilder condition = new StringBuilder();
    condition.append("exists (").append("select lgroup.id from clazz.enrollment.restrictions lgroup where ");
    for (int i = 0; i < limitItemConditions.size(); i++) {
      Pair<ClazzRestrictionMeta, Object> limitItemCondition = limitItemConditions.get(i);
      ClazzRestrictionMeta field = limitItemCondition._1;
      Object value = limitItemCondition._2;
      condition.append(" exists ( from lgroup.items litem where").append(" litem.meta=")
          .append(field.getId()).append(" and ").append(" litem.included=true")
          .append(" and locate(',").append(value.toString())
          .append(",' , ',' || litem.contents || ',')>0").append(")");

      if (i < limitItemConditions.size() - 1) {
        condition.append(" and ");
      }
    }
    condition.append(")");
    return condition.toString();
  }
}
