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
package org.openurp.edu.clazz.service;

import org.beangle.commons.dao.query.builder.Condition;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Squad;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;
import org.openurp.code.std.model.StdType;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;

public class CourseLimitUtils {

  public static <T extends Entity<?>> Condition build(T entity, String alias) {
    ClazzRestrictionMeta meta = null;
    if (entity instanceof Gender) {
      meta = ClazzRestrictionMeta.Gender;
    } else if (entity instanceof StdType) {
      meta = ClazzRestrictionMeta.StdType;
    } else if (entity instanceof Department) {
      meta = ClazzRestrictionMeta.Department;
    } else if (entity instanceof Major) {
      meta = ClazzRestrictionMeta.Major;
    } else if (entity instanceof MajorDirection) {
      meta = ClazzRestrictionMeta.Direction;
    } else if (entity instanceof Squad) {
      meta = ClazzRestrictionMeta.Squad;
    } else if (entity instanceof EducationLevel) {
      meta = ClazzRestrictionMeta.Level;
    } else {
      throw new RuntimeException("not supported limit meta class " + entity.getClass().getName());
    }
    return build(meta, alias, entity.getId().toString());
  }

  public static Condition build(ClazzRestrictionMeta meta, String alias, String id) {
    String template = " alias.meta = :meta and (case when alias.included=true and locate(:values,','||alias.contents||',')>0 then 1 else 0 end) = 1 ";
    String paramName = "metaValue" + randomInt();
    String paramName2 = paramName + "s";
    template = Strings.replace(template, "alias", alias);
    template = Strings.replace(template, "values", paramName2);
    template = Strings.replace(template, "value", paramName);
    return new Condition(template).param(meta).param(id).param("," + id + ",");
  }

  private static String randomInt() {
    String d = String.valueOf(Math.random());
    d = Strings.replace(d, ".", "");
    d = d.substring(0, 8);
    return d;
  }
}
