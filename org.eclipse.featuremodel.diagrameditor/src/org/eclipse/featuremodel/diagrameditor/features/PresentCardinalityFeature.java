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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.featuremodel.Group;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil.RelationType;
import org.eclipse.featuremodel.diagrameditor.utilities.CalCurveMiddleUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.featuremodel.diagrameditor.utilities.StyleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

/**
 * Feature handler to present corresponding cardinality for set relations in the
 * diagram, for example, 1...3 will be presented under the polygon which
 * represents the XOR relation.
 */
public class PresentCardinalityFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   *          feature provider
   */
  public PresentCardinalityFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Gets the name of this function feature.
   * 
   * @return the name
   */
  @Override
  public String getName() {
    return "Present Cardinality";
  }

  /**
   * Gets the description of the function feature.
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return "Present the cardinality";
  }

  /**
   * Checks whether the cardinality of the given context can be presented. This
   * implementation returns <code>true</code> if there is at least one set
   * relation exist and the current cardinality type not equals to the type
   * {@link Properties#PROP_VAL_CARDINALITY_TYPE_PRESENT}.
   * 
   * @param context
   *          the context
   * @return boolean
   */
  @Override
  public boolean canExecute(ICustomContext context) {
    // set true if there is a set relation exist in the diagram
    boolean hasSetRelation = false;
    EList<Connection> connections = getDiagram().getConnections();
    for (Connection conn : connections) {
      Group group = (Group) getBusinessObjectForPictogramElement(conn);
      if (RelationType.OR.equals(BOUtil.getRelationType(group))) {
        hasSetRelation = true;
        break;
      }
    }

    String cardinalityType = Graphiti.getPeService().getPropertyValue(getDiagram(), Properties.PROP_KEY_CARDINALITY_TYPE);
    // return true if there has a set relation and the cardinality type is
    // present or null
    if (hasSetRelation && (!Properties.PROP_VAL_CARDINALITY_TYPE_PRESENT.equals(cardinalityType) || cardinalityType == null)) {
      return true;
    }
    return false;
  }

  /**
   * Present cardinality for all set relations.
   * 
   * @param context
   *          the context
   */
  @Override
  public void execute(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    peService.setPropertyValue(getDiagram(), Properties.PROP_KEY_CARDINALITY_TYPE, Properties.PROP_VAL_CARDINALITY_TYPE_PRESENT);
    EList<Connection> connections = getDiagram().getConnections();
    Set<Group> groupSet = new HashSet<Group>();
    for (Connection conn : connections) {
      // remove the redundant groups
      Group group = (Group) getBusinessObjectForPictogramElement(conn);
      groupSet.add(group);
    }

    for (Group group : groupSet) {
      if (group.getFeatures().size() > 1 && RelationType.OR.equals(BOUtil.getRelationType(group))) {
        ContainerShape relationPE = BOUtil.getPictogramElementForBusinessObject(group, ContainerShape.class, getFeatureProvider());
        if (relationPE == null) {
          relationPE = peService.createContainerShape(getDiagram(), true);
          link(relationPE, group);
        }
        createCardinalityGraphics(group);
      }
    }
  }

  /**
   * According to the group information and container shape to draw a text at
   * the appropriate location to show the cardinality for each set relation.
   * 
   * @param group
   */
  private void createCardinalityGraphics(Group group) {

    ContainerShape cardinalityCS = Graphiti.getPeService().createContainerShape(getDiagram(), true);

    Text text = Graphiti.getGaService().createText(cardinalityCS);
    text.setStyle(StyleUtil.getStyleForCardinalityText(getDiagram()));
    text.setX(CalCurveMiddleUtil.calXCurveMiddle(group, getFeatureProvider()) - 9);
    text.setY(CalCurveMiddleUtil.calYCurveMiddle(group, getFeatureProvider()));
    text.setHeight(15);
    text.setWidth(35);
    text.setValue(group.getLower() + "..." + group.getUpper());
    Graphiti.getPeService().setPropertyValue(cardinalityCS, Properties.PROP_KEY_CARDINALITY_TYPE, Properties.PROP_VAL_CARDINALITY_TYPE_PRESENT);
    link(cardinalityCS, group);
  }
}
