/*******************************************************************************
 * Copyright (c) 2013 FZI Forschungszentrum Informatik.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.featuremodel.diagrameditor.utilities;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <code>IdGen</code> supports the generating of unique ids.
 * 
 * 
 */
public class IdGen {
  /**
   * Generates a unique id.
   * 
   * @return The unique id.
   */
  public static String generate() {
    return EcoreUtil.generateUUID();
  }
}
