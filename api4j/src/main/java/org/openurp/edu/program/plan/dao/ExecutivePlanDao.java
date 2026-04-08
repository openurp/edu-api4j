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
package org.openurp.edu.program.plan.dao;

import java.util.List;

import org.openurp.code.edu.model.EducationLevel;
import org.openurp.base.edu.model.Major;
import org.openurp.edu.program.model.ExecutivePlan;

/**
 */
public interface ExecutivePlanDao {

  public List<ExecutivePlan> getExecutivePlanList(String grade, Major major, EducationLevel level);

  /**
   * 获得指定id的培养计划
   *
   * @param planIdSeq
   * @return
   */
  public List<ExecutivePlan> getExecutivePlans(Long[] planIds);

}
