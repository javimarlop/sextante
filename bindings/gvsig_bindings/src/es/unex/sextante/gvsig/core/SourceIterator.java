package es.unex.sextante.gvsig.core;

import java.util.Iterator;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class SourceIterator
         implements
            Iterator {

   private int                  index;
   private long                 count;
   private FBitSet              selection = null;
   private final SelectableDataSource source;


   public SourceIterator(final SelectableDataSource source) throws ReadDriverException {
      this.source = source;
      if (source.getSelection().cardinality() == 0) {
         this.selection = null;
         this.index = 0;
         this.count = source.getRowCount();
      }
      else {
         this.selection = source.getSelection();
         this.index = selection.nextSetBit(0);
         this.count = selection.cardinality();
      }

   }


   public void remove() {
      throw new UnsupportedOperationException();
   }


   public boolean hasNext() {
      if (this.selection == null) {
         return this.index < this.count;
      }
      return this.index >= 0;
   }


   public Object next() {
      try {
         return this.nextFeature();
      }
      catch (final ReadDriverException e) {
         throw new RuntimeException(e);
      }
   }


   public IFeature nextFeature() throws ReadDriverException {

      final Value[] values = this.source.getRow(this.index);
      final IFeature feat = new DefaultFeature(null, values, "" + this.index);
      if (this.selection == null) {
         this.index++;
      }
      else {
         this.index = this.selection.nextSetBit(this.index + 1);
      }
      return feat;

   }


   public long count() {
      return this.count;
   }

}
