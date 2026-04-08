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

import org.openurp.edu.program.model.ExecutivePlan;

/**
 * 个人培养计划同步的时候出现的异常
 * 从eams-3shufe移植
 */
public class PersonalPlanSyncException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 6670641749888531624L;

  private String name = "没有找到和该生的个人培养计划匹配的专业培养计划";

  private String enName = "There are no Major Teach Plan matched with this Student's Teach Plan";

  private ExecutivePlan executePlan;

  public PersonalPlanSyncException(ExecutivePlan p) {
    super();
    this.executePlan = p;
  }

  public PersonalPlanSyncException() {
    super();
  }

  public PersonalPlanSyncException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public PersonalPlanSyncException(String arg0) {
    super(arg0);
  }

  public PersonalPlanSyncException(Throwable arg0) {
    super(arg0);
  }

  public String getEnName() {
    return enName;
  }

  public String getName() {
    return name;
  }

  public ExecutivePlan getExecutivePlan() {
    return executePlan;
  }

  public void setExecutivePlan(ExecutivePlan executePlan) {
    this.executePlan = executePlan;
  }

}
