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
package org.openurp.std.graduation.app.model;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.beangle.commons.lang.Strings;
import org.openurp.base.std.model.Student;
import org.openurp.std.graduation.model.GraduateResult;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.MessageFormat;
import java.util.Date;

/**
 * 毕业审核日志
 */
@Entity(name = "org.openurp.std.graduation.app.model.GraduateAuditLog")
public class GraduateAuditLog extends LongIdObject {

  private static final long serialVersionUID = -6428661513378861476L;

  /**
   * 毕业审核批次名称
   */
  @NotNull
  @Size(max = 150)
  @Column(name = "ssn")
  private String batch;

  /**
   * 毕业审核时所用的标准
   */
  @NotNull
  @Size(max = 500)
  private String standardUsed;

  /**
   * 学生
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /**
   * 审核人
   */
  @NotNull
  private String auditBy;

  /**
   * 审核ip
   */
  @NotNull
  @Size(max = 60)
  private String ip;

  /**
   * 操作时间
   */
  @NotNull
  private Date operateAt;

  /**
   * 是否通过
   */
  @NotNull
  private boolean passed;

  /**
   * 各项详细审核结果，要包括是否自动
   */
  @Size(max = 4000)
  private String detail;

  public GraduateAuditLog() {
    super();
  }

  public GraduateAuditLog(GraduateResult result) {
    this.std = result.getStd();
    this.passed = result.getPassed().booleanValue();
    this.batch = result.getBatch().getName();
    StringBuilder resultString = new StringBuilder();
    resultString.append(MessageFormat.format("最终结果：{0}", result.getPassed() ? "通过" : "未通过"));
    resultString.append(MessageFormat.format("\n毕结业结论：{0}",
        result.getEducationResult() == null ? "无" : result.getEducationResult().getName()));
    resultString.append("已通过:" + result.getPassedItems());
    resultString.append("未通过:" + result.getFailedItems());

    if (Strings.isNotBlank(result.getComments())) {
      resultString.append("\r\n备注：").append(result.getComments());
    }
    this.detail = resultString.toString();
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean passed) {
    this.passed = passed;
  }

  public String getAuditBy() {
    return auditBy;
  }

  public void setAuditBy(String auditBy) {
    this.auditBy = auditBy;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public String getBatch() {
    return batch;
  }

  public void setBatch(String batch) {
    this.batch = batch;
  }

  public Date getOperateAt() {
    return operateAt;
  }

  public void setOperateAt(Date operateAt) {
    this.operateAt = operateAt;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getStandardUsed() {
    return standardUsed;
  }

  public void setStandardUsed(String standardUsed) {
    this.standardUsed = standardUsed;
  }

}
