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
package org.openurp.edu.grade.course.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.edu.model.Semester;

/**
 * 每学期绩点
 */
@Entity(name = "org.openurp.edu.grade.course.model.StdSemesterGpa")
public class StdSemesterGpa extends LongIdObject {

  private static final long serialVersionUID = 8305350708094614459L;

  /** 学生绩点 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private StdGpa stdGpa;

  /** 学期 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Semester semester;

  /** 平均绩点 */
  private Float gpa;

  /** 平均分 */
  private Float wms;

  /** 获得学分 */
  private Float credits;

  /** 修读学分 */
  private Float totalCredits;

  /** 总成绩数量 */
  private int gradeCount;

  public StdSemesterGpa() {
    this.credits = 0f;
    this.gradeCount = 0;
    this.wms = new Float(0);
    this.gpa = new Float(0);
  }

  public StdSemesterGpa(Semester semester, Float gpa) {
    this.semester = semester;
    this.gpa = gpa;
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  public Float getGpa() {
    return gpa;
  }

  public void setGpa(Float gpa) {
    this.gpa = gpa;
  }

  public Float getWms() {
    return wms;
  }

  public void setWms(Float wms) {
    this.wms = wms;
  }

  public int getGradeCount() {
    return gradeCount;
  }

  public void setGradeCount(int gradeCount) {
    this.gradeCount = gradeCount;
  }

  public Float getCredits() {
    return credits;
  }

  public void setCredits(Float credits) {
    this.credits = credits;
  }

  public StdGpa getStdGpa() {
    return stdGpa;
  }

  public void setStdGpa(StdGpa stdGpa) {
    this.stdGpa = stdGpa;
  }

  public Float getTotalCredits() {
    return totalCredits;
  }

  public void setTotalCredits(Float totalCredits) {
    this.totalCredits = totalCredits;
  }

}
