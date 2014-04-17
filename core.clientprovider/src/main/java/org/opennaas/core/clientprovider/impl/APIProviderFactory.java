package org.opennaas.core.clientprovider.impl;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opennaas.core.client.cxf.InternalCXFClientProvider;
import org.opennaas.core.clientprovider.api.IAPIProvider;
import org.opennaas.core.clientprovider.api.IAPIProviderFactory;
import org.opennaas.core.clientprovider.api.IInternalAPIProvider;

public class APIProviderFactory extends AbstractProviderFactory implements IAPIProviderFactory {

	private static Set<Type> VALID_API_PROVIDERS;

	static {
		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIProvider.class);
	}
	
	List<IInternalAPIProvider<?>> internalAPIProviders;
	
	public APIProviderFactory() {
		// List of available IInternalAPIProvider must be maintained by
		// classpath scanning
		internalAPIProviders = new ArrayList<IInternalAPIProvider<?>>();
		internalAPIProviders.add(new InternalCXFClientProvider());
	}
	
	@Override
	public <CC, C extends IAPIProvider<CC>> C getAPIProvider(
			Class<C> apiProviderClass) {
		// Match against list of providers...
		for (IInternalAPIProvider<?> internalApiProvider : internalAPIProviders) {
			Class<?> internalAPIProviderClass = internalApiProvider.getClass();

			if (doTypeArgumentsMatch(VALID_API_PROVIDERS,
					apiProviderClass, internalAPIProviderClass, 1)) {
				
				C c = (C) Proxy.newProxyInstance(internalAPIProviderClass.getClassLoader(),
						new Class[] { apiProviderClass },
						new APIProviderAdapter(internalApiProvider));
				
				return c;
			}
		}
		return null;
	}


}
