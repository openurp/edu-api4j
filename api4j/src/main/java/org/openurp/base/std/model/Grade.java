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
package org.openurp.base.std.model;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openurp.base.edu.model.Project;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Date;

@Entity(name = "org.openurp.base.std.model.Grade")
@Cacheable
@Cache(region = "openurp.base", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Grade extends LongIdObject {

  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  private String code;

  private String name;

  private String enName;

  private java.sql.Date beginIn;

  private java.sql.Date endIn;

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
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

  public Date getBeginIn() {
    return beginIn;
  }

  public void setBeginIn(Date beginIn) {
    this.beginIn = beginIn;
  }

  public Date getEndIn() {
    return endIn;
  }

  public void setEndIn(Date endIn) {
    this.endIn = endIn;
  }

  @Override
  public String toString() {
    return code;
  }
}
