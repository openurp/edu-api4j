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
import org.beangle.commons.entity.pojo.NumberIdObject;
import org.beangle.commons.lang.Objects;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 学年学期 教学日历代表的是具体学年度的 学期设置，每个学期的起始时间和结束时间，教学周个数数.<br>
 * 以及每个教学周的具体起始、结束日期.<br>
 * [start,finish]
 *
 * @hibernate.class
 * @depend - - - Calendar
 */
@Entity(name = "org.openurp.base.edu.model.Semester")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Semester extends NumberIdObject<Integer> implements Comparable<Semester> {

  private static final long serialVersionUID = 1418209086970834483L;

  /**
   * 编码
   */
  @NotNull
  @NaturalId(mutable = true)
  @Size(max = 32)
  private String code;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private SchoolYear year;

  /**
   * 学期名称
   */
  @NotNull
  @Size(max = 100)
  private String name;

  /**
   * 起始日期
   */
  @NotNull
  private java.sql.Date beginOn;

  /**
   * 截止日期
   */
  @NotNull
  private java.sql.Date endOn;

  /**
   * 教学日历方案类别
   */
  @NotNull
  @NaturalId(mutable = true)
  @ManyToOne(fetch = FetchType.LAZY)
  private Calendar calendar;

  /**
   * 备注
   */
  @Size(max = 200)
  private String remark;

  /**
   * 包含阶段
   */
  @OneToMany(mappedBy = "semester", orphanRemoval = true)
  @Cascade({CascadeType.ALL})
  private List<SemesterStage> stages;

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Semester() {
    super();
  }

  public Semester(Integer id) {
    this.id = id;
  }

  public Semester(SchoolYear year, String name, Date beginOn, Date endOn) {
    super();
    this.year = year;
    this.name = name;
    this.beginOn = beginOn;
    this.endOn = endOn;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean before(Semester semester) {
    return endOn.before(semester.getBeginOn());
  }

  public boolean after(Semester semester) {
    return beginOn.after(semester.getEndOn());
  }

  /**
   * 判断日期是否在教学日历的范围内.
   *
   * @param date
   * @return
   */
  public boolean contains(java.util.Date date) {
    if (date.before(getBeginOn()) || date.after(getEndOn())) return false;
    else return true;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  /**
   * 是否是小学期 暑期、寒假学期等(时间<=2月)
   */
  public boolean isShorter() {
    return getWeeks() <= 8;
  }

  public Calendar getCalendar() {
    return calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  /**
   * 获得该日历的真实起始年份(不是学年度的中的起始年份,例如第二个学期时)
   *
   * @return
   */
  public int getStartYear() {
    if (null != getBeginOn()) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(getBeginOn());
      return gc.get(java.util.Calendar.YEAR);
    }
    return 0;
  }

  /**
   * 按照实际指定的第一天,计算开始周
   *
   * @return
   */
  public int getStartWeek() {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setFirstDayOfWeek(calendar.getFirstWeekday().getIndex());
    gc.setTime(getBeginOn());
    return gc.get(java.util.Calendar.WEEK_OF_YEAR);
  }

  public int getWeeks() {
    long length = getEndOn().getTime() - getBeginOn().getTime();
    return (int) Math.ceil(length / (1000 * 3600 * 24 * 7.0));
  }

  /**
   * 返回每周的日历
   *
   * @return 包含一个每周七天的集合, 如果开始日期和结束日期不在星期的第一和最后一天，将向两边延伸
   */
  public List<List<java.util.Date>> getWeekDates() {
    List<List<java.util.Date>> dates = CollectUtils.newArrayList();
    java.util.Calendar start = new GregorianCalendar();
    start.setTime(getBeginOn());
    Date finish = getEndOn();
    List<java.util.Date> weekDates = null;

    int firstWeekDayIndex = calendar.getFirstWeekday().getIndex();
    // 向前回朔到本周第一天
    while (start.get(java.util.Calendar.DAY_OF_WEEK) != firstWeekDayIndex) {
      start.add(java.util.Calendar.DAY_OF_YEAR, -1);
    }

    int i = 0;
    while (!start.getTime().after(finish)) {
      if (i == 0) {
        weekDates = CollectUtils.newArrayList();
        dates.add(weekDates);
      }
      weekDates.add(start.getTime());
      i++;
      i %= 7;
      start.add(java.util.Calendar.DAY_OF_YEAR, 1);
    }

    // 如果结束日期是所在周第一天，则向后移动一天
    if (start.get(java.util.Calendar.DAY_OF_WEEK) == firstWeekDayIndex) {
      start.add(java.util.Calendar.DAY_OF_YEAR, 1);
      weekDates.add(start.getTime());
    }

    // 添加本周的其他天
    while (start.get(java.util.Calendar.DAY_OF_WEEK) != firstWeekDayIndex) {
      start.add(java.util.Calendar.DAY_OF_YEAR, 1);
      weekDates.add(start.getTime());
    }
    return dates;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return Objects.toStringBuilder(this).add("year", this.year.getName()).add("name", this.name)
        .add("beginOn", this.getBeginOn()).add("endOn", this.getEndOn()).toString();
  }

  /**
   * 比较学生类别\学年度\起始日期
   *
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Semester other) {
    return Objects.compareBuilder().add(this.beginOn, other.getBeginOn()).toComparison();
  }

  public List<SemesterStage> getStages() {
    return stages;
  }

  public void setStages(List<SemesterStage> stages) {
    this.stages = stages;
  }

  public String getSchoolYear() {
    return year.getName();
  }

  public SchoolYear getYear() {
    return year;
  }

  public void setYear(SchoolYear year) {
    this.year = year;
  }
}
