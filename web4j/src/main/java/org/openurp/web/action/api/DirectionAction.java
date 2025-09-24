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
package org.openurp.web.action.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletResponse;

import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Arrays;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.edu.web.action.BaseAction;
import org.openurp.web.action.internal.ClazzGsonBuilderHelper;
import org.openurp.web.action.internal.ClazzGsonBuilderWorker;

import com.google.gson.Gson;

/**
 * 专业web service api<br>
 * entry: api/major<br>
 */
public class DirectionAction extends BaseAction {

  /**
   * Entry : api/direction!get.action<br>
   * Accept params: <br>
   * <ul>
   * <li>majorIds</li>
   * <li>departmentIds</li>
   * <li>pageNo 页数</li>
   * <li>pageSize 页长</li>
   * <li>direction.属性</li>
   * </ul>
   * Return: json<br>
   *
   * @throws IOException
   */
  public String json() throws IOException {
    Integer[] majorIds = Strings.splitToInt(get("majorIds"));
    Integer[] departmentIds = Strings.splitToInt(get("departmentIds"));
    String warnings = "";
    if (Arrays.isEmpty(majorIds)) {
      warnings += "请先选择专业";
    }
    if (Strings.isNotBlank(warnings)) {
      put("warnings", warnings);
      return forward("directionsJSON");
    }

    List<MajorDirection> directions = new ArrayList<MajorDirection>();
    if (Strings.isBlank(warnings)) {
      OqlBuilder<MajorDirection> query = OqlBuilder.from(MajorDirection.class, "direction");
      query.where("direction.major.project.id = :projectId", getIntId("project"))
          .where("direction.major.id in (:majorIds)", majorIds).orderBy("direction.code, direction.name");
      if (null != departmentIds && departmentIds.length > 0) {
        query.where("exists(from direction.journals dd where dd.depart.id in (:departIds))", departmentIds);
      }
      directions = entityDao.search(query);
    }

    Gson gson = new Gson();
    String json = gson
        .toJson(ClazzGsonBuilderHelper.genGroupResult(directions, warnings, new ClazzGsonBuilderWorker() {
          public void dirtywork(Object object, Map<String, Object> groups) {
            MajorDirection rawEntity = (MajorDirection) object;
            String groupName = rawEntity.getMajor().getName();
            if (groups.get(groupName) == null) {
              groups.put(groupName, new ArrayList<Map<String, Object>>());
            }
            List<Map<String, Object>> entities = (List<Map<String, Object>>) groups.get(groupName);
            Map<String, Object> entity = new TreeMap<String, Object>();
            entity.put("id", rawEntity.getId());
            entity.put("name", rawEntity.getName());
            entity.put("code", rawEntity.getCode());
            entities.add(entity);
          }
        }));
    HttpServletResponse response = getResponse();
    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().write(json);
    response.getWriter().close();
    return null;
  }

}
