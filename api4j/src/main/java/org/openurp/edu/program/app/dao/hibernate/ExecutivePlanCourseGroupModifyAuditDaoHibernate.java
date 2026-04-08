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
package org.openurp.edu.program.app.dao.hibernate;

import java.util.Date;

import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.model.User;
import org.openurp.edu.program.app.dao.ExecutivePlanCourseGroupModifyAuditDao;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.plan.service.ExecutivePlanAuditException;

public class ExecutivePlanCourseGroupModifyAuditDaoHibernate extends HibernateEntityDao
    implements ExecutivePlanCourseGroupModifyAuditDao {

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void rejected(ExecutiveCourseGroupModify apply, User assessor) {
    apply.setAssessor(assessor);
    apply.setFlag(ExecutiveCourseGroupModify.REFUSE);
    apply.setReplyDate(new Date());
    saveOrUpdate(apply);
  }

  public void approved(ExecutiveCourseGroupModify apply, User assessor) throws ExecutivePlanAuditException {
    ExecutivePlan plan = get(ExecutivePlan.class, apply.getExecutivePlan().getId());
    if (plan == null) { throw new ExecutivePlanAuditException("您要修改的专业培养计划已经不存在。"); }
    ExecutiveCourseGroup parent = null;
    if (apply.getNewPlanCourseGroup() != null && apply.getNewPlanCourseGroup().getParent() != null) {
      parent = get(ExecutiveCourseGroup.class, apply.getNewPlanCourseGroup().getParent().getId());
      if (null == parent) { throw new ExecutivePlanAuditException("父课程组已不存在"); }
    }

    ExecutiveCourseGroup courseGroup = new ExecutiveCourseGroup();

    if (ExecutiveCourseGroupModify.DELETE.equals(apply.getRequisitionType())) {
      // 删除
      Integer typeId = apply.getOldPlanCourseGroup().getCourseType().getId();
      ExecutiveCourseGroup group = (ExecutiveCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (group != null) {
        planCourseGroupCommonDao.removeCourseGroup(group);
      } else {
        throw new ExecutivePlanAuditException(
            "课程组不存在:" + apply.getOldPlanCourseGroup().getCourseType().getName());
      }
    } else if (ExecutiveCourseGroupModify.ADD.equals(apply.getRequisitionType())) {
      // 添加
      Integer typeId = apply.getNewPlanCourseGroup().getCourseType().getId();
      ExecutiveCourseGroup group = (ExecutiveCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (group != null) { throw new ExecutivePlanAuditException(
          "课程组已存在:" + apply.getNewPlanCourseGroup().getCourseType().getName()); }
      group = new ExecutiveCourseGroup();
      group.setPlan(plan);
      group.setCourseType(apply.getNewPlanCourseGroup().getCourseType());
      group.setTermCredits(apply.getNewPlanCourseGroup().getTermCredits());
      group.setCredits(apply.getNewPlanCourseGroup().getCredits());
      group.setSubCount(apply.getNewPlanCourseGroup().getSubCount());
      group.setRemark(apply.getNewPlanCourseGroup().getRemark());

      group.setIndexno("--");
      planCourseGroupCommonDao.addCourseGroupToPlan(group, parent, plan);

    } else if (ExecutiveCourseGroupModify.MODIFY.equals(apply.getRequisitionType())) {
      // 变动
      // 课程组变更申请不会更改一个课程组的父课程组，因为技术上有难度
      Integer typeId = apply.getOldPlanCourseGroup().getCourseType().getId();
      ExecutiveCourseGroup oldGroup = (ExecutiveCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (oldGroup == null) { throw new ExecutivePlanAuditException(
          "课程组不存在:" + apply.getOldPlanCourseGroup().getCourseType().getName()); }
      planCourseGroupCommonDao.updateCourseGroupParent(oldGroup, parent, plan);

      oldGroup.setCourseType(apply.getNewPlanCourseGroup().getCourseType());
      oldGroup.setTermCredits(apply.getNewPlanCourseGroup().getTermCredits());
      oldGroup.setCredits(apply.getNewPlanCourseGroup().getCredits());
      oldGroup.setSubCount(apply.getNewPlanCourseGroup().getSubCount());
      oldGroup.setRemark(apply.getNewPlanCourseGroup().getRemark());

      planCourseGroupCommonDao.saveOrUpdateCourseGroup(oldGroup);
      saveOrUpdate(plan);
    }

    apply.setAssessor(assessor);
    apply.setFlag(ExecutiveCourseGroupModify.ACCEPT);
    apply.setReplyDate(new Date());
    saveOrUpdate(apply);
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

}
