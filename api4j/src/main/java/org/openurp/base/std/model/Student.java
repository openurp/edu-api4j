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
import org.beangle.commons.entity.pojo.TemporalEntity;
import org.openurp.base.edu.model.Direction;
import org.openurp.base.edu.model.EduLevelBasedObject;
import org.openurp.base.edu.model.Major;
import org.openurp.base.hr.model.Teacher;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Department;
import org.openurp.base.model.Person;
import org.openurp.code.std.model.StdLabel;
import org.openurp.code.std.model.StdLabelType;
import org.openurp.code.std.model.StdType;
import org.openurp.code.edu.model.StudyType;
import org.openurp.code.person.model.Gender;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

/**
 * 学籍信息实现
 */
@Entity(name = "org.openurp.base.std.model.Student")
public class Student extends EduLevelBasedObject<Long> implements TemporalEntity {

  private static final long serialVersionUID = -1973115982366299767L;

  @ManyToOne(fetch = FetchType.LAZY)
  private Gender gender;

  private String code;

  private String name;

  /**
   * 基本信息
   */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Person person;

  /**
   * 学生类别 所在项目内的学生类别
   */
  @ManyToOne(fetch = FetchType.LAZY)
  protected StdType stdType;

  /**
   * 学籍状态日志
   */
  @ManyToOne(fetch = FetchType.LAZY)
  protected StudentState state;

  /**
   * 状态变化日志
   */
  @OneToMany(mappedBy = "std", cascade = {CascadeType.ALL}, orphanRemoval = true)
  protected Set<StudentState> states = CollectUtils.newHashSet();

  /**
   * 学生分类标签
   */
  @ManyToMany
  @MapKeyJoinColumn(name = "std_label_type_id")
  protected Map<StdLabelType, StdLabel> labels = CollectUtils.newHashMap();

  /**
   * 学制 学习年限（允许0.5年出现）1
   */
  @NotNull
  protected Float duration;

  /**
   * 是否有学籍
   */
  @NotNull
  protected boolean registed;

  /**
   * 学籍生效日期
   */
  @NotNull
  protected java.sql.Date beginOn;

  /**
   * 学籍结束日期
   */
  @NotNull
  protected java.sql.Date endOn;

  /**
   * 预计毕业日期
   */
  @NotNull
  protected java.sql.Date graduateOn;

  /**
   * 学习形式 全日制/业余/函授
   */
  @ManyToOne(fetch = FetchType.LAZY)
  protected StudyType studyType;

  /**
   * 备注
   */
  @Size(max = 2000)
  protected String remark;

  public Student() {
    super();
  }

  public Student(Long id) {
    super();
    this.setId(id);
  }

  public Squad getSquad() {
    return state.getSquad();
  }

  public Department getDepartment() {
    return state.getDepartment();
  }

  public Float getDuration() {
    return duration;
  }

  public void setDuration(Float duration) {
    this.duration = duration;
  }

  public Gender getGender() {
    return person.getGender();
  }

  public Grade getGrade() {
    return state.getGrade();
  }

  public Major getMajor() {
    return state.getMajor();
  }

  public Direction getDirection() {
    return state.getDirection();
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Campus getCampus() {
    return state.getCampus();
  }

  public java.sql.Date getBeginOn() {
    return beginOn;
  }

  public void setBeginOn(java.sql.Date beginOn) {
    this.beginOn = beginOn;
  }

  public StudyType getStudyType() {
    return studyType;
  }

  public void setStudyType(StudyType studyType) {
    this.studyType = studyType;
  }

  public java.sql.Date getEndOn() {
    return endOn;
  }

  public void setEndOn(java.sql.Date endOn) {
    this.endOn = endOn;
  }

  public StdType getStdType() {
    return stdType;
  }

  public void setStdType(StdType type) {
    this.stdType = type;
  }

  public void addLabel(StdLabel label) {
    if (null == labels) {
      labels = CollectUtils.newHashMap();
    }
    labels.put(label.getLabelType(), label);
  }

  public void addLabels(StdLabel... labels) {
    if (null != labels) {
      for (StdLabel label : labels) {
        addLabel(label);
      }
    }
  }

  public void clearLabels() {
    if (null != labels) {
      labels.clear();
    }
  }

  public Map<StdLabelType, StdLabel> getLabels() {
    return labels;
  }

  public void setLabels(Map<StdLabelType, StdLabel> labels) {
    this.labels = labels;
  }

  public StudentState getState() {
    return state;
  }

  public void setState(StudentState state) {
    this.state = state;
  }

  /**
   * 将学籍状态记录日志中的某个符合要求学籍状态记录置为当前学籍状态
   *
   * @throws Exception
   */
  public void pointCurrentState() throws Exception {
    if (CollectionUtils.isEmpty(states)) {
      throw new IllegalArgumentException("states is empty!!!");
    }
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
    long now = Long.valueOf(f.format(new java.sql.Date(System.currentTimeMillis())));
    java.sql.Date nowDate = new java.sql.Date(System.currentTimeMillis());
    long distance = Long.MAX_VALUE;
    StudentState last = null;
    for (StudentState state : states) {
      if (nowDate.getTime() >= state.getBeginOn().getTime()
          && nowDate.getTime() <= state.getEndOn().getTime()) {
        last = state;
        break;
      }
      long middle = (Long.valueOf(f.format(state.getEndOn())) + Long.valueOf(f.format(state.getBeginOn())))
          / 2;
      long d = Math.abs(now - middle);
      if (d < distance) {
        distance = d;
        last = state;
      }
    }
    this.state = last;
  }

  public void addState(StudentState state) {
    if (null == states) {
      states = CollectUtils.newHashSet();
    }
    state.setStd(this);
    states.add(state);
  }

  public Set<StudentState> getStates() {
    return states;
  }

  public void setStates(Set<StudentState> states) {
    this.states = states;
  }

  public boolean isRegisted() {
    return registed;
  }

  public void setRegisted(boolean registed) {
    this.registed = registed;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
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

  public java.sql.Date getGraduateOn() {
    return graduateOn;
  }

  public void setGraduateOn(java.sql.Date graduateOn) {
    this.graduateOn = graduateOn;
  }

}
