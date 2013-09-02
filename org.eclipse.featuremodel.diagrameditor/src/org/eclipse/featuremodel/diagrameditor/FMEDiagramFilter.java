package org.eclipse.featuremodel.diagrameditor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.featuremodel.Feature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;


public class FMEDiagramFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (eObject instanceof Feature) {
      return true;
    }
    return false;
  }

}
