package br.edu.ufcg.lsd.core.models;

import eu.atmosphere.tmaf.monitor.message.Data;
import eu.atmosphere.tmaf.monitor.message.Data.Type;

public class MessageComponent {

	private DescriptionMonitor descriptionId;
	private Data.Type type;
	private long timestamp;
	private double value;

	public MessageComponent(DescriptionMonitor descriptionMonitorId, Type type, long timestamp, double value) {
		super();
		this.descriptionId = descriptionMonitorId;
		this.type = type;
		this.timestamp = timestamp;
		this.value = value;
	}
	
	public DescriptionMonitor getDescriptionId() {
		return descriptionId;
	}
	
	public Data.Type getType() {
		return type;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public double getValue() {
		return value;
	}

	public enum DescriptionMonitor {
		FULFILLED_COMPUTES(1), 
		FAILED_COMPUTES(2),
		FULFILLED_VOLUMES(3),
		FAILED_VOLUMES(4),
		FULFILLED_NETWORKS(5),
		FAILED_NETWORKS(6),
		FULFILLED_ATTACHMENTS(7),
		FAILED_ATTACHMENTS(8),
		FULFILLED_PUBLIC_IP(9),
		FAILED_PUBLIC_IP(10),		
		LAST_MEASUREMENT(101); // TODO will be used in the future

		private int value;

		private DescriptionMonitor(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

}
