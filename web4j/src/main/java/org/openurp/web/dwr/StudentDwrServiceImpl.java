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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.openurp.base.std.model.Student;

public class StudentDwrServiceImpl extends BaseServiceImpl {

  public Map getStudentByProjectAndCode(String code, Integer projectId) {
    OqlBuilder<Student> builder = OqlBuilder.from(Student.class, "student");
    builder.where("student.code =:code", code);
    if (null != projectId) {
      builder.where("student.project.id=:projectId", projectId);
    }
    List<Student> students = entityDao.search(builder);
    if (students.size() > 0) { return toMap(students.get(0)); }
    return null;
  }

  public boolean stdExists(String code) {
    return entityDao.count(Student.class, "code", code) == 1;
  }

  public Map getStudent(String code, Integer projectId) {
    OqlBuilder query = OqlBuilder.from(Student.class, "student");
    query.where("student.code = :code and student.project.id=:projectId", code, projectId);
    List list = entityDao.search(query);
    if (list.isEmpty()) {
      return null;
    } else {
      return toMap((Student) list.get(0));
    }
  }

  private Map toMap(Student std) {
    Map stdMap = new HashMap();
    stdMap.put("id", std.getId());
    stdMap.put("code", std.getCode());

    Map person = new HashMap();
    person.put("name", std.getPerson().getName());
    stdMap.put("person", person);

    Map state = new HashMap();
    person.put("grade", std.getState().getGrade());
    stdMap.put("state", state);

    stdMap.put("stdId", std.getId());
    stdMap.put("name", std.getName());
    Map user = new HashMap();
    stdMap.put("name", std.getName());
    stdMap.put("squad", (null != std.getSquad()) ? std.getSquad().getName() : "");
    stdMap.put("major", (null != std.getMajor()) ? std.getMajor().getName() : "");
    stdMap.put("grade", std.getGrade());
    stdMap.put("inSchool", std.getState().isInschool());
    stdMap.put("department", (null != std.getDepartment()) ? std.getDepartment().getName() : "");
    return stdMap;
  }

  public Map getStudentMap(String code, Integer projectId) {
    OqlBuilder query = OqlBuilder.from(Student.class, "student");
    query.where("student.code = :code and student.project.id=:projectId", code, projectId);
    List list = entityDao.search(query);
    if (list.isEmpty()) {
      return null;
    } else {
      return toMap((Student) list.get(0));
    }
  }

}
