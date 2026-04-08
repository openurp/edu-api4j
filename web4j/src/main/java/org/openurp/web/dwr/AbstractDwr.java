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

import com.opensymphony.xwork2.ActionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.beangle.security.core.context.SecurityContext;
import org.beangle.security.ids.SecurityContextBuilder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDwr {
  private SecurityContextBuilder securityContextBuilder;

  protected void prepareSecurity(HttpServletRequest request, HttpServletResponse response) {
    SecurityContext.set(securityContextBuilder.build(request, response));
    ActionContext context = new ActionContext(new HashMap<String, Object>());
    HttpSession s = request.getSession();
    if (null != s) {
      Enumeration<String> names = s.getAttributeNames();
      Map<String, Object> session = new HashMap<String, Object>();
      while (names.hasMoreElements()) {
        String name = names.nextElement();
        session.put(name, s.getAttribute(name));
      }
      context.setSession(session);
    }
    context.put(StrutsStatics.HTTP_REQUEST, request);
    HttpParameters params = HttpParameters.create().build();
    context.setParameters(params);
    ActionContext.setContext(context);
  }

  protected void cleanSecurity() {
    SecurityContext.clear();
    ActionContext.setContext(null);
  }

  public void setSecurityContextBuilder(SecurityContextBuilder securityContextBuilder) {
    this.securityContextBuilder = securityContextBuilder;
  }

}
