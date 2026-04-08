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
package org.openurp.edu.clazz.service.internal;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Strings;
import org.beangle.orm.hibernate.udt.WeekDay;
import org.beangle.orm.hibernate.udt.WeekState;
import org.beangle.orm.hibernate.udt.WeekTime;
import org.beangle.commons.lang.tuple.Pair;
import org.openurp.code.edu.model.CourseType;
import org.openurp.base.edu.model.*;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.model.AuditStatus;
import org.openurp.base.model.Department;
import org.openurp.base.resource.model.Classroom;
import org.openurp.code.edu.model.ActivityType;
import org.openurp.code.edu.model.ElectionMode;
import org.openurp.edu.clazz.dao.ClazzDao;
import org.openurp.edu.clazz.model.*;
import org.openurp.edu.clazz.service.*;
import org.openurp.edu.clazz.util.ClazzElectionUtil;
import org.openurp.edu.room.model.Occupancy;
import org.openurp.edu.room.model.RoomOccupyApp;

import java.io.Serializable;
import java.util.*;

/**
 * 教学任务管理服务实现
 */
class LogHelper {
  public void info(String s) {
  }
}

public class ClazzServiceImpl extends BaseServiceImpl implements ClazzService {

  private ClazzDao clazzDao;

  private ClazzLogHelper clazzLogHelper;

  public List<Department> teachDepartsOfSemester(List<Project> projects, List<Department> departments,
                                                 Semester semester) {
    if (CollectUtils.isNotEmpty(projects) && CollectUtils.isNotEmpty(departments)) {
      OqlBuilder<Department> query = OqlBuilder.from(Clazz.class.getName() + " clazz");
      query.select("distinct(clazz.teachDepart)");
      query.where(
          " clazz.semester=:semester and clazz.teachDepart in (:departments) and clazz.project in (:projects) ",
          semester, departments, projects);
      return entityDao.search(query);
    } else {
      return CollectUtils.newArrayList(0);
    }
  }

  public List<CourseType> courseTypesOfSemester(List<Project> projects, List<Department> departments,
                                                Semester semester) {
    if (CollectUtils.isNotEmpty(projects) && CollectUtils.isNotEmpty(departments)) {
      OqlBuilder<CourseType> query = OqlBuilder.from(Clazz.class.getName() + " clazz");
      query.select("distinct(clazz.courseType)");
      query.where(
          " clazz.semester=:semester and clazz.teachDepart in (:departments) and clazz.project in (:projects) ",
          semester, departments, projects);
      return entityDao.search(query);
    } else {
      return CollectUtils.newArrayList(0);
    }
  }

  public List<Department> attendDepartsOfSemester(List<Project> projects, Semester semester) {
    if (CollectUtils.isNotEmpty(projects)) {
      OqlBuilder<ClazzRestrictionItem> qq = OqlBuilder.from(Clazz.class.getName() + " clazz");
      qq.join("clazz.enrollment.limitGroups", "lgroup").join("lgroup.items", "litem")
          .where("litem.meta = :meta", ClazzRestrictionMeta.Department)
          .where("clazz.semester = :semester", semester).where("clazz.project in (:projects)", projects);
      qq.select("litem").cacheable();
      List<ClazzRestrictionItem> limitItems = entityDao.search(qq);
      StringBuilder sb = new StringBuilder();
      for (ClazzRestrictionItem item : limitItems) {
        String[] ids = item.getContents().split(",");
        sb.append(Strings.join(ids, ",")).append(',');
      }
      Integer[] departmentIds = Strings.splitToInt(sb.toString());
      List<Integer> distinctIds = new ArrayList<Integer>();
      Arrays.sort(departmentIds);
      Integer prev = -1;
      for (int i = 0; i < departmentIds.length; i++) {
        if (!prev.equals(departmentIds[i])) {
          distinctIds.add(departmentIds[i]);
        }
        prev = departmentIds[i];
      }
      if (CollectUtils.isEmpty(distinctIds)) {
        return new ArrayList<Department>();
      }
      return entityDao.get(Department.class, distinctIds);
    } else {
      return CollectUtils.newArrayList(0);
    }
  }

  public List<Department> canAttendDepartsOfSemester(List<Project> projects, List<Department> departments,
                                                     Semester semester) {
    if (CollectUtils.isNotEmpty(projects) && CollectUtils.isNotEmpty(departments)) {
      OqlBuilder<Department> query = OqlBuilder.from(Department.class, "department");
      query.where(
          "exists (from org.openurp.edu.program.model.ExecutivePlan plan"
              + " where plan.program.department=department and plan.program.major.project in (:projects)"
              + " and plan.program.department in (:departs)"
              + " and current_date() >= plan.program.beginOn and (plan.program.endOn is null or current_date() <= plan.program.endOn))",
          semester, departments, projects);
      return entityDao.search(query);
    } else {
      return CollectUtils.newArrayList(0);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Project> getProjectsForTeacher(Teacher teacher) {
    OqlBuilder<?> query = OqlBuilder.from(Clazz.class, "clazz").select("clazz.project")
        .join("clazz.teachers", "teacher").where("teacher = :teacher", teacher);
    return (List<Project>) entityDao.search(query);
  }

  public List<Clazz> getClazzByCategory(Serializable id, ClazzFilterStrategy strategy,
                                        Collection<Semester> semesters) {
    if (null == id || semesters.isEmpty()) {
      return CollectUtils.newArrayList(0);
    } else {
      String prefix = "select distinct clazz.id from org.openurp.edu.clazz.model.Clazz as clazz ";
      String postfix = " and clazz.semester in (:semesters) ";
      OqlBuilder<Long> query = OqlBuilder.hql(prefix + strategy.getFilterString() + postfix);
      query.param("id", id);
      query.param("semesters", semesters);
      List<Long> clazzIds = entityDao.search(query);
      List<Clazz> rs = CollectUtils.newArrayList();
      if (CollectUtils.isNotEmpty(clazzIds)) {
        rs = entityDao.get(Clazz.class, clazzIds.toArray(new Long[0]));
      }
      return rs;
    }
  }

  public List<Clazz> getClazzByCategory(Serializable id, ClazzFilterStrategy strategy, Semester semester) {
    if (null == id || null == semester.getId()) {
      return CollectUtils.newArrayList(0);
    } else {
      return getClazzByCategory(id, strategy, Collections.singletonList(semester));
    }
  }

  public List<Clazz> copy(List<Clazz> clazzes, TaskCopyParams params) {
    List<Clazz> copiedTasks = new ArrayList<Clazz>();
    // 之所以这么做是因为clazzDao.saveOrUpdate会flush hibernate的session
    for (Clazz clazz : clazzes) {
      Clazz copy = clazz.clone();
      // 拷贝学生
      if (!params.isCopyCourseTakers()) {
        copy.getEnrollment().getCourseTakers().clear();
        copy.getEnrollment().setStdCount(0);
      } else {
        for (CourseTaker taker : copy.getEnrollment().getCourseTakers()) {
          taker.setSemester(params.getSemester());
          taker.setElectionMode(Model.newInstance(ElectionMode.class, ElectionMode.Assigned));
        }
      }
      copy.setStatus(AuditStatus.UNSUBMITTED);
      copy.setSemester(params.getSemester());

      ClazzElectionUtil.normalizeTeachClass(copy);
      copiedTasks.add(copy);
    }

    for (Clazz copy : copiedTasks) {
      clazzDao.saveOrUpdate(copy);
      clazzLogHelper.log(ClazzLogBuilder.create(copy, "复制任务,生成任务"));
    }
    return copiedTasks;
  }

  public <T extends Entity<?>> List<Clazz> getClazzes(Semester semester, T entity) {
    OqlBuilder<Clazz> builder = OqlBuilder.from(Clazz.class, "clazz");
    builder.where("clazz.semester =:semester", semester);
    Condition con = CourseLimitUtils.build(entity, "lgi");
    List<?> params = con.getParams();
    builder.where(
        "exists(from clazz.enrollment.restrictions lg join lg.items as lgi where" + con.getContent() + ")",
        params.get(0), params.get(1), params.get(2));
    return entityDao.search(builder);
  }

  public void fillTeachers(Long[] teacherIds, Clazz clazz) {
    if (teacherIds == null || teacherIds.length == 0) {
      clazz.getTeachers().clear();
    } else {
      List<Teacher> teachers= new ArrayList<>();
      for(Long teacherId :teacherIds){
        if(null!=teacherId) {
          teachers.add(entityDao.get(Teacher.class, teacherId));
        }
      }
      if(!teachers.equals(clazz.getTeachers())){
        clazz.getTeachers().clear();
        clazz.getTeachers().addAll(teachers);
      }
    }
  }

  @Override
  public void adjustWeekstateBySchedule(Semester semester, List<Clazz> clazzes) {
    if (null == clazzes || clazzes.isEmpty()) return;
    Map<WeekDay, Pair<Integer, Integer>> offsets = CollectUtils.newHashMap();
    for (WeekDay day : WeekDay.All) {
      offsets.put(day,
          Pair.of(WeekTimeBuilder.getOffset(semester, day), WeekTimeBuilder.getReverseOffset(semester, day)));
    }
    for (Clazz l : clazzes) {
      Set<ClazzActivity> activities = l.getSchedule().getActivities();
      if (!activities.isEmpty()) {
        WeekState state = new WeekState(0);
        for (ClazzActivity ac : activities) {
          WeekTime time = ac.getTime();
          if (time.getStartOn().getYear() == semester.getBeginOn().getYear()) {
            state = new WeekState(
                state.value | (time.getWeekstate().value >> offsets.get(time.getWeekday())._1));
          } else {
            state = new WeekState(
                state.value | (time.getWeekstate().value << offsets.get(time.getWeekday())._2));
          }
        }
        if (!l.getSchedule().getWeekstate().equals(state)) {
          l.getSchedule().setWeekstate(state);
        }
      }
    }

  }

  /**
   * 1.合并教学活动
   * 2.纠正教学活动中跨年的活动
   * 3.同步教学活动和教室占用表的不匹配现象
   */
  public void normalizeActivity(Clazz l) {
    List<ClazzActivity> newCas = CollectUtils.newArrayList();
    for (ClazzActivity ca : l.getSchedule().getActivities()) {
      if (WeekTimeBuilder.needNormalize(ca.getTime())) {
        WeekTime next = WeekTimeBuilder.normalize(ca.getTime());
        ClazzActivity newCa = new ClazzActivity();
        newCa.setClazz(l);
        newCa.setTime(next);
        newCa.getTeachers().addAll(ca.getTeachers());
        newCa.getRooms().addAll(ca.getRooms());
        newCas.add(newCa);
      }
    }
    if (!newCas.isEmpty()) {
      l.getSchedule().getActivities().addAll(newCas);
    }
    List<ClazzActivity> activityList = new ArrayList<ClazzActivity>();
    activityList.addAll(l.getSchedule().getActivities());
    List<ClazzActivity> mergedActivityList = ClazzActivity.mergeActivites(activityList);
    l.getSchedule().getActivities().retainAll(mergedActivityList);

    OqlBuilder<Occupancy> oq = OqlBuilder.from(Occupancy.class, "occupancy");
    oq.where("occupancy.activityId = :activityId and occupancy.app.id=:appId", l.getId(), RoomOccupyApp.COURSE);
    oq.where("occupancy.activityType = :activityType", new ActivityType(ActivityType.Course));
    List<Occupancy> occupancies = entityDao.search(oq);
    Map<java.sql.Date, List<Occupancy>> occupancyMaps = CollectUtils.newHashMap();
    for (Occupancy o : occupancies) {
      List<Occupancy> occsOneDay = occupancyMaps.get(o.getTime().getStartOn());
      if (null == occsOneDay) {
        occsOneDay = CollectUtils.newArrayList();
        occupancyMaps.put(o.getTime().getStartOn(), occsOneDay);
      }
      occsOneDay.add(o);
    }

    Map<java.sql.Date, List<ClazzActivity>> caMaps = CollectUtils.newHashMap();
    for (ClazzActivity ca : l.getSchedule().getActivities()) {
      List<ClazzActivity> casOneDay = caMaps.get(ca.getTime().getStartOn());
      if (null == casOneDay) {
        casOneDay = CollectUtils.newArrayList();
        caMaps.put(ca.getTime().getStartOn(), casOneDay);
      }
      casOneDay.add(ca);
    }
    List<Occupancy> savedOccupancies = new ArrayList<Occupancy>();
    for (Map.Entry<java.sql.Date, List<ClazzActivity>> entry : caMaps.entrySet()) {
      java.sql.Date startOn = entry.getKey();
      List<ClazzActivity> cas = entry.getValue();
      List<Occupancy> ocs = occupancyMaps.get(startOn);
      if (null == ocs) ocs = new ArrayList<Occupancy>();
      for (ClazzActivity ca : cas) {
        for (Classroom r : ca.getRooms()) {
          Occupancy occupancy = null;
          if (cas.size() == 1 && ocs.size() == 1) {
            occupancy = ocs.get(0);
            if (!ca.getTime().equals(occupancy.getTime())) {
              occupancy.setTime(ca.getTime());
            }
            if (!occupancy.getRoom().equals(r)) {
              occupancy.setRoom(r);
            }
            savedOccupancies.add(occupancy);
            occupancies.remove(occupancy);
          } else {
            for (Occupancy occ : ocs) {
              if (r.equals(occ.getRoom())) {
                occupancy = occ;
                break;
              }
            }
            if (occupancy != null) {
              if (!ca.getTime().equals(occupancy.getTime())) {
                occupancy.setTime(ca.getTime());
              }
              if (!occupancy.getRoom().equals(r)) {
                occupancy.setRoom(r);
              }
              savedOccupancies.add(occupancy);
              occupancies.remove(occupancy);
            } else {
              Occupancy newOcc = new Occupancy();
              newOcc.setTime(cas.get(0).getTime());
              newOcc.setApp(new RoomOccupyApp(RoomOccupyApp.COURSE));
              newOcc.setActivityId(l.getId());
              newOcc.setActivityType(new ActivityType(ActivityType.Course));
              newOcc.setRoom(r);
              newOcc.setComments(l.getCrn() + "[" + l.getCourse().getName() + "]");
              savedOccupancies.add(newOcc);
            }
          }
        }
      }
    }
    entityDao.saveOrUpdate(l, savedOccupancies);
    if (!occupancies.isEmpty()) entityDao.remove(occupancies);
  }

  public void setClazzDao(ClazzDao clazzDao) {
    this.clazzDao = clazzDao;
  }

  public void setClazzLogHelper(ClazzLogHelper clazzLogHelper) {
    this.clazzLogHelper = clazzLogHelper;
  }

}
