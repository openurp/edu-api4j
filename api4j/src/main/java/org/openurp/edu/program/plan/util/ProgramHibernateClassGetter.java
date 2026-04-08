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
package org.openurp.edu.program.plan.util;

import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.model.MajorCourseGroup;
import org.openurp.edu.program.model.MajorPlan;
import org.openurp.edu.program.model.MajorPlanCourse;
import org.openurp.edu.program.model.PlanCourse;

/**
 * 用于获得eams-teach-program包中的各种类别的Hibernate映射的接口类
 */
public class ProgramHibernateClassGetter {

  public static Class<? extends CourseGroup> hibernateClass(CourseGroup planGroup) {
    if (MajorCourseGroup.class
        .isAssignableFrom(planGroup.getClass())) { return MajorCourseGroup.class; }
    if (ExecutiveCourseGroup.class.isAssignableFrom(planGroup.getClass())) { return ExecutiveCourseGroup.class; }
    return null;
  }

  public static Class<? extends PlanCourse> hibernateClass(PlanCourse planCourse) {
    if (MajorPlanCourse.class.isAssignableFrom(planCourse.getClass())) { return MajorPlanCourse.class; }
    if (ExecutivePlanCourse.class.isAssignableFrom(planCourse.getClass())) { return ExecutivePlanCourse.class; }
    return null;
  }

  public static Class<? extends CoursePlan> hibernateClass(CoursePlan plan) {
    if (MajorPlan.class.isAssignableFrom(plan.getClass())) { return MajorPlan.class; }
    if (ExecutivePlan.class.isAssignableFrom(plan.getClass())) { return ExecutivePlan.class; }
    return null;
  }

}
