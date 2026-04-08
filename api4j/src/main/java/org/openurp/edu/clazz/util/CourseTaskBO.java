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
package org.openurp.edu.clazz.util;

import org.openurp.base.edu.model.Semester;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.PlanCourse;

public class CourseTaskBO {

  private Semester semester;

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  private ExecutivePlan teachPlan;

  private Squad adminClass;

  private PlanCourse planCourse;

  public Squad getSquad() {
    return adminClass;
  }

  public void setSquad(Squad adminClass) {
    this.adminClass = adminClass;
  }

  public PlanCourse getPlanCourse() {
    return planCourse;
  }

  public void setPlanCourse(PlanCourse planCourse) {
    this.planCourse = planCourse;
  }

  public ExecutivePlan getExecutivePlan() {
    return teachPlan;
  }

  public void setExecutivePlan(ExecutivePlan teachPlan) {
    this.teachPlan = teachPlan;
  }

  public CourseTaskBO() {

  }

  public CourseTaskBO(ExecutivePlan teachPlan, Squad adminClass, PlanCourse planCourse) {
    this.teachPlan = teachPlan;
    this.adminClass = adminClass;
    this.planCourse = planCourse;
  }

  public CourseTaskBO(ExecutivePlan teachPlan, Squad adminClass, PlanCourse planCourse, Semester semester) {
    this.teachPlan = teachPlan;
    this.adminClass = adminClass;
    this.planCourse = planCourse;
    this.semester = semester;
  }

  public int getTerm() {
    int term = 1;
    if (semester != null) {
      String schoolyear = semester.getSchoolYear();
      String name = semester.getName();// 学期
      String beginYear = schoolyear.substring(0, 4);
      int beginYearInt = Integer.valueOf(beginYear).intValue();
      int nameInt = Integer.valueOf(name).intValue();

      String grade = teachPlan.getProgram().getGrade().getCode();
      String year = grade.substring(0, 4);
      String flag = grade.substring(5, grade.length());
      int yearInt = Integer.valueOf(year).intValue();
      if (flag.equals("9")) {// 秋季班
        term = (beginYearInt - yearInt) * 2 + nameInt;
      }
      if (flag.equals("3")) {// 春季班
        term = (beginYearInt - yearInt) * 2 + nameInt + 1;
      }
    }
    return term;
  }

  public int getTerm(Semester semester) {
    int term = 1;
    String schoolyear = semester.getSchoolYear();
    String name = semester.getName();// 学期
    String beginYear = schoolyear.substring(0, 4);
    int beginYearInt = Integer.valueOf(beginYear).intValue();
    int nameInt = Integer.valueOf(name).intValue();

    String grade = teachPlan.getProgram().getGrade().getCode();
    String year = grade.substring(0, 4);
    String flag = grade.substring(5, grade.length());
    int yearInt = Integer.valueOf(year).intValue();
    if (flag.equals("9")) {// 秋季班
      term = (beginYearInt - yearInt) * 2 + nameInt;
    }
    if (flag.equals("3")) {// 春季班
      term = (beginYearInt - yearInt) * 2 + nameInt + 1;
    }
    return term;
  }

}
