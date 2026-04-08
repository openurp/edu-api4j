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
package org.openurp.edu.program.plan.service.impl;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.openurp.base.edu.model.Course;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.model.PlanCourse;
import org.openurp.edu.program.plan.dao.PlanCommonDao;
import org.openurp.edu.program.plan.service.ExecutivePlanQueryBuilder;
import org.openurp.edu.program.plan.service.ExecutivePlanService;

import java.util.*;

/**
 * 培养计划服务接口
 */
public class ExecutivePlanServiceImpl extends BaseServiceImpl implements ExecutivePlanService {

  private PlanCommonDao planCommonDao;

  public Set<String> getUnusedCourseTypeNames(ExecutivePlan plan) {
    return planCommonDao.getUnusedCourseTypeNames(plan);
  }

  public void removeExecutivePlan(ExecutivePlan plan) {
    planCommonDao.removePlan(plan);
  }

  public void saveOrUpdateExecutivePlan(ExecutivePlan plan) {
    planCommonDao.saveOrUpdatePlan(plan);
  }

  public float statPlanCredits(Long planId) {
    return statPlanCredits(entityDao.get(ExecutivePlan.class, planId));
  }

  public float statPlanCredits(ExecutivePlan plan) {
    return planCommonDao.statPlanCredits(plan);
  }

  public boolean hasCourse(ExecutiveCourseGroup cgroup, Course course) {
    return planCommonDao.hasCourse(cgroup, course);
  }

  public boolean hasCourse(ExecutiveCourseGroup cgroup, Course course, PlanCourse planCourse) {
    return planCommonDao.hasCourse(cgroup, course, planCourse);
  }

  public void setPlanCommonDao(PlanCommonDao planCommonDao) {
    this.planCommonDao = planCommonDao;
  }

  public ExecutivePlan getExecutivePlanByAdminClass(Squad clazz) {
    List<ExecutivePlan> res = entityDao.search(ExecutivePlanQueryBuilder.build(clazz));
    return CollectUtils.isEmpty(res) ? null : res.get(0);
  }

  public List<ExecutivePlanCourse> getPlanCourses(ExecutivePlan plan) {
    if (CollectUtils.isEmpty(plan.getGroups())) {
      return Collections.EMPTY_LIST;
    }
    List<ExecutivePlanCourse> planCourses = new ArrayList<ExecutivePlanCourse>();
    for (Iterator iter = plan.getGroups().iterator(); iter.hasNext(); ) {
      ExecutiveCourseGroup group = (ExecutiveCourseGroup) iter.next();
      planCourses.addAll((List) group.getPlanCourses());
    }
    return planCourses;
  }

}
