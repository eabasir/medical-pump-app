package com.ansari.medicalpump.network.utils;

import java.security.PublicKey;

/**
 * Created by Eabasir on 7/13/2017.
 */

public class PacketUtility {

    /**
     * this is used to convert two bytes into int address.
     * since LSB is received first, 1st and 2nd bytes must be replaced
     *
     * @param data
     * @return
     */
    public static int convertTwoBytesToIntAddressViceVerca(byte[] data) {

        byte[] temp = new byte[2];
        temp[1] = data[0];
        temp[0] = data[1];
        int res = ((temp[0] & 0xff) << 8) | (temp[1] & 0xff);
        return res;
    }


    public static byte[] convertIntToTwoByte(int value) {

        byte[] result = new byte[2];
        if (value < 255) {
            result[0] = 0x00;
            result[1] = (byte) value;
        } else {
            result[0] = (byte) (value & 0xFF);
            result[1] = (byte) ((value >> 8) & 0xFF);
        }
        return result;


    }

    public static int unsignedIntFromByteArray(byte[] bytes) {
        int res = 0;
        if (bytes == null)
            return res;

        for (int i = 0; i < bytes.length; i++) {
            res = (res * 10) + ((bytes[i] & 0xff));
        }
        return res;
    }

//    public static byte[] IntToTwoBytes(int i)
//    {
//        byte[] res = new byte[2];
//        res[0] = (byte)i;
//        res[1] = (byte)(i >> 8);
//
//        return res;
//
//    }
//
//
//    public static byte[] IntToByteArray(int i)
//    {
//        byte[] intBytes = BitConverter.GetBytes(i);
//        if (BitConverter.IsLittleEndian)
//            Array.Reverse(intBytes);
//
//        return intBytes;
//    }
//
//    public static string ByteArrayToHexString(byte[] ba)
//    {
//        StringBuilder hex = new StringBuilder(ba.Length * 2);
//        foreach (byte b in ba)
//        hex.AppendFormat("{0:x2}", b);
//        return hex.ToString();
//    }
//
//    public static byte[] HexStringToByteArray(string hex)
//    {
//        if (hex.Length == 2)
//            hex = "00" + hex;
//
//        else if (hex.Length < 4)
//            hex = "0" + hex;
//
//        return Enumerable.Range(0, hex.Length)
//                .Where(x => x % 2 == 0)
//        .Select(x => Convert.ToByte(hex.Substring(x, 2), 16))
//        .ToArray();
//
//
//
//    }
//
//    /// <summary>
//    /// get packet seperator as byte array
//    /// </summary>
//    /// <returns>packet seperator byte array</returns>
//    private static byte[] getPacketSeperator()
//    {
//        byte[] result= Encoding.ASCII.GetBytes(AbstractPacket.seperator_text);
//        return result;
//    }
//
//
//    /// <summary>تبدیل
//    ///
//    /// Search for seperator indexes in data
//    /// </summary>
//    /// <param name="data"></param>
//    /// <returns>list of found  packet start indexes => found index + pattern length </returns>
//    public static List<int> search(byte[] data)
//    {
//        List<int> lstPattentIndex = new List<int>();
//
//        byte[] pattern = getPacketSeperator();
//        for (int i = 0; i <= data.Length - pattern.Length; i++)
//        {
//            if (match(data, getPacketSeperator(), i))
//            {
//                lstPattentIndex.Add(i + pattern.Length);
//            }
//        }
//        return  lstPattentIndex;
//    }
//
//    private static bool match(byte[] data, byte[] pattern,   int start)
//    {
//        if (pattern.Length + start > data.Length)
//        {
//            return false;
//        }
//        else
//        {
//            for (int i = 0; i < pattern.Length; i++)
//            {
//                if (pattern[i] != data[i + start])
//                {
//                    return false;
//                }
//            }
//            return true;
//        }
//    }
//
//
//    /// <summary>
//    /// Finds packet seperator in a buffer and make list of seperated packets
//    /// </summary>
//    /// <param name="data"></param>
//    /// <returns> list of seperated packets</returns>
//    public static List<byte[]> getDataPackets(byte[] data)
//    {
//
//        List<byte[]> result = new List<byte[]>();
//
//        List<byte[]> temps = new List<byte[]>();
//
//
//
//        List<int> lstPatternIndexes = PacketUtility.search(data);
//
//        for (int i = 0; i < lstPatternIndexes.Count; i++)
//        {
//            if ((lstPatternIndexes.Count - 1) >= (i + 1))
//            {
//                temps.Add(data.Skip(lstPatternIndexes[i]).Take(lstPatternIndexes[i + 1] - lstPatternIndexes[i]).ToArray());
//            }
//            else
//            {
//                temps.Add(data.Skip(lstPatternIndexes[i]).ToArray());
//
//            }
//
//        }
//
//        return temps;
//
//
//    }
//
//
//    public static int messagePagesCounter(string message) {
//        if (string.IsNullOrEmpty(message))
//            return 0;
//
//        if (message.Length <= 70)
//            return 1;
//
//        if (message.Length > 70 && message.Length <= 134)
//            return 2;
//
//        if (message.Length > 134 && message.Length <= 201)
//            return 3;
//
//        if (message.Length > 201 && message.Length <= 238)
//            return 4;
//
//        if (message.Length > 238 && message.Length <= 3000)
//            return 5;
//
//
//        return 0;
//    }
//
//    public static bool isRelatedId(int refId, int receviedId, int pages) {
//
//        if (pages == 1) {
//            if (receviedId == refId)
//                return true;
//            else
//                return false;
//        }
//
//        for (int i = refId; i < pages; i--) {
//            if (i < 0)
//                i += 255;
//
//            if (receviedId == i)
//                return true;
//
//        }
//
//        return false;
//    }
}
