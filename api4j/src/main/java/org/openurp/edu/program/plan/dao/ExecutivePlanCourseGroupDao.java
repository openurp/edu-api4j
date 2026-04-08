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

/**
 * 培养计划课程组信息存取类
 */
public interface ExecutivePlanCourseGroupDao {

  /**
   * 返回指定计划中，指定课程的课程类别
   *
   * @param planId
   * @param courseId
   * @return 不存在该课程，则返回null
   */
  public CourseType getCourseType(Long planId, Long courseId);

}
