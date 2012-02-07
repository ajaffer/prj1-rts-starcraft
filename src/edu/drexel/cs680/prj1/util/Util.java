package edu.drexel.cs680.prj1.util;

import java.util.Collection;
import java.util.Map;

public class Util {
	public static String toString(Collection<?> list) {
		if (list.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Object item : list){
			if (item instanceof Collection) {
				builder.append(toString((Collection<?>)item));
				builder.append("\t");
			}
			builder.append(item);
			builder.append("\t");
		}
		String str = builder.toString();
		return str;
	}

	public static <K,V> String toString(Map<K,V> map) {
		if (map.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
//		for (Map.Entry<K,V> e : map.entrySet()){
//			builder.append(String.format("K:%s,V:%s\t", e.getKey().toString(), e.getValue().toString()));
//		}
		String str = builder.toString();
		return str;
	}
}
