package com.massisframework.massis.sim;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.gui.DrawableTabbedFrame;
import com.massisframework.massis.displays.HomeDisplay;
import com.massisframework.massis.displays.buildingmap.BuildingMap;
import com.massisframework.massis.displays.displays3d.HomeDisplay3D;
import com.massisframework.massis.displays.floormap.layers.ConnectionsLayer;
import com.massisframework.massis.displays.floormap.layers.CrowdDensityLayer;
import com.massisframework.massis.displays.floormap.layers.DoorLayer;
import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
import com.massisframework.massis.displays.floormap.layers.PathFinderLayer;
import com.massisframework.massis.displays.floormap.layers.PathLayer;
import com.massisframework.massis.displays.floormap.layers.PeopleIDLayer;
import com.massisframework.massis.displays.floormap.layers.PeopleLayer;
import com.massisframework.massis.displays.floormap.layers.QTLayer;
import com.massisframework.massis.displays.floormap.layers.RadioLayer;
import com.massisframework.massis.displays.floormap.layers.RoomsLabelLayer;
import com.massisframework.massis.displays.floormap.layers.RoomsLayer;
import com.massisframework.massis.displays.floormap.layers.VisibleAgentsLines;
import com.massisframework.massis.displays.floormap.layers.VisionRadioLayer;
import com.massisframework.massis.displays.floormap.layers.WallLayer;
import com.massisframework.massis.model.building.Floor;

import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;

public class SimulationWithUI extends GUIState {

	static {
		System.setProperty("j3d.implicitAntialiasing", "true");
		System.setProperty("j3d.optimizeForSpace", "false");

	}

	private DrawableLayer<DrawableFloor>[] layers;

	public SimulationWithUI(SimState state) {
		super(state);
	}

	public SimulationWithUI(SimState state, DrawableLayer<DrawableFloor>... layers) {
		super(state);
		this.layers = layers;
	}

	@Override
	public void init(Controller controller) {
		super.init(controller);

	}

	public static Object getInfo() {
		try {
			return "<html>\r\n" + "<head></head>\r\n" + "<body>\r\n" + "<p style=\"text-align:center;\">\r\n"
					+ "<strong>MASSIS</strong><br /><em> MultiAgent Simulation System for Indoor Scenarios </em>\r\n"
					+ "\r\n" + "<pre style=\"font: 6px monospace;\"><br />\r\n" + "\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMASSISMASSISMASSI</span><span style=\"color: #fefefe;\">S</span><span style=\"color: #f0f1f2;\">M</span><span style=\"color: #c4c7cd;\">A</span><span style=\"color: #9197a2;\">S</span><span style=\"color: #868d9a;\">SISM</span><span style=\"color: #89909c;\">A</span><span style=\"color: #a0a6b0;\">S</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: #fdfdfd;\">I</span><span style=\"color: white;\">SMASSISMASSISMA</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSISMASSISMASSI</span><span style=\"color: #fdfdfd;\">S</span><span style=\"color: #dfe1e4;\">M</span><span style=\"color: #adb2ba;\">A</span><span style=\"color: #858d99;\">S</span><span style=\"color: #7c8491;\">SISM</span><span style=\"color: #838b97;\">A</span><span style=\"color: #a8aeb6;\">S</span><span style=\"color: #dbdee1;\">S</span><span style=\"color: #fcfcfc;\">I</span><span style=\"color: white;\">SMASSISMASSISMASS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMASSISMASSI</span><span style=\"color: #fbfcfc;\">S</span><span style=\"color: #cfd2d7;\">M</span><span style=\"color: #9aa0aa;\">A</span><span style=\"color: #808895;\">S</span><span style=\"color: #7c8491;\">SISM</span><span style=\"color: #878f9a;\">A</span><span style=\"color: #bbbfc6;\">S</span><span style=\"color: #ebedef;\">S</span><span style=\"color: #fdfefe;\">I</span><span style=\"color: white;\">SMASSISMASSISMASSIS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMASSISMASS</span><span style=\"color: #fefefe;\">I</span><span style=\"color: #ecedef;\">S</span><span style=\"color: #bbbfc6;\">M</span><span style=\"color: #878f9a;\">A</span><span style=\"color: #7c8491;\">SSIS</span><span style=\"color: #808895;\">M</span><span style=\"color: #9aa0aa;\">A</span><span style=\"color: #cfd2d7;\">S</span><span style=\"color: #fbfcfc;\">S</span><span style=\"color: white;\">ISMASSISMASSISMASSISMA</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSISMASS</span><span style=\"color: #fdfdfd;\">I</span><span style=\"color: #dee0e3;\">S</span><span style=\"color: #acb1b9;\">M</span><span style=\"color: #838b97;\">A</span><span style=\"color: #7c8491;\">SSIS</span><span style=\"color: #858d99;\">M</span><span style=\"color: #adb2ba;\">A</span><span style=\"color: #dfe1e4;\">S</span><span style=\"color: #fdfdfd;\">S</span><span style=\"color: white;\">ISMASSISMASSISMASSISMASS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMASS</span><span style=\"color: #fbfbfc;\">I</span><span style=\"color: #ced1d5;\">S</span><span style=\"color: #999fa9;\">M</span><span style=\"color: #808894;\">A</span><span style=\"color: #7c8491;\">SSIS</span><span style=\"color: #8b929d;\">M</span><span style=\"color: #c0c4ca;\">A</span><span style=\"color: #eeeff1;\">S</span><span style=\"color: #fefefe;\">S</span><span style=\"color: white;\">ISMASSISMASSISMASSISMASSIS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMAS</span><span style=\"color: #fdfdfe;\">S</span><span style=\"color: #ebecee;\">I</span><span style=\"color: #babec5;\">S</span><span style=\"color: #868e9a;\">M</span><span style=\"color: #7c8491;\">ASSI</span><span style=\"color: #818996;\">S</span><span style=\"color: #9ea4ae;\">M</span><span style=\"color: #d4d7db;\">A</span><span style=\"color: #fdfdfd;\">S</span><span style=\"color: white;\">SIS</span><span style=\"color: #fdfefe;\">MASSISMASSISMASSISMASSISM</span><span style=\"color: white;\">A</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMAS</span><span style=\"color: #fbfcfc;\">S</span><span style=\"color: #dadce0;\">I</span><span style=\"color: #a7adb5;\">S</span><span style=\"color: #828a96;\">M</span><span style=\"color: #7c8491;\">ASSIS</span><span style=\"color: #a6abb4;\">M</span><span style=\"color: #e3e5e7;\">A</span><span style=\"color: #fefefe;\">S</span><span style=\"color: white;\">SISM</span><span style=\"color: #ebf3f6;\">A</span><span style=\"color: #448eae;\">SSISMASSISMASSISMASSISMA</span><span style=\"color: #4790b0;\">S</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMAS</span><span style=\"color: #f9f9fa;\">S</span><span style=\"color: #c9ccd2;\">I</span><span style=\"color: #959ca6;\">S</span><span style=\"color: #7f8793;\">M</span><span style=\"color: #7c8491;\">ASSISMA</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: white;\">SISMAS</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">ISMASSISMASSISMASSISMASS</span><span style=\"color: #046793;\">I</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MA</span><span style=\"color: #fdfdfd;\">S</span><span style=\"color: #e7e9eb;\">S</span><span style=\"color: #b5b9c1;\">I</span><span style=\"color: #848b97;\">S</span><span style=\"color: #7c8491;\">MASSISMASS</span><span style=\"color: #d4d7db;\">I</span><span style=\"color: white;\">SMASSI</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">MASSISMASS</span><span style=\"color: #20799f;\">I</span><span style=\"color: #3d8aab;\">S</span><span style=\"color: #3e8aac;\">MASSISMASSIS</span><span style=\"color: #418cad;\">M</span><span style=\"color: white;\">A</span></strong>\r\n"
					+ "<strong><span style=\"color: #fcfcfc;\">S</span><span style=\"color: #d6d9dd;\">S</span><span style=\"color: #a3a9b2;\">I</span><span style=\"color: #818895;\">S</span><span style=\"color: #7c8491;\">MASS</span><span style=\"color: #8b929d;\">I</span><span style=\"color: #b6bbc2;\">S</span><span style=\"color: #9ba1ab;\">M</span><span style=\"color: #7c8491;\">ASSIS</span><span style=\"color: #d4d7db;\">M</span><span style=\"color: white;\">ASSISM</span><span style=\"color: #e4eff3;\">A</span><span style=\"color: #006591;\">SSISMASSIS</span><span style=\"color: #15729a;\">M</span><span style=\"color: #65a2bd;\">A</span><span style=\"color: #c6dde6;\">S</span><span style=\"color: #fcfdfe;\">S</span><span style=\"color: white;\">ISMASSISMASS</span></strong>\r\n"
					+ "<strong><span style=\"color: #fdfdfe;\">I</span><span style=\"color: #cfd1d6;\">S</span><span style=\"color: #959ca6;\">M</span><span style=\"color: #7c8491;\">ASS</span><span style=\"color: #9399a4;\">I</span><span style=\"color: #cacdd3;\">S</span><span style=\"color: #f3f4f5;\">M</span><span style=\"color: white;\">A</span><span style=\"color: #a8adb6;\">S</span><span style=\"color: #7c8491;\">SISMA</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: white;\">SISMAS</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">ISMAS</span><span style=\"color: #19749c;\">S</span><span style=\"color: #3f8cac;\">I</span><span style=\"color: #006591;\">SMASS</span><span style=\"color: #0a6b95;\">I</span><span style=\"color: #408bad;\">S</span><span style=\"color: #a9cbda;\">M</span><span style=\"color: #fafcfd;\">A</span><span style=\"color: white;\">SSISMASSIS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MAS</span><span style=\"color: #e1e3e6;\">S</span><span style=\"color: #b6bac2;\">I</span><span style=\"color: #dee1e4;\">S</span><span style=\"color: white;\">MASS</span><span style=\"color: #a8adb6;\">I</span><span style=\"color: #7c8491;\">SMASS</span><span style=\"color: #d4d7db;\">I</span><span style=\"color: white;\">SMASSI</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">MASSI</span><span style=\"color: #3484a7;\">S</span><span style=\"color: white;\">M</span><span style=\"color: #d2e4ec;\">A</span><span style=\"color: #66a3bd;\">S</span><span style=\"color: #17739b;\">S</span><span style=\"color: #006591;\">ISMAS</span><span style=\"color: #1b759d;\">S</span><span style=\"color: #81b3c9;\">I</span><span style=\"color: #ddebf0;\">S</span><span style=\"color: #fdfefe;\">M</span><span style=\"color: white;\">ASSISMA</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSIS</span><span style=\"color: #a8adb6;\">M</span><span style=\"color: #7c8491;\">ASSIS</span><span style=\"color: #d4d7db;\">M</span><span style=\"color: white;\">ASSISM</span><span style=\"color: #e4eff3;\">A</span><span style=\"color: #006591;\">SSISM</span><span style=\"color: #3484a7;\">A</span><span style=\"color: white;\">SSI</span><span style=\"color: #e3eef3;\">S</span><span style=\"color: #8ebace;\">M</span><span style=\"color: #3082a5;\">A</span><span style=\"color: #016691;\">S</span><span style=\"color: #006591;\">SISM</span><span style=\"color: #116f98;\">A</span><span style=\"color: #5c9db9;\">S</span><span style=\"color: #bed8e3;\">S</span><span style=\"color: #fafcfd;\">I</span><span style=\"color: white;\">SMASS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMA</span><span style=\"color: #a8adb6;\">S</span><span style=\"color: #7c8491;\">SISMA</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: white;\">SISMAS</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">ISMAS</span><span style=\"color: #3484a7;\">S</span><span style=\"color: white;\">ISMAS</span><span style=\"color: #f2f7f9;\">S</span><span style=\"color: #b4d2df;\">I</span><span style=\"color: #4a91b0;\">S</span><span style=\"color: #026692;\">M</span><span style=\"color: #006591;\">ASSI</span><span style=\"color: #086a94;\">S</span><span style=\"color: #3887a9;\">M</span><span style=\"color: #9fc5d5;\">A</span><span style=\"color: #f6fafc;\">S</span><span style=\"color: white;\">SIS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMASS</span><span style=\"color: #a8adb6;\">I</span><span style=\"color: #7c8491;\">SMASS</span><span style=\"color: #d4d7db;\">I</span><span style=\"color: white;\">SMASSI</span><span style=\"color: #e4eff3;\">S</span><span style=\"color: #006591;\">MASSI</span><span style=\"color: #3484a7;\">S</span><span style=\"color: white;\">MASSISMA</span><span style=\"color: #dbe9ef;\">S</span><span style=\"color: #71a9c1;\">S</span><span style=\"color: #1c759d;\">I</span><span style=\"color: #016691;\">S</span><span style=\"color: #006591;\">MASS</span><span style=\"color: #14719a;\">I</span><span style=\"color: #78adc4;\">S</span><span style=\"color: #d7e7ee;\">M</span><span style=\"color: white;\">A</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSIS</span><span style=\"color: #a8adb6;\">M</span><span style=\"color: #7c8491;\">ASSIS</span><span style=\"color: #d4d7db;\">M</span><span style=\"color: white;\">ASSISM</span><span style=\"color: #e4eff3;\">A</span><span style=\"color: #006591;\">SSISM</span><span style=\"color: #3484a7;\">A</span><span style=\"color: white;\">SSISMASSIS</span><span style=\"color: #e8f1f5;\">M</span><span style=\"color: #97c0d2;\">A</span><span style=\"color: #3786a9;\">S</span><span style=\"color: #026692;\">S</span><span style=\"color: #006591;\">ISMA</span><span style=\"color: #0f6e98;\">S</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMA</span><span style=\"color: #a8adb6;\">S</span><span style=\"color: #7c8491;\">SISMA</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: white;\">SISMAS</span><span style=\"color: #e6f0f4;\">S</span><span style=\"color: #16739b;\">ISMAS</span><span style=\"color: #468faf;\">S</span><span style=\"color: white;\">ISMASSISMASS</span><span style=\"color: #f4f9fa;\">I</span><span style=\"color: #bdd7e2;\">S</span><span style=\"color: #5497b5;\">M</span><span style=\"color: #046893;\">A</span><span style=\"color: #006591;\">SS</span><span style=\"color: #046793;\">I</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMASS</span><span style=\"color: #a8adb6;\">I</span><span style=\"color: #7c8491;\">SMASS</span><span style=\"color: #d4d7db;\">I</span><span style=\"color: white;\">SMASSISMASSISMASSISMASSISMAS</span><span style=\"color: #e2edf3;\">S</span><span style=\"color: #7bafc6;\">I</span><span style=\"color: #20799f;\">S</span><span style=\"color: #066894;\">M</span><span style=\"color: white;\">A</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSIS</span><span style=\"color: #a8adb6;\">M</span><span style=\"color: #7c8491;\">ASSIS</span><span style=\"color: #d4d7db;\">M</span><span style=\"color: white;\">ASSISMASSISMASSISMASSISMASSISM</span><span style=\"color: #edf4f7;\">A</span><span style=\"color: #a2c7d7;\">S</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMA</span><span style=\"color: #a8adb6;\">S</span><span style=\"color: #7c8491;\">SISMA</span><span style=\"color: #d4d7db;\">S</span><span style=\"color: white;\">SISMASSISMASSISMASSISMASSISMASSIS</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">MASSISMASS</span><span style=\"color: #a8adb6;\">I</span><span style=\"color: #7c8491;\">SMASS</span><span style=\"color: #89909c;\">I</span><span style=\"color: #9096a2;\">SMASS</span><span style=\"color: #8f96a1;\">I</span><span style=\"color: #8b929d;\">SMASSISMASSISMASSISMASSIS</span><span style=\"color: #8d949f;\">M</span><span style=\"color: white;\">A</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">SSISMASSIS</span><span style=\"color: #a8adb6;\">M</span><span style=\"color: #7c8491;\">ASSISMASSISMASSISMASSISMASSISMASSISMA</span><span style=\"color: #7e8693;\">S</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "<strong><span style=\"color: white;\">ISMASSISMA</span><span style=\"color: #cacdd2;\">S</span><span style=\"color: #afb4bc;\">SISMASSISMASSISMASSISMASSISMASSISMASS</span><span style=\"color: #b0b5bd;\">I</span><span style=\"color: white;\">S</span></strong>\r\n"
					+ "\r\n" + "</pre></p>\r\n"
					+ "Rafael Pax, 2015. <span style=\"font-family: 'courier new', courier;\">pax.rafael#gmail#com</span>\r\n"
					+ "</body>\r\n" + "</html>\r\n";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		super.start();
		AbstractSimulation simulation = (AbstractSimulation) state;
		HomeDisplay displayFrame = new HomeDisplay(simulation.building.getHome());
		displayFrame.setTitle("Building 2D");
		HomeDisplay3D displayFrame3D = new HomeDisplay3D(simulation.building);
		displayFrame3D.setTitle("Building 3D");
		if (this.layers == null) {
			/**
			 * @formatter:off
			 * Default layers
			 **/
			this.layers = new DrawableLayer[] {
					new RoomsLayer(false),
					new RoomsLabelLayer(false),
					new VisionRadioLayer(false),
					new CrowdDensityLayer(true),
					new WallLayer(true),
					new DoorLayer(true),
					new ConnectionsLayer(false),
					new PathLayer(false),
					new PeopleLayer(true),
					new RadioLayer(false),
					new PathFinderLayer(false),
					new PeopleIDLayer(false),
					new VisibleAgentsLines(false),
					new QTLayer(false)
					};
			/**
			 * @formatter:on
			 */
		}

		
		String welcomeText = "MASSIS";

		BuildingMap buildingMap = new BuildingMap(simulation.building, layers);
		buildingMap.setTitle("Building Map");

		this.controller.registerFrame(buildingMap);
		this.controller.registerFrame(displayFrame);
		this.controller.registerFrame(displayFrame3D);

		simulation.building.registerDisplays(displayFrame3D, buildingMap);
	}

	@Override
	public void quit() {
		super.quit();

		for (Object f : this.controller.getAllFrames()) {
			((JFrame) f).dispose();
		}

		this.controller.unregisterAllFrames();
	}
}
