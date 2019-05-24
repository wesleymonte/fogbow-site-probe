/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: Monitor - Client
 * <p>
 * Repository: https://github.com/eubr-atmosphere/tma-framework
 * License: https://github.com/eubr-atmosphere/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.monitor.client;

import eu.atmosphere.tmaf.monitor.message.Message;

/**
 *
 * <p>
 * <p>
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class SynchronousClient extends MonitorClient {

    public SynchronousClient() {
        super();
    }

    public SynchronousClient(String endpoint) {
        super(endpoint);
    }

    @Override
    public int dispatch(Message message) {
        return this.post(message);
    }
}
