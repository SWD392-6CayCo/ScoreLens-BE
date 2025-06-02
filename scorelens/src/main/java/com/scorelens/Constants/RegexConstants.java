package com.scorelens.Constants;

public class RegexConstants {
    public static final String VIETNAMESE_EMAIL =
            "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com\\.vn|outlook\\.com\\.vn|hocmai\\.vn|fpt\\.edu\\.vn|vnpt\\.vn)$";

    public static final String VIETNAMESE_PHONE =
            "^0\\d{7}(\\d{2})?$"; // 9 hoặc 10 số, bắt đầu bằng 0
}
