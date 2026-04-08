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
package org.openurp.edu.grade.course.service.impl;

import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.lang.Strings;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.service.ProjectPropertyService;
import org.openurp.base.std.model.Student;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.model.StdGpa;
import org.openurp.edu.grade.course.model.StdSemesterGpa;
import org.openurp.edu.grade.course.model.StdYearGpa;
import org.openurp.edu.grade.course.service.CourseGradeProvider;
import org.openurp.edu.grade.course.service.GpaStatService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultGpaStatService implements GpaStatService {

  private final Map<String, GradeFilter> filters = CollectUtils.newHashMap();

  private CourseGradeProvider courseGradeProvider;

  private GpaPolicy gpaPolicy;

  private ApplicationContext applicationContext;

  private ProjectPropertyService projectPropertyService;

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public void setProjectPropertyService(ProjectPropertyService projectPropertyService) {
    this.projectPropertyService = projectPropertyService;
  }

  public void afterPropertiesSet() throws Exception {
    if (null == applicationContext) return;
    String[] names = applicationContext.getBeanNamesForType(GradeFilter.class);
    if (null != names && names.length > 0) {
      for (String name : names) {
        filters.put(name, (GradeFilter) applicationContext.getBean(name));
      }
    }
  }

  public StdGpa stat(Student std, List<CourseGrade> grades) {
    String filterNames = projectPropertyService.get(std.getProject(), "edu.grade.gpa_filters", "bestGradeFilter");
    List<GradeFilter> filters = getFilters(filterNames);
    if (filters.isEmpty()) {
      return gpaPolicy.calc(std, grades, true);
    } else {
      var filterGrades = grades;
      for (GradeFilter filter : filters) {
        filterGrades = filter.filter(filterGrades);
      }
      return gpaPolicy.calc(std, filterGrades, true);
    }
  }

  private List<GradeFilter> getFilters(String name) {
    if (null == name || name.isEmpty() || name.equalsIgnoreCase("none")) return Collections.emptyList();
    String[] filterNames = Strings.split(name, new char[]{'|', ','});
    List<GradeFilter> myFilters = CollectUtils.newArrayList();
    for (String filterName : filterNames) {
      GradeFilter filter = filters.get(filterName);
      if (null != filter) myFilters.add(filter);
    }
    return myFilters;
  }

  public void refresh(StdGpa stdGpa) {
    StdGpa newer = stat(stdGpa.getStd());
    merge(stdGpa, newer);
  }

  public void refresh(StdGpa stdGpa, List<CourseGrade> grades) {
    StdGpa newer = stat(stdGpa.getStd(), grades);
    merge(stdGpa, newer);
  }

  public StdGpa stat(Student std, Semester... semesters) {
    return stat(std, courseGradeProvider.getPublished(std, semesters));
  }

  public MultiStdGpa stat(Collection<Student> stds, Semester... semesters) {
    MultiStdGpa multiStdGpa = new MultiStdGpa();
    for (Student std : stds) {
      StdGpa stdGpa = stat(std, semesters);
      if (stdGpa != null) {
        multiStdGpa.getStdGpas().add(stdGpa);
      }
    }
    multiStdGpa.statSemestersFromStdGpa();
    return multiStdGpa;
  }

  private void merge(StdGpa target, StdGpa source) {
    target.setWms(source.getWms());
    target.setGpa(source.getGpa());
    target.setGradeCount(source.getGradeCount());
    target.setCredits(source.getCredits());
    target.setTotalCredits(source.getTotalCredits());

    // 合并学期绩点统计
    Map<Semester, StdSemesterGpa> existedTerms = semesterGpa2Map(target.getSemesterGpas());
    Map<Semester, StdSemesterGpa> sourceTerms = semesterGpa2Map(source.getSemesterGpas());
    for (Map.Entry<Semester, StdSemesterGpa> entry : sourceTerms.entrySet()) {
      StdSemesterGpa targetTerm = existedTerms.get(entry.getKey());
      StdSemesterGpa sourceTerm = entry.getValue();
      if (null == targetTerm) {
        source.getSemesterGpas().remove(sourceTerm);
        target.add(sourceTerm);
      } else {
        targetTerm.setWms(sourceTerm.getWms());
        targetTerm.setGpa(sourceTerm.getGpa());
        targetTerm.setGradeCount(sourceTerm.getGradeCount());
        targetTerm.setCredits(sourceTerm.getCredits());
        targetTerm.setTotalCredits(sourceTerm.getTotalCredits());
      }
    }

    for (Map.Entry<Semester, StdSemesterGpa> entry : existedTerms.entrySet()) {
      if (null == sourceTerms.get(entry.getKey())) {
        StdSemesterGpa targetTerm = entry.getValue();
        targetTerm.setStdGpa(null);
        target.getSemesterGpas().remove(targetTerm);
      }
    }

    // 合并学年绩点统计
    Map<String, StdYearGpa> existedYears = yearGpa2Map(target.getYearGpas());
    Map<String, StdYearGpa> sourceYears = yearGpa2Map(source.getYearGpas());
    for (Map.Entry<String, StdYearGpa> entry : sourceYears.entrySet()) {
      StdYearGpa targetTerm = existedYears.get(entry.getKey());
      StdYearGpa sourceTerm = entry.getValue();
      if (null == targetTerm) {
        source.getYearGpas().remove(sourceTerm);
        target.add(sourceTerm);
      } else {
        targetTerm.setWms(sourceTerm.getWms());
        targetTerm.setGpa(sourceTerm.getGpa());
        targetTerm.setGradeCount(sourceTerm.getGradeCount());
        targetTerm.setCredits(sourceTerm.getCredits());
        targetTerm.setTotalCredits(sourceTerm.getTotalCredits());
      }
    }
    for (Map.Entry<String, StdYearGpa> entry : existedYears.entrySet()) {
      if (null == sourceYears.get(entry.getKey())) {
        StdYearGpa targetTerm = entry.getValue();
        targetTerm.setStdGpa(null);
        target.getYearGpas().remove(targetTerm);
      }
    }
    target.setUpdatedAt(source.getUpdatedAt());
  }

  private Map<Semester, StdSemesterGpa> semesterGpa2Map(List<StdSemesterGpa> gpas) {
    Map<Semester, StdSemesterGpa> datas = CollectUtils.newHashMap();
    for (StdSemesterGpa a : gpas) {
      datas.put(a.getSemester(), a);
    }
    return datas;
  }

  private Map<String, StdYearGpa> yearGpa2Map(List<StdYearGpa> gpas) {
    Map<String, StdYearGpa> datas = CollectUtils.newHashMap();
    for (StdYearGpa a : gpas) {
      datas.put(a.getSchoolYear(), a);
    }
    return datas;
  }

  public void setCourseGradeProvider(CourseGradeProvider provider) {
    this.courseGradeProvider = provider;
  }

  public void setGpaPolicy(GpaPolicy gpaPolicy) {
    this.gpaPolicy = gpaPolicy;
  }

}
