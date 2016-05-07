package top.wuhaojie.trackclient.constant;

/**
 * Created by wuhaojie on 2016/5/3 16:37.
 */
public interface Constants {
    /**
     * 文件选择界面响应码
     */
    int FILE_SELECT_CODE = 0;
    String UPLOAD_ADDR_BAK = "http://192.168.23.1:8080/upload";
    String PROGRESS_ADDR_BAK = "http://192.168.23.1:8080/progress";
    String DOWNLOAD_ADDR_BAK = "http://192.168.23.1:8080/download";
    String TAIL_UPLOAD_ADDR = "/upload";
    String PROGRESS_ADDR = "/progress";
    String TAIL_DOWNLOAD_ADDR = "/download";
    String CONFIG_IP = "IP";
    String WEBSITE_REGEX = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";
}