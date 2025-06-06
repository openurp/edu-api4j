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

import org.beangle.commons.entity.pojo.LongIdObject;
import org.openurp.base.std.model.Student;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity(name = "org.openurp.edu.program.model.StdProgramBinding")
public class StdProgramBinding extends LongIdObject {
    /**
     * 学生
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Student std;

    /**
     * 学生
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Program program;

    public Student getStd() {
        return std;
    }

    public void setStd(Student std) {
        this.std = std;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
