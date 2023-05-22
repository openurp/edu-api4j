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

import org.beangle.commons.text.i18n.Message;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.Session;

/**
 * 排课验证
 * service application interface
 *
 *
 */
public interface ClazzScheduleChecker {

  public Message check(Clazz clazz, List<Session> activities);
}