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

import org.beangle.commons.entity.Entity;
import org.openurp.base.time.Terms;
import org.openurp.code.edu.model.CourseType;

import java.util.List;

/**
 * 课程设置中的课程组.
 * </p>
 * <li>1)对应计划</li>
 * <li>2)课程类型</li>
 * <li>3)要求学分</li>
 * <li>4)是否必修课</li>
 * <li>5)父组</li>
 * <li>6)子组集合</li>
 * <li>7)组内所有的课程</li>
 * <li>8)备注</li>
 */
public interface CourseGroup extends Entity<Long>, Cloneable, Comparable<CourseGroup> {

  /**
   * @return
   */
  public String getName();

  /**
   * 获得课程方案
   *
   * @return 课程方案
   */
  public CoursePlan getPlan();

  /**
   * 设置课程方案
   *
   * @param plan 课程方案
   */
  public void setPlan(CoursePlan plan);

  /**
   * 获得上级组
   *
   * @return 上级组
   */
  public CourseGroup getParent();

  /**
   * 设置上级组
   *
   * @param courseGroup 上级组
   */
  public void setParent(CourseGroup courseGroup);

  /**
   * 获得子节点集合.
   *
   * @return 子节点集合
   */
  public List<CourseGroup> getChildren();

  /**
   * 设置子节点集合
   *
   * @param children 子节点集合
   */
  public void setChildren(List<CourseGroup> children);

  public short getSubCount();

  public void setSubCount(short subCount);

  /**
   * 获得课程类别.
   *
   * @return 课程类别
   */
  public CourseType getCourseType();

  /**
   * 设置课程类别
   *
   * @param courseType 课程类别
   */
  public void setCourseType(CourseType courseType);

  /**
   * 要求组内要求总学分
   *
   * @return 组内要求总学分
   */
  public float getCredits();

  /**
   * 设置组内要求总学分
   *
   * @param credits 组内要求总学分
   */
  public void setCredits(float credits);

  public List<PlanCourse> getPlanCourses();

  /**
   * 设置组内计划课程
   *
   * @param planCourses 组内计划课程
   */
  public void setPlanCourses(List<PlanCourse> planCourses);

  /**
   * 是否自动累计学分
   */
  public boolean isAutoAddup();

  /**
   * 添加多个计划课程
   *
   * @param planCourses 多个计划课程
   */
  public void addPlanCourses(List<PlanCourse> planCourses);

  /**
   * 添加计划课程
   *
   * @param planCourse 计划课程
   */
  public void addPlanCourse(PlanCourse planCourse);

  /**
   * 添加子组
   *
   * @param group 子组
   */
  public void addChildGroup(CourseGroup group);

  /**
   * 克隆
   *
   * @return 克隆后的组
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException;

  /**
   * 更新对应的课程计划
   *
   * @param plan
   */
  void updateCoursePlan(CoursePlan plan);

  /**
   * 获得备注.
   *
   * @return 备注
   */
  public String getRemark();

  /**
   * 设置备注
   *
   * @param remark 备注
   */
  public void setRemark(String remark);

  /**
   * 删除计划课程
   *
   * @param course 计划课程
   */
  public void removePlanCourse(PlanCourse course);

  /**
   * 获得每学期学分
   *
   * @return 每学期学分
   */
  public String getTermCredits();

  /**
   * 设置每学期学分
   *
   * @param termCredits 每学期学分
   */
  public void setTermCredits(String termCredits);

  public String getIndexno();

  public void setIndexno(String indexno);

  public int getIndex();

  public Terms getTerms();

  public void setTerms(Terms terms);

  public int getCreditHours();

  public void setCreditHours(int creditHours);

  public String getHourRatios();

  public void setHourRatios(String ratios);

}
