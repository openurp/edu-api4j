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

import org.beangle.commons.lang.Objects;
import org.beangle.orm.hibernate.udt.WeekState;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import org.openurp.base.time.Terms;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 原始计划的计划课程
 */
@Entity(name = "org.openurp.edu.program.model.MajorPlanCourse")
public class MajorPlanCourse extends AbstractPlanCourse {

  private static final long serialVersionUID = -2091355773150181171L;

  /**
   * 课程组
   */
  @Target(MajorCourseGroup.class)
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseGroup group;

  /**
   * <pre>
   * 建议修读学期，该字段只在培养计划是完全学分制时才有用。
   * 和{@link #getTerms()}不同，{@link #getTerms()}指导开课，本字段指导学生修读
   * </pre>
   */
  @NotNull
  @Type(type = "org.openurp.base.time.hibernate.TermsType")
  protected Terms suggestTerms = Terms.Empty;

  @NotNull
  @Type(type = "org.beangle.orm.hibernate.udt.WeekStateType")
  private WeekState weekstate = WeekState.Zero;

  public Terms getSuggestTerms() {
    return suggestTerms;
  }

  public void setSuggestTerms(Terms suggestTerms) {
    this.suggestTerms = suggestTerms;
  }

  public WeekState getWeekstate() {
    return weekstate;
  }

  public void setWeekstate(WeekState weekstate) {
    this.weekstate = weekstate;
  }

  public CourseGroup getGroup() {
    return group;
  }

  public void setGroup(CourseGroup group) {
    this.group = group;
  }

  public boolean isSame(Object object) {
    if (!(object instanceof MajorPlanCourse)) {
      return false;
    }
    MajorPlanCourse rhs = (MajorPlanCourse) object;
    return Objects.equalsBuilder().add(terms, rhs.terms).add(remark, rhs.remark)
        .add(course.getId(), rhs.course.getId())
        .add(id, rhs.id).isEquals();
  }

  @Override
  public String toString() {
    return "MajorPlanCourse [group=" + group + ", course=" + course + ", terms=" + terms + ", compulsory="
        + compulsory + "]";
  }
}
