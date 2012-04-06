

package es.unex.sextante.arcgis.dataobjects;

import es.unex.sextante.dataObjects.IRecord;


public class ArcRecord
         implements
            IRecord {

   private final Object values[];


   public ArcRecord(final Object[] values) {

      this.values = values;

   }


   @Override
   public Object getValue(final int iField) {

      return values[iField];

   }


   @Override
   public Object[] getValues() {

      return values;

   }

}
