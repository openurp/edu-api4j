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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.model.Department;
import org.openurp.base.model.User;

/**
 * 专业计划课程组变更申请
 */
@Entity(name = "org.openurp.edu.program.app.model.ExecutiveCourseGroupModify")
@Table(name = "EXECUTE_GROUP_MODIFIES")
public class ExecutiveCourseGroupModify extends LongIdObject {

  private static final long serialVersionUID = 5737589654235506632L;

  public static Integer INITREQUEST = new Integer(-1); // 刚申请
  public static Integer REFUSE = new Integer(0); // 拒绝
  public static Integer ACCEPT = new Integer(1); // 接受
  public static String MODIFY = "变动";
  public static String ADD = "增加";
  public static String DELETE = "删除";
  public static String[] REQUISITIONTYPE = { MODIFY, ADD, DELETE };

  /**
   * 更改类型
   */
  private String requisitionType;

  /** 所属计划 */
  private FakePlan executePlan;

  /** 部门 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  /** 申请单状态 */
  private Integer flag = INITREQUEST;

  /** 更改原因 */
  private String reason;

  /** 申请时间 */
  private Date applyDate = new Date(System.currentTimeMillis());

  /** 答复时间 */
  private Date replyDate;

  /** 学院、系、部审核意见 */
  private String depOpinion;

  /** 教务处审核意见 */
  private String teachOpinion;

  /** 学院（部）会签 */
  private String depSign;

  /** 教学研究科会签 */
  private String teachSign;

  /** 实践学科会签 */
  private String practiceSign;
  /** 申请人 */
  @ManyToOne(fetch = FetchType.LAZY)
  private User proposer;

  /** 更改前的状态 */
  @OneToOne(optional = true, cascade = { CascadeType.ALL }, orphanRemoval = true)
  private ExecutiveCourseGroupModifyDetailBefore oldPlanCourseGroup;

  /** 更改后的状态 */
  @OneToOne(optional = true, cascade = { CascadeType.ALL }, orphanRemoval = true)
  private ExecutiveCourseGroupModifyDetailAfter newPlanCourseGroup;

  /** 审核员 */
  @ManyToOne(fetch = FetchType.LAZY)
  private User assessor;

  public String getRequisitionType() {
    return requisitionType;
  }

  public void setRequisitionType(String requisitionType) {
    this.requisitionType = requisitionType;
  }

  /**
   * 获得的是一个假的专业培养计划，因为直接在这里做引用并不好
   *
   * @return
   */
  public FakePlan getExecutivePlan() {
    return executePlan;
  }

  public void setExecutivePlan(FakePlan executePlan) {
    this.executePlan = executePlan;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Date getApplyDate() {
    return applyDate;
  }

  public void setApplyDate(Date applyDate) {
    this.applyDate = applyDate;
  }

  public Date getReplyDate() {
    return replyDate;
  }

  public void setReplyDate(Date replyDate) {
    this.replyDate = replyDate;
  }

  public String getDepOpinion() {
    return depOpinion;
  }

  public void setDepOpinion(String depOpinion) {
    this.depOpinion = depOpinion;
  }

  public String getTeachOpinion() {
    return teachOpinion;
  }

  public void setTeachOpinion(String teachOpinion) {
    this.teachOpinion = teachOpinion;
  }

  public String getDepSign() {
    return depSign;
  }

  public void setDepSign(String depSign) {
    this.depSign = depSign;
  }

  public String getTeachSign() {
    return teachSign;
  }

  public void setTeachSign(String teachSign) {
    this.teachSign = teachSign;
  }

  public String getPracticeSign() {
    return practiceSign;
  }

  public void setPracticeSign(String practiceSign) {
    this.practiceSign = practiceSign;
  }

  public User getProposer() {
    return proposer;
  }

  public void setProposer(User proposer) {
    this.proposer = proposer;
  }

  public ExecutiveCourseGroupModifyDetailBefore getOldPlanCourseGroup() {
    return oldPlanCourseGroup;
  }

  public void setOldPlanCourseGroup(ExecutiveCourseGroupModifyDetailBefore oldPlanCourseGroup) {
    this.oldPlanCourseGroup = oldPlanCourseGroup;
  }

  public ExecutiveCourseGroupModifyDetailAfter getNewPlanCourseGroup() {
    return newPlanCourseGroup;
  }

  public void setNewPlanCourseGroup(ExecutiveCourseGroupModifyDetailAfter newPlanCourseGroup) {
    this.newPlanCourseGroup = newPlanCourseGroup;
  }

  public User getAssessor() {
    return assessor;
  }

  public void setAssessor(User assessor) {
    this.assessor = assessor;
  }
}
