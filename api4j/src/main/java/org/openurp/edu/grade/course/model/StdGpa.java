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

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.std.model.Student;

/**
 * 学生历学期的成绩绩点
 *
 * @see StdSemesterGpa
 */
@Entity(name = "org.openurp.edu.grade.course.model.StdGpa")
public class StdGpa extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -5674629758471007484L;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 学生 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /**
   * 每学期平均绩点
   *
   * @see StdSemesterGpa
   */
  @OneToMany(mappedBy = "stdGpa", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<StdSemesterGpa> semesterGpas;

  /**
   * 每学年平均绩点
   *
   * @see StdSemesterGpa
   */
  @OneToMany(mappedBy = "stdGpa", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<StdYearGpa> yearGpas;
  /**
   * 总平均绩点
   */
  private Float gpa;

  /**
   * 平均分
   */
  private Float wms;

  /**
   * 获得学分
   */
  private Float credits;

  /** 修读学分 */
  private Float totalCredits;
  /**
   * 成绩的门数
   */
  private int gradeCount;

  /** 查询类缓存 */
  private transient Map<Semester, StdSemesterGpa> semesterGpaCache;

  private transient Map<String, StdYearGpa> yearGpaCache;

  public StdGpa() {
    super();
  }

  public StdGpa(Long id) {
    super(id);
  }

  public StdGpa(Student std) {
    this.std = std;
    this.project = std.getProject();
    this.semesterGpas = CollectUtils.newArrayList();
    this.yearGpas = CollectUtils.newArrayList();
    this.credits = 0f;
    this.gradeCount = 0;
    this.wms = new Float(0);
    this.gpa = new Float(0);
  }

  public Float getGpa(Semester semester) {
    StdSemesterGpa gpterm = getStdTermGpa(semester);
    if (null == gpterm) return null;
    else return gpterm.getGpa();
  }

  public StdSemesterGpa getStdTermGpa(Semester semester) {
    if (null == semesterGpaCache || semesterGpaCache.size() != semesterGpas.size()) {
      semesterGpaCache = CollectUtils.newHashMap();
      for (StdSemesterGpa gpterm : getSemesterGpas())
        semesterGpaCache.put(gpterm.getSemester(), gpterm);
    }
    return semesterGpaCache.get(semester);
  }

  public StdYearGpa getYearGpa(String schoolYear) {
    if (null == yearGpaCache || yearGpaCache.size() != yearGpas.size()) {
      yearGpaCache = CollectUtils.newHashMap();
      for (StdYearGpa gpYear : getYearGpas())
        yearGpaCache.put(gpYear.getSchoolYear(), gpYear);
    }
    return yearGpaCache.get(schoolYear);
  }

  public void add(StdSemesterGpa stdTermGpa) {
    if (null == semesterGpas) semesterGpas = CollectUtils.newArrayList();
    stdTermGpa.setStdGpa(this);
    semesterGpas.add(stdTermGpa);
  }

  public void add(StdYearGpa stdYearGpa) {
    if (null == yearGpas) this.yearGpas = CollectUtils.newArrayList();
    stdYearGpa.setStdGpa(this);
    yearGpas.add(stdYearGpa);
  }

  public List<StdSemesterGpa> getSemesterGpas() {
    return semesterGpas;
  }

  public void setSemesterGpas(List<StdSemesterGpa> semesterGpas) {
    this.semesterGpas = semesterGpas;
  }

  public List<StdYearGpa> getYearGpas() {
    return yearGpas;
  }

  public void setYearGpas(List<StdYearGpa> yearGpas) {
    this.yearGpas = yearGpas;
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student student) {
    this.std = student;
  }

  public Float getGpa() {
    return gpa;
  }

  public void setGpa(Float gpa) {
    this.gpa = gpa;
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

  public Float getWms() {
    return wms;
  }

  public void setWms(Float wms) {
    this.wms = wms;
  }

  public Float getTotalCredits() {
    return totalCredits;
  }

  public void setTotalCredits(Float totalCredits) {
    this.totalCredits = totalCredits;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

}
