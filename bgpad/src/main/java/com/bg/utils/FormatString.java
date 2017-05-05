package com.bg.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by zjy on 2017-04-18.
 */

public class FormatString {
    /**
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static int hex2Dec(char ch) {
        if (ch == '0')
            return 0;
        else if (ch == '1')
            return 1;
        else if (ch == '2')
            return 2;
        else if (ch == '3')
            return 3;
        else if (ch == '4')
            return 4;
        else if (ch == '5')
            return 5;
        else if (ch == '6')
            return 6;
        else if (ch == '7')
            return 7;
        else if (ch == '8')
            return 8;
        else if (ch == '9')
            return 9;
        else if (ch == 'a')
            return 10;
        else if (ch == 'A')
            return 10;
        else if (ch == 'B')
            return 11;
        else if (ch == 'b')
            return 11;
        else if (ch == 'C')
            return 12;
        else if (ch == 'c')
            return 12;
        else if (ch == 'D')
            return 13;
        else if (ch == 'd')
            return 13;
        else if (ch == 'E')
            return 14;
        else if (ch == 'e')
            return 14;
        else if (ch == 'F')
            return 15;
        else if (ch == 'f')
            return 15;
        else
            return -1;
    }
    /**
     * 16进制字符转串字符串
     */
    private static String hex2String(String hexStr) {
        if (null == hexStr || "".equals(hexStr) || (hexStr.length()) % 2 != 0) {
            return null;
        }

        int byteLength = hexStr.length() / 2;
        byte[] bytes = new byte[byteLength];

        int temp = 0;
        for (int i = 0; i < byteLength; i++) {
            temp = hex2Dec(hexStr.charAt(2 * i)) * 16
                    + hex2Dec(hexStr.charAt(2 * i + 1));
            bytes[i] = (byte) (temp < 128 ? temp : temp - 256);
        }
        try {
            hexStr = new String(bytes, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return hexStr;
    }

    public static String[] formateData(String[] data, int[][] start_end) {  //一组gbk16进制字符串的解析
//        int[][] in = new int[][]{{0,1},{2,2},{3,3},{4,11}};
        String[] allData = new String[start_end.length];
        for (int i = 0; i < start_end.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = start_end[i][0]; j <= start_end[i][1]; j++) {
                builder.append(data[j]);
            }
            allData[i] = hex2String(builder.toString()).trim();
        }
        return allData;
    }

    public static String formatResult(String str1, String str2, int type) { //测试数据高位和低位的解析
//       *1：十进制 = ((十六进制（高位）*256+十六进制（低位）)/10
//       *2：十进制 = ((十六进制（高位）*256+十六进制（低位）)/100
//       *3：十进制 = ((十六进制（高位）*256+十六进制（低位）)
        String result = "";
        switch (type) {
            case 1:
                result = (Integer.parseInt(str1,16) * 256 + Integer.parseInt(str2,16)) / 10 + "";
                break;
            case 2:
                result = (Integer.parseInt(str1,16) * 256 + Integer.parseInt(str2,16)) / 100 + "";
                break;
            case 3:
                result = (Integer.parseInt(str1,16) * 256 + Integer.parseInt(str2,16)) + "";
                break;
        }
        return result;
    }

    public static String[] formatStandard(String str1, String str2, String str3, int position) {
        String result[] = new String[2];
        int base = 17;
        if (Float.parseFloat(str3) < Float.parseFloat(str1)) { //正常范围的左值str1,右值str2,测量值str3
            result[0] = "低标准";
            result[1] = base + position * 2 + "";
        } else if (Float.parseFloat(str2) < Float.parseFloat(str3)) {
            result[0] = "超标准";
            result[1] = base * 5 + position * 2 + "";
        } else {
            result[0] = "正常";
            result[1] = base * 3 + position * 2 + "";
        }
        return result;
    }
}
