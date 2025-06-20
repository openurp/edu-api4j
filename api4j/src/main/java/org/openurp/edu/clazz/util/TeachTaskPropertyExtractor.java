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

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.tuple.Pair;
import org.beangle.commons.text.i18n.TextResource;
import org.beangle.commons.transfer.exporter.DefaultPropertyExtractor;
import org.openurp.base.edu.model.Textbook;
import org.openurp.base.edu.model.TimeSetting;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.resource.model.Classroom;
import org.openurp.base.service.SemesterService;
import org.openurp.base.service.TimeSettingService;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.edu.model.ExamType;
import org.openurp.code.edu.model.TeachingNature;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.ClazzActivity;
import org.openurp.edu.clazz.model.ClazzRestriction;
import org.openurp.edu.clazz.service.CourseLimitService;
import org.openurp.edu.exam.util.ExamActivityDigestor;

import java.text.MessageFormat;
import java.util.*;

public class TeachTaskPropertyExtractor extends DefaultPropertyExtractor {

  protected String courseActivityFormat;

  protected String examActivityFormat;

  protected ExamType examType;

  protected SemesterService semesterService;

  protected TimeSettingService timeSettingService;

  protected CourseLimitService courseLimitService;


  protected EntityDao entityDao;

  protected List<TeachingNature> teachingNatures = new ArrayList<TeachingNature>();

  public TeachTaskPropertyExtractor(TextResource resource) {
    super(resource);
  }

  public Object getPropertyValue(Object target, String property) throws Exception {
    Clazz clazz = (Clazz) target;
    TimeSetting timeSetting = timeSettingService.getClosestTimeSetting(clazz.getProject(),
        clazz.getSemester(), null);
    ScheduleDigestor digestor = ScheduleDigestor.getInstance();
    ExamActivityDigestor examDigestor = ExamActivityDigestor.getInstance();
    /**
     * 下面和教师有关
     */
    if ("fake.teachers".equals(property)) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<Teacher> iter = clazz.getTeachers().iterator(); iter.hasNext(); ) {
        Teacher teacher = iter.next();
        sb.append(teacher.getName());
        if (iter.hasNext()) {
          sb.append(',');
        }
      }
      return sb.toString();
    }
    if ("fake.teachers.code".equals(property)) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<Teacher> iter = clazz.getTeachers().iterator(); iter.hasNext(); ) {
        Teacher teacher = iter.next();
        sb.append(teacher.getCode());
        if (iter.hasNext()) {
          sb.append(',');
        }
      }
      return sb.toString();
    } else if ("teachers.eduDegreeInside".equals(property)) {
      String eduDegreeNames = "";
      if (clazz.getTeachers() != null) {
        boolean singleTeacher = clazz.getTeachers().size() == 1 ? true : false;
        for (Iterator iter = clazz.getTeachers().iterator(); iter.hasNext(); ) {
          Teacher teacher = (Teacher) iter.next();
          if (singleTeacher) {
            return super.getPropertyValue(teacher, "degreeInfo.eduDegreeInside.name");
          } else {
            if (eduDegreeNames != "") eduDegreeNames += " ";
            eduDegreeNames += super.getPropertyValue(teacher, "degreeInfo.eduDegreeInside.name") + " ";
          }
        }
      }
      return eduDegreeNames;
    } else if ("teachers.degree".equals(property)) {
      String degreeNames = "";
      if (clazz.getTeachers() != null) {
        boolean singleTeacher = clazz.getTeachers().size() == 1 ? true : false;
        for (Iterator iter = clazz.getTeachers().iterator(); iter.hasNext(); ) {
          Teacher teacher = (Teacher) iter.next();
          if (singleTeacher) {
            return super.getPropertyValue(teacher, "degreeInfo.degree.name");
          } else {
            if (degreeNames != "") degreeNames += " ";
            degreeNames += super.getPropertyValue(teacher, "degreeInfo.degree.name") + " ";
          }
        }
      }
      return degreeNames;
    } else if ("teachers.teacherType".equals(property)) {
      return getPropertyIn(clazz.getTeachers(), "teacherType.name");
    } else if ("teachers.department.name".equals(property)) {
      return getPropertyIn(clazz.getTeachers(), "department.name");
    }
    /**
     * 以下是教学班信息
     */
    else if ("fake.semester".equals(property)) {
      return clazz.getSemester().getSchoolYear() + '-' + clazz.getSemester().getName();
    } else if ("fake.state".equals(property)) {
      return clazz.getStatus().getFullName();
    }
    /**
     * 和排课有关
     */
    else if ("semester".equals(property)) {
      return clazz.getSemester().getSchoolYear() + " " + clazz.getSemester().getName();
    } else if ("courseSchedule.activities.weeks".equals(property)) {
      return digestor.digest(textResource, timeSetting, clazz, ScheduleDigestor.weeks);
    } else if ("fake.weeks".equals(property)) {
      return clazz.getSchedule().getWeeks();
    } else if ("fake.arrange".equals(property)) {
      // 课程安排
      return digestor.digest(textResource, timeSetting, clazz, ":day :units :weeks :room");
    } else if ("roomType.name".equals(property)) {
      if (clazz.getSchedule().getRoomType() != null) {
        return clazz.getSchedule().getRoomType().getName();
      }
      return "";
    } else if ("fake.schedule.practiceHour".equals(property)) {
      TeachingNature type = null;
      for (TeachingNature hourType : teachingNatures) {
        if (hourType.getName().indexOf("实践") != -1) {
          type = hourType;
        }
      }
      if (type != null) {
        return clazz.getCourse().getHour(type);
      }
      return null;
    } else if ("fake.schedule.theoryHour".equals(property)) {
      TeachingNature type = null;
      for (TeachingNature nature : teachingNatures) {
        if (nature.getName().indexOf("理论") != -1) {
          type = nature;
        }
      }
      if (type != null) {
        return clazz.getCourse().getHour(type);
      }
      return null;
    } else if ("fake.schedule.operateHour".equals(property)) {
      TeachingNature type = null;
      for (TeachingNature nature : teachingNatures) {
        if (nature.getName().indexOf("上机") != -1 || nature.getName().indexOf("操作") != -1) {
          type = nature;
        }
      }
      if (type != null) {
        return clazz.getCourse().getHour(type);
      }
      return null;
    }

    // 教室可容纳人数
    else if ("courseSchedule.activities.room.capacityOfCourse".equals(property)) {
      Set<Classroom> rooms = new HashSet<Classroom>();
      for (ClazzActivity activity : clazz.getSchedule().getActivities()) {
        rooms.addAll(activity.getRooms());
      }
      Integer[] seats = new Integer[rooms.size()];
      int i = 0;
      for (Classroom room : rooms) {
        seats[i++] = room.getCourseCapacity();
      }
      return Strings.join(seats, ',');

    } else if ("courseSchedule.activities.time".equals(property)) {
      return digestor.digest(textResource, timeSetting, clazz, ":day :units");
    } else if ("courseSchedule.activities.room".equals(property)) {
      Set<Classroom> rooms = new HashSet<Classroom>();
      for (ClazzActivity activity : clazz.getSchedule().getActivities()) {
        rooms.addAll(activity.getRooms());
      }
      String[] names = new String[rooms.size()];
      int i = 0;
      for (Classroom room : rooms) {
        names[i++] = room.getName();
      }
      return Strings.join(names, ",");
    } else if ("clazz.schedule".equals(property)) {
      String arrangeInfo = "";
      int weeks = clazz.getSchedule().getWeeks();
      float weekUnits = clazz.getSchedule().getWeekHours();
      if (0 != weeks && 0 != weekUnits) {
        arrangeInfo = weeks + "*" + weekUnits;
      }
      return arrangeInfo;
    }
    /**
     * 默认导出
     */
    else if ("eduLevel.name".equals(property)) {
      Map<ClazzRestriction, Pair<Boolean, List<EducationLevel>>> tmpRes = courseLimitService
          .xtractEducationLimit(clazz.getEnrollment());
      String eduLevelStr = "";
      for (Pair<Boolean, List<EducationLevel>> pair : tmpRes.values()) {
        if (!pair._1) {
          for (EducationLevel level : pair._2) {
            if (Strings.contains(eduLevelStr, level.getName() + "(不包括)")) {
              continue;
            }
            if (Strings.isNotBlank(eduLevelStr)) {
              eduLevelStr += ",";
            }
            eduLevelStr += level.getName() + "(不包括)";
          }
        }
        if (pair._1) {
          for (EducationLevel level : pair._2) {
            if (Strings.contains(eduLevelStr, level.getName() + "(包括)")) {
              continue;
            }
            if (Strings.isNotBlank(eduLevelStr)) {
              eduLevelStr += ",";
            }
            eduLevelStr += level.getName() + "(包括)";
          }
        }
      }
      return eduLevelStr;
    } else if ("teachLang.name".equals(property)) {
      if (clazz.getLangType() != null) {
        return clazz.getLangType().getName();
      }
      return "";
    } else {
      try {
        return super.getPropertyValue(target, property);
      } catch (Exception e) {
        System.out.println("Cannot find " + property + " in clazz");
        return "";
      }
    }
  }

  public void setSemesterService(SemesterService semesterService) {
    this.semesterService = semesterService;
  }

  public void setCourseLimitService(CourseLimitService courseLimitService) {
    this.courseLimitService = courseLimitService;
  }

  public void setSessionFormat(String courseActivityFormat) {
    this.courseActivityFormat = courseActivityFormat;
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
    teachingNatures = entityDao.getAll(TeachingNature.class);
  }

  public void setTimeSettingService(TimeSettingService timeSettingService) {
    this.timeSettingService = timeSettingService;
  }

}
