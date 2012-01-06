

package es.unex.sextante.arcgis.gptool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.esri.arcgis.geodatabase.IEnumGPName;
import com.esri.arcgis.geodatabase.IGPName;
import com.esri.arcgis.geoprocessing.EnumGPEnvironment;
import com.esri.arcgis.geoprocessing.EnumGPName;
import com.esri.arcgis.geoprocessing.GPBoolean;
import com.esri.arcgis.geoprocessing.GPBooleanType;
import com.esri.arcgis.geoprocessing.GPEnvironment;
import com.esri.arcgis.geoprocessing.GPFunctionName;
import com.esri.arcgis.geoprocessing.IEnumGPEnvironment;
import com.esri.arcgis.geoprocessing.IGPFunction;
import com.esri.arcgis.geoprocessing.IGPFunctionFactory;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.interop.extn.ArcGISCategories;
import com.esri.arcgis.interop.extn.ArcGISExtension;
import com.esri.arcgis.system.IArray;
import com.esri.arcgis.system.IUID;
import com.esri.arcgis.system.UID;
import com.esri.arcgis.system.esriProductCode;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.grass.GrassAlgorithm;
import es.unex.sextante.gui.r.RAlgorithm;
import es.unex.sextante.gui.saga.SagaAlgorithm;


@ArcGISExtension(categories = { ArcGISCategories.GPFunctionFactories })
public class SextanteFunctionFactory
         implements
            IGPFunctionFactory {

   static {
      SextanteInitializer.initialize();
   }

   private final String functionFactoryAlias = "sextantealgorithms";
   private final String factoryName          = "SEXTANTE algorithms";


   public IGPFunction getFunction(final String name) throws IOException, AutomationException {

      final HashMap<String, GeoAlgorithm> algs = Sextante.getAlgorithms().get("SEXTANTE");
      final HashMap<String, GeoAlgorithm> algsSaga = Sextante.getAlgorithms().get("SAGA");
      if (algsSaga != null) {
         algs.putAll(algsSaga);
      }
      //final HashMap<String, GeoAlgorithm> algsR = Sextante.getAlgorithms().get("R");
      //algs.putAll(algsR);
      //GRASS algorithms are not available at the moment... Still have to fix a few things
      //final HashMap<String, GeoAlgorithm> algsGrass = Sextante.getAlgorithms().get("GRASS");
      //algs.putAll(algsGrass);

      final GeoAlgorithm alg = algs.get(name);
      if (alg != null) {
         final SextanteFunction function = new SextanteFunction();
         function.setAlgorithm(alg);
         if (function.initialize()) {
            return function;
         }
         return null;
      }
      return null;

   }


   public IGPName getFunctionName(final String name) throws IOException, AutomationException {

      final HashMap<String, GeoAlgorithm> algs = Sextante.getAlgorithms().get("SEXTANTE");
      final HashMap<String, GeoAlgorithm> algsSaga = Sextante.getAlgorithms().get("SAGA");
      if (algsSaga != null) {
         algs.putAll(algsSaga);
      }
      //      final HashMap<String, GeoAlgorithm> algsR = Sextante.getAlgorithms().get("R");
      //      algs.putAll(algsR);
      //final HashMap<String, GeoAlgorithm> algsGrass = Sextante.getAlgorithms().get("GRASS");
      //algs.putAll(algsGrass);

      final GeoAlgorithm alg = algs.get(name);
      if (alg != null) {
         return getFunctionName(alg);
      }
      return null;

   }


   public IGPName getFunctionName(final GeoAlgorithm alg) throws IOException, AutomationException {

      final GPFunctionName functionName = new GPFunctionName();
      String sGroup = alg.getGroup();
      if (alg instanceof SagaAlgorithm) {
         sGroup = "SAGA - " + alg.getGroup();
      }
      else if (alg instanceof GrassAlgorithm) {
         sGroup = "GRASS - " + alg.getGroup();
      }
      else if (alg instanceof RAlgorithm) {
         sGroup = "R - " + alg.getGroup();
      }
      functionName.setCategory(sGroup);
      functionName.setDescription(alg.getName());
      functionName.setDisplayName(alg.getName());
      functionName.setName(alg.getCommandLineName());
      functionName.setMinimumProduct(esriProductCode.esriProductCodeReader);
      functionName.setFactoryByRef(this);
      return functionName;

   }


   public IEnumGPName getFunctionNames() throws IOException, AutomationException {

      final EnumGPName nameArray = new EnumGPName();
      final HashMap<String, GeoAlgorithm> algs = Sextante.getAlgorithms().get("SEXTANTE");
      final HashMap<String, GeoAlgorithm> algsSaga = Sextante.getAlgorithms().get("SAGA");
      if (algsSaga != null) {
         algs.putAll(algsSaga);
      }
      //      final HashMap<String, GeoAlgorithm> algsR = Sextante.getAlgorithms().get("R");
      //      algs.putAll(algsR);
      final Set<String> set = algs.keySet();
      final Iterator<String> iter = set.iterator();
      while (iter.hasNext()) {
         final String sName = iter.next();
         final GeoAlgorithm alg = Sextante.getAlgorithmFromCommandLineName(sName);
         nameArray.add(getFunctionName(alg));
      }

      return nameArray;

   }


   public String getAlias() throws IOException, AutomationException {
      return functionFactoryAlias;
   }


   public IUID getCLSID() throws IOException, AutomationException {
      final UID uid = new UID();
      uid.setValue("{" + UUID.nameUUIDFromBytes(this.getClass().getName().getBytes()) + "}");

      return uid;
   }


   public IEnumGPEnvironment getFunctionEnvironments() throws IOException, AutomationException {

      final IArray array = new EnumGPEnvironment();

      final GPEnvironment env = new GPEnvironment();
      env.setCategory("SEXTANTE");
      env.setName(SextanteGlobalParams.USE_ONLY_SELECTED);
      env.setDisplayName("Use only selected features");
      final GPBoolean bool = new GPBoolean();
      bool.setValue(true);
      env.setDataTypeByRef(new GPBooleanType());
      env.setValueByRef(bool);
      array.add(env);

      return (IEnumGPEnvironment) array;

   }


   public String getName() throws IOException, AutomationException {
      return factoryName;
   }

}
