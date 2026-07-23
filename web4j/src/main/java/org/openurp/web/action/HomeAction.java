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
/*
 *
 */
package org.openurp.web.action;

import com.opensymphony.xwork2.ActionContext;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.SessionMap;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.web.url.UrlBuilder;
import org.beangle.commons.web.util.CookieUtils;
import org.beangle.commons.web.util.HttpUtils;
import org.beangle.commons.web.util.RequestUtils;
import org.beangle.ems.app.Ems;
import org.beangle.ems.app.EmsApp;
import org.beangle.security.Securities;
import org.beangle.security.codec.EncryptUtil;
import org.beangle.security.core.AuthenticationException;
import org.beangle.security.ids.Cas;
import org.beangle.security.ids.CasConfig;
import org.beangle.struts2.annotation.Result;
import org.beangle.struts2.annotation.Results;
import org.openurp.base.model.User;
import org.openurp.web.EmsCookie;
import org.openurp.edu.web.action.AdminBaseAction;
import org.openurp.service.security.EamsUserCategory;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * 加载用户主界面
 */
@Results({@Result(name = "logout_success", type = "redirectAction", location = "login")})
public class HomeAction extends AdminBaseAction {

  private CasConfig casConfig;

  public String index() throws Exception {
    String userCode = Securities.getUsername();
    if (null == userCode) throw new AuthenticationException("without login");
    List<User> users = entityDao.search(OqlBuilder.from(User.class, "u").where("u.code=:code", userCode));
    User user = (users.isEmpty()) ? null : users.get(0);

    NavContext nav = new NavContext();
    nav.setPrincipal(Securities.getPrincipal());
    nav.setUsername(Securities.getUsername());
    put("user", user);
    put("nav", nav);

    String debug= get("debug");
    if(null!=debug){
      System.out.println("-----------------");
      System.out.println(Securities.getUsername());
      Map<String, Object> sessions= ActionContext.getContext().getSession();
      for(Map.Entry<String,Object> e:sessions.entrySet()){
        System.out.println(e.getKey()+"->"+e.getValue().toString());
      }
      System.out.println("-----------------");
    }
    String menuUrl = Ems.Instance.getApi() + "/platform/security/func/" + EmsApp.getName() + "/menus/user/"
      + Securities.getUsername() + ".json?forDomain=1";
    String menuJson = HttpUtils.getResponseText(new URL(menuUrl), "UTF-8");
    nav.setMenusJson(menuJson);
    nav.setEms(Ems.getInstance());
    nav.fetchDomain();
    put("thisAppName", EmsApp.getName());
    put("webappBase", Ems.Instance.getWebapp());
    put("urpBase", Ems.Instance.getBase());

    HttpServletRequest request = this.getRequest();
    UrlBuilder ub = new UrlBuilder(request.getContextPath());
    ub.scheme((RequestUtils.isHttps(request)) ? "https" : "http");
    ub.serverName(request.getServerName());
    ub.port(RequestUtils.getServerPort(request));
    put("appBase", ub.buildUrl());

    put("isManager", false);
    if (EamsUserCategory.MANAGER_USER.equals(user.getCategory().getId())) {
      put("isManager", true);
    }
    nav.setAvatarUrl(Ems.Instance.getApi() + "/platform/user/avatars/" + EncryptUtil.encode(userCode));
    put("casconfig", casConfig);

    String url = Ems.Instance.getApi() + "/platform/user/profiles/" + user.getCode() + ".json?domain=edu&resolved=1";
    String profiles = HttpUtils.getResponseText(url);
    nav.setProfiles(profiles);
    nav.setProfileId(EmsCookie.getProfileId(request));
    return forward();
  }

  public String welcome() {
    put("date", new Date(System.currentTimeMillis()));
    return forward();
  }

  public String logout() {
    String result = determineTarget(getRequest());
    Cas.cleanup(casConfig, getRequest(), getResponse());
    (ActionContext.getContext().getSession()).clear();
    CookieUtils.deleteCookieByName(getRequest(), getResponse(), "project_id");
    CookieUtils.deleteCookieByName(getRequest(), getResponse(), "semester.id");
    ((SessionMap<String, Object>) ActionContext.getContext().getSession()).invalidate();
    return result;
  }

  protected String determineTarget(HttpServletRequest request) {
    String target = get("redirect");
    if (null == target) target = "redirect:" + casConfig.getCasServer() + "/logout";
    else target = "redirect:" + target;
    return target;
  }

  public void setCasConfig(CasConfig casConfig) {
    this.casConfig = casConfig;
  }

}
