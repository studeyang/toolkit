package io.github.toolkit.cache.util;

import io.github.toolkit.cache.dto.MonitorMemoryDto;
import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

@UtilityClass
public class MemoryUtil {

    public static MonitorMemoryDto displayMemory() {
        MonitorMemoryDto dto = new MonitorMemoryDto();

        DecimalFormat df = new DecimalFormat("0.00");
        long max = Runtime.getRuntime().maxMemory();
        dto.setMaxMemory(df.format(max / 1048576.0F) + " MB");

        long total = Runtime.getRuntime().totalMemory();
        dto.setTotalMemory(df.format(total / 1048576.0F) + " MB");

        long free = Runtime.getRuntime().freeMemory();
        dto.setFreeMemory(df.format(free / 1048576.0F) + " MB");

        dto.setMaxUseMemory(df.format((max - total + free) / 1048576.0F) + " MB");
        return dto;
    }
}
