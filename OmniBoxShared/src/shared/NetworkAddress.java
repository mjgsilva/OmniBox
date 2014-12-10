package shared;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/** Network Address class.
 *
 * This class is intended to overcome the problem of machines with different
 * addresses (i.e. lo:127.0.0.1, wlan0:192.168.1.4, eth0:192.168.1.5)
 *
 * Created by OmniBox on 04-12-2014.
 */
public final class NetworkAddress {
    /**
     * Returns the address accordingly to the interface adapter connected via ethernet (eth)
     * or wireless (wlan). Only compatible with IPv4.
     *
     * Returns null if no valid address was find.
     *
     * @return
     * @throws SocketException
     */
    public static InetAddress getAddressAsString() throws SocketException {
        Enumeration<NetworkInterface> networkInterfacesEnum = NetworkInterface.getNetworkInterfaces();

        for (; networkInterfacesEnum.hasMoreElements();) {
            NetworkInterface it = networkInterfacesEnum.nextElement();

            Enumeration<InetAddress> inetAddressEnumeration = it.getInetAddresses();
            for (; inetAddressEnumeration.hasMoreElements();) {
                InetAddress addr = inetAddressEnumeration.nextElement();
                if (!addr.isLoopbackAddress() && addr instanceof Inet4Address)
                    return addr;
            }
        }

        return null;
    }

    public static InetAddress getNetworkInetAddr() {
        NetworkInterface en0 = null;
        try {
            en0 = NetworkInterface.getByName("en0");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Enumeration<InetAddress> adrs = en0.getInetAddresses();
        while (adrs.hasMoreElements()) {
            InetAddress adr = adrs.nextElement();
            if (adr instanceof Inet4Address)
                return adr;
        }
        return null;
    }
}
