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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.beangle.commons.collection.CollectUtils;
import org.openurp.base.edu.model.MajorDirection;

/**
 * 原始计划的课程组
 *
 *
 */
@Entity(name = "org.openurp.edu.program.model.MajorCourseGroup")
public class MajorCourseGroup extends AbstractCourseGroup {

  private static final long serialVersionUID = 4144045243297075224L;

  /** 该组针对的专业方向 */
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;

  /** 培养方案 */
  @ManyToOne(targetEntity = MajorPlan.class)
  @JoinColumn(name = "PLAN_ID", updatable = false, insertable = false, nullable = false)
  private CoursePlan plan;

  /** 上级组 */
  @ManyToOne(targetEntity = MajorCourseGroup.class)
  @JoinColumn(name = "PARENT_ID", nullable = true)
  private CourseGroup parent;

  /** 下级节点 */
  @OneToMany(targetEntity = MajorCourseGroup.class, cascade = { CascadeType.ALL })
  @OrderBy("indexno")
  @JoinColumn(name = "PARENT_ID", nullable = true)
  private List<CourseGroup> children = CollectUtils.newArrayList();

  /** 组内课程 */
  @OneToMany(mappedBy = "group", orphanRemoval = true, targetEntity = MajorPlanCourse.class, cascade = {
      CascadeType.ALL })
  private List<PlanCourse> planCourses = CollectUtils.newArrayList();

  public CoursePlan getPlan() {
    return plan;
  }

  public void setPlan(CoursePlan plan) {
    this.plan = plan;
  }

  public CourseGroup getParent() {
    return parent;
  }

  public void setParent(CourseGroup parent) {
    this.parent = parent;
  }

  public List<CourseGroup> getChildren() {
    return children;
  }

  public void setChildren(List<CourseGroup> children) {
    this.children = children;
  }

  public List<PlanCourse> getPlanCourses() {
    return planCourses;
  }

  public void setPlanCourses(List<PlanCourse> planCourses) {
    this.planCourses = planCourses;
  }

  public MajorDirection getDirection() {
    return direction;
  }

  public void setDirection(MajorDirection direction) {
    this.direction = direction;
  }

}
