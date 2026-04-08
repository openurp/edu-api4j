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
package org.openurp.edu.program.plan.dao.impl;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.plan.dao.PlanCommonDao;
import org.openurp.edu.program.plan.dao.PlanCourseCommonDao;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.util.ProgramHibernateClassGetter;

import java.util.List;

public class PlanCourseCommonDaoHibernate extends HibernateEntityDao implements PlanCourseCommonDao {

  private PlanCommonDao planCommonDao;

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void addPlanCourse(PlanCourse planCourse, CoursePlan plan) {
    CourseGroup myGroup = get(ProgramHibernateClassGetter.hibernateClass(planCourse.getGroup()),
        planCourse.getGroup().getId());

    myGroup.addPlanCourse(planCourse);
    saveOrUpdate(planCourse);
    saveOrUpdate(myGroup);
    planCourse.setCourse(get(Course.class, planCourse.getCourse().getId()));

    planCourseGroupCommonDao.updateGroupTreeCredits(planCourseGroupCommonDao.getTopGroup(myGroup));
    plan.setCredits(planCommonDao.statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  public void removePlanCourse(PlanCourse planCourse, CoursePlan plan) {
    CourseGroup myGroup = planCourse.getGroup();
    myGroup.getPlanCourses().remove(planCourse);
    remove(planCourse);

    planCourseGroupCommonDao.updateGroupTreeCredits(planCourseGroupCommonDao.getTopGroup(myGroup));
    plan.setCredits(planCommonDao.statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  public void updatePlanCourse(PlanCourse planCourse, CoursePlan plan) {
    CourseGroup group = planCourse.getGroup();
    saveOrUpdate(planCourse);
    saveOrUpdate(group);

    planCourseGroupCommonDao.updateGroupTreeCredits(planCourseGroupCommonDao.getTopGroup(group));
    plan.setCredits(planCommonDao.statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  public ExecutivePlanCourse getExecutivePlanCourseByCourse(ExecutivePlan executePlan, Course course) {
    OqlBuilder query = OqlBuilder.from(ProgramHibernateClassGetter.hibernateClass(executePlan), "plan");
    query.select("planCourse").join("plan.groups", "cgroup").join("cgroup.planCourses", "planCourse")
        .where("planCourse.course=:course", course).where("plan.id = :planId", executePlan.getId());
    List<? extends ExecutivePlanCourse> courses = search(query);
    if (null == courses || courses.size() == 0) { return null; }
    return courses.get(0);
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

  public void setPlanCommonDao(PlanCommonDao planCommonDao) {
    this.planCommonDao = planCommonDao;
  }

}
