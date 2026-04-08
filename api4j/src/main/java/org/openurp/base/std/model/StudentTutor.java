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

import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Type;
import org.openurp.base.hr.model.Teacher;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity(name = "org.openurp.base.std.model.StudentTutor")
public class StudentTutor extends LongIdObject implements Comparable<StudentTutor> {

  @ManyToOne(fetch = FetchType.LAZY)
  private Student std;

  @ManyToOne(fetch = FetchType.LAZY)
  private Teacher tutor;

  private int idx;

  @NotNull
  @Type(type = "org.beangle.orm.hibernate.udt.IDEnumType")
  private Tutorship tutorship;

  @Override
  public int compareTo(StudentTutor o) {
    return this.idx - o.idx;
  }

  public Student getStd() {
    return std;
  }

  public void setStd(Student std) {
    this.std = std;
  }

  public Teacher getTutor() {
    return tutor;
  }

  public void setTutor(Teacher tutor) {
    this.tutor = tutor;
  }

  public int getIdx() {
    return idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

  public Tutorship getTutorship() {
    return tutorship;
  }

  public void setTutorship(Tutorship tutorship) {
    this.tutorship = tutorship;
  }
}
