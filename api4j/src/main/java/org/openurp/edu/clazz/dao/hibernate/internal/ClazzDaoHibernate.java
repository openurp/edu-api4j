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
package org.openurp.edu.clazz.dao.hibernate.internal;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.collection.page.Page;
import org.beangle.commons.collection.page.PageLimit;
import org.beangle.commons.dao.query.builder.Conditions;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.orm.hibernate.HibernateEntityDao;
import org.hibernate.Cache;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.model.AuditStatus;
import org.openurp.edu.clazz.dao.ClazzCRNGenerator;
import org.openurp.edu.clazz.dao.ClazzDao;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.clazz.model.ClazzRestriction;
import org.openurp.edu.clazz.model.ScheduleSuggest;
import org.openurp.edu.clazz.service.ClazzFilterStrategy;
import org.openurp.edu.exam.model.ExamActivity;
import org.openurp.edu.exam.model.ExamTaker;
import org.openurp.edu.grade.course.model.CourseGradeState;
import org.openurp.edu.program.model.ExecutivePlan;
import org.openurp.edu.room.model.Occupancy;
import org.openurp.edu.room.model.RoomOccupyApp;

import java.io.Serializable;
import java.util.*;

public class ClazzDaoHibernate extends HibernateEntityDao implements ClazzDao {

  private ClazzCRNGenerator clazzCRNGenerator;

  private void evictClazzRegion() {
    Cache cache = sessionFactory.getCache();
    if (null != cache) {
      cache.evictEntityRegion(Clazz.class);
    }
  }

  public Page<Clazz> getClazzesByCategory(Serializable id, ClazzFilterStrategy strategy, Semester semester, int pageNo,
                                          int pageSize) {
    Map<String, Object> params = new HashMap<String, Object>(3);
    if (strategy.getName().equals("teacher")) {
      id = "%" + id + "%";
    }
    params.put("id", id);
    params.put("semesterId", semester.getId());
    String queryStr = strategy.getQueryString(null, " and task.semester.id= :semesterId ");
    List<Clazz> clazzes = search(queryStr, params, new PageLimit(pageNo, pageSize), false);
    return (Page<Clazz>) clazzes;
  }

  @SuppressWarnings("unchecked")
  public List<Clazz> getClazzesByCategory(Serializable id, ClazzFilterStrategy strategy,
                                          Collection<Semester> semesters) {
    Query taskQuery = strategy.createQuery(getSession(), "select distinct task.id from Clazz as task ",
        " and task.semester in (:semesters) ");
    taskQuery.setParameter("id", id);
    taskQuery.setParameterList("semesters", semesters);

    return get(Clazz.class, taskQuery.list());
  }

  public List<Clazz> getClazzesOfStd(Serializable stdId, List<Semester> semesters) {
    OqlBuilder<Clazz> queryBuilder = OqlBuilder.from(Clazz.class, "clazz");
    queryBuilder.join("clazz.enrollment.courseTakers", "courseTaker");
    queryBuilder.where("courseTaker.std.id =:stdId", stdId);
    queryBuilder.where("clazz.semester in (:semesters)", semesters);
    return search(queryBuilder);
  }

  public int updateClazzByCategory(String attr, Object value, Long id, ClazzFilterStrategy strategy,
                                   Semester semester) {
    evictClazzRegion();
    String queryStr = strategy.getQueryString("update TeachTask set " + attr + " = :value ",
        " and semester.id = :semesterId");
    return executeUpdate(queryStr, new Object[]{value, semester.getId()});
  }

  /**
   * 生成批量更新的动态hql
   *
   * @param attr
   * @param value
   * @param clazz
   * @param stdTypeIds
   * @param departIds
   * @param newParamsMap
   * @return
   */
  private String getUpdateQueryString(String attr, Object value, Clazz clazz, Integer[] stdTypeIds, Long[] departIds,
                                      Map<String, Object> newParamsMap) {
    OqlBuilder<Clazz> entityQuery = OqlBuilder.from(Clazz.class, "clazz");
    entityQuery.where(Conditions.extractConditions("clazz", clazz));
    if (null != stdTypeIds && 0 != stdTypeIds.length) {
      entityQuery.where("clazz.teachclass.stdType.id in (:stdTypeIds) ", stdTypeIds);
    }
    if (null != departIds && 0 != departIds.length) {
      entityQuery.where("clazz.teachDepart.id in (:departIds) ", departIds);
    }
    StringBuffer updateSql = new StringBuffer(
        "update " + Clazz.class.getName() + " set " + attr + "=(:" + attr + ") where id in (");
    updateSql.append(entityQuery.build().getStatement()).append(")");
    // debug("[updateTeachTaskByCriteria]:" + updateSql);
    newParamsMap.put(attr, value);
    newParamsMap.putAll(entityQuery.getParams());
    return updateSql.toString();
  }

  public int updateClazzByCriteria(String attr, Object value, Clazz clazz, Integer[] stdTypeIds, Long[] departIds) {
    evictClazzRegion();
    Map<String, Object> newParamsMap = CollectUtils.newHashMap();
    String updateSql = getUpdateQueryString(attr, value, clazz, stdTypeIds, departIds, newParamsMap);
    Query query = getSession().createQuery(updateSql);
    QuerySupport.setParameter(query, newParamsMap);
    return query.executeUpdate();
  }

  public int countClazz(Serializable id, ClazzFilterStrategy strategy, Semester semester) {
    Query countQuery = strategy.createQuery(getSession(),
        "select count(task.id) from " + Clazz.class.getName() + " as task ", " and task.semester.id  = :semesterId");
    if (strategy.getName().equals("teacher"))
      id = "%" + id + "%";
    countQuery.setParameter("id", id);
    countQuery.setParameter("semesterId", semester.getId());

    @SuppressWarnings("unchecked")
    List<Number> rsList = countQuery.list();
    return ((Number) rsList.get(0)).intValue();
  }

  public void saveMergeResult(Clazz[] clazzes, int index) {
    saveOrUpdate(clazzes[index]);
    for (int i = 0; i < clazzes.length; i++) {
      if (i == index) {
        continue;
      }
      remove(clazzes[i]);
    }
  }

  public void remove(Clazz clazz) {
    List<Object> removeEntities = CollectUtils.newArrayList();

    List<Occupancy> occupancies = getOccupancies(clazz);
    removeEntities.addAll(occupancies);

    List<ScheduleSuggest> suggests = get(ScheduleSuggest.class, "clazz", clazz);
    removeEntities.addAll(suggests);

    List<ExamTaker> examTakers = get(ExamTaker.class, "clazz", clazz);
    removeEntities.addAll(examTakers);

    List<ExamActivity> examActivities = get(ExamActivity.class, "clazz", clazz);
    removeEntities.addAll(examActivities);

    List<CourseGradeState> gradeStates = get(CourseGradeState.class, "clazz", clazz);
    removeEntities.addAll(gradeStates);

    removeEntities.add(clazz);
    super.remove(removeEntities);
  }

  public List<Occupancy> getOccupancies(Clazz clazz) {
    OqlBuilder<Occupancy> builder = OqlBuilder.from(Occupancy.class, "occupancy").where(
        "occupancy.activityId = :clazzId and occupancy.app.id in(:apps)", clazz.getId(),
        new Integer[]{RoomOccupyApp.COURSE, RoomOccupyApp.EXAM});
    return search(builder);
  }

  public void saveGenResult(ExecutivePlan plan, Semester semester, List<Clazz> clazzes, boolean removeExists) {
    if (removeExists) {
      OqlBuilder<Clazz> query = OqlBuilder.from(Clazz.class, "clazz");
      query.where("clazz.planId = :planId", plan.getId());
      query.where("clazz.semester=:semester", semester);
      var existsClazzes = search(query);
      for (Clazz clazz : existsClazzes) {
        remove(clazz);
      }
    }
    getSession().setFlushMode(FlushMode.COMMIT);
    // 要在这里一次性生成一批课程序号
    clazzCRNGenerator.genClazzSeqNos(clazzes);
    for (Clazz clazz : clazzes) {
      clazz.setStatus(AuditStatus.UNSUBMITTED);
      clazz.setPlanId(plan.getId());
      super.saveOrUpdate(clazz);
    }
    // getSession().flush();
    // for(Clazz clazz : clazzes) {
    // clazzPlanRelationDao.saveRelation(plan, clazz);
    // }
  }

  public void saveOrUpdate(Clazz clazz) {
    // 清除空的limitGroup
    Iterator<ClazzRestriction> iter = clazz.getEnrollment().getRestrictions().iterator();
    while (iter.hasNext()) {
      if (CollectUtils.isEmpty(iter.next().getItems())) {
        iter.remove();
      }
    }
    clazzCRNGenerator.genClazzSeqNo(clazz);
    super.saveOrUpdate(clazz);
  }

  public void setClazzCRNGenerator(ClazzCRNGenerator clazzSeqNoGenerator) {
    this.clazzCRNGenerator = clazzSeqNoGenerator;
  }

}
