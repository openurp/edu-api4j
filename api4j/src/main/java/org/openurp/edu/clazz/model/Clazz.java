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
package org.openurp.edu.clazz.model;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.metadata.Model;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import org.openurp.code.edu.model.CourseType;
import org.openurp.base.edu.model.Course;
import org.openurp.base.edu.model.ProjectBasedObject;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.model.AuditStatus;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.ExamMode;
import org.openurp.code.edu.model.TeachLangType;
import org.openurp.code.edu.model.ClazzTag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 教学任务
 *
 * @since 2005-10-16
 */
@Entity(name = "org.openurp.edu.clazz.model.Clazz")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"crn", "semester_id", "project_id"})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
public class Clazz extends ProjectBasedObject<Long> implements Cloneable {

  private static final long serialVersionUID = 1071972497531228225L;

  /**
   * 课程序号
   */
  @Size(max = 32)
  private String crn;

  /**
   * 教学班简称
   */
  @NotNull
  @Size(max = 4000)
  private String clazzName;

  /**
   * 课程
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Course course;

  /**
   * 主题
   */
  @Size(max = 200)
  private String subject;
  /**
   * 课程类别
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseType courseType;

  /**
   * 开课院系
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Department teachDepart;

  /**
   * 授课教师
   */
  @ManyToMany
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
  @OrderColumn(name = "idx", insertable = true, updatable = true, nullable = false)
  private List<Teacher> teachers = new ArrayList<Teacher>();

  /**
   * 教学任务标签
   */
  @ManyToMany
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "edu.course")
  private List<ClazzTag> tags = new ArrayList<ClazzTag>();

  /**
   * 开课校区
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Campus campus;

  /**
   * 教学班
   */
  @Target(Enrollment.class)
  private Enrollment enrollment;

  /**
   * 教学日历
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Semester semester;

  /**
   * 课程安排
   */
  private Schedule schedule;

  /**
   * 授课语言类型
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private TeachLangType langType;

  /**
   * 所属课程组
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private ClazzGroup group;

  /**
   * 考核方式
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamMode examMode;
  /**
   * 是否有补考
   */
  private boolean hasMakeup;

  /**
   * 审核状态
   */
  @NotNull
  @Type(type = "org.beangle.orm.hibernate.udt.IDEnumType")
  private AuditStatus status = AuditStatus.UNSUBMITTED;

  /**
   * 备注
   */
  @Size(max = 500)
  private String remark;

  private Long planId;

  /**
   * 默认构造函数
   */
  public Clazz() {
    super();
  }

  public Clazz(Long lessonId) {
    this.id = lessonId;
  }

  public static Clazz getDefault() {
    Clazz task = Model.newInstance(Clazz.class);
    task.setEnrollment(new Enrollment());
    task.setSchedule(new Schedule());
    return task;
  }

  /**
   * 得到第一次上课时间
   *
   * @return
   */
  public Date getFirstCourseTime() {
    if (null != semester && semester.isPersisted()) {
      Date date = null;
      if (null != this.getSchedule().getActivities()) {
        for (ClazzActivity session : getSchedule().getActivities()) {
          Date myDate = session.getFirstActivityTime();
          if (date == null || myDate.before(date)) {
            date = myDate;
          }
        }
      }
      return date;
    }
    return null;
  }

  /**
   * 得到最后一次上课时间
   *
   * @return
   */
  public Date getLastCourseTime() {
    if (null != semester && semester.isPersisted()) {
      Date date = null;
      if (null != this.getSchedule().getActivities()) {
        for (ClazzActivity session : getSchedule().getActivities()) {
          Date myDate = session.getLastActivityTime();
          if (date == null || myDate.after(date)) {
            date = myDate;
          }
        }
      }
      return date;
    }
    return null;
  }

  /**
   * @return Returns the semester.
   */
  public Semester getSemester() {
    return semester;
  }

  /**
   * @param semester The semester to set.
   */
  public void setSemester(Semester semester) {
    this.semester = semester;
  }

  /**
   * @return Returns the course.
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @param course The course to set.
   */
  public void setCourse(Course course) {
    this.course = course;
  }

  public String getCrn() {
    return crn;
  }

  public void setCrn(String crn) {
    this.crn = crn;
  }

  /**
   * @return Returns the courseType.
   */
  public CourseType getCourseType() {
    return courseType;
  }

  /**
   * @param courseType The courseType to set.
   */
  public void setCourseType(CourseType courseType) {
    this.courseType = courseType;
  }

  public Enrollment getEnrollment() {
    return enrollment;
  }

  public void setEnrollment(Enrollment enrollment) {
    this.enrollment = enrollment;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String toString() {
    return " [id:" + getId() + "]  " + getCourse().getName() + " " + getClazzName();
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }

  public List<Teacher> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<Teacher> teachers) {
    this.teachers = teachers;
  }

  public Campus getCampus() {
    return campus;
  }

  public void setCampus(Campus campus) {
    this.campus = campus;
  }

  /**
   * 获得上课教师名称
   *
   * @return
   */
  public String getTeacherNames() {
    if (null != getTeachers() && !getTeachers().isEmpty()) {
      StringBuffer buf = new StringBuffer(10);
      for (Teacher teacher : getTeachers()) {
        buf.append(teacher.getName()).append(",");
      }
      buf.deleteCharAt(buf.length() - 1);
      return buf.toString();
    } else {
      return "";
    }
  }

  /**
   * 获得上课教师名称
   *
   * @return
   */
  public String getTeacherNamesWithCode() {
    if (null != getTeachers() && !getTeachers().isEmpty()) {
      StringBuffer buf = new StringBuffer(10);
      for (Teacher teacher : getTeachers()) {
        buf.append(teacher.getName()).append("[").append(teacher.getCode()).append("]").append(",");
      }
      buf.deleteCharAt(buf.length() - 1);
      return buf.toString();
    } else {
      return "";
    }
  }

  public Department getTeachDepart() {
    return teachDepart;
  }

  public void setTeachDepart(Department teachDepart) {
    this.teachDepart = teachDepart;
  }

  public TeachLangType getLangType() {
    return langType;
  }

  public void setLangType(TeachLangType langType) {
    this.langType = langType;
  }

  public List<ClazzTag> getTags() {
    return tags;
  }

  public void setTags(List<ClazzTag> tags) {
    this.tags = tags;
  }

  public ClazzGroup getGroup() {
    return group;
  }

  public void setGroup(ClazzGroup group) {
    this.group = group;
  }

  public AuditStatus getStatus() {
    return status;
  }

  public void setStatus(AuditStatus status) {
    this.status = status;
  }

  public String getClazzName() {
    return clazzName;
  }

  public void setClazzName(String clazzName) {
    this.clazzName = clazzName;
  }

  /**
   * id为null 课程序号为null
   */
  public Clazz clone() {
    try {
      Clazz one = (Clazz) super.clone();
      one.setId(null);
      one.setCrn(null);
      one.setGroup(null);
      one.setTags(new ArrayList<ClazzTag>());
      one.getTags().addAll(this.getTags());
      one.setTeachers(CollectUtils.<Teacher>newArrayList());
      one.getTeachers().addAll(this.getTeachers());
      one.setSchedule(getSchedule().clone());
      one.setEnrollment(getEnrollment().clone());
      for (CourseTaker taker : one.getEnrollment().getCourseTakers()) {
        taker.setClazz(one);
      }
      for (ClazzRestriction group : one.getEnrollment().getRestrictions()) {
        group.setClazz(one);
      }
      Date createdAt = new Date(System.currentTimeMillis());
      one.setUpdatedAt(createdAt);

      return one;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public ExamMode getExamMode() {
    return examMode;
  }

  public void setExamMode(ExamMode examMode) {
    this.examMode = examMode;
  }

  public boolean isHasMakeup() {
    return hasMakeup;
  }

  public void setHasMakeup(boolean hasMakeup) {
    this.hasMakeup = hasMakeup;
  }

  public Long getPlanId() {
    return planId;
  }

  public void setPlanId(Long planId) {
    this.planId = planId;
  }
}
