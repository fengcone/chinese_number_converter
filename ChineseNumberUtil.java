package com.fengcone.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChineseNumberUtil {

	public static String convertString(String string) {
		StringBuilder builder = new StringBuilder();
		List<NumberEnum> tempList = new ArrayList<>();
		boolean isSimple = true;
		for (int i = 0; i < string.length(); i++) {
			NumberEnum numberEnum = NumberEnum.getByChar(string.charAt(i));
			if (numberEnum == null && tempList.size() != 0) {
				if (isSimple) {
					builder.append(convert2Simple(tempList));
				} else if (tempList.size() == 1 && tempList.get(0).type > 2) {
					builder.append(string.charAt(i - 1));
				} else {
					builder.append(convert2Number(tempList));
				}
				tempList = new ArrayList<>();
			}
			if (numberEnum == null) {
				isSimple = true;
				builder.append(string.charAt(i));
				continue;
			}
			if (numberEnum.type > 1 && isSimple) {
				isSimple = false;
				if (tempList.size() >= 2) {
					builder.append(convert2Simple(tempList.subList(0, tempList.size() - 1)));
					NumberEnum temp = tempList.get(tempList.size() - 1);
					tempList = new ArrayList<>();
					tempList.add(temp);
				}
			}
			tempList.add(numberEnum);
			if (i == string.length() - 1) {
				if (isSimple) {
					builder.append(convert2Simple(tempList));
				} else if (tempList.size() == 1 && tempList.get(0).type > 2) {
					builder.append(string.charAt(i));
				} else {
					builder.append(convert2Number(tempList));
				}
			}
		}
		return builder.toString();
	}

	private static String convert2Simple(List<NumberEnum> tempList) {
		StringBuilder builder = new StringBuilder();
		for (NumberEnum numberEnum : tempList) {
			builder.append(numberEnum.value);
		}
		return builder.toString();
	}

	private static Long convert2Number(List<NumberEnum> numberList) {
		List<NumberEnum> tempList = new ArrayList<ChineseNumberUtil.NumberEnum>();
		Long result = 0L;
		for (int i = 0; i < numberList.size(); i++) {
			NumberEnum numberEnum = numberList.get(i);
			if (numberEnum.type == 4) {
				if (result >= NumberEnum.TEN_THOUSAND.value) {
					result = (result + convert2BasicNum(tempList)) * numberEnum.value;
				} else {
					result = result + convert2BasicNum(tempList) * numberEnum.value;
				}
				tempList = new ArrayList<>();
			} else {
				tempList.add(numberList.get(i));
			}
			if (i == numberList.size() - 1) {
				result = result + convert2BasicNum(tempList);
			}
		}
		return result;
	}

	private static Long convert2BasicNum(List<NumberEnum> numberList) {
		NumberEnum last = NumberEnum.ONE;
		Long result = 0L;
		for (int i = 0; i < numberList.size(); i++) {
			NumberEnum numberEnum = numberList.get(i);
			if (numberEnum.type == 2 || numberEnum.type == 3) {
				result = result + last.value * numberEnum.value;
			}
			if (i == numberList.size() - 1 && numberEnum.type == 1) {
				if (last == NumberEnum.ZERO || numberList.size() == 1) {
					result = result + numberEnum.value;
				} else {
					result = result + (numberEnum.value * last.value) / 10;
				}
			}
			last = numberEnum;
		}
		return result;
	}

	public static void main(String[] args) {
		String number = "就这么滴吧规范呐23445十1万";
		System.out.println(ChineseNumberUtil.convertString(number));
	}

	enum NumberEnum {
		ZERO("零〇", 0L, 1), ONE("一壹", 1L, 1), TWO("二两贰", 2L, 1), THREE("三叁", 3L, 1), FOUR("四肆", 4L, 1), FIVE("五伍", 5L, 1), SIX("六陆", 6L, 1), SEVEN("七柒", 7L, 1), EIGHT("八捌", 8L, 1), NINE("九玖", 9L, 1), TEN("十拾", 10L, 2), HUNDRED("百佰", 100L, 3), THOUSAND("千仟", 1000L, 3), TEN_THOUSAND("万萬", 10000L,
				4), HUNDRED_MILLION("亿億", 100000000L, 4);

		String key;
		Long value;
		Integer type;
		private static Map<Character, NumberEnum> map = new HashMap<>();

		public static NumberEnum getByChar(Character character) {
			map = new HashMap<>();
			for (NumberEnum number : NumberEnum.values()) {
				for (int i = 0; i < number.key.length(); i++) {
					map.put(number.key.charAt(i), number);
				}
				if (number.type == 1) {
					map.put(number.value.toString().charAt(0), number);
				}
			}
			return map.get(character);
		}

		private NumberEnum(String key, Long value, Integer type) {
			this.key = key;
			this.value = value;
			this.type = type;
		}
	}
}
