package org.opennaas.core.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.opennaas.core.annotation.AddsResource;
import org.opennaas.core.annotation.RemovesResource;

/**
 * <p>
 * <code>IResourceManagement</code> is one of the core capabilities of OpenNaaS.
 * </p>
 * 
 * <p>
 * It manages all {@link IResource}s available in the platform. These resources may either be representations of physical devices or any other unity
 * offering its services.
 * </p>
 * 
 */
@Path("/mqnaas/resources")
public interface IResourceManagement extends ICapability {

	/**
	 * <p>
	 * Adds the given {@link IResource} to the resources managed by the platform.
	 * </p>
	 * <p>
	 * The service {@link IBindingManagement#resourceAdded(IResource)} is notified, whenever this service was executed successfully.
	 * </p>
	 * 
	 * @param resource
	 *            The resource to be added to the platform
	 */
	@AddsResource
	@PUT
	void createRootResource(RootResourceDescriptor descriptor);

	void createRootResource(Specification specification);

	/**
	 * <p>
	 * Remove the given {@link IResource} from the resources managed by the platform.
	 * </p>
	 * 
	 * <p>
	 * The service {@link IBindingManagement#resourceRemoved(IResource)} is notified whenever this service was executed successfully.
	 * 
	 * @param resource
	 *            The resource to be removed from the platform
	 */
	@RemovesResource
	@DELETE
	void removeRootResource(IResource resource);

	/**
	 * Returns all {@link IResource} currently managed by the platform.
	 * 
	 * @return The currently managed resources
	 */
	@GET
	List<IResource> getRootResources();

	/**
	 * TODO This method a draft method. May not be part of the final API.
	 */
	<R extends IResource> R getRootResource(Class<R> clazz);

}
