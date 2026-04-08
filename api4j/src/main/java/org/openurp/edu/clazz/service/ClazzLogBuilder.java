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
package org.openurp.edu.clazz.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.beangle.commons.lang.Strings;
import org.openurp.base.hr.model.Teacher;
import org.openurp.edu.clazz.model.Clazz;

public class ClazzLogBuilder {

  public static final String TYPE = "TYPE";
  public static final String LESSON_ID = "LESSON.ID";
  public static final String LESSON_PROJECT = "LESSON.PROJECT";
  public static final String LESSON_SEMESTER = "LESSON.SEMESTER";
  public static final String LESSON_NO = "LESSON.NO";
  public static final String COURSE_CODE = "LESSON.COURSE.CODE";
  public static final String COURSE_NAME = "LESSON.COURSE.NAME";
  public static final String REASON = "REASON";
  public static final String DETAIL = "DETAIL";

  public static final String[] LOG_FIELDS = { LESSON_ID, TYPE, LESSON_PROJECT, LESSON_SEMESTER, LESSON_NO,
      COURSE_CODE, COURSE_NAME, REASON, DETAIL };

  public enum Operation {
    CREATE, DELETE, UPDATE
  }

  private static Map<String, String> makeInformations(Clazz clazz) {
    Map<String, String> empty = new HashMap<String, String>();
    for (int i = 0; i < LOG_FIELDS.length; i++) {
      empty.put(LOG_FIELDS[i], "");
    }
    empty.put(LESSON_ID, String.valueOf(clazz.getId()));
    empty.put(LESSON_PROJECT, String.valueOf(clazz.getProject().getName()));
    empty.put(LESSON_SEMESTER, clazz.getSemester().getId().toString());
    empty.put(LESSON_NO, clazz.getCrn());
    empty.put(COURSE_CODE, clazz.getCourse().getCode());
    empty.put(COURSE_NAME, clazz.getCourse().getName());
    return empty;
  }

  private static String toString(Map<String, String> informations) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < LOG_FIELDS.length; i++) {
      sb.append(LOG_FIELDS[i]).append("=").append(informations.get(LOG_FIELDS[i]));
      if (i != LOG_FIELDS.length - 1) {
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  public static String create(Clazz clazz, String reason) {
    Map<String, String> map = makeInformations(clazz);
    map.put(TYPE, ClazzLogBuilder.Operation.CREATE.name());
    map.put(DETAIL, stringify(clazz));
    if (Strings.isNotEmpty(reason)) {
      map.put(REASON, reason);
    }
    return toString(map);
  }

  public static String delete(Clazz clazz, String reason) {
    Map<String, String> map = makeInformations(clazz);
    map.put(TYPE, ClazzLogBuilder.Operation.DELETE.name());
    if (Strings.isNotEmpty(reason)) {
      map.put(REASON, reason);
    }
    return toString(map);
  }

  public static String update(Clazz clazz, String reason) {
    Map<String, String> map = makeInformations(clazz);
    map.put(TYPE, ClazzLogBuilder.Operation.UPDATE.name());
    map.put(DETAIL, stringify(clazz));
    if (Strings.isNotEmpty(reason)) {
      map.put(REASON, reason);
    }
    return toString(map);
  }

  private static String stringify(Clazz clazz) {
    StringBuilder sb = new StringBuilder();
    append(sb, "课程序号", clazz.getCrn());
    append(sb, "学期", clazz.getSemester().getSchoolYear() + '-' + clazz.getSemester().getName());
    append(sb, "课程", clazz.getCourse().getName() + '[' + clazz.getCourse().getCode() + ']');
    append(sb, "教学项目", clazz.getProject().getName());
    append(sb, "课程类别", clazz.getCourseType().getName());
    append(sb, "开课院系", clazz.getTeachDepart().getName());

    StringBuilder tsb = new StringBuilder();
    for (Iterator<Teacher> iter = clazz.getTeachers().iterator(); iter.hasNext();) {
      Teacher teacher = iter.next();
      tsb.append(teacher.getName() + '[' + teacher.getCode() + ']');
      if (iter.hasNext()) {
        tsb.append(',');
      }
    }
    append(sb, "授课教师", tsb.toString());

    if (clazz.getLangType() != null) {
      append(sb, "授课语言", clazz.getLangType().getName());
    } else {
      append(sb, "授课语言", null);
    }

    append(sb, "挂牌", 1);

    if (clazz.getCampus() != null) {
      append(sb, "校区", clazz.getCampus().getName());
    } else {
      append(sb, "校区", null);
    }
    append(sb, "教学班", clazz.getClazzName());
    append(sb, "年级", clazz.getEnrollment().getGrades());
    if (clazz.getEnrollment().getDepart() != null) {
      append(sb, "上课院系", clazz.getEnrollment().getDepart().getName());
    } else {
      append(sb, "上课院系", null);
    }

    append(sb, "实际人数", clazz.getEnrollment().getStdCount());
    append(sb, "人数上限", clazz.getEnrollment().getCapacity());
    append(sb, "起始周", clazz.getSchedule().getFirstWeek());
    append(sb, "结束周", clazz.getSchedule().getLastWeek());
    append(sb, "课时", clazz.getSchedule().getCreditHours());
    append(sb, "备注", clazz.getRemark());
    // 把最后的一个换行给弄掉
    sb.replace(sb.length() - 1, sb.length(), "");
    return sb.toString();
  }

  private static void append(StringBuilder sb, String fieldName, Object value) {
    if (value == null) {
      sb.append(fieldName).append("=空\n");
    } else {
      sb.append(fieldName).append('=').append(value.toString()).append('\n');
    }
  }
}
