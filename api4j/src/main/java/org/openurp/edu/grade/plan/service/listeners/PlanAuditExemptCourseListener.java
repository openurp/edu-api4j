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
package org.openurp.edu.grade.plan.service.listeners;

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.grade.plan.model.AuditCourseResult;
import org.openurp.edu.grade.plan.model.AuditGroupResult;
import org.openurp.edu.grade.plan.service.AuditPlanContext;
import org.openurp.edu.grade.plan.service.AuditPlanListener;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.ExemptCourse;
import org.openurp.edu.program.model.PlanCourse;

import java.util.HashSet;
import java.util.Set;

public class PlanAuditExemptCourseListener implements AuditPlanListener {
  private String exemptCourseKey = "exemptCourses";

  private EntityDao entityDao;

  public boolean startPlanAudit(AuditPlanContext context) {
    var std = context.getResult().getStd();
    var query = OqlBuilder.from(ExemptCourse.class, "ec");
    query.where("ec.fromGrade.code<=:gradeCode", std.getGrade().getCode());
    var ecs = entityDao.search(query);
    Set<Course> exemptCourses = new HashSet<>();
    for (ExemptCourse ec : ecs) {
      if (ec.getStdTypes().contains(std.getStdType())) {
        if (null == ec.getToGrade() || ec.getToGrade().getCode().compareTo(std.getGrade().getCode()) >= 0) {
          exemptCourses.add(ec.getCourse());
        }
      }
    }
    context.getParams().put(exemptCourseKey, exemptCourses);
    return true;
  }

  public boolean startCourseAudit(AuditPlanContext context, AuditGroupResult groupResult, PlanCourse planCourse) {
    return true;
  }

  public boolean startGroupAudit(AuditPlanContext context, CourseGroup courseGroup, AuditGroupResult groupResult) {
    return true;
  }

  public void endPlanAudit(AuditPlanContext context) {
    var exemptCourses = (Set<Course>) context.getParams().get(exemptCourseKey);
    if (exemptCourses.isEmpty()) return;
    for (AuditGroupResult groupResult : context.getResult().getGroupResults()) {
      for (AuditCourseResult car : groupResult.getCourseResults()) {
        if (!car.isPassed() && exemptCourses.contains(car.getCourse())) {
          car.setScores("免修");
          car.setPassed(true);
          groupResult.updateCourseResult(car);
          groupResult.checkPassed(true);
        }
      }
    }
  }

  public EntityDao getEntityDao() {
    return entityDao;
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }
}
