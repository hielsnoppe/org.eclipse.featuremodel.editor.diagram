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

import java.util.List;

import org.eclipse.featuremodel.Feature;
import org.eclipse.featuremodel.Group;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Feature handle removing of a Feature pictogram element. All child Features
 * and Groups are removed before a Feature object is removed.
 * 
 */
public class RemoveFeatureFeature extends DefaultRemoveFeature {

  /**
   * The parent Group of the Feature to remove.
   */
  private Group parentGroup;

  /**
   * Creates an instance of {@link RemoveFeatureFeature}.
   * 
   * @param fp
   *          The feature provider.
   */
  public RemoveFeatureFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Checks if given object could be removed. This implementation returns
   * <code>true</code> if the pictogram element represents a Feature.
   * 
   * @param context
   *          the context
   * @return <code>true</code> if the pictogram element represents a Feature.
   */
  @Override
  public boolean canRemove(IRemoveContext context) {
    Object obj = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (obj instanceof Feature);
  }

  /**
   * Remove all child Features and Groups pictogram elements before a Feature
   * object is removed.
   * 
   * @param context
   *          The delete context.
   */
  @Override
  public void preRemove(IRemoveContext context) {
    Feature feature = (Feature) this.getBusinessObjectForPictogramElement(context.getPictogramElement());

    // if child Groups exist remove all
    if (!feature.getChildren().isEmpty()) {
      Group[] groups = feature.getChildren().toArray(new Group[0]);
      for (Group group : groups) {
        // remove all child Features pictogrm elements
        Feature[] features = group.getFeatures().toArray(new Feature[0]);
        for (Feature childFeature : features) {
          PictogramElement pe = this.getFeatureProvider().getPictogramElementForBusinessObject(childFeature);
          RemoveContext removeContext = new RemoveContext(pe);
          IRemoveFeature deleteFeature = getFeatureProvider().getRemoveFeature(removeContext);
          deleteFeature.execute(removeContext);
        }
      }
    }

    this.parentGroup = feature.getParentGroup();
  }

  /**
   * Remove the parent Group pictogram element if the removed Feature pictogram
   * element was the last in the Group.
   * 
   * @param context
   *          The delete context.
   */
  @Override
  public void postRemove(IRemoveContext context) {
    if (this.parentGroup != null) {
      // remove the parent Group if this was the last Feature in Group
      if (this.parentGroup.getFeatures().size() < 1) {
        ContainerShape cs = BOUtil.getPictogramElementForBusinessObject(this.parentGroup, ContainerShape.class, getFeatureProvider());
        Graphiti.getPeService().deletePictogramElement(cs);
      }
      else { // otherwise update Group
        Connection c = BOUtil.getPictogramElementForBusinessObject(this.parentGroup, Connection.class, getFeatureProvider());
        this.updatePictogramElement(c);
      }
    }
  }

  /**
   * Removes all connections of the given anchor.
   * 
   * @param anchor
   *          the anchor
   */
  @Override
  protected void removeAllConnections(Anchor anchor) {
    List<Connection> allConnections = Graphiti.getPeService().getAllConnections(anchor);
    for (Connection connection : allConnections) {
      ConnectionDecorator[] decorators = connection.getConnectionDecorators().toArray(new ConnectionDecorator[0]);
      for (ConnectionDecorator decorator : decorators) {
        Graphiti.getPeService().deletePictogramElement(decorator);
      }
      Graphiti.getPeService().deletePictogramElement(connection);
    }
  }
}
