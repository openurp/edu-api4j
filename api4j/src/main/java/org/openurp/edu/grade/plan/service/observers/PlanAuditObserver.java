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
package org.openurp.edu.grade.plan.service.observers;

import org.openurp.edu.grade.plan.service.AuditPlanContext;

/**
 * 在审核的时候Observer的notifyBegin先执行<br>
 * 然后是Listener<br>
 * 然后是Observer的notifyEnd
 */
public interface PlanAuditObserver {

  public void notifyStart();

  public boolean notifyBegin(AuditPlanContext context, int index);

  public void notifyEnd(AuditPlanContext context, int index);

  public void finish();

}
