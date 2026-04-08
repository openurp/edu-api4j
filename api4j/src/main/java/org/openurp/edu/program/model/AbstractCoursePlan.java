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
import org.openurp.code.edu.model.CourseType;

import javax.persistence.CascadeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象课程方案
 * </p>
 *
 * @since 2009
 */
@MappedSuperclass
public abstract class AbstractCoursePlan extends LongIdObject implements CoursePlan {

  private static final long serialVersionUID = 1606351182470625309L;
  /**
   * 培养方案
   */
  @NotNull
  @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  protected Program program;
  /**
   * 要求学分
   */
  @NotNull
  private float credits;
  /**
   * 课时
   */
  private int creditHours;
  /**
   * 课时比例
   */
  private String hourRatios;

  private java.util.Date updatedAt;

  public float getCredits() {
    return credits;
  }

  public void setCredits(float credits) {
    this.credits = credits;
  }

  public int getEndTerm() {
    return program.getEndTerm();
  }

  public int getStartTerm() {
    return program.getStartTerm();
  }

  public int getTermsCount() {
    return getEndTerm() - getStartTerm() + 1;
  }

  public void addGroup(CourseGroup group) {
    if (null == getGroups()) {
      setGroups(new ArrayList<CourseGroup>());
    }
    getGroups().add(group);
    group.updateCoursePlan(this);
  }

  public List<CourseGroup> getTopCourseGroups() {
    if (getGroups() == null) {
      return new ArrayList<CourseGroup>();
    }
    List<CourseGroup> res = new ArrayList<CourseGroup>();
    for (CourseGroup group : getGroups()) {
      if (group != null && group.getParent() == null) res.add(group);
    }
    return res;
  }

  public CourseGroup getGroup(CourseType type) {
    if (null == getGroups()) return null;
    for (CourseGroup group : getGroups()) {
      if (group.getCourseType().equals(type)) return group;
    }
    return null;
  }

  public boolean isNumericTerm() {
    return true;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  @Override
  public Date getBeginOn() {
    return program.getBeginOn();
  }

  @Override
  public Date getEndOn() {
    return program.getEndOn();
  }

  public java.util.Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.util.Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public int getCreditHours() {
    return creditHours;
  }

  public void setCreditHours(int creditHours) {
    this.creditHours = creditHours;
  }

  public String getHourRatios() {
    return hourRatios;
  }

  public void setHourRatios(String hourRatios) {
    this.hourRatios = hourRatios;
  }
}
