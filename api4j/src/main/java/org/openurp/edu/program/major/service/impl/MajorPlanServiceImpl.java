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

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.edu.model.Course;
import org.openurp.base.model.AuditStatus;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.program.major.service.MajorPlanGenParameter;
import org.openurp.edu.program.major.service.MajorPlanService;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.dao.PlanCommonDao;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.plan.service.MajorPlanQueryBuilder;
import org.openurp.edu.program.plan.util.ProgramNamingHelper;

import java.util.*;

/**
 * 培养计划服务接口
 */
public class MajorPlanServiceImpl extends BaseServiceImpl implements MajorPlanService {

  private PlanCommonDao planCommonDao;

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public MajorPlan genMajorPlan(MajorPlan sourcePlan, MajorPlanGenParameter genParameter) throws Exception {
    // 先得保存program
    Program program = sourcePlan.getProgram();
    Program newProgram = newProgram(program, genParameter);
    newProgram.setStartTerm(program.getStartTerm());
    newProgram.setEndTerm(program.getEndTerm());

    entityDao.saveOrUpdate(newProgram);
    tryCloneDoc(program, newProgram);

    // 然后才能复制plan，保存plan
    MajorPlan newPlan = (MajorPlan) sourcePlan.clone();
    newPlan.setId(null);
    newPlan.setCredits(sourcePlan.getCredits());
    newPlan.setCreditHours(sourcePlan.getCreditHours());
    newPlan.setHourRatios(sourcePlan.getHourRatios());
    newPlan.setUpdatedAt(new java.util.Date());

    if (genParameter != null) {
      newProgram.setStartTerm(genParameter.getStartTerm());
      newProgram.setEndTerm(genParameter.getEndTerm());
    }
    newPlan.setGroups(new ArrayList<CourseGroup>());
    newPlan.setProgram(newProgram);
    entityDao.saveOrUpdate(newPlan);

    // 要先保存好一个空的培养计划后才可以复制课程组到里面
    if (sourcePlan != null) {
      for (CourseGroup sourceCourseGroup : sourcePlan.getTopCourseGroups()) {
        planCourseGroupCommonDao.copyCourseGroup(sourceCourseGroup, null,
            newPlan, MajorCourseGroup.class, MajorPlanCourse.class);
      }
    }
    planCommonDao.saveOrUpdatePlan(newPlan);
    return newPlan;
  }

  public List<MajorPlan> genMajorPlans(Collection<MajorPlan> plans, MajorPlanGenParameter partialParams) throws Exception {
    List<MajorPlan> genedPlans = new ArrayList<MajorPlan>(plans.size());
    for (MajorPlan plan : plans) {
      MajorPlanGenParameter t_param = new MajorPlanGenParameter();
      t_param.setGrade(partialParams.getGrade());
      t_param.setBeginOn(partialParams.getBeginOn());
      t_param.setEndOn(partialParams.getEndOn());
      t_param.setDuration(plan.getProgram().getDuration());
      t_param.setStartTerm(plan.getProgram().getStartTerm());
      t_param.setEndTerm(plan.getProgram().getEndTerm());

      t_param.setDegree(plan.getProgram().getDegree());
      t_param.setDepartment(plan.getProgram().getDepartment());
      t_param.setDirection(plan.getProgram().getDirection());
      t_param.setLevel(plan.getProgram().getLevel());
      t_param.setMajor(plan.getProgram().getMajor());
      t_param.getStdTypes().addAll(plan.getProgram().getStdTypes());
      t_param.setStudyType(plan.getProgram().getStudyType());
      t_param.setName(ProgramNamingHelper.name(t_param, entityDao));
      genedPlans.add(genMajorPlan(plan, t_param));
    }
    return genedPlans;
  }

  public Set<String> getUnusedCourseTypeNames(MajorPlan plan) {
    return planCommonDao.getUnusedCourseTypeNames(plan);
  }

  public void removeMajorPlan(MajorPlan plan) {
    planCommonDao.removePlan(plan);
  }

  public void saveOrUpdateMajorPlan(MajorPlan plan) {
    planCommonDao.saveOrUpdatePlan(plan);
  }

  public float statPlanCredits(Long planId) {
    return statPlanCredits(entityDao.get(MajorPlan.class, planId));
  }

  public float statPlanCredits(MajorPlan plan) {
    for (CourseGroup group : plan.getTopCourseGroups()) {
      planCourseGroupCommonDao.updateGroupTreeCredits(group);
    }
    float res = planCommonDao.statPlanCredits(plan);
    plan.setCredits(res);
    this.entityDao.saveOrUpdate(plan);
    return res;
  }

  public boolean hasCourse(MajorCourseGroup cgroup, Course course) {
    return planCommonDao.hasCourse(cgroup, course);
  }

  public boolean hasCourse(MajorCourseGroup cgroup, Course course, PlanCourse planCourse) {
    return planCommonDao.hasCourse(cgroup, course, planCourse);
  }

  public void setPlanCommonDao(PlanCommonDao planCommonDao) {
    this.planCommonDao = planCommonDao;
  }

  public MajorPlan getMajorPlanByAdminClass(Squad clazz) {
    List<MajorPlan> res = entityDao.search(MajorPlanQueryBuilder.build(clazz));
    return CollectUtils.isEmpty(res) ? null : res.get(0);
  }

  public List<MajorPlanCourse> getPlanCourses(MajorPlan plan) {
    if (CollectUtils.isEmpty(plan.getGroups())) {
      return Collections.EMPTY_LIST;
    }
    List<MajorPlanCourse> planCourses = new ArrayList<MajorPlanCourse>();
    for (Iterator iter = plan.getGroups().iterator(); iter.hasNext(); ) {
      MajorCourseGroup group = (MajorCourseGroup) iter.next();
      planCourses.addAll((List) group.getPlanCourses());
    }
    return planCourses;
  }

  private void tryCloneDoc(Program from, Program to) {
    OqlBuilder<ProgramDoc> q = OqlBuilder.from(ProgramDoc.class, "pd");
    q.where("pd.program=:program", from);
    for (ProgramDoc od : entityDao.search(q)) {
      ProgramDoc nd = new ProgramDoc();
      nd.setProgram(to);
      nd.setDocLocale(od.getDocLocale());
      nd.setUpdatedAt(new java.util.Date());
      for (ProgramDocSection ods : od.getSections()) {
        ProgramDocSection nds = new ProgramDocSection();
        nds.setIndexno(ods.getIndexno());
        nds.setDoc(nd);
        nds.setContents(ods.getContents());
        nds.setName(ods.getName());
        nd.getSections().add(nds);
      }
      entityDao.saveOrUpdate(nd);
    }
  }

  private Program newProgram(Program originalProgram, MajorPlanGenParameter param) {
    try {
      Program newProgram = (Program) (originalProgram).clone();
      newProgram.setDegreeCourses(new HashSet<>(originalProgram.getDegreeCourses()));
      newProgram.setTermCampuses(new ArrayList<>());
      for (TermCampus tc : originalProgram.getTermCampuses()) {
        TermCampus ntc = new TermCampus();
        ntc.setProgram(newProgram);
        ntc.setTerms(tc.getTerms());
        ntc.setCampus(tc.getCampus());
        newProgram.getTermCampuses().add(ntc);
      }
      Date now = new Date();
      newProgram.setId(null);
      newProgram.setName(param.getName());
      newProgram.setStatus(AuditStatus.UNSUBMITTED);
      newProgram.setUpdatedAt(now);
      newProgram.setDuration(param.getDuration());
      newProgram.setGrade(param.getGrade());
      newProgram.setBeginOn(param.getBeginOn());
      newProgram.setEndOn(param.getEndOn());

      if (param.getDegree() == null || param.getDegree().isTransient()) {
        newProgram.setDegree(null);
      } else {
        newProgram.setDegree(param.getDegree());
      }
      if (param.getDepartment() == null || param.getDepartment().isTransient()) {
        newProgram.setDepartment(null);
      } else {
        newProgram.setDepartment(param.getDepartment());
      }
      if (param.getDirection() == null || param.getDirection().isTransient()) {
        newProgram.setDirection(null);
      } else {
        newProgram.setDirection(param.getDirection());
      }
      if (param.getLevel() == null || param.getLevel().isTransient()) {
        newProgram.setLevel(null);
      } else {
        newProgram.setLevel(param.getLevel());
      }
      if (param.getMajor() == null || param.getMajor().isTransient()) {
        newProgram.setMajor(null);
      } else {
        newProgram.setMajor(param.getMajor());
      }
      newProgram.setStdTypes(new HashSet<>());
      newProgram.getStdTypes().addAll(param.getStdTypes());
      if (param.getStudyType() == null || param.getStudyType().isTransient()) {
        newProgram.setStudyType(null);
      } else {
        newProgram.setStudyType(param.getStudyType());
      }
      newProgram.setDegreeCourses(new HashSet<>());
      newProgram.getDegreeCourses().addAll(param.getDegreeCourses());
      return newProgram;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }
}
