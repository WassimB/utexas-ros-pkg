package edu.utexas.ece.pharos.proteus3.apps.tests;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
//import org.ros.concurrent.CancellableLoop;

import edu.utexas.ece.pharos.logger.FileLogger;
import edu.utexas.ece.pharos.logger.Logger;
import edu.utexas.ece.pharos.proteus3.sensors.CompassBuffer;


// Import the messages
import proteus3_compass.CompassMsg;

/**
 * Subscribes to Proteus III compass measurements and displays/logs
 * them.
 *
 * @author Chien-Liang Fok
 */
public class CompassLogger extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("proteus3/compass_logger");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		System.setProperty ("PharosMiddleware.debug", "true");
		//final Log log = connectedNode.getLog();
		FileLogger flogger = new FileLogger("Compass.log");
		Logger.setFileLogger(flogger);

		CompassBuffer compassBuffer = new CompassBuffer();

		Subscriber<CompassMsg> compassSubscriber = connectedNode.newSubscriber("/compass/measurement", CompassMsg._TYPE);
		compassSubscriber.addMessageListener(compassBuffer); 
		
		
		//compassBuffer.setGUI(compassGUI);
		
//		connectedNode.executeCancellableLoop(new CancellableLoop() {
//		      
//		      @Override
//		      protected void setup() {
//		       
//		      }
//
//		      @Override
//		      protected void loop() throws InterruptedException {
//		        
//		      }
//		    });
	}
}