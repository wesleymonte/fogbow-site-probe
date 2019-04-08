package br.edu.ufcg.lsd.core.plugins.submission;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufcg.lsd.core.models.MessageComponent;
import br.edu.ufcg.lsd.core.utils.ProbeConstants;
import eu.atmosphere.tmaf.monitor.client.BackgroundClient;
import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Data.Type;
import eu.atmosphere.tmaf.monitor.message.Message;
import eu.atmosphere.tmaf.monitor.message.Observation;

public class ClientTMASubmissionMonitor implements SubmissionMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientTMASubmissionMonitor.class);
	
	private static final int DEFAULT_WAITING_TIME_TO_SEND_MESSAGES = 5000;
	
	private String monitorUrl;
	private Integer probeId;
	private String probePassword;
	private Integer resourceId;
	
	public ClientTMASubmissionMonitor(Properties properties) throws Exception {
		this.monitorUrl = properties.getProperty(ProbeConstants.Properties.MONITOR_URL);
		
		try {
			this.probeId = Integer.valueOf(properties.getProperty(ProbeConstants.Properties.PROBE_ID));			
		} catch (Exception e) {
			throw new Exception("Is not possible get the probe id", e);
		}
		
		try {
			this.resourceId = Integer.valueOf(properties.getProperty(ProbeConstants.Properties.RESOURCE_ID));			
		} catch (Exception e) {
			throw new Exception("Is not possible get the resource id", e);
		}		
		
		this.probePassword = properties.getProperty(ProbeConstants.Properties.PROBE_PASSWORD);
	}
	
	public void sendToMonitor(List<MessageComponent> messagesComponent) throws Exception {
		if (messagesComponent == null || messagesComponent.size() == 0) {
			LOGGER.info("There are not messages to send");
			return;
		}
		LOGGER.info(String.format("Sending %s message(s) to %s", messagesComponent.size(), this.monitorUrl));
		BackgroundClient client = new BackgroundClient(this.monitorUrl);

		checkAuthentication(client);
		checkStarting(client);

		for (MessageComponent messageComponent : messagesComponent) {
			Message message = client.createMessage();
			
			message.setResourceId(this.resourceId);
			
			Type type = messageComponent.getType();
			long time = messageComponent.getTimestamp();
			double value = messageComponent.getValue();
			int descriptionId = messageComponent.getDescriptionId().getValue();
		
			LOGGER.debug(String.format("Trying send message: %s, %s, %s, %s, %s",
					this.resourceId, type.name(), descriptionId, time, value));			
			message.addData(new Data(type, descriptionId, new Observation(time, value)));
			
			client.send(message);
		}

		try {
			// TODO understand better. 
			// This "sleep" is necessary because the BackgroundClient is trying send the messages to the monitor
			// The BackgroundClient do not have a callback of messages sent
			Thread.sleep(DEFAULT_WAITING_TIME_TO_SEND_MESSAGES);
			
			client.stop();
		} catch (Exception e) {
			LOGGER.error("There was a problem when was tried to stop", e);
		} 

		client.shutdown();
	}

	private void checkStarting(BackgroundClient client) throws Exception {
		boolean start = client.start();
		if (!start) {
			throw new Exception("The client was not started");	
		}
	}

	private void checkAuthentication(BackgroundClient client) throws Exception {
		boolean authenticated = client.authenticate(this.probeId, this.probePassword.getBytes());
		if (!authenticated) {
			throw new Exception("It is not authenticated");
		}
	}		

}
