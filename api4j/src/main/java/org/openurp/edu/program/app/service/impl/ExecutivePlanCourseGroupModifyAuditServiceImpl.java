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
import org.openurp.edu.program.app.dao.ExecutivePlanCourseGroupModifyAuditDao;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.app.service.ExecutivePlanCourseGroupModifyAuditService;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.plan.service.ExecutivePlanAuditException;
import org.openurp.edu.program.plan.service.ExecutivePlanCourseGroupService;

public class ExecutivePlanCourseGroupModifyAuditServiceImpl extends BaseServiceImpl
    implements ExecutivePlanCourseGroupModifyAuditService {

  private ExecutivePlanCourseGroupModifyAuditDao executePlanCourseGroupModifyAuditDao;

  protected ExecutivePlanCourseGroupService executePlanCourseGroupService;

  protected PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void approved(ExecutiveCourseGroupModify apply, User assessor) throws ExecutivePlanAuditException {
    executePlanCourseGroupModifyAuditDao.approved(apply, assessor);

    /*
     * 补丁代码
     * 解决添加课程组时设置indexno的，太丑陋了，需要重构executePlanCourseGroupModifyAuditDao
     * 可能变成service
     */
    if (ExecutiveCourseGroupModify.ADD.equals(apply.getRequisitionType())) {
      ExecutiveCourseGroup parent = null;
      ExecutivePlan plan = entityDao.get(ExecutivePlan.class, apply.getExecutivePlan().getId());
      if (apply.getNewPlanCourseGroup() != null && apply.getNewPlanCourseGroup().getParent() != null) {
        parent = entityDao.get(ExecutiveCourseGroup.class, apply.getNewPlanCourseGroup().getParent().getId());
      }
      Integer typeId = apply.getNewPlanCourseGroup().getCourseType().getId();
      ExecutiveCourseGroup group = (ExecutiveCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(new ExecutiveCourseGroup(), plan.getId(), typeId);
      if (ExecutiveCourseGroupModify.ADD.equals(apply.getRequisitionType())) {
        int indexno = 0;
        if (parent != null) {
          indexno = parent.getChildren().size() + 1;
        } else {
          indexno = plan.getTopCourseGroups().size() + 1;
        }
        executePlanCourseGroupService.move(group, parent, indexno);
      }
    }
  }

  public void rejected(ExecutiveCourseGroupModify apply, User assessor) {
    executePlanCourseGroupModifyAuditDao.rejected(apply, assessor);
  }

  public void setExecutivePlanCourseGroupModifyAuditDao(
      ExecutivePlanCourseGroupModifyAuditDao executePlanCourseGroupModifyAuditDao) {
    this.executePlanCourseGroupModifyAuditDao = executePlanCourseGroupModifyAuditDao;
  }

  public void setExecutivePlanCourseGroupService(ExecutivePlanCourseGroupService executePlanCourseGroupService) {
    this.executePlanCourseGroupService = executePlanCourseGroupService;
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

}
