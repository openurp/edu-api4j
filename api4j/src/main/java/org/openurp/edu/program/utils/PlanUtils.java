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
package org.openurp.edu.program.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.openurp.base.time.Terms;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.model.PlanCourse;

public class PlanUtils {

  /**
   * 获得一个专业培养计划中，在terms学期上课的Course
   *
   * @param plan
   * @param term
   *          要查询的学期，多个学期用逗号分割比如1,2,3
   * @return
   */
  public static List<Course> getCourses(ExecutivePlan plan, int term) {
    Set<Course> courses = new HashSet<Course>();

    for (ExecutivePlanCourse planCourse : getPlanCourses(plan)) {
      if (isUnplannedTerm(planCourse.getTerms())) {
        continue;
      }
      if (planCourse.getTerms().contains(term)) {
        courses.add(planCourse.getCourse());
      }
    }
    return new ArrayList<Course>(courses);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static List<ExecutivePlanCourse> getPlanCourses(ExecutivePlan plan) {
    if (CollectUtils.isEmpty(plan.getGroups())) { return CollectUtils.newArrayList(); }
    List<ExecutivePlanCourse> planCourses = new ArrayList<ExecutivePlanCourse>();
    for (CourseGroup courseGroup : plan.getGroups()) {
      if (null != courseGroup) {
        planCourses.addAll((List) courseGroup.getPlanCourses());
      }
    }
    return planCourses;
  }

  public static List<ExecutivePlanCourse> getUnPlannedPlanCourses(ExecutivePlan plan) {
    if (CollectUtils.isEmpty(plan.getGroups())) { return CollectUtils.newArrayList(); }
    List<ExecutivePlanCourse> planCourses = new ArrayList<ExecutivePlanCourse>();
    for (CourseGroup courseGroup : plan.getGroups()) {
      if (courseGroup.getPlanCourses() != null && courseGroup.getPlanCourses().size() > 0) {
        for (PlanCourse pcourse : courseGroup.getPlanCourses()) {
          if (isUnplannedTerm(pcourse.getTerms())) {
            planCourses.add((ExecutivePlanCourse) pcourse);
          }
        }
      }
    }
    return planCourses;
  }

  /**
   * 根据课程找出课程类型
   *
   * @param cb
   * @return
   */
  public static List<ExecutivePlanCourse> getPlannedCourse(ExecutivePlan plan) {
    if (CollectUtils.isEmpty(plan.getGroups())) { return CollectUtils.newArrayList(); }
    List<ExecutivePlanCourse> planCourses = new ArrayList<ExecutivePlanCourse>();
    for (CourseGroup courseGroup : plan.getGroups()) {
      if (courseGroup.getPlanCourses() != null && courseGroup.getPlanCourses().size() > 0) {
        for (PlanCourse pcourse : courseGroup.getPlanCourses()) {
          if (isUnplannedTerm(pcourse.getTerms())) {
            planCourses.add((ExecutivePlanCourse) pcourse);
          }
        }
      }
    }
    return planCourses;
  }

  /**
   * 确定是不是不定开课学期的
   *
   * @param term
   * @return
   */
  public static boolean isUnplannedTerm(Terms terms) {
    return null == terms || terms.value <= 1;
  }

  public static float getGroupCredits(CourseGroup group, int term) {
    String[] terms = group.getTermCredits().replaceAll("^,", "").replaceAll(",$", "").split(",");
    if (term > terms.length || term < 1) { return 0f; }
    return Float.valueOf(terms[term - 1]);
  }

  /**
   * 获得一个专业培养计划中，在term学期上课的ExecutePlanCourse
   *
   * @param plan
   * @param terms
   *          要查询的学期，多个学期用逗号分割比如1,2,3
   * @return
   */
  public static List<ExecutivePlanCourse> getPlanCourses(ExecutivePlan plan, int term) {
    List<ExecutivePlanCourse> planCourses = new ArrayList<ExecutivePlanCourse>();

    for (ExecutivePlanCourse planCourse : getPlanCourses(plan)) {
      if (openOnThisTerm(planCourse.getTerms(), term)) {
        planCourses.add(planCourse);
      }
    }
    return planCourses;
  }

  /**
   * 获得一个课程组中，在term学期上课的ExecutePlanCourse
   *
   * @param courseGroup
   * @param term
   * @return
   */
  public static List<PlanCourse> getPlanCourses(CourseGroup group, int term) {
    List<PlanCourse> result = new ArrayList<PlanCourse>();
    for (PlanCourse pCourse : group.getPlanCourses()) {
      if (openOnThisTerm(pCourse.getTerms(), term)) {
        result.add(pCourse);
      }
    }
    return result;
  }

  /**
   * 根据开课学期字符串来判断是否在这个学期开课
   *
   * @param terms
   *          开课学期字符串
   * @param term
   *          某个学期
   * @return
   */
  public static boolean openOnThisTerm(Terms terms, int term) {
    return terms.contains(term);
  }

  /**
   * 获得一个课程组中，到term为止需要上的所有PlanCourse
   *
   * @param courseGroup
   * @param term
   * @return
   */
  public static List<PlanCourse> getPlanCoursesUntilTerm(CourseGroup courseGroup, int term) {
    Set<PlanCourse> result = CollectUtils.newHashSet();
    for (int i = 1; i <= term; i++) {
      result.addAll(getPlanCourses(courseGroup, i));
    }
    return new ArrayList<PlanCourse>(result);
  }
}
