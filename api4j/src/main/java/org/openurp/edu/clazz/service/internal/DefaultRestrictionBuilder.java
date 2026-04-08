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
package org.openurp.edu.clazz.service.internal;

import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Squad;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;
import org.openurp.code.std.model.StdType;
import org.openurp.edu.clazz.model.ClazzRestriction;
import org.openurp.edu.clazz.model.ClazzRestrictionItem;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;
import org.openurp.edu.clazz.service.RestrictionBuilder;

public class DefaultRestrictionBuilder implements RestrictionBuilder {

  ClazzRestriction group = new ClazzRestriction();

  DefaultTeachClassNameStrategy strategy = new DefaultTeachClassNameStrategy();

  public DefaultRestrictionBuilder() {
    super();
  }

  public DefaultRestrictionBuilder(ClazzRestriction group) {
    super();
    this.group = group;
  }

  public RestrictionBuilder inGrades(String... grades) {
    if (grades.length > 0 && grades[0] != null) {
      ClazzRestrictionItem item = getOrCreateItem(ClazzRestrictionMeta.Grade);
      addValues(item, true, grades);
    }
    return this;
  }

  public RestrictionBuilder notInGrades(String... grades) {
    ClazzRestrictionItem item = getOrCreateItem(ClazzRestrictionMeta.Grade);
    addValues(item, false, grades);
    return this;
  }

  public <T extends Entity<?>> RestrictionBuilder in(T... entities) {
    if (entities.length > 0 && entities[0] != null) {
      T first = entities[0];
      ClazzRestrictionItem item = getItem(first);
      addValues(item, true, getIds(entities));
    }
    if (entities.length > 0 && entities[0] instanceof Squad) {
      clear(ClazzRestrictionMeta.Grade);
      clear(ClazzRestrictionMeta.StdType);
      clear(ClazzRestrictionMeta.Department);
      clear(ClazzRestrictionMeta.Major);
      clear(ClazzRestrictionMeta.Direction);
      clear(ClazzRestrictionMeta.Level);
    }

    return this;
  }

  public <T extends Entity<?>> RestrictionBuilder notIn(T... entities) {
    if (entities.length >= 0 && entities[0] != null) {
      T first = entities[0];
      ClazzRestrictionItem item = getItem(first);
      addValues(item, false, getIds(entities));
    }
    return this;
  }

  public RestrictionBuilder clear(ClazzRestrictionMeta meta) {
    ClazzRestrictionItem removed = null;
    for (ClazzRestrictionItem item : group.getItems()) {
      if (item.getMeta().equals(meta)) {
        removed = item;
      }
    }
    if (null != removed) {
      group.getItems().remove(removed);
    }
    return this;
  }

  public ClazzRestriction build() {
    return group;
  }

  /**
   * 获取对象的id字符串数组
   *
   * @param entities
   * @return
   */
  private String[] getIds(Entity<?>... entities) {
    String[] ids = new String[entities.length];
    for (int i = 0; i < entities.length; i++) {
      if (entities[i] == null) {
        continue;
      }
      ids[i] = String.valueOf(entities[i].getId());
    }
    return ids;
  }

  /**
   * 向item中添加多个值
   *
   * @param item
   * @param values
   */
  private void addValues(ClazzRestrictionItem item, boolean contain, String... values) {
    String old = item.getContents();
    if (old.length() > 0) {
      old = Strings.concat(old, ",", Strings.join(values, ","));
    } else {
      old = Strings.join(values, ",");
    }
    if (-1 != old.indexOf(',')) {
      if (!old.startsWith(",")) old = "," + old;
      if (!old.endsWith(",")) old = old + ",";
    }
    item.setContents(old);
    item.setIncluded(contain);
  }

  private ClazzRestrictionItem getOrCreateItem(ClazzRestrictionMeta meta) {
    for (ClazzRestrictionItem item : group.getItems()) {
      if (item.getMeta().equals(meta)) {
        return item;
      }
    }
    ClazzRestrictionItem item = new ClazzRestrictionItem();
    item.setMeta(meta);
    item.setIncluded(true);
    item.setContents("");
    item.setRestriction(group);
    group.getItems().add(item);
    return item;
  }

  private <T> ClazzRestrictionItem getItem(T first) {
    ClazzRestrictionItem item = null;
    if (first instanceof Gender) {
      item = getOrCreateItem(ClazzRestrictionMeta.Gender);
    } else if (first instanceof StdType) {
      item = getOrCreateItem(ClazzRestrictionMeta.StdType);
    } else if (first instanceof Department) {
      item = getOrCreateItem(ClazzRestrictionMeta.Department);
    } else if (first instanceof Major) {
      item = getOrCreateItem(ClazzRestrictionMeta.Major);
    } else if (first instanceof MajorDirection) {
      item = getOrCreateItem(ClazzRestrictionMeta.Direction);
    } else if (first instanceof Squad) {
      item = getOrCreateItem(ClazzRestrictionMeta.Squad);
    } else if (first instanceof EducationLevel) {
      item = getOrCreateItem(ClazzRestrictionMeta.Level);
    } else {
      throw new RuntimeException("no support limit meta class " + first.getClass().getName());
    }
    return item;
  }

}
