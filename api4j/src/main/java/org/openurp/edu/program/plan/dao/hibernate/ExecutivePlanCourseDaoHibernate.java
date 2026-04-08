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

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.time.Terms;
import org.openurp.edu.program.plan.dao.ExecutivePlanCourseDao;
import org.openurp.edu.program.model.ExecutivePlanCourse;

import java.util.List;

/**
 * 培养计划课程组内的课程数据存取实现
 */
public class ExecutivePlanCourseDaoHibernate extends HibernateEntityDao implements ExecutivePlanCourseDao {

  public List<ExecutivePlanCourse> getPlanCourseByTerm(Long planId, Integer term) {
    OqlBuilder<ExecutivePlanCourse> query = OqlBuilder.from(ExecutivePlanCourse.class, "planCourse");
    query.join("planCourse.group", "courseGroup");
    query.where("bitand(planCourse.terms,:term) > 0)", new Terms(String.valueOf(term)).value);
    query.where("courseGroup.coursePlan.id=:planId", planId);
    return search(query);
  }

}
