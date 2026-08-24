package com.roomlog.global.infra;

/**
 * 외부 검색 API가 돌려주는 제목 정리용.
 * 네이버는 검색어에 &lt;b&gt; 태그를 씌워 보내고, 유튜브는 따옴표를 HTML 엔티티로 보낸다.
 */
public final class HtmlText {

    private HtmlText() {
    }

    public static String clean(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&nbsp;", " ")
                .trim();
    }
}
