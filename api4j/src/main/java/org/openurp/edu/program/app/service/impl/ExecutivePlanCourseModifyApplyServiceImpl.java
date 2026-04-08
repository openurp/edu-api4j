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
import org.openurp.edu.program.app.dao.ExecutivePlanCourseModifyApplyDao;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModify;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModifyDetailAfter;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModifyDetailBefore;
import org.openurp.edu.program.app.service.ExecutivePlanCourseModifyApplyService;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;

/**
 *
 */
public class ExecutivePlanCourseModifyApplyServiceImpl extends BaseServiceImpl implements
    ExecutivePlanCourseModifyApplyService {

  private ExecutivePlanCourseModifyApplyDao executePlanCourseModifyApplyDao;

  public void saveModifyApply(ExecutivePlanCourseModify apply, ExecutivePlanCourseModifyDetailBefore before,
                              ExecutivePlanCourseModifyDetailAfter after) {
    executePlanCourseModifyApplyDao.saveModifyApply(apply, before, after);
  }

  public void setExecutivePlanCourseModifyApplyDao(ExecutivePlanCourseModifyApplyDao executePlanCourseModifyApplyDao) {
    this.executePlanCourseModifyApplyDao = executePlanCourseModifyApplyDao;
  }

  public List<ExecutivePlanCourseModify> myApplies(Long planId, Long userId) {

    OqlBuilder<ExecutivePlanCourseModify> query = OqlBuilder.from(ExecutivePlanCourseModify.class, "apply");
    query.where("apply.executePlan.id = :planId", planId).where("apply.proposer.id = :userId", userId)
        .orderBy("apply.flag").orderBy(Order.desc("apply.applyDate"));
    return entityDao.search(query);
  }

  public List<ExecutivePlanCourseModify> myReadyAddApplies(Long planId, Long userId) {
    OqlBuilder<ExecutivePlanCourseModify> query = OqlBuilder.from(ExecutivePlanCourseModify.class, "apply");
    query.where("apply.executePlan.id = :planId", planId).where("apply.proposer.id = :userId", userId)
        .where("apply.flag = :flag", ExecutivePlanCourseModify.INITREQUEST)
        .where("apply.requisitionType = :requisitionType", ExecutivePlanCourseModify.ADD)
        .orderBy(Order.desc("apply.applyDate"));
    return entityDao.search(query);
  }

  public List<ExecutivePlanCourseModify> appliesOfPlan(Long planId) {
    OqlBuilder<ExecutivePlanCourseModify> query = OqlBuilder.from(ExecutivePlanCourseModify.class, "apply");
    query.where("apply.executePlan.id = :planId", planId).orderBy("apply.flag")
        .orderBy(Order.desc("apply.applyDate")).limit(null);
    return entityDao.search(query);
  }

  public List<ExecutivePlanCourse> myReadyModifyApply(Long planId, Long userId) {
    OqlBuilder query = OqlBuilder.from(ExecutivePlanCourseModify.class.getName() + " apply, "
        + ExecutivePlan.class.getName() + " plan");
    query
        .select("select pcourse")
        .where("apply.executePlan.id = plan.id")
        .join("plan.groups", "cgroup")
        .join("cgroup.planCourses", "pcourse")
        .where("apply.executePlan.id = :planId", planId)
        .where("apply.proposer.id = :userId", userId)
        .where("apply.flag = :flag", ExecutivePlanCourseModify.INITREQUEST)
        .where(
            "exists(select state.id from " + ExecutivePlanCourseModifyDetailBefore.class.getName()
                + " state where state.apply.id = apply.id and state.course=pcourse.course)");
    return entityDao.search(query);
  }

}
