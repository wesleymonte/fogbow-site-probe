package br.edu.ufcg.lsd.core.plugins.components;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.lsd.core.models.MessageComponent;
import br.edu.ufcg.lsd.core.plugins.databases.RASDatabase;

public class RASComponentTest {

	private RASComponent rasComponent;
	
	@Before
	public void setUp() {
		this.rasComponent = Mockito.spy(new RASComponent());
	}
	
	// test case: success case
	@Test
	public void testGetMessage() {
		// set up
		int computeFulffield = 1;
		int computeFailed = 2;
		
		Mockito.doReturn(computeFulffield).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.COMPUTE), Mockito.eq(RASDatabase.OrderState.FULFILLED));
		Mockito.doReturn(computeFailed).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.COMPUTE), Mockito.eq(RASDatabase.OrderState.FAILED));
		
		int volumeFulffield = 1;
		int volumeFailed = 2;
		
		Mockito.doReturn(volumeFulffield).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.VOLUME), Mockito.eq(RASDatabase.OrderState.FULFILLED));
		Mockito.doReturn(volumeFailed).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.VOLUME), Mockito.eq(RASDatabase.OrderState.FAILED));		

		int networkFulffield = 3;
		int networkFailed = 5;
		
		Mockito.doReturn(networkFulffield).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.NETWORK), Mockito.eq(RASDatabase.OrderState.FULFILLED));
		Mockito.doReturn(networkFailed).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.NETWORK), Mockito.eq(RASDatabase.OrderState.FAILED));
		
		int attachmentFulffield = 1;
		int attachmentFailed = 2;
		
		Mockito.doReturn(attachmentFulffield).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.ATTACHMENT), Mockito.eq(RASDatabase.OrderState.FULFILLED));
		Mockito.doReturn(attachmentFailed).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.ATTACHMENT), Mockito.eq(RASDatabase.OrderState.FAILED));
				
		int publicIpFulffield = 14;
		int publicIpFailed = 6;
		
		Mockito.doReturn(publicIpFulffield).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.PUBLIC_ID), Mockito.eq(RASDatabase.OrderState.FULFILLED));
		Mockito.doReturn(publicIpFailed).when(this.rasComponent).getOrdersQuant(
				Mockito.eq(RASDatabase.OrderType.PUBLIC_ID), Mockito.eq(RASDatabase.OrderState.FAILED));		
		
		// exercise
		List<MessageComponent> messages = this.rasComponent.getMessagesComponent();
		
		// verify
		Assert.assertTrue(messages.get(0).getValue() == computeFulffield);
		Assert.assertTrue(messages.get(1).getValue() == computeFailed);
		Assert.assertTrue(messages.get(2).getValue() == volumeFulffield);
		Assert.assertTrue(messages.get(3).getValue() == volumeFailed);
		Assert.assertTrue(messages.get(4).getValue() == networkFulffield);
		Assert.assertTrue(messages.get(5).getValue() == networkFailed);
		Assert.assertTrue(messages.get(6).getValue() == attachmentFulffield);
		Assert.assertTrue(messages.get(7).getValue() == attachmentFailed);
		Assert.assertTrue(messages.get(8).getValue() == publicIpFulffield);
		Assert.assertTrue(messages.get(9).getValue() == publicIpFailed);		
	}
	
	// test case:
	@Ignore
	@Test
	public void testGetMessageBDerror() {
		
	}
	
}
