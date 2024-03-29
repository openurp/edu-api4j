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
package org.openurp.base.resource.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.model.AbstractBaseInfo;
import org.openurp.base.model.Campus;
import org.openurp.base.model.Department;
import org.openurp.base.model.School;
import org.openurp.code.asset.model.RoomType;

/**
 * 教室基本信息
 */
@Entity(name = "org.openurp.base.resource.model.Room")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Room extends AbstractBaseInfo {

  private static final long serialVersionUID = 3229044942979160250L;

  /** 所在校区 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Campus campus;

  /** 学校 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private School school;

  /** 设备配置代码 */
  @ManyToOne(fetch = FetchType.LAZY)
  private RoomType roomType;

  /** 管理部门 */
  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  public Room() {
  }

  public Room(Integer id) {
    super(id);
  }

  public Room(String code) {
    super(Integer.valueOf(code));
    setCode(code);
  }

  public Room(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public School getSchool() {
    return school;
  }

  public void setSchool(School school) {
    this.school = school;
  }

  public Campus getCampus() {
    return campus;
  }

  public void setCampus(Campus campus) {
    this.campus = campus;
  }

  public RoomType getRoomType() {
    return roomType;
  }

  public void setRoomType(RoomType type) {
    this.roomType = type;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

}
