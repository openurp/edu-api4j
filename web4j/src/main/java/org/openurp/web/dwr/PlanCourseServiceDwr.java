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
package org.openurp.web.dwr;

import org.beangle.commons.dao.EntityDao;
import org.openurp.base.edu.model.Course;
import org.openurp.base.time.NumberRangeDigestor;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.model.MajorPlanCourse;
import org.openurp.edu.program.model.Program;

import java.util.Map;

public class PlanCourseServiceDwr {

  private EntityDao entityDao;

  public Map<String, Object> getExecutivePlanCourse(Long id) {
    ExecutivePlanCourse pc = entityDao.get(ExecutivePlanCourse.class, id);
    Map<String, Object> datas = new java.util.HashMap<String, Object>();
    datas.put("id", pc.getId().toString());
    datas.put("course", convertCourse(pc.getCourse(), pc.getGroup().getPlan().getProgram()));
    datas.put("terms", pc.getTerms().toString());
    if (pc.getWeekstate() != null) datas.put("weekstate", NumberRangeDigestor.digest(pc.getWeekstate()));
    datas.put("compulsory", pc.isCompulsory());
    datas.put("remark", pc.getRemark());
    return datas;
  }

  public Map<String, Object> getMajorPlanCourse(Long id) {
    MajorPlanCourse pc = entityDao.get(MajorPlanCourse.class, id);
    Map<String, Object> datas = new java.util.HashMap<String, Object>();
    datas.put("id", pc.getId().toString());
    datas.put("course", convertCourse(pc.getCourse(), pc.getGroup().getPlan().getProgram()));
    datas.put("terms", pc.getTerms().toString());
    if (pc.getWeekstate() != null) datas.put("weekstate", NumberRangeDigestor.digest(pc.getWeekstate()));
    datas.put("compulsory", pc.isCompulsory());
    datas.put("remark", pc.getRemark());
    return datas;
  }

  private Map<String, Object> convertCourse(Course c, Program p) {
    Map<String, Object> course = new java.util.HashMap<String, Object>();
    course.put("id", c.getId());
    course.put("name", c.getName());
    course.put("defaultCredits", c.getCredits(p.getLevel()));
    course.put("creditHours", c.getCreditHours());
    course.put("weekHours", c.getWeekHours());
    if (c.getWeeks() != null) {
      course.put("weeks", c.getWeeks());
    }
    return course;
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

}
