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
package org.eclipse.featuremodel.diagrameditor.features;

import org.eclipse.featuremodel.Feature;
import org.eclipse.featuremodel.diagrameditor.utilities.DialogUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class ChangeFeatureColorFeature extends AbstractCustomFeature {
  /**
   * The constructor.
   * 
   * @param fp
   */
  public ChangeFeatureColorFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Gets the name of this function feature.
   * 
   * @return the name
   */
  @Override
  public String getName() {
    return "Change Feature Color";
  }

  /**
   * Gets the description of the function feature.
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return "Change the feature color";
  }

  /**
   * Checks whether the color of feature can be changed or not. This
   * implementation returns <code>true</code> if there is a feature be selected.
   * 
   * @param context
   *          the context
   * @return boolean
   */
  @Override
  public boolean canExecute(ICustomContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    if (pes == null || pes.length == 0) { // nothing selected
      return false;
    }

    for (PictogramElement pe : pes) {
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof Feature)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Set the user selected color as the background color of the selected feature.
   */
  @Override
  public void execute(ICustomContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    Color currentColor = Graphiti.getGaService().manageColor(getDiagram(), 0, 0, 0);
    Color newColor = DialogUtil.editColor(currentColor);
    if (newColor == null) {
      return;
    }
    for (PictogramElement pe : pes) {
      pe.getGraphicsAlgorithm().setBackground(newColor);
    }
    getDiagramBehavior().refresh();
  }

}
