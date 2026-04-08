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
package org.openurp.edu.program.app.service;

import java.util.List;

import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModifyDetailAfter;
import org.openurp.edu.program.model.ExecutiveCourseGroup;

/**
 *
 *
 *
 */
public interface ExecutivePlanCourseGroupModifyApplyService {

  /**
   * 保存课程组修改申请的方法
   * [获取修改前课程组的信息]
   */
  public void saveModifyApply(ExecutiveCourseGroupModify modifyBean, Long courseGroupId,
                              ExecutiveCourseGroupModifyDetailAfter after);

  /**
   * 获得某个用户的培养计划课程组变更申请
   *
   * @param planId
   * @param userId
   * @return
   */
  List<ExecutiveCourseGroupModify> myApplies(Long planId, Long userId);

  /**
   * 获得某个用户的待审核的计划课程组变更申请(不包含添加课程组的变更申请) 中的 课程组
   *
   * @param planId
   * @param userId
   * @return
   */
  List<ExecutiveCourseGroup> myReadyModifyApply(Long planId, Long userId);

  /**
   * 获得某个用户的待审核的计划课程组变更申请(仅包含添加课程组申请) 中的 课程组
   *
   * @param planId
   * @param userId
   * @return
   */
  List<ExecutiveCourseGroupModify> myReadyAddApplies(Long planId, Long userId);

  /**
   * 获得某个培养计划的课程组变更申请
   *
   * @param planId
   * @return
   */
  List<ExecutiveCourseGroupModify> appliesOfPlan(Long planId);

}
