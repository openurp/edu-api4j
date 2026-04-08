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

import java.util.Comparator;

import org.beangle.commons.lang.Objects;
import org.beangle.commons.lang.functor.Transformer;
import org.openurp.edu.program.model.PlanCourse;

/**
 * ExecutivePlanCourse的包装类，用于提供不同的equals逻辑
 * 从eams-3shufe移植
 */
public class PlanCourseWrapper implements Comparable {

  public static final Transformer WRAPPER = new WrapperTransformer();
  public static final Transformer UNWRAPPER = new UnWrapperTransformer();
  public static final Comparator<PlanCourse> COMPARATOR = new PlanCourseComparator();

  private PlanCourse planCourse;

  public PlanCourseWrapper(PlanCourse object) {
    this.planCourse = object;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((planCourse.getCourse() == null) ? 0 : planCourse.getCourse().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    PlanCourseWrapper other = (PlanCourseWrapper) obj;
    if (planCourse.getCourse() == null) {
      if (other.planCourse.getCourse() != null) return false;
    } else if (!planCourse.getCourse().equals(other.planCourse.getCourse())) return false;

    if (planCourse.getRemark() == null) {
      if (other.planCourse.getRemark() != null) return false;
    } else if (!planCourse.getRemark().equals(other.planCourse.getRemark())) return false;
    if (planCourse.getTerms() == null) {
      if (other.planCourse.getTerms() != null) return false;
    } else if (!(planCourse.getTerms().value == other.planCourse.getTerms().value)) return false;

    return true;
  }

  public int compareTo(Object object) {
    PlanCourseWrapper myClass = (PlanCourseWrapper) object;
    return Objects.compareBuilder()
        .add(planCourse.getCourse().getCode(), myClass.getPlanCourse().getCourse().getCode())
        .add(planCourse.getCourse().getName(), myClass.getPlanCourse().getCourse().getName())
        .add(planCourse.getCourse().getDefaultCredits(), myClass.getPlanCourse().getCourse().getDefaultCredits())
        .toComparison();
  }

  public PlanCourse getPlanCourse() {
    return planCourse;
  }

  public void setPlanCourse(PlanCourse executePlanCourse) {
    this.planCourse = executePlanCourse;
  }

  private static class WrapperTransformer implements Transformer {
    public Object apply(Object object) {
      if (PlanCourse.class
          .isAssignableFrom(object.getClass())) { return new PlanCourseWrapper((PlanCourse) object); }
      throw new IllegalArgumentException("cannot accept object other than ExecutivePlanCourse");
    }
  }

  private static class UnWrapperTransformer implements Transformer {
    public Object apply(Object object) {
      if (object.getClass()
          .equals(PlanCourseWrapper.class)) { return ((PlanCourseWrapper) object).getPlanCourse(); }
      throw new IllegalArgumentException("cannot accept object other than stdType of ExecutivePlanCourseWrapper");
    }
  }

  private static class PlanCourseComparator implements Comparator<PlanCourse> {

    public int compare(PlanCourse left, PlanCourse right) {
      return Objects.compareBuilder().add(left.getCourse().getCode(), right.getCourse().getCode())
          .add(left.getCourse().getName(), right.getCourse().getName())
          .add(left.getCourse().getDefaultCredits(), right.getCourse().getDefaultCredits())
          .add(left.getTerms(), right.getTerms()).add(left.isCompulsory(), right.isCompulsory())
          .toComparison();
    }

  }

  @Override
  public String toString() {
    return "ExecutivePlanCourseWrapper [executePlanCourse=" + planCourse + "]";
  }

}
