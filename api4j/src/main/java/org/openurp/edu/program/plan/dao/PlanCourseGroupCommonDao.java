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

import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.model.*;

import java.util.List;
import java.util.Set;

public interface PlanCourseGroupCommonDao {

  /**
   * 保存/更新培养计划课程组，但是仅限于课程组的父课程类别没有变动过的情况<br>
   * 会重算课程组和整个计划的学分
   *
   * @param group
   * @see PlanCourseGroupCommonDao.updateGroupTreeCredits
   * @see PlanCommonDao.statPlanCredits
   */
  public void saveOrUpdateCourseGroup(CourseGroup group);

  /**
   * 给培养计划添加一级课程组<br>
   * 会重算课程组和整个计划的学分
   *
   * @param group 必须是一个transient瞬态对象
   * @param plan  必须是一个persistent持久化对象
   * @see PlanCourseGroupCommonDao.updateGroupTreeCredits
   * @see PlanCommonDao.statPlanCredits
   */
  @Deprecated
  public void addCourseGroupToPlan(CourseGroup group, CoursePlan plan);

  /**
   * 给培养计划添加二级/更低级的课程组<br>
   * 会重算课程组和整个计划的学分
   *
   * @param group  必须是一个transient瞬态对象
   * @param parent 必须是一个persistent持久化对象,parent课程组必须已经是plan的子课程组
   * @param plan   必须是一个persistent持久化对象
   * @see PlanCourseGroupCommonDao.updateGroupTreeCredits
   * @see PlanCommonDao.statPlanCredits
   */
  public void addCourseGroupToPlan(CourseGroup group, CourseGroup parent, CoursePlan plan);

  /**
   * 删除一个课程组，不管这个课程组处于第几层<br>
   * 会重算课程组和整个计划的学分
   *
   * @param group
   * @param plan
   * @see PlanCourseGroupCommonDao.updateGroupTreeCredits
   * @see PlanCommonDao.statPlanCredits
   */
  public void removeCourseGroup(CourseGroup group);

  /**
   * 将课程组下移一个位置
   *
   * @param courseGroup
   */
  @Deprecated
  public void updateCourseGroupMoveDown(CourseGroup courseGroup);

  /**
   * 将课程组上移一个位置
   *
   * @param courseGroup
   */
  @Deprecated
  public void updateCourseGroupMoveUp(CourseGroup courseGroup);

  /**
   * 根据课程类别获得一个计划中的课程组
   *
   * @param plan
   * @param planId
   * @param courseTypeId
   * @return
   */
  CourseGroup getCourseGroupByCourseType(CourseGroup planGroup, Long planId, Integer courseTypeId);

  /**
   * 这个方法还未实现，实现起来有难度<br>
   * 将一个group的parent换成别的parent<br>
   * parent可以为null，这样一来就变成了顶级课程组
   *
   * @param group     必须是persisitent对象
   * @param newParent 必须是persisitent对象或者为null
   * @param plan      必须是persisitent对象
   */
  @Deprecated
  void updateCourseGroupParent(CourseGroup group, CourseGroup newParent, CoursePlan plan);

  /**
   * 获得一个课程组内，属于terms学期的课程
   *
   * @param group
   * @param terms
   * @return
   */
  List<Course> extractCourseInCourseGroup(ExecutiveCourseGroup group, String terms);

  /**
   * 获得一个课程组内，属于terms学期的计划课程
   *
   * @param group
   * @param terms
   * @return
   */
  List<ExecutivePlanCourse> extractPlanCourseInCourseGroup(ExecutiveCourseGroup group, Set<String> terms);

  /**
   * 统计从当前课程组往下（包括当前课程组）所有层级的学分要求<br>
   * 采用递归统计，先从最地下开始统计，逐渐往上回溯<br>
   * 统计逻辑为：<br>
   * 如果是必修课程组，则将所有子组学分相加<br>
   * 如果非必修课程组，则维持原学分要求，不改变<br>
   * 统计学分分布<br>
   * 统计课时分布<br>
   *
   * @param group
   */
  void updateGroupTreeCredits(CourseGroup group);

  /**
   * 获得当前课程组的最祖先的节点，有可能直接返回自己
   *
   * @param group
   * @return
   */
  CourseGroup getTopGroup(CourseGroup group);

  CourseGroup copyCourseGroup(CourseGroup sourceCourseGroup, CourseGroup parentAttachTo,
                              CoursePlan planAttachTo, Class<?> groupClazz, Class<?> pcClazz);

  PlanCourse copyPlanCourse(PlanCourse sourcePlanCourse, CourseGroup courseGroupAttachTo, Class<?> pcClazz);
}
