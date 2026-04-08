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

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.code.edu.model.CourseType;
import org.openurp.edu.program.plan.dao.ExecutivePlanCourseGroupDao;
import org.openurp.edu.program.model.ExecutiveCourseGroup;

/**
 * 课程组信息存取实现类
 */

public class ExecutivePlanCourseGroupDaoHibernate extends HibernateEntityDao implements ExecutivePlanCourseGroupDao {

  public CourseType getCourseType(Long planId, Long courseId) {
    OqlBuilder<ExecutiveCourseGroup> query = OqlBuilder.from(ExecutiveCourseGroup.class, "courseGroup");
    query.select("select courseGroup.courseType").join("courseGroup.planCourses", "planCourse")
        .join("courseGroup.coursePlan", "plan").where("plan.id=:planId", planId)
        .where("planCourse.course.id=:courseId", courseId);
    List rs = search(query);
    return (CourseType) (rs.isEmpty() ? null : rs.get(0));
  }

  @SuppressWarnings("unchecked")
  private void swap(List anyList, int index1, int index2) {
    Object o1 = anyList.get(index1);
    Object o2 = anyList.get(index2);
    anyList.set(index2, o1);
    anyList.set(index1, o2);
  }

}
