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
package org.openurp.base.service.impl;

import org.beangle.commons.bean.transformers.PropertyTransformer;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.Calendar;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.service.SemesterService;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.*;

public class SemesterServiceImpl extends BaseServiceImpl implements SemesterService {

  public Semester getSemester(Integer id) {
    Semester semester = (Semester) entityDao.get(Semester.class, id);
    return semester;
  }

  public Calendar getCalendar(Project project) {
    return project.getCalendar();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<Calendar> getCalendars(List<Project> projects) {
    List<Integer> ids = CollectUtils.collect(projects, new PropertyTransformer("id"));
    OqlBuilder query = OqlBuilder.from(Project.class, "project").where("project.id in (:projectIds))", ids);
    List<Project> ps = entityDao.search(query);
    Set<Calendar> calendars = CollectUtils.newHashSet();
    for (Project p : ps) {
      calendars.add(p.getCalendar());
    }
    return new ArrayList(calendars);
  }

  public Semester getSemester(Project project, String schoolYear, String name) {
    Calendar calendar = getCalendar(project);
    return getSemester(calendar, schoolYear, name);
  }

  public List<Semester> getSemestersOfOverlapped(Semester semester) {
    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester");
    builder.where("semester.beginOn <= :endOn", semester.getEndOn());
    builder.where("semester.endOn >= :beginOn", semester.getBeginOn());
    builder.cacheable(true);
    return entityDao.search(builder);
  }

  public Semester getSemester(Calendar calendar, Date date) {
    Map<String, Object> params = CollectUtils.newHashMap();
    params.put("calendar", calendar);
    params.put("date", date);
    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester")
        .where("semester.calendar=:calendar");
    builder.orderBy("abs(semester.beginOn - :date + semester.endOn - :date)");
    builder.params(params).cacheable();
    List<Semester> rs = entityDao.search(builder);
    if (rs.size() < 1) {
      return null;
    } else {
      return (Semester) rs.get(0);
    }
  }

  public Semester getSemester(Calendar calendar, Date begOn, Date endOn) {
    Map<String, Object> params = CollectUtils.newHashMap();
    params.put("calendar", calendar);
    params.put("begOn", begOn);
    params.put("endOn", endOn);
    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester")
        .where("semester.beginOn<=:endOn and semester.endOn>=:begOn and semester.calendar=:calendar")
        .orderBy("semester.beginOn");
    builder.params(params);
    List<Semester> rs = entityDao.search(builder);
    if (rs.size() < 1) {
      return null;
    } else {
      return (Semester) rs.get(0);
    }
  }

  public Semester getSemester(Calendar calendar, String schoolYear, String name) {
    OqlBuilder query = OqlBuilder.from(Semester.class, "semester");
    query.where("semester.calendar = :calendar", calendar);
    query.where("semester.year.name = :schoolYear", schoolYear);
    query.where("semester.name = :name", name);
    List semesters = entityDao.search(query);
    if (semesters.isEmpty()) {
      return null;
    } else {
      return (Semester) semesters.get(0);
    }
  }

  public Semester getNextSemester(Semester semester) {
    OqlBuilder<Semester> nextQuery = OqlBuilder.from(Semester.class, "s");
    nextQuery.where("s.calendar=:calendar", semester.getCalendar());
    nextQuery.where("s.beginOn>:beginOn", semester.getEndOn()).orderBy("s.beginOn").limit(1, 1);
    List<Semester> nexts = entityDao.search(nextQuery);
    if (nexts.isEmpty()) return null;
    else return nexts.get(0);
  }

  /**
   */
  public Semester getPreviousSemester(Calendar calendar) {
    Map params = new HashMap();
    params.put("calendar", calendar);
    List rs = entityDao.search("@getPreviousSemester", params);
    if (rs.size() < 1) throw new EntityNotFoundException("without schoolYear for calendar id:" + calendar);
    return (Semester) rs.get(0);
  }

  public Semester getCurSemester(Calendar calendar) {
    OqlBuilder builder = OqlBuilder.from(Calendar.class, "calender").where("calender.id = :calenderId",
        calendar.getId());
    builder.join("calender.semesters", "semester")
        .where("semester.beginOn <= :date and semester.endOn >= :date", new java.util.Date());
    builder.select("semester");
    List rs = entityDao.search(builder);
    if (rs.size() == 1) {
      return (Semester) rs.get(0);
    } else {
      return calendar.getNearest();
    }
  }

  public Semester getCurSemester(Integer calendarId) {
    Calendar calendar = entityDao.get(Calendar.class, calendarId);
    if (null == calendar) {
      return null;
    }
    return getCurSemester(calendar);
  }

  public int getTermsBetween(Semester first, Semester second, boolean omitSmallTerm) {
    if (!first.getCalendar().equals(second.getCalendar())) return 0;
    OqlBuilder query = OqlBuilder.from(Semester.class, "semester");
    query.select("count(semester.id)").where("semester.beginOn >= :firstStart")
        .where("semester.beginOn <= :secondStart").where("semester.calendar = :calendar")
        // 系统规定，如果周期小于等于2个月的就是小学期
        .where(
            "((:omitSmallTerm = true and (year(semester.endOn) * 12 + month(semester.endOn)) - (year(semester.beginOn) * 12 + month(semester.beginOn)) > 2) or (:omitSmallTerm = false))");

    query.param("calendar", first.getCalendar());
    query.param("omitSmallTerm", new Boolean(omitSmallTerm));
    query.cacheable();
    Date firDate = first.getBeginOn();
    Date secDate = second.getBeginOn();
    /*
     * 因为数据库中的date类型可能会带有小时／分钟／秒的信息，因此在通过两个日期区间找在这个区间内的
     * 学期数会有偏差。为了解决这个问题，我们在区间两端各加减一天，扩大区间以保证查询结果的正确性。
     */
    if (first.after(second)) {
      GregorianCalendar calendar = new GregorianCalendar();

      // calendar.setTime(secDate);
      // calendar.roll(java.util.Calendar.DAY_OF_MONTH, -1);
      // query.param("firstStart", calendar.getTime());
      query.param("firstStart", secDate);

      // calendar.setTime(firDate);
      // calendar.roll(java.util.Calendar.DAY_OF_MONTH, 1);
      // query.param("secondStart", calendar.getTime());
      query.param("secondStart", firDate);

      return -((Number) entityDao.search(query).get(0)).intValue();
    } else {
      // GregorianCalendar calendar = new GregorianCalendar();
      //
      // calendar.setTime(firDate);
      // calendar.roll(java.util.Calendar.DAY_OF_MONTH, -1);
      query.param("firstStart", firDate);

      // calendar.setTime(secDate);
      // calendar.roll(java.util.Calendar.DAY_OF_MONTH, 1);
      //
      query.param("secondStart", secDate);
      return ((Number) entityDao.search(query).get(0)).intValue();
    }
  }

  /**
   * @see org.openurp.base.edu.service.service.system.semester.SemesterService#removeSemester(org.openurp.base.model.Semester)
   */
  public void removeSemester(Semester semester) {
    entityDao.remove(semester);
  }

  public void saveSemester(Semester semester) {
    if (null == semester) return;
    if (Strings.isEmpty(semester.getCode())) semester.setCode(semester.getSchoolYear() + semester.getName());

    entityDao.saveOrUpdate(semester);
  }

  public boolean checkDateCollision(Semester semester) {
    if (null == semester) return false;
    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester");
    builder.where("semester.calendar=:calendar", semester.getCalendar());
    if (null != semester.getId()) builder.where("id <> " + semester.getId());

    Collection<Semester> semesterList = entityDao.search(builder);
    for (Semester one : semesterList) {
      if (semester.getBeginOn().before(one.getEndOn()) && one.getBeginOn().before(semester.getEndOn()))
        return true;
    }
    return false;
  }

  public Semester getCurSemester(Project project) {
    Calendar calendar = getCalendar(project);
    return getCurSemester(calendar);
  }

  public Semester getNearestSemester(Project project) {
    Calendar calendar = getCalendar(project);
    return getNearestSemester(calendar);
  }

  public Semester getNearestSemester(Calendar calendar) {
    List ss = entityDao.search("select id,schoolYear,name,endOn from " + Semester.class.getName());

    /**
     * Semester as semester where semester.calendar=:calendar and
     * ((semester.beginOn-current_date())*(semester.endOn-current_date()))
     * <= all(select (c.beginOn-current_date())*(c.endOn-current_date())
     * from Semester as c where c.calendar =:calendar)
     */
    OqlBuilder<Semester> query = OqlBuilder.from(Semester.class, "semester");
    query.where("semester.calendar = :calendar", calendar).where(
        "((semester.beginOn-current_date())*(semester.endOn-current_date())) <= all(select (c.beginOn-current_date())*(c.endOn-current_date())from  "
            + Semester.class.getName() + "as c where c.calendar =:calendar)",
        calendar);
    List<Semester> semesters = entityDao.search(query);
    if (semesters.isEmpty()) {
      return null;
    } else {
      return (Semester) semesters.get(0);
    }
  }

  public List<Semester> getSemesters(Integer semesterStartId, Integer semesterEndId) {
    Semester semesterStart = new Semester();
    Semester semesterEnd = new Semester();
    if (semesterStartId != null) {
      semesterStart = entityDao.get(Semester.class, semesterStartId);
    }
    if (semesterEndId != null) {
      semesterEnd = entityDao.get(Semester.class, semesterEndId);
    }

    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester");

    if (semesterStartId != null && semesterEndId == null) {
      builder.where("semester.beginOn >= :startTime", semesterStart.getBeginOn());
    }
    if (semesterStartId == null && semesterEndId != null) {
      builder.where("semester.beginOn <= :endTime", semesterEnd.getBeginOn());
    }
    if (semesterStartId != null && semesterEndId != null) {
      builder.where("semester.beginOn >= :startTime", semesterStart.getBeginOn());
      builder.where("semester.beginOn <= :endTime", semesterEnd.getBeginOn());
    }
    List<Semester> semesterList = new ArrayList<Semester>();
    semesterList = entityDao.search(builder);
    return semesterList;
  }

  public Semester getPrevSemester(Semester semester) {
    OqlBuilder<Semester> query = OqlBuilder.from(Semester.class, "semester")
        .where("semester.calendar = :calendar", semester.getCalendar())
        .where("semester.endOn < (select cur.beginOn from " + Semester.class.getName()
            + " cur where cur.id = :curId)", semester.getId())
        .orderBy("semester.endOn desc").cacheable();
    List<Semester> semesters = entityDao.search(query);
    if (CollectUtils.isNotEmpty(semesters)) {
      return semesters.get(0);
    }
    return null;
  }

}
