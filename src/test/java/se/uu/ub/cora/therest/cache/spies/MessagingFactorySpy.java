/*
 * Copyright 2025 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.therest.cache.spies;

import se.uu.ub.cora.messaging.MessageListener;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessageSender;
import se.uu.ub.cora.messaging.MessagingFactory;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MessagingFactorySpy implements MessagingFactory {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public MessagingFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorTopicMessageSender", MessageSenderSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorTopicMessageListener", MessageListenerSpy::new);
	}

	@Override
	public MessageSender factorTopicMessageSender(MessageRoutingInfo messagingRoutingInfo) {
		return (MessageSender) MCR.addCallAndReturnFromMRV("messagingRoutingInfo",
				messagingRoutingInfo);
	}

	@Override
	public MessageListener factorTopicMessageListener(MessageRoutingInfo messagingRoutingInfo) {
		return (MessageListener) MCR.addCallAndReturnFromMRV("messagingRoutingInfo",
				messagingRoutingInfo);
	}

}
