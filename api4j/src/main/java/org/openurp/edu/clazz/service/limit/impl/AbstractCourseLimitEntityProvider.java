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

import org.beangle.commons.bean.PropertyUtils;
import org.beangle.commons.collection.page.PageLimit;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Arrays;
import org.beangle.commons.lang.tuple.Pair;
import org.openurp.base.service.ProjectContext;
import org.openurp.edu.clazz.model.ClazzRestrictionMeta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCourseLimitEntityProvider<T extends Entity<?>> extends AbstractCourseLimitContentProvider<T> {
  protected ProjectContext projectContext;

  @Override
  protected Map<String, T> getContentMap(Object[] content) {
    List<T> entities = entityDao.get(getMeta().getContentType().getName(), "id", content);
    Map<String, T> results = new LinkedHashMap<String, T>();
    for (T entity : entities) {
      results.put(entity.getId().toString(), entity);
    }
    return results;
  }

  public OqlBuilder<T> getQueryBuilder(Object[] content, String term, PageLimit limit) {
    OqlBuilder<T> queryBuilder = OqlBuilder.from(getMeta().getContentType().getName(), "entity");
    if (!Arrays.isEmpty(content)) {
      queryBuilder.where("entity.id not in(:ids)", content);
    }
    if (null != term) {
      addTermCondition(queryBuilder, term);
    }
    queryBuilder.orderBy("id");
    queryBuilder.limit(limit);
    return queryBuilder;
  }

  @Override
  protected List<T> getCascadeContents(Object[] content, String term, PageLimit limit,
                                       Map<ClazzRestrictionMeta, String> cascadeField) {
    OqlBuilder<T> builder = getQueryBuilder(content, term, limit);
    addCascadeQuery(builder, cascadeField);
    return entityDao.search(builder);
  }

  protected void addCascadeQuery(OqlBuilder<T> builder, Map<ClazzRestrictionMeta, String> cascadeField) {

  }

  @Override
  protected List<T> getOtherContents(Object[] content, String term, PageLimit limit) {
    return entityDao.search(getQueryBuilder(content, term, limit));
  }

  protected void addTermCondition(OqlBuilder<T> queryBuilder, String term) {
    StringBuilder sb = new StringBuilder();
    boolean hasName = false;
    try {
      if (String.class.isAssignableFrom(
          Model.getType(getMeta().getContentType()).getPropertyType("name").getReturnedClass())) {
        sb.append("entity.name like :codeOrName ");
        hasName = true;
      }
    } catch (Exception e) {
    }
    try {
      if (String.class.isAssignableFrom(
          Model.getType(getMeta().getContentType()).getPropertyType("code").getReturnedClass())) {
        if (hasName) {
          sb.append("or ");
        }
        sb.append("entity.code like :codeOrName ");
      }
    } catch (Exception e) {
    }
    if (sb.length() > 0) {
      queryBuilder.where(sb.toString(), "%" + term + "%");
    }
  }

  public Map<String, String> getContentIdTitleMap(String content) {
    List<T> entities = entityDao.get(getMeta().getContentType().getName(), "id",
        getContentValues(content));
    Map<String, String> results = new LinkedHashMap<String, String>();
    for (T entity : entities) {
      Pair<String, String> idTitle = getContentIdTitle(entity);
      results.put(idTitle.getLeft(), idTitle.getRight());
    }
    return results;

  }

  public Pair<String, String> getContentIdTitle(T entity) {
    try {
      String name = null;
      try {
        name = PropertyUtils.getProperty(entity, "shortName");
      } catch (Exception e) {
      }
      if (null == name) name = PropertyUtils.getProperty(entity, "name");
      return new Pair<>(entity.getId().toString(), name);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void setProjectContext(ProjectContext projectContext) {
    this.projectContext = projectContext;
  }
}
