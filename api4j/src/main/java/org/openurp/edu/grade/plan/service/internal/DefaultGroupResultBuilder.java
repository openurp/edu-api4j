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

import java.util.Set;

import org.beangle.commons.collection.CollectUtils;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.grade.plan.model.AuditGroupResult;
import org.openurp.edu.grade.plan.service.AuditPlanContext;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.PlanCourse;
import org.openurp.edu.program.utils.PlanUtils;

public class DefaultGroupResultBuilder implements GroupResultBuilder {

  public AuditGroupResult buildResult(AuditPlanContext context, CourseGroup group) {
    AuditGroupResult result = new AuditGroupResult();

    // 课程组的学分要求应该根据审核学期的变化而变化
    float requiredCredits = group.getCredits();
    if (context.getAuditTerms() != null && context.getAuditTerms().length != 0) {
      requiredCredits = 0;
      float groupCourseCredits = 0f;
      boolean creditsNeedCompare = false;
      // 已计算学分课程
      Set<Course> auditedCourses = CollectUtils.newHashSet();
      for (int i = 0; i < context.getAuditTerms().length; i++) {
        int term = Integer.valueOf(context.getAuditTerms()[i]);
        requiredCredits += PlanUtils.getGroupCredits(group, term);
        // 组课程学分
        if (group.getChildren().isEmpty() && !group.getPlanCourses().isEmpty() && group.isAutoAddup()) {
          creditsNeedCompare = true;
          for (PlanCourse planCourse : group.getPlanCourses()) {
            if (!auditedCourses.contains(planCourse.getCourse()) && planCourse.getTerms().contains(term)) {
              groupCourseCredits += planCourse.getCourse().getDefaultCredits();
              auditedCourses.add(planCourse.getCourse());
            }
          }
        }
      }
      if (creditsNeedCompare) {
        requiredCredits = Float.compare(requiredCredits, groupCourseCredits) < 0 ? requiredCredits
            : groupCourseCredits;
      }
    }
    result.getAuditStat().setRequiredCredits(requiredCredits);

    result.setCourseType(group.getCourseType());
    result.setName(group.getName());
    result.setSubCount(group.getSubCount());
    result.setIndexno(group.getIndexno());
    result.setPlanResult(context.getResult());
    return result;
  }

}
