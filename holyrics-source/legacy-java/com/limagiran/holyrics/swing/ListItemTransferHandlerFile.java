package com.limagiran.holyrics.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

class ListItemTransferHandlerFile extends TransferHandler {
   private final DataFlavor localObjectFlavor;
   private File[] transferedObjects = null;
   private int[] indices = null;
   private int addIndex = -1;
   private int addCount = 0;

   public ListItemTransferHandlerFile() {
      this.localObjectFlavor = new ActivationDataFlavor(File[].class, "application/x-java-jvm-local-objectref", "Array/File");
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      JList list = (JList)c;
      this.indices = list.getSelectedIndices();
      List<File> array = list.getSelectedValuesList();
      this.transferedObjects = new File[array.size()];

      for (int i = 0; i < this.transferedObjects.length; i++) {
         this.transferedObjects[i] = array.get(i);
      }

      return new DataHandler(this.transferedObjects, this.localObjectFlavor.getMimeType());
   }

   @Override
   public boolean canImport(TransferSupport info) {
      return info.isDrop() && info.isDataFlavorSupported(this.localObjectFlavor);
   }

   @Override
   public int getSourceActions(JComponent c) {
      return 2;
   }

   @Override
   public boolean importData(TransferSupport info) {
      if (!this.canImport(info)) {
         return false;
      } else {
         try {
            JList target = (JList)info.getComponent();
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            DefaultListModel listModel = (DefaultListModel)target.getModel();
            int index = dl.getIndex();
            int max = listModel.getSize();
            if (index < 0 || index > max) {
               index = max;
            }

            this.addIndex = index;
            Object[] values = (Object[])info.getTransferable().getTransferData(this.localObjectFlavor);
            this.addCount = values.length;

            for (Object value : values) {
               int idx = index++;
               listModel.add(idx, value);
               target.addSelectionInterval(idx, idx);
            }

            return true;
         } catch (Exception var13) {
            return false;
         }
      }
   }

   @Override
   protected void exportDone(JComponent c, Transferable data, int action) {
      this.cleanup(c, action == 2);
   }

   private void cleanup(JComponent c, boolean remove) {
      try {
         if (remove && this.indices != null) {
            JList source = (JList)c;
            DefaultListModel model = (DefaultListModel)source.getModel();
            if (this.addCount > 0) {
               for (int i = 0; i < this.indices.length; i++) {
                  if (this.indices[i] >= this.addIndex) {
                     this.indices[i] = this.indices[i] + this.addCount;
                  }
               }
            }

            for (int ix = this.indices.length - 1; ix >= 0; ix--) {
               int rem = this.indices[ix];

               while (rem >= 0 && !this.remove(model, rem--)) {
               }
            }
         }

         this.indices = null;
         this.addCount = 0;
         this.addIndex = -1;
      } catch (Exception var7) {
      }
   }

   private boolean remove(DefaultListModel model, int rem) {
      try {
         model.remove(rem);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }
}
