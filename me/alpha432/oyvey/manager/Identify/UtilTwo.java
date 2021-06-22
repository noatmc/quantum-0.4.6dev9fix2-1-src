package me.alpha432.oyvey.manager.Identify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UtilTwo {
  public static final String CHARSET_UTF8 = "UTF-8";
  
  public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
  
  public static final String CONTENT_TYPE_JSON = "application/json";
  
  public static final String ENCODING_GZIP = "gzip";
  
  public static final String HEADER_ACCEPT = "Accept";
  
  public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
  
  public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
  
  public static final String HEADER_AUTHORIZATION = "Authorization";
  
  public static final String HEADER_CACHE_CONTROL = "Cache-Control";
  
  public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
  
  public static final String HEADER_CONTENT_LENGTH = "Content-Length";
  
  public static final String HEADER_CONTENT_TYPE = "Content-Type";
  
  public static final String HEADER_DATE = "Date";
  
  public static final String HEADER_ETAG = "ETag";
  
  public static final String HEADER_EXPIRES = "Expires";
  
  public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
  
  public static final String HEADER_LAST_MODIFIED = "Last-Modified";
  
  public static final String HEADER_LOCATION = "Location";
  
  public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
  
  public static final String HEADER_REFERER = "Referer";
  
  public static final String HEADER_SERVER = "Server";
  
  public static final String HEADER_USER_AGENT = "User-Agent";
  
  public static final String METHOD_DELETE = "DELETE";
  
  public static final String METHOD_GET = "GET";
  
  public static final String METHOD_HEAD = "HEAD";
  
  public static final String METHOD_OPTIONS = "OPTIONS";
  
  public static final String METHOD_POST = "POST";
  
  public static final String METHOD_PATCH = "PATCH";
  
  public static final String METHOD_PUT = "PUT";
  
  public static final String METHOD_TRACE = "TRACE";
  
  public static final String PARAM_CHARSET = "charset";
  
  private static final String BOUNDARY = "00content0boundary00";
  
  private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=00content0boundary00";
  
  private static final String CRLF = "\r\n";
  
  private static final String[] EMPTY_STRINGS = new String[0];
  
  private static SSLSocketFactory TRUSTED_FACTORY;
  
  private static HostnameVerifier TRUSTED_VERIFIER;
  
  private static String getValidCharset(String charset) {
    if (charset != null && charset.length() > 0)
      return charset; 
    return "UTF-8";
  }
  
  private static SSLSocketFactory getTrustedFactory() throws HttpRequestException {
    if (TRUSTED_FACTORY == null) {
      TrustManager[] trustAllCerts = { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
            
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
          } };
      try {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustAllCerts, new SecureRandom());
        TRUSTED_FACTORY = context.getSocketFactory();
      } catch (GeneralSecurityException e) {
        IOException ioException = new IOException("Security exception configuring SSL context");
        ioException.initCause(e);
        throw new HttpRequestException(ioException);
      } 
    } 
    return TRUSTED_FACTORY;
  }
  
  private static HostnameVerifier getTrustedVerifier() {
    if (TRUSTED_VERIFIER == null)
      TRUSTED_VERIFIER = new HostnameVerifier() {
          public boolean verify(String hostname, SSLSession session) {
            return true;
          }
        }; 
    return TRUSTED_VERIFIER;
  }
  
  private static StringBuilder addPathSeparator(String baseUrl, StringBuilder result) {
    if (baseUrl.indexOf(':') + 2 == baseUrl.lastIndexOf('/'))
      result.append('/'); 
    return result;
  }
  
  private static StringBuilder addParamPrefix(String baseUrl, StringBuilder result) {
    int queryStart = baseUrl.indexOf('?');
    int lastChar = result.length() - 1;
    if (queryStart == -1) {
      result.append('?');
    } else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&') {
      result.append('&');
    } 
    return result;
  }
  
  private static StringBuilder addParam(Object key, Object<Object> value, StringBuilder result) {
    if (value != null && value.getClass().isArray())
      value = (Object<Object>)arrayToList(value); 
    if (value instanceof Iterable) {
      Iterator<?> iterator = ((Iterable)value).iterator();
      while (iterator.hasNext()) {
        result.append(key);
        result.append("[]=");
        Object element = iterator.next();
        if (element != null)
          result.append(element); 
        if (iterator.hasNext())
          result.append("&"); 
      } 
    } else {
      result.append(key);
      result.append("=");
      if (value != null)
        result.append(value); 
    } 
    return result;
  }
  
  public static interface ConnectionFactory {
    public static final ConnectionFactory DEFAULT = new ConnectionFactory() {
        public HttpURLConnection create(URL url) throws IOException {
          return (HttpURLConnection)url.openConnection();
        }
        
        public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
          return (HttpURLConnection)url.openConnection(proxy);
        }
      };
    
    HttpURLConnection create(URL param1URL) throws IOException;
    
    HttpURLConnection create(URL param1URL, Proxy param1Proxy) throws IOException;
  }
  
  private static ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
  
  public static void setConnectionFactory(ConnectionFactory connectionFactory) {
    if (connectionFactory == null) {
      CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
    } else {
      CONNECTION_FACTORY = connectionFactory;
    } 
  }
  
  public static interface UploadProgress {
    public static final UploadProgress DEFAULT = new UploadProgress() {
        public void onUpload(long uploaded, long total) {}
      };
    
    void onUpload(long param1Long1, long param1Long2);
  }
  
  public static class Base64 {
    private static final byte EQUALS_SIGN = 61;
    
    private static final String PREFERRED_ENCODING = "US-ASCII";
    
    private static final byte[] _STANDARD_ALPHABET = new byte[] { 
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 43, 47 };
    
    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
      byte[] ALPHABET = _STANDARD_ALPHABET;
      int inBuff = ((numSigBytes > 0) ? (source[srcOffset] << 24 >>> 8) : 0) | ((numSigBytes > 1) ? (source[srcOffset + 1] << 24 >>> 16) : 0) | ((numSigBytes > 2) ? (source[srcOffset + 2] << 24 >>> 24) : 0);
      switch (numSigBytes) {
        case 3:
          destination[destOffset] = ALPHABET[inBuff >>> 18];
          destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
          destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
          destination[destOffset + 3] = ALPHABET[inBuff & 0x3F];
          return destination;
        case 2:
          destination[destOffset] = ALPHABET[inBuff >>> 18];
          destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
          destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
          destination[destOffset + 3] = 61;
          return destination;
        case 1:
          destination[destOffset] = ALPHABET[inBuff >>> 18];
          destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
          destination[destOffset + 2] = 61;
          destination[destOffset + 3] = 61;
          return destination;
      } 
      return destination;
    }
    
    public static String encode(String string) {
      byte[] bytes;
      try {
        bytes = string.getBytes("US-ASCII");
      } catch (UnsupportedEncodingException e) {
        bytes = string.getBytes();
      } 
      return encodeBytes(bytes);
    }
    
    public static String encodeBytes(byte[] source) {
      return encodeBytes(source, 0, source.length);
    }
    
    public static String encodeBytes(byte[] source, int off, int len) {
      byte[] encoded = encodeBytesToBytes(source, off, len);
      try {
        return new String(encoded, "US-ASCII");
      } catch (UnsupportedEncodingException uue) {
        return new String(encoded);
      } 
    }
    
    public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {
      if (source == null)
        throw new NullPointerException("Cannot serialize a null array."); 
      if (off < 0)
        throw new IllegalArgumentException("Cannot have negative offset: " + off); 
      if (len < 0)
        throw new IllegalArgumentException("Cannot have length offset: " + len); 
      if (off + len > source.length)
        throw new IllegalArgumentException(
            
            String.format("Cannot have offset of %d and length of %d with array of length %d", new Object[] { Integer.valueOf(off), Integer.valueOf(len), Integer.valueOf(source.length) })); 
      int encLen = len / 3 * 4 + ((len % 3 > 0) ? 4 : 0);
      byte[] outBuff = new byte[encLen];
      int d = 0;
      int e = 0;
      int len2 = len - 2;
      for (; d < len2; d += 3, e += 4)
        encode3to4(source, d + off, 3, outBuff, e); 
      if (d < len) {
        encode3to4(source, d + off, len - d, outBuff, e);
        e += 4;
      } 
      if (e <= outBuff.length - 1) {
        byte[] finalOut = new byte[e];
        System.arraycopy(outBuff, 0, finalOut, 0, e);
        return finalOut;
      } 
      return outBuff;
    }
  }
  
  public static class HttpRequestException extends RuntimeException {
    private static final long serialVersionUID = -1170466989781746231L;
    
    public HttpRequestException(IOException cause) {
      super(cause);
    }
    
    public IOException getCause() {
      return (IOException)super.getCause();
    }
  }
  
  protected static abstract class Operation<V> implements Callable<V> {
    protected abstract V run() throws UtilTwo.HttpRequestException, IOException;
    
    protected abstract void done() throws IOException;
    
    public V call() throws UtilTwo.HttpRequestException {
      boolean thrown = false;
      try {
        return run();
      } catch (HttpRequestException e) {
        thrown = true;
        throw e;
      } catch (IOException e) {
        thrown = true;
        throw new UtilTwo.HttpRequestException(e);
      } finally {
        try {
          done();
        } catch (IOException e) {
          if (!thrown)
            throw new UtilTwo.HttpRequestException(e); 
        } 
      } 
    }
  }
  
  protected static abstract class CloseOperation<V> extends Operation<V> {
    private final Closeable closeable;
    
    private final boolean ignoreCloseExceptions;
    
    protected CloseOperation(Closeable closeable, boolean ignoreCloseExceptions) {
      this.closeable = closeable;
      this.ignoreCloseExceptions = ignoreCloseExceptions;
    }
    
    protected void done() throws IOException {
      if (this.closeable instanceof Flushable)
        ((Flushable)this.closeable).flush(); 
      if (this.ignoreCloseExceptions) {
        try {
          this.closeable.close();
        } catch (IOException iOException) {}
      } else {
        this.closeable.close();
      } 
    }
  }
  
  protected static abstract class FlushOperation<V> extends Operation<V> {
    private final Flushable flushable;
    
    protected FlushOperation(Flushable flushable) {
      this.flushable = flushable;
    }
    
    protected void done() throws IOException {
      this.flushable.flush();
    }
  }
  
  public static class RequestOutputStream extends BufferedOutputStream {
    private final CharsetEncoder encoder;
    
    public RequestOutputStream(OutputStream stream, String charset, int bufferSize) {
      super(stream, bufferSize);
      this.encoder = Charset.forName(UtilTwo.getValidCharset(charset)).newEncoder();
    }
    
    public RequestOutputStream write(String value) throws IOException {
      ByteBuffer bytes = this.encoder.encode(CharBuffer.wrap(value));
      write(bytes.array(), 0, bytes.limit());
      return this;
    }
  }
  
  private static List<Object> arrayToList(Object array) {
    if (array instanceof Object[])
      return Arrays.asList((Object[])array); 
    List<Object> result = new ArrayList();
    if (array instanceof int[]) {
      for (int value : (int[])array)
        result.add(Integer.valueOf(value)); 
    } else if (array instanceof boolean[]) {
      for (boolean value : (boolean[])array)
        result.add(Boolean.valueOf(value)); 
    } else if (array instanceof long[]) {
      for (long value : (long[])array)
        result.add(Long.valueOf(value)); 
    } else if (array instanceof float[]) {
      for (float value : (float[])array)
        result.add(Float.valueOf(value)); 
    } else if (array instanceof double[]) {
      for (double value : (double[])array)
        result.add(Double.valueOf(value)); 
    } else if (array instanceof short[]) {
      for (short value : (short[])array)
        result.add(Short.valueOf(value)); 
    } else if (array instanceof byte[]) {
      for (byte value : (byte[])array)
        result.add(Byte.valueOf(value)); 
    } else if (array instanceof char[]) {
      for (char value : (char[])array)
        result.add(Character.valueOf(value)); 
    } 
    return result;
  }
  
  public static String encode(CharSequence url) throws HttpRequestException {
    URL parsed;
    try {
      parsed = new URL(url.toString());
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    String host = parsed.getHost();
    int port = parsed.getPort();
    if (port != -1)
      host = host + ':' + Integer.toString(port); 
    try {
      String encoded = (new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), null)).toASCIIString();
      int paramsStart = encoded.indexOf('?');
      if (paramsStart > 0 && paramsStart + 1 < encoded.length())
        encoded = encoded.substring(0, paramsStart + 1) + encoded.substring(paramsStart + 1).replace("+", "%2B"); 
      return encoded;
    } catch (URISyntaxException e) {
      IOException io = new IOException("Parsing URI failed");
      io.initCause(e);
      throw new HttpRequestException(io);
    } 
  }
  
  public static String append(CharSequence url, Map<?, ?> params) {
    String baseUrl = url.toString();
    if (params == null || params.isEmpty())
      return baseUrl; 
    StringBuilder result = new StringBuilder(baseUrl);
    addPathSeparator(baseUrl, result);
    addParamPrefix(baseUrl, result);
    Iterator<?> iterator = params.entrySet().iterator();
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iterator.next();
    addParam(entry.getKey().toString(), entry.getValue(), result);
    while (iterator.hasNext()) {
      result.append('&');
      entry = (Map.Entry<?, ?>)iterator.next();
      addParam(entry.getKey().toString(), entry.getValue(), result);
    } 
    return result.toString();
  }
  
  public static String append(CharSequence url, Object... params) {
    String baseUrl = url.toString();
    if (params == null || params.length == 0)
      return baseUrl; 
    if (params.length % 2 != 0)
      throw new IllegalArgumentException("Must specify an even number of parameter names/values"); 
    StringBuilder result = new StringBuilder(baseUrl);
    addPathSeparator(baseUrl, result);
    addParamPrefix(baseUrl, result);
    addParam(params[0], params[1], result);
    for (int i = 2; i < params.length; i += 2) {
      result.append('&');
      addParam(params[i], params[i + 1], result);
    } 
    return result.toString();
  }
  
  public static UtilTwo get(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "GET");
  }
  
  public static UtilTwo get(URL url) throws HttpRequestException {
    return new UtilTwo(url, "GET");
  }
  
  public static UtilTwo get(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
    String url = append(baseUrl, params);
    return get(encode ? encode(url) : url);
  }
  
  public static UtilTwo get(CharSequence baseUrl, boolean encode, Object... params) {
    String url = append(baseUrl, params);
    return get(encode ? encode(url) : url);
  }
  
  public static UtilTwo post(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "POST");
  }
  
  public static UtilTwo post(URL url) throws HttpRequestException {
    return new UtilTwo(url, "POST");
  }
  
  public static UtilTwo post(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
    String url = append(baseUrl, params);
    return post(encode ? encode(url) : url);
  }
  
  public static UtilTwo post(CharSequence baseUrl, boolean encode, Object... params) {
    String url = append(baseUrl, params);
    return post(encode ? encode(url) : url);
  }
  
  public static UtilTwo patch(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "PATCH");
  }
  
  public static UtilTwo put(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "PUT");
  }
  
  public static UtilTwo put(URL url) throws HttpRequestException {
    return new UtilTwo(url, "PUT");
  }
  
  public static UtilTwo put(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
    String url = append(baseUrl, params);
    return put(encode ? encode(url) : url);
  }
  
  public static UtilTwo put(CharSequence baseUrl, boolean encode, Object... params) {
    String url = append(baseUrl, params);
    return put(encode ? encode(url) : url);
  }
  
  public static UtilTwo delete(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "DELETE");
  }
  
  public static UtilTwo delete(URL url) throws HttpRequestException {
    return new UtilTwo(url, "DELETE");
  }
  
  public static UtilTwo delete(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
    String url = append(baseUrl, params);
    return delete(encode ? encode(url) : url);
  }
  
  public static UtilTwo delete(CharSequence baseUrl, boolean encode, Object... params) {
    String url = append(baseUrl, params);
    return delete(encode ? encode(url) : url);
  }
  
  public static UtilTwo head(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "HEAD");
  }
  
  public static UtilTwo head(URL url) throws HttpRequestException {
    return new UtilTwo(url, "HEAD");
  }
  
  public static UtilTwo head(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
    String url = append(baseUrl, params);
    return head(encode ? encode(url) : url);
  }
  
  public static UtilTwo head(CharSequence baseUrl, boolean encode, Object... params) {
    String url = append(baseUrl, params);
    return head(encode ? encode(url) : url);
  }
  
  public static UtilTwo options(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "OPTIONS");
  }
  
  public static UtilTwo options(URL url) throws HttpRequestException {
    return new UtilTwo(url, "OPTIONS");
  }
  
  public static UtilTwo trace(CharSequence url) throws HttpRequestException {
    return new UtilTwo(url, "TRACE");
  }
  
  public static UtilTwo trace(URL url) throws HttpRequestException {
    return new UtilTwo(url, "TRACE");
  }
  
  public static void keepAlive(boolean keepAlive) {
    setProperty("http.keepAlive", Boolean.toString(keepAlive));
  }
  
  public static void maxConnections(int maxConnections) {
    setProperty("http.maxConnections", Integer.toString(maxConnections));
  }
  
  public static void proxyHost(String host) {
    setProperty("http.proxyHost", host);
    setProperty("https.proxyHost", host);
  }
  
  public static void proxyPort(int port) {
    String portValue = Integer.toString(port);
    setProperty("http.proxyPort", portValue);
    setProperty("https.proxyPort", portValue);
  }
  
  public static void nonProxyHosts(String... hosts) {
    if (hosts != null && hosts.length > 0) {
      StringBuilder separated = new StringBuilder();
      int last = hosts.length - 1;
      for (int i = 0; i < last; i++)
        separated.append(hosts[i]).append('|'); 
      separated.append(hosts[last]);
      setProperty("http.nonProxyHosts", separated.toString());
    } else {
      setProperty("http.nonProxyHosts", null);
    } 
  }
  
  private static String setProperty(final String name, final String value) {
    PrivilegedAction<String> action;
    if (value != null) {
      action = new PrivilegedAction<String>() {
          public String run() {
            return System.setProperty(name, value);
          }
        };
    } else {
      action = new PrivilegedAction<String>() {
          public String run() {
            return System.clearProperty(name);
          }
        };
    } 
    return AccessController.<String>doPrivileged(action);
  }
  
  private HttpURLConnection connection = null;
  
  private final URL url;
  
  private final String requestMethod;
  
  private RequestOutputStream output;
  
  private boolean multipart;
  
  private boolean form;
  
  private boolean ignoreCloseExceptions = true;
  
  private boolean uncompress = false;
  
  private int bufferSize = 8192;
  
  private long totalSize = -1L;
  
  private long totalWritten = 0L;
  
  private String httpProxyHost;
  
  private int httpProxyPort;
  
  private UploadProgress progress = UploadProgress.DEFAULT;
  
  public UtilTwo(CharSequence url, String method) throws HttpRequestException {
    try {
      this.url = new URL(url.toString());
    } catch (MalformedURLException e) {
      throw new HttpRequestException(e);
    } 
    this.requestMethod = method;
  }
  
  public UtilTwo(URL url, String method) throws HttpRequestException {
    this.url = url;
    this.requestMethod = method;
  }
  
  private Proxy createProxy() {
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.httpProxyHost, this.httpProxyPort));
  }
  
  private HttpURLConnection createConnection() {
    try {
      HttpURLConnection connection;
      if (this.httpProxyHost != null) {
        connection = CONNECTION_FACTORY.create(this.url, createProxy());
      } else {
        connection = CONNECTION_FACTORY.create(this.url);
      } 
      connection.setRequestMethod(this.requestMethod);
      return connection;
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public String toString() {
    return method() + ' ' + url();
  }
  
  public HttpURLConnection getConnection() {
    if (this.connection == null)
      this.connection = createConnection(); 
    return this.connection;
  }
  
  public UtilTwo ignoreCloseExceptions(boolean ignore) {
    this.ignoreCloseExceptions = ignore;
    return this;
  }
  
  public boolean ignoreCloseExceptions() {
    return this.ignoreCloseExceptions;
  }
  
  public int code() throws HttpRequestException {
    try {
      closeOutput();
      return getConnection().getResponseCode();
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public UtilTwo code(AtomicInteger output) throws HttpRequestException {
    output.set(code());
    return this;
  }
  
  public boolean ok() throws HttpRequestException {
    return (200 == code());
  }
  
  public boolean created() throws HttpRequestException {
    return (201 == code());
  }
  
  public boolean noContent() throws HttpRequestException {
    return (204 == code());
  }
  
  public boolean serverError() throws HttpRequestException {
    return (500 == code());
  }
  
  public boolean badRequest() throws HttpRequestException {
    return (400 == code());
  }
  
  public boolean notFound() throws HttpRequestException {
    return (404 == code());
  }
  
  public boolean notModified() throws HttpRequestException {
    return (304 == code());
  }
  
  public String message() throws HttpRequestException {
    try {
      closeOutput();
      return getConnection().getResponseMessage();
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public UtilTwo disconnect() {
    getConnection().disconnect();
    return this;
  }
  
  public UtilTwo chunk(int size) {
    getConnection().setChunkedStreamingMode(size);
    return this;
  }
  
  public UtilTwo bufferSize(int size) {
    if (size < 1)
      throw new IllegalArgumentException("Size must be greater than zero"); 
    this.bufferSize = size;
    return this;
  }
  
  public int bufferSize() {
    return this.bufferSize;
  }
  
  public UtilTwo uncompress(boolean uncompress) {
    this.uncompress = uncompress;
    return this;
  }
  
  protected ByteArrayOutputStream byteStream() {
    int size = contentLength();
    if (size > 0)
      return new ByteArrayOutputStream(size); 
    return new ByteArrayOutputStream();
  }
  
  public String body(String charset) throws HttpRequestException {
    ByteArrayOutputStream output = byteStream();
    try {
      copy(buffer(), output);
      return output.toString(getValidCharset(charset));
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public String body() throws HttpRequestException {
    return body(charset());
  }
  
  public UtilTwo body(AtomicReference<String> output) throws HttpRequestException {
    output.set(body());
    return this;
  }
  
  public UtilTwo body(AtomicReference<String> output, String charset) throws HttpRequestException {
    output.set(body(charset));
    return this;
  }
  
  public boolean isBodyEmpty() throws HttpRequestException {
    return (contentLength() == 0);
  }
  
  public byte[] bytes() throws HttpRequestException {
    ByteArrayOutputStream output = byteStream();
    try {
      copy(buffer(), output);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return output.toByteArray();
  }
  
  public BufferedInputStream buffer() throws HttpRequestException {
    return new BufferedInputStream(stream(), this.bufferSize);
  }
  
  public InputStream stream() throws HttpRequestException {
    InputStream stream;
    if (code() < 400) {
      try {
        stream = getConnection().getInputStream();
      } catch (IOException e) {
        throw new HttpRequestException(e);
      } 
    } else {
      stream = getConnection().getErrorStream();
      if (stream == null)
        try {
          stream = getConnection().getInputStream();
        } catch (IOException e) {
          if (contentLength() > 0)
            throw new HttpRequestException(e); 
          stream = new ByteArrayInputStream(new byte[0]);
        }  
    } 
    if (!this.uncompress || !"gzip".equals(contentEncoding()))
      return stream; 
    try {
      return new GZIPInputStream(stream);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public InputStreamReader reader(String charset) throws HttpRequestException {
    try {
      return new InputStreamReader(stream(), getValidCharset(charset));
    } catch (UnsupportedEncodingException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public InputStreamReader reader() throws HttpRequestException {
    return reader(charset());
  }
  
  public BufferedReader bufferedReader(String charset) throws HttpRequestException {
    return new BufferedReader(reader(charset), this.bufferSize);
  }
  
  public BufferedReader bufferedReader() throws HttpRequestException {
    return bufferedReader(charset());
  }
  
  public UtilTwo receive(File file) throws HttpRequestException {
    final OutputStream output;
    try {
      output = new BufferedOutputStream(new FileOutputStream(file), this.bufferSize);
    } catch (FileNotFoundException e) {
      throw new HttpRequestException(e);
    } 
    return (new CloseOperation<UtilTwo>(output, this.ignoreCloseExceptions) {
        protected UtilTwo run() throws UtilTwo.HttpRequestException, IOException {
          return UtilTwo.this.receive(output);
        }
      }).call();
  }
  
  public UtilTwo receive(OutputStream output) throws HttpRequestException {
    try {
      return copy(buffer(), output);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public UtilTwo receive(PrintStream output) throws HttpRequestException {
    return receive(output);
  }
  
  public UtilTwo receive(final Appendable appendable) throws HttpRequestException {
    final BufferedReader reader = bufferedReader();
    return (new CloseOperation<UtilTwo>(reader, this.ignoreCloseExceptions) {
        public UtilTwo run() throws IOException {
          CharBuffer buffer = CharBuffer.allocate(UtilTwo.this.bufferSize);
          int read;
          while ((read = reader.read(buffer)) != -1) {
            buffer.rewind();
            appendable.append(buffer, 0, read);
            buffer.rewind();
          } 
          return UtilTwo.this;
        }
      }).call();
  }
  
  public UtilTwo receive(final Writer writer) throws HttpRequestException {
    final BufferedReader reader = bufferedReader();
    return (new CloseOperation<UtilTwo>(reader, this.ignoreCloseExceptions) {
        public UtilTwo run() throws IOException {
          return UtilTwo.this.copy(reader, writer);
        }
      }).call();
  }
  
  public UtilTwo readTimeout(int timeout) {
    getConnection().setReadTimeout(timeout);
    return this;
  }
  
  public UtilTwo connectTimeout(int timeout) {
    getConnection().setConnectTimeout(timeout);
    return this;
  }
  
  public UtilTwo header(String name, String value) {
    getConnection().setRequestProperty(name, value);
    return this;
  }
  
  public UtilTwo header(String name, Number value) {
    return header(name, (value != null) ? value.toString() : null);
  }
  
  public UtilTwo headers(Map<String, String> headers) {
    if (!headers.isEmpty())
      for (Map.Entry<String, String> header : headers.entrySet())
        header(header);  
    return this;
  }
  
  public UtilTwo header(Map.Entry<String, String> header) {
    return header(header.getKey(), header.getValue());
  }
  
  public String header(String name) throws HttpRequestException {
    closeOutputQuietly();
    return getConnection().getHeaderField(name);
  }
  
  public Map<String, List<String>> headers() throws HttpRequestException {
    closeOutputQuietly();
    return getConnection().getHeaderFields();
  }
  
  public long dateHeader(String name) throws HttpRequestException {
    return dateHeader(name, -1L);
  }
  
  public long dateHeader(String name, long defaultValue) throws HttpRequestException {
    closeOutputQuietly();
    return getConnection().getHeaderFieldDate(name, defaultValue);
  }
  
  public int intHeader(String name) throws HttpRequestException {
    return intHeader(name, -1);
  }
  
  public int intHeader(String name, int defaultValue) throws HttpRequestException {
    closeOutputQuietly();
    return getConnection().getHeaderFieldInt(name, defaultValue);
  }
  
  public String[] headers(String name) {
    Map<String, List<String>> headers = headers();
    if (headers == null || headers.isEmpty())
      return EMPTY_STRINGS; 
    List<String> values = headers.get(name);
    if (values != null && !values.isEmpty())
      return values.<String>toArray(new String[values.size()]); 
    return EMPTY_STRINGS;
  }
  
  public String parameter(String headerName, String paramName) {
    return getParam(header(headerName), paramName);
  }
  
  public Map<String, String> parameters(String headerName) {
    return getParams(header(headerName));
  }
  
  protected Map<String, String> getParams(String header) {
    if (header == null || header.length() == 0)
      return Collections.emptyMap(); 
    int headerLength = header.length();
    int start = header.indexOf(';') + 1;
    if (start == 0 || start == headerLength)
      return Collections.emptyMap(); 
    int end = header.indexOf(';', start);
    if (end == -1)
      end = headerLength; 
    Map<String, String> params = new LinkedHashMap<>();
    while (start < end) {
      int nameEnd = header.indexOf('=', start);
      if (nameEnd != -1 && nameEnd < end) {
        String name = header.substring(start, nameEnd).trim();
        if (name.length() > 0) {
          String value = header.substring(nameEnd + 1, end).trim();
          int length = value.length();
          if (length != 0)
            if (length > 2 && '"' == value.charAt(0) && '"' == value
              .charAt(length - 1)) {
              params.put(name, value.substring(1, length - 1));
            } else {
              params.put(name, value);
            }  
        } 
      } 
      start = end + 1;
      end = header.indexOf(';', start);
      if (end == -1)
        end = headerLength; 
    } 
    return params;
  }
  
  protected String getParam(String value, String paramName) {
    if (value == null || value.length() == 0)
      return null; 
    int length = value.length();
    int start = value.indexOf(';') + 1;
    if (start == 0 || start == length)
      return null; 
    int end = value.indexOf(';', start);
    if (end == -1)
      end = length; 
    while (start < end) {
      int nameEnd = value.indexOf('=', start);
      if (nameEnd != -1 && nameEnd < end && paramName
        .equals(value.substring(start, nameEnd).trim())) {
        String paramValue = value.substring(nameEnd + 1, end).trim();
        int valueLength = paramValue.length();
        if (valueLength != 0) {
          if (valueLength > 2 && '"' == paramValue.charAt(0) && '"' == paramValue
            .charAt(valueLength - 1))
            return paramValue.substring(1, valueLength - 1); 
          return paramValue;
        } 
      } 
      start = end + 1;
      end = value.indexOf(';', start);
      if (end == -1)
        end = length; 
    } 
    return null;
  }
  
  public String charset() {
    return parameter("Content-Type", "charset");
  }
  
  public UtilTwo userAgent(String userAgent) {
    return header("User-Agent", userAgent);
  }
  
  public UtilTwo referer(String referer) {
    return header("Referer", referer);
  }
  
  public UtilTwo useCaches(boolean useCaches) {
    getConnection().setUseCaches(useCaches);
    return this;
  }
  
  public UtilTwo acceptEncoding(String acceptEncoding) {
    return header("Accept-Encoding", acceptEncoding);
  }
  
  public UtilTwo acceptGzipEncoding() {
    return acceptEncoding("gzip");
  }
  
  public UtilTwo acceptCharset(String acceptCharset) {
    return header("Accept-Charset", acceptCharset);
  }
  
  public String contentEncoding() {
    return header("Content-Encoding");
  }
  
  public String server() {
    return header("Server");
  }
  
  public long date() {
    return dateHeader("Date");
  }
  
  public String cacheControl() {
    return header("Cache-Control");
  }
  
  public String eTag() {
    return header("ETag");
  }
  
  public long expires() {
    return dateHeader("Expires");
  }
  
  public long lastModified() {
    return dateHeader("Last-Modified");
  }
  
  public String location() {
    return header("Location");
  }
  
  public UtilTwo authorization(String authorization) {
    return header("Authorization", authorization);
  }
  
  public UtilTwo proxyAuthorization(String proxyAuthorization) {
    return header("Proxy-Authorization", proxyAuthorization);
  }
  
  public UtilTwo basic(String name, String password) {
    return authorization("Basic " + Base64.encode(name + ':' + password));
  }
  
  public UtilTwo proxyBasic(String name, String password) {
    return proxyAuthorization("Basic " + Base64.encode(name + ':' + password));
  }
  
  public UtilTwo ifModifiedSince(long ifModifiedSince) {
    getConnection().setIfModifiedSince(ifModifiedSince);
    return this;
  }
  
  public UtilTwo ifNoneMatch(String ifNoneMatch) {
    return header("If-None-Match", ifNoneMatch);
  }
  
  public UtilTwo contentType(String contentType) {
    return contentType(contentType, null);
  }
  
  public UtilTwo contentType(String contentType, String charset) {
    if (charset != null && charset.length() > 0) {
      String separator = "; charset=";
      return header("Content-Type", contentType + "; charset=" + charset);
    } 
    return header("Content-Type", contentType);
  }
  
  public String contentType() {
    return header("Content-Type");
  }
  
  public int contentLength() {
    return intHeader("Content-Length");
  }
  
  public UtilTwo contentLength(String contentLength) {
    return contentLength(Integer.parseInt(contentLength));
  }
  
  public UtilTwo contentLength(int contentLength) {
    getConnection().setFixedLengthStreamingMode(contentLength);
    return this;
  }
  
  public UtilTwo accept(String accept) {
    return header("Accept", accept);
  }
  
  public UtilTwo acceptJson() {
    return accept("application/json");
  }
  
  protected UtilTwo copy(final InputStream input, final OutputStream output) throws IOException {
    return (new CloseOperation<UtilTwo>(input, this.ignoreCloseExceptions) {
        public UtilTwo run() throws IOException {
          byte[] buffer = new byte[UtilTwo.this.bufferSize];
          int read;
          while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
            UtilTwo.this.totalWritten = UtilTwo.this.totalWritten + read;
            UtilTwo.this.progress.onUpload(UtilTwo.this.totalWritten, UtilTwo.this.totalSize);
          } 
          return UtilTwo.this;
        }
      }).call();
  }
  
  protected UtilTwo copy(final Reader input, final Writer output) throws IOException {
    return (new CloseOperation<UtilTwo>(input, this.ignoreCloseExceptions) {
        public UtilTwo run() throws IOException {
          char[] buffer = new char[UtilTwo.this.bufferSize];
          int read;
          while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
            UtilTwo.this.totalWritten = UtilTwo.this.totalWritten + read;
            UtilTwo.this.progress.onUpload(UtilTwo.this.totalWritten, -1L);
          } 
          return UtilTwo.this;
        }
      }).call();
  }
  
  public UtilTwo progress(UploadProgress callback) {
    if (callback == null) {
      this.progress = UploadProgress.DEFAULT;
    } else {
      this.progress = callback;
    } 
    return this;
  }
  
  private UtilTwo incrementTotalSize(long size) {
    if (this.totalSize == -1L)
      this.totalSize = 0L; 
    this.totalSize += size;
    return this;
  }
  
  protected UtilTwo closeOutput() throws IOException {
    progress(null);
    if (this.output == null)
      return this; 
    if (this.multipart)
      this.output.write("\r\n--00content0boundary00--\r\n"); 
    if (this.ignoreCloseExceptions) {
      try {
        this.output.close();
      } catch (IOException iOException) {}
    } else {
      this.output.close();
    } 
    this.output = null;
    return this;
  }
  
  protected UtilTwo closeOutputQuietly() throws HttpRequestException {
    try {
      return closeOutput();
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  protected UtilTwo openOutput() throws IOException {
    if (this.output != null)
      return this; 
    getConnection().setDoOutput(true);
    String charset = getParam(
        getConnection().getRequestProperty("Content-Type"), "charset");
    this.output = new RequestOutputStream(getConnection().getOutputStream(), charset, this.bufferSize);
    return this;
  }
  
  protected UtilTwo startPart() throws IOException {
    if (!this.multipart) {
      this.multipart = true;
      contentType("multipart/form-data; boundary=00content0boundary00").openOutput();
      this.output.write("--00content0boundary00\r\n");
    } else {
      this.output.write("\r\n--00content0boundary00\r\n");
    } 
    return this;
  }
  
  protected UtilTwo writePartHeader(String name, String filename) throws IOException {
    return writePartHeader(name, filename, null);
  }
  
  protected UtilTwo writePartHeader(String name, String filename, String contentType) throws IOException {
    StringBuilder partBuffer = new StringBuilder();
    partBuffer.append("form-data; name=\"").append(name);
    if (filename != null)
      partBuffer.append("\"; filename=\"").append(filename); 
    partBuffer.append('"');
    partHeader("Content-Disposition", partBuffer.toString());
    if (contentType != null)
      partHeader("Content-Type", contentType); 
    return send("\r\n");
  }
  
  public UtilTwo part(String name, String part) {
    return part(name, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, String part) throws HttpRequestException {
    return part(name, filename, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, String contentType, String part) throws HttpRequestException {
    try {
      startPart();
      writePartHeader(name, filename, contentType);
      this.output.write(part);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return this;
  }
  
  public UtilTwo part(String name, Number part) throws HttpRequestException {
    return part(name, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, Number part) throws HttpRequestException {
    return part(name, filename, (part != null) ? part.toString() : null);
  }
  
  public UtilTwo part(String name, File part) throws HttpRequestException {
    return part(name, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, File part) throws HttpRequestException {
    return part(name, filename, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, String contentType, File part) throws HttpRequestException {
    InputStream stream;
    try {
      stream = new BufferedInputStream(new FileInputStream(part));
      incrementTotalSize(part.length());
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return part(name, filename, contentType, stream);
  }
  
  public UtilTwo part(String name, InputStream part) throws HttpRequestException {
    return part(name, (String)null, (String)null, part);
  }
  
  public UtilTwo part(String name, String filename, String contentType, InputStream part) throws HttpRequestException {
    try {
      startPart();
      writePartHeader(name, filename, contentType);
      copy(part, this.output);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return this;
  }
  
  public UtilTwo partHeader(String name, String value) throws HttpRequestException {
    return send(name).send(": ").send(value).send("\r\n");
  }
  
  public UtilTwo send(File input) throws HttpRequestException {
    InputStream stream;
    try {
      stream = new BufferedInputStream(new FileInputStream(input));
      incrementTotalSize(input.length());
    } catch (FileNotFoundException e) {
      throw new HttpRequestException(e);
    } 
    return send(stream);
  }
  
  public UtilTwo send(byte[] input) throws HttpRequestException {
    if (input != null)
      incrementTotalSize(input.length); 
    return send(new ByteArrayInputStream(input));
  }
  
  public UtilTwo send(InputStream input) throws HttpRequestException {
    try {
      openOutput();
      copy(input, this.output);
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return this;
  }
  
  public UtilTwo send(final Reader input) throws HttpRequestException {
    try {
      openOutput();
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    final Writer writer = new OutputStreamWriter(this.output, this.output.encoder.charset());
    return (new FlushOperation<UtilTwo>(writer) {
        protected UtilTwo run() throws IOException {
          return UtilTwo.this.copy(input, writer);
        }
      }).call();
  }
  
  public UtilTwo send(CharSequence value) throws HttpRequestException {
    try {
      openOutput();
      this.output.write(value.toString());
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return this;
  }
  
  public OutputStreamWriter writer() throws HttpRequestException {
    try {
      openOutput();
      return new OutputStreamWriter(this.output, this.output.encoder.charset());
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
  }
  
  public UtilTwo form(Map<?, ?> values) throws HttpRequestException {
    return form(values, "UTF-8");
  }
  
  public UtilTwo form(Map.Entry<?, ?> entry) throws HttpRequestException {
    return form(entry, "UTF-8");
  }
  
  public UtilTwo form(Map.Entry<?, ?> entry, String charset) throws HttpRequestException {
    return form(entry.getKey(), entry.getValue(), charset);
  }
  
  public UtilTwo form(Object name, Object value) throws HttpRequestException {
    return form(name, value, "UTF-8");
  }
  
  public UtilTwo form(Object name, Object value, String charset) throws HttpRequestException {
    boolean first = !this.form;
    if (first) {
      contentType("application/x-www-form-urlencoded", charset);
      this.form = true;
    } 
    charset = getValidCharset(charset);
    try {
      openOutput();
      if (!first)
        this.output.write(38); 
      this.output.write(URLEncoder.encode(name.toString(), charset));
      this.output.write(61);
      if (value != null)
        this.output.write(URLEncoder.encode(value.toString(), charset)); 
    } catch (IOException e) {
      throw new HttpRequestException(e);
    } 
    return this;
  }
  
  public UtilTwo form(Map<?, ?> values, String charset) throws HttpRequestException {
    if (!values.isEmpty())
      for (Map.Entry<?, ?> entry : values.entrySet())
        form(entry, charset);  
    return this;
  }
  
  public UtilTwo trustAllCerts() throws HttpRequestException {
    HttpURLConnection connection = getConnection();
    if (connection instanceof HttpsURLConnection)
      ((HttpsURLConnection)connection)
        .setSSLSocketFactory(getTrustedFactory()); 
    return this;
  }
  
  public UtilTwo trustAllHosts() {
    HttpURLConnection connection = getConnection();
    if (connection instanceof HttpsURLConnection)
      ((HttpsURLConnection)connection)
        .setHostnameVerifier(getTrustedVerifier()); 
    return this;
  }
  
  public URL url() {
    return getConnection().getURL();
  }
  
  public String method() {
    return getConnection().getRequestMethod();
  }
  
  public UtilTwo useProxy(String proxyHost, int proxyPort) {
    if (this.connection != null)
      throw new IllegalStateException("The connection has already been created. This method must be called before reading or writing to the request."); 
    this.httpProxyHost = proxyHost;
    this.httpProxyPort = proxyPort;
    return this;
  }
  
  public UtilTwo followRedirects(boolean followRedirects) {
    getConnection().setInstanceFollowRedirects(followRedirects);
    return this;
  }
}
