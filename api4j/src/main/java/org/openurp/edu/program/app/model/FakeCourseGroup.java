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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.openurp.code.edu.model.CourseType;
import org.openurp.edu.program.model.ExecutiveCourseGroup;

/**
 * 假的课程组
 * 被ExecutivePlanCourseModifyDetailBean使用
 */
@Embeddable
public class FakeCourseGroup implements Serializable {

  private static final long serialVersionUID = 2806576960132247993L;

  /** 课程组ID */
  @Column(name = "group_id")
  private Long id;

  /** 课程类别 */
  @JoinColumn(name = "fake_course_type_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseType courseType;

  public FakeCourseGroup() {
  }

  public FakeCourseGroup(ExecutiveCourseGroup group) {
    if (group != null) {
      this.id = group.getId();
      this.courseType = group.getCourseType();
    }
  }

  public CourseType getCourseType() {
    return courseType;
  }

  public void setCourseType(CourseType courseType) {
    this.courseType = courseType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
