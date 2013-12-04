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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.featuremodel.Feature;
import org.eclipse.featuremodel.FeatureModel;
import org.eclipse.featuremodel.FeatureModelFactory;
import org.eclipse.featuremodel.Group;
import org.eclipse.featuremodel.diagrameditor.FMEDiagramEditorUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.CalCurveMiddleUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.IdGen;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Feature handle moving of the pictogram element represents Feature.
 * 
 */
public class MoveFeatureFeature extends DefaultMoveShapeFeature {

	/**
	 * Creates an instance of {@link MoveFeatureFeature}.
	 * 
	 * @param fp
	 *            The feature provider.
	 */
	public MoveFeatureFeature(IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Checks whether the current pictogram element of the given context can be
	 * moved. This implementation returns <code>true</code> if the pictogram
	 * element represents a Feature.
	 * 
	 * @param context
	 *            The context.
	 * @return true if the pictogram element represents a Feature
	 */
	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Object objToMove = getBusinessObjectForPictogramElement(context
				.getShape());
		// this implementation support only Feature objects
		if (objToMove instanceof Feature) {
			Feature featureToMove = (Feature) objToMove;
			Shape targetContainer = context.getTargetContainer();
			Connection targetConnection = context.getTargetConnection();
			Object target;
			// check whether the new Feature is wanted to be added to a
			// connection
			if (targetConnection != null) {
				target = this.getFeatureProvider()
						.getBusinessObjectForPictogramElement(targetConnection);
			} else {
				target = this.getFeatureProvider()
						.getBusinessObjectForPictogramElement(targetContainer);
			}

			// allow if the target is not the parent Group and not a child of
			// the Feature to move
			if (target instanceof Group //
					&& featureToMove.getParentGroup() != null
					&& !featureToMove.getParentGroup().equals(target) //
					&& !isTargetGroupChild(featureToMove, (Group) target)) {
				return true;
			} else if (target instanceof Feature
					&& !isTargetFeatureChild(featureToMove, (Feature) target)) {
				// allow if the target is a Feature and not a child of the
				// Feature to
				// move
				return true;
			} else if (target instanceof FeatureModel) {
				// allow if the target is the diagram
				return true;
			}

		}
		return false;
	}

	/**
	 * Checks whether the target Feature is a child of the Feature to move.
	 * 
	 * @param featureToMove
	 *            the Feature to move
	 * @param targetFeature
	 *            the target Feature
	 * @return true if the target Feature is a child of the Feature to move.
	 */
	private boolean isTargetFeatureChild(Feature featureToMove,
			Feature targetFeature) {
		for (Group grp : featureToMove.getChildren()) {
			for (Feature child : grp.getFeatures()) {
				if (child.equals(targetFeature)) {
					return true;
				} else if (isTargetFeatureChild(child, targetFeature)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether the target Feature is a child of the Feature to move.
	 * 
	 * @param featureToMove
	 *            the Feature to move
	 * @param targetGroup
	 *            the target Group
	 * @return true if the target Group is a child of the Feature to move.
	 */
	private boolean isTargetGroupChild(Feature featureToMove, Group targetGroup) {
		for (Group grp : featureToMove.getChildren()) {
			if (grp.equals(targetGroup)) {
				return true;
			}

			for (Feature child : grp.getFeatures()) {
				if (isTargetGroupChild(child, targetGroup)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Perform the moving of a Feature. If target object is a Feature or a Group
	 * the Feature to move will be reconnected, otherwise only its position in
	 * the diagram is changed.
	 * 
	 * @param context
	 *            the context
	 */
	@Override
	public void internalMove(IMoveShapeContext context) {
		// check whether the new Feature is wanted to be added to a connection.
		Object newParent;
		if (context.getTargetConnection() != null) {
			newParent = this.getFeatureProvider()
					.getBusinessObjectForPictogramElement(
							context.getTargetConnection());
		} else {
			newParent = this.getFeatureProvider()
					.getBusinessObjectForPictogramElement(
							context.getTargetContainer());
		}

		if (newParent instanceof Feature || newParent instanceof Group) {
			Feature featureToMove = (Feature) this.getFeatureProvider()
					.getBusinessObjectForPictogramElement(context.getShape());

			// remove the old parent Group
			removeParentGroup(featureToMove);

			if (newParent instanceof Feature) {
				Feature newParentFeature = (Feature) newParent;
				// create a new Group an add Feature to it
				Group newParentGroup = createGroup();
				newParentFeature.getChildren().add(newParentGroup);
				newParentGroup.getFeatures().add(featureToMove);
				// add graphical representation
				drawGroup(newParentGroup, featureToMove, newParentFeature);
			} else if (newParent instanceof Group) {
				Group newParentGroup = (Group) newParent;
				// add Feature to the new parent Group of the Group
				newParentGroup.getFeatures().add(featureToMove);
				// add graphical representation of the Group
				drawGroup(newParentGroup, featureToMove,
						featureToMove.getParent());
			}

			// get the current position of the Feature to move
			int x = context.getShape().getGraphicsAlgorithm().getX();
			int y = context.getShape().getGraphicsAlgorithm().getY();

			layoutPictogramElement(context.getShape());

			// get the new position of the Feature to move
			int newX = context.getShape().getGraphicsAlgorithm().getX();
			int newY = context.getShape().getGraphicsAlgorithm().getY();

			// move all child Features too
			moveChildElements(featureToMove, newX - x, newY - y);
		} else {
			super.internalMove(context);
			moveCardinalityText(context);
		}
	}

	/**
	 * Move the cardinality text if it is available. There are two situations:
	 * 1. the current focused feature is collapsed, all the sub cardinality
	 * texts should be moved appropriated 2. the current focused feature is
	 * expanded, only the just below cardinality text should be moved
	 * appropriated
	 * 
	 * @param context
	 */
	private void moveCardinalityText(IMoveShapeContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		boolean isCollapsed = false;
		for (Property prop : pictogramElement.getProperties()) {
			if (prop.getKey().equals(Properties.PROP_KEY_CONTAINER_TYPE)
					&& prop.getValue().equals(
							Properties.PROP_VAL_CONTAINER_TYPE_COLLAPSED)) {
				isCollapsed = true;
			}
		}
		Object[] bos = getFeatureProvider()
				.getAllBusinessObjectsForPictogramElement(pictogramElement);
		for (Object bo : bos) {
			if (bo instanceof Feature) {
				Feature feature = (Feature) bo;
				if (isCollapsed) { // situation 1
					moveAllSubTexts(context, feature);
				} else { // situation 2
					if (feature.getParentGroup() != null) { // if there is a parent group, it should also be moved
						moveTextWithCurveMiddle(context,
								feature.getParentGroup());
					}
					if (feature.getChildren().size() > 0) {
						for (Group group : feature.getChildren()) {
							moveTextWithCurveMiddle(context, group);
						}
					}
				}
			}
		}
	}

	/**
	 * Move all the cardinality texts which are below the current feature.
	 * 
	 * @param context
	 * @param feature
	 */
	private void moveAllSubTexts(IMoveShapeContext context, Feature feature) {
		EList<Group> children = feature.getChildren();
		for (Group group : children) {
			EList<Feature> features = group.getFeatures();
			for (Feature f : features) {
				moveAllSubTexts(context, f);
			}
			moveTextWithRelativePosition(context, group);
		}
	}

	/**
	 * Move the text according to the relative change of the moved feature.
	 * 
	 * @param context
	 * @param group
	 */
	private void moveTextWithRelativePosition(IMoveShapeContext context,
			Group group) {
		PictogramElement[] pes = getFeatureProvider()
				.getAllPictogramElementsForBusinessObject(group);
		for (PictogramElement pe : pes) {
			if (pe.getGraphicsAlgorithm() instanceof Text) {
				Text cardinalityText = (Text) pe.getGraphicsAlgorithm();
				Graphiti.getGaService().setLocation(cardinalityText,
						cardinalityText.getX() + context.getDeltaX(),
						cardinalityText.getY() + context.getDeltaY());
			}
		}
	}

	/**
	 * Move the text according to the middle position of the curve which
	 * represents OR relation.
	 * 
	 * @param context
	 * @param group
	 */
	private void moveTextWithCurveMiddle(IMoveShapeContext context, Group group) {
		PictogramElement[] pes = getFeatureProvider()
				.getAllPictogramElementsForBusinessObject(group);
		for (PictogramElement pe : pes) {
			if (pe.getGraphicsAlgorithm() instanceof Text) {
				Text cardinalityText = (Text) pe.getGraphicsAlgorithm();
				Graphiti.getGaService().setLocation(
						cardinalityText,
						CalCurveMiddleUtil.calXCurveMiddle(group,
								getFeatureProvider()),
						CalCurveMiddleUtil.calYCurveMiddle(group,
								getFeatureProvider()));
			}
		}
	}

	/**
	 * Update all associated Groups after moving the Feature pictogram element.
	 * A Feature is associated with the parent Group and child Groups.
	 * 
	 * @param context
	 *            The context.
	 */
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		Object pe = this.getFeatureProvider()
				.getBusinessObjectForPictogramElement(context.getShape());
		Feature movedFeature = null;
		Group movedGroup = null;

		if (pe instanceof Feature) {
			movedFeature = (Feature) pe;
		} else if (pe instanceof Group) {
			movedGroup = (Group) pe;
		}

		if (movedFeature != null) {
			// update the parent Group if exists
			if (movedFeature.getParentGroup() != null) {
				Connection c = BOUtil.getPictogramElementForBusinessObject(
						movedFeature.getParentGroup(), Connection.class,
						getFeatureProvider());
				this.updatePictogramElement(c);
			}

			// update all child Groups if exist
			for (Group gr : movedFeature.getChildren()) {
				Connection c = BOUtil.getPictogramElementForBusinessObject(gr,
						Connection.class, getFeatureProvider());
				this.updatePictogramElement(c);
			}
		}
		// update the text which represents the cardinality if exists
		else if (movedGroup != null) {
			Connection c = BOUtil.getPictogramElementForBusinessObject(
					movedGroup, Connection.class, getFeatureProvider());
			this.updatePictogramElement(c);
		}
	}

	/**
	 * Moves recursively all child Features of the given Feature.
	 * 
	 * @param parent
	 *            the parent Feature
	 * @param deltaX
	 *            delta in X direction
	 * @param deltaY
	 *            delta in Y direction
	 */
	private void moveChildElements(Feature parent, int deltaX, int deltaY) {
		for (Group grp : parent.getChildren()) {
			for (Feature child : grp.getFeatures()) {
				ContainerShape shape = BOUtil
						.getPictogramElementForBusinessObject(child,
								ContainerShape.class, getFeatureProvider());

				// get the current position of the child Feature
				int x = shape.getGraphicsAlgorithm().getX();
				int y = shape.getGraphicsAlgorithm().getY();

				// move the child Feature in the given delta
				Graphiti.getGaService().setLocation(
						shape.getGraphicsAlgorithm(), x + deltaX, y + deltaY);

				// move all child Features of this child Feature too
				moveChildElements(child, deltaX, deltaY);
			}

		}
	}

	/**
	 * Removes the parent Group of the given Feature.
	 * 
	 * @param featureToMove
	 *            the Feature to move
	 */
	private void removeParentGroup(Feature featureToMove) {
		Group oldParentGroup = featureToMove.getParentGroup();
		oldParentGroup.getFeatures().remove(featureToMove);

		// if the Group is empty
		if (oldParentGroup.getFeatures().size() == 0) {
			// delete all associated pictogram elements
			PictogramElement[] pes = getFeatureProvider()
					.getAllPictogramElementsForBusinessObject(oldParentGroup);
			for (PictogramElement pe : pes) {
				Graphiti.getPeService().deletePictogramElement(pe);
			}
			// delete the business object
			EcoreUtil.delete(oldParentGroup, true);
		} else {
			// otherwise remove connection between Feature to move and the old
			// Group
			ContainerShape featureToMoveCS = BOUtil
					.getPictogramElementForBusinessObject(featureToMove,
							ContainerShape.class, getFeatureProvider());
			Anchor inAnchor = BOUtil.getInputAnchor(featureToMoveCS);
			// in Feature Diagram only one input connection for a Feature
			// allowed
			Connection conn = inAnchor.getIncomingConnections().get(0);
			Graphiti.getPeService().deletePictogramElement(conn);
			// update Group relation notation
			Connection c = BOUtil.getPictogramElementForBusinessObject(
					oldParentGroup, Connection.class, getFeatureProvider());
			updatePictogramElement(c);
		}
		getDiagramBehavior().refresh();
	}

	/**
	 * Creates a new Group model element.
	 * 
	 * @return the new Group
	 */
	private Group createGroup() {
		// create a new Group model object.
		Group group = FeatureModelFactory.eINSTANCE.createGroup();
		group.setId(IdGen.generate());
		FMEDiagramEditorUtil.saveToModelFile(group, getDiagram());
		return group;
	}

	/**
	 * Draws a graphical representation of the Group.
	 * 
	 * @param group
	 *            The Group to add.
	 * @param childFeature
	 *            The new child Feature.
	 * @param parentFeature
	 *            The parent Feature;
	 */
	private void drawGroup(Group group, Feature childFeature,
			Feature parentFeature) {
		// add graphical representation of the created Group
		ContainerShape parentFeatureCS = BOUtil
				.getPictogramElementForBusinessObject(parentFeature,
						ContainerShape.class, getFeatureProvider());
		ContainerShape newFeatureCS = BOUtil
				.getPictogramElementForBusinessObject(childFeature,
						ContainerShape.class, getFeatureProvider());

		// determine source and target anchors
		Anchor sourceAnchor = BOUtil.getOutputAnchor(parentFeatureCS);
		Anchor targetAnchor = BOUtil.getInputAnchor(newFeatureCS);

		AddConnectionContext addGroupContext = new AddConnectionContext(
				sourceAnchor, targetAnchor);
		addGroupContext.setNewObject(group);
		getFeatureProvider().addIfPossible(addGroupContext);
	}
}
