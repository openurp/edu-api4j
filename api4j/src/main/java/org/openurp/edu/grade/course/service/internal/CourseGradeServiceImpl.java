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
package org.openurp.edu.grade.course.service.internal;

import org.beangle.commons.bean.comparators.PropertyComparator;
import org.beangle.commons.collection.CollectUtils;
import org.beangle.commons.dao.Operation;
import org.beangle.commons.dao.impl.BaseServiceImpl;
import org.beangle.commons.dao.query.builder.OqlBuilder;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.commons.lang.Objects;
import org.beangle.commons.lang.Strings;
import org.beangle.commons.lang.functor.Predicate;
import org.openurp.base.edu.model.Project;
import org.openurp.code.edu.model.CourseTakeType;
import org.openurp.code.edu.model.GradeType;
import org.openurp.edu.clazz.model.Clazz;
import org.openurp.edu.grade.Grade;
import org.openurp.edu.grade.course.model.*;
import org.openurp.edu.grade.course.service.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.openurp.edu.grade.Grade.Status.New;
import static org.openurp.edu.grade.Grade.Status.Published;

public class CourseGradeServiceImpl extends BaseServiceImpl implements CourseGradeService {

  protected CourseGradeCalculator calculator;

  protected GradeCourseTypeProvider gradeCourseTypeProvider;

  protected CourseGradePublishStack publishStack;

  protected CourseGradeSettings settings;

  private List<CourseGrade> getGrades(Clazz clazz) {
    OqlBuilder<CourseGrade> query = OqlBuilder.from(CourseGrade.class, "courseGrade");
    query.where("courseGrade.clazz = :clazz", clazz);
    return entityDao.search(query);
  }

  public CourseGradeState getState(Clazz clazz) {
    List<CourseGradeState> list = entityDao.get(CourseGradeState.class, "clazz", clazz);
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  public List<GradeType> getPublishableGradeTypes(Project project) {
    // 查找除去最终成绩之外的所有可发布成绩
    List<GradeType> gradeTypes = entityDao.getAll(GradeType.class);
    CollectUtils.filter(gradeTypes, new Predicate<GradeType>() {
      @Override
      public Boolean apply(GradeType input) {
        return input.isGa() || input.getId().equals(GradeType.FINAL_ID);
      }
    });
    Collections.sort(gradeTypes, new PropertyComparator("code"));
    return gradeTypes;
  }

  /**
   * 发布学生成绩
   */
  public void publish(String clazzIdSeq, GradeType[] gradeTypes, boolean isPublished) {
    List<Clazz> clazzes = entityDao.get(Clazz.class, Strings.transformToLong(clazzIdSeq.split(",")));
    if (CollectUtils.isNotEmpty(clazzes)) {
      for (Clazz clazz : clazzes) {
        updateState(clazz, gradeTypes, isPublished ? Published : New);
      }
    }

  }

  /**
   * 依据状态调整成绩
   */
  public void recalculate(CourseGradeState gradeState) {
    if (null == gradeState) {
      return;
    }
    List<GradeType> published = CollectUtils.newArrayList();
    for (ExamGradeState egs : gradeState.getExamStates()) {
      if (egs.getStatus() == Published) published.add(egs.getGradeType());
    }
    for (GaGradeState egs : gradeState.getGaStates()) {
      if (egs.getStatus() == Published) published.add(egs.getGradeType());
    }
    List<CourseGrade> grades = getGrades(gradeState.getClazz());
    for (CourseGrade grade : grades) {
      if (grade.getCourseTakeType().getId().equals(CourseTakeType.Exemption)) {
        continue;
      }
      updateGradeState(grade, gradeState, grade.getProject());

      for (GaGradeState state : gradeState.getGaStates()) {
        GradeType gradeType = state.getGradeType();
        updateGradeState(grade.getGaGrade(gradeType), state, grade.getProject());
      }

      for (ExamGradeState state : gradeState.getExamStates()) {
        GradeType gradeType = state.getGradeType();
        ExamGrade examGrade = grade.getExamGrade(gradeType);
        if (null != examGrade) {
          examGrade.setWeight(state.getWeight());
          updateGradeState(grade.getExamGrade(gradeType), state, grade.getProject());
        }
      }
      grade.getExamGrades().removeIf(x -> gradeState.getState(x.getGradeType()) == null);
      calculator.calcAll(grade, gradeState);
    }
    entityDao.saveOrUpdate(grades);
    if (!published.isEmpty()) publish(gradeState.getClazz().getId().toString(),
        published.toArray(new GradeType[published.size()]), true);
  }

  public void remove(Clazz clazz, GradeType gradeType) {
    CourseGradeState state = getState(clazz);
    Collection<CourseGrade> courseGrades = entityDao.get(CourseGrade.class, "clazz", clazz);
    CourseGradeSetting gradeSetting = settings.getSetting(clazz.getProject());
    Collection<Object> save = CollectUtils.newArrayList();
    Collection<Object> remove = CollectUtils.newArrayList();

    Collection<GradeType> gts = CollectUtils.newHashSet(gradeType);
    if (GradeType.GA_ID.equals(gradeType.getId())) {
      gts.addAll(gradeSetting.getGaElementTypes());
    } else if (GradeType.MAKEUP_GA_ID.equals(gradeType.getId())) {
      gts.add(new GradeType(GradeType.MAKEUP_ID));
    } else if (GradeType.DELAY_GA_ID.equals(gradeType.getId())) {
      gts.add(new GradeType(GradeType.DELAY_ID));
    }

    for (CourseGrade courseGrade : courseGrades) {
      if (courseGrade.getCourseTakeType().getId().equals(CourseTakeType.Exemption)) {
        courseGrade.setClazz(null);
        courseGrade.setCrn("--");
        save.add(courseGrade);
        continue;
      }
      if (GradeType.FINAL_ID.equals(gradeType.getId())) {
        if (New == courseGrade.getStatus()) remove.add(courseGrade);
      } else {
        if (removeGrade(courseGrade, gts, state)) {
          remove.add(courseGrade);
        } else {
          save.add(courseGrade);
        }
      }
    }
    if (null != state) {
      if (GradeType.FINAL_ID.equals(gradeType.getId())) {
        state.setStatus(New);
        state.getExamStates().clear();
        state.getGaStates().clear();
      } else {
        for (GradeType gt : gts) {
          if (gt.isGa()) {
            final GaGradeState ggs = (GaGradeState) state.getState(gt);
            state.getGaStates().remove(ggs);
          } else {
            final ExamGradeState egs = (ExamGradeState) state.getState(gt);
            state.getExamStates().remove(egs);
          }
        }
      }
    }
    if (state.getExamStates().isEmpty() && state.getGaStates().isEmpty()) {
      remove.add(state);
    } else {
      save.add(state);
    }
    // FIXME 日志
    entityDao.execute(Operation.saveOrUpdate(save).remove(remove));
  }

  private boolean removeGrade(CourseGrade courseGrade, Collection<GradeType> gradeTypes,
                              CourseGradeState state) {
    for (GradeType gradeType : gradeTypes) {
      if (gradeType.isGa()) {
        final GaGrade ga = courseGrade.getGaGrade(gradeType);
        if (null != ga && New == ga.getStatus()) courseGrade.getGaGrades().remove(ga);
      } else {
        final ExamGrade exam = courseGrade.getExamGrade(gradeType);
        if (null != exam && New == exam.getStatus()) courseGrade.getExamGrades().remove(exam);
      }
    }
    if (CollectUtils.isNotEmpty(courseGrade.getGaGrades())
        || CollectUtils.isNotEmpty(courseGrade.getExamGrades())) {
      calculator.calcAll(courseGrade, state);
      return false;
    } else {
      return true;
    }
  }

  public void setCalculator(CourseGradeCalculator calculator) {
    this.calculator = calculator;
  }

  public void setCourseGradePublishStack(CourseGradePublishStack courseGradePublishStack) {
    this.publishStack = courseGradePublishStack;
  }

  public void setGradeCourseTypeProvider(GradeCourseTypeProvider gradeCourseTypeProvider) {
    this.gradeCourseTypeProvider = gradeCourseTypeProvider;
  }

  /**
   * 依据状态信息更新成绩的状态和记录方式
   *
   * @param grade
   * @param state
   */
  private void updateGradeState(Grade grade, GradeState state, Project project) {
    if (null != grade && null != state) {
      if (!Objects.equals(grade.getGradingMode(), state.getGradingMode())) {
        grade.setGradingMode(state.getGradingMode());
        ScoreConverter converter = calculator.getGradeRateService().getConverter(project,
            state.getGradingMode());
        grade.setScoreText(converter.convert(grade.getScore()));
      }
      grade.setStatus(state.getStatus());
    }
  }

  private void updateState(Clazz clazz, GradeType[] gradeTypes, int status) {
    List<CourseGradeState> courseGradeStates = entityDao.get(CourseGradeState.class, "clazz", clazz);
    CourseGradeState gradeState = null;
    for (GradeType gradeType : gradeTypes) {
      if (courseGradeStates.isEmpty()) {
        gradeState = Model.newInstance(CourseGradeState.class);
      } else {
        gradeState = courseGradeStates.get(0);
      }
      if (gradeType.getId().equals(GradeType.FINAL_ID)) {
        gradeState.setStatus(status);
      } else {
        gradeState.updateStatus(gradeType, status);
      }
    }

    List<CourseGrade> grades = entityDao.get(CourseGrade.class, "clazz", clazz);
    List<Operation> toBeSaved = CollectUtils.newArrayList();
    Set<CourseGrade> published = CollectUtils.newHashSet();
    for (CourseGrade grade : grades) {
      if (grade.getCourseTakeType().getId().equals(CourseTakeType.Exemption)) continue;
      for (GradeType gradeType : gradeTypes) {
        boolean updated = false;
        if (gradeType.getId().equals(GradeType.FINAL_ID)) {
          grade.setStatus(status);
          updated = true;
        } else {
          Grade examGrade = grade.getGrade(gradeType);
          if (null != examGrade) {
            examGrade.setStatus(status);
            updated = true;
          }
        }
        if (updated) published.add(grade);
      }
    }
    if (status == Published) toBeSaved.addAll(publishStack.onPublish(published, gradeState, gradeTypes));
    toBeSaved.addAll(Operation.saveOrUpdate(clazz, gradeState).saveOrUpdate(published).build());
    entityDao.execute(toBeSaved.toArray(new Operation[toBeSaved.size()]));
  }

  public void setSettings(CourseGradeSettings settings) {
    this.settings = settings;
  }

}
