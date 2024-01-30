package com.jn.agileway.web.prediate;

import com.jn.agileway.http.rr.RR;
import com.jn.langx.util.function.Predicate;

/**
 * 用于进行某种匹配。在HttpRequest匹配过程中，很常见的一种情况是，在多个值中任选一个进行匹配。
 * <p>
 * 所以对于 HttpRequestPredicate实现中，通常对于存在多值的情况，进行 or 匹配，不存在多值的情况，进行 equals 匹配
 *
 * @see HttpRequestPredicateGroup 用于对多个Predicate进行 and 操作
 */
public interface HttpRequestPredicate extends Predicate<RR> {
    @Override
    boolean test(RR holder);
}
