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

import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.PlanCourse;

import java.util.List;
import java.util.Map;

/**
 * 培养计划和培养计划的对比服务类<br>
 * 可以对专业培养计划(执行计划), 个人培养计划, 原始计划 这三种计划进行两两之间的对比<br>
 * 因为这三个计划的父类都是ExecutivePlan
 */
public interface PlanCompareService {

  /**
   * 获得两个培养计划的差异
   *
   * @param leftPlan
   * @param rightPlan
   * @return <pre>
   *     CourseType<br>
   *       List<? extends ExecutivePlanCourse>[]<br>
   *         [0]  leftPlan的课程<br>
   *         [1]  rightPlan中的课程
   *         </pre>
   */
  Map<String, List<? extends PlanCourse>[]> diff(CoursePlan leftPlan, CoursePlan rightPlan);

}
