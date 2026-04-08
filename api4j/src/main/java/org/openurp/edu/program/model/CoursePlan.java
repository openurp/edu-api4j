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
package org.openurp.edu.program.model;

import org.beangle.commons.entity.Entity;
import org.openurp.code.edu.model.CourseType;

import java.sql.Date;
import java.util.List;

/**
 * 课程方案
 * </p>
 * <li>1)年级</li>
 * <li>2)培养层次</li>
 * <li>3)学分要求</li>
 * <li>4)课程组</li>
 *
 * @composed 1 has * CourseGroup
 * @depend - - - Degree
 * @since 2009
 */
public interface CoursePlan extends Entity<Long>, Cloneable {
  /**
   * 获得总学分
   *
   * @return 总学分
   */
  public float getCredits();

  /**
   * 设置总学分
   *
   * @param credits 总学分
   */
  public void setCredits(float credits);

  /**
   * 获得计划课程组
   *
   * @return 计划课程组
   */
  public List<CourseGroup> getGroups();

  /**
   * 查询指定类型的组
   *
   * @param type
   * @return
   */
  public CourseGroup getGroup(CourseType type);

  /**
   * 获得顶级课程组
   *
   * @return
   */
  List<CourseGroup> getTopCourseGroups();

  /**
   * 设置计划课程组
   *
   * @param groups 计划课程组
   */
  public void setGroups(List<CourseGroup> groups);

  /**
   * 获得生效时间
   *
   * @return 生效时间
   */
  public Date getBeginOn();

  /**
   * 获得失效时间
   *
   * @return 失效时间
   */
  public Date getEndOn();


  Program getProgram();
}
