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
package org.openurp.edu.web.helper;

import java.util.Date;

import org.beangle.commons.collection.Order;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.beangle.struts2.helper.Params;
import org.beangle.struts2.helper.QueryHelper;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.model.AuditStatus;
import org.openurp.web.helper.SearchHelper;
import org.openurp.edu.program.model.Program;
import org.openurp.edu.program.model.ExecutivePlan;

public class ExecutivePlanSearchHelper extends SearchHelper {

  public OqlBuilder<Program> buildProgramQuery() {
    OqlBuilder<Program> query = OqlBuilder.from(Program.class, "program")
        .where("program.majorProgram = true");
    QueryHelper.populateConditions(query);
    if (Strings.isEmpty(Params.get(Order.ORDER_STR))) {
      query.orderBy(new Order("program.grade desc"));
    } else {
      query.orderBy(Params.get(Order.ORDER_STR));
    }
    query.limit(QueryHelper.getPageLimit());
    String status = Params.get("fake.status");
    if (Strings.isNotBlank(status)) {
      query.where("program.status = :status", AuditStatus.valueOf(status.toUpperCase()));
    }
    Date now = new Date();
    // 活跃的培养计划:在有效期内的和有效期在当前时间之后的培养计划都是活跃的培养计划，否则不是
    Boolean valid = Params.getBoolean("fake.valid");
    if (valid != null && valid) {
      query.where("(program.endOn is null or :now1 < program.endOn)", now);
    } else if (valid != null && !valid) {
      query.where(":now1 >= program.endOn", now);
    }
    return query;
  }

  public OqlBuilder<ExecutivePlan> buildPlanQuery() {
    OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
    QueryHelper.populateConditions(query);
    if (Strings.isEmpty(Params.get(Order.ORDER_STR))) {
      query.orderBy(new Order("plan.program.grade desc"));
    } else {
      query.orderBy(Params.get(Order.ORDER_STR));
    }
    query.limit(QueryHelper.getPageLimit());

    String status = Params.get("fake.status");
    if (Strings.isNotBlank(status)) {
      query.where("plan.program.status = :status", AuditStatus.valueOf(status.toUpperCase()));
    }

    Date now = new Date();
    // 活跃的培养计划:在有效期内的和有效期在当前时间之后的培养计划都是活跃的培养计划，否则不是
    Boolean valid = Params.getBoolean("fake.valid");
    if (valid != null && valid) {
      query.where("(plan.program.endOn is null or :now1 < plan.program.endOn)", now);
    } else if (valid != null && !valid) {
      query.where(":now1 >= plan.program.endOn", now);
    }
    query.join("left outer", "plan.program.direction", "direction");
    return query;
  }

  /**
   * 添加在某个学期下活跃的/有效的培养计划的查询条件
   */
  public void addSemesterActiveCondition(OqlBuilder<ExecutivePlan> query, Semester semester) {
    Date semesterBeg = semester.getBeginOn();
    Date semesterEnd = semester.getEndOn();

    query.where("(" + "(plan.program.endOn is null     and :semesterEnd1 >= plan.program.beginOn) or "
        + "(plan.program.endOn is not null and :semesterEnd2 >= plan.program.beginOn and :semesterBeg <= plan.program.endOn)"
        + ")", semesterEnd, semesterEnd, semesterBeg);
  }
}
