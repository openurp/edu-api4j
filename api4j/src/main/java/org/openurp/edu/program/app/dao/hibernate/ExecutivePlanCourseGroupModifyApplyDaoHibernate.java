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
package org.openurp.edu.program.app.dao.hibernate;

import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.edu.program.app.dao.ExecutivePlanCourseGroupModifyApplyDao;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModify;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModifyDetailAfter;
import org.openurp.edu.program.app.model.ExecutiveCourseGroupModifyDetailBefore;

public class ExecutivePlanCourseGroupModifyApplyDaoHibernate extends HibernateEntityDao
    implements ExecutivePlanCourseGroupModifyApplyDao {

  public void saveModifyApply(ExecutiveCourseGroupModify apply, ExecutiveCourseGroupModifyDetailBefore before,
                              ExecutiveCourseGroupModifyDetailAfter after) {
    saveOrUpdate(apply);
    if (before != null) {
      apply.setOldPlanCourseGroup(before);
      before.setApply(apply);
      saveOrUpdate(before);
    }
    if (after != null) {
      apply.setNewPlanCourseGroup(after);
      after.setApply(apply);
      saveOrUpdate(after);
    }
  }
}
