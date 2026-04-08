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
package org.openurp.edu.clazz.util;

import org.beangle.commons.bean.comparators.MultiPropertyComparator;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.tuple.Pair;
import org.beangle.commons.text.i18n.TextResource;
import org.beangle.orm.hibernate.udt.WeekDay;
import org.beangle.orm.hibernate.udt.WeekState;
import org.beangle.orm.hibernate.udt.WeekTime;
import org.beangle.orm.hibernate.udt.Weeks;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.edu.model.TimeSetting;
import org.openurp.base.edu.model.WeekTimeBuilder;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.resource.model.Classroom;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.ClazzActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * 输出一个教学任务教学活动的字符串表示
 */
public class ScheduleDigestor {

  public static final String singleTeacher = ":teacher1";

  public static final String multiTeacher = ":teacher+";

  public static final String moreThan1Teacher = ":teacher2";

  public static final String day = ":day";

  public static final String units = ":units";

  public static final String weeks = ":weeks";

  public static final String time = ":time";

  public static final String room = ":room";

  public static final String building = ":building";

  public static final String district = ":district";

  public static final String clazz = ":clazz";

  public static final String course = ":course";

  public static final String starton = ":starton";

  /**
   * :teacher+ :day :units :weeks :room
   */
  public static final String defaultFormat = ":teacher+ :day :units :weeks :room";

  private String delimeter = ",";

  private static Map<Integer, String> weekdayNames = new HashMap<>();

  private ScheduleDigestor() {
    super();
    for (WeekDay wd : WeekDay.All) {
      weekdayNames.put(wd.getId(), wd.getName().replace("星期", "周"));
    }
  }

  public static ScheduleDigestor getInstance() {
    return new ScheduleDigestor();
  }

  /**
   * 根据默认格式 {@link #defaultFormat} ，获得教学任务的排课文字信息
   *
   * @param textResource
   * @param clazz
   * @return
   */
  public String digest(TextResource textResource, TimeSetting timeSetting, Clazz clazz) {
    return digest(textResource, timeSetting, clazz, defaultFormat);
  }

  /**
   * 根据格式，获得教学任务的排课文字信息
   *
   * @param textResource
   * @param clazz
   * @param format
   * @return
   */
  public String digest(TextResource textResource, TimeSetting timeSetting, Clazz clazz, String format) {
    return digest(textResource, timeSetting, clazz.getSchedule().getActivities(), format);
  }

  /**
   * 根据默认格式 {@link #defaultFormat}格式，获得教学任务里部分排课活动的文字信息
   *
   * @param textResource
   * @param timeSetting
   * @param activities   任务里的部分排课活动
   * @return
   */
  public String digest(TextResource textResource, TimeSetting timeSetting,
                       Collection<ClazzActivity> activities) {
    return digest(textResource, timeSetting, activities, defaultFormat);
  }

  /**
   * 根据格式，获得教学任务里部分排课活动的文字信息
   *
   * @param textResource
   * @param timeSetting
   * @return
   */
  public String digest(TextResource textResource, TimeSetting timeSetting,
                       Collection<ClazzActivity> activities, String format) {
    if (CollectUtils.isEmpty(activities)) return "";
    if (Strings.isEmpty(format)) format = defaultFormat;

    Clazz clazzx = activities.iterator().next().getClazz();
    Semester semester = clazzx.getSemester();

    Set<Teacher> teachers = CollectUtils.newHashSet();
    boolean hasRoom = Strings.contains(format, room);
    boolean hasTeacher = Strings.contains(format, "teacher");
    if (hasTeacher) {
      for (ClazzActivity ca : activities) {
        if (CollectUtils.isNotEmpty(ca.getTeachers())) teachers.addAll(ca.getTeachers());
      }
    }
    List<ClazzActivity> mergedActivities = merge(semester, activities, hasTeacher, hasRoom);
    // 是否添加老师
    boolean addTeacher = false;
    if (hasTeacher) {
      addTeacher = true;
      if (format.indexOf(singleTeacher) != -1 && teachers.size() != 1) {
        addTeacher = false;
      }
      if (format.indexOf(moreThan1Teacher) != -1 && teachers.size() < 2) {
        addTeacher = false;
      }
      if (format.indexOf(multiTeacher) != -1 && teachers.size() == 0) {
        addTeacher = false;
      }
    }
    StringBuffer CourseArrangeBuf = new StringBuffer();
    Collections.sort(mergedActivities, new MultiPropertyComparator("clazz.course.code,time.startOn"));
    // 合并后的教学活动
    for (ClazzActivity activity : mergedActivities) {
      CourseArrangeBuf.append(format);
      int replaceStart = 0;
      replaceStart = CourseArrangeBuf.indexOf(":teacher");
      if (addTeacher) {
        StringBuilder teacherStr = new StringBuilder("");
        for (Teacher teacher : activity.getTeachers()) {
          teacherStr.append(teacher.getName());
        }
        CourseArrangeBuf.replace(replaceStart, replaceStart + 9, teacherStr.toString());
      } else if (-1 != replaceStart) {
        CourseArrangeBuf.replace(replaceStart, replaceStart + 9, "");
      }
      replaceStart = CourseArrangeBuf.indexOf(day);
      if (-1 != replaceStart) {
        WeekDay weekday = activity.getTime().getWeekday();
        if (null != textResource && textResource.getLocale().getLanguage().equals("en")) {
          CourseArrangeBuf.replace(replaceStart, replaceStart + day.length(), weekday.getEnName() + ".");
        } else {
          CourseArrangeBuf.replace(replaceStart, replaceStart + day.length(), weekdayNames.get(weekday.getId()));
        }
      }
      replaceStart = CourseArrangeBuf.indexOf(units);
      if (-1 != replaceStart) {
        Pair<Integer, Integer> rs = timeSetting.getUnitLevel(activity.getTime().getBeginAt(), activity
            .getTime().getEndAt());
        CourseArrangeBuf.replace(replaceStart, replaceStart + units.length(),
            rs.getLeft() + "-" + rs.getRight());
      }
      replaceStart = CourseArrangeBuf.indexOf(time);
      if (-1 != replaceStart) {
        // 如果教学活动中有具体时间
        CourseArrangeBuf.replace(replaceStart, replaceStart + time.length(), activity.getTime().getBeginAt()
            .toString()
            + "-" + activity.getTime().getEndAt().toString());
      }
      replaceStart = CourseArrangeBuf.indexOf(clazz);
      if (-1 != replaceStart) {
        CourseArrangeBuf.replace(replaceStart, replaceStart + clazz.length(), activity.getClazz().getCrn());
      }
      replaceStart = CourseArrangeBuf.indexOf(course);
      if (-1 != replaceStart) {
        CourseArrangeBuf.replace(replaceStart, replaceStart + course.length(), activity.getClazz()
            .getCourse().getName()
            + "(" + activity.getClazz().getCourse().getCode() + ")");
      }
      replaceStart = CourseArrangeBuf.indexOf(weeks);
      if (-1 != replaceStart) {
        // 以本年度的最后一周(而不是从教学日历周数计算而来)作为结束周进行缩略.
        // 是因为很多日历指定的周数,仅限于教学使用了.
        CourseArrangeBuf.replace(replaceStart, replaceStart + weeks.length(),
            WeekTimeBuilder.digestWeekTime(activity.getTime(), semester) + " ");
      }
      SimpleDateFormat sdf = new SimpleDateFormat("M月dd日起");
      replaceStart = CourseArrangeBuf.indexOf(starton);
      if (-1 != replaceStart) {
        java.util.Date firstDay = activity.getClazz().getFirstCourseTime();
        if (null != firstDay) {
          CourseArrangeBuf.replace(replaceStart, replaceStart + starton.length(), sdf.format(firstDay));
        }
      }
      replaceStart = CourseArrangeBuf.indexOf(room);
      if (-1 != replaceStart) {
        Set<Classroom> rooms = activity.getRooms();
        StringBuilder roomStr = new StringBuilder("");
        for (Iterator<Classroom> it = rooms.iterator(); it.hasNext(); ) {
          Classroom room = it.next();
          roomStr.append(room.getName());
          if (it.hasNext()) {
            roomStr.append(",");
          }
        }
        if (roomStr.length() == 0 && null != activity.getRemark()) {
          roomStr.append(activity.getRemark());
        }
        CourseArrangeBuf.replace(replaceStart, replaceStart + room.length(), roomStr.toString());

        replaceStart = CourseArrangeBuf.indexOf(building);
        if (-1 != replaceStart) {
          StringBuilder buildingStr = new StringBuilder("");
          for (Iterator<Classroom> iterator = rooms.iterator(); iterator.hasNext(); ) {
            Classroom room = iterator.next();
            if (room.getBuilding() != null) {
              buildingStr.append(room.getBuilding().getName());
            }
            if (iterator.hasNext()) {
              buildingStr.append(",");
            }
          }
          CourseArrangeBuf.replace(replaceStart, replaceStart + building.length(), buildingStr.toString());
        }
        replaceStart = CourseArrangeBuf.indexOf(district);
        if (-1 != replaceStart) {
          StringBuilder districtStr = new StringBuilder("");
          for (Iterator<Classroom> it = rooms.iterator(); it.hasNext(); ) {
            Classroom room = it.next();
            districtStr.append(room.getCampus().getName());
            if (it.hasNext()) {
              districtStr.append(",");
            }
          }
          CourseArrangeBuf.replace(replaceStart, replaceStart + district.length(), districtStr.toString());
        }
      }

      CourseArrangeBuf.append(" ").append(delimeter);
    }
    if (CourseArrangeBuf.lastIndexOf(delimeter) != -1) CourseArrangeBuf.delete(
        CourseArrangeBuf.lastIndexOf(delimeter), CourseArrangeBuf.length());
    return CourseArrangeBuf.toString();
  }

  /**
   * 不计较年份,比较是否是相近的教学活动.
   *
   * @param other
   * @param teacher 是否考虑教师
   * @param room    是否考虑教室
   * @return
   */
  private static boolean isSameActivityExcept(ClazzActivity target, ClazzActivity other, boolean teacher,
                                              boolean room) {
    if (teacher) {
      if (!target.getTeachers().equals(other.getTeachers())) return false;
    }
    if (room) {
      if (!target.getRooms().equals(other.getRooms())) return false;
    }
    return ClazzActivity.timeCanMergerWith(target, other, false, 25);
  }

  public static List<ClazzActivity> merge(Semester semester, Collection<ClazzActivity> activities,
                                          boolean hasTeacher, boolean hasRoom) {
    List<ClazzActivity> mergedActivities = CollectUtils.newArrayList();
    List<ClazzActivity> activitiesList = CollectUtils.newArrayList();
    for (ClazzActivity ca : activities) {
      activitiesList.add((ClazzActivity) ca.clone());
    }
    Collections.sort(activitiesList);
    // 合并相同时间点(不计年份)的教学活动
    for (ClazzActivity ca : activitiesList) {
      ClazzActivity activity = ca;
      if (ca.getTime().getStartYear() != semester.getStartYear()) {
        LocalDate nextYearStart = activity.getTime().getStartOn().toLocalDate();
        LocalDate thisYearStart = WeekTime.getStartOn(semester.getStartYear(), activity.getTime()
            .getWeekday());
        int weeks = Weeks.between(thisYearStart, nextYearStart);
        activity.getTime().setStartOn(java.sql.Date.valueOf(thisYearStart));
        activity.getTime().setWeekstate(new WeekState(activity.getTime().getWeekstate().value << weeks));
      }
      boolean merged = false;
      for (ClazzActivity added : mergedActivities) {
        if (added.getClazz().equals(activity.getClazz()) && isSameActivityExcept(added, activity, hasTeacher, hasRoom)) {
          if (added.getTime().getBeginAt().ge(activity.getTime().getBeginAt())) {
            added.getTime().setBeginAt(activity.getTime().getBeginAt());
          }
          if (added.getTime().getEndAt().le(activity.getTime().getEndAt())) {
            added.getTime().setEndAt(activity.getTime().getEndAt());
          }
          added.getTime().setWeekstate(
              new WeekState(added.getTime().getWeekstate().value | activity.getTime().getWeekstate().value));
          merged = true;
        }
      }
      if (!merged) {
        mergedActivities.add(activity);
      }
    }
    return mergedActivities;
  }

  /**
   * 设置分割符，默认为逗号
   *
   * @param delimeter
   * @return
   */
  public ScheduleDigestor setDelimeter(String delimeter) {
    this.delimeter = delimeter;
    return this;
  }

}
