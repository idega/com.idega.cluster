/*
 * $Id: MyConfigurator.java,v 1.1 2006/12/11 15:49:08 thomas Exp $
 * Created on Nov 30, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster;
	
	import net.jxta.ext.config.AbstractConfigurator;
	import net.jxta.ext.config.Configurator;
	import net.jxta.exception.ConfiguratorException;
	import net.jxta.impl.protocol.PlatformConfig;

	public class MyConfigurator
	extends AbstractConfigurator {

	  private static final String NAME = "Thomas";
	  private static final String PRINCIPAL = "principal";
	  private static final String PASSWORD = "password";

	  public PlatformConfig createPlatformConfig(Configurator c)
	  throws ConfiguratorException {
	    c.setName(NAME);
	    c.setSecurity(PRINCIPAL, PASSWORD);

	    return c.getPlatformConfig();
	  }	
	
	
}
