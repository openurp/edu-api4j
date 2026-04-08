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
package org.openurp.edu.clazz.dao;

import org.beangle.commons.collection.page.Page;
import org.openurp.base.edu.model.Semester;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.service.ClazzFilterStrategy;
import org.openurp.edu.program.model.ExecutivePlan;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface ClazzDao {

  /**
   * 按照指定的类别获得当前学期的所有教学任务
   *
   * @param id
   * @param strategy
   * @return
   */
  public List<Clazz> getClazzesByCategory(Serializable id, ClazzFilterStrategy strategy,
      Collection<Semester> semesters);

  /**
   * 按照指定的类别获取当前学期的固定页面的教学任务
   *
   * @param id
   * @param pageNo
   * @param pageSize
   * @return
   */
  public Page<Clazz> getClazzesByCategory(Serializable id, ClazzFilterStrategy strategy, Semester semester,
      int pageNo, int pageSize);

  /**
   * 依照过滤的类别，批量更新
   *
   * @param attr
   * @param value
   * @param semester
   */
  public int updateClazzByCategory(String attr, Object value, Long id, ClazzFilterStrategy strategy,
      Semester semester);

  /**
   * 通过更新条件查询的覆盖结果
   *
   * @param attr
   * @param value
   * @param clazz
   * @param stdTypeIds
   * @param departIds
   */
  public int updateClazzByCriteria(String attr, Object value, Clazz clazz, Integer[] stdTypeIds,
      Long[] departIds);

  /**
   * 按照学年度学期和给定的类别统计
   *
   * @param strategy
   * @return
   */
  public int countClazz(Serializable id, ClazzFilterStrategy strategy, Semester semester);

  /**
   * 删除教学任务
   *
   * @param clazz
   */
  public void remove(Clazz clazz);

  /**
   * tasks[target] is to be updated,and other's is to be deleted.
   *
   * @param tasks
   * @param target
   */
  public void saveMergeResult(Clazz[] tasks, int target);

  /**
   * 1)保存一个培养计划的生成结果:tasks<br>
   * 2)更新培养计划，记录已经生成的学期标记<br>
   *
   * @param plan 来自于哪个培养计划
   * @param semester 生成到哪个学期？
   * @param clazzes 没有保存的任务
   * @param removeExists 是否删除已经生成过的教学任务
   */
  public void saveGenResult(ExecutivePlan plan, Semester semester, List<Clazz> clazzes, boolean removeExists);

  /**
   * 保存新的教学任务或更新老的教学任务<br>
   * 如果教学任务是新的，那么就生成课程序号<br>
   * 如果教学任务是老的，那么就更新教学任务
   *
   * @param clazz
   */
  public void saveOrUpdate(Clazz clazz);

}
