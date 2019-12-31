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
package org.openurp.edu.graduation.plan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.bean.comparators.PropertyComparator;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.beangle.commons.lang.Strings;
import org.hibernate.annotations.Type;
import org.openurp.base.time.Terms;
import org.openurp.edu.base.model.Course;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.model.ExamGrade;
import org.openurp.edu.program.plan.model.PlanCourse;

/**
 * 培养计划课程审核结果
 */
@Entity(name = "org.openurp.edu.graduation.plan.model.CourseAuditResult")
public class CourseAuditResult extends LongIdObject {

  private static final long serialVersionUID = 7271307757012360755L;

  /** 课程组审核结果 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private GroupAuditResult groupResult;

  /** 课程 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Course course;

  /** 成绩 */
  @Size(max = 100)
  private String scores;

  /** 是否通过 */
  private boolean passed;

  /** 是否必修 */
  private boolean compulsory;

  /** 开课学期 */
  @NotNull
  @Type(type = "org.openurp.base.time.hibernate.TermsType")
  protected Terms terms = Terms.Empty;
  /** 备注 */
  @Size(max = 500)
  private String remark;

  public CourseAuditResult() {
    super();
  }

  public CourseAuditResult(PlanCourse planCourse) {
    this.course = planCourse.getCourse();
    this.compulsory = planCourse.isCompulsory();
  }

  public CourseAuditResult init(PlanCourse planCourse) {
    this.course = planCourse.getCourse();
    this.compulsory = planCourse.isCompulsory();
    return this;
  }

  public GroupAuditResult getGroupResult() {
    return groupResult;
  }

  public void setGroupResult(GroupAuditResult groupResult) {
    this.groupResult = groupResult;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean passed) {
    this.passed = passed;
  }

  public void checkPassed(List<CourseGrade> grades) {
    StringBuilder sb = new StringBuilder();
    if (CollectUtils.isEmpty(grades)) {
      scores = "--";
    } else {
      for (CourseGrade grade : grades) {
        sb.append(Strings.defaultIfBlank(grade.getScoreText(), "--")).append(" ");
        if (!passed) passed = grade.isPassed();
      }
      scores = sb.toString();
    }

    if (!passed && !grades.isEmpty()) {
      List<CourseGrade> newGrades = new ArrayList(grades);
      StringBuilder remarkSB = new StringBuilder();
      Collections.sort(newGrades, new PropertyComparator("semester"));
      String brString = "";
      for (CourseGrade grade : newGrades) {
        remarkSB.append(brString);
        remarkSB.append(grade.getSemester().getSchoolYear()).append(grade.getSemester().getName())
            .append(" ");
        for (ExamGrade examGrade : grade.getExamGrades()) {
          remarkSB.append(examGrade.getGradeType().getName()).append(" ");
          if (Strings.isNotBlank(examGrade.getScoreText())) {
            remarkSB.append(examGrade.getScoreText()).append(" ");
          } else {
            remarkSB.append(examGrade.getExamStatus().getName()).append(" ");
          }
        }
        brString = "\n";
      }
      remark = remarkSB.toString();
    }
  }

  public void checkPassed(List<CourseGrade> grades, List<CourseGrade> substituteGrades) {
    checkPassed(grades);

    if (!this.passed && !substituteGrades.isEmpty()) {
      // 处理一个替代课程有多个成绩的情况
      Map<Long, Boolean> courseId2passed = CollectUtils.newHashMap();
      for (CourseGrade subGrade : substituteGrades) {
        Boolean courseIdPassed = courseId2passed.get(subGrade.getCourse().getId());
        if (courseIdPassed == null) {
          courseIdPassed = subGrade.isPassed();
        }
        courseIdPassed |= subGrade.isPassed();
        courseId2passed.put(subGrade.getCourse().getId(), courseIdPassed);
      }

      this.passed = true;
      for (Long courseId : courseId2passed.keySet()) {
        this.passed &= courseId2passed.get(courseId);
      }

      StringBuffer tempStr = new StringBuffer();
      for (Iterator<CourseGrade> iter = substituteGrades.iterator(); iter.hasNext();) {
        CourseGrade grade = iter.next();
        tempStr.append(grade.getCourse().getName()).append('[').append(grade.getCourse().getCode())
            .append(']').append("\n");
        tempStr.append(grade.getSemester().getSchoolYear()).append(grade.getSemester().getName()).append(" ");
        for (ExamGrade examGrade : grade.getExamGrades()) {
          tempStr.append(examGrade.getGradeType().getName()).append(" ");
          if (Strings.isNotBlank(examGrade.getScoreText())) {
            tempStr.append(examGrade.getScoreText()).append(" ");
          } else {
            tempStr.append(examGrade.getExamStatus().getName()).append(" ");
          }
        }
        if (iter.hasNext()) {
          tempStr.append('\n');
        }
      }
      remark = tempStr.toString();
    }
  }

  public boolean isCompulsory() {
    return compulsory;
  }

  public void setCompulsory(boolean compulsory) {
    this.compulsory = compulsory;
  }

  public String getScores() {
    return scores;
  }

  public void setScores(String scores) {
    this.scores = scores;
  }

  public Terms getTerms() {
    return terms;
  }

  public void setTerms(Terms terms) {
    this.terms = terms;
  }

}