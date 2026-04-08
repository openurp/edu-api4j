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
package org.openurp.code.edu.model;

import org.beangle.commons.entity.pojo.Code;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.code.school;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * 课程类别
 *
 * @since 2005-9-7
 */
@Entity(name = "org.openurp.code.edu.model.CourseType")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@school
public class CourseType extends Code<Integer> {

  private static final long serialVersionUID = 8232522018765348618L;
  /**
   * 上级类别
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private CourseType parent;

  @ManyToOne(fetch = FetchType.LAZY)
  private CourseModule module;

  @ManyToOne(fetch = FetchType.LAZY)
  private CourseRank rank;

  /**
   * @return
   */
  @Deprecated
  public boolean isMajor() {
    if (null == module) return false;
    return module.isMajor();
  }

  @Deprecated
  public boolean isOptional() {
    if (null == rank) return false;
    return !rank.isCompulsory();
  }


  @Deprecated
  public boolean isPractical() {
    if (null == module) return false;
    return module.isPractical();
  }

  public CourseType() {
    super();
  }

  public CourseType(Integer id) {
    super(id);
  }

  public CourseType getParent() {
    return parent;
  }

  public void setParent(CourseType parent) {
    this.parent = parent;
  }

  public CourseModule getModule() {
    return module;
  }

  public void setModule(CourseModule module) {
    this.module = module;
  }

  public CourseRank getRank() {
    return rank;
  }

  public void setRank(CourseRank rank) {
    this.rank = rank;
  }
}
