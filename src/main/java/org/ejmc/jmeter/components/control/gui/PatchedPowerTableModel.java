package org.ejmc.jmeter.components.control.gui;

import java.util.List;
import javax.swing.event.TableModelEvent;
import org.apache.jmeter.gui.util.PowerTableModel;

public class PatchedPowerTableModel extends PowerTableModel {

   private static final long serialVersionUID = 12629504217923821L;


   public PatchedPowerTableModel() {}

   public PatchedPowerTableModel(String[] headers, Class[] classes) {
      super(headers, classes);
   }

   public void setColumnData(int col, List data) {
      super.setColumnData(col, data);
      this.fireTableChanged(new TableModelEvent(this));
   }

   public void setValueAt(Object value, int row, int column) {
      super.setValueAt(value, row, column);
      this.fireTableCellUpdated(row, column);
   }

   public void setRowValues(int row, Object[] values) {
      super.setRowValues(row, values);
      this.fireTableRowsUpdated(row, row);
   }
}
