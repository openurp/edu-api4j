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
package org.openurp.edu.clazz.model;

import org.beangle.commons.entity.Entity;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.IDEnum;
import org.openurp.code.edu.model.EducationType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Squad;
import org.openurp.base.model.Department;
import org.openurp.code.std.model.StdLabel;
import org.openurp.code.std.model.StdType;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;

public enum ClazzRestrictionMeta implements IDEnum {

  Grade(1, String.class, "年级"), StdType(2, StdType.class, "学生类别"),
  Gender(3, Gender.class, "性别"), Department(4, Department.class, "院系"),
  Major(5, Major.class, "专业"), Direction(6, MajorDirection.class, "方向"),
  Squad(7, Squad.class, "班级"), Level(8, EducationLevel.class, "培养层次"),
  EduType(9, EducationType.class, "培养类型"), StdLabel(11, StdLabel.class, "学生标签");

  private final int id;
  private final String title;
  private final Class<?> contentType;

  ClazzRestrictionMeta(int id, Class<?> contentType, String title) {
    this.id = id;
    this.title = title;
    this.contentType = contentType;
  }

  public static ClazzRestrictionMeta of(int id) {
    for (ClazzRestrictionMeta meta : values()) {
      if (meta.getId() == id) return meta;
    }
    return null;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Class<?> getContentType() {
    return contentType;
  }

  public Class<?> getContentValueType() {
    if (Entity.class.isAssignableFrom(contentType)) {
      return Model.getType(contentType).getIdType();
    } else {
      return contentType;
    }
  }
}
