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
import org.openurp.edu.program.app.dao.ExecutionPlanCourseGroupModifyAuditDao;
import org.openurp.edu.program.app.model.ExecutionCourseGroupModify;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.ExecutionCourseGroup;
import org.openurp.edu.program.model.ExecutionPlan;
import org.openurp.edu.program.plan.service.ExecutionPlanAuditException;

public class ExecutionPlanCourseGroupModifyAuditDaoHibernate extends HibernateEntityDao
    implements ExecutionPlanCourseGroupModifyAuditDao {

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void rejected(ExecutionCourseGroupModify apply, User assessor) {
    apply.setAssessor(assessor);
    apply.setFlag(ExecutionCourseGroupModify.REFUSE);
    apply.setReplyDate(new Date());
    saveOrUpdate(apply);
  }

  public void approved(ExecutionCourseGroupModify apply, User assessor) throws ExecutionPlanAuditException {
    ExecutionPlan plan = get(ExecutionPlan.class, apply.getExecutionPlan().getId());
    if (plan == null) { throw new ExecutionPlanAuditException("您要修改的专业培养计划已经不存在。"); }
    ExecutionCourseGroup parent = null;
    if (apply.getNewPlanCourseGroup() != null && apply.getNewPlanCourseGroup().getParent() != null) {
      parent = get(ExecutionCourseGroup.class, apply.getNewPlanCourseGroup().getParent().getId());
      if (null == parent) { throw new ExecutionPlanAuditException("父课程组已不存在"); }
    }

    ExecutionCourseGroup courseGroup = new ExecutionCourseGroup();

    if (ExecutionCourseGroupModify.DELETE.equals(apply.getRequisitionType())) {
      // 删除
      Integer typeId = apply.getOldPlanCourseGroup().getCourseType().getId();
      ExecutionCourseGroup group = (ExecutionCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (group != null) {
        planCourseGroupCommonDao.removeCourseGroup(group);
      } else {
        throw new ExecutionPlanAuditException(
            "课程组不存在:" + apply.getOldPlanCourseGroup().getCourseType().getName());
      }
    } else if (ExecutionCourseGroupModify.ADD.equals(apply.getRequisitionType())) {
      // 添加
      Integer typeId = apply.getNewPlanCourseGroup().getCourseType().getId();
      ExecutionCourseGroup group = (ExecutionCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (group != null) { throw new ExecutionPlanAuditException(
          "课程组已存在:" + apply.getNewPlanCourseGroup().getCourseType().getName()); }
      group = new ExecutionCourseGroup();
      group.setPlan(plan);
      group.setCourseType(apply.getNewPlanCourseGroup().getCourseType());
      group.setCourseCount(apply.getNewPlanCourseGroup().getCourseCount());
      group.setTermCredits(apply.getNewPlanCourseGroup().getTermCredits());
      group.setCredits(apply.getNewPlanCourseGroup().getCredits());
      group.setSubCount(apply.getNewPlanCourseGroup().getSubCount());
      group.setRemark(apply.getNewPlanCourseGroup().getRemark());

      group.setIndexno("--");
      planCourseGroupCommonDao.addCourseGroupToPlan(group, parent, plan);

    } else if (ExecutionCourseGroupModify.MODIFY.equals(apply.getRequisitionType())) {
      // 变动
      // 课程组变更申请不会更改一个课程组的父课程组，因为技术上有难度
      Integer typeId = apply.getOldPlanCourseGroup().getCourseType().getId();
      ExecutionCourseGroup oldGroup = (ExecutionCourseGroup) planCourseGroupCommonDao
          .getCourseGroupByCourseType(courseGroup, plan.getId(), typeId);
      if (oldGroup == null) { throw new ExecutionPlanAuditException(
          "课程组不存在:" + apply.getOldPlanCourseGroup().getCourseType().getName()); }
      planCourseGroupCommonDao.updateCourseGroupParent(oldGroup, parent, plan);

      oldGroup.setCourseType(apply.getNewPlanCourseGroup().getCourseType());
      oldGroup.setCourseCount(apply.getNewPlanCourseGroup().getCourseCount());
      oldGroup.setTermCredits(apply.getNewPlanCourseGroup().getTermCredits());
      oldGroup.setCredits(apply.getNewPlanCourseGroup().getCredits());
      oldGroup.setSubCount(apply.getNewPlanCourseGroup().getSubCount());
      oldGroup.setRemark(apply.getNewPlanCourseGroup().getRemark());

      planCourseGroupCommonDao.saveOrUpdateCourseGroup(oldGroup);
      saveOrUpdate(plan);
    }

    apply.setAssessor(assessor);
    apply.setFlag(ExecutionCourseGroupModify.ACCEPT);
    apply.setReplyDate(new Date());
    saveOrUpdate(apply);
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

}
