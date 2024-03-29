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
package org.openurp.edu.program.model;

import org.beangle.commons.entity.HierarchyEntity;
import org.beangle.commons.entity.pojo.LongIdObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 培养计划文档章节
 */
@Entity(name = "org.openurp.edu.program.model.ProgramDocSection")
public class ProgramDocSection extends LongIdObject implements HierarchyEntity<ProgramDocSection, Long> {

  private static final long serialVersionUID = 3516521279941525138L;

  /**
   * 内部编码
   */
  @NotNull
  @Size(max = 20)
  private String indexno;

  /**
   * 标题
   */
  @NotNull
  @Size(max = 100)
  private String name;

  /**
   * 内容
   */
  @Size(max = 50000)
  @Lob
  @Type(type = "org.hibernate.type.TextType")
  private String contents;

  /**
   * 文档
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private ProgramDoc doc;

  /**
   * 上级章节
   */
  @ManyToOne(fetch = FetchType.LAZY)
  ProgramDocSection parent;

  /**
   * 下级章节列表
   */
  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ProgramDocSection> children;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getIndexno() {
    return indexno;
  }

  public void setIndexno(String indexno) {
    this.indexno = indexno;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public ProgramDoc getDoc() {
    return doc;
  }

  public void setDoc(ProgramDoc doc) {
    this.doc = doc;
  }

  public ProgramDocSection getParent() {
    return parent;
  }

  public void setParent(ProgramDocSection parent) {
    this.parent = parent;
  }

  public List<ProgramDocSection> getChildren() {
    return children;
  }

  public void setChildren(List<ProgramDocSection> children) {
    this.children = children;
  }

}
