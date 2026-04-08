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
package org.openurp.base.std.model;

import org.beangle.commons.collection.CollectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.EduLevelBasedObject;
import org.openurp.base.edu.model.Major;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Department;
import org.openurp.base.hr.model.Staff;
import org.openurp.code.std.model.StdType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 学生行政班级信息
 *
 * @since 2005-9-12
 */
@Entity(name = "org.openurp.base.std.model.Squad")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Squad extends EduLevelBasedObject<Long> {

  private static final long serialVersionUID = 6467000522483394459L;

  /**
   * 编码代码
   */
  @Column(unique = true)
  @NotNull
  @Size(max = 32)
  protected String code;

  /**
   * 名称
   */
  @NotNull
  @Size(max = 50)
  protected String name;

  /**
   * 简称
   */
  @Size(max = 50)
  protected String shortName;

  /**
   * 备注
   */
  @Size(max = 500)
  protected String remark;

  /**
   * 年级,形式为yyyy-p
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Grade grade;

  /**
   * 院系
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /**
   * 专业
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private Major major;

  /**
   * 方向
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;
  /**
   * 学生类别
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private StdType stdType;

  /**
   * 计划人数
   */
  private int planCount;

  /**
   * 开始日期
   */
  @NotNull
  private java.sql.Date beginOn;

  /**
   * 结束日期 结束日期包括在有效期内
   */
  @NotNull
  private java.sql.Date endOn;

  /**
   * 学籍有效人数
   */
  @NotNull
  private int stdCount;

  /**
   * 学生列表 班级学生
   */
  @OneToMany(mappedBy = "squad")
  private Set<StudentState> stdStates = CollectUtils.newHashSet();

  @ManyToOne(fetch = FetchType.LAZY)
  private Staff master;

  @ManyToOne(fetch = FetchType.LAZY)
  private Campus campus;

  public Squad() {
  }

  public Squad(Long id) {
    super(id);
  }

  public Squad(Long id, String code, String name) {
    super(id);
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Grade getGrade() {
    return grade;
  }

  public void setGrade(Grade grade) {
    this.grade = grade;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
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

  public int getPlanCount() {
    return planCount;
  }

  public void setPlanCount(int planCount) {
    this.planCount = planCount;
  }

  public StdType getStdType() {
    return stdType;
  }

  public void setStdType(StdType stdType) {
    this.stdType = stdType;
  }

  public java.sql.Date getBeginOn() {
    return beginOn;
  }

  public void setBeginOn(java.sql.Date beginOn) {
    this.beginOn = beginOn;
  }

  public java.sql.Date getEndOn() {
    return endOn;
  }

  public void setEndOn(java.sql.Date endOn) {
    this.endOn = endOn;
  }

  public int getStdCount() {
    return stdCount;
  }

  public void setStdCount(int stdCount) {
    this.stdCount = stdCount;
  }

  public Set<Student> getAllStudents() {
    Set<Student> students = new java.util.HashSet<Student>();
    for (StudentState ss : stdStates) {
      students.add(ss.getStd());
    }
    return students;
  }

  public Set<Student> getStudents(java.sql.Date date) {
    Set<Student> students = new java.util.HashSet<Student>();
    for (StudentState ss : stdStates) {
      if (ss.isValid(date)) students.add(ss.getStd());
    }
    return students;
  }

  public Set<StudentState> getStdStates() {
    return stdStates;
  }

  public void setStdStates(Set<StudentState> stdStates) {
    this.stdStates = stdStates;
  }

  public Campus getCampus() {
    return campus;
  }

  public void setCampus(Campus campus) {
    this.campus = campus;
  }

  public Staff getMaster() {
    return master;
  }

  public void setMaster(Staff master) {
    this.master = master;
  }
}
