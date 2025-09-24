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
package org.openurp.std.info.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationMode;
import org.openurp.code.edu.model.EnrollMode;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;

/**
 * 录取信息
 *
 * @author duant
 */
@Entity(name = "org.openurp.std.info.model.Admission")
public class Admission extends StudentInfoBean {

  private static final long serialVersionUID = -4421799484795106018L;

  /** 录取年月 */
  private java.sql.Date enrollOn;

  /** 录取专业 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Major major;

  /** 录取方向 */
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;

  /** 录取院系 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /** 录取通知书号 */
  private String letterNo;

  /** 录取第几志愿 */
  private int admissionIndex;

  /** 入学方式 */
  @ManyToOne(fetch = FetchType.LAZY)
  private EnrollMode enrollMode;

  /** 培养方式 */
  @ManyToOne(fetch = FetchType.LAZY)
  private EducationMode educationMode;

  public java.sql.Date getEnrollOn() {
    return enrollOn;
  }

  public void setEnrollOn(java.sql.Date enrollOn) {
    this.enrollOn = enrollOn;
  }

  public Major getMajor() {
    return major;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public MajorDirection getDirection() {
    return direction;
  }

  public void setDirection(MajorDirection direction) {
    this.direction = direction;
  }

  public String getLetterNo() {
    return letterNo;
  }

  public void setLetterNo(String letterNo) {
    this.letterNo = letterNo;
  }

  public int getAdmissionIndex() {
    return admissionIndex;
  }

  public void setAdmissionIndex(int admissionIndex) {
    this.admissionIndex = admissionIndex;
  }

  public EnrollMode getEnrollMode() {
    return enrollMode;
  }

  public void setEnrollMode(EnrollMode enrollMode) {
    this.enrollMode = enrollMode;
  }

  public EducationMode getEducationMode() {
    return educationMode;
  }

  public void setEducationMode(EducationMode educationMode) {
    this.educationMode = educationMode;
  }

}
