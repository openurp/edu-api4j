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

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 方向信息 专业领域.
 *
 *
 */
@Entity(name = "org.openurp.base.edu.model.MajorDirection")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MajorDirection extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -5678184739078006225L;
  /** 专业方向编码 */
  @Column(unique = true)
  @NotNull
  @Size(max = 32)
  protected String code;

  /** 专业方向名称 */
  @NotNull
  @Size(max = 100)
  protected String name;

  /** 专业方向英文名 */
  @Size(max = 255)
  protected String enName;

  /** 备注 */
  @Size(max = 500)
  protected String remark;

  /** 生效时间 */
  @NotNull
  protected java.sql.Date beginOn;

  /** 失效时间 */
  protected java.sql.Date endOn;

  /** 所属专业 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Major major;

  /** 项目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  /** 部门 */
  @OneToMany(mappedBy = "direction", cascade = { CascadeType.ALL }, orphanRemoval = true)
  @Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<MajorDirectionJournal> journals = CollectUtils.newArrayList();

  public MajorDirection() {
    super();
  }

  public MajorDirection(Long id) {
    super(id);
  }

  public Major getMajor() {
    return major;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public List<MajorDirectionJournal> getJournals() {
    return journals;
  }

  public void setJournals(List<MajorDirectionJournal> journals) {
    this.journals = journals;
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

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
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

}
