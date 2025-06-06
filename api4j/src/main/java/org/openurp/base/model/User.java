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
package org.openurp.base.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.code.hr.model.UserCategory;
import org.openurp.code.person.model.Gender;

@Entity(name = "org.openurp.base.model.User")
@Table(name = "users", schema = "base")
public class User extends LongIdObject {

  private static final long serialVersionUID = 7382338864305594916L;

  @ManyToOne(fetch = FetchType.LAZY)
  private Gender gender;

  private String code;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  private String email;

  private String mobile;

  /** 学校 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private School school;

  private java.sql.Date beginOn;

  private java.sql.Date endOn;

  private java.util.Date updatedAt;

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public School getSchool() {
    return school;
  }

  public void setSchool(School school) {
    this.school = school;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  private UserCategory category;

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

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public UserCategory getCategory() {
    return category;
  }

  public void setCategory(UserCategory category) {
    this.category = category;
  }

  public java.util.Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.util.Date updatedAt) {
    this.updatedAt = updatedAt;
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

  public boolean isCategory(int categoryId) {
    return category.getId().intValue() == categoryId;
  }
}
