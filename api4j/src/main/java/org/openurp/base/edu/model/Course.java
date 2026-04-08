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
package org.openurp.base.edu.model;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Iterator;
import java.util.List;

/**
 * 课程基本信息
 *
 * @since 2008-09-24
 */
@Entity(name = "org.openurp.base.edu.model.Course")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
public class Course extends ProjectBasedObject<Long> implements Comparable<Course> {

  private static final long serialVersionUID = 732786365746405676L;

  /**
   * 课程代码
   */
  @Column(unique = true)
  @NotNull
  @Size(max = 32)
  protected String code;

  /**
   * 课程名称
   */
  @NotNull
  @Size(max = 255)
  protected String name;

  /**
   * 课程英文名
   */
  @Size(max = 300)
  protected String enName;

  /**
   * 培养层次
   */
  @OneToMany(mappedBy = "course", cascade = {CascadeType.ALL}, orphanRemoval = true)
  private List<CourseLevel> levels = CollectUtils.newArrayList();

  /**
   * 学分
   */
  private float defaultCredits;

  /**
   * 学时/总课时
   */
  @NotNull
  private int creditHours;

  /**
   * 分类课时
   */
  @OneToMany(mappedBy = "course", cascade = {CascadeType.ALL}, orphanRemoval = true)
  private List<CourseHour> hours = CollectUtils.newArrayList();

  /**
   * 分类课时
   */
  @OneToMany(mappedBy = "course", cascade = {CascadeType.ALL}, orphanRemoval = true)
  private List<CourseJournal> journals = CollectUtils.newArrayList();
  /**
   * 周课时
   */
  private int weekHours;

  /**
   * 周数
   */
  private Integer weeks;

  /**
   * 院系
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /**
   * 建议课程类别
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseType courseType;

  /**
   * 课程性质
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseNature nature;

  /**
   * 考试方式
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamMode examMode;

  /**
   * 成绩记录方式
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private GradingMode gradingMode;

  private java.sql.Date beginOn;

  private java.sql.Date endOn;

  /**
   * 是否有补考
   */
  private boolean hasMakeup;

  /**
   * 是否计算绩点
   */
  private boolean calgp;

  /**
   * 课程备注
   */
  @Size(max = 500)
  protected String remark;

  public Course() {
    super();
  }

  public Course(Long id) {
    super(id);
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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public float getDefaultCredits() {
    return defaultCredits;
  }

  public void setDefaultCredits(float defaultCredits) {
    this.defaultCredits = defaultCredits;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public int compareTo(Course other) {
    return Objects.compareBuilder().add(getCode(), other.getCode()).toComparison();
  }

  public float getCredits(EducationLevel level) {
    for (CourseLevel l : levels) {
      if (l.getLevel().equals(level) && null != l.getCredits()) return l.getCredits();
    }
    return defaultCredits;
  }

  public int getCreditHours() {
    return creditHours;
  }

  public void setCreditHours(int creditHours) {
    this.creditHours = creditHours;
  }

  public int getWeekHours() {
    return weekHours;
  }

  public void setWeekHours(int weekHours) {
    this.weekHours = weekHours;
  }

  public Integer getWeeks() {
    return weeks;
  }

  public void setWeeks(Integer weeks) {
    this.weeks = weeks;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public CourseType getCourseType() {
    return courseType;
  }

  public void setCourseType(CourseType courseType) {
    this.courseType = courseType;
  }

  public ExamMode getExamMode() {
    return examMode;
  }

  public void setExamMode(ExamMode examMode) {
    this.examMode = examMode;
  }

  public CourseJournal getJournal(Semester semester) {
    for (CourseJournal j : journals) {
      if (!j.getBeginOn().after(semester.getBeginOn()) && (null == j.getEndOn() || j.getEndOn().after(semester.getBeginOn()))) {
        return j;
      }
    }
    return new CourseJournal(this);
  }

  public String getCreditHourString() {
    if (hours.isEmpty()) {
      return "";
    } else {
      StringBuffer sb = new StringBuffer();
      for (Iterator<CourseHour> iter = hours.iterator(); iter.hasNext(); ) {
        CourseHour courseHour = iter.next();
        Integer value = courseHour.getCreditHours();
        sb.append(value);
        if (iter.hasNext()) {
          sb.append('+');
        }
      }
      return sb.toString();
    }
  }

  public Integer getHour(TeachingNature type) {
    for (Iterator<CourseHour> iter = hours.iterator(); iter.hasNext(); ) {
      CourseHour courseHour = iter.next();
      if (null != type && courseHour.getNature().equals(type)) {
        return courseHour.getCreditHours();
      }
    }
    return null;
  }

  public Integer getHourById(Integer typeId) {
    for (Iterator<CourseHour> iter = hours.iterator(); iter.hasNext(); ) {
      CourseHour courseHour = iter.next();
      if (null != typeId
          && courseHour.getNature().getId().equals(typeId)) {
        return courseHour.getCreditHours();
      }
    }
    return null;
  }

  public String toString() {
    return Objects.toStringBuilder(this).add("id", this.getId()).add("code", this.code).add("name", this.name)
        .toString();
  }

  public List<CourseHour> getHours() {
    return hours;
  }

  public void setHours(List<CourseHour> courseHours) {
    this.hours = courseHours;
  }

  public List<CourseLevel> getLevels() {
    return levels;
  }

  public void setLevels(List<CourseLevel> levels) {
    this.levels = levels;
  }

  public GradingMode getGradingMode() {
    return gradingMode;
  }

  public void setGradingMode(GradingMode gradingMode) {
    this.gradingMode = gradingMode;
  }

  public boolean isEnabled() {
    return null == endOn;
  }

  public boolean validIn(java.sql.Date from, java.sql.Date to) {
    if (getBeginOn().after(to)) return false;
    if (getEndOn() == null) return true;
    return getBeginOn().before(to) && from.before(getEndOn());
  }

  public boolean isHasMakeup() {
    return hasMakeup;
  }

  public void setHasMakeup(boolean hasMakeup) {
    this.hasMakeup = hasMakeup;
  }

  public boolean isPractical() {
    if (null == nature) return false;
    else return nature.isPractical();
  }

  public CourseNature getNature() {
    return nature;
  }

  public void setNature(CourseNature nature) {
    this.nature = nature;
  }

  public void setEnabled(boolean e) {
    if (e) {
      if (null == beginOn) beginOn = new java.sql.Date(System.currentTimeMillis());
      endOn = null;
    } else {
      endOn = new java.sql.Date(System.currentTimeMillis());
    }
  }

  public boolean isCalgp() {
    return calgp;
  }

  public void setCalgp(boolean calgp) {
    this.calgp = calgp;
  }

  public List<CourseJournal> getJournals() {
    return journals;
  }

  public void setJournals(List<CourseJournal> journals) {
    this.journals = journals;
  }
}
