package io.github.toolkit.config.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class NetworkUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);
	private static final String[] ignoredNetworkInterfaceNameKeyword = {
			" Loopback ", ",LOOPBACK,", " Virtual ", " VPN ", "TeamViewer ", "Bluetooth "};

	private static boolean ignoredNetworkInterface(String displayName) {
		if (StringUtils.hasText(displayName)) {
			for (String kw : ignoredNetworkInterfaceNameKeyword) {
				if (displayName.startsWith(kw.trim()) || displayName.indexOf(kw) > 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getServerIPv4() {
        String candidateAddress = null;
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                String nicName = nic.getName();
                if (ignoredNetworkInterface(nic.getDisplayName())) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String address = inetAddresses.nextElement().getHostAddress();
                    if (isIpv4(address)) {
	                    if (nicName.startsWith("eth0") || nicName.startsWith("en0")) {
	                        return address;
	                    }
	                    if (nicName.endsWith("0") || candidateAddress == null) {
	                        candidateAddress = address;
	                    }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Cannot resolve local network address", e);
        }
        if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("当前服务地址: " + candidateAddress);
		}
        return candidateAddress == null ? "127.0.0.1" : candidateAddress;
    }
	
	private static boolean isIpv4(String ipAddress) {  
		  
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."  
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";  
  
        Pattern pattern = Pattern.compile(ip);  
        Matcher matcher = pattern.matcher(ipAddress);  
        return matcher.matches();  
  
    }
}
