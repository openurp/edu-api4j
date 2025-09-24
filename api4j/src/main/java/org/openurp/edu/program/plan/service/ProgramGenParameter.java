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
package org.openurp.edu.program.plan.service;

import java.sql.Date;

import org.openurp.base.model.Department;
import org.openurp.base.std.model.Grade;
import org.openurp.code.edu.model.Degree;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.edu.model.StudyType;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Student;

/**
 * 复制的是Program，不是 Plan
 */
public class ProgramGenParameter {

  private String name;

  private Grade grade;

  private EducationLevel level;

  private StdType stdType;

  private Department department;

  private Major major;

  private MajorDirection direction;

  private Date beginOn;

  private Date endOn;

  private float duration;

  private StudyType studyType;

  private Degree degree;

  private Student student;
  private int termsCount;

  public int getTermsCount() {
    return termsCount;
  }

  public void setTermsCount(int termsCount) {
    this.termsCount = termsCount;
  }

  public void setGrade(Grade grade) {
    this.grade = grade;
  }

  public void setLevel(EducationLevel level) {
    this.level = level;
  }

  public void setStdType(StdType stdType) {
    this.stdType = stdType;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public void setDirection(MajorDirection direction) {
    this.direction = direction;
  }

  public Grade getGrade() {
    return grade;
  }

  public EducationLevel getLevel() {
    return level;
  }

  public StdType getStdType() {
    return stdType;
  }

  public Major getMajor() {
    return major;
  }

  public MajorDirection getDirection() {
    return direction;
  }

  public ProgramGenParameter() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public float getDuration() {
    return duration;
  }

  public void setDuration(float duration) {
    this.duration = duration;
  }

  public StudyType getStudyType() {
    return studyType;
  }

  public void setStudyType(StudyType studyType) {
    this.studyType = studyType;
  }

  public Degree getDegree() {
    return degree;
  }

  public void setDegree(Degree degree) {
    this.degree = degree;
  }

  public Student getStudent() {
    return student;
  }

  public Date getBeginOn() {
    return beginOn;
  }

  public void setBeginOn(Date beginOn) {
    this.beginOn = beginOn;
  }

  public Date getEndOn() {
    return endOn;
  }

  public void setEndOn(Date endOn) {
    this.endOn = endOn;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

}
