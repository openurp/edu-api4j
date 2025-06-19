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
package org.openurp.base.hr.model;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.Department;
import org.openurp.base.model.School;
import org.openurp.code.hr.model.WorkStatus;
import org.openurp.code.job.model.ProfessionalTitle;
import org.openurp.code.person.model.Gender;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity(name = "org.openurp.base.hr.model.Staff")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Staff extends LongIdObject {
  /**
   * 学校
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private School school;

  private String code;

  private String name;

  private String enName;

  @ManyToOne(fetch = FetchType.LAZY)
  private Gender gender;

  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  @ManyToOne(fetch = FetchType.LAZY)
  private ProfessionalTitle title;

  /**
   * 教师在职状态
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private WorkStatus status;

  public School getSchool() {
    return school;
  }

  public void setSchool(School school) {
    this.school = school;
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

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public ProfessionalTitle getTitle() {
    return title;
  }

  public void setTitle(ProfessionalTitle title) {
    this.title = title;
  }

  public WorkStatus getStatus() {
    return status;
  }

  public void setStatus(WorkStatus status) {
    this.status = status;
  }
}
