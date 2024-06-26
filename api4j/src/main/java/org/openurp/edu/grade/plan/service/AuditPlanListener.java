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
package org.openurp.edu.grade.plan.service;

import org.openurp.edu.grade.plan.model.AuditGroupResult;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.PlanCourse;

/**
 * 审核监听器
 */
public interface AuditPlanListener {

  /**
   * 开始审核计划
   *
   * @param context
   * @return false 表示不能继续审核
   */
  public boolean startPlanAudit(AuditPlanContext context);

  /**
   * 开始审核课程组
   *
   * @param context
   * @param courseGroup
   *          TODO
   * @param groupResult
   * @return false 表示不能继续审核
   */
  public boolean startGroupAudit(AuditPlanContext context, CourseGroup courseGroup,
                                 AuditGroupResult groupResult);

  /**
   * 开始审核课程
   *
   * @param context
   * @param planCourse TODO
   * @return false 表示不能继续审核
   */
  public boolean startCourseAudit(AuditPlanContext context, AuditGroupResult groupResult,
                                  PlanCourse planCourse);

  /**
   * 结束审核计划
   *
   * @param context
   */
  public void endPlanAudit(AuditPlanContext context);
}
