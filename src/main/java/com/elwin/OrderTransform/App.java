package com.elwin.OrderTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.elwin.OrderTransform.OrderDataTransform.OrderDataTemplate;
import com.elwin.OrderTransform.OrderDataTransform.OrderItem;
import com.google.gson.Gson;

/**
 * Hello world!
 * 
 */
public class App {

	public static void main(String[] args) {
		ChannelBuffer buffer = ChannelBuffers.hexDump("ac12ee02");
		ChannelBuffer bu2 = ChannelBuffers.dynamicBuffer(12);
		System.out.println("len:" + bu2.readableBytes());
		List<OrderItem> items = new ArrayList<OrderItem>();

		OrderItem item1 = new OrderItem();
		item1.key = "result";
		item1.size = 2;
		items.add(item1);

		OrderItem item2 = new OrderItem();
		item2.key = "workStatus";
		item2.size = 1;
		items.add(item2);

		OrderItem item3 = new OrderItem();
		item3.key = "len";
		item3.size = 2;
		items.add(item3);

		OrderItem item4 = new OrderItem();
		item4.key = "childArr";
		item4.size = 3;
		item4.step_count_key = "len";
		item4.childList = new ArrayList<OrderItem>();
		items.add(item4);

		OrderItem item41 = new OrderItem();
		item41.key = "workStatus1";
		item41.size = 1;
		item4.childList.add(item41);

		OrderItem item42 = new OrderItem();
		item42.key = "workStatus2";
		item42.size = 1;
		item4.childList.add(item42);

		OrderItem item43 = new OrderItem();
		item43.key = "workStatus3";
		item43.size = 1;
		item4.childList.add(item43);

		OrderItem item5 = new OrderItem();
		item5.key = "tail";
		item5.size = 5;
		items.add(item5);

		System.err.println("json:" + new Gson().toJson(items));

		ChannelBuffer testBuf = ChannelBuffers
				.hexDump("00000100020102030203012211334455");
		Map decodeMap = OrderDataTransform.decodeWithBuffers(testBuf, items);
		System.out.println("map:" + decodeMap);
		OrderDataTemplate template = new OrderDataTemplate();
		template.template = items;
		System.err.println("-----------temple:" + new Gson().toJson(template));

		ChannelBuffer encodeBuf = OrderDataTransform.encodeBufferWithMap(
				decodeMap, items);
		System.out
				.println("encodebuf_str:" + ChannelBuffers.hexDump(encodeBuf));
	}
}
