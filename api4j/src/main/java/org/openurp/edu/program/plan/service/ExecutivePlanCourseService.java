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

import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;

/**
 */
public interface ExecutivePlanCourseService {

  /**
   * 删除培养计划中的课程<br>
   * 如果所在组是必修，并且自己是必修则<br>
   * 1.重算课程组学分<br>
   * 2.重算完课程组学分后，级联重算整个父课程组树学分<br>
   * 3.重算计划的总学分<br>
   * 如果组非必修，或自己不是必修则不重算课程组学分<br>
   *
   * @param planCourse
   * @param plan
   */
  public void removePlanCourse(ExecutivePlanCourse planCourse, ExecutivePlan plan);

  /**
   * 添加培养计划中的课程<br>
   * 如果所在组是必修，并且自己是必修则<br>
   * 1.重算课程组学分<br>
   * 2.重算完课程组学分后，级联重算整个父课程组树学分<br>
   * 3.重算计划的总学分<br>
   * 如果组非必修，或自己不是必修则不重算课程组学分<br>
   *
   * @param planCourse
   * @param plan
   */
  public void addPlanCourse(ExecutivePlanCourse planCourse, ExecutivePlan plan);

  /**
   * 更新培养计划中的课程<br>
   * 如果所在组是必修，并且自己是必修则<br>
   * 1.重算课程组学分<br>
   * 2.重算完课程组学分后，级联重算整个父课程组树学分<br>
   * 3.重算计划的总学分<br>
   * 如果组非必修，或自己不是必修则不重算课程组学分<br>
   *
   * @param planCourse
   * @param plan
   */
  public void updatePlanCourse(ExecutivePlanCourse planCourse, ExecutivePlan plan);

}
