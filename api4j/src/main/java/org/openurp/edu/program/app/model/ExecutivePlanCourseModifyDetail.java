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
package org.openurp.edu.program.app.model;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.apache.commons.beanutils.PropertyUtils;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.beangle.commons.lang.Objects;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.Throwables;
import org.hibernate.annotations.Type;
import org.openurp.base.model.Department;
import org.openurp.base.time.Terms;
import org.openurp.base.edu.model.Course;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.ExecutivePlanCourse;
import org.openurp.edu.program.model.PlanCourse;

/**
 * 专业计划课程修改详情
 */
@MappedSuperclass
public abstract class ExecutivePlanCourseModifyDetail extends LongIdObject implements Comparable, Cloneable {

  private static final long serialVersionUID = 5552733977609925991L;

  /** 课程 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Course course;

  /** 开课院系 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Department department;

  /** 开课的学期 */
  @Type(type = "org.openurp.base.time.hibernate.TermsType")
  protected Terms terms;

  /** 是否必修 */
  protected boolean compulsory;

  /**
   * 所属课程组 <br>
   * 不能直接使用ExecutivePlanCourseGroup，否则会出现无法删除课程组的情况<br>
   * 这是假的课程组，记录了课程组的ID和课程类别
   */
  protected FakeCourseGroup fakeCourseGroup;

  /** 备注 */
  protected String remark;

  /**
   * 全盘比较?? FIXME for reason?
   *
   * @see java.lang.Object#equals(Object)
   */
  public boolean isSame(Object object) {
    if (!(object instanceof PlanCourse)) { return false; }
    ExecutivePlanCourse rhs = (ExecutivePlanCourse) object;
    return Objects.equalsBuilder()
        // .add(getHskLevel(), rhs.getHskLevel())
        .add(getTerms(), rhs.getTerms()).add(getRemark(), rhs.getRemark())
        .add(getCourse().getId(), rhs.getCourse().getId()).add(getId(), rhs.getId()).isEquals();
  }

  /**
   * 默认按照学分进行排序
   *
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object object) {
    PlanCourse myClass = (PlanCourse) object;
    return Objects.compareBuilder().add(getCourse().getDefaultCredits(), myClass.getCourse().getDefaultCredits())
        .toComparison();
  }

  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    ExecutivePlanCourse planCourse = new ExecutivePlanCourse();
    try {
      PropertyUtils.copyProperties(planCourse, this);
      planCourse.setGroup(null);
      planCourse.setId(null);
      // planCourse.setPreCourses(new ArrayList());
      // planCourse.getPreCourses().addAll(this.getPreCourses());
      // planCourse.setCourseHours(new HashMap<Long, Integer>());
      // planCourse.getCourseHours().putAll(this.getCourseHours());
    } catch (Exception e) {
      throw new RuntimeException("error in clone planCourse:" + Throwables.getStackTrace(e));
    }
    return planCourse;
  }

  public boolean inTerm(String terms) {
    if (Strings.isEmpty(terms)) {
      return true;
    } else {
      String termArray[] = Strings.split(terms, ",");
      for (int i = 0; i < termArray.length; i++) {
        if (Strings.contains("," + getTerms() + ",", "," + termArray[i] + ",")) return true;
      }
      return false;
    }

  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public FakeCourseGroup getFakeCourseGroup() {
    return fakeCourseGroup;
  }

  public void setFakeCourseGroup(FakeCourseGroup fakeCourseGroup) {
    this.fakeCourseGroup = fakeCourseGroup;
  }

  public void setFakeCourseGroupByReal(ExecutiveCourseGroup courseGroup) {
    if (this.fakeCourseGroup == null) {
      this.fakeCourseGroup = new FakeCourseGroup();
    }
    this.fakeCourseGroup.setId(courseGroup.getId());
    this.fakeCourseGroup.setCourseType(courseGroup.getCourseType());
  }

  public String getRemark() {
    return remark;
  }

  public Terms getTerms() {
    return terms;
  }

  public void setTerms(Terms terms) {
    this.terms = terms;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public boolean isCompulsory() {
    return compulsory;
  }

  public void setCompulsory(boolean compulsory) {
    this.compulsory = compulsory;
  }

  public abstract ExecutivePlanCourseModify getApply();

  public abstract void setApply(ExecutivePlanCourseModify apply);

}
