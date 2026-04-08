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
package org.openurp.edu.program.plan.dao;

import org.openurp.code.edu.model.CourseType;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.model.*;

import java.util.List;
import java.util.Set;

public interface PlanCommonDao {

  /**
   * 删除一个计划，这个计划可能是专业计划也可能是个人计划<br>
   *
   * @param plan
   */
  public void removePlan(CoursePlan plan);

  /**
   * 保存或更新计划<br>
   * 调用saveSetting
   *
   * @param plan
   */
  public void saveOrUpdatePlan(CoursePlan plan);

  /**
   * 统计计划的总学分，总学分的值来自于顶级课程组的学分的累加，不保存，不采用递归统计<br>
   * 不递归统计的原因是，程序完全信赖计划中各个层级的课程组的学分要求的正确性
   *
   * @param plan
   * @return
   * @see PlanCourseGroupCommonDao.updateGroupTreeCredits
   */
  public float statPlanCredits(CoursePlan plan);

  public boolean hasCourse(CourseGroup cgroup, Course course);

  public Set<String> getUsedCourseTypeNames(CoursePlan plan);

  public Set<String> getUnusedCourseTypeNames(CoursePlan plan);

  public List<CourseType> getUnusedCourseTypes(CoursePlan plan);

  public List<Program> getDuplicatePrograms(Program program);

  public boolean isDuplicate(Program program);

  /**
   * 查找对于固定学期培养计划中要求的学分值.
   *
   * @param plan
   * @param term [1..maxTerm]
   * @return
   */
  public Float getCreditByTerm(ExecutivePlan plan, int term);

  public boolean hasCourse(CourseGroup cgroup, Course course, PlanCourse planCourse);
}
