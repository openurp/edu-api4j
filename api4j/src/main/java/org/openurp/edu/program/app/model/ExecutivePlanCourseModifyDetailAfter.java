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
package org.openurp.edu.program.app.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlanCourse;

/** 专业计划课程修改后信息 */
@Entity(name = "org.openurp.edu.program.app.model.ExecutivePlanCourseModifyDetailAfter")
@Table(name = "EXECUTE_COURSE_MOD_AFTERS")
public class ExecutivePlanCourseModifyDetailAfter extends ExecutivePlanCourseModifyDetail {

  private static final long serialVersionUID = 7799663739549705026L;

  /** 课程申请 */
  @OneToOne(optional = false, mappedBy = "newPlanCourse")
  @JoinColumn(name = "MA_PLAN_C_MOD_A_APPLY")
  protected ExecutivePlanCourseModify apply;

  public ExecutivePlanCourseModifyDetailAfter() {
    super();
  }

  public ExecutivePlanCourseModifyDetailAfter(Course course, ExecutiveCourseGroup courseGroup) {
    super();
    setCourse(course);
    setFakeCourseGroupByReal(courseGroup);
  }

  public ExecutivePlanCourseModifyDetailAfter(ExecutivePlanCourse planCourse) {
    setCourse(planCourse.getCourse());
    setFakeCourseGroupByReal((ExecutiveCourseGroup) planCourse.getGroup());
    // getCourseHours().putAll(planCourse.getCourseHours());
    // setHskLevel(planCourse.getHskLevel());
    // getPreCourses().addAll(planCourse.getPreCourses());
    setRemark(planCourse.getRemark());
    setTerms(planCourse.getTerms());
  }

  public ExecutivePlanCourseModify getApply() {
    return apply;
  }

  public void setApply(ExecutivePlanCourseModify apply) {
    this.apply = apply;
  }

}
