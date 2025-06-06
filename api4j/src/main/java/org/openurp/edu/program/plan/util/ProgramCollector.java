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
package org.openurp.edu.program.plan.util;

import org.beangle.commons.lang.functor.Transformer;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.program.model.MajorPlan;

/**
 * 被CollectionUtils.collect使用，用于从*Plan中收集Program<br>
 * 这个类以后最终可能都是要删除的，因为ExecutivePlan中获取Program的方法是不得以而为之
 */
public class ProgramCollector implements Transformer {
  public static final ProgramCollector INSTANCE = new ProgramCollector();

  public Object apply(Object input) {
    if (input instanceof MajorPlan) {
      return ((MajorPlan) input).getProgram();
    } else if (input instanceof ExecutivePlan) {
      return ((ExecutivePlan) input).getProgram();
    } else {
      return ((StdPlan) input).getProgram();
    }
  }

  private ProgramCollector() {
  }
}
