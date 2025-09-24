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

import org.beangle.commons.lang.tuple.Pair;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.person.model.Gender;
import org.openurp.code.std.model.StdType;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Squad;
import org.openurp.edu.clazz.model.ClazzRestriction;

public interface CourseLimitExtractorService {

  public Pair<Boolean, List<EducationLevel>> xtractEducationLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<Squad>> xtractSquadLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<String>> xtractGradeLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<StdType>> xtractStdTypeLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<Department>> xtractAttendDepartLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<Major>> xtractMajorLimit(ClazzRestriction restriction);

  public Pair<Boolean, List<MajorDirection>> xtractDirectionLimit(ClazzRestriction restriction);

  /**
   * 提取培养层次
   *
   * @param restriction
   * @return
   */
  public List<EducationLevel> extractEducations(ClazzRestriction restriction);

  /**
   * 提取Restriction中的行政班
   *
   * @param restriction
   * @return
   */
  public List<Squad> extractSquades(ClazzRestriction restriction);

  /**
   * 提交Restriction中的年级
   *
   * @param restriction
   * @return
   */
  public String extractGrade(ClazzRestriction restriction);

  /**
   * 提取Restriction中的学生类别
   *
   * @param restriction
   * @return
   */
  public List<StdType> extractStdTypes(ClazzRestriction restriction);

  /**
   * 提取Restriction中的专业
   *
   * @param restriction
   * @return
   */
  public List<Major> extractMajors(ClazzRestriction restriction);

  /**
   * 提取Restriction中的方向
   *
   * @param restriction
   * @return
   */
  public List<MajorDirection> extractDirections(ClazzRestriction restriction);

  /**
   * 提取Restriction中的上课院系
   *
   * @param restriction
   * @return
   */
  public List<Department> extractAttendDeparts(ClazzRestriction restriction);

  /**
   * 提取Restriction中的性别
   *
   * @param restriction
   * @return
   */
  public Gender extractGender(ClazzRestriction restriction);

}
