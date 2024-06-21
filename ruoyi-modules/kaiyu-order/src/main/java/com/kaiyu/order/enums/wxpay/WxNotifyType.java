package com.kaiyu.order.enums.wxpay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WxNotifyType {

	/**
	 * 支付通知
	 */
	NATIVE_NOTIFY("/order/wxpay/native/notify"),

	JSAPI_NOTIFY("/order/wxpay/jsapi/notify"),

	/**
	 * 支付通知V2
	 */
	NATIVE_NOTIFY_V2("/order/wx-pay-v2/native/notify"),


	/**
	 * 退款结果通知
	 */
	REFUND_NOTIFY("/order/wxpay/refunds/notify");

	/**
	 * 类型
	 */
	private final String type;
}
