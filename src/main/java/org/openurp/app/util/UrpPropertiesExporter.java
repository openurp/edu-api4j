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
package org.openurp.app.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.openurp.app.Ems;
import org.openurp.app.EmsApp;

import java.util.Map;

public class UrpPropertiesExporter implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {

    for (Map.Entry<String, String> entry : Ems.Instance.getProperties().entrySet()) {
      if (entry.getKey().contains(".")) System.setProperty(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, String> entry : EmsApp.Instance.getProperties().entrySet()) {
      if (entry.getKey().contains(".")) System.setProperty(entry.getKey(), entry.getValue());
    }

    Ems ems = Ems.getInstance();

    ServletContext context = sce.getServletContext();
    context.setAttribute("templatePath", ems.getApi() + "/platform/config/files/" + EmsApp.getName() + "/{path},webapp://pages,class://");
    context.setAttribute("static_base", ems.getStatic());
    System.setProperty("beangle.webmvc.static_base", ems.getStatic());
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }

}
