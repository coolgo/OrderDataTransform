package com.elwin.OrderTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.google.gson.Gson;

/**
 * 统一指令解析文件
 * 
 * @author elwin
 * 
 */
public class OrderDataTransform {
	public static class OrderItem {
		/**
		 * 指令项的key,类似于name
		 */
		public String key;

		/**
		 * 1.单个指令的大小<br>
		 * 2.循环体的大小,类似于数组循环，step_count_key 一定不为空<br>
		 */
		public int size;

		/**
		 * 存在循环
		 */
		public String step_count_key;

		/**
		 * 如果存在子结构则需要初始化
		 */
		List<OrderItem> childList;
	}

	public static class OrderDataTemplate {
		List<OrderItem> template;
	}

	/**
	 * 
	 * 最原始的指令解析工具<br>
	 * 提供强大的指令解析功能，支持循环数组的解析方式<br>
	 * 
	 * 
	 * @param buffers
	 * @param OrderDataList
	 * @return
	 */
	public static Map<String, Object> decodeWithBuffers(ChannelBuffer buffers,
			List<OrderItem> orderDataList) {
		Map<String, Object> decodeMap = new HashMap<String, Object>();
		int len = buffers.readableBytes();
		int index = 0;
		for (OrderItem orderItem : orderDataList) {
			if (orderItem.childList != null && orderItem.childList.size() > 0) {
				int step_count = (Integer) decodeMap
						.get(orderItem.step_count_key); // 当前step的量
				int step_total_size = step_count * orderItem.size;
				int currentIndex = index + step_total_size;
				if (currentIndex > len) {
					break;
				} else {
					List<Object> childList = new ArrayList<Object>();
					while (step_count-- > 0) {
						ChannelBuffer stepBufs = buffers
								.readBytes(orderItem.size);
						index += orderItem.size;
						Map<String, Object> itemMap = decodeWithBuffers(
								stepBufs, orderItem.childList);
						childList.add(itemMap);
					}
					decodeMap.put(orderItem.key, childList);
				}
			} else {
				Object value;
				switch (orderItem.size) {
				case 1:
					int value1 = buffers.readByte() & 0xff;
					value = value1;
					break;
				case 2:
					int value2 = buffers.readShort() & 0xffff;
					value = value2;
					break;
				case 4:
					int value3 = buffers.readInt() & 0xffffffff;
					value = value3;
					break;
				default:
					ChannelBuffer buffer = buffers.readBytes(orderItem.size);
					value = ChannelBuffers.hexDump(buffer);
					break;
				}
				index += orderItem.size;
				decodeMap.put(orderItem.key, value);
			}
		}
		return decodeMap;
	}

	/**
	 * eg:
	 * {"template":[{"key":"result","size":2},{"key":"workStatus","size":1},{
	 * "key":"len","size":2},{"key":"childArr","size":3,"step_count_key":"len",
	 * "childList"
	 * :[{"key":"workStatus1","size":1},{"key":"workStatus2","size":1}
	 * ,{"key":"workStatus3","size":1}]}]}
	 * 
	 * @param buffer
	 * @param jsonTemplate
	 * @return
	 */
	public static Map<String, Object> decodeBufferByJsonTemplate(
			ChannelBuffer buffer, String jsonTemplate) {
		List<OrderItem> template = new Gson().fromJson(jsonTemplate,
				OrderDataTemplate.class).template;
		return decodeWithBuffers(buffer, template);
	}

	/**
	 * -------------------------------------------------组装----------------------
	 * ------------
	 * 
	 */
	/**
	 * 指令组装流程
	 * 
	 * @param encodeMap
	 * @param orderDataList
	 * @return
	 */
	public static ChannelBuffer encodeBufferWithMap(
			Map<String, Object> encodeMap, List<OrderItem> orderDataList) {
		ChannelBuffer buffers = ChannelBuffers.dynamicBuffer();
		for (OrderItem orderItem : orderDataList) {
			if (orderItem.childList != null && orderItem.childList.size() > 0) {
				List<Map> childList = (List<Map>) encodeMap.get(orderItem.key);
				for (Map objMap : childList) {
					ChannelBuffer buf = encodeBufferWithMap(objMap,
							orderItem.childList);
					buffers.writeBytes(buf);
				}
			} else {
				Object obj = encodeMap.get(orderItem.key);
				switch (orderItem.size) {
				case 1:
					int value1 = 0;
					if (obj instanceof Double) {
						value1 = ((Double) obj).intValue();
					} else {
						value1 = ((Integer) obj).intValue();
					}
					buffers.writeByte(value1);
					break;
				case 2:
					int value2 = 0;
					if (obj instanceof Double) {
						value2 = ((Double) obj).intValue();
					} else {
						value2 = ((Integer) obj).intValue();
					}
					buffers.writeShort(value2);
					break;
				case 4:
					int value3 = 0;
					if (obj instanceof Double) {
						value3 = ((Double) obj).intValue();
					} else {
						value3 = ((Integer) obj).intValue();
					}
					buffers.writeInt(value3);
					break;
				default:
					String bufStr = (String) encodeMap.get(orderItem.key);
					ChannelBuffer value4 = ChannelBuffers.hexDump(bufStr);
					buffers.writeBytes(value4);
					break;
				}
			}
		}

		return buffers;
	}

	/**
	 * 
	 * @param encodeMap
	 * @param jsonTemplate
	 * @return
	 */
	public static ChannelBuffer encodeBufferWithTemplate(
			Map<String, Object> encodeMap, String jsonTemplate) {
		List<OrderItem> template = new Gson().fromJson(jsonTemplate,
				OrderDataTemplate.class).template;
		return encodeBufferWithMap(encodeMap, template);
	}

	/**
	 * 获取指令解析的template
	 * 
	 * @param jsonTemplate
	 * @return
	 */
	public static List<OrderItem> getOrderTemplateFromJson(String jsonTemplate) {
		return new Gson().fromJson(jsonTemplate, OrderDataTemplate.class).template;
	}

}
