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

import org.openurp.base.model.User;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.plan.service.ExecutivePlanAuditException;

/**
 * 计划课程组变更审核服务类
 */
public interface ExecutivePlanCourseGroupModifyAuditService {

  /**
   * 通过一个培养计划课程组变更申请
   *
   * @param apply
   * @param assessor
   * @throws ExecutivePlanAuditException
   */
  void approved(ExecutiveCourseGroupModify apply, User assessor) throws ExecutivePlanAuditException;

  /**
   * 拒绝一个培养计划课程组变更申请
   *
   * @param apply
   * @param assessor
   */
  void rejected(ExecutiveCourseGroupModify apply, User assessor);

}
