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
package org.openurp.edu.grade.plan.service.observers;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.edu.model.Course;
import org.openurp.base.std.model.Student;
import org.openurp.edu.grade.plan.adapters.GroupResultAdapter;
import org.openurp.edu.grade.plan.model.AuditCourseResult;
import org.openurp.edu.grade.plan.model.AuditGroupResult;
import org.openurp.edu.grade.plan.model.AuditPlanResult;
import org.openurp.edu.grade.plan.service.AuditPlanContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 计划审核保存
 */
public class PlanAuditPersistObserver extends BaseServiceImpl implements PlanAuditObserver {

  public void notifyStart() {
  }

  public void finish() {
  }

  public boolean notifyBegin(AuditPlanContext context, int index) {
    return true;
  }

  private AuditPlanResult getResult(Student std) {
    OqlBuilder<AuditPlanResult> query = OqlBuilder.from(AuditPlanResult.class, "planResult");
    query.where("planResult.std = :std", std);
    List<AuditPlanResult> results = entityDao.search(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return null;
  }

  public void notifyEnd(AuditPlanContext context, int index) {
    AuditPlanResult newResult = context.getResult();
    AuditPlanResult existedResult = getResult(context.getStd());
    if (null != existedResult) {
      existedResult.setRemark(newResult.getRemark());

      existedResult.setUpdatedAt(new Date());
      existedResult.setRequiredCredits(newResult.getRequiredCredits());
      existedResult.setPassedCredits(newResult.getPassedCredits());
      StringBuilder updates = new StringBuilder();
      mergeGroupResult(existedResult, new GroupResultAdapter(existedResult),
          new GroupResultAdapter(newResult), updates);

      existedResult.setPassed(newResult.isPassed());

      // delete last ';'
      if (updates.length() > 0) updates.deleteCharAt(updates.length() - 1);
      existedResult.setUpdates(updates.toString());
    } else {
      existedResult = newResult;
    }
    try {
      entityDao.saveOrUpdate(existedResult);
    } catch (Exception e) {
      logger.error("save {} plan audit result error.", context.getStd().getCode());
    }
    context.setResult(existedResult);
  }

  private void mergeGroupResult(AuditPlanResult existedResult, AuditGroupResult target,
                                AuditGroupResult source, StringBuilder updates) {
    // 统计完成学分的变化
    float delta = source.getAuditStat().getPassedCredits() - target.getAuditStat().getPassedCredits();
    if (Float.compare(delta, 0) != 0) {
      updates.append(source.getName());
      if (delta > 0) updates.append('+').append(delta);
      else updates.append(delta);
      updates.append(';');
    }
    target.setAuditStat(source.getAuditStat());
    target.setPassed(source.isPassed());
    target.setIndexno(source.getIndexno());
    // 收集课程组[groupName->groupResult]
    Map<String, AuditGroupResult> targetGroupResults = CollectUtils.newHashMap();
    Map<String, AuditGroupResult> sourceGroupResults = CollectUtils.newHashMap();
    for (AuditGroupResult result : target.getChildren())
      targetGroupResults.put(result.getName(), result);
    for (AuditGroupResult result : source.getChildren())
      sourceGroupResults.put(result.getName(), result);

    // 收集课程结果[course->courseResult]
    Map<Course, AuditCourseResult> targetCourseResults = CollectUtils.newHashMap();
    Map<Course, AuditCourseResult> sourceCourseResults = CollectUtils.newHashMap();
    for (AuditCourseResult courseResult : target.getCourseResults())
      targetCourseResults.put(courseResult.getCourse(), courseResult);
    for (AuditCourseResult courseResult : source.getCourseResults())
      sourceCourseResults.put(courseResult.getCourse(), courseResult);

    // 删除没有的课程组
    Set<String> removed = CollectUtils.subtract(targetGroupResults.keySet(), sourceGroupResults.keySet());
    for (String groupName : removed) {
      AuditGroupResult gg = targetGroupResults.get(groupName);
      gg.detach();
      target.removeChild(gg);
    }

    // 添加课程组
    Set<String> added = CollectUtils.subtract(sourceGroupResults.keySet(), targetGroupResults.keySet());
    for (String groupName : added) {
      AuditGroupResult groupResult = (AuditGroupResult) sourceGroupResults.get(groupName);
      target.addChild(groupResult);
      groupResult.attachTo(existedResult);
    }

    // 合并课程组
    Set<String> common = CollectUtils.intersection(sourceGroupResults.keySet(), targetGroupResults.keySet());
    for (String groupName : common) {
      mergeGroupResult(existedResult, targetGroupResults.get(groupName), sourceGroupResults.get(groupName),
          updates);
    }
    // ------- 合并课程结果
    // 删除没有的课程
    Set<Course> removedCourses = CollectUtils.subtract(targetCourseResults.keySet(),
        sourceCourseResults.keySet());
    for (Course course : removedCourses) {
      AuditCourseResult courseResult = (AuditCourseResult) targetCourseResults.get(course);
      target.getCourseResults().remove(courseResult);
    }
    // 添加新的课程结果
    Set<Course> addedCourses = CollectUtils.subtract(sourceCourseResults.keySet(),
        targetCourseResults.keySet());
    for (Course course : addedCourses) {
      AuditCourseResult courseResult = (AuditCourseResult) sourceCourseResults.get(course);
      courseResult.getGroupResult().getCourseResults().remove(courseResult);
      courseResult.setGroupResult(target);
      target.getCourseResults().add(courseResult);
    }
    // 合并共同的课程
    Set<Course> commonCourses = CollectUtils.intersection(sourceCourseResults.keySet(),
        targetCourseResults.keySet());
    for (Course course : commonCourses) {
      AuditCourseResult targetCourseResult = targetCourseResults.get(course);
      AuditCourseResult sourceCourseResult = sourceCourseResults.get(course);
      targetCourseResult.setPassed(sourceCourseResult.isPassed());
      targetCourseResult.setScores(sourceCourseResult.getScores());
      targetCourseResult.setCompulsory(sourceCourseResult.isCompulsory());
      targetCourseResult.setRemark(sourceCourseResult.getRemark());
    }
  }
}
