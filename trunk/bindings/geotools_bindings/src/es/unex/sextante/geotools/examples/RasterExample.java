package es.unex.sextante.geotools.examples;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.geotools.GTOutputFactory;
import es.unex.sextante.geotools.GTRasterLayer;
import es.unex.sextante.hydrology.accFlow.AccFlowAlgorithm;
import es.unex.sextante.hydrology.topographicIndices.TopographicIndicesAlgorithm;
import es.unex.sextante.morphometry.slope.SlopeAlgorithm;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

public class RasterExample {

	/*
	 * The output factory to use when calling geoalgorithms
	 * This tells the algorithm how to create new data objects (layers
	 * and tables)
	 * The GTOutputFactory creates objects based on geotools
	 * data objects (DataStore and GridCoverage)
	 */
	private static OutputFactory m_OutputFactory = new GTOutputFactory();

	public static void main(String[] args) {

		initialize();

		try {
			doProcessing();
			System.exit(0);
		} catch(GeoAlgorithmExecutionException e){
			System.out.println("Error executing algorithm");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error opening raster layer");
			System.exit(1);
		}

	}

	private static void initialize() {

		/*
		 * Initialize the library.
		 * This will load all the algorithms and resource strings.
		 * Since no language code is passed, default language(en)
		 * will be used
		 */
		Sextante.initialize();

	}

	private static void doProcessing()
					throws
						GeoAlgorithmExecutionException,
						IOException{

		/*
		 * First we need some data.
		 * Let's open a DEM
		 */
		GridCoverage2D gc = openRasterLayer("/home/my_user_name/dem.asc");

		/*
		 * To use this data we need to wrap it with an object
		 * that implements the IRasterLayer, so SEXTANTE algorithms
		 * can access it.
		 * Since it is a Geotools object, we will use the Geotools
		 * wrapper class GTRasterLayer
		 */
		GTRasterLayer dem = new GTRasterLayer();
		dem.create(gc);

		/*
		 * And now, let's do some processing with that layer.
		 * We will calculate slope and flow accumulation,
		 * and then use both to calculate the so-called
		 * Topographic Wetness Index (Beven & Kirby, 1978),
		 * the Universal Soil Loss Equation LS Factor,
		 * and the Stream Power Index
		 *
		 */
		IRasterLayer slope = getSlope(dem);
		IRasterLayer flowacc = getFlowAcc(dem);

		TopographicIndicesAlgorithm alg = new TopographicIndicesAlgorithm();
		ParametersSet params = alg.getParameters();
		params.getParameter(TopographicIndicesAlgorithm.SLOPE).setParameterValue(slope);
		params.getParameter(TopographicIndicesAlgorithm.ACCFLOW).setParameterValue(flowacc);

		OutputObjectsSet outputs = alg.getOutputObjects();

		Output twi = outputs.getOutput(TopographicIndicesAlgorithm.TWI);
		twi.setOutputChannel(new FileOutputChannel("/home/my_user_name/TWI.tif"));

		Output ls = outputs.getOutput(TopographicIndicesAlgorithm.LS);
		ls.setOutputChannel(new FileOutputChannel("/home/my_user_name/TWI.tif"));

		Output spi = outputs.getOutput(TopographicIndicesAlgorithm.SPI);
		spi.setOutputChannel(new FileOutputChannel("/home/my_user_name/TWI.tif"));

		alg.execute(null, m_OutputFactory);

		/*
		 * Let's check that the results are OK.
		 * We will print the average value of each layer
		 */

		IRasterLayer twiLayer = (IRasterLayer) twi.getOutputObject();
		System.out.println("TWI mean value: " +
				Double.toString(twiLayer.getMeanValue()));

		IRasterLayer lsLayer = (IRasterLayer) ls.getOutputObject();
		System.out.println("LS mean value: " +
				Double.toString(lsLayer.getMeanValue()));

		IRasterLayer spiLayer = (IRasterLayer) spi.getOutputObject();
		System.out.println("SPI mean value: " +
				Double.toString(spiLayer.getMeanValue()));


	}

	private static GridCoverage2D openRasterLayer(String sFilename) throws IOException {

		URL url = new File(sFilename).toURL();
		ArcGridReader agr = new ArcGridReader(url);
        GridCoverage2D coverage = (GridCoverage2D) agr.read(null);

		return coverage;

	}


	/**
	 * Returns a slope layer created from the passed DEM
	 * @param dem the DEM
	 * @return a slope layer
	 * @throws GeoAlgorithmExecutionException
	 */
	private static IRasterLayer getSlope(IRasterLayer dem)
							throws GeoAlgorithmExecutionException{

		/*
		 * Instantiate the SlopeAlgorithm class
		 */
		SlopeAlgorithm alg = new SlopeAlgorithm();

		/*
		 * The first thing we have to do is to set up the input parameters
		 */
		ParametersSet params = alg.getParameters();
		params.getParameter(SlopeAlgorithm.DEM).setParameterValue(dem);

		//Zevenberger & Thorne method
		params.getParameter(SlopeAlgorithm.METHOD).setParameterValue(SlopeAlgorithm.METHOD_ZEVENBERGEN);

		//Resulting values in radians
		params.getParameter(SlopeAlgorithm.UNITS).setParameterValue(SlopeAlgorithm.UNITS_RADIANS);

		/*
		 *  This algorithm will generate a new raster layer.
		 * We can select "where" to put the result. To do this, we
		 * retrieve the output container and set the output channel,
		 * which contains information about the destiny of the resulting
		 * data. The most common way of using this is setting
		 * a FileOutputChannel, which contains the information needed to
		 * put the output to a file (basically a filename).
		 * If we omit this, a FileOutputChannel will be used,
		 * using a temporary filename.
		 */
		OutputObjectsSet outputs = alg.getOutputObjects();
		Output out = outputs.getOutput(SlopeAlgorithm.SLOPE);
		out.setOutputChannel(new FileOutputChannel("/home/my_user_name/slope.tif"));

		/*
		 * Execute the algorithm. We use no task monitor,
		 * so we will not be able to monitor the progress
		 * of the execution. SEXTANTE also provides a DefaultTaskMonitor,
		 * which shows a simple progress bar, or you could make your
		 * own one, implementing the ITaskMonitor interface
		 *
		 * The execute method returns true if everything went OK, or false if it
		 * was canceled. Since we are not giving the user the chance to
		 * cancel it (there is no task monitor), we do not care about the
		 * return value.
		 *
		 * If something goes wrong, it will throw an exception.
		 */
		alg.execute(null, m_OutputFactory);

		/*
		 * Now the result can be taken from the output container
		 */
		IRasterLayer slope = (IRasterLayer) out.getOutputObject();

		return slope;

	}

	/**
	 * Returns a flow accumulation layer created from the passed DEM
	 * @param dem the DEM
	 * @return a flow accumulation layer
	 * @throws GeoAlgorithmExecutionException
	 */
	private static IRasterLayer getFlowAcc(GTRasterLayer dem)
			throws GeoAlgorithmExecutionException{

		AccFlowAlgorithm alg = new AccFlowAlgorithm();

		ParametersSet params = alg.getParameters();
		params.getParameter(AccFlowAlgorithm.DEM).setParameterValue(dem);

		//Multiple Flow Direction method
		params.getParameter(AccFlowAlgorithm.METHOD).setParameterValue(AccFlowAlgorithm.MFD);

		OutputObjectsSet outputs = alg.getOutputObjects();
		Output out = outputs.getOutput(AccFlowAlgorithm.FLOWACC);
		out.setOutputChannel(new FileOutputChannel("/home/my_user_name/flowacc.tif"));

		alg.execute(null, m_OutputFactory);

		IRasterLayer flowacc = (IRasterLayer) out.getOutputObject();

		return flowacc;


	}

}
