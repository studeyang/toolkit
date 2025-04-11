package io.github.toolkit.cache.dto;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PageInfo<E> implements Serializable {
    private static final long serialVersionUID = -7457822353324524388L;
    private List<E> content;
    private int pageNo = 1;
    private int pageSize = 10;
    public static final int defaultPageSize = 10;
    private int total;
    private int count;

    public List<E> getContent() {
        return this.content;
    }

    public void setContent(List<E> content) {
        this.content = content;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPage() {
        if (this.count == 0) {
            return 0;
        } else {
            int pageNum = this.count / this.pageSize;
            if (this.count % this.pageSize > 0) {
                ++pageNum;
            }

            this.total = pageNum;
            return this.total;
        }
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return !(object instanceof PageInfo) ? false : EqualsBuilder.reflectionEquals(this, object);
    }
}
