package io.github.toolkit.cache.util;

import java.util.NoSuchElementException;

public class IPAddress implements Cloneable {
    protected int ipAddress = 0;

    public IPAddress(String ipAddressStr) {
        this.ipAddress = this.parseIPAddress(ipAddressStr);
    }

    public IPAddress(int address) {
        this.ipAddress = address;
    }

    public final int getIPAddress() {
        return this.ipAddress;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int temp = this.ipAddress & 255;
        result.append(temp);
        result.append(".");
        temp = this.ipAddress >> 8 & 255;
        result.append(temp);
        result.append(".");
        temp = this.ipAddress >> 16 & 255;
        result.append(temp);
        result.append(".");
        temp = this.ipAddress >> 24 & 255;
        result.append(temp);
        return result.toString();
    }

    public final boolean isClassA() {
        return (this.ipAddress & 1) == 0;
    }

    public final boolean isClassB() {
        return (this.ipAddress & 3) == 1;
    }

    public final boolean isClassC() {
        return (this.ipAddress & 7) == 3;
    }

    final int parseIPAddress(String ipAddressStr) {
        int result = 0;
        if (ipAddressStr == null) {
            throw new IllegalArgumentException();
        } else {
            try {
                String tmp = ipAddressStr;
                int offset = 0;

                int number;
                for(number = 0; number < 3; ++number) {
                    int index = tmp.indexOf(46);
                    if (index == -1) {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }

                    String numberStr = tmp.substring(0, index);
                    int indexNumber = Integer.parseInt(numberStr);
                    if (indexNumber < 0 || indexNumber > 255) {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }

                    result += indexNumber << offset;
                    offset += 8;
                    tmp = tmp.substring(index + 1);
                }

                if (tmp.length() > 0) {
                    number = Integer.parseInt(tmp);
                    if (number >= 0 && number <= 255) {
                        result += number << offset;
                        this.ipAddress = result;
                        return result;
                    } else {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                }
            } catch (NoSuchElementException var9) {
                throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]", var9);
            } catch (NumberFormatException var10) {
                throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]", var10);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.ipAddress;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof IPAddress && this.ipAddress == ((IPAddress)another).ipAddress;
    }
}