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

import java.util.List;

import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.edu.program.app.dao.ExecutivePlanCourseGroupModifyApplyDao;
import org.openurp.edu.program.app.model.FakeCourseGroup;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModifyDetailAfter;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModifyDetailBefore;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModify;
import org.openurp.edu.program.app.service.ExecutivePlanCourseGroupModifyApplyService;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;

public class ExecutivePlanCourseGroupModifyApplyServiceImpl extends BaseServiceImpl implements
    ExecutivePlanCourseGroupModifyApplyService {
  private ExecutivePlanCourseGroupModifyApplyDao executePlanCourseGroupModifyApplyDao;

  public List<ExecutiveCourseGroupModify> appliesOfPlan(Long planId) {
    OqlBuilder<ExecutiveCourseGroupModify> oql = OqlBuilder.from(ExecutiveCourseGroupModify.class, "applyBean");
    oql.where("applyBean.executePlan.id =:planId", planId);
    return entityDao.search(oql);
  }

  public List<ExecutiveCourseGroupModify> myApplies(Long planId, Long userId) {
    OqlBuilder<ExecutiveCourseGroupModify> oql = OqlBuilder.from(ExecutiveCourseGroupModify.class, "apply");
    oql.where("apply.executePlan.id =:planId", planId).where("apply.proposer.id =:userId", userId)
        .orderBy("apply.flag").orderBy(Order.desc("apply.applyDate"));
    return entityDao.search(oql);
  }

  public List<ExecutiveCourseGroup> myReadyModifyApply(Long planId, Long userId) {
    OqlBuilder query = OqlBuilder.from(ExecutiveCourseGroupModify.class.getName() + " apply, "
        + ExecutivePlan.class.getName() + " plan");
    query
        .select("select cgroup")
        .where("apply.executePlan.id = plan.id")
        .join("plan.groups", "cgroup")
        .where("apply.executePlan.id = :planId", planId)
        .where("apply.proposer.id = :userId", userId)
        .where("apply.flag = :flag", ExecutivePlanCourseModify.INITREQUEST)
        .where(
            "exists(select state.id from " + ExecutiveCourseGroupModifyDetailBefore.class.getName() + " state "
                + "where state.apply.id = apply.id and state.courseType=cgroup.courseType)");
    return entityDao.search(query);
  }

  public List<ExecutiveCourseGroupModify> myReadyAddApplies(Long planId, Long userId) {
    OqlBuilder<ExecutiveCourseGroupModify> oql = OqlBuilder.from(ExecutiveCourseGroupModify.class, "apply");
    oql.where("apply.executePlan.id =:planId", planId).where("apply.proposer.id =:userId", userId)
        .where("apply.flag = :flag", ExecutivePlanCourseModify.INITREQUEST)
        .where("apply.requisitionType = :requisitionType", ExecutivePlanCourseModify.ADD)
        .orderBy(Order.desc("apply.applyDate"));
    return entityDao.search(oql);
  }

  /**
   * 保存课程组修改申请的方法
   * [获取修改前课程组的信息]
   */
  public void saveModifyApply(ExecutiveCourseGroupModify modifyBean, Long courseGroupId,
                              ExecutiveCourseGroupModifyDetailAfter after) {
    ExecutiveCourseGroupModifyDetailBefore before = null;
    if (courseGroupId != null) {
      ExecutiveCourseGroup courseGroup = entityDao.get(ExecutiveCourseGroup.class, courseGroupId);
      before = new ExecutiveCourseGroupModifyDetailBefore();
      before.setApply(modifyBean);
      before.setSubCount(courseGroup.getSubCount());
      before.setCourseType(courseGroup.getCourseType());
      before.setCredits(courseGroup.getCredits());
      before.setParent(new FakeCourseGroup((ExecutiveCourseGroup) courseGroup.getParent()));
      before.setRemark(courseGroup.getRemark());
      before.setTermCredits(courseGroup.getTermCredits());
    }
    executePlanCourseGroupModifyApplyDao.saveModifyApply(modifyBean, before, after);
  }

  public void setExecutivePlanCourseGroupModifyApplyDao(
      ExecutivePlanCourseGroupModifyApplyDao executePlanCourseGroupModifyApplyDao) {
    this.executePlanCourseGroupModifyApplyDao = executePlanCourseGroupModifyApplyDao;
  }

}
