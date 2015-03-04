package org.mqnaas.extensions.network.itests;

/*
 * #%L
 * MQNaaS :: Network Integration Tests
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.network.api.modelreader.IResourceModelReader;
import org.mqnaas.network.api.modelreader.ResourceModelWrapper;
import org.mqnaas.network.impl.Network;
import org.mqnaas.network.impl.NetworkSubResource;
import org.mqnaas.network.impl.PortResourceWrapper;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * 
 * @author Adrián Roselló Rey(i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ResourceModelReaderTest {

	@Inject
	IRootResourceProvider		rootResourceProvider;

	@Inject
	IRootResourceAdministration	rootResourceAdministration;

	@Inject
	IServiceProvider			serviceProvider;

	private IRootResource		network;
	private IRootResource		ofSwitch;
	private IResource			ofSwitchPort1;
	private IResource			ofSwitchPort2;

	private static final String	OFSWITCH_PORT1_EXT_ID	= "eth1";
	private static final String	OFSWITCH_PORT2_EXT_ID	= "eth2";

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.1
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.1"))
						.karafVersion("3.0.1").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/paxexam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// maintain our log configuration
				KarafDistributionOption.doNotModifyLogConfiguration(),
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void startResources() throws InstantiationException, IllegalAccessException, URISyntaxException {
		// create network
		network = rootResourceAdministration.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		Network networkWrapper = new Network(network, serviceProvider);

		// create ofswitch inside network
		Endpoint endpoint = new Endpoint(new URI("http://www.myfakeswitch.com"));
		ofSwitch = networkWrapper.createResource(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint));
		NetworkSubResource ofSwitchWrapper = new NetworkSubResource(ofSwitch, serviceProvider);

		ofSwitchPort1 = ofSwitchWrapper.createPort();
		ofSwitchPort2 = ofSwitchWrapper.createPort();

		PortResourceWrapper ofSwitchPort1Wrapper = new PortResourceWrapper(ofSwitchPort1, serviceProvider);
		PortResourceWrapper ofSwitchPort2Wrapper = new PortResourceWrapper(ofSwitchPort2, serviceProvider);
		ofSwitchPort1Wrapper.setAttribute(AttributeStore.RESOURCE_EXTERNAL_ID, OFSWITCH_PORT1_EXT_ID);
		ofSwitchPort2Wrapper.setAttribute(AttributeStore.RESOURCE_EXTERNAL_ID, OFSWITCH_PORT2_EXT_ID);

	}

	@Test
	public void modelReaderTest() throws URISyntaxException, InstantiationException, IllegalAccessException, CapabilityNotFoundException {

		IResourceModelReader modelReader = serviceProvider.getCapability(network, IResourceModelReader.class);
		Assert.assertNotNull("Network should contain a IResourceModelReader capability.", modelReader);

		ResourceModelWrapper networkModel = modelReader.getResourceModel();

		// Network resource asserts
		Assert.assertNotNull("Network model should not be null.", networkModel);
		Assert.assertEquals("Network resource reporesentation in model should contain the same id as the real network", network.getId(),
				networkModel.getId());
		Assert.assertEquals("Network resource reporesentation in model should contain the same type as the network", network.getDescriptor()
				.getSpecification().getType().toString(), networkModel.getType());
		Assert.assertEquals("Network model representation should contain 1 subresource.", 1, networkModel.getResources().size());

		// Switch resource asserts
		ResourceModelWrapper switchModel = networkModel.getResources().get(0);
		Assert.assertNotNull("Switch model should not be null.", switchModel);
		Assert.assertEquals("Switch resource reporesentation in model should contain the same id as the real switch", ofSwitch.getId(),
				switchModel.getId());
		Assert.assertEquals("Switch resource reporesentation in model should contain the same type as the real switch", ofSwitch.getDescriptor()
				.getSpecification().getType().toString(), switchModel.getType());
		Assert.assertEquals("Switch model representation should contain 2 subresources.", 1, networkModel.getResources().size());

		// Switch ports asserts
		ResourceModelWrapper port1Model = switchModel.getResources().get(0);
		ResourceModelWrapper port2Model = switchModel.getResources().get(1);
		Assert.assertFalse("Both model ports should be different.", port1Model.equals(port2Model));

		Assert.assertTrue("First model port should be a representation of a real switch port.",
				port1Model.getId().equals(ofSwitchPort1.getId()) || port1Model.getId().equals(ofSwitchPort2.getId()));
		Assert.assertTrue("Second model port should be a representation of a real switch port.",
				port2Model.getId().equals(ofSwitchPort1.getId()) || port2Model.getId().equals(ofSwitchPort2.getId()));
		Assert.assertFalse("Both model ports should contain different ids.", port1Model.getId().equals(port2Model.getId()));

		Assert.assertEquals("First model port should be of type port.", "port", port1Model.getType());
		Assert.assertEquals("Second model port should be of type port.", "port", port2Model.getType());

		Assert.assertTrue("First model port should not contain subresources.", port1Model.getResources().isEmpty());
		Assert.assertTrue("Second model port should not contain subresources.", port2Model.getResources().isEmpty());

		if (port1Model.getId().equals(ofSwitchPort1.getId())) {
			Assert.assertEquals("First model port should contain the expected external port id. ", OFSWITCH_PORT1_EXT_ID, port1Model.getExternalId());
			Assert.assertEquals("Second model port should contain the expected external port id. ", OFSWITCH_PORT2_EXT_ID, port2Model.getExternalId());
		}
		else {
			Assert.assertEquals("First model port should contain the expected external port id. ", OFSWITCH_PORT2_EXT_ID, port1Model.getExternalId());
			Assert.assertEquals("Second model port should contain the expected external port id. ", OFSWITCH_PORT1_EXT_ID, port2Model.getExternalId());

		}

	}
}
