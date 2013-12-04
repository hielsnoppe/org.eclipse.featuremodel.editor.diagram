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

import org.eclipse.featuremodel.diagrameditor.utilities.DialogUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Feature handle changing the color of all cardinality text in the diagram.
 */
public class ChangeCardinalityTextColorFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   */
  public ChangeCardinalityTextColorFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Gets the name of this function feature.
   * 
   * @return the name
   */
  @Override
  public String getName() {
    return "Change Color";
  }

  /**
   * Gets the description of the function feature.
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return "Change the foreground color";
  }

  /**
   * Checks whether the color of cardinality text can be changed or not. This
   * implementation returns <code>true</code> if there is a cardinality text be
   * selected.
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
      GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
      if (ga instanceof Text) {
        return true;
      }
    }

    return false;
  }

  /**
   * Set the user selected color as the foreground color of the text.
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
      pe.getGraphicsAlgorithm().setForeground(newColor);
    }
    getDiagramBehavior().refresh();
  }

}
