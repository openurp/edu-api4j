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
package org.openurp.edu.grade.plan.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.hibernate.annotations.NaturalId;
import org.openurp.code.edu.model.CourseType;
import org.openurp.base.std.model.Student;

/**
 * 计划完成审核结果<br>
 */
@Entity(name = "org.openurp.edu.grade.plan.model.AuditPlanResult")
public class AuditPlanResult extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -3096429906586836701L;

  /** 对应学生 */
  @NotNull
  @NaturalId
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /** 学分审核结果 */
  @Embedded
  private AuditStat auditStat = new AuditStat();

  /** 各课程组审核结果 */
  @OneToMany(mappedBy = "planResult", orphanRemoval = true, cascade = { CascadeType.ALL })
  @OrderBy("indexno")
  private List<AuditGroupResult> groupResults = CollectUtils.newArrayList();

  /** 是否通过 */
  private boolean passed;

  /** 备注 */
  @Size(max = 500)
  private String remark;
  /**
   * 增量更新内容
   */
  @Size(max = 1000)
  private String updates;

  /**
   * 是否发布审核结果
   */
  private boolean archived = false;

  public AuditPlanResult() {
    super();
  }

  public AuditPlanResult(Student student) {
    setStd(student);
  }

  public List<AuditGroupResult> getTopGroupResults() {
    List<AuditGroupResult> results = CollectUtils.newArrayList();
    for (AuditGroupResult result : groupResults) {
      if (null == result.getParent()) {
        results.add(result);
      }
    }
    return results;
  }

  public List<AuditGroupResult> getGroupResults() {
    return groupResults;
  }

  public void setGroupResults(List<AuditGroupResult> groupAuditResults) {
    this.groupResults = groupAuditResults;
  }

  public AuditStat getAuditStat() {
    return auditStat;
  }

  public void setAuditStat(AuditStat auditStat) {
    this.auditStat = auditStat;
  }

  public void addGroupResult(AuditGroupResult rs) {
    rs.setPlanResult(this);
    this.groupResults.add(rs);
  }

  public void removeGroupResult(AuditGroupResult rs) {
    rs.setPlanResult(null);
    this.groupResults.remove(rs);
  }

  /**
   * 获取指定课程类别的课程组审核结果
   *
   * @param stdType
   * @return
   */
  public AuditGroupResult getGroupResult(CourseType type) {
    if (null == groupResults) { return null; }
    for (AuditGroupResult groupAuditResult : groupResults) {
      AuditGroupResult res = getGroupResult(groupAuditResult, type);
      if (null != res) { return res; }
    }
    return null;
  }

  /**
   * 递归发现符合课程类型要求的组
   *
   * @param groupResult
   * @param stdType
   * @return
   */
  private AuditGroupResult getGroupResult(AuditGroupResult groupResult, CourseType type) {
    if (type.equals(groupResult.getCourseType())) { return groupResult; }
    for (AuditGroupResult childResult : groupResult.getChildren()) {
      AuditGroupResult res = getGroupResult(childResult, type);
      if (null != res) return res;
    }
    return null;
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student student) {
    this.std = student;
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean passed) {
    this.passed = passed;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getUpdates() {
    return updates;
  }

  public void setUpdates(String updates) {
    this.updates = updates;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

}
