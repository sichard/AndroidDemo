package com.tct.launcher.weather;

/**
 * <br>类描述:天气数据状态枚举变量
 * <br>详细描述:
 *<br><b>Author sichard</b>
 *<br><b>Date 2016/7/7</b>
 */
public enum WeatherDataChangeEvent {
	/** 添加数据事件 */
	ADD,
	/** 删除数据事件 */
	DELETE,
	/** 更新数据事件 */
	UPDATE,
	/** 数据交换顺序 */
	SWAP,
	/** 自动定位城市添加事件 */
	FIXED_POSITION_ADD,
	/** 自动定位城市天气数据更新数事件 */
	FIXED_POSITION_UPDATE,
	/** 自动定位城市改变事件 */
	FIXED_POSITION_CHANGE;


	@Override
	public String toString() {
		switch (this) {
			case ADD :
				return "ADD";
			case DELETE :
				return "DELETE";
			case UPDATE :
				return "UPDATE";
			case SWAP :
				return "SWAP" ;
			case FIXED_POSITION_ADD :
				return "FIXED_POSITION_ADD" ;
			case FIXED_POSITION_UPDATE :
				return "FIXED_POSITION_UPDATE" ;
			case FIXED_POSITION_CHANGE :
				return "FIXED_POSITION_CHANGE" ;
			default :
				return null;
		}
	}
}
