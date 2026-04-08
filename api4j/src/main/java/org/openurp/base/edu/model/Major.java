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

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;

/**
 * 专业
 */
@Entity(name = "org.openurp.base.edu.model.Major")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Major extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = 7360406731828210066L;
  /** 专业编码 */
  @Column(unique = true)
  @NotNull
  @Size(max = 32)
  protected String code;

  /** 专业名称 */
  @NotNull
  @Size(max = 100)
  protected String name;

  /** 专业英文名 */
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

  /** 项目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 获得方向 */
  @OneToMany(mappedBy = "major", cascade = { CascadeType.ALL }, orphanRemoval = true)
  private Set<MajorDirection> directions = CollectUtils.newHashSet();

  /** 建设过程 */
  @OneToMany(mappedBy = "major", cascade = { CascadeType.ALL }, orphanRemoval = true)
  @Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<MajorJournal> journals = CollectUtils.newArrayList();

  /** 学科 */
  @OneToMany(mappedBy = "major", cascade = { CascadeType.ALL }, orphanRemoval = true)
  @Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<MajorDiscipline> disciplines = CollectUtils.newArrayList();

  /** 学制*/
  @OneToMany(mappedBy = "major", cascade = { CascadeType.ALL }, orphanRemoval = true)
  @Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private List<SchoolLength> schoolLengths = CollectUtils.newArrayList();

  /** 简称 */
  @Size(max = 30)
  private String shortName;

  public Major() {
    super();
  }

  public Major(Long id) {
    super(id);
  }

  public Set<MajorDirection> getDirections() {
    return directions;
  }

  public void setDirections(Set<MajorDirection> directions) {
    this.directions = directions;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public List<MajorJournal> getJournals() {
    return journals;
  }

  public void setJournals(List<MajorJournal> journals) {
    this.journals = journals;
  }

  public List<EducationLevel> getLevels() {
    Set<EducationLevel> educations = new java.util.HashSet<EducationLevel>();
    for (MajorJournal j : journals) {
      educations.add(j.getLevel());
    }
    return new java.util.ArrayList<EducationLevel>(educations);
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

  public String getDisciplineCode(java.sql.Date endOn) {
    for (MajorDiscipline md : disciplines) {
      if (md.isMatchIn(endOn)) { return md.getDisciplineCode(); }
    }
    return "";
  }

  public Set<Department> getDepartments() {
    Set<Department> departs = CollectUtils.newHashSet();
    for (MajorJournal md : getJournals()) {
      departs.add(md.getDepart());
    }
    return departs;
  }

  public Set<Department> getValidDepartments() {
    Set<Department> departs = CollectUtils.newHashSet();
    Date now = new Date();
    for (MajorJournal md : getJournals()) {
      if (now.after(md.getBeginOn())
          && ((md.getEndOn() != null && now.before(md.getEndOn())) || (md.getEndOn() == null))) {
        departs.add(md.getDepart());
      }
    }
    return departs;
  }

  public Set<Department> getValidDepartments(Date time) {
    Set<Department> departs = CollectUtils.newHashSet();
    for (MajorJournal md : getJournals()) {
      if (time.after(md.getBeginOn())
          && ((md.getEndOn() != null && time.before(md.getEndOn())) || (md.getEndOn() == null))) {
        departs.add(md.getDepart());
      }
    }
    return departs;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public List<MajorDiscipline> getDisciplines() {
    return disciplines;
  }

  public void setDisciplines(List<MajorDiscipline> disciplines) {
    this.disciplines = disciplines;
  }

  public List<SchoolLength> getSchoolLengths() {
    return schoolLengths;
  }

  public void setSchoolLengths(List<SchoolLength> schoolLengths) {
    this.schoolLengths = schoolLengths;
  }
}
