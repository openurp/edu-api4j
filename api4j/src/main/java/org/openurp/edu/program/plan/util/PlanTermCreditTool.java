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
package org.openurp.edu.program.plan.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.lang.Strings;
import org.openurp.base.time.Terms;
import org.openurp.edu.program.model.CourseGroup;
import org.openurp.edu.program.model.CoursePlan;
import org.openurp.edu.program.model.ExecutiveCourseGroup;
import org.openurp.edu.program.model.PlanCourse;

/**
 * 专门用来处理和培养计划的学期，学期学分的有关的工具类
 */
public class PlanTermCreditTool {

  /**
   * 把学期进行排序，然后返回,1,2,3,或者,1,这样的形式
   *
   * @param terms
   * @return
   */
  public static String normalizeTerms(String terms) {
    String[] arr = terms.replaceAll("\\s", "").replaceAll("^,", "").replaceAll(",$", "").split(",");
    try {
      Arrays.sort(arr, new Comparator<String>() {
        public int compare(String o1, String o2) {
          return Integer.valueOf(o1) - Integer.valueOf(o2);
        }
      });
    } catch (Exception e) {
      return ',' + Strings.join(arr, ',') + ',';
    }
    if (arr.length == 1 && arr[0].equals("*")) { return arr[0]; }
    return ',' + Strings.join(arr, ',') + ',';
  }

  public static String mergeTermCredits(String termCredits1, String termCredits2) {
    if (Strings.isEmpty(termCredits1)) { return termCredits2; }
    if (Strings.isEmpty(termCredits2)) { return termCredits1; }
    String[] credits1 = termCredits1.replaceAll("^,", "").replaceAll(",$", "").split(",");
    String[] credits2 = termCredits2.replaceAll("^,", "").replaceAll(",$", "").split(",");
    String[] credits3 = new String[credits1.length];
    // 这里假设credits1 和 credits2 长度一样
    for (int i = 0; i < credits1.length; i++) {
      try {
        credits3[i] = String.valueOf(Float.valueOf(credits1[i]) + Float.valueOf(credits2[i]))
            .replaceAll(".0$", "");
      } catch (Exception e) {
        credits3[i] = credits2[i];
      }
    }
    return "," + Strings.join(credits3, ",") + ",";
  }

  /**
   * 构造CourseGroup的每学期学分分布<br>
   * 如果新学期数小于旧的学期数，那么就要把截短的学期的学分统统加到最后一个学期上<br>
   * 如果新学期数大于旧的学期数，那么就要在后面补0
   *
   * @param termCredits CourseGroup原来的每学期学分分布
   * @param oldTermsCount 计划原来的学期数
   * @param newTermsCount 新的学期数
   * @return
   */
  public static String buildCourseGroupTermCredits(Float[] termCredits, Integer oldTermsCount,
      Integer newTermsCount) {
    StringBuffer result = new StringBuffer(",");
    if (newTermsCount < oldTermsCount) {
      int count = oldTermsCount - newTermsCount;
      // 原来在后面的学分统统加到最后一个学期上
      Float remain = 0f;
      for (int i = 0; i < count; i++) {
        remain += termCredits[newTermsCount + i];
      }
      for (int i = 0; i < newTermsCount; i++) {
        if (i == newTermsCount - 1) {
          result.append(termCredits[i] + remain + ",");
        } else {
          result.append(termCredits[i] + ",");
        }
      }

    } else {
      for (int i = 0; i < oldTermsCount; i++) {
        result.append(termCredits[i] + ",");
      }
      int count = newTermsCount - oldTermsCount;
      for (int i = 0; i < count; i++) {
        result.append("0,");
      }
    }
    return result.toString().replace(".0", "");
  }

  /**
   * 构造PlanCourse的开课学期<br>
   * 如果新的学期小于旧的学期，那么就把PlanCourse的开课学期变成新的学期<br>
   * 如果不是那么就啥都不变
   *
   * @param terms PlanCourse的开课学期
   * @param oldTermsCount 计划原来的学期数
   * @param newTermsCount 新的学期数
   * @return
   */
  public static Terms buildPlanCourseTerms(Terms terms, Integer oldTermsCount, Integer newTermsCount) {
    if (newTermsCount < oldTermsCount) {
      Integer[] termArr = terms.getTermList().toArray(new Integer[] {});
      for (int i = 0; i < termArr.length; i++) {
        if (termArr[i] > newTermsCount) {
          termArr[i] = newTermsCount;
        }
      }
      Integer prev = -1;
      List<Integer> termArr_ = new ArrayList<Integer>();
      for (int i = 0; i < termArr.length; i++) {
        if (prev != termArr[i]) {
          termArr_.add(termArr[i]);
          prev = termArr[i];
        }
      }
      return new Terms("," + Strings.join(termArr_.toArray(new Integer[] {}), ',') + ",");
    }
    return terms;
  }

  /**
   * 用于处理培养计划的学期数发生变化的情况，给每个CourseGroup和每个PlanCourse设置正确的学期学分，和开课学期
   *
   * @param plan
   * @param oldTermsCount
   * @param newTermsCount
   * @param entityDao
   */
  public static void updateTermsCount(CoursePlan plan, Integer oldTermsCount, Integer newTermsCount,
      EntityDao entityDao) {
    for (Iterator<CourseGroup> it = plan.getGroups().iterator(); it.hasNext();) {
      ExecutiveCourseGroup group = (ExecutiveCourseGroup) it.next();

      String newCreditPerTerms = PlanTermCreditTool.buildCourseGroupTermCredits(
          transformToFloat(group.getTermCredits()), oldTermsCount, newTermsCount);
      group.setTermCredits(newCreditPerTerms);

      for (PlanCourse planCourse : group.getPlanCourses()) {
        Terms terms = PlanTermCreditTool.buildPlanCourseTerms(planCourse.getTerms(), oldTermsCount,
            newTermsCount);
        planCourse.setTerms(terms);
        entityDao.saveOrUpdate(planCourse);
      }
      entityDao.saveOrUpdate(group);
    }
  }

  public static Float[] transformToFloat(String strIds) {
    String[] ids = Strings.split(strIds, ",");
    Float[] idsOfFloat = new Float[ids.length];
    for (int i = 0; i < ids.length; i++) {
      idsOfFloat[i] = new Float(ids[i]);
    }
    return idsOfFloat;
  }
}
