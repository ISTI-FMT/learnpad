/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.learnpad.core.impl.or;

import java.io.InputStream;
import java.nio.file.Paths;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Path;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.rest.XWikiRestComponent;

import eu.learnpad.core.impl.or.XwikiBridgeInterfaceRestResource;
import eu.learnpad.core.rest.RestResource;
import eu.learnpad.core.rest.XWikiRestUtils;
import eu.learnpad.exception.LpRestException;
import eu.learnpad.exception.impl.LpRestExceptionImpl;
import eu.learnpad.or.Controller;
import eu.learnpad.or.BridgeInterface;

/*
 * It is not clear yet who is responsible for the instantiation
 * of this class. From what I read from Jean it is supposed to be done
 * automatically when registering the Java component into the XWiki Platform.
 * Thus I do not know how may instances we may actually have. Thus, for the
 * moment I marked it as Singleton.
 *  
 */
@Component
@Singleton
@Named("eu.learnpad.core.impl.or.XwikiController")
@Path("/learnpad/or/corefacade")
public class XwikiController extends Controller implements XWikiRestComponent, Initializable{
	
    /** Set to true once the inherited BridgeInterface has been initialized. */
    private boolean initialized = false;	
    
/*
 * Note that in this solution the Controllers do not interact
 * each-others, but each controller directly invokes the BridgesInterfaces
 * (from the other controllers) it needs. This is not actually what was
 * originally planned, thus in the future it may change.
 *
 * Also, not sure if this is the correct way to proceed.
 * I would like to decide in a configuration file
 * the implementation to bind, and not into the source
 * code. In fact, this second case implies to rebuild the
 * whole platform at each change.	
 */
	private eu.learnpad.cw.BridgeInterface cw;
	private eu.learnpad.sim.BridgeInterface sim;

    public synchronized void updateBridgeInterface (BridgeInterface bi){
		this.bridge = bi;    
    }

	 /** A means of instantiating the inherited BridgeInterface according
	  * to XWIKI (see  http://extensions.xwiki.org/xwiki/bin/view/Extension/Component+Module#HComponentInitialization).
	  * Actually in this implementation we currently support only 
	  * the class XwikiBridgeInterfaceRestResource, but other classes (such as XwikiBridgeInterface)
	  * should be supported in the future
	  * 
	  * Not sure if we can consider the default constructor.*/
	
	@Override
	public synchronized void initialize() throws InitializationException {
		if (!this.initialized){
			this.bridge = new XwikiBridgeInterfaceRestResource();
			
			this.cw = new eu.learnpad.core.impl.cw.XwikiBridgeInterfaceRestResource();
			this.sim = new eu.learnpad.core.impl.sim.XwikiBridgeInterfaceRestResource();

			this.initialized=true;
		}
	}
  
    
    @Override
	public byte[] getComments(String modelSetId, String artifactId)
			throws LpRestExceptionImpl {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getModel(String modelSetId, String type)
			throws LpRestException {
		String attachmentName = String.format("%s.%s", modelSetId, type);
		String fileName = "adoxx_modelset.xml";
		java.nio.file.Path filePath = Paths.get(fileName);
		return XWikiRestUtils.getFileInAttachment(RestResource.CORE_REPOSITORY_WIKI,
				RestResource.CORE_REPOSITORY_SPACE, modelSetId, attachmentName, filePath);
	}


}
