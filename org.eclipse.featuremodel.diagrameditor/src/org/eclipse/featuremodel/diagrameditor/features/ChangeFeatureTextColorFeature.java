package org.eclipse.featuremodel.diagrameditor.features;

import org.eclipse.featuremodel.Feature;
import org.eclipse.featuremodel.diagrameditor.utilities.DialogUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ChangeFeatureTextColorFeature extends AbstractCustomFeature {

  /**
   * The constructor.
   * 
   * @param fp
   */
  public ChangeFeatureTextColorFeature(IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Gets the name of this function feature.
   * 
   * @return the name
   */
  @Override
  public String getName() {
    return "Change Text Color";
  }

  /**
   * Gets the description of the function feature.
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return "Change the feature text color";
  }

  /**
   * Checks whether the color of feature text can be changed or not. This
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
    
    if (pes.length > 1) {
      return false;
    }
    
    return true;
  }

  /**
   * Set the user selected color as the background color.
   */
  @Override
  public void execute(ICustomContext context) {
    GraphicsAlgorithm ga = context.getInnerGraphicsAlgorithm();
    Color currentColor = ga.getForeground();
    Color newColor = DialogUtil.editColor(currentColor);
    if (newColor == null) {
      return;
    }
    ga.setForeground(newColor);
    getDiagramBehavior().refresh();

  }

}
