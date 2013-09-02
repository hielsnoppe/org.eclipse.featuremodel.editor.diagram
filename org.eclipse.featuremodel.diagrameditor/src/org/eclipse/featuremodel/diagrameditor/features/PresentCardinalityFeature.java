package org.eclipse.featuremodel.diagrameditor.features;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.featuremodel.Group;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil;
import org.eclipse.featuremodel.diagrameditor.utilities.BOUtil.RelationType;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.featuremodel.diagrameditor.utilities.StyleUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
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

  private static final int POLIGON_SIZE = 40;

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
    List<Connection> connections = BOUtil.getAllPictogramElementsForBusinessObject(group, Connection.class, getFeatureProvider());
    Connection[] outerConn = getOuterConnections(connections);
    IPeService peService = Graphiti.getPeService();
    ILocation p0 = peService.getLocationRelativeToDiagram(outerConn[0].getStart());
    Point p1 = calculatePoint(outerConn[0], POLIGON_SIZE);
    Point p2 = calculatePoint(outerConn[1], POLIGON_SIZE);
    int x0 = p0.getX();
    int y0 = p0.getY();
    int x1 = p1.getX();
    int y1 = p1.getY();
    int x2 = p2.getX();
    int y2 = p2.getY();

    int xCurveMiddle = x1 + ((x2 - x1) / 2);
    int yCurveMiddle = y2 + ((y1 - y2) / 2);
    if (xCurveMiddle != x0) {
      xCurveMiddle -= (x0 - xCurveMiddle) / 2;
    }
    if (yCurveMiddle != y0) {
      yCurveMiddle -= (y0 - yCurveMiddle) / 2;
    }

    ContainerShape cardinalityCS = peService.createContainerShape(getDiagram(), true);

    Text text = Graphiti.getGaService().createText(cardinalityCS);
    text.setStyle(StyleUtil.getStyleForCardinalityText(getDiagram()));
    text.setX(xCurveMiddle);
    text.setY(yCurveMiddle);
    text.setHeight(15);
    text.setWidth(35);
    text.setValue(group.getLower() + "..." + group.getUpper());
    peService.setPropertyValue(cardinalityCS, Properties.PROP_KEY_CARDINALITY_TYPE, Properties.PROP_VAL_CARDINALITY_TYPE_PRESENT);
    link(cardinalityCS, group);
  }

  /**
   * Gets two outer connections of set relation.
   * 
   * @param connections
   *          The connections represent relations.
   * @return The array of two outer connections.
   */
  private Connection[] getOuterConnections(List<Connection> connections) {
    int xMin = Integer.MAX_VALUE; // X coordinate of the left outer connection
    int xMax = Integer.MIN_VALUE; // X coordinate of the right outer connection
    Connection[] result = new Connection[2];

    // run trough all connections a look for X coordinate of the connection end
    for (Connection conn : connections) {
      Point p = calculatePoint(conn, 40);

      if (p.getX() < xMin) {
        result[0] = conn;
        xMin = p.getX();
      }

      if (p.getX() >= xMax) {
        result[1] = conn;
        xMax = p.getX();
      }
    }
    return result;
  }

  /**
   * Calculates the coordinates of a connection line point in according to the
   * given distance <code>dis</code> from the connection start point.
   * 
   * @param connection
   *          the connection.
   * @param dis
   *          the distance from the connection start point.
   * @return The calculated point.
   */
  private Point calculatePoint(Connection connection, double dis) {
    // determine line start and end points
    ILocation a = Graphiti.getPeService().getLocationRelativeToDiagram(connection.getStart());
    ILocation b = Graphiti.getPeService().getLocationRelativeToDiagram(connection.getEnd());
    // line vector
    Point ba = Graphiti.getGaService().createPoint(b.getX() - a.getX(), b.getY() - a.getY());
    // norm of the line vector
    double norm = Math.sqrt(ba.getX() * ba.getX() + ba.getY() * ba.getY());
    // calculate coordinates
    double x = a.getX() + dis * (ba.getX() / norm);
    double y = a.getY() + dis * (ba.getY() / norm);

    return Graphiti.getGaService().createPoint((int) x, (int) y);
  }
}
