package org.mqnaas.core.api.network;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.AddsResource;

/**
 * A network management capability that uses network requests to create new
 * networks.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IRequestBasedNetworkManagement extends IBaseNetworkManagement, ICapability {

	/**
	 * Returns the new network resource created, which is configured as defined
	 * in the given request.
	 */
	@AddsResource
	IRootResource createNetwork(IResource request);


}
