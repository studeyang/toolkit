package io.github.toolkit.cache.util;

import lombok.Getter;

@Getter
public class IPRange {
    private IPAddress ipAddress = null;
    private IPAddress ipSubnetMask = null;
    private int extendedNetworkPrefix = 0;

    public IPRange(String range) {
        this.parseRange(range);
    }

    @Override
    public String toString() {
        return this.ipAddress.toString() + "/" + this.extendedNetworkPrefix;
    }

    final void parseRange(String range) {
        if (range == null) {
            throw new IllegalArgumentException("Invalid IP range");
        } else {
            int index = range.indexOf(47);
            String subnetStr = null;
            if (index == -1) {
                this.ipAddress = new IPAddress(range);
            } else {
                this.ipAddress = new IPAddress(range.substring(0, index));
                subnetStr = range.substring(index + 1);
            }

            try {
                if (subnetStr != null) {
                    this.extendedNetworkPrefix = Integer.parseInt(subnetStr);
                    if (this.extendedNetworkPrefix < 0 || this.extendedNetworkPrefix > 32) {
                        throw new IllegalArgumentException("Invalid IP range [" + range + "]");
                    }

                    this.ipSubnetMask = this.computeMaskFromNetworkPrefix(this.extendedNetworkPrefix);
                }
            } catch (NumberFormatException var5) {
                this.ipSubnetMask = new IPAddress(subnetStr);
                this.extendedNetworkPrefix = this.computeNetworkPrefixFromMask(this.ipSubnetMask);
                if (this.extendedNetworkPrefix == -1) {
                    throw new IllegalArgumentException("Invalid IP range [" + range + "]", var5);
                }
            }

        }
    }

    private int computeNetworkPrefixFromMask(IPAddress mask) {
        int result = 0;

        int tmp;
        for(tmp = mask.getIPAddress(); (tmp & 1) == 1; tmp >>>= 1) {
            ++result;
        }

        return tmp != 0 ? -1 : result;
    }

    public static String toDecimalString(String inBinaryIpAddress) {
        StringBuilder decimalIp = new StringBuilder();
        String[] binary = new String[4];
        int i = 0;

        for(int c = 0; i < 32; ++c) {
            binary[c] = inBinaryIpAddress.substring(i, i + 8);
            int octet = Integer.parseInt(binary[c], 2);
            decimalIp.append(octet);
            if (c < 3) {
                decimalIp.append('.');
            }

            i += 8;
        }

        return decimalIp.toString();
    }

    private IPAddress computeMaskFromNetworkPrefix(int prefix) {
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < 32; ++i) {
            if (i < prefix) {
                str.append("1");
            } else {
                str.append("0");
            }
        }

        String decimalString = toDecimalString(str.toString());
        return new IPAddress(decimalString);
    }

    public boolean isIPAddressInRange(IPAddress address) {
        if (this.ipSubnetMask == null) {
            return this.ipAddress.equals(address);
        } else {
            int result1 = address.getIPAddress() & this.ipSubnetMask.getIPAddress();
            int result2 = this.ipAddress.getIPAddress() & this.ipSubnetMask.getIPAddress();
            return result1 == result2;
        }
    }
}