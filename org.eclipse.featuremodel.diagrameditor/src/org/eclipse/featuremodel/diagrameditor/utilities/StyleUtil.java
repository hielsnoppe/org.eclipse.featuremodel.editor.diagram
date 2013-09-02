package org.eclipse.featuremodel.diagrameditor.utilities;

import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * This class provides some specific styles for different pictogram elements.
 * 
 */
public class StyleUtil {
  private static final IColorConstant CARDINALITY_TEXT_FOREGROUND = new ColorConstant(0, 0, 0);
  private static final IColorConstant FEATURE_FOREGROUND          = new ColorConstant(0, 0, 0);
  private static final IColorConstant FEATURE_BACKGROUND          = new ColorConstant(255, 255, 255);

  /**
   * Get style for common value.
   * 
   * @param diagram
   * @return
   */
  public static Style getStyleForCommonValues(Diagram diagram) {
    final String styleId = "COMMON-VALUES";
    IGaService gaService = Graphiti.getGaService();

    // Is style already persisted?
    Style style = gaService.findStyle(diagram, styleId);
    
    if (style == null) {
      style = gaService.createPlainStyle(diagram, styleId);
      setCommonValues(style);
    }
    return style;
  }

  /**
   * Get style for feature.
   * 
   * @param diagram
   * @return
   */
  public static Style getStyleForFeature(Diagram diagram) {
    final String styleId = "FEATURE";
    IGaService gaService = Graphiti.getGaService();

    // this is a child style of the common-values-style
    Style parentStyle = getStyleForCommonValues(diagram);
    Style style = gaService.findStyle(parentStyle, styleId);

    if (style == null) {
      style = gaService.createPlainStyle(parentStyle, styleId);
      style.setFilled(true);
      style.setForeground(gaService.manageColor(diagram, FEATURE_FOREGROUND));
      style.setBackground(gaService.manageColor(diagram, FEATURE_BACKGROUND));
    }
    return style;
  }

  /**
   * Get style for the cardinality text.
   * 
   * @param diagram
   * @return
   */
  public static Style getStyleForCardinalityText(Diagram diagram) {
    final String styleId = "CARDINALITY-TEXT";
    IGaService gaService = Graphiti.getGaService();

    // this is a child style of the common-values-style
    Style parentStyle = getStyleForCommonValues(diagram);
    Style style = gaService.findStyle(parentStyle, styleId);

    if (style == null) {
      style = gaService.createPlainStyle(parentStyle, styleId);
      setCommonTextValues(diagram, gaService, style);
      style.setFont(gaService.manageDefaultFont(diagram, false, true));
    }
    return style;
  }

  /**
   * Get style for text decorator.
   * 
   * @param diagram
   * @return
   */
  public static Style getStyleForTextDecorator(Diagram diagram) {
    final String styleId = "TEXT-DECORATOR-TEXT";
    IGaService gaService = Graphiti.getGaService();

    // this is a child style of the common-values-style
    Style parentStyle = getStyleForCommonValues(diagram);
    Style style = gaService.findStyle(parentStyle, styleId);

    if (style == null) {
      style = gaService.createPlainStyle(parentStyle, styleId);
      setCommonTextValues(diagram, gaService, style);
      style.setFont(gaService.manageDefaultFont(diagram, true, false));
    }
    return style;

  }

  /**
   * Set style for common text value.
   * 
   * @param diagram
   * @param gaService
   * @param style
   */
  private static void setCommonTextValues(Diagram diagram, IGaService gaService, Style style) {
    style.setFilled(false);
    style.setAngle(0);
    style.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    style.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    style.setForeground(gaService.manageColor(diagram, CARDINALITY_TEXT_FOREGROUND));
  }

  /**
   * Set style for common value.
   * 
   * @param style
   */
  private static void setCommonValues(Style style) {
    style.setLineStyle(LineStyle.SOLID);
    style.setLineVisible(true);
    style.setLineWidth(2);
    style.setTransparency(0.0);
  }
}
