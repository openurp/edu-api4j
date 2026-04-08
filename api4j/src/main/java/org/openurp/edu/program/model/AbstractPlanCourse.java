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
package org.openurp.edu.program.model;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Type;
import org.openurp.base.edu.model.Course;
import org.openurp.base.time.Terms;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 抽象计划内课程
 * </p>
 *
 * @since 2009
 */
@MappedSuperclass
public abstract class AbstractPlanCourse extends LongIdObject implements PlanCourse, Cloneable {

  private static final long serialVersionUID = 8777711425517220702L;

  /**
   * 课程
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected Course course;

  /**
   * 开课学期
   */
  @NotNull
  @Type(type = "org.openurp.base.time.hibernate.TermsType")
  protected Terms terms = Terms.Empty;

  /**
   * 是否必修
   */
  protected boolean compulsory;

  /**
   * 备注
   */
  @Size(max = 500)
  protected String remark;

  public boolean isCompulsory() {
    return compulsory;
  }

  public void setCompulsory(boolean compulsory) {
    this.compulsory = compulsory;
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractPlanCourse planCourse = (AbstractPlanCourse) super.clone();
    planCourse.setId(null);
    return planCourse;
  }

  public float getCredits() {
    if (null == getGroup()) return course.getDefaultCredits();
    else return course.getCredits(getGroup().getPlan().getProgram().getLevel());
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

  public Terms getTerms() {
    return terms;
  }

  public void setTerms(Terms terms) {
    this.terms = terms;
  }

}
