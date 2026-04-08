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
package org.openurp.web.dwr;

import jakarta.servlet.http.HttpServletRequest;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.security.core.userdetail.Profile;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Project;
import org.openurp.base.model.Department;
import org.openurp.base.service.ProjectContext;
import org.openurp.base.std.model.Squad;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.std.model.StdType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class ProjectMajorDwr extends AbstractDwr {

  EntityDao entityDao;
  ProjectContext projectContext;

  public String projects() {
    return null;
  }

  public List[] levelAndDeparts(HttpServletRequest request, Integer projectId, String resourceName) {
    try {
      prepareSecurity(request, null);
      Project project = entityDao.get(Project.class, projectId);
      Profile profile = null;
      if (!projectContext.getProfiles().isEmpty()) {
        profile = projectContext.getCurrentProfile();
      }
      List<Object[]> departInfos = CollectUtils.newArrayList();
      java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
      for (Department depart : (null == profile) ? project.getDepartments() : projectContext.getDeparts()) {
        if (depart.getEndOn() == null || depart.getEndOn().after(now)) {
          departInfos.add(new Object[]{depart.getId(), depart.getName()});
        }
      }

      List<Object[]> levelInfos = CollectUtils.newArrayList();
      for (EducationLevel level : (null == profile) ? project.getLevels()
          : projectContext.getEducationLevels()) {
        levelInfos.add(new Object[]{level.getId(), level.getName()});
      }

      List<Object[]> stdTypeInfos = CollectUtils.newArrayList();
      for (StdType stdType : (null == profile) ? project.getStdTypes() : projectContext.getStdTypes()) {
        stdTypeInfos.add(new Object[]{stdType.getId(), stdType.getName()});
      }
      cleanSecurity();
      return new List[]{levelInfos, departInfos, stdTypeInfos};
    } catch (Exception e) {
      e.printStackTrace();
      return new List[]{Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),};
    }
  }

  public List majors(Integer projectId, Integer levelId, Integer departId) {
    if (projectId == null) {
      return Collections.emptyList();
    }
    Date now = new Date();
    OqlBuilder query = OqlBuilder.from(Major.class, "s");
    query.select("str(s.id), s.code,s.name, s.enName").where("s.beginOn<=:now", now)
        .where("(s.endOn is null or s.endOn >= :now)", now).where("s.project.id = :projectId", projectId);
    if (null != levelId && null != departId) {
      query.where(
          "exists(from s.journals md where md.major=s and md.depart.id = :departId and md.level.id = :levelId)",
          departId, levelId);
    } else if (null != departId) {
      query.where("exists(from s.journals md where md.major=s and md.depart.id = :departId)", departId);
    } else if (null != levelId) {
      query.where("exists(from s.journals md where md.major=s and md.level.id = :levelId)", levelId);
    }
    query.orderBy("s.name");
    return entityDao.search(query);
  }

  // FIXME 没有把院系的条件加入进来
  public List<Object[]> directions(Integer levelId, Integer departId, Long majorId) {
    if (null == majorId) {
      return Collections.emptyList();
    }
    Date now = new Date();
    OqlBuilder query = OqlBuilder.from(MajorDirection.class, "s");
    query.select("str(s.id),s.code, s.name, s.enName").where("s.beginOn<=:now", now)
        .where("(s.endOn is null or s.endOn >= :now)", now).where("s.major.id = :majorId", majorId);
    if (null != levelId) {
      query.where("exists(from s.journals as j where j.level.id=:levelId)", levelId);
    }
    if (null != departId) {
      query.where("exists(from s.journals as j where j.depart.id=:departId)", departId);
    }
    query.orderBy("s.name");
    return entityDao.search(query);
  }

  public List<Object[]> adminClasses(String grade, Long directionId, Long majorId) {
    if (null == majorId) {
      return Collections.emptyList();
    }
    String hql = "select str(s.id), s.name " + "from " + Squad.class.getName() + " as s"
        + " where s.beginOn<=:now and (s.endOn is null or s.endOn>=:now) and s.major.id=:majorId";
    if (null != directionId) {
      hql += " and s.direction.id =:directionId";
    }
    if (null != grade) {
      hql += " and s.grade = :grade";
    }
    hql += " order by s.name";
    OqlBuilder<Object[]> builder = OqlBuilder.from(hql);
    builder.param("majorId", majorId);
    builder.param("now", new Date());
    if (null != directionId) {
      builder.param("directionId", directionId);
    }
    if (null != grade) {
      builder.param("grade", grade);
    }
    return entityDao.search(builder);

  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  public void setProjectContext(ProjectContext projectContext) {
    this.projectContext = projectContext;
  }

}
