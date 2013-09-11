package org.ejmc.jmeter.components.control.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.ejmc.jmeter.components.control.WeightedRandomController;
import org.ejmc.jmeter.components.control.gui.JWeightTable;
import org.ejmc.jmeter.components.control.gui.PatchedPowerTableModel;

public class WeightedRandomControllerGui extends AbstractControllerGui {

   static final int C_WEIGHT = 0;
   static final int C_PERCENT = 2;
   static final int C_NAME = 1;
   private static final long serialVersionUID = -5116882582355283790L;
   private JTable table;
   private PowerTableModel tableModel;
   private boolean recalculating;
   private boolean invalid;


   public WeightedRandomControllerGui() {
      this.init();
   }

   public void clearGui() {
      super.clearGui();
      this.tableModel.clearData();
   }

   public void configure(TestElement el) {
      super.configure(el);
      this.rebuildModel(el);
   }

   private JScrollPane createConfigPanel() {
      String[] headers = new String[]{"Weight", "Controller", "Percentage"};
      Class[] classes = new Class[]{Integer.class, String.class, Float.class};
      this.tableModel = new PatchedPowerTableModel(headers, classes);
      this.table = new JWeightTable(this.tableModel);
      this.table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
      this.table.setSelectionMode(0);
      this.tableModel.addTableModelListener(new WeightedRandomControllerGui.RepaintListener());
      return this.makeScrollPane(this.table);
   }

   public TestElement createTestElement() {
      WeightedRandomController ic = new WeightedRandomController();
      super.configureTestElement(ic);
      return ic;
   }

   public String getLabelResource() {
      return this.getClass().getName();
   }

   public String getStaticLabel() {
      return "Weighted Random Control";
   }

   private void init() {
      this.setLayout(new BorderLayout(0, 10));
      this.setBorder(this.makeBorder());
      this.add(this.makeTitlePanel(), "North");
      this.add(this.createConfigPanel(), "Center");
   }

   public void modifyTestElement(TestElement ic) {
      if(!this.invalid) {
         int length = this.tableModel.getRowCount();
         int[] w = new int[length];

         for(int old = 0; old < length; ++old) {
            Integer wI = (Integer)this.tableModel.getRowData(old)[0];
            w[old] = wI.intValue();
         }

         int[] var6 = ((WeightedRandomController)ic).getWeights();
         if(!Arrays.equals(var6, w)) {
            super.configureTestElement(ic);
            ((WeightedRandomController)ic).setWeights(w);
         }

      }
   }

   private List getSubControllers(TestElement ic) {
      JMeterTreeNode node;
      if(ic == null) {
         node = GuiPackage.getInstance().getCurrentNode();
      } else {
         node = GuiPackage.getInstance().getNodeOf(ic);
      }

      if(node == null) {
         return null;
      } else {
         ArrayList subControllers = new ArrayList();

         for(int i = 0; i < node.getChildCount(); ++i) {
            JMeterTreeNode n = (JMeterTreeNode)node.getChildAt(i);
            TestElement el = n.getTestElement();
            if(el != null && (el instanceof Sampler || el instanceof Controller)) {
               subControllers.add(n);
            }
         }

         return subControllers;
      }
   }

   public void realculatePercentAndValue(TestElement ic) {
      try {
         this.recalculating = true;
         float total = 0.0F;

         for(int testElements = 0; testElements < this.tableModel.getRowCount(); ++testElements) {
            Object[] i = this.tableModel.getRowData(testElements);
            total += ((Integer)i[0]).floatValue();
         }

         List var9 = this.getSubControllers(ic);

         for(int var10 = 0; var10 < this.tableModel.getRowCount(); ++var10) {
            Object[] e = this.tableModel.getRowData(var10);
            e[2] = Float.valueOf(((Integer)e[0]).floatValue() / total * 100.0F);
            e[1] = ((JMeterTreeNode)var9.get(var10)).getName();
            this.tableModel.setRowValues(var10, e);
         }

      } finally {
         this.recalculating = false;
      }
   }

   private void rebuildModel(TestElement el) {
      this.tableModel.clearData();
      List testElements = this.getSubControllers(el);
      if(testElements == null) {
         this.invalid = true;
      } else {
         this.invalid = false;
         int size = testElements.size();
         int[] weights = ((WeightedRandomController)el).getWeights();
         int lim = Math.min(size, weights.length);

         int i;
         Object[] values;
         for(i = 0; i < lim; ++i) {
            values = new Object[]{Integer.valueOf(weights[i]), "", Float.valueOf(0.0F)};
            this.tableModel.addRow(values);
         }

         if(weights.length < size) {
            for(i = weights.length; i < size; ++i) {
               values = new Object[]{Integer.valueOf(1), "", Float.valueOf(0.0F)};
               this.tableModel.addRow(values);
            }
         }

         this.tableModel.fireTableDataChanged();
      }
   }

   private class RepaintListener implements TableModelListener {

      private RepaintListener() {}

      public void tableChanged(TableModelEvent e) {
         if(!WeightedRandomControllerGui.this.recalculating) {
            WeightedRandomControllerGui.this.realculatePercentAndValue((TestElement)null);
         }

      }
   }
}
