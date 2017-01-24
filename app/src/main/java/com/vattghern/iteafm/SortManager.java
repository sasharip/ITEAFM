package com.vattghern.iteafm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
/**
 * Created by vattg on 19.01.2017.
 */

public class SortManager {
    private static final int SORT_NONE = 0;
    private static final int SORT_ALPHA = 1;
    private static final int SORT_FORMAT = 2;
    private static final int SORT_SIZE = 3;
    private int type = SORT_ALPHA;
    private static String newPath;

    public void setSortType(int type) {
        type = type;
    }

    public static ArrayList<String> sorting(int type, String path, ArrayList<String> filesList) {
        String newPath = path;
        switch (type) {
            case SORT_NONE:
                //no sorting needed
                break;

            case SORT_ALPHA:
                Object[] tt = filesList.toArray();
                filesList.clear();

                Arrays.sort(tt, alph);

                for (Object a : tt) {
                    filesList.add((String) a);
                }
                break;

            case SORT_SIZE:
                int index = 0;
                Object[] size_ar = filesList.toArray();
                String dir = newPath;

                Arrays.sort(size_ar, size);

                filesList.clear();
                for (Object a : size_ar) {
                    if (new File(dir + "/" + (String) a).isDirectory())
                        filesList.add(index++, (String) a);
                    else
                        filesList.add((String) a);
                }
                break;

            case SORT_FORMAT:
                int dirindex = 0;
                Object[] type_ar = filesList.toArray();
                String current = newPath;

                Arrays.sort(type_ar, format);
                filesList.clear();

                for (Object a : type_ar) {
                    if (new File(current + "/" + (String) a).isDirectory())
                        filesList.add(dirindex++, (String) a);
                    else
                        filesList.add((String) a);
                }
                break;

        }
        return filesList;
    }

    private static final Comparator alph = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            return arg0.toLowerCase().compareTo(arg1.toLowerCase());
        }
    };

    private static final Comparator size = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            String dir = newPath;
            Long first = new File(dir + "/" + arg0).length();
            Long second = new File(dir + "/" + arg1).length();

            return first.compareTo(second);
        }
    };

    private static final Comparator format = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            String ext = null;
            String ext2 = null;
            int ret;

            try {
                ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length()).toLowerCase();
                ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length()).toLowerCase();

            } catch (IndexOutOfBoundsException e) {
                return 0;
            }
            ret = ext.compareTo(ext2);

            if (ret == 0)
                return arg0.toLowerCase().compareTo(arg1.toLowerCase());

            return ret;
        }
    };
}
