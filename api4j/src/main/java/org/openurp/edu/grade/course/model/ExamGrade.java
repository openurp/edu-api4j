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
package org.openurp.edu.grade.course.model;

import static org.openurp.edu.grade.Grade.Status.Confirmed;
import static org.openurp.edu.grade.Grade.Status.Published;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.hibernate.annotations.NaturalId;
import org.openurp.code.edu.model.ExamStatus;
import org.openurp.code.edu.model.GradeType;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.base.std.model.Student;
import org.openurp.edu.grade.Grade;

/**
 * 考试成绩
 */
@Entity(name = "org.openurp.edu.grade.course.model.ExamGrade")
public class ExamGrade extends NumberIdTimeObject<Long> implements Grade {

  private static final long serialVersionUID = 3737090012804400743L;

  /** 成绩类型 */
  @NaturalId
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private GradeType gradeType;

  /** 成绩记录方式 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private GradingMode gradingMode;

  /** 考试情况 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamStatus examStatus;

  /** 得分 */
  private Float score;

  /** 得分字面值 */
  private String scoreText;

  /** 对应的课程成绩 */
  @NaturalId
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseGrade courseGrade;

  /** 成绩状态 */
  protected int status;

  /** 是否通过 */
  private boolean passed;

  /** 操作者 */
  @Size(max = 50)
  private String operator;

  private java.util.Date createdAt;

  /**
   * 百分比描述<br>
   */
  private Short weight;

  public ExamGrade() {
  }

  public ExamGrade(GradeType gradeType, Float score) {
    this.gradeType = gradeType;
    this.score = score;
  }

  public ExamGrade(GradeType gradeType, ExamStatus examStatus, Float score) {
    this.gradeType = gradeType;
    this.examStatus = examStatus;
    this.score = score;
  }

  public CourseGrade getCourseGrade() {
    return courseGrade;
  }

  public void setCourseGrade(CourseGrade courseGrade) {
    this.courseGrade = courseGrade;
  }

  public GradeType getGradeType() {
    return gradeType;
  }

  public void setGradeType(GradeType gradeType) {
    this.gradeType = gradeType;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public String getScoreText() {
    return scoreText;
  }

  public void setScoreText(String scoreText) {
    this.scoreText = scoreText;
  }

  public boolean isPublished() {
    return getStatus() == Published;
  }

  public boolean isConfirmed() {
    return getStatus() >= Confirmed;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  // 大的成绩放前面
  public int compareTo(Grade grade) {
    if (null == getScore()) return 1;
    else if (null == grade.getScore()) return -1;
    return grade.getScore().compareTo(getScore());
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean isPass) {
    this.passed = isPass;
  }

  public ExamStatus getExamStatus() {
    return examStatus;
  }

  public void setExamStatus(ExamStatus examStatus) {
    this.examStatus = examStatus;
  }

  public GradingMode getGradingMode() {
    return gradingMode;
  }

  public void setGradingMode(GradingMode gradingMode) {
    this.gradingMode = gradingMode;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Short getWeight() {
    return weight;
  }

  public void setWeight(Short weight) {
    this.weight = weight;
  }

  @Override
  public Student getStd() {
    return (null == courseGrade) ? null : courseGrade.getStd();
  }

  public java.util.Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.util.Date createdAt) {
    this.createdAt = createdAt;
  }

}
