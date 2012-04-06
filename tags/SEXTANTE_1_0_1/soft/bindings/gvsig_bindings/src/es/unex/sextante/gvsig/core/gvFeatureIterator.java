package es.unex.sextante.gvsig.core;

import java.util.List;
import java.util.NoSuchElementException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;

public class gvFeatureIterator
         implements
            IFeatureIterator {

   private int                            m_iIndex;
   private final ReadableVectorial        m_RV;
   private final List<IVectorLayerFilter> m_Filters;
   private boolean                        m_bIsNextConsumed;
   private IFeature                       m_CurrentValue;
   private boolean                        m_bFinished;
   private int                            m_iShapesCount;


   public gvFeatureIterator(final FLyrVect layer,
                            final List<IVectorLayerFilter> filters) {

      m_bIsNextConsumed = true;
      m_iIndex = 0;
      m_RV = layer.getSource();
      try {
         m_iShapesCount = m_RV.getShapeCount();
      }
      catch (final ReadDriverException e) {
         Sextante.addErrorToLog(e);
         m_iShapesCount = 0;
      }

      m_Filters = filters;

   }


   public gvFeatureIterator() {

      m_iShapesCount = 0;
      m_iIndex = 0;
      m_RV = null;
      m_Filters = null;

   }


   private boolean accept(final IFeature value,
                          final int iIndex) {

      for (final IVectorLayerFilter filter : m_Filters) {
         if (!filter.accept(value, m_iIndex)) {
            return false;
         }
      }
      return true;
   }


   public boolean moveToNextValid() {

      boolean bFound = false;
      while (!bFound && (m_iIndex < m_iShapesCount)) {
         try {
            final IGeometry shape = m_RV.getShape(m_iIndex);
            final Geometry geom = shape.toJTSGeometry();
            final Value[] gvSIGValues = m_RV.getRecordset().getRow(m_iIndex);
            final IFeature currentValue1 = new FeatureImpl(geom, DataTools.getSextanteValues(gvSIGValues));
            if (accept(currentValue1, m_iIndex)) {
               bFound = true;
               m_CurrentValue = currentValue1;
               m_bIsNextConsumed = false;
            }
            m_iIndex++;
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
            m_iIndex++;
         }
      }
      if (!bFound) {
         m_bFinished = true;
      }
      return bFound;
   }


   @Override
   public IFeature next() {

      if (!m_bIsNextConsumed) {
         m_bIsNextConsumed = true;
         return m_CurrentValue;
      }

      if (!m_bFinished) {
         if (moveToNextValid()) {
            m_bIsNextConsumed = true;
            return m_CurrentValue;
         }
      }

      throw new NoSuchElementException();
   }


   @Override
   public boolean hasNext() {

      if (m_bFinished) {
         return false;
      }

      if (!m_bIsNextConsumed) {
         return true;
      }

      return moveToNextValid();

   }


   public void close() {}


}
