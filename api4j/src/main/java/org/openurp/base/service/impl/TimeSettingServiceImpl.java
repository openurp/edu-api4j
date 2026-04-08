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
package org.openurp.base.service.impl;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.edu.model.TimeSetting;
import org.openurp.base.model.Campus;
import org.openurp.base.service.TimeSettingService;

import java.util.List;

public class TimeSettingServiceImpl extends BaseServiceImpl implements TimeSettingService {

  public void saveTimeSetting(TimeSetting setting) {
    entityDao.saveOrUpdate(setting);
  }

  public void removeTimeSetting(TimeSetting setting) {
    entityDao.remove(setting);
  }

  public TimeSetting getClosestTimeSetting(Project project, Semester semester, Campus campus) {
    if (null != campus) {
      List<TimeSetting> settings = getSettings(project, semester, campus);
      if (!settings.isEmpty()) return settings.get(0);
    }
    List<TimeSetting> settings = getSettings(project, semester, null);
    if (!settings.isEmpty()) {
      return settings.get(0);
    } else {
      return null;
    }
  }

  private List<TimeSetting> getSettings(Project project, Semester semester, Campus campus) {
    var query = OqlBuilder.from(TimeSetting.class, "ts");
    query.where("ts.project=:project", project);
    if (null != campus) query.where("ts.campus=:campus", campus);
    query.where("ts.beginOn <=:now and (ts.endOn is null or ts.endOn >=:now)", semester.getBeginOn());
    query.cacheable();
    return entityDao.search(query);
  }
}
