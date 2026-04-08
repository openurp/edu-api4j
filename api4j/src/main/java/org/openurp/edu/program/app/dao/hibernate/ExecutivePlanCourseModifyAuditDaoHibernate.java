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

import java.sql.Date;

import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.model.User;
import org.openurp.base.time.Terms;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.app.dao.ExecutivePlanCourseModifyAuditDao;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModify;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModifyDetail;
import org.openurp.edu.program.app.model.ExecutivePlanCourseModifyDetailAfter;
import org.openurp.edu.program.plan.dao.PlanCourseCommonDao;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.plan.service.ExecutivePlanAuditException;

public class ExecutivePlanCourseModifyAuditDaoHibernate extends HibernateEntityDao
    implements ExecutivePlanCourseModifyAuditDao {

  private PlanCourseCommonDao planCourseCommonDao;

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void approved(ExecutivePlanCourseModify apply, User assessor) throws ExecutivePlanAuditException {
    // 只能对待审核的申请进行审核
    if (!ExecutivePlanCourseModify.INITREQUEST
        .equals(apply.getFlag())) { throw new ExecutivePlanAuditException("只能对待审核的申请进行审核"); }

    ExecutivePlan plan = get(ExecutivePlan.class, apply.getExecutivePlan().getId());
    if (plan == null) { throw new ExecutivePlanAuditException("您要修改的专业培养计划已经不存在。"); }
    if (ExecutivePlanCourseModify.ADD.equals(apply.getRequisitionType())) {
      ExecutivePlanCourseModifyDetail after = apply.getNewPlanCourse();
      ExecutivePlanCourse planCourse = new ExecutivePlanCourse();
      planCourse.setCourse(after.getCourse());
      planCourse.setTerms(after.getTerms());
      if (planCourse.getTerms() == null) {
        planCourse.setTerms(new Terms(0));
      }
      planCourse.setRemark(after.getRemark());
      planCourse.setCompulsory(after.isCompulsory());
      ExecutiveCourseGroup mg = get(ExecutiveCourseGroup.class, after.getFakeCourseGroup().getId());
      if (mg == null) { throw new ExecutivePlanAuditException(
          "课程组不存在：" + after.getFakeCourseGroup().getCourseType().getName()); }
      planCourse.setGroup(mg);
      planCourseCommonDao.addPlanCourse(planCourse, plan);
    } else if (ExecutivePlanCourseModify.DELETE.equals(apply.getRequisitionType())) {
      ExecutivePlanCourseModifyDetail before = apply.getOldPlanCourse();
      Course course = before.getCourse();
      ExecutivePlanCourse planCourse = planCourseCommonDao.getExecutivePlanCourseByCourse(plan, course);
      if (planCourse == null) { throw new ExecutivePlanAuditException("课程不存在：" + course); }
      planCourseCommonDao.removePlanCourse(planCourse, plan);
    } else if (ExecutivePlanCourseModify.MODIFY.equals(apply.getRequisitionType())) {
      ExecutivePlanCourseModifyDetail before = apply.getOldPlanCourse();
      ExecutivePlanCourse planCourse = planCourseCommonDao.getExecutivePlanCourseByCourse(plan, before.getCourse());
      if (planCourse == null) { throw new ExecutivePlanAuditException("课程不存在：" + before.getCourse()); }
      ExecutiveCourseGroup oldGroup = (ExecutiveCourseGroup) planCourse.getGroup();

      ExecutivePlanCourseModifyDetailAfter after = apply.getNewPlanCourse();
      planCourse.setCompulsory(after.isCompulsory());
      planCourse.setCourse(after.getCourse());
      planCourse.setTerms(after.getTerms());
      if (planCourse.getTerms() == null) {
        planCourse.setTerms(new Terms(0));
      }
      planCourse.setRemark(after.getRemark());
      ExecutiveCourseGroup mg = get(ExecutiveCourseGroup.class, after.getFakeCourseGroup().getId());
      if (mg == null) { throw new ExecutivePlanAuditException(
          "课程组不存在：" + after.getFakeCourseGroup().getCourseType().getName()); }

      planCourse.setGroup(mg);
      planCourseCommonDao.updatePlanCourse(planCourse, plan);
      planCourseGroupCommonDao.saveOrUpdateCourseGroup(oldGroup);
    } else {
      throw new ExecutivePlanAuditException("错误的计划课程变更申请类型");
    }

    apply.setFlag(ExecutivePlanCourseModify.ACCEPT);
    apply.setAssessor(assessor);
    apply.setReplyDate(new Date(System.currentTimeMillis()));
    saveOrUpdate(apply);
  }

  public void rejected(ExecutivePlanCourseModify apply, User assessor) throws ExecutivePlanAuditException {
    // 只能对待审核的申请进行审核
    if (!ExecutivePlanCourseModify.INITREQUEST
        .equals(apply.getFlag())) { throw new ExecutivePlanAuditException("只能对待审核的申请进行审核"); }

    apply.setFlag(ExecutivePlanCourseModify.REFUSE);
    apply.setAssessor(assessor);
    apply.setReplyDate(new Date(System.currentTimeMillis()));
    saveOrUpdate(apply);
  }

  public void setPlanCourseCommonDao(PlanCourseCommonDao planCourseCommonDao) {
    this.planCourseCommonDao = planCourseCommonDao;
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

}
