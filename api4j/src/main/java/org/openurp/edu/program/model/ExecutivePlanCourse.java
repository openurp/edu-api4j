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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import org.openurp.base.time.Terms;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 专业计划课程
 */
@Entity(name = "org.openurp.edu.program.model.ExecutivePlanCourse")
@Cacheable
@Cache(region = "edu.course", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExecutivePlanCourse extends AbstractPlanCourse {

  private static final long serialVersionUID = 6223259360999867620L;

  /**
   * 课程组
   */
  @Target(ExecutiveCourseGroup.class)
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseGroup group;


  @NotNull
  @Type(type = "org.beangle.orm.hibernate.udt.WeekStateType")
  private WeekState weekstate = WeekState.Zero;

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
    if (!(object instanceof ExecutivePlanCourse)) {
      return false;
    }
    ExecutivePlanCourse rhs = (ExecutivePlanCourse) object;
    return Objects.equalsBuilder().add(terms, rhs.terms).add(remark, rhs.remark)
        .add(course.getId(), rhs.course.getId())
        .add(id, rhs.id).isEquals();
  }

  @Override
  public String toString() {
    return "ExecutivePlanCourse [group=" + group + ", course=" + course + ", terms=" + terms + ", compulsory="
        + compulsory + "]";
  }
}
