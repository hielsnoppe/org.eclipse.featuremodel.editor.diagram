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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

/**
 * Feature handle hiding the legend box which located at the right bottom corner
 * in the diagram editor.
 * 
 */
public class HideLegendBoxFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   */
  public HideLegendBoxFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Hide Legend Box";
  }

  @Override
  public String getDescription() {
    return "Hide the current legend box";
  }

  /**
   * Checks whether the legend box of the given context can be hidden. This
   * implementation returns <code>true</code> if the legend box type is not null
   * and also not equals to the type
   * {@link Properties#PROP_VAL_LEGENDBOX_TYPE_HIDDEN}.
   * 
   * @param context
   *          the context
   * @return boolean
   */
  @Override
  public boolean canExecute(ICustomContext context) {
    boolean result = false;
    String propertyValue = Graphiti.getPeService().getPropertyValue(getDiagram(), Properties.PROP_KEY_LEGENDBOX_TYPE);
    if (propertyValue != null && !propertyValue.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_HIDDEN)) {
      result = true;
    }
    return result;
  }

  /**
   * Remove the current legend box.
   */
  @Override
  public void execute(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    peService.setPropertyValue(getDiagram(), Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_HIDDEN);
    EList<Shape> children = getDiagram().getChildren();
    List<Shape> deletableShapes = new ArrayList<Shape>();

    for (Shape shape : children) {
      String legendBoxType = peService.getPropertyValue(shape, Properties.PROP_KEY_LEGENDBOX_TYPE) != null ? peService.getPropertyValue(shape,
          Properties.PROP_KEY_LEGENDBOX_TYPE) : "";
      if (legendBoxType.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_ADAPTED) || legendBoxType.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE)) {
        deletableShapes.add(shape);
      }
    }

    for (Shape shape : deletableShapes) {
      peService.deletePictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
    }
  }

}
