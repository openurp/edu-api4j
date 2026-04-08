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
package org.openurp.edu.program.plan.dao.impl;

import org.apache.commons.lang3.Range;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.code.edu.model.CourseType;
import org.openurp.base.edu.model.Course;
import org.openurp.base.model.AuditStatus;
import org.openurp.code.service.CodeService;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.dao.PlanCommonDao;
import org.openurp.edu.program.plan.util.ProgramHibernateClassGetter;
import org.openurp.edu.program.utils.PlanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlanCommonDaoHibernate extends HibernateEntityDao implements PlanCommonDao {

  private CodeService codeService;

  public final void removePlan(CoursePlan plan) {
    remove(get(ProgramHibernateClassGetter.hibernateClass(plan), plan.getId()));
  }

  public final void saveOrUpdatePlan(CoursePlan plan) {
    saveSetting(plan);
    saveOrUpdate(plan);
    plan.setCredits(statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  /**
   * 用于在保存的时候对Plan的属性进行一些设置，可以重载这个方法以提供额外的设置
   *
   * @param plan
   */
  protected void saveSetting(CoursePlan plan) {
    // EntityUtils.evictEmptyProperty(plan);
    if (plan instanceof ExecutivePlan) {
      ExecutivePlan mplan = (ExecutivePlan) plan;
      if (null == mplan.getProgram().getStatus()) {
        mplan.getProgram().setStatus(AuditStatus.UNSUBMITTED);
      }
    }
  }

  public float statPlanCredits(CoursePlan plan) {
    float totalCredits = 0;
    if (plan.getTopCourseGroups() != null) {
      for (CourseGroup group : plan.getTopCourseGroups()) {
        totalCredits += group.getCredits();
      }
    }
    return totalCredits;
  }

  public boolean hasCourse(CourseGroup cgroup, Course course) {
    return hasCourse(cgroup, course, null);
  }

  public boolean hasCourse(CourseGroup cgroup, Course course, PlanCourse planCourse) {
    OqlBuilder<? extends CourseGroup> query = OqlBuilder
        .from(ProgramHibernateClassGetter.hibernateClass(cgroup), "cGroup");
    query.select("select distinct planCourse").join("cGroup.planCourses", "planCourse")
        .where("cGroup.id = :cGroupId", cgroup.getId()).where("planCourse.course = :course", course);
    if (planCourse != null) {
      query.where("planCourse <> :planCourse", planCourse);
    }
    List<?> res = search(query);
    return !res.isEmpty();
  }

  public Set<String> getUsedCourseTypeNames(CoursePlan plan) {
    return plan.getGroups().stream().map(x -> x.getName()).collect(Collectors.toSet());
  }

  public List<CourseType> getUnusedCourseTypes(CoursePlan plan) {
    List<CourseType> allCourseTypes = codeService.getCodes(CourseType.class);
    List<CourseType> usedCourseTypes = plan.getGroups().stream().map(x -> x.getCourseType()).collect(Collectors.toList());

    allCourseTypes.removeAll(usedCourseTypes);
    return allCourseTypes;
  }

  public Set<String> getUnusedCourseTypeNames(CoursePlan plan) {
    Set<String> allCourseTypes = codeService.getCodes(CourseType.class).stream().map(x -> x.getName()).collect(Collectors.toSet());
    Set<String> usedCourseTypes = getUsedCourseTypeNames(plan);

    allCourseTypes.removeAll(usedCourseTypes);
    return allCourseTypes;
  }

  public List<Program> getDuplicatePrograms(Program program) {
    OqlBuilder<Program> query = OqlBuilder.from(Program.class, "program");
    if (program.isPersisted()) {
      query.where("program.id <> :me", program.getId());
    }
    query.where("program.grade = :grade", program.getGrade());
    query.where("program.level.id = :levelId", program.getLevel().getId());
    if (program.getStdTypes().isEmpty()) {
      query.where("size(program.stdTypes)==0");
    } else {
      query.where("exists(from program.stdTypes as stdType where stdType in(:stdTypes))", program.getStdTypes());
    }

    query.where("program.department.id = :departmentId", program.getDepartment().getId());
    query.where("program.major.id = :majorId", program.getMajor().getId());
    if (program.getDirection() != null && program.getDirection().getId() != null) {
      query.where("program.direction.id = :directionId", program.getDirection().getId());
    } else {
      query.where("program.direction is null");
    }
    return search(query);
  }

  public boolean isDuplicate(Program program) {
    return CollectUtils.isNotEmpty(getDuplicatePrograms(program));
  }

  public Float getCreditByTerm(ExecutivePlan plan, int term) {
    Range<Integer> termRange = Range.between(1, plan.getProgram().getTermsCount());
    if (!termRange.contains(term)) {
      throw new RuntimeException("term out range");
    } else {
      float credit = 0;
      // calculate for each group
      for (CourseGroup group : plan.getGroups()) {
        credit += PlanUtils.getGroupCredits(group, term);
      }
      return Float.valueOf(credit);
    }
  }

  public void setCodeService(CodeService codeService) {
    this.codeService = codeService;
  }
}
