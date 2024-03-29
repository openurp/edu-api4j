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
package org.openurp.service.security;

import org.openurp.base.model.User;

/**
 * 学生类别权限不足异常.
 *
 *
 */
public class StdTypeAuthorityException extends RuntimeException {

  private static final long serialVersionUID = 7356207753232573651L;

  public StdTypeAuthorityException(User user, String module) {
    super("StdTypeAuthorityException->[User:]" + user.getName() + " [module:]" + module + "");
  }

}
