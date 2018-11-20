package br.edu.ufcg.lsd.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufcg.lsd.core.models.MessageComponent;
import br.edu.ufcg.lsd.core.models.MessageComponent.DescriptionMonitor;
import br.edu.ufcg.lsd.core.plugins.components.Component;
import br.edu.ufcg.lsd.core.plugins.components.RASComponent;
import br.edu.ufcg.lsd.core.plugins.submission.ClientTMASubmissionMonitor;
import br.edu.ufcg.lsd.core.plugins.submission.SubmissionMonitor;
import br.edu.ufcg.lsd.core.utils.ManagerTimer;
import br.edu.ufcg.lsd.core.utils.ProbeConstants;
import eu.atmosphere.tmaf.monitor.message.Data;

public class ProbeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProbeController.class);
	
	protected static final int SCHEDULER_TIME_DEFAULT = 5000;
	
	private Component component;
	private SubmissionMonitor submissionMonitor;
	private Properties properties;
	private ManagerTimer schedulerTimer;
	
	protected ProbeController() {}
	
	public ProbeController(Properties properties) throws Exception {
		this.properties = properties;
		this.schedulerTimer = new ManagerTimer(Executors.newScheduledThreadPool(1));
		
		configureComponent(properties);
		configureSubmissionMontiro(properties);		
	}
	
	public void start() throws Exception {
		long schedulerPeriod = getSchedulerPeriod();
		
		sendInitialEventoToMonitor();
		
		this.schedulerTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					action();
				} catch (Throwable e) {
					LOGGER.error("Error while sending message to TMA Monitor", e);
				}
			}

		}, 0, schedulerPeriod);
	}

	protected long getSchedulerPeriod() {
		String schedulerPeriodStr = properties.getProperty(ProbeConstants.Properties.SCHEDULER_PERIOD);
		
		long schedulerPeriod = SCHEDULER_TIME_DEFAULT;
		try {
			schedulerPeriod = schedulerPeriodStr != null && !schedulerPeriodStr.isEmpty() 
					? Long.parseLong(schedulerPeriodStr) : SCHEDULER_TIME_DEFAULT;			
		} catch (NumberFormatException e) {
			LOGGER.warn("The " + ProbeConstants.Properties.SCHEDULER_PERIOD + " is wrong" , e);
		}
		
		return schedulerPeriod;
	}
	
	protected void action() throws Exception {
		List<MessageComponent> messagesComponent = this.component.getMessagesComponent();
		this.submissionMonitor.sendToMonitor(messagesComponent);
	}
	
	// TODO (next release) implement support to other components
	protected void configureComponent(Properties properties) throws Exception {
		try {
			this.component = new RASComponent(properties);
		} catch (Exception e) {
			String errorMsg = "Is not possible initialize the component";
			LOGGER.error(errorMsg, e);
			throw new Exception(errorMsg);
		}
	}	

	// TODO (next release) implement support to other submission monitors
	protected void configureSubmissionMontiro(Properties properties) throws Exception {
		try {
			this.submissionMonitor = new ClientTMASubmissionMonitor(properties);
		} catch (Exception e) {
			String errorMsg = "Is not possible initialize the submission monitor";
			LOGGER.error(errorMsg, e);
			throw new Exception(errorMsg);
		}
	}
	
	protected void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	protected void sendInitialEventoToMonitor() throws Exception {
		LOGGER.info("The sending initial evento to Monitor");
		
		List<MessageComponent> messagesComponent = new LinkedList<MessageComponent>();
		// TODO in the future it is the time of initialization of the RAS
		long now = System.currentTimeMillis();
		messagesComponent.add(new MessageComponent(
				DescriptionMonitor.FULFILLED_COMPUTES, Data.Type.EVENT, now, now));
		
		this.submissionMonitor.sendToMonitor(messagesComponent);		
	}	
	
}
