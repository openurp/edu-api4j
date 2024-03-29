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

import org.openurp.base.hr.model.Teacher;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.ClazzActivity;

public class CourseOccupanceComment {
  public static String generate(ClazzActivity s) {
    Clazz clazz = s.getClazz();
    return clazz.getCourse().getName() + "(" + clazz.getCourse().getCode() + " " + getTeacherNames(s) + " " + clazz.getCrn() + ")";
  }

  private static String getTeacherNames(ClazzActivity s) {
    if (null != s.getTeachers() && !s.getTeachers().isEmpty()) {
      StringBuffer buf = new StringBuffer(10);
      for (Teacher teacher : s.getTeachers()) {
        buf.append(teacher.getName()).append(",");
      }
      buf.deleteCharAt(buf.length() - 1);
      return buf.toString();
    } else {
      return "";
    }
  }
}
