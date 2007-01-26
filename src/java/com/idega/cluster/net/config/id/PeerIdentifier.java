/*
 * $Id: PeerIdentifier.java,v 1.1 2007/01/26 07:15:02 thomas Exp $
 * Created on Jan 26, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.config.id;

import java.net.URI;


/**
 * 
 *  Last modified: $Date: 2007/01/26 07:15:02 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class PeerIdentifier {
	
	private URI uri = null;
	private String identifier = null;
	
	public PeerIdentifier(URI uri, String identifier) {
		this.uri = uri;
		this.identifier = identifier;
	}

	
	public String getIdentifier() {
		return identifier;
	}

	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	
	public URI getUri() {
		return uri;
	}

	
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
}
