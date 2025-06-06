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
package org.openurp.edu.program.plan.service;

import org.openurp.base.edu.model.Semester;
import org.openurp.base.std.model.Student;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.PlanCourse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 培养计划提供者
 */
public interface CoursePlanProvider {

  /**
   * 获得专业培养计划
   *
   * @param student
   * @return
   */
  public ExecutivePlan getExecutivePlan(Student student);

  /**
   * 获得学生的计划
   *
   * @param std
   * @return
   */
  CoursePlan getCoursePlan(Student std);

  /**
   * 获得一批学生的和计划的键值对映射关系<br>
   * 可能这个方法以后就不用了，因为Program和学生的关系不一样了
   *
   * @param students
   * @return
   */
  Map<Student, CoursePlan> getCoursePlans(Collection<Student> students);

  /**
   * 获得学生的计划课程
   *
   * @param student
   * @return
   */
  public List<PlanCourse> getPlanCourses(Student student);

  /**
   * 根据培养计划课程所在学期获得对应的学年学期
   *
   * @param planCourse
   * @return
   */
  public List<Semester> getSemesterByPlanCourse(PlanCourse planCourse);
}
