package io.github.toolkit.cache.exception;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.UUID;

public class BaseException extends RuntimeException {
    protected String id;
    protected String message;
    protected Integer status;
    protected String defineCode;
    protected String chnDesc;
    protected String realClassName;

    protected BaseException(Integer status, String defineCode, String chnDesc) {
        this.status = status;
        this.defineCode = defineCode;
        this.chnDesc = chnDesc;
        this.setMessage("错误代码：" + this.status + ";错误描述：" + this.defineCode + "|" + chnDesc);
        this.initId();
    }

    private void initId() {
        this.id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message, Object... args) {
        this.message = MessageFormat.format(message, args);
    }

    public String getDefineCode() {
        return this.defineCode;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getChnDesc() {
        return this.chnDesc;
    }

    public static <T extends BaseException> T newException(T exception, String message, Object... args) {
        if (exception == null) {
            throw new RuntimeException("no exception instance specified");
        } else {
            try {
                Constructor<? extends BaseException> constructor = exception.getClass().getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                T newException = (T)constructor.newInstance(exception.getDefineCode());
                newException.setMessage(message, args);
                return newException;
            } catch (Throwable var5) {
                throw new RuntimeException("create exception instance fail : " + var5.getMessage(), var5);
            }
        }
    }

    public boolean codeEquals(BaseException e) {
        if (e == null) {
            return false;
        } else if (!e.getClass().equals(this.getClass())) {
            return false;
        } else {
            return e.getDefineCode().equals(this.getDefineCode());
        }
    }

    public BaseException upcasting() {
        if (this.getClass().equals(BaseException.class)) {
            return this;
        } else {
            BaseException superexception = new BaseException(this.status, this.defineCode, this.chnDesc);
            superexception.message = this.message;
            superexception.realClassName = this.getClass().getName();
            superexception.id = this.id;
            superexception.setStackTrace(this.getStackTrace());
            return superexception;
        }
    }

    public BaseException downcasting() {
        if (this.realClassName != null && !BaseException.class.getName().equals(this.realClassName)) {
            Class clz = null;

            try {
                clz = Class.forName(this.realClassName);
            } catch (Exception e) {
            }

            if (clz == null) {
                return this;
            } else {
                try {
                    Constructor constructor = clz.getDeclaredConstructor(String.class);
                    constructor.setAccessible(true);
                    BaseException newException = (BaseException)constructor.newInstance(this.defineCode);
                    newException.message = this.message;
                    newException.id = this.id;
                    newException.setStackTrace(this.getStackTrace());
                    return newException;
                } catch (Throwable throwable) {
                    return this;
                }
            }
        } else {
            return this;
        }
    }

    public String getRealClassName() {
        return this.realClassName == null ? this.getClass().getName() : this.realClassName;
    }

}
