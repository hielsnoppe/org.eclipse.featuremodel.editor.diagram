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
