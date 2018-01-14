package root.util;


import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Map;

//集合工具类
public final class CollectionUtil {
	
	//	判断Collection是否为空
	public static boolean isEmpty(Collection<?> collection){
		return CollectionUtils.isEmpty(collection);
	}
	
	//	判断Collection是否非空
	public static boolean isNotEmpty(Collection<?> collection){
		return  !collection.isEmpty();
	}
	
	//	判断Map是否为空
	public static boolean isEmpty(Map<?,?> map){
		return map.isEmpty();
	}
	
	//	判断Map是否非空
	public static boolean isNotEmpty(Map<?,?> map){
		return !map.isEmpty();
	}
	
}
