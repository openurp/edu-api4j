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
package org.openurp.edu.clazz.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.tuple.Pair;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.CourseTaker;
import org.openurp.edu.clazz.model.Enrollment;
import org.openurp.edu.clazz.model.ClazzRestriction;
import org.openurp.edu.clazz.model.RestrictionPair;

public interface CourseLimitService {

  /**
   * source中的restrictions统统合并到target中
   *
   * @param target
   * @param source
   */
  @Deprecated
  public void mergeAll(Enrollment target, Enrollment source);

  /**
   * source中的某个restriction合并到target中
   *
   * @param mergeType
   * @param target
   * @param source
   */
  @Deprecated
  public void merge(Long mergeType, Enrollment target, Enrollment source);

  public Map<ClazzRestriction, Pair<Boolean, List<EducationLevel>>> xtractEducationLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<EducationLevel>> xtractEducationLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<Squad>>> xtractSquadLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<Squad>> xtractSquadLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<String>>> xtractGradeLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<String>> xtractGradeLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<StdType>>> xtractStdTypeLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<StdType>> xtractStdTypeLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<Department>>> xtractAttendDepartLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<Department>> xtractAttendDepartLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<Major>>> xtractMajorLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<Major>> xtractMajorLimit(ClazzRestriction group);

  public Map<ClazzRestriction, Pair<Boolean, List<MajorDirection>>> xtractDirectionLimit(
      Enrollment teachclass);

  public Pair<Boolean, List<MajorDirection>> xtractDirectionLimit(ClazzRestriction group);

  public void limitEnrollment(boolean operator, Enrollment teachclass, String... grades);

  public <T extends Entity<?>> void limitEnrollment(boolean operator, Enrollment teachclass, T... entities);

  /**
   * 提交培养层次
   *
   * @param teachclass
   * @return
   */
  public List<EducationLevel> extractEducations(Enrollment teachclass);

  public List<EducationLevel> extractEducations(ClazzRestriction group);

  /**
   * 提取教学任务中的行政班
   *
   * @param teachclass
   * @return
   */
  public List<Squad> extractSquades(Enrollment teachclass);

  public List<Squad> extractSquades(ClazzRestriction group);

  /**
   * 提交教学班中的年级
   *
   * @param teachclass
   * @return
   */
  public String extractGrade(Enrollment teachclass);

  public String extractGrade(ClazzRestriction group);

  /**
   * 提取教学班中的学生类别
   *
   * @param teachclass
   * @return
   */
  public List<StdType> extractStdTypes(Enrollment teachclass);

  public List<StdType> extractStdTypes(ClazzRestriction group);

  /**
   * 提取教学班中的专业
   *
   * @param teachclass
   * @return
   */
  public List<Major> extractMajors(Enrollment teachclass);

  public List<Major> extractMajors(ClazzRestriction group);

  /**
   * 提取教学班中的方向
   *
   * @param teachclass
   * @return
   */
  public List<MajorDirection> extractDirections(Enrollment teachclass);

  public List<MajorDirection> extractDirections(ClazzRestriction group);

  /**
   * 提取教学班中的上课院系
   *
   * @param teachclass
   * @return
   */
  public List<Department> extractAttendDeparts(Enrollment teachclass);

  public List<Department> extractAttendDeparts(ClazzRestriction group);

  /**
   * 提取教学班中的性别
   *
   * @param teachclass
   * @return
   */
  // TODO 系统中使用该方法的已经过期或者获取了以后不传递到前台了
  @Deprecated
  public Gender extractGender(Enrollment teachclass);

  // TODO 无人使用
  @Deprecated
  public Gender extractGender(ClazzRestriction group);

  /**
   * 获得构建器
   *
   * @return
   */
  public RestrictionBuilder builder();

  /**
   * 获得默认条件组的构建器
   *
   * @return
   */
  public RestrictionBuilder builder(Enrollment teachclass);

  /**
   * 获得教学任务中不属于任务中任何一个行政班的选课记录
   *
   * @param teachclass
   * @return
   */
  public Set<CourseTaker> extractLonelyTakers(Enrollment teachclass);

  /**
   * 如果教学任务已经有人选了，则直接返回已经选课的人<br>
   * 如果没有人选，那么返回所有行政班中的人
   *
   * @param teachclass
   * @return
   */
  public Set<CourseTaker> extractPossibleCourseTakers(Clazz clazz);

  /**
   * 判断教学班名称是否自动命名
   *
   * @param clazz
   * @return
   */
  public boolean isAutoName(Clazz clazz);

  public RestrictionPair xtractLimitGroup(ClazzRestriction group);

}
