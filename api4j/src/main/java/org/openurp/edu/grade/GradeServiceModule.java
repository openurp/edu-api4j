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
package org.openurp.edu.grade;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.grade.app.service.impl.GradeInputSwithServiceImpl;
import org.openurp.edu.grade.app.service.impl.TranscriptTemplateServiceImpl;
import org.openurp.edu.grade.course.service.CourseGradePublishStack;
import org.openurp.edu.grade.course.service.GradingModeHelper;
import org.openurp.edu.grade.course.service.impl.*;
import org.openurp.edu.grade.course.service.internal.*;
import org.openurp.edu.grade.plan.service.internal.AuditSettingServiceImpl;
import org.openurp.edu.grade.plan.service.internal.AuditPlanServiceImpl;
import org.openurp.edu.grade.plan.service.listeners.*;
import org.openurp.edu.grade.plan.service.observers.PlanAuditPersistObserver;
import org.openurp.edu.grade.setting.service.impl.CourseGradeSettingsImpl;
import org.openurp.edu.grade.transcript.service.impl.*;

public class GradeServiceModule extends AbstractBindModule {
  @Override
  protected void doBinding() {
    bind("bestGradeCourseGradeProvider", BestGradeCourseGradeProviderImpl.class);
    bind(CourseGradeSettingsImpl.class);

    bind("gradeRateService", GradeRateServiceImpl.class);
    bind("bestGradeFilter", BestGradeFilter.class);
    bind("gpaPolicy", DefaultGpaPolicy.class);
    bind("bestOriginGradeFilter", BestOriginGradeFilter.class);
    bind("passedGradeFilter", PassedGradeFilter.class);
    bind("gradeFilterRegistry", SpringGradeFilterRegistry.class);

    bind("courseGradeService", CourseGradeServiceImpl.class);
    bind("gradeInputSwithService", GradeInputSwithServiceImpl.class);
    bind(TranscriptTemplateServiceImpl.class);
    bind("scriptGradeFilter", ScriptGradeFilter.class);
    bind("courseGradeProvider", CourseGradeProviderImpl.class);
    bind("courseGradeCalculator", DefaultCourseGradeCalculator.class);
    bind("gpaService", DefaultGpaService.class);
    bind("defaultGpaStatService", DefaultGpaStatService.class);
    bind("gradeCourseTypeProvider", GradeCourseTypeProviderImpl.class);

    bind("makeupStdStrategy", MakeupByExamStrategy.class);
    bind("gradingModeHelper", GradingModeHelper.class);
    bind("gradingModeStrategy", DefaultGradingModeStrategy.class);
    bind("stdGradeService", StdGradeServiceImpl.class);
    bind("makeupGradeFilter", MakeupGradeFilter.class);
    bind("recalcGpPublishListener", RecalcGpPublishListener.class);
    bind("examTakerGeneratePublishListener", ExamTakerGeneratePublishListener.class);
    bind("courseGradePublishStack", CourseGradePublishStack.class).property("listeners",
        list(ref("recalcGpPublishListener"), ref("examTakerGeneratePublishListener")));
    bind(DefaultGradeTypePolicy.class);
    bind(MoreHalfReserveMethod.class, More01ReserveMethod.class).shortName();

    bind(TranscriptPlanCourseProvider.class, TranscriptGpaProvider.class,
        TranscriptPublishedGradeProvider.class, TranscriptGraduateProvider.class,
        SpringTranscriptDataProviderRegistry.class, TranscriptCertificateGradeProvider.class)
        .shortName();

    // 这些监听器再配置文件中顺序应该按照以下顺序
    bind("planAuditAlternativeCourseListener", PlanAuditAlternativeCourseListener.class);
    bind("planAuditCourseTakerListener", PlanAuditCourseTakerListener.class);
    bind("planAuditExemptCourseListener", PlanAuditExemptCourseListener.class);
    bind("planAuditCourseTypeMatchListener", PlanAuditCourseTypeMatchListener.class);
    bind("planAuditCommonElectiveListener", PlanAuditCommonElectiveListener.class);
    bind("planAuditPersistObserver", PlanAuditPersistObserver.class);

    bind("planAuditService", AuditPlanServiceImpl.class).property("listeners",
        list(ref("planAuditAlternativeCourseListener"),
            ref("planAuditExemptCourseListener"),
            ref("planAuditCourseTypeMatchListener"),
            ref("planAuditCommonElectiveListener"),
            ref("planAuditCourseTakerListener")));

    bind("auditSettingService", AuditSettingServiceImpl.class);
  }
}
