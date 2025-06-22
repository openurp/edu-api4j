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
package org.openurp.edu.extern.model;

import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.openurp.base.std.model.Student;
import org.openurp.code.edu.model.Certificate;
import org.openurp.code.edu.model.ExamStatus;
import org.openurp.code.edu.model.GradingMode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

/**
 * 资格考试成绩 <br>
 *
 * @author chaostone
 */

@Entity(name = "org.openurp.edu.extern.model.CertificateGrade")
public class CertificateGrade extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -4394645753927819458L;

  /**
   * 学生
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  /**
   * 得分
   */
  private Float score;

  /**
   * 得分等级/等分文本内容
   */
  private String scoreText;

  /**
   * 是否合格
   */
  @NotNull
  private boolean passed;

  /**
   * 考试科目
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Certificate certificate;

  /**
   * 准考证号
   */
  @Size(max = 50)
  private String examNo;

  /**
   * 证书编号
   */
  private String certificateNo;

  /***/
  private java.sql.Date acquiredIn;

  private int status;
  /**
   * 成绩记录方式
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected GradingMode gradingMode;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private ExamStatus examStatus;

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public String getScoreText() {
    return scoreText;
  }

  public void setScoreText(String scoreText) {
    this.scoreText = scoreText;
  }

  public boolean isPassed() {
    return passed;
  }

  public void setPassed(boolean passed) {
    this.passed = passed;
  }

  public Certificate getCertificate() {
    return certificate;
  }

  public void setCertificate(Certificate certificate) {
    this.certificate = certificate;
  }

  public String getExamNo() {
    return examNo;
  }

  public void setExamNo(String examNo) {
    this.examNo = examNo;
  }

  public String getCertificateNo() {
    return certificateNo;
  }

  public void setCertificateNo(String certificateNo) {
    this.certificateNo = certificateNo;
  }

  public Date getAcquiredIn() {
    return acquiredIn;
  }

  public void setAcquiredIn(Date acquiredIn) {
    this.acquiredIn = acquiredIn;
  }

  public GradingMode getGradingMode() {
    return gradingMode;
  }

  public void setGradingMode(GradingMode gradingMode) {
    this.gradingMode = gradingMode;
  }

  public ExamStatus getExamStatus() {
    return examStatus;
  }

  public void setExamStatus(ExamStatus examStatus) {
    this.examStatus = examStatus;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

}
