package com.musinsa.pointapiserver.code;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @class Name : PointType.java
 * @Description : 포인트 유형 공통코드
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
public enum PointType {
    USE("USE","사용",value -> value.multiply(BigDecimal.valueOf(-1))),
    SAVE("SAVE","적립",value -> value),
    CANCLE("CANCEL","사용취소",value -> value.abs()),
    EXPIRE("EXPIRE","소멸",  value -> value.multiply(BigDecimal.valueOf(-1)))
    ;

    private final String code;
    private final String name;
    private Function<BigDecimal, BigDecimal> expression;
    
    
	private PointType(String code, String name, Function<BigDecimal, BigDecimal> expression) {
		this.code = code;
		this.name = name;
		this.expression = expression;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
    
	public BigDecimal calculate(BigDecimal value) {
		return this.expression.apply(value);
	}
    
}
