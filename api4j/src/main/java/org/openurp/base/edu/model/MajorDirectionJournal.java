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

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.NumberIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;

/**
 * 专业方向建设过程
 */
@Entity(name = "org.openurp.base.edu.model.MajorDirectionJournal")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MajorDirectionJournal extends NumberIdObject<Long> {

  private static final long serialVersionUID = -325648764365874076L;

  /** 专业方向 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private MajorDirection direction;

  /** 培养层次 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private EducationLevel level;

  /** 部门 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Department depart;

  /** 生效时间 */
  @NotNull
  protected java.sql.Date beginOn;

  /** 失效时间 */
  protected java.sql.Date endOn;

  /** 备注 */
  @Size(max = 255)
  private String remark;

  public MajorDirection getDirection() {
    return direction;
  }

  public void setDirection(MajorDirection direction) {
    this.direction = direction;
  }

  public Department getDepart() {
    return depart;
  }

  public void setDepart(Department depart) {
    this.depart = depart;
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

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public EducationLevel getLevel() {
    return level;
  }

  public void setLevel(EducationLevel level) {
    this.level = level;
  }

}
