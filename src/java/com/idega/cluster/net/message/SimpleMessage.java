/*
 * $Id: SimpleMessage.java,v 1.1 2007/01/12 15:42:36 thomas Exp $
 * Created on Dec 28, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.cluster.net.message;

import java.io.IOException;
import net.jxta.pipe.OutputPipe;
import com.idega.core.event.MethodCallEvent;



/**
 * 
 *  Last modified: $Date: 2007/01/12 15:42:36 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public interface SimpleMessage extends MethodCallEvent {
	
	void send(OutputPipe outputPipe) throws IOException;
	
}
