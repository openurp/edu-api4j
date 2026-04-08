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

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.tuple.Pair;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Squad;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;
import org.openurp.edu.clazz.model.ClazzRestriction;
import org.openurp.edu.clazz.model.ClazzRestrictionItem;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;
import org.openurp.edu.clazz.service.CourseLimitExtractorService;

import java.util.ArrayList;
import java.util.List;

public class CourseLimitExtractorServiceImpl extends BaseServiceImpl implements CourseLimitExtractorService {

  public List<EducationLevel> extractEducations(ClazzRestriction restriction) {
    Pair<Boolean, List<EducationLevel>> res = xtractEducationLimit(restriction);
    if (res.getLeft()) {
      return res.getRight();
    }
    return CollectUtils.newArrayList();
  }

  public List<Squad> extractSquades(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Squad.equals(item.getMeta()) && (item.isIncluded())) {
        return entityDao.get(Squad.class, Strings.splitToLong(item.getContents()));
      }
    }
    return CollectUtils.newArrayList();
  }

  public String extractGrade(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Grade.equals(item.getMeta()) && (item.isIncluded())) {
        return item.getContents();
      }
    }
    return null;
  }

  public List<StdType> extractStdTypes(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.StdType.equals(item.getMeta()) && (item.isIncluded())) {
        return entityDao.get(StdType.class, Strings.splitToInt(item.getContents()));
      }
    }
    return CollectUtils.newArrayList();
  }

  public List<Major> extractMajors(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Major.equals(item.getMeta().getId()) && (item.isIncluded())) {
        return entityDao.get(Major.class, Strings.splitToLong(item.getContents()));
      }
    }
    return CollectUtils.newArrayList();
  }

  public List<MajorDirection> extractDirections(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Direction.equals(item.getMeta().getId()) && (item.isIncluded())) {
        return entityDao.get(MajorDirection.class, Strings.splitToLong(item.getContents()));
      }
    }
    return CollectUtils.newArrayList();
  }

  public List<Department> extractAttendDeparts(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Department.equals(item.getMeta().getId()) && (item.isIncluded())) {
        return entityDao.get(Department.class, Strings.splitToInt(item.getContents()));
      }
    }
    return CollectUtils.newArrayList();
  }

  public Gender extractGender(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Gender.equals(item.getMeta().getId()) && (item.isIncluded())) {
        return entityDao.get(Gender.class, Strings.splitToInt(item.getContents())).get(0);
      }
    }
    return null;
  }

  public Pair<Boolean, List<EducationLevel>> xtractEducationLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Level.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<EducationLevel>>(item.isIncluded(), entityDao.get(EducationLevel.class, Strings.splitToInt(item.getContents())));
      }
    }
    return new Pair<Boolean, List<EducationLevel>>(Boolean.TRUE, new ArrayList<EducationLevel>());
  }

  public Pair<Boolean, List<Squad>> xtractSquadLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Squad.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<Squad>>(item.isIncluded(), entityDao.get(Squad.class, Strings.splitToLong(item.getContents())));
      }
    }
    return new Pair<Boolean, List<Squad>>(Boolean.TRUE, new ArrayList<Squad>());
  }

  public Pair<Boolean, List<Department>> xtractAttendDepartLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Department.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<Department>>(item.isIncluded(), entityDao.get(Department.class, Strings.splitToInt(item.getContents())));
      }
    }
    return new Pair<Boolean, List<Department>>(Boolean.TRUE, new ArrayList<Department>());
  }

  public Pair<Boolean, List<MajorDirection>> xtractDirectionLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Direction.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<MajorDirection>>(item.isIncluded(), entityDao.get(MajorDirection.class, Strings.splitToLong(item.getContents())));
      }
    }
    return new Pair<Boolean, List<MajorDirection>>(Boolean.TRUE, new ArrayList<MajorDirection>());
  }

  public Pair<Boolean, List<String>> xtractGradeLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Gender.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<String>>(item.isIncluded(), CollectUtils.newArrayList(item.getContents()));
      }
    }
    return new Pair<Boolean, List<String>>(Boolean.TRUE, new ArrayList<String>());
  }

  public Pair<Boolean, List<Major>> xtractMajorLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.Major.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<Major>>(item.isIncluded(), entityDao.get(Major.class, Strings.splitToLong(item.getContents())));
      }
    }
    return new Pair<Boolean, List<Major>>(Boolean.TRUE, new ArrayList<Major>());
  }

  public Pair<Boolean, List<StdType>> xtractStdTypeLimit(ClazzRestriction restriction) {
    for (ClazzRestrictionItem item : restriction.getItems()) {
      if (ClazzRestrictionMeta.StdType.equals(item.getMeta().getId())) {
        return new Pair<Boolean, List<StdType>>(item.isIncluded(), entityDao.get(StdType.class, Strings.splitToInt(item.getContents())));
      }
    }
    return new Pair<Boolean, List<StdType>>(Boolean.TRUE, new ArrayList<StdType>());
  }

}
