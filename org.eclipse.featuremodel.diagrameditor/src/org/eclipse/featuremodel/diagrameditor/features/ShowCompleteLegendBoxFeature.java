package org.eclipse.featuremodel.diagrameditor.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.featuremodel.diagrameditor.utilities.Properties;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.ColorConstant;

/**
 * Feature handle showing complete legend box which located at the right bottom
 * corner in the diagram editor.
 * 
 */
public class ShowCompleteLegendBoxFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   */
  public ShowCompleteLegendBoxFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Show Complete Legend Box";
  }

  @Override
  public String getDescription() {
    return "Show all cardinality grahics in the diagram";
  }

  /**
   * Checks whether the complete legend box of the given context can be
   * presented. This implementation returns <code>true</code> if the legend box
   * type is not null and also not equals to the type
   * {@link Properties#PROP_VAL_LEGENDBOX_TYPE_COMPLETE}.
   * 
   * @param context
   *          the context
   * @return boolean
   */
  @Override
  public boolean canExecute(ICustomContext context) {
    boolean result = true;
    Diagram diagram = getDiagram();
    String propertyValue = Graphiti.getPeService().getPropertyValue(diagram, Properties.PROP_KEY_LEGENDBOX_TYPE);
    if (propertyValue != null && propertyValue.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE)) {
      result = false;
    }
    return result;
  }

  /**
   * Show the complete legend box.
   */
  @Override
  public void execute(ICustomContext context) {

    deleteCurrentLegendBox();

    Graphiti.getPeService().setPropertyValue(getDiagram(), Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    drawBoxEdgeGraph(context);
    drawMandatoryGraph(context);
    drawOptionalGraph(context);
    drawXORGraph(context);
    drawORGraph(context);
  }

  /**
   * Delete current legend box if it exist.
   */
  private void deleteCurrentLegendBox() {
    IPeService peService = Graphiti.getPeService();
    Diagram diagram = getDiagram();

    // If there is already adapted legend box exist, then delete it before show
    // the complete legend box.
    String propertyValue = peService.getPropertyValue(diagram, Properties.PROP_KEY_LEGENDBOX_TYPE);
    if (propertyValue != null && propertyValue.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_ADAPTED)) {
      EList<Shape> children = diagram.getChildren();
      List<Shape> shapesToDelete = new ArrayList<Shape>();
      for (Shape shape : children) {
        String legendBoxType = peService.getPropertyValue(shape, Properties.PROP_KEY_LEGENDBOX_TYPE) != null ? peService.getPropertyValue(shape,
            Properties.PROP_KEY_LEGENDBOX_TYPE) : "";
        if (legendBoxType.equals(Properties.PROP_VAL_LEGENDBOX_TYPE_ADAPTED)) {
          shapesToDelete.add(shape);
        }
      }

      for (Shape shape : shapesToDelete) {
        peService.deletePictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
      }
    }
  }

  /**
   * Draw the legend box edge.
   * 
   * @param context
   */
  private void drawBoxEdgeGraph(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();
    Diagram diagram = getDiagram();
    Shape rectangleShape = peService.createShape(diagram, true);
    Rectangle rectangle = gaService.createRectangle(rectangleShape);
    rectangle.setHeight(100);
    rectangle.setWidth(200);
    rectangle.setTransparency(0.0);
    rectangle.setLineWidth(2);
    rectangle.setBackground(manageColor(ColorConstant.WHITE));
    rectangle.setFilled(true);
    gaService.setLocation(rectangle, context.getX(), context.getY());
    PictogramElement pictogramElement = diagram.getChildren().get(0).getGraphicsAlgorithm().getPictogramElement();
    peService.setPropertyValue(rectangleShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);
    link(rectangleShape, pictogramElement);
  }

  /**
   * Draw the "Mandatory" part of the legend box.
   * 
   * @param context
   */
  private void drawMandatoryGraph(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();
    Diagram diagram = getDiagram();

    int x = context.getX();
    int y = context.getY();

    Shape ellipseShape = peService.createShape(diagram, true);
    Shape polylineShape = peService.createShape(diagram, true);
    Shape textShape = peService.createShape(diagram, true);

    ellipseShape.setVisible(true);
    Ellipse ellipse = gaService.createEllipse(ellipseShape);
    ellipse.setHeight(10);
    ellipse.setWidth(10);
    ellipse.setForeground(manageColor(ColorConstant.BLACK));
    ellipse.setBackground(manageColor(ColorConstant.BLACK));
    gaService.setLocation(ellipse, x + 10, y + 30);
    peService.setPropertyValue(ellipseShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] points = new int[] { x + 14, y + 32, x + 14, y + 20, x + 14, y + 5 };
    Polyline polyline = gaService.createPolyline(polylineShape, points);
    polyline.setForeground(manageColor(ColorConstant.BLACK));
    polyline.setLineWidth(2);
    polyline.setFilled(true);
    peService.setPropertyValue(polylineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    Text mandatoryText = gaService.createText(textShape);
    mandatoryText.setForeground(manageColor(ColorConstant.BLACK));
    mandatoryText.setHeight(30);
    mandatoryText.setWidth(70);
    mandatoryText.setValue("Mandatory");
    gaService.setLocation(mandatoryText, x + 30, y + 10);
    peService.setPropertyValue(textShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);
  }

  /**
   * Draw the "Optional" part of the legend box.
   * 
   * @param context
   */
  private void drawOptionalGraph(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();
    Diagram diagram = getDiagram();

    int x = context.getX();
    int y = context.getY();

    Shape ellipseShape = peService.createShape(diagram, true);
    Shape polylineShape = peService.createShape(diagram, true);
    Shape textShape = peService.createShape(diagram, true);

    Ellipse ellipse = gaService.createEllipse(ellipseShape);
    ellipse.setHeight(10);
    ellipse.setWidth(10);
    ellipse.setForeground(manageColor(ColorConstant.BLACK));
    ellipse.setBackground(manageColor(ColorConstant.WHITE));
    gaService.setLocation(ellipse, x + 10, y + 80);
    peService.setPropertyValue(ellipseShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] points = new int[] { x + 14, y + 82, x + 14, y + 70, x + 14, y + 55 };
    Polyline polyline = gaService.createPolyline(polylineShape, points);
    polyline.setForeground(manageColor(ColorConstant.BLACK));
    polyline.setLineWidth(2);
    polyline.setFilled(true);
    peService.setPropertyValue(polylineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    Text optionalText = gaService.createText(textShape);
    optionalText.setForeground(manageColor(ColorConstant.BLACK));
    optionalText.setHeight(30);
    optionalText.setWidth(70);
    optionalText.setValue("Optional");
    gaService.setLocation(optionalText, x + 30, y + 60);
    peService.setPropertyValue(textShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);
  }

  /**
   * Draw the "XOR" part of the legend box.
   * 
   * @param context
   */
  private void drawXORGraph(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();
    Diagram diagram = getDiagram();

    int x = context.getX();
    int y = context.getY();

    Shape leftLineShape = peService.createShape(diagram, true);
    Shape rightlineShape = peService.createShape(diagram, true);
    Shape polylineShape = peService.createShape(diagram, true);
    Shape textShape = peService.createShape(diagram, true);

    int[] leftPoints = new int[] { x + 116, y + 10, x + 111, y + 20, x + 101, y + 40 };
    Polyline leftLine = gaService.createPolyline(leftLineShape, leftPoints);
    leftLine.setForeground(manageColor(ColorConstant.BLACK));
    leftLine.setLineWidth(2);
    leftLine.setFilled(true);
    peService.setPropertyValue(leftLineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] rightPoints = new int[] { x + 115, y + 10, x + 120, y + 20, x + 130, y + 40 };
    Polyline rightLine = gaService.createPolyline(rightlineShape, rightPoints);
    rightLine.setForeground(manageColor(ColorConstant.BLACK));
    rightLine.setLineWidth(2);
    rightLine.setFilled(true);
    peService.setPropertyValue(rightlineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] polylinePoints = new int[] { x + 107, y + 25, x + 115, y + 30, x + 123, y + 25 };
    int[] beforeAfter = new int[] { 0, 0, 20, 20, 0, 0 };
    Polyline polyline = gaService.createPolyline(polylineShape, polylinePoints, beforeAfter);
    polyline.setForeground(manageColor(ColorConstant.BLACK));
    polyline.setLineWidth(2);
    polyline.setFilled(true);
    peService.setPropertyValue(polylineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    Text xorText = gaService.createText(textShape);
    xorText.setForeground(manageColor(ColorConstant.BLACK));
    xorText.setHeight(30);
    xorText.setWidth(70);
    xorText.setValue("Alternative");
    gaService.setLocation(xorText, x + 130, y + 10);
    peService.setPropertyValue(textShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);
  }

  /**
   * Draw the "OR" part of the legend box.
   * 
   * @param context
   */
  private void drawORGraph(ICustomContext context) {
    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();
    Diagram diagram = getDiagram();

    int x = context.getX();
    int y = context.getY();

    Shape leftLineShape = peService.createShape(diagram, true);
    Shape rightlineShape = peService.createShape(diagram, true);
    Shape middlelineShape = peService.createShape(diagram, true);
    Shape polygonShape = peService.createShape(diagram, true);
    Shape textShape = peService.createShape(diagram, true);

    int[] leftPoints = new int[] { x + 116, y + 60, x + 111, y + 70, x + 101, y + 90 };
    Polyline leftLine = gaService.createPolyline(leftLineShape, leftPoints);
    leftLine.setForeground(manageColor(ColorConstant.BLACK));
    leftLine.setLineWidth(2);
    leftLine.setFilled(true);
    peService.setPropertyValue(leftLineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] rightPoints = new int[] { x + 115, y + 60, x + 120, y + 70, x + 130, y + 90 };
    Polyline rightLine = gaService.createPolyline(rightlineShape, rightPoints);
    rightLine.setForeground(manageColor(ColorConstant.BLACK));
    rightLine.setLineWidth(2);
    rightLine.setFilled(true);
    peService.setPropertyValue(rightlineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] middlePoints = new int[] { x + 115, y + 60, x + 115, y + 70, x + 115, y + 90 };
    Polyline middleLine = gaService.createPolyline(middlelineShape, middlePoints);
    middleLine.setForeground(manageColor(ColorConstant.BLACK));
    middleLine.setLineWidth(2);
    middleLine.setFilled(true);
    peService.setPropertyValue(middlelineShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    int[] polylinePoints = new int[] { x + 116, y + 60, x + 107, y + 75, x + 115, y + 80, x + 123, y + 75 };
    int[] beforeAfter = new int[] { 0, 0, 0, 0, 20, 20, 0, 0 };
    Polygon polygon = gaService.createPolygon(polygonShape, polylinePoints, beforeAfter);
    polygon.setBackground(manageColor(ColorConstant.BLACK));
    polygon.setLineVisible(false);
    peService.setPropertyValue(polygonShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);

    Text orText = gaService.createText(textShape);
    orText.setForeground(manageColor(ColorConstant.BLACK));
    orText.setHeight(30);
    orText.setWidth(70);
    orText.setValue("Or");
    gaService.setLocation(orText, x + 130, y + 60);
    peService.setPropertyValue(textShape, Properties.PROP_KEY_LEGENDBOX_TYPE, Properties.PROP_VAL_LEGENDBOX_TYPE_COMPLETE);
  }

}
