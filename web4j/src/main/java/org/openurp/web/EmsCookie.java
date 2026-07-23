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
package org.openurp.web;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.web.util.CookieUtils;

import java.util.HashMap;
import java.util.Map;

public class EmsCookie {
  Map<String, String> data;
  private long profile;
  public EmsCookie(Map<String, String> data) {
    this.data = data;
  }

  public final static String ProfileIdCookieName = "beangle.security.profileId";
  public final static String EmsCookieName = "beangle.ems.context";
  public final static int COOKIE_AGE = 60 * 60 * 24 * 7; // 7 days

  public static final EmsCookie parse(String cookieValue) {
    Gson gson = new Gson();
    Map<String, String> v = gson.fromJson(cookieValue, Map.class);
    return new EmsCookie(v);
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this.data);
  }

  public static final EmsCookie get(HttpServletRequest request, HttpServletResponse response) {
    String cv = CookieUtils.getCookieValue(request, EmsCookie.EmsCookieName);
    EmsCookie cookie = null;
    if (org.beangle.commons.lang.Strings.isEmpty(cv)) cookie = new EmsCookie(new HashMap<String, String>());
    else cookie = EmsCookie.parse(cv);

    String profileId = CookieUtils.getCookieValue(request, EmsCookie.ProfileIdCookieName);
    if(Strings.isNotBlank(profileId)) {
      cookie.setProfile(Long.valueOf(profileId));
    }
    return cookie;
  }

  public static final void set(HttpServletRequest request, HttpServletResponse response, EmsCookie ec) {
    CookieUtils.addCookie(request, response, EmsCookieName, ec.toJson(), "/", COOKIE_AGE);
  }

  public long getProfile() {
    return profile;
  }

  public void setProfile(long profile) {
    this.profile = profile;
  }

  public void put(String key, String value) {
    data.put(key, value);
  }

  public String get(String key) {
    return data.get(key);
  }

  public void remove(String key) {
    data.remove(key);
  }

  public boolean contains(String key) {
    return data.containsKey(key);
  }

}
