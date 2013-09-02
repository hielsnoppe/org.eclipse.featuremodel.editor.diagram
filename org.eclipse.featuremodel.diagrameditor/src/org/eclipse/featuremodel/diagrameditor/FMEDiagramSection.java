package org.eclipse.featuremodel.diagrameditor;

import org.eclipse.featuremodel.Feature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class FMEDiagramSection extends GFPropertySection implements ITabbedPropertyConstants {

  private Table table;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
    super.createControls(parent, aTabbedPropertySheetPage);

    table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER_SOLID | SWT.NO_SCROLL);
    table.setLinesVisible(false);
    table.setHeaderVisible(true);
    FillLayout layout = new FillLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    table.setLayout(layout);

    String[] titles = { "Property", "Value" };

    for (int i = 0; i < titles.length; i++) {
      TableColumn column = new TableColumn(table, SWT.NONE);
      column.setText(titles[i]);
      table.getColumn(i).pack();
    }
    
    for (int i = 0; i < 30; i ++){
      new TableItem(table, SWT.NONE);
    }

    final TableEditor editor = new TableEditor(table);
    editor.horizontalAlignment = SWT.LEFT;
    editor.grabHorizontal = true;
    editor.minimumWidth = 50;
    // editing the second column
    final int EDITABLECOLUMN = 1;
    
    table.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e){
        // Clean up any previous editor control
        Control oldEditor = editor.getEditor();
        if (oldEditor != null){
          oldEditor.dispose();
        }
        // Identify the selected row
        TableItem item = (TableItem) e.item;
        if (item == null) {
          return;
        }
        // The control that will be the editor must be a child of the table
        Text newEditor = new Text (table, SWT.NONE);
        newEditor.setText(item.getText(EDITABLECOLUMN));
        newEditor.addModifyListener(new ModifyListener() {
          
          @Override
          public void modifyText(ModifyEvent e) {
            Text text = (Text) editor.getEditor();
            editor.getItem().setText(EDITABLECOLUMN, text.getText());
//            EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
//            if (bo != null) {
//              ((Feature) bo).setName(text.getText());
//            }
          }
        });
        newEditor.selectAll();
        newEditor.setFocus();
        editor.setEditor(newEditor, item, EDITABLECOLUMN);
      }
    });
  }


  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      // the filter assured, that it is a feature
      if (bo == null) {
        return;
      }
      Feature feature = (Feature) bo;
      String name = feature.getName();
      String id;
      if (feature.getParentGroup() == null) {
        id = "";
      }
      else {
        id = feature.getParentGroup().getId();
      }
      table.getItem(0).setText(new String[] { "Id", id });
      table.getItem(1).setText(new String[] { "Name", name });
    }
  }
}
