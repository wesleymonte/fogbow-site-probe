package br.edu.ufcg.lsd.core.plugins.databases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.core.plugins.databases.RASDatabase.OrderType;
import br.edu.ufcg.lsd.core.utils.ProbeConstants;

public class PrimaryRASDatabaseTest {

	// embedded database
	private final String DBCP2_DRIVER = "org.apache.commons.dbcp2.PoolingDriver";
	private static final String SRC_TEST_RESOURCE_BD_BASE_SQL = "src/test/resource/bd/base.sql";
	private static final String JDBC_HSQLDB_PASSWORD = "pass";
	private static final String JDBC_HSQLDB_USERNAME = "sa";
	private static final String JDBC_HSQLDB_URL = "jdbc:hsqldb:mem:myhb";
	
	// orders
	private static final int COMPUTE_FULFILLED_SIZE = 3;
	private static final int VOLUME_FULFILLED_SIZE = 2;
	private static final int NETWORK_FULFILLED_SIZE = 1;
	private static final int ATTACHMENT_FULFILLED_SIZE = 1;
	private static final int PUBLIC_IP_FULFILLED_SIZE = 1;
	
	private static final int COMPUTE_FAILED_SIZE = 2;
	private static final int VOLUME_FAILED_SIZE = 1;
	private static final int NETWORK_FAILED_SIZE = 1;
	private static final Object PUBLIC_IP_FAILED_SIZE = 1;
	private static final Object ATTACHMENT_FAILED_SIZE = 1;
	
	private PrimaryRASDatabase primaryRASDatabase;
	
	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		properties.put(ProbeConstants.Properties.USER_DATABASE, JDBC_HSQLDB_USERNAME);
		properties.put(ProbeConstants.Properties.DATABASE_URL, JDBC_HSQLDB_URL);
		properties.put(ProbeConstants.Properties.PASSWORD_DATABASE, JDBC_HSQLDB_PASSWORD);
		properties.put(ProbeConstants.Properties.DATABASE_DRIVER, DBCP2_DRIVER);
		this.primaryRASDatabase = new PrimaryRASDatabase(properties);
		createSchema(this.primaryRASDatabase.getConnection());
	}
	
	// test case : Count fulfilled orders
	@Test
	public void testCountFulfilled() {
		int countComputeFulfilled = this.primaryRASDatabase.getCountOrder(OrderType.COMPUTE, RASDatabase.OrderState.FULFILLED);
		int countVolumeFulfilled = this.primaryRASDatabase.getCountOrder(OrderType.VOLUME, RASDatabase.OrderState.FULFILLED);
		int countNetworkFulfilled = this.primaryRASDatabase.getCountOrder(OrderType.NETWORK, RASDatabase.OrderState.FULFILLED);
		int countAttachmentFulfilled = this.primaryRASDatabase.getCountOrder(OrderType.ATTACHMENT, RASDatabase.OrderState.FULFILLED);
		int countPublicIpFulfilled = this.primaryRASDatabase.getCountOrder(OrderType.PUBLIC_ID, RASDatabase.OrderState.FULFILLED);
		
		// verify
		Assert.assertEquals(COMPUTE_FULFILLED_SIZE, countComputeFulfilled);
		Assert.assertEquals(VOLUME_FULFILLED_SIZE, countVolumeFulfilled);
		Assert.assertEquals(NETWORK_FULFILLED_SIZE, countNetworkFulfilled);
		Assert.assertEquals(ATTACHMENT_FULFILLED_SIZE, countAttachmentFulfilled);
		Assert.assertEquals(PUBLIC_IP_FULFILLED_SIZE, countPublicIpFulfilled);
	}
	
	// test case : Count failed orders
	@Test
	public void testCountFailed() {
		int countComputeFailed = this.primaryRASDatabase.getCountOrder(OrderType.COMPUTE, RASDatabase.OrderState.FAILED);
		int countVolumeFailed = this.primaryRASDatabase.getCountOrder(OrderType.VOLUME, RASDatabase.OrderState.FAILED);
		int countNetworkFaield = this.primaryRASDatabase.getCountOrder(OrderType.NETWORK, RASDatabase.OrderState.FAILED);
		int countAttachmentFaield = this.primaryRASDatabase.getCountOrder(OrderType.ATTACHMENT, RASDatabase.OrderState.FAILED);
		int countPublicIpFaield = this.primaryRASDatabase.getCountOrder(OrderType.PUBLIC_ID, RASDatabase.OrderState.FAILED);
		
		// verify
		Assert.assertEquals(COMPUTE_FAILED_SIZE, countComputeFailed);
		Assert.assertEquals(VOLUME_FAILED_SIZE, countVolumeFailed);
		Assert.assertEquals(NETWORK_FAILED_SIZE, countNetworkFaield);
		Assert.assertEquals(ATTACHMENT_FAILED_SIZE, countAttachmentFaield);
		Assert.assertEquals(PUBLIC_IP_FAILED_SIZE, countPublicIpFaield);
	}	
	
	private void createSchema(final Connection conn) throws SQLException, IOException {
		File file = new File(SRC_TEST_RESOURCE_BD_BASE_SQL);
		runScript(conn, new InputStreamReader(new FileInputStream(file)));
	}
	
	private void runScript(final Connection conn, final Reader script)
			throws SQLException, IOException {
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(script);
	}	
	
}
