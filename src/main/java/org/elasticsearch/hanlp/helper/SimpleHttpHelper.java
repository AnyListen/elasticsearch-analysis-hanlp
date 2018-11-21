package org.elasticsearch.hanlp.helper;

import com.hankcs.hanlp.utility.Predefine;
import com.hankcs.hanlp.utility.TextUtility;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简易的 HTTP 请求工具
 * @author xietansheng
 * Modified by HEZHILONG on 2018-03-16.
 */
public class SimpleHttpHelper
{

    /**
     * 默认的请求头
     */
    private static final Map<String, String> DEFAULT_REQUEST_HEADERS = new HashMap<String, String>();

    /**
     * 操作默认请求头的读写锁
     */
    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    /**
     * User-Agent PC
     */
    private static final String USER_AGENT_FOR_PC = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

    /**
     * User-Agent Mobile
     */
    private static final String USER_AGENT_FOR_MOBILE = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12A366 Safari/600.1.4";

    /**
     * 连接超时时间, 单位: ms, 0 表示无穷大超时(即不检查超时), 默认 30s 超时
     */
    private static int CONNECT_TIME_OUT = 30 * 1000;

    /**
     * 读取超时时间, 单位: ms, 0 表示无穷大超时(即不检查超时), 默认为 0
     */
    private static int READ_TIME_OUT = 0;

    static {
        // 设置一个默认的 Cookie 管理器, 使用 HttpURLConnection 请求时,
        // 会在内存中自动保存相应的 Cookie, 并且在下一个请求时自动发送相应的 Cookie
        CookieHandler.setDefault(new CookieManager());

        // 默认使用PC浏览器模式
        setMobileBrowserModel(false);
    }

    /**
     * 设置默认的User-Agent是否为移动浏览器模式, 默认为PC浏览器模式,  <br/>
     * <p>
     * 也可以通过 {@link #setDefaultRequestHeader(String, String)} 自定义设置 User-Agent
     *
     * @param isMobileBrowser true: 手机浏览器; false: PC浏览器
     */
    public static void setMobileBrowserModel(boolean isMobileBrowser) {
        setDefaultRequestHeader("User-Agent", isMobileBrowser ? USER_AGENT_FOR_MOBILE : USER_AGENT_FOR_PC);
    }

    /**
     * 设置超时时间, 单位: ms, 0 表示无穷大超时(即不检查超时)
     *
     * @param connectTimeOut 连接超时时间, 默认为 15s
     * @param readTimeOut    读取超时时间, 默认为 0
     */
    public static void setTimeOut(int connectTimeOut, int readTimeOut) {
        if (connectTimeOut < 0 || readTimeOut < 0) {
            return;
        }
        RW_LOCK.writeLock().lock();
        try {
            CONNECT_TIME_OUT = connectTimeOut;
            READ_TIME_OUT = readTimeOut;
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    /**
     * 设置默认的请求头, 每次请求时都将会 添加 并 覆盖 原有的默认请求头
     */
    public static void setDefaultRequestHeader(String key, String value) {
        RW_LOCK.writeLock().lock();
        try {
            DEFAULT_REQUEST_HEADERS.put(key, value);
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    /**
     * 移除默认的请求头
     */
    public static void removeDefaultRequestHeader(String key) {
        RW_LOCK.writeLock().lock();
        try {
            DEFAULT_REQUEST_HEADERS.remove(key);
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> headers) {
        return get(url, headers, null);
    }

    public static String get(String url, File saveToFile) {
        return get(url, null, saveToFile);
    }

    public static String get(String url, Map<String, String> headers, File saveToFile) {
        return sendRequest(url, "GET", headers, null, saveToFile, true);
    }

    public static String post(String url, byte[] body) {
        return post(url, null, body);
    }

    public static String post(String url, Map<String, String> headers, byte[] body) {
        InputStream in = null;
        if (body != null && body.length > 0) {
            in = new ByteArrayInputStream(body);
        }
        return post(url, headers, in, true);
    }

    public static String post(String url, File bodyFile) {
        return post(url, null, bodyFile);
    }

    public static String post(String url, Map<String, String> headers, File bodyFile) {
        try {
            InputStream in = null;
            if (bodyFile != null && bodyFile.exists() && bodyFile.isFile() && bodyFile.length() > 0) {
                in = new FileInputStream(bodyFile);
            }
            return post(url, headers, in, true);
        } catch (Exception ex) {
            Predefine.logger.warning(TextUtility.exceptionToString(ex));
            return "";
        }
    }

    public static String post(String url, InputStream bodyStream) {
        return post(url, null, bodyStream, true);
    }

    public static String post(String url, Map<String, String> headers, InputStream bodyStream, boolean allowRedirect) {
        return sendRequest(url, "POST", headers, bodyStream, null, allowRedirect);
    }

    /**
     * 执行一个通用的 http/https 请求, 支持 301, 302 的重定向, 支持自动识别 charset, 支持同进程中 Cookie 的自动保存与发送
     *
     * @param url        请求的链接, 只支持 http 和 https 链接
     * @param method     (可选) 请求方法, 可以为 null
     * @param headers    (可选) 请求头 (将覆盖默认请求), 可以为 null
     * @param bodyStream (可选) 请求内容, 流将自动关闭, 可以为 null
     * @param saveToFile (可选) 响应保存到该文件, 可以为 null
     * @return 如果响应内容保存到文件, 则返回文件路径, 否则返回响应内容的文本 (自动解析 charset 进行解码)
     */
    public static String sendRequest(String url, String method, Map<String, String> headers, InputStream bodyStream, File saveToFile, boolean allowRedirect) {
        if (!checkUrlValid(url)) {
            Predefine.logger.warning("非法的请求地址：" + url);
            return "";
        }
        HttpURLConnection conn = null;
        try {
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();
            setDefaultProperties(conn);
            if (method != null && method.length() > 0) {
                conn.setRequestMethod(method);
            }
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = conn.getHeaderField("Location");
                if (location != null && allowRedirect) {
                    closeStream(bodyStream);
                    // 重定向为 GET 请求
                    return sendRequest(location, "GET", headers, null, saveToFile, true);
                }
            }
            String contentType = conn.getContentType();
            InputStream in = conn.getInputStream();
            if (code != HttpURLConnection.HTTP_OK) {
                Predefine.logger.warning("Http Error: " + code + "; Desc: " + handleResponseBodyToString(in, contentType));
                return "";
            }
            if (saveToFile != null) {
                handleResponseBodyToFile(in, saveToFile);
                return saveToFile.getPath();
            }
            return handleResponseBodyToString(in, contentType);
        } catch (Exception ex) {
            Predefine.logger.warning(TextUtility.exceptionToString(ex));
            return "";
        } finally {
            closeConnection(conn);
        }
    }

    private static boolean checkUrlValid(String url) {
        boolean isValid = false;
        if (url != null) {
            url = url.toLowerCase();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                isValid = true;
            }
        }
        return isValid;
    }

    private static void setDefaultProperties(HttpURLConnection conn) {
        RW_LOCK.readLock().lock();
        try {
            // 设置连接超时时间
            conn.setConnectTimeout(CONNECT_TIME_OUT);

            // 设置读取超时时间
            conn.setReadTimeout(READ_TIME_OUT);

            // 添加默认的请求头
            if (DEFAULT_REQUEST_HEADERS.size() > 0) {
                for (Map.Entry<String, String> entry : DEFAULT_REQUEST_HEADERS.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        } finally {
            RW_LOCK.readLock().unlock();
        }
    }

    private static void handleResponseBodyToFile(InputStream in, File saveToFile) throws Exception {
        OutputStream out = null;
        try {
            out = new FileOutputStream(saveToFile);
            copyStreamAndClose(in, out);
        } finally {
            closeStream(out);
        }
    }

    private static String handleResponseBodyToString(InputStream in, String contentType) {
        ByteArrayOutputStream bytesOut = null;
        try {
            bytesOut = new ByteArrayOutputStream();
            copyStreamAndClose(in, bytesOut);
            byte[] contentBytes = bytesOut.toByteArray();
            String charset = parseCharset(contentType);
            if (charset == null) {
                charset = parseCharsetFromHtml(contentBytes);
                if (charset == null) {
                    charset = "utf-8";
                }
            }
            String content;
            try {
                content = new String(contentBytes, charset);
            } catch (UnsupportedEncodingException e) {
                content = new String(contentBytes);
            }
            return content;
        } finally {
            closeStream(bytesOut);
        }
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private static String parseCharsetFromHtml(byte[] htmlBytes) {
        if (htmlBytes == null || htmlBytes.length == 0) {
            return null;
        }
        String html;
        try {
            // 先使用单字节编码的 ISO-8859-1 去尝试解码
            html = new String(htmlBytes, "ISO-8859-1");
            return parseCharsetFromHtml(html);
        } catch (UnsupportedEncodingException e) {
            html = new String(htmlBytes);
        }
        return parseCharsetFromHtml(html);
    }

    private static String parseCharsetFromHtml(String html) {
        if (html == null || html.length() == 0) {
            return null;
        }
        html = html.toLowerCase();
        Pattern p = Pattern.compile("<meta [^>]+>");
        Matcher m = p.matcher(html);
        String meta;
        String charset = null;
        while (m.find()) {
            meta = m.group();
            charset = parseCharset(meta);
            if (charset != null) {
                break;
            }
        }
        return charset;
    }

    private static String parseCharset(String content) {
        // text/html; charset=iso-8859-1
        // <meta charset="utf-8">
        // <meta charset='utf-8'>
        // <meta http-equiv="Content-Type" content="text/html; charset=gbk" />
        // <meta http-equiv="Content-Type" content='text/html; charset=gbk' />
        // <meta http-equiv=content-type content=text/html;charset=utf-8>
        if (content == null) {
            return null;
        }
        content = content.trim().toLowerCase();
        Pattern p = Pattern.compile("(?<=((charset=)|(charset=')|(charset=\")))[^'\"/> ]+(?=($|'|\"|/|>| ))");
        Matcher m = p.matcher(content);
        String charset = null;
        while (m.find()) {
            charset = m.group();
            if (charset != null) {
                break;
            }
        }
        return charset;
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                Predefine.logger.warning(TextUtility.exceptionToString(e));
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                Predefine.logger.warning(TextUtility.exceptionToString(e));
            }
        }
    }

    /**
     * 获取链接重定向地址
     * @param link 请求的链接
     * @return 重定向地址
     */
    public static String getLocation(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(10000);
            if (urlConnection.getResponseCode() == 200) {
                return link;
            }
            return urlConnection.getHeaderFields().containsKey("Location") ? urlConnection.getHeaderFields().get("Location").get(0) : link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }
}