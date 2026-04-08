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

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Target;
import org.openurp.code.edu.model.Language;
import org.openurp.code.edu.model.CourseAbilityRate;
import org.openurp.code.edu.model.CourseType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共共享课程组(默认实现)
 */
@Entity(name = "org.openurp.edu.program.model.ShareCourseGroup")
public class ShareCourseGroup extends LongIdObject implements Cloneable {

  private static final long serialVersionUID = -6481752967441999886L;

  /**
   * 公共共享计划
   */
  @Target(SharePlan.class)
  @ManyToOne(fetch = FetchType.LAZY)
  private SharePlan plan;

  /**
   * index no
   */
  @Size(max = 30)
  @NotNull
  private String indexno;

  /**
   * 课程类别
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected CourseType courseType;

  /**
   * 上级组
   */
  @Target(ShareCourseGroup.class)
  @ManyToOne(fetch = FetchType.LAZY)
  private ShareCourseGroup parent;

  /**
   * 下级组列表
   */
  @OneToMany(targetEntity = ShareCourseGroup.class, mappedBy = "parent")
  @OrderBy("indexno")
  @Cascade(CascadeType.ALL)
  private List<ShareCourseGroup> children = CollectUtils.newArrayList();

  /**
   * 课程列表
   */
  @OneToMany(mappedBy = "group", targetEntity = SharePlanCourse.class)
  @Cascade(CascadeType.ALL)
  protected List<SharePlanCourse> planCourses = CollectUtils.newArrayList();

  /**
   * 对应外语语种
   */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Language language;

  /**
   * 对应课程能力等级
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseAbilityRate abilityRate;

  public String getIndexno() {
    return indexno;
  }

  public void setIndexno(String indexno) {
    this.indexno = indexno;
  }

  public CourseType getCourseType() {
    return courseType;
  }

  public void setCourseType(CourseType courseType) {
    this.courseType = courseType;
  }

  public Object clone() throws CloneNotSupportedException {
    ShareCourseGroup courseGroup = (ShareCourseGroup) super.clone();
    courseGroup.setId(null);
    // 克隆子组
    List<ShareCourseGroup> groups = courseGroup.getChildren();
    List<ShareCourseGroup> groupClones = CollectUtils.newArrayList();
    for (ShareCourseGroup cg : groups) {
      ShareCourseGroup groupClone = (ShareCourseGroup) cg.clone();
      groupClones.add(groupClone);
      groupClone.setParent(courseGroup);
    }
    courseGroup.setChildren(groupClones);
    courseGroup.setPlanCourses(new ArrayList<SharePlanCourse>());
    // 克隆课程
    for (SharePlanCourse planCourse : getPlanCourses()) {
      courseGroup.addPlanCourse((SharePlanCourse) planCourse.clone());
    }
    // 克隆关系
    // courseGroup.setRelation((GroupRelation) getRelation().clone());
    return courseGroup;
  }

  public void addPlanCourse(SharePlanCourse planCourse) {
    for (SharePlanCourse planCourse1 : getPlanCourses()) {
      if (planCourse.getCourse().equals(planCourse1.getCourse())) {
        return;
      }
    }
    planCourse.setGroup(this);
    getPlanCourses().add(planCourse);
  }

  public Object cloneToExecuteCourseGroup() {
    ExecutiveCourseGroup courseGroup = Model.newInstance(ExecutiveCourseGroup.class);
    courseGroup.setCourseType(getCourseType());
    courseGroup.setIndexno(getIndexno());
    courseGroup.setChildren(new ArrayList<CourseGroup>());
    courseGroup.setPlanCourses(new ArrayList<PlanCourse>());
    courseGroup.setId(null);
    return courseGroup;
  }

  public void setPlanCourses(List<SharePlanCourse> planCourses) {
    this.planCourses = planCourses;
  }

  public List<ShareCourseGroup> getChildren() {
    return children;
  }

  public void setChildren(List<ShareCourseGroup> children) {
    this.children = children;
  }

  public SharePlan getPlan() {
    return plan;
  }

  public void setPlan(SharePlan plan) {
    this.plan = plan;
  }

  public ShareCourseGroup getParent() {
    return parent;
  }

  public void setParent(ShareCourseGroup parent) {
    this.parent = parent;
  }

  /**
   * 添加计划课程
   */
  public void addPlanCourses(List<SharePlanCourse> givenPlanCourses) {
    for (SharePlanCourse element : givenPlanCourses) {
      boolean finded = false;
      for (SharePlanCourse element2 : planCourses) {
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

  public void updateCoursePlan(SharePlan plan) {
    setPlan(plan);
    if (getChildren() != null) {
      for (ShareCourseGroup group : getChildren()) {
        group.updateCoursePlan(plan);
      }
    }
  }

  public List<SharePlanCourse> getPlanCourses() {
    return this.planCourses;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public CourseAbilityRate getAbilityRate() {
    return abilityRate;
  }

  public void setAbilityRate(CourseAbilityRate abilityRate) {
    this.abilityRate = abilityRate;
  }

}
