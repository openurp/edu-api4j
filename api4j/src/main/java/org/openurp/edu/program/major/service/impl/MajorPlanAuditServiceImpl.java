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
package org.openurp.edu.program.major.service.impl;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.AuditStatus;
import org.openurp.edu.program.major.service.MajorPlanAuditService;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;

import java.util.List;

public class MajorPlanAuditServiceImpl extends BaseServiceImpl implements MajorPlanAuditService {

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void audit(List<MajorPlan> plans, AuditStatus status) {
    for (MajorPlan plan : plans) {
      if (canTransferTo(plan.getProgram().getStatus(), status)) {
        plan.getProgram().setStatus(status);
        entityDao.saveOrUpdate(plan.getProgram(), plan);
        Program program = plan.getProgram();
        OqlBuilder<ExecutivePlan> q = OqlBuilder.from(ExecutivePlan.class, "ep");
        q.where("ep.program=:program", program);
        List<ExecutivePlan> eps = entityDao.search(q);

        if (status.equals(AuditStatus.ACCEPTED)) {
          if (eps.isEmpty()) {
            ExecutivePlan ep = new ExecutivePlan();
            ep.setProgram(program);
            ep.setUpdatedAt(new java.util.Date());
            ep.setDepartment(program.getDepartment());
            ep.setCredits(plan.getCredits());
            ep.setHourRatios(plan.getHourRatios());
            ep.setCreditHours(plan.getCreditHours());
            entityDao.saveOrUpdate(ep);
            for (CourseGroup cg : plan.getGroups()) {
              if (cg.getParent() == null) {
                planCourseGroupCommonDao.copyCourseGroup(cg, null, ep, ExecutiveCourseGroup.class, ExecutivePlanCourse.class);
              }
            }
          }
        }
      }
    }
  }

  public void revokeAccepted(List<MajorPlan> plans) {
    for (MajorPlan plan : plans) {
      if (canTransferTo(plan.getProgram().getStatus(), AuditStatus.REJECTED)) {
        if (plan.getProgram().getStatus() == AuditStatus.ACCEPTED) {
          plan.getProgram().setStatus(AuditStatus.REJECTED);
          entityDao.saveOrUpdate(plan);
        }
      }
    }
  }

  public void submit(List<MajorPlan> plans) {
    for (MajorPlan plan : plans) {
      if (canTransferTo(plan.getProgram().getStatus(), AuditStatus.SUBMITTED)) {
        plan.getProgram().setStatus(AuditStatus.SUBMITTED);
      }
    }
    entityDao.saveOrUpdate(plans);
  }

  public void revokeSubmitted(List<Program> plans) {
    for (Program program : plans) {
      if (canTransferTo(program.getStatus(), AuditStatus.UNSUBMITTED)) {
        program.setStatus(AuditStatus.UNSUBMITTED);
      }
    }
    entityDao.saveOrUpdate(plans);
  }

  /**
   * 是否能从一个状态转换到另一个状态
   *
   * @param from
   * @param to
   * @return
   */
  private boolean canTransferTo(AuditStatus from, AuditStatus to) {
    // FIXME
    switch (from) {
      case UNSUBMITTED:
        if (to == AuditStatus.SUBMITTED) {
          return true;
        } else {
          return false;
        }
      case SUBMITTED:
        if (to == AuditStatus.ACCEPTED || to == AuditStatus.REJECTED || to == AuditStatus.UNSUBMITTED) {
          return true;
        } else {
          return false;
        }
      case REJECTED:
        if (to == AuditStatus.SUBMITTED || to == AuditStatus.ACCEPTED) {
          return true;
        } else {
          return false;
        }
      case ACCEPTED:
        if (to == AuditStatus.REJECTED) {
          return true;
        } else {
          return false;
        }
      default:
        return false;
    }
  }

  public MajorPlan getMajorMajorPlan(Long majorPlanId) {
    OqlBuilder<MajorPlan> query = OqlBuilder.from(MajorPlan.class, "plan");
    query.where(
        "plan.program.id=(select mp.program from org.openurp.edu.program.model.MajorPlan mp where mp.id = :mplanid)",
        majorPlanId);
    List<MajorPlan> majorPlans = entityDao.search(query);
    if (majorPlans == null
        || majorPlans.size() == 0) {
      throw new RuntimeException("Cannot find Major Plan");
    }
    if (majorPlans.size() > 1) {
      throw new RuntimeException("Error More than one Major Plan found");
    }
    return majorPlans.get(0);
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }
}
