package burp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

//    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
//        byte[] result = new byte[bt1.length+bt2.length];
//        System.arraycopy(bt1, 0, result, 0, bt1.length);
//        System.arraycopy(bt2, 0, result, bt1.length, bt2.length);
//        return result;
//    }

//    public static byte[] packRequestRawData(List<String> headers, byte[] body){
////        byte[] result = new ;
//
//        byte[] headersBytes = BurpExtender.helpers.stringToBytes(headersToString(headers));
//
//        return byteMerger(headersBytes, body);
//    }

    public static byte[] subBytes(byte[] src, int off, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, off, result, 0, length);
        return result;
    }

    public static byte[] getBodyBytes(byte[] rawData, int off) {
        return subBytes(rawData, off, rawData.length - off);
    }

//    public static String headersToString(List<String> headers){
//        String result = new String("");
//        for (int i = 0; i < headers.size(); i++) {
//            result+= headers.get(i) + "\n";
//        }
//
////        result+="\n";
//
//
//        return  result;
//    }

    public static Map<String, String> listStringToMapString(List<String> listString) {
        Map<String, String> result = new HashMap<>();
        for (String item : listString) {
            String[] kvs = item.split(": ");
            if (kvs.length <= 1) {
                continue;
            }
            result.put(kvs[0], kvs[1]);
        }
        return result;
    }

//    public static List<String> mapStringToListString(Map<String, String> map){
//        List<String> result = new ArrayList<>();
//        for (Map.Entry<String, String> entry: map.entrySet()){
//            result.add(entry.getKey() + ": " + entry.getValue());
//        }
//
//        return result;
//
//    }

}
