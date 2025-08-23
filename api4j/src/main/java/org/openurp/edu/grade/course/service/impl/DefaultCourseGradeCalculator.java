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

import org.beangle.commons.dao.EntityDao;
import org.beangle.commons.entity.metadata.Model;
import org.beangle.security.Securities;
import org.openurp.base.edu.model.Project;
import org.openurp.base.service.ProjectPropertyService;
import org.openurp.base.std.model.Student;
import org.openurp.code.edu.model.CourseTakeType;
import org.openurp.code.edu.model.ExamStatus;
import org.openurp.code.edu.model.GradeType;
import org.openurp.code.edu.model.GradingMode;
import org.openurp.edu.grade.Grade;
import org.openurp.edu.grade.course.model.CourseGrade;
import org.openurp.edu.grade.course.model.CourseGradeState;
import org.openurp.edu.grade.course.model.ExamGrade;
import org.openurp.edu.grade.course.model.GaGrade;
import org.openurp.edu.grade.course.service.CourseGradeCalculator;
import org.openurp.edu.grade.course.service.GradeRateService;
import org.openurp.edu.grade.course.service.NumPrecisionReserveMethod;
import org.openurp.edu.grade.course.service.ScoreConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 缺省的成绩计算器
 */
public class DefaultCourseGradeCalculator implements CourseGradeCalculator {

  private static final GradeType Ga = new GradeType(GradeType.GA_ID);
  private static final GradeType MakeupGa = new GradeType(GradeType.MAKEUP_GA_ID);
  private static final GradeType Makeup = new GradeType(GradeType.MAKEUP_ID);
  private static final GradeType DelayGa = new GradeType(GradeType.DELAY_GA_ID);
  private static final GradeType Delay = new GradeType(GradeType.DELAY_ID);
  private static final GradeType End = new GradeType(GradeType.END_ID);

  protected EntityDao entityDao;
  protected GradeRateService gradeRateService;

  protected ProjectPropertyService projectPropertyService;

  /**
   * 计算总评的最小期末成绩，用于计算期末总评和缓考总评
   */
  private float minEndScore = 0;
  /**
   * 当修读类别为免听，缓考卷面和期末卷面直接作为总评，不再参考期末成绩。
   */
  private boolean endIsGaWhenFreeListening = true;

  protected NumPrecisionReserveMethod numPrecisionReserveMethod = new MoreHalfReserveMethod();

  /**
   * 计算总评成绩,最终成绩,是否通过和绩点以及分数字面值
   *
   * @param grade
   */
  public void calcFinal(CourseGrade grade, CourseGradeState state) {
    if (!grade.isPublished()) grade.setStatus(guessFinalStatus(grade));
    updateScore(grade, calcScore(grade, state), null);
  }

  @Override
  public void calcAll(CourseGrade grade, CourseGradeState state) {
    calcEndGa(grade, state);
    calcMakeupDelayGa(grade, state);
  }

  @Override
  public GaGrade calcEndGa(CourseGrade grade, CourseGradeState state) {
    // 这里需要再加载一次学生信息，因为需要在后续的逻辑中使用
    Long stdId = grade.getStd().getId();
    grade.setStd(entityDao.get(Student.class, stdId));

    GaGrade gag = getGaGrade(grade, Ga);
    Float gaScore = calcEndGaScore(grade, state);
    updateScore(gag, gaScore, null);
    if (!gag.isPublished() && null != state) gag.setStatus(state.getStatus(gag.getGradeType()));

    if (!grade.isPublished()) grade.setStatus(guessFinalStatus(grade));
    if (gag.getStatus() == grade.getStatus()) {
      updateScore(grade, calcScore(grade, state), null);
    }
    return gag;
  }

  /**
   * 计算总评成绩
   * <p>
   * 如果仅包含总评，仍旧返回原来的值
   */
  protected Float calcEndGaScore(CourseGrade grade, CourseGradeState state) {
    // 计算除去补考、缓考的其他考试的违纪、作弊考试情况
    boolean isCheating = false;
    for (ExamGrade eg : grade.getExamGrades()) {
      if (eg.getGradeType().equals(Makeup) || eg.getGradeType().equals(Delay)) continue;
      if (null != eg.getExamStatus() && eg.getExamStatus().isCheating()) {
        isCheating = true;
        break;
      }
    }
    // 出现作弊和违纪情况下(无论期中还是期末)，总评成绩就重置为0.
    if (isCheating) return 0f;

    Float ga = null;
    GaGrade gaGrade = (GaGrade) grade.getGrade(Ga);
    if (gaGrade != null) {
      ga = gaGrade.getScore();
      if (grade.getExamGrades().isEmpty()) return ga;
    }

    ExamGrade endGrade = grade.getExamGrade(End);
    gaGrade = getGaGrade(grade, Ga);// 没有总评的情况会创建一个
    if (null != endGrade) {
      // 免修不免试的学生直接将期末成绩记入总评
      if (endIsGaWhenFreeListening
          && grade.isFreeListening()) {
        return addDelta(gaGrade, endGrade.getScore(), state);
      }
      // 进行期末最低分判断
      if (!hasDelta(gaGrade) && null != endGrade.getScore() && Float.compare(endGrade.getScore().floatValue(),
          minEndScore) < 0) {
        return addDelta(gaGrade, endGrade.getScore(), state);
      }
    }

    double totalGa = 0;
    int totalPercent = 0;
    int scorePercent = 0;// 分数不为空所有成绩的累计百分比
    boolean hasEmptyEndGrade = false;
    for (ExamGrade examGrade : grade.getExamGrades()) {
      if (examGrade.getGradeType().equals(Delay)) continue;
      if (examGrade.getGradeType().equals(End) && null == examGrade.getScore()) {
        hasEmptyEndGrade = true;
      }
      Short myPercent = getPercent(examGrade, grade, state);
      if (null == myPercent || myPercent <= 0) continue;

      // 成绩和考试情况不是有效组合(空成绩就应该有非正常考试情况,防止用户在未录入期末时,界面上计算出总评来)
      if (null == examGrade.getScore() && (null == examGrade.getExamStatus()
          || examGrade.getExamStatus().getId().equals(ExamStatus.NORMAL)))
        continue;
      Float score = examGrade.getScore();
      if (examGrade.getScorePercent() != null) myPercent = examGrade.getScorePercent();
      totalPercent += myPercent;

      if (null != score) {
        scorePercent += myPercent;
        totalGa += (myPercent / 100.0d) * score.doubleValue();
      }
    }
    // 整个百分比不足100
    if (totalPercent < 100) {
      if (totalPercent > 0) {// 如果存在百分比,否则返回原值
        return null;
      } else {
        return gaGrade.getScore();
      }
    } else {
      if (scorePercent <= 51 || hasEmptyEndGrade) {// 有分数比重不足半数
        return null;
      } else {
        return addDelta(gaGrade, new Float(totalGa), state);
      }
    }
  }

  @Override
  public GaGrade calcMakeupDelayGa(CourseGrade grade, CourseGradeState state) {
    // 这里需要再加载一次学生信息，因为需要在后续的逻辑中使用
    Long stdId = grade.getStd().getId();
    grade.setStd(entityDao.get(Student.class, stdId));

    // 计算两类总评
    List<GradeType> gatypes = Arrays.asList(DelayGa, MakeupGa);
    GaGrade makeupDelayGa = null;
    ExamGrade makeupDelayGrade = null;
    for (GradeType gatype : gatypes) {
      GaGrade gag = getGaGrade(grade, gatype);
      Float gaScore = null;
      if (gatype.equals(DelayGa)) {
        gaScore = calcDelayGaScore(grade, state);
        makeupDelayGrade = grade.getExamGrade(Delay);
      } else {
        gaScore = calcMakeupGaScore(grade, state);
        makeupDelayGrade = grade.getExamGrade(Makeup);
      }
      if (null == gaScore && (null == makeupDelayGrade || makeupDelayGrade.getExamStatus().getId().equals(ExamStatus.NORMAL))) {
        Grade gaGrade = grade.getGrade(gatype);
        grade.getGaGrades().remove(gaGrade);
      } else {
        updateScore(gag, gaScore, null);
        if (!gag.isPublished() && null != state) gag.setStatus(state.getStatus(gag.getGradeType()));
        makeupDelayGa = gag;
      }
    }
    if (null != makeupDelayGa) {
      if (!grade.isPublished()) grade.setStatus(guessFinalStatus(grade));
      if (makeupDelayGa.getStatus() == grade.getStatus()) {
        updateScore(grade, calcScore(grade, state), null);
      }
    }
    return makeupDelayGa;
  }

  private Short getPercent(ExamGrade eg, CourseGrade cg, CourseGradeState cgs) {
    if (null != eg.getScorePercent()) return eg.getScorePercent();
    if (eg.getGradeType().equals(Delay)) {
      ExamGrade end = cg.getExamGrade(End);
      if (null != end && null != end.getScorePercent()) return end.getScorePercent();
      else return null == cgs ? null : cgs.getPercent(End);
    } else {
      return null == cgs ? null : cgs.getPercent(eg.getGradeType());
    }
  }

  /**
   * 计算缓考总评
   *
   * @param grade
   * @param state
   */
  protected Float calcDelayGaScore(CourseGrade grade, CourseGradeState state) {
    Float gascore = null;
    GaGrade gaGrade = (GaGrade) grade.getGrade(DelayGa);
    if (gaGrade != null) {
      gascore = gaGrade.getScore();
      if (grade.getExamGrade(Delay) == null) return gascore;
    }

    ExamGrade deGrade = grade.getExamGrade(Delay);
    if (deGrade == null) return null;
    if (null != deGrade.getExamStatus() && deGrade.getExamStatus().isCheating()) return 0f;

    boolean delayIsGa = projectPropertyService.get(grade.getProject(), "edu.grade.delay_is_ga", "false").equals("true");
    if (delayIsGa) return deGrade.getScore();

    gaGrade = getGaGrade(grade, DelayGa);

    // 免修不免试的学生直接将期末成绩记入总评
    if (endIsGaWhenFreeListening
        && grade.isFreeListening()) {
      return addDelta(gaGrade, deGrade.getScore(), state);
    }

    // 进行缓考最低分判断
    if (!hasDelta(gaGrade) && null != deGrade.getScore() && Float.compare(deGrade.getScore().floatValue(),
        minEndScore) < 0) {
      return addDelta(gaGrade, deGrade.getScore(), state);
    }

    double ga = 0;
    int totalPercent = 0;// 累计百分比
    int scorePercent = 0; // 分数不为空所有成绩的累计百分比

    for (ExamGrade examGrade : grade.getExamGrades()) {
      if (examGrade.getGradeType().equals(End)) continue;
      Short myPercent = getPercent(examGrade, grade, state);
      if (null == myPercent || myPercent <= 0) continue;
      // 无效成绩
      if (null == examGrade.getScore() && (null == examGrade.getExamStatus()
          || examGrade.getExamStatus().getId().equals(ExamStatus.NORMAL)))
        continue;
      Float score = examGrade.getScore();
      totalPercent += myPercent;
      if (null != score) {
        scorePercent += myPercent;
        ga += (myPercent / 100.0d) * score.doubleValue();
      }
    }

    if (totalPercent < 100) {
      return null;
    } else {
      return (scorePercent < 51) ? null : addDelta(gaGrade, new Float(ga), state);
    }
  }

  /**
   * 计算补考总评
   */
  protected Float calcMakeupGaScore(CourseGrade grade, CourseGradeState gradeState) {
    Float gascore = null;
    GaGrade gaGrade = (GaGrade) grade.getGrade(MakeupGa);
    if (gaGrade != null) {
      gascore = gaGrade.getScore();
      if (grade.getExamGrade(Makeup) == null) return gascore;
    }
    ExamGrade makeup = grade.getExamGrade(Makeup);
    if (null == makeup || null == makeup.getScore()) return null;
    // 作弊或者违纪0分处理
    if (null != makeup.getExamStatus() && makeup.getExamStatus().isCheating()) return 0f;
    gaGrade = getGaGrade(grade, MakeupGa);
    addDelta(gaGrade, makeup.getScore(), gradeState);
    boolean makeupIsGa = projectPropertyService.get(grade.getProject(), "edu.grade.makeup_is_ga", "false").equals("true");
    if (makeupIsGa) return gaGrade.getScore();
    else return (Float.compare(gaGrade.getScore(), 60) >= 0) ? 60f : gaGrade.getScore();
  }

  /**
   * 计算最终得分 MAX(GA,发布的缓考总评,发布的补考成绩) <br>
   * 如果成绩中有加分项，则在最终成绩上添加该分数。
   *
   * @return 最好的，可以转化为最终成绩的考试成绩,如果没有任何可选记录仍旧返回原值
   */
  protected Float calcScore(CourseGrade grade, CourseGradeState state) {
    Float best = null;
    for (GaGrade gg : grade.getGaGrades()) {
      if (null == gg.getScore()) continue;
      // 非期末总评时，须已发布
      Float myScore = null;
      if (!gg.getGradeType().equals(Ga)) {
        if (gg.isPublished()) myScore = gg.getScore();
      } else {
        myScore = gg.getScore();
      }
      if (null == best) best = myScore;
      if (null != myScore && myScore.compareTo(best) > 0) best = myScore;
    }
    return best;
  }

  /**
   * 返回考试成绩中的总评成绩
   */
  private GaGrade getGaGrade(CourseGrade grade, GradeType gradeType) {
    GaGrade gaGrade = (GaGrade) grade.getGrade(gradeType);
    if (null != gaGrade) return gaGrade;
    gaGrade = (GaGrade) Model.newInstance(GaGrade.class);
    gaGrade.setGradingMode(grade.getGradingMode());
    gaGrade.setGradeType(gradeType);
    gaGrade.setUpdatedAt(new Date());
    // 这里不能设置状态，例如在已经发布的成绩上录入则默认就是发布了，不妥
    // examGrade.setStatus(grade.getStatus());
    grade.addGaGrade(gaGrade);
    return gaGrade;
  }

  private int guessFinalStatus(CourseGrade grade) {
    int status = Grade.Status.New;
    Grade ga = grade.getGrade(Ga);
    if (null != ga && ga.getStatus() > status) status = ga.getStatus();
    Grade makeup = grade.getGrade(MakeupGa);
    if (null != makeup && makeup.getStatus() > status) status = makeup.getStatus();
    Grade delay = grade.getGrade(DelayGa);
    if (null != delay && delay.getStatus() > status) status = delay.getStatus();
    return status;
  }

  public final void updateScore(CourseGrade grade, Float score, GradingMode newStyle) {
    GradingMode gradingMode = newStyle;
    if (null == gradingMode) gradingMode = grade.getGradingMode();
    else grade.setGradingMode(gradingMode);

    ScoreConverter converter = gradeRateService.getConverter(grade.getProject(), gradingMode);
    grade.setScore(score);
    grade.setScoreText(converter.convert(score));
    if (null != grade.getCourseTakeType()
        && grade.getCourseTakeType().getId().equals(CourseTakeType.Exemption)) {
      grade.setPassed(true);
    } else {
      grade.setPassed(converter.isPassed(grade.getScore()));
    }
    if (grade.getCourseTakeType().getId().equals(CourseTakeType.Exemption)) {
      if (null == grade.getScore()) {//防止计算成0
        grade.setGp(null);
      } else {
        grade.setGp(converter.calcGp(grade.getScore()));
      }
    } else {
      grade.setGp(converter.calcGp(grade.getScore()));
    }
    grade.setOperator(Securities.getUsername());
    if (null == grade.getCreatedAt()) {
      grade.setCreatedAt(new Date());
    }
    grade.setUpdatedAt(new Date());
  }

  public final void updateScore(ExamGrade eg, Float score, GradingMode newStyle) {
    eg.setScore(score);
    GradingMode gradingMode = newStyle;
    if (null == gradingMode) gradingMode = eg.getGradingMode();
    else eg.setGradingMode(gradingMode);

    ScoreConverter converter = gradeRateService.getConverter(eg.getCourseGrade().getProject(),
        eg.getGradingMode());
    eg.setScoreText(converter.convert(eg.getScore()));
    eg.setPassed(converter.isPassed(eg.getScore()));
    if (null == eg.getCreatedAt()) {
      eg.setCreatedAt(new Date());
    }
    eg.setUpdatedAt(new java.util.Date());
    eg.setOperator(Securities.getUsername());
  }

  public final void updateScore(GaGrade gag, Float score, GradingMode newStyle) {
    gag.setScore(score);
    GradingMode gradingMode = newStyle;
    if (null == gradingMode) gradingMode = gag.getGradingMode();
    else gag.setGradingMode(gradingMode);

    ScoreConverter converter = gradeRateService.getConverter(gag.getCourseGrade().getProject(), gradingMode);
    gag.setScoreText(converter.convert(gag.getScore()));
    gag.setPassed(converter.isPassed(gag.getScore()));
    gag.setUpdatedAt(new java.util.Date());
    if (null == gag.getCreatedAt()) {
      gag.setCreatedAt(new Date());
    }
    gag.setOperator(Securities.getUsername());
    if (gag.getCourseGrade().getCourseTakeType().getId().equals(CourseTakeType.Exemption)) {
      if (null == gag.getScore()) {//防止计算成0
        gag.setGp(null);
      } else {
        gag.setGp(converter.calcGp(gag.getScore()));
      }
    } else {
      gag.setGp(converter.calcGp(gag.getScore()));
    }
  }

  protected boolean hasDelta(GaGrade gaGrade) {
    return null != gaGrade.getDelta();
  }

  protected Float getDelta(GaGrade gaGrade, Float score, CourseGradeState state) {
    return gaGrade.getDelta();
  }

  private final Float addDelta(GaGrade gaGrade, Float score, CourseGradeState state) {
    if (null == score) return null;
    Project project = gaGrade.getCourseGrade().getProject();
    Float delta = getDelta(gaGrade, score, state);
    if (null != delta) {
      Float ga = new Float(reserve(project, delta + score, state));
      gaGrade.setScore(ga);
      return ga;
    } else {
      Float ga = reserve(project, score, state);
      gaGrade.setScore(ga);
      return ga;
    }
  }

  private int getDefaultScorePrecision(Project project) {
    return Integer.parseInt(projectPropertyService.get(project, "edu.grade.score_precision", "0"));
  }

  protected Float reserve(Project project, Float score, CourseGradeState state) {
    if (null == score) return score;
    int precision = (null == state) ? getDefaultScorePrecision(project) : state.getScorePrecision();
    return numPrecisionReserveMethod.reserve(score, precision);
  }

  protected double reserve(Project project, double score, CourseGradeState state) {
    int precision = (null == state) ? getDefaultScorePrecision(project) : state.getScorePrecision();
    return numPrecisionReserveMethod.reserve(score, precision);
  }

  public void setEntityDao(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  public void setGradeRateService(GradeRateService gradeRateService) {
    this.gradeRateService = gradeRateService;
  }

  public GradeRateService getGradeRateService() {
    return gradeRateService;
  }

  public float getMinEndScore() {
    return minEndScore;
  }

  public void setMinEndScore(float minEndScore) {
    this.minEndScore = minEndScore;
  }

  public boolean isEndIsGaWhenFreeListening() {
    return endIsGaWhenFreeListening;
  }

  public void setEndIsGaWhenFreeListening(boolean endIsGaWhenFreeListening) {
    this.endIsGaWhenFreeListening = endIsGaWhenFreeListening;
  }

  public NumPrecisionReserveMethod getNumPrecisionReserveMethod() {
    return numPrecisionReserveMethod;
  }

  public void setNumPrecisionReserveMethod(NumPrecisionReserveMethod numPrecisionReserveMethod) {
    this.numPrecisionReserveMethod = numPrecisionReserveMethod;
  }

  public void setProjectPropertyService(ProjectPropertyService projectPropertyService) {
    this.projectPropertyService = projectPropertyService;
  }
}
