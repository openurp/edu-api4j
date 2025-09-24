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

import org.beangle.commons.dao.EntityDao;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.std.model.Grade;
import org.openurp.base.std.model.Student;
import org.openurp.edu.program.model.Program;
import org.openurp.edu.program.plan.service.ProgramGenParameter;

import java.text.MessageFormat;

/**
 * Program命名帮助类
 */
public class ProgramNamingHelper {

  /**
   * 培养方案命名格式:专业 方向
   */
  private static final String NAJOR_NAMING_FMT = "{0} {1}{2}";

  private static final String PERSONAL_NAMING_FMT = "{0}({1})个人计划";

  /**
   * 对专业program进行命名
   *
   * @param program persisted Program
   * @return
   */
  public static String name(Program program, EntityDao entityDao) {
    return name(entityDao, program.getGrade(), program.getMajor(), program.getDirection());
  }

  /**
   * 对个人program进行命名
   *
   * @param program persisted Program
   * @param std
   * @return
   */
  public static String name(Student std) {
    return MessageFormat.format(PERSONAL_NAMING_FMT, std.getName(), std.getCode());
  }

  public static String name(ProgramGenParameter genParameter, EntityDao entityDao) {
    return name(entityDao, genParameter.getGrade(), genParameter.getMajor(), genParameter.getDirection());
  }

  public static String name(org.openurp.edu.program.major.service.MajorPlanGenParameter genParameter, EntityDao entityDao) {
    return name(entityDao, genParameter.getGrade(), genParameter.getMajor(), genParameter.getDirection());
  }

  private static String name(EntityDao entityDao, Grade grade, Major major, MajorDirection direction) {
    String gradeCode = entityDao.get(Grade.class, grade.getId()).getCode();
    String majorName = entityDao.get(Major.class, major.getId()).getName();
    String directionName = "";
    if (null != direction) {
      directionName = "/" + entityDao.get(MajorDirection.class, direction.getId()).getName();
    }
    return MessageFormat.format(NAJOR_NAMING_FMT, gradeCode, majorName, directionName);
  }

}
