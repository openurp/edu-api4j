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

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Grade;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.Course;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Project;

/**
 * 专业替代课程
 */
@Entity(name = "org.openurp.edu.program.model.MajorAlternativeCourse")
@Table(name = "major_alt_courses")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
public class MajorAlternativeCourse extends AbstractCourseSubstitution {

  private static final long serialVersionUID = 5820298588618272410L;
  /** 年级 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Grade fromGrade;

  /** 年级 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Grade toGrade;

  /** 项目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 院系 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /** 学生类别 */
  @ManyToOne(fetch = FetchType.LAZY)
  private StdType stdType;

  /** 专业 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Major major;

  /** 方向 */
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;

  /** 备注 */
  @Size(max = 300)
  private String remark;

  /** 被替代的课程 */
  @ManyToMany
  @JoinColumn(nullable = false)
  @JoinTable(name = "major_alt_courses_olds", joinColumns = @JoinColumn(name = "major_alt_course_id"))
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
  private Set<Course> olds = CollectUtils.newHashSet();

  /** 已替代的课程 */
  @ManyToMany
  @JoinColumn(nullable = false)
  @JoinTable(name = "major_alt_courses_news", joinColumns = @JoinColumn(name = "major_alt_course_id"))
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
  private Set<Course> news = CollectUtils.newHashSet();

  public Grade getFromGrade() {
    return fromGrade;
  }

  public void setFromGrade(Grade fromGrade) {
    this.fromGrade = fromGrade;
  }

  public Grade getToGrade() {
    return toGrade;
  }

  public void setToGrade(Grade toGrade) {
    this.toGrade = toGrade;
  }

  public Major getMajor() {
    return major;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public MajorDirection getDirection() {
    return direction;
  }

  public void setDirection(MajorDirection direction) {
    this.direction = direction;
  }

  public StdType getStdType() {
    return stdType;
  }

  public void setStdType(StdType stdType) {
    this.stdType = stdType;
  }

  public Set<Course> getOlds() {
    return olds;
  }

  public void setOlds(Set<Course> olds) {
    this.olds = olds;
  }

  public Set<Course> getNews() {
    return news;
  }

  public void setNews(Set<Course> news) {
    this.news = news;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }
}
