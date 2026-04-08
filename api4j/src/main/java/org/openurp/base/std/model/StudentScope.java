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
package org.openurp.base.std.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.entity.Component;
import org.openurp.base.edu.model.MajorDirection;
import org.openurp.base.edu.model.Major;
import org.openurp.base.edu.model.Project;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.code.std.model.StdType;

/**
 * 学生范围
 */
@Embeddable
public class StudentScope implements Component {

  /** 年级 */
  @Size(max = 255)
  private String grades;

  /** 项目 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Project project;

  /** 培养层次集合 */
  @ManyToMany
  private Set<EducationLevel> levels = new HashSet<EducationLevel>();

  /** 学生类别集合 */
  @ManyToMany
  private Set<StdType> stdTypes = new HashSet<StdType>();

  /** 部门集合 */
  @ManyToMany
  private Set<Department> departments = new HashSet<Department>();

  /** 专业集合 */
  @ManyToMany
  private Set<Major> majors = new HashSet<Major>();

  /** 专业方向集合 */
  @ManyToMany
  private Set<MajorDirection> directions = new HashSet<MajorDirection>();

  public Set<Department> getDepartments() {
    return departments;
  }

  public void setDepartments(Set<Department> departments) {
    this.departments = departments;
  }

  public Set<Major> getMajors() {
    return majors;
  }

  public void setMajors(Set<Major> majors) {
    this.majors = majors;
  }

  public Set<MajorDirection> getDirections() {
    return directions;
  }

  public void setDirections(Set<MajorDirection> directions) {
    this.directions = directions;
  }

  public String getGrades() {
    return grades;
  }

  public void setGrades(String grades) {
    this.grades = grades;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public Set<StdType> getStdTypes() {
    return stdTypes;
  }

  public void setStdTypes(Set<StdType> stdTypes) {
    this.stdTypes = stdTypes;
  }

  public Set<EducationLevel> getLevels() {
    return levels;
  }

  public void setLevels(Set<EducationLevel> levels) {
    this.levels = levels;
  }

  /**
   * 检验两个学生范围是否有交叉
   * TODO 该方法目前不完善
   */
  public boolean overlappedWith(StudentScope scope) {
    return scope.getProject().equals(this.project)
        && CollectUtils.intersection(scope.getStdTypes(), this.stdTypes).size() > 0
        && CollectUtils.intersection(scope.getLevels(), this.levels).size() > 0;
  }

}
