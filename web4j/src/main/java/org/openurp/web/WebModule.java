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

import org.beangle.commons.conversion.impl.DefaultConversion;
import org.beangle.commons.inject.Scope;
import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.base.util.String2RatioConvertor;
import org.openurp.edu.web.helper.ClazzSearchHelper;
import org.openurp.edu.web.helper.ExecutivePlanSearchHelper;
import org.openurp.service.squartz.URPSchedulerFactoryBean;
import org.openurp.web.action.DataQueryAction;
import org.openurp.web.action.HomeAction;
import org.openurp.web.action.api.CourseAction;
import org.openurp.web.action.api.DirectionAction;
import org.openurp.web.action.api.MajorAction;
import org.openurp.web.action.api.NormalclassAction;
import org.openurp.web.action.api.ProgramAction;
import org.openurp.web.action.api.SquadAction;
import org.openurp.web.action.api.TeacherAction;
import org.openurp.web.helper.*;
import org.openurp.web.rule.RuleParameterPopulator;
import org.openurp.web.view.component.semester.ui.MenuSemesterCalendar;
import org.openurp.web.view.component.semester.ui.SemesterUIFactory;

public class WebModule extends AbstractBindModule {

  protected void doBinding() {
    bind(SquadAction.class, DataQueryAction.class);
    bind(TeacherAction.class);
    bind(MajorAction.class);
    bind(DirectionAction.class);
    bind(CourseAction.class);
    bind(NormalclassAction.class);
    bind(ProgramAction.class);

    bind(HomeAction.class);

    bind(SecurityHelper.class);

    bind(LogHelper.class);

    bind(RuleParameterPopulator.class);
    bind("stdSearchHelper", StdSearchHelper.class);
    SemesterUIFactory.register("MENU", new MenuSemesterCalendar());

    bind("projectContext", ProjectContextImpl.class);
    bind(URPSchedulerFactoryBean.class).lazy(false).in(Scope.SINGLETON);

    DefaultConversion.Instance.addConverter(new String2RatioConvertor());

    bind("baseInfoSearchHelper", BaseInfoSearchHelper.class);
    bind("squadSearchHelper", SquadSearchHelper.class);
    bind("clazzSearchHelper", ClazzSearchHelper.class);
    bind("executePlanSearchHelper", ExecutivePlanSearchHelper.class);
  }
}
