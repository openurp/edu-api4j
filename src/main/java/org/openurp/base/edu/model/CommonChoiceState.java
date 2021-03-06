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
package org.openurp.base.edu.model;

/**
 * 通用的选择状态枚举类，有2种状态：选择、不选择
 */
public enum CommonChoiceState {

  UNDECIDED("未选择"), WANT("选择"), DONTWANT("不选择");

  private final String fullName;

  private CommonChoiceState() {
    fullName = "";
  }

  private CommonChoiceState(String fullName) {
    this.fullName = fullName;
  }

  public String getEnName() {
    return this.name();
  }

  public String getFullName() {
    return this.fullName;
  }

}
