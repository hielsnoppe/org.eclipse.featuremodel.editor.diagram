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
package org.eclipse.featuremodel.diagrameditor;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.graphiti.ui.editor.DiagramEditorActionBarContributor;
import org.eclipse.jface.action.IToolBarManager;

/**
 * This class is responsible for managing the installation and removal of global
 * menus, menu items, and toolbar buttons for a editor.
 * 
 */
public class FMEEditorActionBarContributor extends DiagramEditorActionBarContributor {

  /**
   * Adds Actions to the given IToolBarManager, which is displayed above the
   * editor. See the corresponding method in the super class.
   * 
   * @param tbm
   *          the {@link IToolBarManager}
   * 
   * @see org.eclipse.graphiti.ui.editor.DiagramEditorActionBarContributor#contributeToToolBar(IToolBarManager)
   */
  @Override
  public void contributeToToolBar(IToolBarManager tbm) {
    super.contributeToToolBar(tbm);
    // remove alignment tools
    tbm.remove(GEFActionConstants.ALIGN_LEFT);
    tbm.remove(GEFActionConstants.ALIGN_CENTER);
    tbm.remove(GEFActionConstants.ALIGN_RIGHT);
    tbm.remove(GEFActionConstants.ALIGN_TOP);
    tbm.remove(GEFActionConstants.ALIGN_MIDDLE);
    tbm.remove(GEFActionConstants.ALIGN_BOTTOM);
    tbm.remove(GEFActionConstants.MATCH_WIDTH);
    tbm.remove(GEFActionConstants.MATCH_HEIGHT);
  }

}
