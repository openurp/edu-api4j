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
package org.openurp.edu.grade.plan.service.internal;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.base.std.model.Student;
import org.openurp.edu.grade.app.model.AuditSetting;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.service.CourseGradeProvider;
import org.openurp.edu.grade.plan.adapters.CourseGroupAdapter;
import org.openurp.edu.grade.plan.adapters.GroupResultAdapter;
import org.openurp.edu.grade.plan.model.AuditStat;
import org.openurp.edu.grade.plan.model.AuditCourseResult;
import org.openurp.edu.grade.plan.model.AuditGroupResult;
import org.openurp.edu.grade.plan.model.AuditPlanResult;
import org.openurp.edu.grade.plan.service.AuditSettingService;
import org.openurp.edu.grade.plan.service.AuditPlanContext;
import org.openurp.edu.grade.plan.service.AuditPlanListener;
import org.openurp.edu.grade.plan.service.PlanAuditService;
import org.openurp.edu.grade.plan.service.observers.ObserverUtils;
import org.openurp.edu.grade.plan.service.observers.PlanAuditObserverStack;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.PlanCourse;
import org.openurp.edu.program.plan.service.CoursePlanProvider;
import org.openurp.edu.program.utils.PlanUtils;
import org.openurp.service.OutputObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class AuditPlanServiceImpl extends BaseServiceImpl implements PlanAuditService {

  private Logger logger = LoggerFactory.getLogger(AuditPlanServiceImpl.class);

  protected CoursePlanProvider coursePlanProvider;

  protected AuditSettingService auditSettingService;

  protected CourseGradeProvider courseGradeProvider;

  protected GroupResultBuilder groupResultBuilder = new DefaultGroupResultBuilder();

  protected List<AuditPlanListener> listeners = new ArrayList<AuditPlanListener>();

  public AuditPlanResult audit(final Student student) {
    var plan = coursePlanProvider.getCoursePlan(student);
    var setting = auditSettingService.getSetting(student);
    if (setting.isTransient() && null != plan && null != plan.getProgram().getOffsetType()) {
      setting.setConvertTarget(plan.getProgram().getOffsetType());
    }
    AuditPlanContext context = new AuditPlanContext(student, plan, setting);
    return audit(student, context);
  }

  public AuditPlanResult audit(final Student student, AuditPlanContext context) {
    logger.debug("start audit {}", student.getCode());
    AuditPlanResult planAuditResult = new AuditPlanResult(student);
    planAuditResult.setPassed(false);
    planAuditResult.setRemark(null);
    planAuditResult.setUpdatedAt(new Date());
    context.setResult(planAuditResult);

    // 获得学生对应的计划
    CoursePlan plan = context.getCoursePlan();
    if (null == plan) {
      planAuditResult.setRemark("没有匹配的培养计划");
      return context.getResult();
    }

    context.setStdGrade(new StdGradeImpl(courseGradeProvider.getPublished(student)));

    // context里面已经设置好了result，不管是空的还是怎样
    for (AuditPlanListener listener : listeners) {
      if (!listener.startPlanAudit(context)) {
        return planAuditResult;
      }
    }

    // 构建了一个虚拟的顶层组，这个顶层组在CoursePlan的结构中是没有的，这个顶层组可以认为是CoursePlan
    CourseGroupAdapter courseGroupAdapter = new CourseGroupAdapter(context.getCoursePlan());
    AuditGroupResult groupResultAdapter = new GroupResultAdapter(planAuditResult);

    // 培养计划的学分要求和门数要求需要根据审核学期来发生变化
    float requiredCredits = context.getCoursePlan().getCredits();
    if (context.getAuditTerms() != null && context.getAuditTerms().length != 0) {
      requiredCredits = 0;
      for (int i = 0; i < context.getAuditTerms().length; i++) {
        for (CourseGroup group : context.getCoursePlan().getGroups()) {
          if (group.getParent() == null) {
            requiredCredits += PlanUtils.getGroupCredits(group, Integer.valueOf(context.getAuditTerms()[i]));
          }
        }
      }
    }
    planAuditResult.setRequiredCredits(requiredCredits);

    auditGroup(context, courseGroupAdapter, groupResultAdapter);

    for (AuditPlanListener listener : listeners) {
      listener.endPlanAudit(context);
    }

    AuditGroupResult lastTarget = context.getResult().getGroupResult(context.getSetting().getConvertTarget());
    if (null != lastTarget) {
      if (lastTarget.getAuditStat().getPassedCredits() == 0
          && lastTarget.getAuditStat().getRequiredCredits() == 0 && lastTarget.getCourseResults().isEmpty()) {
        context.getResult().removeGroupResult(lastTarget);
      }
    }
    return planAuditResult;
  }

  /**
   * 获得课程组审核结果(递归)
   *
   * @param context
   * @param courseGroup
   * @param groupAuditResult
   */
  private void auditGroup(AuditPlanContext context, CourseGroup courseGroup,
                          AuditGroupResult groupAuditResult) {
    // 循环子组的结果,将结果放入该组
    List<CourseGroup> courseGroups = courseGroup.getChildren();
    AuditPlanResult planAuditResult = context.getResult();

    groupAudit:
    for (Iterator<CourseGroup> it = courseGroups.iterator(); it.hasNext(); ) {
      CourseGroup children = it.next();
      AuditGroupResult childResult = groupResultBuilder.buildResult(context, children);
      groupAuditResult.addChild(childResult);
      planAuditResult.addGroupResult(childResult);
      for (Iterator<AuditPlanListener> it1 = listeners.iterator(); it1.hasNext(); ) {
        AuditPlanListener listener = it1.next();
        if (!listener.startGroupAudit(context, children, childResult)) {
          planAuditResult.reduceRequired(childResult.getAuditStat().getRequiredCredits());
          groupAuditResult.removeChild(childResult);
          planAuditResult.removeGroupResult(childResult);
          continue groupAudit;
        }
      }
      auditGroup(context, children, childResult);
    }
    // 该组的计划课程审核结果
    List<PlanCourse> myPlanCourses = courseGroup.getPlanCourses();

    courseAudit:
    for (Iterator<PlanCourse> iter = myPlanCourses.iterator(); iter.hasNext(); ) {
      PlanCourse planCourse = iter.next();
      for (AuditPlanListener listener : listeners) {
        if (!listener.startCourseAudit(context, groupAuditResult, planCourse)) {
          continue courseAudit;
        }
      }

      AuditCourseResult planCourseAuditResult = new AuditCourseResult(planCourse);
      List<CourseGrade> courseGrades = context.getStdGrade().useGrades(planCourse.getCourse());
      if (courseGrades.isEmpty()) {
        if (!planCourse.isCompulsory() && !courseGroup.isAutoAddup()) continue;
        courseGrades = Collections.emptyList();
      }
      planCourseAuditResult.checkPassed(courseGrades);
      groupAuditResult.addCourseResult(planCourseAuditResult);
    }
    groupAuditResult.checkPassed(false);
  }

  public AuditPlanResult getResult(Student std) {
    OqlBuilder<AuditPlanResult> query = OqlBuilder.from(AuditPlanResult.class, "planResult");
    query.where("planResult.std = :std", std);
    List<AuditPlanResult> results = entityDao.search(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return null;
  }

  /**
   * 批量审核培养计划完成情况
   */
  public void batchAudit(Collection<Student> stds, String[] auditTerms, PlanAuditObserverStack observerStack,
                         OutputObserver weboutput) {
    observerStack.notifyStart();

    int amount = stds.size();
    String msg = MessageFormat.format("{0} 位学生计划完成审核", amount);
    ObserverUtils.notifyStart(weboutput, msg, amount, null);

    int count = 0;
    for (Iterator<Student> iterator = stds.iterator(); iterator.hasNext(); count++) {
      Student student = iterator.next();

      // 获得学生对应的计划
      CoursePlan plan = coursePlanProvider.getCoursePlan(student);
      AuditSetting setting = auditSettingService.getSetting(student);
      if (setting.isTransient() && null != plan && null != plan.getProgram().getOffsetType()) {
        setting.setConvertTarget(plan.getProgram().getOffsetType());
      }
      AuditPlanContext context = new AuditPlanContext(student, plan, setting);
      context.getParams().put("_weboutput", weboutput);
      context.setAuditTerms(auditTerms);

      if (null == context.getCoursePlan()) {
        msg = MessageFormat.format("无法找到 {0} {1} 的培养计划，审核失败。", student.getName(),
            student.getCode());
        if (observerStack.notifyBegin(context, count)) {
          audit(student, context);
          observerStack.notifyEnd(context, count);
        }
        ObserverUtils.outputMessage(context, OutputObserver.error, msg, true);
      } else {
        String auditTermsStr = "";
        if (auditTerms != null && auditTerms.length > 0) {
          auditTermsStr = "(第" + Strings.join(auditTerms, ",") + "学期)";
        }
        msg = MessageFormat.format("开始审核 {0} {1} 的计划完成情况{2}", student.getName(), student.getCode(),
            auditTermsStr);
        ObserverUtils.outputMessage(context, OutputObserver.good, msg, true);
        if (observerStack.notifyBegin(context, count)) {
          audit(student, context);
          observerStack.notifyEnd(context, count);
        }
        msg = MessageFormat.format("审核完毕 {0} {1} 的计划完成情况", student.getName(), student.getCode());
        ObserverUtils.outputMessage(context, OutputObserver.good, msg, false);
      }

      if (iterator.hasNext()) {
        ObserverUtils.delimiter(weboutput);
      }
    }
    observerStack.finish();
  }

  public AuditSetting getSetting(Student student) {
    return auditSettingService.getSetting(student);
  }

  public void setCoursePlanProvider(CoursePlanProvider coursePlanProvider) {
    this.coursePlanProvider = coursePlanProvider;
  }

  public void setCourseGradeProvider(CourseGradeProvider courseGradeProvider) {
    this.courseGradeProvider = courseGradeProvider;
  }

  public List<AuditPlanListener> getListeners() {
    return listeners;
  }

  public void setListeners(List<AuditPlanListener> listeners) {
    this.listeners = listeners;
  }

  public void setGroupResultBuilder(GroupResultBuilder groupResultBuilder) {
    this.groupResultBuilder = groupResultBuilder;
  }

  public void setAuditSettingService(AuditSettingService auditSettingService) {
    this.auditSettingService = auditSettingService;
  }

}
