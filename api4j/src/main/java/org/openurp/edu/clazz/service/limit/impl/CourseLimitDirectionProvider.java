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
package org.openurp.edu.clazz.service.limit.impl;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;

import java.util.Date;
import java.util.Map;

public class CourseLimitDirectionProvider extends AbstractCourseLimitEntityProvider<MajorDirection> {
  @Override
  protected void addCascadeQuery(OqlBuilder<MajorDirection> builder, Map<ClazzRestrictionMeta, String> cascadeField) {
    builder.where("entity.project = :project", projectContext.getProject());
    if (cascadeField.isEmpty()) {
      return;
    }
    String majorIds = cascadeField.get(ClazzRestrictionMeta.Major);
    String departIds = cascadeField.get(ClazzRestrictionMeta.Department);
    String levelIds = cascadeField.get(ClazzRestrictionMeta.Level);
    if (Strings.isNotBlank(majorIds)) {
      builder.where("entity.major.id in (:majorIds)", Strings.splitToLong(majorIds));
    }
    if (Strings.isNotBlank(departIds) || Strings.isNotBlank(levelIds)) {
      StringBuilder sb = new StringBuilder(
          "exists(from entity.journals journal where journal.beginOn <= :now and (journal.endOn is null or journal.endOn >= :now)");
      if (Strings.isNotBlank(departIds)) {
        sb.append(" and journal.depart.id in (:departIds)");
      }
      if (Strings.isNotBlank(levelIds)) {
        sb.append(" and journal.level.id in (:levelIds)");
      }
      sb.append(")");
      builder.where(sb.toString(), new Date(),
          Strings.isBlank(departIds) ? null : Strings.splitToInt(departIds),
          Strings.isBlank(levelIds) ? null : Strings.splitToInt(levelIds));
    }
  }

  @Override
  public ClazzRestrictionMeta getMeta() {
    return ClazzRestrictionMeta.Direction;
  }

}
