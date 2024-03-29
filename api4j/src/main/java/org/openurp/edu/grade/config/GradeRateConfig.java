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
package org.openurp.edu.grade.config;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.NaturalId;
import org.openurp.base.edu.model.Project;
import org.openurp.code.edu.model.GradingMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.NumberFormat;
import java.util.List;

/**
 * 成绩分级配置
 */
@Entity(name = "org.openurp.edu.grade.config.GradeRateConfig")
public class GradeRateConfig extends LongIdObject {

  private static final long serialVersionUID = 7557740151486177737L;

  /** 成绩记录方式 */
  @NaturalId
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private GradingMode gradingMode;

  /** 对应培养类型(默认為空) */
  @NaturalId
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 成绩分级配置项 */
  @OneToMany(mappedBy = "config", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @OrderBy("maxScore desc")
  private List<GradeRateItem> items;

  /** 及格线 */
  private float passScore;

  public float getPassScore() {
    return passScore;
  }

  public void setPassScore(float passScore) {
    this.passScore = passScore;
  }

  /**
   * 将字符串按照成绩记录方式转换成数字.<br>
   * 空成绩将转换成null
   *
   * @param score
   * @return
   */
  public String convert(Float score) {
    if (null == score) return "";
    if (gradingMode.isNumerical()) return NumberFormat.getInstance().format(score.floatValue());
    for (GradeRateItem item : items) {
      if (item.contains(score)) { return item.getGrade(); }
    }
    return "";
  }

  @Override
  public String toString() {
    return "GradeRateConfig [gradingMode=" + gradingMode + ", project=" + project + ", items=" + items
        + ", passScore=" + passScore + "]";
  }

  public List<GradeRateItem> getItems() {
    return items;
  }

  public void setItems(List<GradeRateItem> items) {
    this.items = items;
  }

  public final Project getProject() {
    return project;
  }

  public final void setProject(Project project) {
    this.project = project;
  }

  public GradingMode getGradingMode() {
    return gradingMode;
  }

  public void setGradingMode(GradingMode gradingMode) {
    this.gradingMode = gradingMode;
  }

}
