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
package org.openurp.edu.program.plan.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Student;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.service.CoursePlanProvider;

import java.util.*;

public class CoursePlanProviderImpl extends BaseServiceImpl implements CoursePlanProvider {

  private Program getProgram(Student std) {
    OqlBuilder<StdProgramBinding> bq = OqlBuilder.from(StdProgramBinding.class, "binding");
    bq.where("binding.std=:std", std);
    var bindings = entityDao.search(bq);
    if (bindings.isEmpty()) {
      if (std.getState().getMajor() == null) {
        return null;
      } else {
        OqlBuilder<Program> query = OqlBuilder.from(Program.class, "program");
        query.where("program.grade=:grade", std.getGrade());
        query.where("program.level=:level", std.getLevel());
        query.where("program.major=:major", std.getState().getMajor());
        var departs = new ArrayList<Department>();
        departs.add(std.getDepartment());
        if (null != std.getDepartment().getParent()) {
          departs.add(std.getDepartment().getParent());
        }
        query.where("program.department in(:departments)", departs);
        if (std.getState().getDirection() == null) {
          query.where("program.direction is null");
        } else {
          query.where("program.direction is null or program.direction=:direction", std.getState().getDirection());
        }
        var programs = entityDao.search(query);
        return programs.isEmpty() ? null : programs.get(0);
      }
    } else {
      return bindings.get(0).getProgram();
    }
  }

  public ExecutivePlan getExecutivePlan(Student student) {
    var program = getProgram(student);
    if (null == program) {
      return null;
    } else {
      OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
      query.where("plan.program=:program", program);
      List<ExecutivePlan> plans = entityDao.search(query);
      if (plans.isEmpty()) {
        return null;
      } else {
        List<ExecutivePlan> suitables = new ArrayList<>();
        if (null == student.getState().getDirection()) {//non direction
          for (ExecutivePlan plan : plans) {
            if (program.getStdTypes().isEmpty() || program.getStdTypes().contains(student.getStdType())) {
              suitables.add(plan);
            }
          }
        } else {// has direction
          for (ExecutivePlan plan : plans) {//first try
            if (plan.getProgram().getDirection() != null && program.getDirection().equals(student.getState().getDirection())) {
              if (program.getStdTypes().isEmpty() || program.getStdTypes().contains(student.getStdType())) {
                suitables.add(plan);
              }
            }
          }
          if (suitables.isEmpty()) {
            for (ExecutivePlan plan : plans) {
              if (program.getDirection() == null) {
                if (program.getStdTypes().isEmpty() || program.getStdTypes().contains(student.getStdType())) {
                  suitables.add(plan);
                }
              }
            }
          }
        }
        return suitables.isEmpty() ? null : suitables.get(0);
      }
    }

  }

  public Map<Student, CoursePlan> getCoursePlans(Collection<Student> students) {
    Map<Student, CoursePlan> result = CollectUtils.newHashMap();
    for (Student student : students) {
      CoursePlan plan = getCoursePlan(student);
      if (null != plan) result.put(student, plan);
    }
    return result;
  }

  public CoursePlan getCoursePlan(Student student) {
    return getExecutivePlan(student);
  }

  public List<PlanCourse> getPlanCourses(Student student) {
    CoursePlan plan = getCoursePlan(student);
    if (null == plan) {
      return CollectUtils.newArrayList();
    }

    Set<PlanCourse> planCourses = CollectUtils.newHashSet();
    addPlanCourse(plan.getGroups(), planCourses);
    return CollectUtils.newArrayList(planCourses);
  }

  /**
   * @param courseGroups
   * @param planCourses
   */
  private void addPlanCourse(List<CourseGroup> courseGroups, Set<PlanCourse> planCourses) {
    if (CollectionUtils.isEmpty(courseGroups)) {
      return;
    }
    for (CourseGroup courseGroup : courseGroups) {
      planCourses.addAll(courseGroup.getPlanCourses());
      addPlanCourse(courseGroup.getChildren(), planCourses);
    }
  }

  public List<Semester> getSemesterByPlanCourse(PlanCourse planCourse) {
    Date beginOn = planCourse.getGroup().getPlan().getBeginOn();
    Date endOn = planCourse.getGroup().getPlan().getEndOn();

    OqlBuilder<Semester> builder = OqlBuilder.from(Semester.class, "semester");
    builder.where("semester.beginOn <= :endOn", endOn);
    builder.where("semester.endOn >= :beginOn", beginOn);
    builder.orderBy(Order.parse("semester.beginOn"));
    List<Semester> semestersByInterval = entityDao.search(builder);

    List<Integer> terms = planCourse.getTerms().getTermList();
    List<Semester> semestersByResult = CollectUtils.newArrayList();
    for (Integer term : terms) {
      semestersByResult.add(semestersByInterval.get(term - 1));
    }
    return semestersByResult;
  }
}
