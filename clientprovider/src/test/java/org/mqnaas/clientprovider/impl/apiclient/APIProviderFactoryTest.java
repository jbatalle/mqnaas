package org.mqnaas.clientprovider.impl.apiclient;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;
import org.mqnaas.test.helpers.ReflectionTestHelper;
import org.mqnaas.test.helpers.capability.ArtificialBundleGuard;
import org.mqnaas.test.helpers.capability.TestCapabilitiesFactory;
import org.mqnaas.test.helpers.clientprovider.EmptyClientAPI;
import org.mqnaas.test.helpers.clientprovider.EmptyClientConfiguration;
import org.mqnaas.test.helpers.clientprovider.ProviderFactoryHelpers;
import org.mqnaas.test.helpers.clientprovider.TestAPIProvider;
import org.mqnaas.test.helpers.clientprovider.TestClientProviderFactory;
import org.mqnaas.test.helpers.clientprovider.TestInternalAPIProvider;
import org.mqnaas.test.helpers.resource.TestResourceFactory;

/**
 * Unit tests for {@link APIProviderFactory}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class APIProviderFactoryTest {

	@Test
	public void testAPIProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		// generate artificial objects
		IRootResource resource = TestResourceFactory.createIRootResource(null, null, null, Arrays.asList(new Endpoint()));
		ArtificialBundleGuard bg = TestCapabilitiesFactory.createArtificialBundleGuard();
		ICoreModelCapability cmc = TestCapabilitiesFactory.createArtificialCoreModelCapability(resource);
		EmptyClientConfiguration ecc = TestClientProviderFactory.createEmptyClientConfiguration();

		// create APIProviderFactory, inject dummy capabilities and start it
		APIProviderFactory apf = new APIProviderFactory();
		ReflectionTestHelper.<APIProviderFactory, IBundleGuard> injectPrivateField(apf, bg, "bundleGuard");
		ReflectionTestHelper.<APIProviderFactory, ICoreModelCapability> injectPrivateField(apf, cmc, "coreModelCapability");
		apf.activate();

		// add InternalClientProvider to the system, using an artificially generated event
		bg.throwClassEntered(ProviderFactoryHelpers.getInternalClassListener(AbstractProviderFactory.class, apf), TestInternalAPIProvider.class);

		// obtain a client from client provider
		EmptyClientAPI client = apf.<EmptyClientConfiguration, TestAPIProvider> getAPIProvider(TestAPIProvider.class).getAPIClient(resource,
				EmptyClientAPI.class, ecc);
		Assert.assertTrue("Client must be an instance of EmptyClientAPI.", client instanceof EmptyClientAPI);
	}
}