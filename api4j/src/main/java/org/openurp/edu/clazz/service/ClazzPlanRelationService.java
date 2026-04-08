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
package org.openurp.edu.clazz.service;

import org.openurp.base.edu.model.Semester;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.program.model.ExecutivePlan;

import java.util.List;

/**
 * 维护教学任务和培养计划之间关系的Service，主要工作是查询
 */
public interface ClazzPlanRelationService {


  /**
   * 获得和计划关联的教学任务
   *
   * @param plan
   * @return
   */
  public List<Clazz> relatedClazzes(ExecutivePlan plan);

  /**
   * 获得和计划关联的教学任务
   *
   * @param plan
   * @param semester
   * @return
   */
  public List<Clazz> relatedClazzes(ExecutivePlan plan, Semester semester);

  /**
   * 可能会和这个课程有关联的培养计划(也就是说不是直接查询数据库中已有的关联关系)
   *
   * @param clazz
   * @return
   */
  public List<ExecutivePlan> possibleRelatePlans(Clazz clazz);

}
