package io.github.toolkit.cache.dto;

import lombok.Data;

@Data
public class MonitorMemoryDto {
    private String totalMemory;
    private String freeMemory;
    private String maxMemory;
    private String maxUseMemory;

    public String toHtmlString() {
        return "<b>当前系统大致内存情况  : </b><br />已分配内存 = " + this.totalMemory + "<br /> 已分配内存中剩余空间 = " + this.freeMemory + "<br />最大内存 = " + this.maxMemory + "<br />最大可用内存  = " + this.maxUseMemory;
    }
}
