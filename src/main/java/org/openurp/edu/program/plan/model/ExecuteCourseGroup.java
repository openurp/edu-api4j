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
package org.openurp.edu.program.plan.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.edu.base.code.model.CourseType;
import org.openurp.edu.base.model.Direction;

/**
 * 专业计划课程组.
 */
@Entity(name = "org.openurp.edu.program.plan.model.ExecuteCourseGroup")
@Cacheable
@Cache(region = "edu.course", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExecuteCourseGroup extends AbstractCourseGroup {

  private static final long serialVersionUID = -6804554057069134031L;

  /** 自定义名称 */
  @Size(max = 40)
  private String alias;

  /** 该组针对的专业方向 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Direction direction;

  public boolean isLeafGroup() {
    return null != alias;
  }

  @Override
  public String getName() {
    StringBuilder sb = new StringBuilder();
    if (null != courseType) sb.append(courseType.getName());
    if (null != alias) sb.append(" ").append(alias);
    return sb.toString();
  }

  /** 专业计划 */
  @ManyToOne(targetEntity = ExecutePlan.class)
  @JoinColumn(name = "plan_id", updatable = false, insertable = false, nullable = false)
  private CoursePlan plan;

  /** 上级组 */
  @ManyToOne(targetEntity = ExecuteCourseGroup.class)
  @JoinColumn(name = "parent_id", nullable = true)
  private CourseGroup parent;

  /** 下级组列表 */
  @OneToMany(targetEntity = ExecuteCourseGroup.class, cascade = { CascadeType.ALL })
  @OrderBy("indexno")
  @JoinColumn(name = "parent_id", nullable = true)
  @Cache(region = "edu.course", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<CourseGroup> children = CollectUtils.newArrayList();

  /** 计划课程列表 */
  @OneToMany(mappedBy = "group", orphanRemoval = true, targetEntity = ExecutePlanCourse.class, cascade = {
      CascadeType.ALL })
  @Cache(region = "edu.course", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<PlanCourse> planCourses = CollectUtils.newArrayList();

  public void setPlanCourses(List<PlanCourse> planCourses) {
    this.planCourses = planCourses;
  }

  public List<CourseGroup> getChildren() {
    return children;
  }

  public void setChildren(List<CourseGroup> children) {
    this.children = children;
  }

  public CoursePlan getPlan() {
    return plan;
  }

  public void setPlan(CoursePlan plan) {
    this.plan = plan;
  }

  /**
   * 添加计划课程
   */
  public void addPlanCourses(List<PlanCourse> givenPlanCourses) {
    for (PlanCourse element : givenPlanCourses) {
      boolean finded = false;
      for (PlanCourse element2 : planCourses) {
        if (element.getCourse().getId().equals(element2.getCourse().getId())) {
          finded = true;
          break;
        }
      }
      if (!finded) {
        element.setGroup(this);
        planCourses.add(element);
      }
    }
  }

  public void updateCoursePlan(CoursePlan plan) {
    setPlan(plan);
    if (getChildren() != null) {
      for (CourseGroup group : getChildren()) {
        group.updateCoursePlan(plan);
      }
    }
  }

  /**
   * 得到全部有效课程.
   */
  public Object clone() throws CloneNotSupportedException {
    ExecuteCourseGroup executeCourseGroup = (ExecuteCourseGroup) super.clone();
    executeCourseGroup.setId(null);
    executeCourseGroup.setParent(null);
    executeCourseGroup.setChildren(new ArrayList<CourseGroup>());
    executeCourseGroup.setPlanCourses(new ArrayList<PlanCourse>());
    // 克隆引用共享组
    return executeCourseGroup;
  }

  public List<PlanCourse> getExecutePlanCourses() {
    return planCourses;
  }

  public List<PlanCourse> getPlanCourses() {
    return planCourses;
  }

  public CourseGroup getParent() {
    return parent;
  }

  public void setParent(CourseGroup parent) {
    this.parent = parent;
  }

  public boolean equals(Object object) {
    if (!(object instanceof ExecuteCourseGroup)) { return false; }
    ExecuteCourseGroup rhs = (ExecuteCourseGroup) object;
    return Objects.equalsBuilder().add(this.id, rhs.id).isEquals();
  }

  private CourseType getParentCourseType() {
    if (parent == null) {
      return null;
    } else {
      return parent.getCourseType();
    }
  }

  public boolean isSameGroup(Object object) {
    if (!(object instanceof ExecuteCourseGroup)) { return false; }
    ExecuteCourseGroup other = (ExecuteCourseGroup) object;
    // it will handle null value
    return Objects.equalsBuilder().add(getCredits(), other.getCredits())
        .add(getCourseType(), other.getCourseType()).add(getParentCourseType(), other.getParentCourseType())
        .add(getRemark(), other.getRemark()).add(getTermCredits(), other.getTermCredits())
        .add(getPlanCourses(), other.getPlanCourses()).isEquals();
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public String toString() {
    return "ExecuteCourseGroup [alias=" + alias + ", direction=" + direction + ", parent=" + parent
        + ", courseType=" + courseType + "]";
  }

}