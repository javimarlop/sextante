

package es.unex.sextante.inmemory.core;

import java.util.Iterator;

import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.IteratorException;


public class InMemoryFeatureIterator
         implements
            IFeatureIterator {

   private final Iterator<FeatureImpl> m_Iterator;


   public InMemoryFeatureIterator(final Iterator<FeatureImpl> iterator) {

      m_Iterator = iterator;

   }


   @Override
   public void close() {
   }


   @Override
   public boolean hasNext() {

      return m_Iterator.hasNext();

   }


   @Override
   public IFeature next() throws IteratorException {

      return m_Iterator.next();

   }

}
