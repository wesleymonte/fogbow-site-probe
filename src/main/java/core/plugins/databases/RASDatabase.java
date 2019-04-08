package br.edu.ufcg.lsd.core.plugins.databases;

public interface RASDatabase {
	
	int getCountOrder(OrderType orderType, OrderState orderState);

	enum OrderState {
		FULFILLED("FULFILLED"),
		// There are two error types(FAILED_AFTER_SUCCESSUL_REQUEST, FAILED_ON_REQUEST)
		FAILED("FAILED%");
		
		private String value;
		
		private OrderState(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}			
	}
	
	enum OrderType {
		COMPUTE("compute_order_table"), 
		VOLUME("volume_order_table"), 
		NETWORK("network_order_table"),
		ATTACHMENT("attachment_order_table"),
		PUBLIC_ID("public_ip_order_table");
		
		private String tableName;
		
		private OrderType(String tableName) {
			this.tableName = tableName;
		}
		
		public String getTableName() {
			return tableName;
		}	
	}
	
}
