package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.exceptions.ApplicationActivationException;

public class SampleMgmtCapability implements ISampleMgmtCapability {

	private List<SampleResource>	resources	= new ArrayList<SampleResource>();

	@Override
	public void addSampleResource(SampleResource resource) {
		resources.add(resource);
	}

	@Override
	public void removeSampleResource(SampleResource resource) {
		resources.remove(resource);

	}

	@Override
	public List<SampleResource> getSampleResources() {
		return new ArrayList<SampleResource>(resources);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
