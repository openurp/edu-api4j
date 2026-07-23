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
package org.openurp.web.action;

import com.google.gson.Gson;
import org.beangle.commons.web.util.HttpUtils;
import org.beangle.ems.app.Ems;

import java.util.Map;

public class NavContext {
  private String avatarUrl;
  private String menusJson;
  private String username;
  private Org org;
  private Object principal;
  private Map<String, String> params;
  private String profiles;
  private Object ems;
  private String profileId;

  private Domain domain;

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getMenusJson() {
    return menusJson;
  }

  public void setMenusJson(String menusJson) {
    this.menusJson = menusJson;
  }

  public Org getOrg() {
    return org;
  }

  public void setOrg(Org org) {
    this.org = org;
  }

  public Object getPrincipal() {
    return principal;
  }

  public void setPrincipal(Object principal) {
    this.principal = principal;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public String getProfiles() {
    return profiles;
  }

  public void setProfiles(String profiles) {
    this.profiles = profiles;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public Object getEms() {
    return ems;
  }

  public void setEms(Object ems) {
    this.ems = ems;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Domain getDomain() {
    return domain;
  }

  public void setDomain(Domain domain) {
    this.domain = domain;
  }

  public Domain fetchDomain() {
    String json = HttpUtils.getResponseText(Ems.Instance.getApi() + "/platform/config/domains.json");
    Gson gson = new Gson();
    Map<String, Object> data = gson.fromJson(json, Map.class);
    int id = ((Number) data.get("id")).intValue();
    String name = data.get("name").toString();
    String title = data.get("title").toString();
    String logoUrl = data.get("logoUrl").toString();
    Map orgData = (Map) data.get("org");
    int orgId = ((Number) orgData.get("id")).intValue();
    String orgName = orgData.get("name").toString();
    Org org = new Org(orgId, orgName);
    this.domain = new Domain(id, name, title, logoUrl, org);
    this.org = org;
    return this.domain;
  }

  public static final class Org {
    final int id;
    final String name;

    public Org(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  public static final class Domain {
    final int id;
    final String name;
    final String title;
    final String logoUrl;

    final Org org;

    public Domain(int id, String name, String title, String logoUrl, Org org) {
      this.id = id;
      this.name = name;
      this.title = title;
      this.logoUrl = logoUrl;
      this.org = org;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getTitle() {
      return title;
    }

    public String getLogoUrl() {
      return logoUrl;
    }
  }
}
