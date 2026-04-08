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
package org.openurp.edu.program.app;

import org.beangle.commons.inject.bind.AbstractBindModule;
import org.openurp.edu.program.app.dao.hibernate.ExecutivePlanCourseGroupModifyApplyDaoHibernate;
import org.openurp.edu.program.app.dao.hibernate.ExecutivePlanCourseGroupModifyAuditDaoHibernate;
import org.openurp.edu.program.app.dao.hibernate.ExecutivePlanCourseModifyApplyDaoHibernate;
import org.openurp.edu.program.app.dao.hibernate.ExecutivePlanCourseModifyAuditDaoHibernate;
import org.openurp.edu.program.app.service.impl.ExecutivePlanCourseGroupModifyApplyServiceImpl;
import org.openurp.edu.program.app.service.impl.ExecutivePlanCourseGroupModifyAuditServiceImpl;
import org.openurp.edu.program.app.service.impl.ExecutivePlanCourseModifyApplyServiceImpl;
import org.openurp.edu.program.app.service.impl.ExecutivePlanCourseModifyAuditServiceImpl;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class DefaultModule extends AbstractBindModule {

  @Override
  protected void doBinding() {
    bind("executePlanCourseModifyApplyDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseModifyApplyDaoHibernate.class).parent("baseTransactionProxyExt");
    bind("executePlanCourseModifyAuditDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseModifyAuditDaoHibernate.class).parent("baseTransactionProxyExt");
    bind("executePlanCourseGroupModifyApplyDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseGroupModifyApplyDaoHibernate.class).parent("baseTransactionProxyExt");
    bind("executePlanCourseGroupModifyAuditDao", TransactionProxyFactoryBean.class)
        .proxy("target", ExecutivePlanCourseGroupModifyAuditDaoHibernate.class).parent("baseTransactionProxyExt");

    bind("executePlanCourseModifyApplyService", ExecutivePlanCourseModifyApplyServiceImpl.class);
    bind("executePlanCourseModifyAuditService", ExecutivePlanCourseModifyAuditServiceImpl.class);
    bind("executePlanCourseGroupModifyApplyService", ExecutivePlanCourseGroupModifyApplyServiceImpl.class);
    bind("executePlanCourseGroupModifyAuditService", ExecutivePlanCourseGroupModifyAuditServiceImpl.class);

  }

}
