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
package org.openurp.edu.program.plan.service;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.program.model.ExecutivePlan;

/**
 *
 */
public class ExecutivePlanQueryBuilder {

  private ExecutivePlanQueryBuilder() {
  }

  public static OqlBuilder<ExecutivePlan> build(Squad squad) {
    OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
    query.where("plan.program.grade = :grade", squad.getGrade())
        .where(":stdType in elements(plan.program.stdTypes)", squad.getStdType())
        .where("plan.program.major = :major", squad.getMajor());

    if (null == squad.getDirection()) {
      query.where("plan.program.direction is null");
    } else {
      query.where("plan.program.direction =:direction", squad.getDirection());
    }
    return query;
  }

  public static OqlBuilder<ExecutivePlan> build(String grade, StdType stdType, Major major, MajorDirection direction) {
    OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
    query.where("plan.program.grade = :grade", grade).where("plan.program.major = :major", major);

    if (null != stdType) {
      query.where(":stdTypes in elements(plan.program.stdTypes)", stdType);
    }

    if (null == direction) {
      // 注意：这里是故意这样写的，不是BUG
      // query.where("plan.program.direction is null");
    } else {
      query.where("plan.program.direction =:direction", direction);
    }
    return query;
  }
}
