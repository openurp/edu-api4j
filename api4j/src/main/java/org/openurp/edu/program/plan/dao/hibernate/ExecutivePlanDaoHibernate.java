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
package org.openurp.edu.program.plan.dao.hibernate;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.base.edu.model.Major;
import org.openurp.base.service.SemesterService;
import org.openurp.edu.program.plan.dao.ExecutivePlanCourseGroupDao;
import org.openurp.edu.program.plan.dao.ExecutivePlanDao;
import org.openurp.edu.program.model.ExecutivePlan;

/**
 */
public class ExecutivePlanDaoHibernate extends HibernateEntityDao implements ExecutivePlanDao {

  public List<ExecutivePlan> getExecutivePlanList(String grade, Major major, EducationLevel level) {
    OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
    query.where("plan.program.grade=:grade", grade).where("plan.program.major=:major", major)
        .where("plan.program.level=:level", level);
    return search(query);
  }

  protected SemesterService semesterService;

  protected ExecutivePlanCourseGroupDao executePlanCourseGroupDao;

  protected ExecutivePlan getExecutivePlan(Long id) {
    return get(ExecutivePlan.class, id);
  }

  public List<ExecutivePlan> getExecutivePlans(Long[] planIds) {
    OqlBuilder<ExecutivePlan> query = OqlBuilder.from(ExecutivePlan.class, "plan");
    query.where("plan.id in (:ids)", planIds);
    return search(query);
  }

  public Float getCreditByTerm(ExecutivePlan plan, int term) {
    Range<Integer> termRange = Range.between(1, plan.getProgram().getTermsCount());
    if (!termRange.contains(term)) {
      throw new RuntimeException("term out range");
    } else {
      return null;
    }
  }

  public void setExecutivePlanCourseGroupDao(ExecutivePlanCourseGroupDao executePlanCourseGroupDao) {
    this.executePlanCourseGroupDao = executePlanCourseGroupDao;
  }

  public void setSemesterService(SemesterService semesterService) {
    this.semesterService = semesterService;
  }

}
