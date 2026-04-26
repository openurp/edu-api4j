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
package org.openurp.edu.room.model;

import org.beangle.commons.entity.pojo.LongIdObject;
import org.beangle.orm.hibernate.udt.WeekTime;
import org.openurp.base.model.Department;
import org.openurp.code.edu.model.ActivityType;
import org.openurp.base.resource.model.Classroom;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 教室时间占用
 *
 * @depend - - - Room
 */
@Entity(name = "org.openurp.edu.room.model.Occupancy")
public class Occupancy extends LongIdObject {

  private static final long serialVersionUID = 2498530728105897805L;

  /** 教室 */
  @ManyToOne(fetch = FetchType.LAZY)
  protected Classroom room;// 教室/考场/活动场地

  /** 时间 */
  protected WeekTime time = new WeekTime();// 时间安排

  /** 用途 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected ActivityType activityType;

  /** 使用者 */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  protected RoomOccupyApp app;

  protected Long activityId;

  protected java.util.Date updatedAt;

  /** 说明 */
  @NotNull
  @Size(max = 500)
  protected String subject;

  @ManyToOne(fetch = FetchType.LAZY)
  private Department depart;

  private int stdCount;

  private boolean shared;

  public Classroom getRoom() {
    return room;
  }

  public void setRoom(Classroom room) {
    this.room = room;
  }

  public WeekTime getTime() {
    return time;
  }

  public void setTime(WeekTime time) {
    this.time = time;
  }

  public ActivityType getActivityType() {
    return activityType;
  }

  public void setActivityType(ActivityType usage) {
    this.activityType = usage;
  }

  public RoomOccupyApp getApp() {
    return app;
  }

  public void setApp(RoomOccupyApp app) {
    this.app = app;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }

  public java.util.Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.util.Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Department getDepart() {
    return depart;
  }

  public void setDepart(Department depart) {
    this.depart = depart;
  }

  public int getStdCount() {
    return stdCount;
  }

  public void setStdCount(int stdCount) {
    this.stdCount = stdCount;
  }

  public boolean isShared() {
    return shared;
  }

  public void setShared(boolean shared) {
    this.shared = shared;
  }
}
