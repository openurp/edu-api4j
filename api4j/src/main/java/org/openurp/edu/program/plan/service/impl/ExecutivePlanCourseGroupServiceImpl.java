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
package org.openurp.edu.program.plan.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Objects;
import org.beangle.commons.lang.Strings;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.plan.service.ExecutivePlanCourseGroupService;
import org.openurp.edu.program.plan.service.ExecutivePlanService;

/**
 * 培养计划课程组服务实现类
 */
public class ExecutivePlanCourseGroupServiceImpl extends BaseServiceImpl implements ExecutivePlanCourseGroupService {

  private ExecutivePlanService executePlanService;

  private PlanCourseGroupCommonDao planCourseGroupCommonDao;

  public void removeCourseGroup(Long groupId) {
    ExecutiveCourseGroup group = entityDao.get(ExecutiveCourseGroup.class, groupId);
    removeCourseGroup(group);
  }

  public void removeCourseGroup(ExecutiveCourseGroup group) {
    planCourseGroupCommonDao.removeCourseGroup(group);
  }

  public ExecutivePlanService getExecutivePlanService() {
    return executePlanService;
  }

  public void saveOrUpdateCourseGroup(ExecutiveCourseGroup group) {
    planCourseGroupCommonDao.saveOrUpdateCourseGroup(group);
  }

  public void courseGroupMoveDown(ExecutiveCourseGroup courseGroup) {
    planCourseGroupCommonDao.updateCourseGroupMoveDown(courseGroup);
  }

  public void courseGroupMoveUp(ExecutiveCourseGroup courseGroup) {
    planCourseGroupCommonDao.updateCourseGroupMoveUp(courseGroup);
  }

  public void setExecutivePlanService(ExecutivePlanService executePlanService) {
    this.executePlanService = executePlanService;
  }

  public void setPlanCourseGroupCommonDao(PlanCourseGroupCommonDao planCourseGroupCommonDao) {
    this.planCourseGroupCommonDao = planCourseGroupCommonDao;
  }

  public void move(CourseGroup node, CourseGroup location, int index) {
    if (Objects.equals(node.getParent(), location)) {
      if (Numbers.toInt(node.getIndexno()) != index) {
        shiftCode(node, location, index);
      }
    } else {
      if (null != node.getParent()) {
        node.getParent().getChildren().remove(node);
      }
      node.setParent(location);
      shiftCode(node, location, index);
    }
  }

  private void shiftCode(CourseGroup node, CourseGroup newParent, int index) {
    List<CourseGroup> sibling = null;
    if (null != newParent) sibling = newParent.getChildren();
    else {
      sibling = CollectUtils.newArrayList();
      for (CourseGroup m : node.getPlan().getTopCourseGroups()) {
        if (null == m.getParent()) sibling.add(m);
      }
    }
    Collections.sort(sibling);
    sibling.remove(node);
    index--;
    if (index > sibling.size()) {
      index = sibling.size();
    }
    sibling.add(index, node);
    int nolength = String.valueOf(sibling.size()).length();
    Set<CourseGroup> nodes = CollectUtils.newHashSet();
    for (int seqno = 1; seqno <= sibling.size(); seqno++) {
      CourseGroup one = sibling.get(seqno - 1);
      generateCode(one, Strings.leftPad(String.valueOf(seqno), nolength, '0'), nodes);
    }
    entityDao.saveOrUpdate(nodes);
    entityDao.refresh(node);
    entityDao.refresh(node.getPlan());
  }

  public void genIndexno(CourseGroup group, String indexno) {
    if (null == group.getParent()) {
      group.setIndexno(indexno);
    } else {
      if (StringUtils.isEmpty(indexno)) {
        group.setIndexno(
            Strings.concat(group.getParent().getIndexno(), ".", String.valueOf(group.getIndex())));
      } else {
        group.setIndexno(Strings.concat(group.getParent().getIndexno(), ".", indexno));
      }
    }
  }

  private void generateCode(CourseGroup node, String indexno, Set<CourseGroup> nodes) {
    nodes.add(node);
    if (null != indexno) {
      genIndexno(node, indexno);
    } else {
      genIndexno(node, null);
    }
    if (null != node.getChildren()) {
      for (CourseGroup m : node.getChildren()) {
        generateCode(m, null, nodes);
      }
    }
  }

  public boolean hasSameGroupInOneLevel(CourseGroup courseGroup, CoursePlan plan, CourseGroup parent) {
    OqlBuilder<ExecutiveCourseGroup> builder = OqlBuilder.from(ExecutiveCourseGroup.class, "courseGroup");
    builder.where("courseGroup.courseType = :courseType", courseGroup.getCourseType());
    builder.where("courseGroup.plan = :plan", plan);
    if (courseGroup.isPersisted()) {
      builder.where("courseGroup.id <> :groupId", courseGroup.getId());
    }
    if (parent == null) {
      builder.where("courseGroup.parent is null");
    } else {
      builder.where("courseGroup.parent = :parent", parent);
    }
    return !entityDao.search(builder).isEmpty();
  }
}
