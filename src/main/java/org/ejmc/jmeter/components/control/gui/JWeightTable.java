package org.ejmc.jmeter.components.control.gui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JWeightTable extends JTable {

   private static final long serialVersionUID = -2461156297092385708L;


   public JWeightTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
      super(dm, cm, sm);
   }

   public JWeightTable(TableModel dm, TableColumnModel cm) {
      super(dm, cm);
   }

   public JWeightTable(TableModel dm) {
      super(dm);
   }

   public boolean isCellEditable(int row, int column) {
      return column == 0;
   }
}
