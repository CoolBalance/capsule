/*
 * Capsule
 * Copyright (c) 2014, Parallel Universe Software Co. All rights reserved.
 * 
 * This program and the accompanying materials are licensed under the terms 
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package co.paralleluniverse.capsule;

import com.sun.jdmk.remote.cascading.CascadingService;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author pron
 */
public class CascadingCapsuleContainer extends MBeanCapsuleContainer {
    private final CascadingService cascade;

    public CascadingCapsuleContainer(MBeanServer mbeanServer) {
        this.cascade = mbeanServer != null ? new CascadingService(mbeanServer) : null;
    }

    public CascadingCapsuleContainer() {
        this(ManagementFactory.getPlatformMBeanServer());
    }

    @Override
    protected ProcessInfo getProcessInfo(String id) {
        return (ProcessInfo) super.getProcessInfo(id);
    }

    @Override
    protected CapsuleContainer.ProcessInfo mountProcess(Process p, String id) throws IOException, InstanceAlreadyExistsException {
        final JMXServiceURL connectorAddress = ProcessUtil.getLocalConnectorAddress(p, false);
        final String mountId = cascade != null ? cascade.mount(connectorAddress, null, ObjectName.WILDCARD, id) : null;
        return new ProcessInfo(p, connectorAddress, mountId);
    }

    protected static class ProcessInfo extends MBeanCapsuleContainer.ProcessInfo {
        final String mountPoint;

        public ProcessInfo(Process process, JMXServiceURL connectorAddress, String mountPoint) {
            super(process, connectorAddress);
            this.mountPoint = mountPoint;
        }
    }
}
