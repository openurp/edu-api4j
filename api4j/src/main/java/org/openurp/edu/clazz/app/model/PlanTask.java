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
package org.openurp.edu.clazz.app.model;

import java.util.Date;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.model.User;
import org.openurp.base.edu.model.Course;
import org.openurp.base.edu.model.Semester;
import org.openurp.edu.program.model.ExecutivePlan;

/**
 * 开课,不开课申请
 * 名字不好，要改
 */
@Deprecated
public class PlanTask extends LongIdObject {

  private static final long serialVersionUID = 7435640814616551019L;

  public static Integer INIT = Integer.valueOf(-1);// 刚申请

  public static Integer REQ_CLOSE = Integer.valueOf(0);// 不开课

  public static Integer REQ_OPEN = Integer.valueOf(1);// 开课

  private Semester semester;

  private ExecutivePlan teachPlan;

  private Course course;

  /**
   * 申请或修改时间
   */
  private Date applyDate = new Date(System.currentTimeMillis());

  /**
   * 答复时间
   */
  private Date replyDate;

  /**
   * 申请单状态
   */
  private Integer flag = INIT;

  /**
   * 申请人
   */
  private User proposer;

  /**
   * 审核员
   */
  private User assessor;

  public Date getReplyDate() {
    return replyDate;
  }

  public void setReplyDate(Date replyDate) {
    this.replyDate = replyDate;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Date getApplyDate() {
    return applyDate;
  }

  public void setApplyDate(Date applyDate) {
    this.applyDate = applyDate;
  }

  public User getAssessor() {
    return assessor;
  }

  public void setAssessor(User assessor) {
    this.assessor = assessor;
  }

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public ExecutivePlan getExecutivePlan() {
    return teachPlan;
  }

  public void setExecutivePlan(ExecutivePlan teachPlan) {
    this.teachPlan = teachPlan;
  }

  public User getProposer() {
    return proposer;
  }

  public void setProposer(User proposer) {
    this.proposer = proposer;
  }

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

}
