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
package org.openurp.edu.clazz.service.impl;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Semester;
import org.openurp.code.std.model.StdType;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.service.ClazzPlanRelationService;
import org.openurp.edu.clazz.service.CourseLimitService;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.plan.service.ExecutivePlanQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ClazzPlanRelationServiceImpl extends BaseServiceImpl implements ClazzPlanRelationService {

  private CourseLimitService courseLimitService;

  private EntityDao entityDao;

  public List<Clazz> relatedClazzes(ExecutivePlan plan) {
    OqlBuilder query = OqlBuilder.from(Clazz.class, "clazz");
    query.where("clazz.planId = :planId", plan.getId());
    return entityDao.search(query);
  }

  public List<Clazz> relatedClazzes(ExecutivePlan plan, Semester semester) {
    OqlBuilder query = OqlBuilder.from(Clazz.class, "clazz");
    query.where("clazz.planId = :planId", plan.getId());
    query.where("clazz.semester=:semester", semester);
    return entityDao.search(query);
  }


  public List<ExecutivePlan> possibleRelatePlans(Clazz clazz) {
    List<ExecutivePlan> plans = new ArrayList<ExecutivePlan>();

    List<Squad> squades = courseLimitService.extractSquades(clazz.getEnrollment());
    // 如果有行政班的，那么就关联到行政班对应的培养计划
    if (CollectUtils.isNotEmpty(squades)) {
      for (Squad squad : squades) {
        plans.addAll(entityDao.search(ExecutivePlanQueryBuilder.build(squad)));
      }
      return plans;
    }

    // 如果没有行政班，则通过年级、专业、方向来匹配到培养计划
    String grade = courseLimitService.extractGrade(clazz.getEnrollment());
    if (Strings.isEmpty(grade)) {
      return CollectUtils.newArrayList();
    }

    List<Major> majors = courseLimitService.extractMajors(clazz.getEnrollment());
    if (CollectUtils.isEmpty(majors)) {
      return CollectUtils.newArrayList();
    }

    List<StdType> stdTypes = courseLimitService.extractStdTypes(clazz.getEnrollment());
    if (CollectUtils.isEmpty(stdTypes)) {
      stdTypes.add(null);
    }

    List<MajorDirection> directions = courseLimitService.extractDirections(clazz.getEnrollment());
    if (CollectUtils.isEmpty(directions)) {
      directions.add(null);
    }

    for (Major major : majors) {
      for (StdType stdType : stdTypes) {
        for (MajorDirection direction : directions) {
          plans.addAll(entityDao.search(ExecutivePlanQueryBuilder.build(grade, stdType, major, direction)));
        }
      }
    }
    return plans;
  }

  @Override
  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }
}
