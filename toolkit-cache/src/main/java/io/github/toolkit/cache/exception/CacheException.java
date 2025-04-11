package io.github.toolkit.cache.exception;

public class CacheException extends BaseException {
    public static CacheException OPERATE_TIMEOUT = new CacheException(98, "OPERATE-TIMEOUT", "操作超时");
    public static final CacheException PARAM_INVALID = new CacheException(97, "PARAM-INVALID", "参数无效");
    public static final CacheException UNKNOWN_SERVICE = new CacheException(96, "UNKNOWN-SERVICE", "未实现的接口");
    public static final CacheException UNKNOWN_SYSTEM_FAIL = new CacheException(95, "UNKNOWN-SYSTEM-FAIL", "网络异常!");
    public static final CacheException UNKNOWN_USER = new CacheException(94, "UNKNOWN-USER", "未知用户");
    public static final CacheException REDIS_FAIL = new CacheException(93, "REDIS-FAIL", "缓存系统异常");
    public static final CacheException SPRINGCONTEXTUTIL_FAIL = new CacheException(99, "SPRINGCONTEXTUTIL_FAIL", "spring文件中，没有注入 SpringContextUtil");
    public static final CacheException REFRESHCODE_FAIL = new CacheException(100, "REFRESHCODE_FAIL", "refreshcode不能为空！");

    protected CacheException(Integer status, String defineCode, String chnDesc) {
        super(status, defineCode, chnDesc);
    }

    public CacheException newInstance(String message, Object... args) {
        CacheException ex = new CacheException(this.status, this.defineCode, this.chnDesc);
        message = "错误代码：" + this.status + ";;错误描述：" + this.defineCode + "|" + this.chnDesc + ";; 详细信息：" + message;
        ex.setMessage(message, args);
        return ex;
    }
}
