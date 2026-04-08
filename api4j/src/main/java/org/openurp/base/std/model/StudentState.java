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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.StudentStatus;

/**
 * 学籍状态日志
 */
@Entity(name = "org.openurp.base.std.model.StudentState")
public class StudentState extends LongIdObject {

  private static final long serialVersionUID = -321115982366299767L;

  /** 学生 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /** 年级 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Grade grade;

  /** 管理院系 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /** 专业 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Major major;

  /** 方向 */
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;

  /** 行政班级 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Squad squad;

  /** 是否在校 */
  @NotNull
  private boolean inschool;

  /** 学籍状态 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id")
  // FIXME very versy strange ,why mapped to std_status_id
  // need update change foreignKeyColumnName method in RailsNamingStrategy
  private StudentStatus status;

  /** 校区 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected Campus campus;

  /** 起始日期 */
  @NotNull
  private java.sql.Date beginOn;

  @NotNull
  /** 结束日期 */
  private java.sql.Date endOn;

  /** 备注 */
  private String remark;

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
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

  public boolean isInschool() {
    return inschool;
  }

  public void setInschool(boolean inschool) {
    this.inschool = inschool;
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

  public Squad getSquad() {
    return squad;
  }

  public void setSquad(Squad squad) {
    this.squad = squad;
  }

  public StudentStatus getStatus() {
    return status;
  }

  public void setStatus(StudentStatus status) {
    this.status = status;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Campus getCampus() {
    return campus;
  }

  public void setCampus(Campus campus) {
    this.campus = campus;
  }

  public boolean isValid(java.util.Date date) {
    if (date.before(beginOn)) return false;
    if (null != endOn && date.after(endOn)) return false;
    return true;
  }
}
