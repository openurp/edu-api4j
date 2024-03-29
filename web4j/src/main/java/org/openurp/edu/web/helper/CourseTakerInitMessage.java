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
package org.openurp.edu.web.helper;

import org.beangle.commons.lang.Strings;
import org.beangle.commons.text.i18n.TextResource;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.service.OutputMessage;

public class CourseTakerInitMessage extends OutputMessage {

  Clazz clazz;

  public CourseTakerInitMessage(String key, Clazz clazz) {
    this.key = key;
    this.clazz = clazz;
  }

  public CourseTakerInitMessage(String key, Clazz clazz, String message) {
    this.key = key;
    this.clazz = clazz;
    this.message = message;
  }

  public String getMessage(TextResource textResource) {
    StringBuilder sb = new StringBuilder();
    sb.append(textResource.getText(key));
    sb.append("[").append(clazz.getCourse().getName()).append(":").append(clazz.getCrn()).append("]")
        .append(clazz.getClazzName()).append(":");
    if (Strings.isNotEmpty(getMessage())) sb.append(getMessage());
    return sb.toString();
  }

}
