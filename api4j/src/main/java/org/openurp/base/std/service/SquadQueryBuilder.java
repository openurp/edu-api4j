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
package org.openurp.base.std.service;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.model.Department;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.Program;

import java.util.Set;

/**
 *
 */
public class SquadQueryBuilder {

  private SquadQueryBuilder() {
  }

  /**
   * 用于构造根据培养计划，来查找行政班的Query
   *
   * @param plan
   * @return
   */
  public static OqlBuilder<Squad> build(ExecutivePlan plan) {
    Program program = plan.getProgram();
    OqlBuilder<Squad> query = OqlBuilder.from(Squad.class, "squad");
    query.where("squad.grade=:grade", program.getGrade());
    query.where("squad.major=:major", program.getMajor());
    query.where("squad.level=:level", program.getLevel());
    Set<Department> departs= CollectUtils.newHashSet(plan.getDepartment());
    departs.addAll(plan.getDepartment().getChildren());
    query.where("squad.department in(:departments)", departs);
    if (null == program.getDirection()) {
      query.where("squad.direction is null");
    } else {
      query.where("squad.direction = :direction", program.getDirection());
    }
    return query;
  }

}
