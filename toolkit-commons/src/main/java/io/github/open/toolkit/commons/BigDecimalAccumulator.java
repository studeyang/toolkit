package io.github.open.toolkit.commons;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0.0 2024/3/26
 */
public class BigDecimalAccumulator {

    @Getter
    private BigDecimal sum;

    public void add(BigDecimal item) {
        sum = sum.add(item);
    }

}
