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
package org.openurp.edu.program;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.program.major.service.impl.MajorPlanAuditServiceImpl;
import org.openurp.edu.program.major.service.impl.MajorPlanCourseGroupServiceImpl;
import org.openurp.edu.program.major.service.impl.MajorPlanCourseServiceImpl;
import org.openurp.edu.program.major.service.impl.MajorPlanServiceImpl;
import org.openurp.edu.program.plan.dao.hibernate.ExecutivePlanCourseDaoHibernate;
import org.openurp.edu.program.plan.dao.hibernate.ExecutivePlanCourseGroupDaoHibernate;
import org.openurp.edu.program.plan.dao.hibernate.ExecutivePlanDaoHibernate;
import org.openurp.edu.program.plan.dao.impl.PlanCommonDaoHibernate;
import org.openurp.edu.program.plan.dao.impl.PlanCourseCommonDaoHibernate;
import org.openurp.edu.program.plan.dao.impl.PlanCourseGroupCommonDaoHibernate;
import org.openurp.edu.program.plan.service.impl.*;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class DefaultModule extends AbstractBindModule {

  @Override
  protected void doBinding() {
    bind("baseTransactionProxyExt", TransactionProxyFactoryBean.class).parent("baseTransactionProxy")
        .setAbstract().property("transactionAttributes",
        props("save*=PROPAGATION_REQUIRED", "update*=PROPAGATION_REQUIRED",
            "remove*=PROPAGATION_REQUIRED", "delete*=PROPAGATION_REQUIRED",
            "create*=PROPAGATION_REQUIRED", "gen*=PROPAGATION_REQUIRED", "copy*=PROPAGATION_REQUIRED",
            "init*=PROPAGATION_REQUIRED", "add*=PROPAGATION_REQUIRED", "approved*=PROPAGATION_REQUIRED",
            "rejected*=PROPAGATION_REQUIRED", "*=PROPAGATION_REQUIRED,readOnly"));
    // props("*=PROPAGATION_REQUIRED"));
    bind("executePlanDao", TransactionProxyFactoryBean.class).proxy("target", ExecutivePlanDaoHibernate.class)
        .parent("baseTransactionProxyExt");
    bind("executePlanCourseGroupDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseGroupDaoHibernate.class).parent("baseTransactionProxyExt");
    bind("executePlanCourseDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseDaoHibernate.class).parent("baseTransactionProxyExt");

    bind("executePlanService", ExecutivePlanServiceImpl.class);
    bind("executePlanCourseGroupService", ExecutivePlanCourseGroupServiceImpl.class);
    bind("executePlanCourseService", ExecutivePlanCourseServiceImpl.class);
    bind("planCompareService", PlanCompareServiceImpl.class);

    bind("majorPlanService", MajorPlanServiceImpl.class);
    bind("MajorPlanCourseGroupService", MajorPlanCourseGroupServiceImpl.class);
    bind("MajorPlanAuditService", MajorPlanAuditServiceImpl.class);
    bind("MajorPlanCourseService", MajorPlanCourseServiceImpl.class);

    // commonDAO
    bind("planCommonDao", TransactionProxyFactoryBean.class).proxy("target", PlanCommonDaoHibernate.class)
        .parent("baseTransactionProxyExt");
    bind("planCourseCommonDao", TransactionProxyFactoryBean.class)
        .proxy("target", PlanCourseCommonDaoHibernate.class).parent("baseTransactionProxyExt");
    bind("planCourseGroupCommonDao", TransactionProxyFactoryBean.class)
        .proxy("target", PlanCourseGroupCommonDaoHibernate.class).parent("baseTransactionProxyExt");

  }

}
