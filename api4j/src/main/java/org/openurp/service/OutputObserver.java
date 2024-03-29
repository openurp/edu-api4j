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
package org.openurp.service;

import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public interface OutputObserver {

  public static int good = 1;

  public static int warnning = 2;

  public static int error = 3;

  public void outputNotify(int level, OutputMessage arg2) throws IOException;

  public void outputNotify(int level, OutputMessage msgObj, boolean increaceProcess);

  public void notifyStart() throws IOException;

  public void notifyStart(String summary, int count, String[] msgs);

  public void notifyFinish() throws IOException;

  public String message(OutputMessage msgObj);

  public String messageOf(String key);

  public String messageOf(String key, Object arg0);

  public PrintWriter getWriter();

  public void setWriter(PrintWriter writer);
}
