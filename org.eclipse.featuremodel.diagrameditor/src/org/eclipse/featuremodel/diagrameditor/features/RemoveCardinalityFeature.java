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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.featuremodel.Group;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

/**
 * Feature handler to remove corresponding cardinality for set relations in the
 * diagram.
 * 
 */
@SuppressWarnings("restriction")
public class RemoveCardinalityFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   *          feature provider
   */
  public RemoveCardinalityFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Gets the name of this function feature.
   * 
   * @return the name
   */
  @Override
  public String getName() {
    return "Hide Cardinality";
  }

  /**
   * Gets the description of the function feature.
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return "Hide the cardinality";
  }

  /**
   * Checks whether the cardinality of the given context can be hidden. This
   * implementation returns <code>true</code> if the cardinality type is not
   * null and also not equals to the type
   * {@link Properties#PROP_VAL_CARDINALITY_TYPE_HIDDEN}.
   * 
   * @param context
   *          the context
   * @return boolean
   */
  @Override
  public boolean canExecute(ICustomContext context) {
    String cardinalityType = Graphiti.getPeService().getPropertyValue(getDiagram(), Properties.PROP_KEY_CARDINALITY_TYPE);
    if (!Properties.PROP_VAL_CARDINALITY_TYPE_HIDDEN.equals(cardinalityType) && cardinalityType != null) {
      return true;
    }
    return false;
  }

  /**
   * Remove all cardinality for all set relations.
   * 
   * @param context
   *          the context
   */
  @Override
  public void execute(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    peService.setPropertyValue(getDiagram(), Properties.PROP_KEY_CARDINALITY_TYPE, Properties.PROP_VAL_CARDINALITY_TYPE_HIDDEN);
    PictogramElement[] pes = context.getPictogramElements();
    for (PictogramElement pe : pes) {
      if (pe instanceof DiagramImpl) {
        DiagramImpl diagram = (DiagramImpl) pe;
        EList<Connection> connections = diagram.getConnections();
        Set<Group> groupSet = new HashSet<Group>();
        for (Connection conn : connections) {
          // remove the redundant groups
          Group group = (Group) getBusinessObjectForPictogramElement(conn);
          groupSet.add(group);
        }

        for (Group group : groupSet) {
          if (group.getFeatures().size() > 1 && BOUtil.RelationType.OR.equals(BOUtil.getRelationType(group))) {
            ContainerShape relationPE = BOUtil.getPictogramElementForBusinessObject(group, ContainerShape.class, getFeatureProvider());
            if (relationPE == null) {
              relationPE = peService.createContainerShape(getDiagram(), true);
              link(relationPE, group);
            }
            removeCardinalityGraphics(relationPE);
          }
        }
      }
    }
  }

  /**
   * According to the group information and the container shape to remove all
   * cardinality graphics in the diagram.
   * @param pe
   *          the container shape
   */
  private void removeCardinalityGraphics(ContainerShape pe) {

    Collection<Shape> allContainedShapes = Graphiti.getPeService().getAllContainedShapes(pe.getContainer());

    Set<Text> texts = new HashSet<Text>();
    for (Shape shape : allContainedShapes) {
      if (shape instanceof ContainerShape) {
        ContainerShape cs = (ContainerShape) shape;
        if (cs.getGraphicsAlgorithm() instanceof Text) {
          Text text = (Text) cs.getGraphicsAlgorithm();
          texts.add(text);
        }
      }
    }

    for (Text text : texts) {
      Graphiti.getPeService().deletePictogramElement(text.getPictogramElement());
    }

  }

}
