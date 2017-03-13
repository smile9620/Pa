package com.bg.constant;

/**
 * Created by zjy on 2017-03-03.
 */

public class InBodyBluetooth {

    public static byte[] send1= {(byte) 0xAA, (byte) 0xAA, (byte) 0x09, (byte) 0x20, (byte) 0x36,
            (byte) 0x36, (byte) 0x36, (byte) 0x36, (byte) 0x36, (byte) 0x36,
            (byte) (byte) 0x36, (byte) 0x36, (byte) 0xFF};
    public static byte[] send2 = {(byte) 0xAA, (byte) 0xAA, (byte) 0x2A, (byte) 0x22, (byte) 0x33,
            (byte) 0x34, (byte) 0x31, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x31,
            (byte) 0x39, (byte) 0x38, (byte) 0x37, (byte) 0x30, (byte) 0x36, (byte) 0x31,
            (byte) 0x31, (byte) 0x35, (byte) 0x30, (byte) 0x33, (byte) 0x41, (byte) 0xD6,
            (byte) 0xEE, (byte) 0xB8, (byte) 0xF0, (byte) 0xC1, (byte) 0xC1, (byte) 0x45,
            (byte) 0x45, (byte) 0x01, (byte) 0x31, (byte) 0x38, (byte) 0x32, (byte) 0x30,
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x36, (byte) 0x31, (byte) 0x31,
            (byte) 0x01, (byte) 0x31, (byte) 0x38, (byte) 0x30, (byte) 0xFF};
    public static byte[] send3 = {(byte) 0xAA, (byte) 0xAA, (byte) 0x01, (byte) 0x24, (byte) 0xFF};
    public static byte[] send4 = {(byte) 0xAA, (byte) 0xAA, (byte) 0x01, (byte) 0x26, (byte) 0xFF};

//    AA AA 09 20 36 36 36 36 36 36 36 36 FF
//    AA AA 2A 22 33 34 31 32 32 32 31 39 38 37 30 36 31 31 35 30 33 41 D6 EE B8 F0 C1 C1 45 45 01 31 38 32 30 30 30 30 36 31 31 01 31 38 30 FF
//    AA AA 2A 22 33 34 31 32 32 32 31 39 38 37 30 36 31 31 35 30
//    AA AA 01 24 FF
//    AA AA 01 26 FF
}
