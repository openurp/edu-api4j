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
package org.openurp.edu.program.app.service.impl;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.openurp.base.model.User;
import org.openurp.edu.program.app.dao.ExecutivePlanCourseModifyAuditDao;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModify;
import org.openurp.edu.program.app.service.ExecutivePlanCourseModifyAuditService;
import org.openurp.edu.program.plan.service.ExecutivePlanAuditException;

public class ExecutivePlanCourseModifyAuditServiceImpl extends BaseServiceImpl
    implements ExecutivePlanCourseModifyAuditService {

  private ExecutivePlanCourseModifyAuditDao executePlanCourseModifyAuditDao;

  public void approved(ExecutivePlanCourseModify apply, User assessor) throws ExecutivePlanAuditException {
    executePlanCourseModifyAuditDao.approved(apply, assessor);
  }

  public void rejected(ExecutivePlanCourseModify apply, User assessor) throws ExecutivePlanAuditException {
    executePlanCourseModifyAuditDao.rejected(apply, assessor);
  }

  public void setExecutivePlanCourseModifyAuditDao(ExecutivePlanCourseModifyAuditDao executePlanCourseModifyAuditDao) {
    this.executePlanCourseModifyAuditDao = executePlanCourseModifyAuditDao;
  }

}
