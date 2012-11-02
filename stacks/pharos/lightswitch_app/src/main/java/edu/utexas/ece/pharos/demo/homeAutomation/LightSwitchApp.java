package edu.utexas.ece.pharos.demo.homeAutomation;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.message.MessageListener;
import edu.utexas.ece.pharos.utils.ByteUtils;
import edu.utexas.ece.pharos.utils.FileLogger;
import edu.utexas.ece.pharos.utils.Logger;
import edu.utexas.ece.pharos.utils.ThreadUtils;

// The following classed are automatically generated. 
// They are located in: 
// ~/.ros/rosjava/lib/org.ros.rosjava.lightswitch_node-0.0.0.jar
import lightswitch_node.AmbientLight;
import lightswitch_node.LightSwitchCmd;

/**
 * This is the basic light switch app.  It toggles the 
 * light NUM_ROUNDS times and records the latency of execution.
 * 
 * @author Chien-Liang Fok
 */
public class LightSwitchApp extends AbstractNodeMain {
	public static final int NUM_ROUNDS = 20;
	
	@Override
	public GraphName getDefaultNodeName() {
		// The parameter is the node's name.
		return GraphName.of("lightswitch_app/LightSwitchApp");
	}

	private String printAmbientLight(AmbientLight message) {
		StringBuffer sb = new StringBuffer();
		sb.append("AmbientLight:\n");
		sb.append("\theader:\n");
		sb.append("\t\tseq = "         + message.getHeader().getSeq() + "\n");
		sb.append("\t\tstamp.secs = "  + message.getHeader().getStamp().secs + "\n");
		sb.append("\t\tstamp.nsecs = " + message.getHeader().getStamp().nsecs + "\n");
		sb.append("\t\tframe_id = "    + message.getHeader().getFrameId() + "\n");
		sb.append("\tlightLevel = : " + ByteUtils.unsignedByteToInt(message.getLightLevel()) + "\n");
		return sb.toString();
	}

	@Override
	public void onStart(ConnectedNode node) {
		
		Logger.setFileLogger(new FileLogger("LightSwitchApp.log"));
		
		Logger.log("Creating a LightSwitchCmd publisher...");
		final Publisher<LightSwitchCmd> publisher =
				node.newPublisher("lightswitch_cmd", "lightswitch_node/LightSwitchCmd");
		
		// For now use custom logging component.
		//final Log log = node.getLog();
		//log.info("Subscribing to AmbientLight messages.");
		
		Logger.log("Subscribing to AmbientLight messages...");
		Subscriber<AmbientLight> subscriber = node.newSubscriber("lightLevel", "lightswitch_node/AmbientLight");
		subscriber.addMessageListener(new MessageListener<AmbientLight>() {
			@Override
			public void onNewMessage(AmbientLight message) {
				Logger.log("Received AmbientLight message:\n" + printAmbientLight(message));
			}
		});

		Logger.log("Sleeping for 1s before starting...");
		ThreadUtils.delay(1000);
		node.executeCancellableLoop(new CancellableLoop() {
			
			int numRounds = 0;
			
			@Override
			protected void setup() {
			}

			@Override
			protected void loop() {
				if (numRounds++ < NUM_ROUNDS) {
					LightSwitchCmd cmd = publisher.newMessage();
					long startTime, endTime;

					StringBuffer sb = new StringBuffer("Round " + (numRounds-1) + "\n");
					sb.append("\t[" + System.currentTimeMillis() + "] Turning light off...\n");
					cmd.setCmd((byte)0);
					startTime = System.nanoTime();
					publisher.publish(cmd);
					endTime = System.nanoTime();
					sb.append("\t[" + System.currentTimeMillis() + "] Light should be off, latency = " + (endTime - startTime) + "\n");

					sb.append("\tWaiting 4s...\n");
					ThreadUtils.delay(4000);

					sb.append("\t[" + System.currentTimeMillis() + "] Turning light on...\n");
					cmd.setCmd((byte)1);
					startTime = System.nanoTime();
					publisher.publish(cmd);
					endTime = System.nanoTime();
					sb.append("\t[" + System.currentTimeMillis() + "] Light should be on, latency = " + (endTime - startTime) + "\n");
					
					Logger.log(sb.toString());
					
					ThreadUtils.delay(4000);
				} else {
					Logger.log("Done!");
					System.exit(0);
				}
			}
		});
	}

	@Override
	public void onShutdown(Node node) {
	}

	@Override
	public void onShutdownComplete(Node node) {
	}
}