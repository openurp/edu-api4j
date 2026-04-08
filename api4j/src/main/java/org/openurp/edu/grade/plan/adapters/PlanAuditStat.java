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
package org.openurp.edu.grade.plan.adapters;

import org.openurp.edu.grade.plan.model.AuditPlanResult;
import org.openurp.edu.grade.plan.model.AuditStat;

public class PlanAuditStat extends AuditStat {

  private final AuditPlanResult planResult;

  public PlanAuditStat(AuditPlanResult planResult) {
    this.planResult = planResult;
  }

  public float getPassedCredits() {
    return planResult.getPassedCredits();
  }

  public void setPassedCredits(float creditsCompleted) {
    planResult.setPassedCredits(creditsCompleted);
  }

  public float getRequiredCredits() {
    return planResult.getRequiredCredits();
  }

  public void setRequiredCredits(float creditsRequired) {
    this.planResult.setRequiredCredits(creditsRequired);
  }

}
