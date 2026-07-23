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

import com.opensymphony.xwork2.ActionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.struts2.StrutsStatics;
import org.beangle.commons.bean.comparators.PropertyComparator;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.Entity;
import org.beangle.commons.lang.Strings;
import org.beangle.ems.app.security.SecurityUtils;
import org.beangle.security.Securities;
import org.beangle.security.authz.AccessDeniedException;
import org.beangle.security.core.context.SecurityContext;
import org.beangle.security.core.userdetail.Profile;
import org.beangle.security.data.Permission;
import org.beangle.security.data.ProfileService;
import org.beangle.struts2.helper.Params;
import org.openurp.base.edu.model.Project;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.service.ProfileKeys;
import org.openurp.base.service.SemesterService;
import org.openurp.base.model.Department;
import org.openurp.base.model.User;
import org.openurp.base.service.ProjectContext;
import org.openurp.code.std.model.StdType;
import org.openurp.code.edu.model.EducationLevel;
import org.openurp.service.EamsException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProjectContextImpl implements ProjectContext {

  private EntityDao entityDao;

  protected ProfileService profileService;

  private SemesterService semesterService;

  protected final String prefix = "---+++";

  protected final Predicate collegePredicate = new Predicate() {
    public boolean evaluate(Object obj) {
      Department depart = (Department) obj;
      return depart.isTeaching();
    }
  };

  protected final Predicate teachPredicate = new Predicate() {
    public boolean evaluate(Object obj) {
      Department depart = (Department) obj;
      return depart.isTeaching();
    }
  };

  protected final Transformer idTransformer = new Transformer() {
    public Object transform(Object input) {
      return ((Entity) input).getId();
    }
  };

  public Project getProject() {
    HttpServletRequest request = getRequest();
    Project p = (Project) request.getAttribute("project");
    if (null != p) {
      return p;
    }
    Profile currentProfile = getCurrentProfile();
    String projectId = currentProfile.getProperty(ProfileKeys.Project);
    if (projectId == null || projectId.contains(",")) {
      throw new RuntimeException("Invalid project value:" + projectId);
    }
    Project r = entityDao.get(Project.class, Integer.valueOf(projectId));
    request.setAttribute("project", r);
    return r;
  }

  private HttpServletRequest getRequest() {
    ActionContext ac = ActionContext.getContext();
    return (HttpServletRequest) ac.get(StrutsStatics.HTTP_REQUEST);
  }

  private HttpServletResponse getResponse() {
    ActionContext ac = ActionContext.getContext();
    return (HttpServletResponse) ac.get(StrutsStatics.HTTP_RESPONSE);
  }

  public boolean isRoot() {
    return Securities.isRoot();
  }

  public User getUser() {
    if (null != retreive(prefix + "getUser")) {
      return (User) retreive(prefix + "getUser");
    }
    List<User> users = entityDao.get(User.class, "code", Securities.getUsername());
    if (users.isEmpty()) {
      System.out.println("Cannot find user:" + Securities.getUsername());
    }
    cache(prefix + "getUser", users.get(0));
    return users.get(0);
  }

  public Semester getSemester() {
    if (null != retreive(prefix + "getSemester")) {
      return (Semester) retreive(prefix + "getSemester");
    }
    Semester r = getSemester_internal();
    cache(prefix + "getSemester", r);
    return r;
  }

  protected Semester getSemester_internal() {
    Project project = getProject();

    HttpServletRequest request = getRequest();
    EmsCookie cookie = EmsCookie.get(request,getResponse());
    String semesterId = Strings.isEmpty(Params.get("semester.id")) ? cookie.get("semester")
        : Params.get("semester.id");
    Semester semester = (semesterId == null) ? null : entityDao.get(Semester.class, Integer.valueOf(semesterId));

    if (semester == null) {
      semester = semesterService.getCurSemester(project);
    } else {
      // 判断session/request的学期是否和当前项目匹配
      if (!project.getCalendar().equals(semester.getCalendar())) {
        semester = semesterService.getCurSemester(project);
      }
    }
    if (semester != null && (!cookie.contains("semester") || !semester.getId().toString().equals(cookie.get("semester")))) {
      rememberSemester(semester.getId());
    }
    return semester;
  }

  public void rememberSemester(Integer semesterId) {
    HttpServletRequest request = getRequest();
    EmsCookie cookie = EmsCookie.get(request,getResponse());
    cookie.put("semester", semesterId.toString());
    EmsCookie.set(request, getResponse(), cookie);
  }

  public void forgetSemester() {
    HttpServletRequest request = getRequest();
    EmsCookie cookie = EmsCookie.get(request,getResponse());
    if (cookie.contains("semester")) {
      cookie.remove("semester");
      EmsCookie.set(request, getResponse(), cookie);
    }
  }

  public List<EducationLevel> getEducationLevels() {
    String v = get(ProfileKeys.EduLevel);
    if (v == null || v.equals("*")) {
      return getProjectThings(getProject(), ProfileKeys.EduLevel);
    } else {
      List resultList = entityDao.get(EducationLevel.class, Strings.splitToInt(v));
      Collections.sort(resultList, new PropertyComparator("code"));
      return resultList;
    }
  }

  public List<StdType> getStdTypes() {
    String v = get(ProfileKeys.StdType);
    if (v == null || v.equals("*")) {
      return getProjectThings(getProject(), ProfileKeys.StdType);
    } else {
      List resultList = entityDao.get(StdType.class, Strings.splitToInt(v));
      Collections.sort(resultList, new PropertyComparator("code"));
      return resultList;
    }
  }

  public List<Department> getDeparts() {
    String v = get(ProfileKeys.Department);
    if (v == null || v.equals("*")) {
      return getProjectThings(getProject(), ProfileKeys.Department);
    } else {
      List<Department> resultList = entityDao.get(Department.class, Strings.splitToInt(v));
      Collections.sort(resultList, new PropertyComparator("code"));
      List<Department> departs = CollectUtils.newArrayList();
      java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
      for (Department depart : resultList) {
        if (depart.getEndOn() == null || depart.getEndOn().after(now)) {
          departs.add(depart);
        }
      }
      return departs;
    }
  }

  public List<Department> getColleges() {
    List<Department> departs = getDeparts();
    return (List<Department>) CollectionUtils.select(departs, collegePredicate);
  }

  public List<Department> getTeachDeparts() {
    List<Department> departs = getDeparts();
    CollectionUtils.filter(departs, teachPredicate);
    return (List<Department>) departs;
  }

  public String getEducationIdSeq() {
    List ids = (List) CollectionUtils.collect(getEducationLevels(), idTransformer);
    return Strings.join(ids.toArray(new Integer[0]), ',');
  }

  public String getStdTypeIdSeq() {
    List ids = (List) CollectionUtils.collect(getStdTypes(), idTransformer);
    return Strings.join(ids.toArray(new Integer[0]), ',');
  }

  public String getDepartIdSeq() {
    List ids = (List) CollectionUtils.collect(getDeparts(), idTransformer);
    return Strings.join(ids.toArray(new Integer[0]), ',');
  }

  public String getCollegeIdSeq() {
    List ids = (List) CollectionUtils.collect(getColleges(), idTransformer);
    return Strings.join(ids.toArray(new Integer[0]), ',');
  }

  public String getTeachDepartIdSeq() {
    List ids = (List) CollectionUtils.collect(getTeachDeparts(), idTransformer);
    return Strings.join(ids.toArray(new Integer[0]), ',');
  }

  public List<Project> getProjects() {
    List resultList = getAllProjects();
    if (resultList.isEmpty()) {
      List<Project> ps = entityDao.getAll(Project.class);
      if (ps.size() == 1) {
        return ps;
      }
    }
    return resultList;

  }

  /**
   * 获得和项目有关的数据级权限的内部方法
   *
   * @param fieldName
   * @return
   */
  private String get(String fieldName) {
    String cache = (String) retreive(prefix + fieldName);
    if (cache != null) {
      return cache;
    }
    Profile currentProfile = getCurrentProfile();
    String value = currentProfile.getProperty(fieldName);
    cache(prefix + fieldName, value);
    return value;
  }

  private List getProjectThings(Project contextProject, String fieldName) {
    List res = null;
    if (ProfileKeys.EduLevel.equals(fieldName)) {
      res = CollectUtils.newArrayList(contextProject.getLevels());
    } else if (ProfileKeys.StdType.equals(fieldName)) {
      res = CollectUtils.newArrayList(contextProject.getStdTypes());
    } else if (ProfileKeys.Department.equals(fieldName)) {
      res = CollectUtils.newArrayList(contextProject.getDepartments());
    } else if (ProfileKeys.StdLabel.equals(fieldName)) {
      res = CollectUtils.newArrayList(contextProject.getStdLabels());
    }
    if (res == null) {
      throw new EamsException("系统不支持 " + fieldName + " 维度的数据级权限");
    }
    return res;
  }

  public List<Profile> getProfiles() {
    return profileService.getProfiles(Securities.getUsername(), null);
  }

  /**
   * 获得当前教学项目下所对应的UserProfile
   *
   * @return
   */
  public Profile getCurrentProfile() {
    Profile cache = (Profile) retreive(prefix + "getCurrentProfile");
    if (cache != null) {
      return cache;
    }
    Profile  profile = SecurityContext.get().getProfile();
    if (null==profile) throw new EamsException("用户缺少数据级权限配置");
    return profile;
  }

  private List getAllProjects() {
    List cache = (List) retreive(prefix + ProfileKeys.Project);
    if (cache != null) {
      return cache;
    }
    List<Profile> profiles = profileService.getProfiles(Securities.getUsername(), null);
    if (CollectionUtils.isEmpty(profiles)) {
      throw new EamsException("用户缺少数据级权限配置");
    }

    Set<Project> res = CollectUtils.newHashSet();
    for (Profile profile : profiles) {
      if (null != profile.getProperty(ProfileKeys.Project)) {
        String value = profile.getProperty(ProfileKeys.Project);
        if (value.equals("*")) {
          res.addAll(entityDao.getAll(Project.class));
          break;
        } else {
          res.addAll(entityDao.get(Project.class, Strings.splitToInt(value)));
        }
      }
    }
    List list = CollectUtils.newArrayList(res);
    cache(prefix + ProfileKeys.Project, list);
    return list;
  }

  public void applyRestriction(OqlBuilder<?> query) {
    String userCode = Securities.getUsername();
    if (Securities.isRoot()) return;
    Permission dp = profileService.getPermission(userCode, query.getEntityClass().getName(),
        Securities.getResource());
    if (null == dp) return;
    List<Profile> profiles = profileService.getProfiles(userCode, null);

    if (profiles.isEmpty())
      throw new AccessDeniedException(Securities.getResource(), "error.security.errorprofile");
    SecurityUtils.apply(query, dp, profiles);
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  public void setProfileService(ProfileService profileService) {
    this.profileService = profileService;
  }

  public void setSemesterService(SemesterService semesterService) {
    this.semesterService = semesterService;
  }

  /**
   * 在本次调用范围内，缓存结果集
   */
  static void cache(String key, Object value) {
    if (ActionContext.getContext() != null) {
      ActionContext.getContext().getContextMap().put(key, value);
    }
  }

  /**
   * 从本次调用的缓存里，取回结果
   *
   * @param key
   * @return
   */
  static Object retreive(String key) {
    if (ActionContext.getContext() != null) { return ActionContext.getContext().getContextMap().get(key); }
    return null;
  }
}
