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
package org.openurp.edu.grade.transcript.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.openurp.base.std.model.Student;
import org.openurp.edu.grade.transcript.service.TranscriptDataProvider;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.PlanCourse;
import org.openurp.edu.program.plan.service.CoursePlanProvider;

/*
 *
 */
public class TranscriptPlanCourse extends BaseServiceImpl implements TranscriptDataProvider {
  private CoursePlanProvider coursePlanProvider;

  public String getDataName() {
    return "planCourses";
  }

  @SuppressWarnings("unchecked")
  public Object getDatas(List<Student> stds, Map<String, String> options) {
    Map<Student, Object> datas = CollectUtils.newHashMap();
    for (Student std : stds) {
      datas.put(std, getPlanCourses(std));
    }
    return datas;
  }

  private List<PlanCourse> getPlanCourses(Student std) {
    // 找个人计划
    // 没有的话 找专业计划
    List<PlanCourse> planCourses = new ArrayList<PlanCourse>();
    // StdPlan personalPlan = coursePlanProvider.getPersonalPlan(std);
    // List<CourseGroup> courseGroups = personalPlan.getGroups();
    // for(CourseGroup courseGroup : courseGroups){
    // planCourses.addAll(courseGroup.getPlanCourses());
    // }
    // if(personalPlan == null){
    CoursePlan coursePlan = coursePlanProvider.getExecutivePlan(std);
    if (coursePlan != null) {
      for (CourseGroup courseGroup : coursePlan.getGroups()) {
        planCourses.addAll(courseGroup.getPlanCourses());
        // }
      }
    }
    return planCourses;
  }

  public void setCoursePlanProvider(CoursePlanProvider coursePlanProvider) {
    this.coursePlanProvider = coursePlanProvider;
  }

}
