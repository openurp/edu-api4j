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
package org.openurp.edu.program.app.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.code.edu.model.CourseType;

/**
 * 专业计划课程组修改明细
 */
@MappedSuperclass
public abstract class ExecutiveCourseGroupModifyDetail extends LongIdObject {

  private static final long serialVersionUID = 5552733977609925991L;

  /** 课程组类别 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected CourseType courseType;

  /** 子组要求 */
  private short subCount;

  /** 总学分 */
  protected float credits;

  /** 课程组每学期对应学分 */
  @Column(length = 50)
  protected String termCredits;

  /** 父课程组 */
  protected FakeCourseGroup parent;

  /** 备注 */
  @Column(length = 500)
  protected String remark;

  public CourseType getCourseType() {
    return courseType;
  }

  public void setCourseType(CourseType courseType) {
    this.courseType = courseType;
  }

  public float getCredits() {
    return credits;
  }

  public void setCredits(float credits) {
    this.credits = credits;
  }

  public String getTermCredits() {
    return termCredits;
  }

  public void setTermCredits(String termCredits) {
    this.termCredits = termCredits;
  }

  public FakeCourseGroup getParent() {
    return parent;
  }

  public void setParent(FakeCourseGroup parent) {
    this.parent = parent;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public short getSubCount() {
    return subCount;
  }

  public void setSubCount(short subCount) {
    this.subCount = subCount;
  }

  public abstract ExecutiveCourseGroupModify getApply();

  public abstract void setApply(ExecutiveCourseGroupModify apply);
}
