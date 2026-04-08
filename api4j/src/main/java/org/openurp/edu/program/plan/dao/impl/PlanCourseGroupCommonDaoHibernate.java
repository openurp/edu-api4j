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
package org.openurp.edu.program.plan.dao.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.lang.Numbers;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.Throwables;
import org.beangle.commons.lang.reflect.Reflections;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.openurp.base.edu.model.Course;
import org.openurp.base.time.Terms;
import org.openurp.edu.program.model.*;
import org.openurp.edu.program.plan.dao.PlanCommonDao;
import org.openurp.edu.program.plan.dao.PlanCourseGroupCommonDao;
import org.openurp.edu.program.plan.util.PlanTermCreditTool;
import org.openurp.edu.program.plan.util.ProgramHibernateClassGetter;
import org.openurp.edu.program.utils.PlanUtils;

import java.util.*;

public class PlanCourseGroupCommonDaoHibernate extends HibernateEntityDao
    implements PlanCourseGroupCommonDao {

  private PlanCommonDao planCommonDao;

  public void saveOrUpdateCourseGroup(CourseGroup group) {
    statGroupCredits(group);
    updateGroupTreeCredits(getTopGroup(group));
    group.getPlan().setCredits(planCommonDao.statPlanCredits(group.getPlan()));
    saveOrUpdate(group.getPlan());
  }

  public void updateCourseGroupParent(CourseGroup group, CourseGroup newParent, CoursePlan plan) {
  }

  public void addCourseGroupToPlan(CourseGroup group, CoursePlan plan) {
    group.setParent(null);
    addGroup(plan, group);
    saveOrUpdate(group);
    saveOrUpdate(plan);
    reSortCourseGroups(plan);

    updateGroupTreeCredits(getTopGroup(group));
    plan.setCredits(planCommonDao.statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  public void addCourseGroupToPlan(CourseGroup group, CourseGroup parent, CoursePlan plan) {
    group.setParent(parent);
    if (parent != null) {
      parent.addChildGroup(group);
      saveOrUpdate(parent);
    }
    addGroup(plan, group);
    saveOrUpdate(group);
    saveOrUpdate(plan);
    reSortCourseGroups(plan);

    updateGroupTreeCredits(getTopGroup(group));
    plan.setCredits(planCommonDao.statPlanCredits(plan));
    saveOrUpdate(plan);
  }

  public void removeCourseGroup(CourseGroup group) {
    // 如果有儿子，先删除儿子
    CoursePlan plan = group.getPlan();
    CourseGroup parent = group.getParent();
    CourseGroup topGroup = getTopGroup(group);

    if (group.getChildren() != null && group.getChildren().size() > 0) {
      // 需要建立一个临时的children list，否则直接遍历group.getChildren()会出错
      List<CourseGroup> t_children = new ArrayList<CourseGroup>(group.getChildren());
      for (CourseGroup child : t_children) {
        removeCourseGroup(child);
      }
    }
    // 把父亲的关系断掉
    if (parent != null) {
      parent.getChildren().remove(group);
      group.setParent(null);
    }
    // 把plan的关系断掉
    plan.getGroups().remove(group);
    reSortCourseGroups(plan);
    //remove(group);
    if (parent == null) {
      statGroupCredits(group);
      plan.setCredits(planCommonDao.statPlanCredits(plan));
    } else {
      updateGroupTreeCredits(topGroup);
      plan.setCredits(planCommonDao.statPlanCredits(plan));
    }
    saveOrUpdate(plan);
  }

  public CourseGroup getTopGroup(CourseGroup group) {
    if (group.getParent() != null) {
      return getTopGroup(group.getParent());
    }
    return group;
  }

  private void statGroupCredits(CourseGroup group) {
    if (group.isAutoAddup() && (CollectUtils.isNotEmpty(group.getChildren())
        || CollectUtils.isNotEmpty(group.getPlanCourses()))) {
      float credits = 0f;
      int creditHours = 0;
      // 学分分布，周课时分布的格式都是这样的：,1,2,3,4,5,6,7,8,。注意前后两个逗号
      String termCredits = Strings.repeat(",0", group.getPlan().getProgram().getEndTerm()) + ",";

      for (CourseGroup child : group.getChildren()) {
        credits += child.getCredits();
        creditHours += child.getCreditHours();
        termCredits = PlanTermCreditTool.mergeTermCredits(termCredits, child.getTermCredits());
      }
      for (PlanCourse pcourse : group.getPlanCourses()) {
        creditHours += pcourse.getCourse().getCreditHours();
        credits += pcourse.getCourse().getCredits(group.getPlan().getProgram().getLevel());
        termCredits = addCreditsInTerms(termCredits, pcourse);
      }
      String[] carray = Strings.split(termCredits);
      String t = ",";
      for (int i = 0; i < carray.length; i++) {
        if (Numbers.toFloat(carray[i]) > 0) {
          t += ((i + 1) + ",");
        }
      }
      Terms terms = new Terms(t);
      group.setTermCredits(termCredits);
      group.setTerms(terms);
      group.setCredits(credits);
      group.setCreditHours(creditHours);
      saveOrUpdate(group);
    }
  }

  /**
   * 级联更新指定组的父组学分
   *
   * @param group
   */
  public void updateParentGroupCredits(CourseGroup group) {
    CourseGroup parent = group.getParent();
    if (null != parent) {
      statGroupCredits(parent);
      updateParentGroupCredits(parent);
    }
  }

  public void updateGroupTreeCredits(CourseGroup group) {
    for (CourseGroup child : group.getChildren()) {
      updateGroupTreeCredits(child);
    }
    statGroupCredits(group);
  }

  private String addCreditsInTerms(String termCredits, PlanCourse planCourse) {
    String[] credits = termCredits.replaceAll("^,", "").replaceAll(",$", "").split(",");
    if (PlanUtils.isUnplannedTerm(planCourse.getTerms())) return termCredits;
    String[] terms = planCourse.getTerms().toString().replaceAll("^,", "").replaceAll(",$", "").split(",");
    var level = planCourse.getGroup().getPlan().getProgram().getLevel();
    for (int i = 0; i < terms.length; i++) {
      try {
        int term = Integer.valueOf(terms[i]);
        // 防止计划课程的开课学期大于课程组的学期范围
        if (term >= credits.length) {
          float credit = Float.valueOf(credits[credits.length - 1]) + planCourse.getCourse().getCredits(level);
          credits[credits.length - 1] = String.valueOf(credit).replaceAll(".0$", "");
        } else {
          float credit = Float.valueOf(credits[term - 1]) + planCourse.getCourse().getCredits(level);
          credits[term - 1] = String.valueOf(credit).replaceAll(".0$", "");
        }
      } catch (NumberFormatException e) {
        String term = terms[i];
        credits[0] = term.replaceAll(".0$", "");
      }
    }
    return "," + Strings.join(credits, ",") + ",";
  }

  public void updateCourseGroupMoveDown(CourseGroup courseGroup) {
    CoursePlan plan = courseGroup.getPlan();
    CourseGroup parent = courseGroup.getParent();
    // 如果是顶级课程组则下移courseGroup.plan.groups
    if (null == parent) {
      int meInTopIndex = plan.getTopCourseGroups().indexOf(courseGroup);
      if (meInTopIndex == plan.getTopCourseGroups().size() - 1) {
        throw new RuntimeException(
            "CourseGroup cannot be moved down, because it's already the last one");
      }
      // courseGroup后面一个课程组
      ExecutiveCourseGroup meHindGroup = (ExecutiveCourseGroup) plan.getTopCourseGroups().get(meInTopIndex + 1);
      int meInPreOrderIndex = plan.getGroups().indexOf(courseGroup);
      int meHindGroupInPreOrderIndex = plan.getGroups().indexOf(meHindGroup);
      swap(plan.getGroups(), meInPreOrderIndex, meHindGroupInPreOrderIndex);
    } else {
      // 如果不是，则下移courseGroup.parent.children
      int meIndex = parent.getChildren().indexOf(courseGroup);
      if (meIndex == parent.getChildren().size() - 1) {
        throw new RuntimeException(
            "CourseGroup cannot be moved down, because it's already the last one in it's parent CourseGroup");
      }
      swap(parent.getChildren(), meIndex, meIndex + 1);
      saveOrUpdate(parent);
    }
    reSortCourseGroups(plan);
    saveOrUpdate(plan);
  }

  protected static List<CourseGroup> getPreOrderTraversalChildren(CourseGroup group) {
    List<CourseGroup> result = CollectUtils.newArrayList();
    result.add(group);
    for (CourseGroup child : group.getChildren()) {
      result.addAll(getPreOrderTraversalChildren(child));
    }
    return result;
  }

  /**
   * 重新对课程组进行排序，课程组是树形结构的（一个课程组可以有子课程组）<br>
   * 但是在TeachPlan里面的courseGroups是将所有课程组里都存放进来（不考虑树形结构）<br>
   * 这些课程组是以前序遍历的结果为顺序排列的
   */
  protected static void reSortCourseGroups(CoursePlan plan) {
    List tGroups = new ArrayList();
    for (CourseGroup courseGroup : plan.getTopCourseGroups()) {
      tGroups.addAll(getPreOrderTraversalChildren(courseGroup));
    }
    plan.getGroups().clear();
    plan.getGroups().addAll(tGroups);
  }

  protected static void addGroup(CoursePlan plan, CourseGroup group) {
    if (null == plan.getGroups()) {
      plan.setGroups(new ArrayList<CourseGroup>());
    }
    plan.getGroups().add(group);
    group.updateCoursePlan(plan);
  }

  public void updateCourseGroupMoveUp(CourseGroup courseGroup) {
    CoursePlan plan = courseGroup.getPlan();
    CourseGroup parent = courseGroup.getParent();
    // 如果是顶级课程组则上移courseGroup.plan.groups
    if (null == parent) {
      int meInTopIndex = plan.getTopCourseGroups().indexOf(courseGroup);
      if (meInTopIndex == 0) {
        throw new RuntimeException(
            "CourseGroup cannot be moved up, because it's already the first one");
      }
      // courseGroup前面一个课程组
      ExecutiveCourseGroup meFrontGroup = (ExecutiveCourseGroup) plan.getTopCourseGroups().get(meInTopIndex - 1);
      int meInPreOrderIndex = plan.getGroups().indexOf(courseGroup);
      int meFrontGroupInPreOrderIndex = plan.getGroups().indexOf(meFrontGroup);
      swap(plan.getGroups(), meInPreOrderIndex, meFrontGroupInPreOrderIndex);
    } else {
      // 如果不是，则上移courseGroup.parent.children
      int meIndex = parent.getChildren().indexOf(courseGroup);
      if (meIndex == 0) {
        throw new RuntimeException(
            "CourseGroup cannot be moved up, because it's already the first one in it's parent CourseGroup");
      }
      swap(parent.getChildren(), meIndex, meIndex - 1);
      saveOrUpdate(parent);
    }
    reSortCourseGroups(plan);
    saveOrUpdate(plan);

  }

  @SuppressWarnings("unchecked")
  private void swap(List anyList, int index1, int index2) {
    Object o1 = anyList.get(index1);
    Object o2 = anyList.get(index2);
    anyList.set(index2, o1);
    anyList.set(index1, o2);
  }

  public ExecutiveCourseGroup getCourseGroupByCourseType(CourseGroup planGroup, Long planId,
                                                         Integer courseTypeId) {
    OqlBuilder oql = OqlBuilder.from(ProgramHibernateClassGetter.hibernateClass(planGroup), "cgroup");
    oql.where("cgroup.courseType.id = :typeId", courseTypeId);
    oql.where("cgroup.plan.id = :planId", planId);
    List<ExecutiveCourseGroup> l = search(oql);
    if (l != null && l.size() > 0) {
      return l.get(0);
    }
    return null;
  }

  public List<Course> extractCourseInCourseGroup(ExecutiveCourseGroup group, String terms) {
    Set<Course> courses = new HashSet<Course>();

    Integer[] findTerm = Strings.splitNumSeq(terms);
    if (null == findTerm || findTerm.length == 0) {
      return Collections.EMPTY_LIST;
    }
    for (int i = 0; i < findTerm.length; i++) {
      for (PlanCourse planCourse : PlanUtils.getPlanCourses(group, findTerm[i])) {
        courses.add(planCourse.getCourse());
      }
    }
    return new ArrayList<Course>(courses);
  }

  public List<ExecutivePlanCourse> extractPlanCourseInCourseGroup(ExecutiveCourseGroup group, Set<String> terms) {
    Set<ExecutivePlanCourse> result = CollectUtils.newHashSet();
    for (Object term : terms) {
      result.addAll((List) PlanUtils.getPlanCourses(group, Integer.valueOf((String) term)));
    }
    return new ArrayList<ExecutivePlanCourse>(result);
  }

  /**
   * 复制一个课程组(包括子课程组)到一个培养计划中
   *
   * @param sourceCourseGroup
   * @param parentAttachTo    必须是一个持久态persistent对象，复制成哪个课程组的子课程组，可以为null，如果为null则为顶级课程组，
   * @param planAttachTo      必须是一个持久态persistent对象
   * @return
   */
  public CourseGroup copyCourseGroup(CourseGroup sourceCourseGroup, CourseGroup parentAttachTo,
                                     CoursePlan planAttachTo, Class<?> groupClazz, Class<?> pcClazz) {
    CourseGroup cloneGroup = (CourseGroup) Reflections.newInstance(groupClazz);

    commonSetting(cloneGroup, sourceCourseGroup);
    normalizeTerms(cloneGroup, planAttachTo.getProgram().getEndTerm());

    if (parentAttachTo == null) {
      addCourseGroupToPlan(cloneGroup, planAttachTo);
    } else {
      if (!parentAttachTo.getPlan().getId().equals(planAttachTo.getId())) {
        throw new RuntimeException(
            "parentAttachTo.coursePlan must be same with planAttachTo");
      }
      addCourseGroupToPlan(cloneGroup, parentAttachTo, planAttachTo);
    }

    // 保存好空的课程组后才可以复制计划课程到组里
    saveOrUpdate(cloneGroup);
    copyPlanCourses(sourceCourseGroup.getPlanCourses(), cloneGroup, pcClazz);

    for (CourseGroup child : sourceCourseGroup.getChildren()) {
      copyCourseGroup(child, cloneGroup, planAttachTo, groupClazz, pcClazz);
    }
    saveOrUpdate(cloneGroup);
    return cloneGroup;
  }

  private void normalizeTerms(CourseGroup group, Integer newTermsCount) {
    Float[] creditsTerms = PlanTermCreditTool.transformToFloat(group.getTermCredits());
    int oldTermsCount = creditsTerms.length;
    String newCreditPerTerms = PlanTermCreditTool.buildCourseGroupTermCredits(creditsTerms, oldTermsCount,
        newTermsCount);
    group.setTermCredits(newCreditPerTerms);
  }

  private void commonSetting(CourseGroup newGroup, CourseGroup src) {
    newGroup.setSubCount(src.getSubCount());
    newGroup.setCreditHours(src.getCreditHours());
    newGroup.setCourseType(src.getCourseType());
    newGroup.setCredits(src.getCredits());
    newGroup.setHourRatios(src.getHourRatios());
    newGroup.setTermCredits(src.getTermCredits());
    newGroup.setRemark(src.getRemark());
    newGroup.setIndexno(src.getIndexno());
    if (newGroup instanceof AbstractCourseGroup) {
      AbstractCourseGroup acg = (AbstractCourseGroup) newGroup;
      acg.setRank(((AbstractCourseGroup) src).getRank());
      acg.setGivenName(((AbstractCourseGroup) src).getGivenName());
    }
  }

  //----------------copy plan course----------------------

  public List<? extends PlanCourse> copyPlanCourses(List<? extends PlanCourse> sourcePlanCourses,
                                                    CourseGroup courseGroupAttachTo, Class<?> pcClazz) {
    List<PlanCourse> res = new ArrayList<PlanCourse>();
    for (PlanCourse planCourse : sourcePlanCourses) {
      res.add(copyPlanCourse(planCourse, courseGroupAttachTo, pcClazz));
    }
    return res;
  }

  public PlanCourse copyPlanCourse(PlanCourse sourcePlanCourse, CourseGroup courseGroupAttachTo, Class<?> pcClazz) {
    PlanCourse clonePlanCourse = (PlanCourse) Reflections.newInstance(pcClazz);
    commonSetting(clonePlanCourse, sourcePlanCourse);
    courseGroupAttachTo.addPlanCourse(clonePlanCourse);
    normalizeTerm(clonePlanCourse, sourcePlanCourse.getGroup().getPlan().getProgram().getTermsCount(),
        courseGroupAttachTo.getPlan().getProgram().getTermsCount());
    saveOrUpdate(courseGroupAttachTo);
    return clonePlanCourse;
  }

  private void normalizeTerm(PlanCourse planCourse, int oldTermsCount, int newTermsCount) {
    planCourse.setTerms(
        PlanTermCreditTool.buildPlanCourseTerms(planCourse.getTerms(), oldTermsCount, newTermsCount));
  }

  private void commonSetting(PlanCourse copy, PlanCourse sourcePlanCourse) {
    try {
      PropertyUtils.copyProperties(copy, sourcePlanCourse);
      copy.setGroup(null);
      copy.setId(null);
    } catch (Exception e) {
      throw new RuntimeException("error in clone ExecutivePlanCourse:" + Throwables.getStackTrace(e));
    }
  }

  public void setPlanCommonDao(PlanCommonDao planCommonDao) {
    this.planCommonDao = planCommonDao;
  }

}
