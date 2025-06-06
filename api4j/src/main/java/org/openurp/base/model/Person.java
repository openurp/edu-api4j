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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.entity.pojo.NumberIdTimeObject;
import org.openurp.code.geo.model.Country;
import org.openurp.code.person.model.Gender;
import org.openurp.code.person.model.IdType;
import org.openurp.code.person.model.Nation;
import org.openurp.code.person.model.PoliticalStatus;

/**
 * 学生基本信息
 *
 * @since 2011-10-12
 */
@Entity(name = "org.openurp.base.model.Person")
public class Person extends NumberIdTimeObject<Long> {

  private static final long serialVersionUID = -6354376799498330264L;
  /** 人员编码 */
  @NotNull
  @Size(max = 32)
  protected String code;

  /** 姓名 */
  @NotNull
  protected String name;

  /** 英文名 */
  @Size(max = 255)
  protected String phoneticName;

  /** 曾用名 */
  protected String formerName;

  /** 性别 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected Gender gender;

  /** 出生年月 */
  protected java.sql.Date birthday;

  @NotNull
  /** 证件类型 身份证/护照等 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected IdType idType;

  /** 国家地区 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Country country;

  /** 民族 留学生使用外国民族 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Nation nation;

  /** 政治面貌 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected PoliticalStatus politicalStatus;

  /** 籍贯 */
  protected String homeTown;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFormerName() {
    return formerName;
  }

  public void setFormerName(String oldname) {
    this.formerName = oldname;
  }

  public java.sql.Date getBirthday() {
    return birthday;
  }

  public void setBirthday(java.sql.Date birthday) {
    this.birthday = birthday;
  }

  public IdType getIdType() {
    return idType;
  }

  public void setIdType(IdType idType) {
    this.idType = idType;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Nation getNation() {
    return nation;
  }

  public void setNation(Nation nation) {
    this.nation = nation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneticName() {
    return phoneticName;
  }

  public void setPhoneticName(String enName) {
    this.phoneticName = enName;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getHomeTown() {
    return homeTown;
  }

  public void setHomeTown(String homeTown) {
    this.homeTown = homeTown;
  }

  public PoliticalStatus getPoliticalStatus() {
    return politicalStatus;
  }

  public void setPoliticalStatus(PoliticalStatus politicalStatus) {
    this.politicalStatus = politicalStatus;
  }

}
