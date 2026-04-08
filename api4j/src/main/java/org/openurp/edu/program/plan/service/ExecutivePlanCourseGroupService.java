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

import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.ExecutiveCourseGroup;

/**
 * 培养计划课程组实现类 所有对专业培养计划的操作()学生的
 */
public interface ExecutivePlanCourseGroupService {

  /**
   * 保存新建的课程组/更新已有的课程组
   *
   * @param group
   *          必须是持久态的
   */
  void saveOrUpdateCourseGroup(ExecutiveCourseGroup group);

  /**
   * @param groupId
   * @param planId
   */
  void removeCourseGroup(Long groupId);

  /**
   * 从培养计划中删除该组与该组的关联<br>
   * 如果没有培养计划关联该组,删除该组.<br>
   * 课程组和plan必须是persisitent持久态的<br>
   * 会重算计划的学分，和课程组树的学分要求
   *
   * @param group
   */
  void removeCourseGroup(ExecutiveCourseGroup group);

  /**
   * 将课程组上移一个位置
   *
   * @param courseGroup
   */
  @Deprecated
  void courseGroupMoveUp(ExecutiveCourseGroup courseGroup);

  /**
   * 将课程组下移一个位置
   *
   * @param courseGroup
   */
  @Deprecated
  void courseGroupMoveDown(ExecutiveCourseGroup courseGroup);

  void move(CourseGroup node, CourseGroup location, int index);

  boolean hasSameGroupInOneLevel(CourseGroup courseGroup, CoursePlan plan, CourseGroup parent);
}
