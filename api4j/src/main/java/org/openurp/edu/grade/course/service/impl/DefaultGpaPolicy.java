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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beangle.commons.collection.CollectUtils;
import org.openurp.base.edu.model.Course;
import org.openurp.base.edu.model.Semester;
import org.openurp.base.std.model.Student;
import org.openurp.code.edu.model.CourseTakeType;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.model.StdGpa;
import org.openurp.edu.grade.course.model.StdSemesterGpa;
import org.openurp.edu.grade.course.model.StdYearGpa;

public class DefaultGpaPolicy implements GpaPolicy {

  /**
   * 平均绩点和平均分的精确位
   */
  private int precision = 2;

  private boolean useArithmeticAvg = false;

  public Float calcGa(List<CourseGrade> grades) {
    if (useArithmeticAvg) {
      return calcArithmeticAvg(grades);
    } else {
      return calcCreditAvg(grades);
    }
  }

  private Float calcCreditAvg(List<CourseGrade> grades) {
    double credits = 0;// 总学分
    double creditGas = 0;// 绩点=∑成绩*学分
    for (CourseGrade grade : grades) {
      if (grade.getCourseTakeType().getId().intValue() != CourseTakeType.Exemption && null != grade.getGp() && grade.getCourse().isCalgp()) {
        Float score = grade.getScore();
        if (null == score) {
          score = 0f;
        }
        double credit = grade.getCourse().getDefaultCredits();
        credits += credit;
        creditGas += credit * score;
      }
    }
    return round((credits == 0) ? 0 : new Float(creditGas / credits));
  }

  private Float calcArithmeticAvg(List<CourseGrade> grades) {
    double scoreSum = 0;// ∑成绩
    int count = 0;
    for (CourseGrade grade : grades) {
      if (grade.getCourseTakeType().getId().intValue() != CourseTakeType.Exemption && null != grade.getGp() && grade.getCourse().isCalgp()) {
        Float score = grade.getScore();
        if (null == score) {
          score = 0f;
        }
        scoreSum += score;
        count += 1;
      }
    }
    return round((scoreSum == 0) ? 0 : new Float(scoreSum / count));
  }

  /**
   * 标准Gpa算法<br>
   * gpa=∑绩点*学分/∑学分
   *
   * @param grades
   * @return
   */
  public Float calcGpa(List<CourseGrade> grades) {
    double credits = 0;// 总学分
    double creditGps = 0;// 绩点=∑绩点*学分
    for (CourseGrade grade : grades) {
      if (grade.getCourseTakeType().getId().intValue() != CourseTakeType.Exemption && null != grade.getGp() && grade.getCourse().isCalgp()) {
        double credit = grade.getCourse().getDefaultCredits();
        credits += credit;
        creditGps += credit * (grade.getGp().doubleValue());
      }
    }
    return round((credits == 0) ? 0 : new Float(creditGps / credits));
  }

  /**
   * 保留小数位(默认四舍五入)
   *
   * @param score
   * @return
   */
  public Float round(Float score) {
    if (null == score) return null;
    int mutilply = (int) Math.pow(10, precision);
    score *= mutilply;
    if (score % 1 >= 0.5) {
      score += 1;
    }
    score -= score % 1;
    return (new Float(score / mutilply));
  }

  public StdGpa calc(Student std, List<CourseGrade> grades, boolean statDetail) {
    StdGpa stdGpa = new StdGpa(std);
    if (statDetail) {
      // 按照学期拆分成绩
      Map<Semester, List<CourseGrade>> gradesMap = CollectUtils.newHashMap();
      for (CourseGrade grade : grades) {
        List<CourseGrade> semesterGrades = gradesMap.get(grade.getSemester());
        if (null == semesterGrades) {
          semesterGrades = CollectUtils.newArrayList();
          gradesMap.put(grade.getSemester(), semesterGrades);
        }
        semesterGrades.add(grade);
      }

      Map<String, List<CourseGrade>> yearGradeMap = CollectUtils.newHashMap();
      for (Semester semester : gradesMap.keySet()) {
        StdSemesterGpa stdTermGpa = new StdSemesterGpa();
        stdTermGpa.setSemester(semester);
        stdGpa.add(stdTermGpa);
        List<CourseGrade> semesterGrades = gradesMap.get(semester);
        List<CourseGrade> yearGrades = yearGradeMap.get(semester.getSchoolYear());
        if (null == yearGrades) {
          yearGrades = CollectUtils.newArrayList();
          yearGradeMap.put(semester.getSchoolYear(), yearGrades);
        }
        yearGrades.addAll(semesterGrades);
        stdTermGpa.setGpa(this.calcGpa(semesterGrades));
        stdTermGpa.setWms(this.calcGa(semesterGrades));
        stdTermGpa.setGradeCount(semesterGrades.size());
        Float[] stats = statCredits(semesterGrades);
        stdTermGpa.setTotalCredits(stats[0]);
        stdTermGpa.setCredits(stats[1]);
      }

      for (String year : yearGradeMap.keySet()) {
        StdYearGpa stdYearGpa = new StdYearGpa();
        stdYearGpa.setSchoolYear(year);
        stdGpa.add(stdYearGpa);
        List<CourseGrade> yearGrades = yearGradeMap.get(year);
        stdYearGpa.setGpa(this.calcGpa(yearGrades));
        stdYearGpa.setWms(this.calcGa(yearGrades));
        stdYearGpa.setGradeCount(yearGrades.size());
        Float[] stats = statCredits(yearGrades);
        stdYearGpa.setTotalCredits(stats[0]);
        stdYearGpa.setCredits(stats[1]);
      }
    }

    stdGpa.setGpa(this.calcGpa(grades));
    stdGpa.setWms(this.calcGa(grades));
    Map<Course, CourseGrade> courseMap = CollectUtils.newHashMap();
    for (CourseGrade grade : grades) {
      CourseGrade exist = courseMap.get(grade.getCourse());
      if (null == exist || !exist.isPassed()) {
        courseMap.put(grade.getCourse(), grade);
      }
    }
    stdGpa.setTotalCount(courseMap.size());
    Float[] totalStats = statCredits(courseMap.values());
    stdGpa.setTotalCredits(totalStats[0]);
    stdGpa.setCredits(totalStats[1]);
    Date now = new Date();
    stdGpa.setUpdatedAt(now);
    return stdGpa;
  }

  /**
   * 统计学分，0为修读学分，1为实得学分
   *
   * @param grades
   * @return
   */
  private Float[] statCredits(Collection<CourseGrade> grades) {
    Float credits = 0f;
    Float all = 0f;
    for (CourseGrade grade : grades) {
      if (grade.isPassed()) credits += grade.getCourse().getDefaultCredits();
      all += grade.getCourse().getDefaultCredits();
    }
    return new Float[] { all, credits };
  }

  public final int getPrecision() {
    return precision;
  }

  public final void setPrecision(int precision) {
    this.precision = precision;
  }

  public boolean isUseArithmeticAvg() {
    return useArithmeticAvg;
  }

  public void setUseArithmeticAvg(boolean useArithmeticAvg) {
    this.useArithmeticAvg = useArithmeticAvg;
  }

}
