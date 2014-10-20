package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
class APIClientProviderAdapter<CC> implements InvocationHandler {

	private IInternalAPIClientProvider<CC>	internalAPIProvider;

	public APIClientProviderAdapter(IInternalAPIClientProvider<CC> internalAPIProvider) {
		this.internalAPIProvider = internalAPIProvider;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Class<?> apiClass = (Class<?>) args[0];

		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		switch (args.length) {
			case 1:
				return internalAPIProvider.getClient(apiClass, ep, c);
			case 2:
				// TODO document suppression
				@SuppressWarnings("unchecked")
				CC clientConfiguration1 = (CC) args[1];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration1);
			case 3:
				// TODO document suppression
				@SuppressWarnings("unchecked")
				CC clientConfiguration2 = (CC) args[1];
				Object applicationSpecificConfiguration = args[2];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration2, applicationSpecificConfiguration);
		}

		throw new IllegalStateException("No mapping for method " + method);
	}

}