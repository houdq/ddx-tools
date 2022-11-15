import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.fastjson.JSONObject;

public class CutUtil {

    public static void main(String[] args) {
        runDouyin("https://v.douyin.com/rBAKkD2");

    }
    public static String runDouyin(String shareUrl) {
        // 1.根据分享的视频地址，通过重定向获取整个html信息
        //  const { data: html } = await request(shareUrl);
        //        // 2.截取itemId， dytk 发起二次请求获取uriId
        //  const itemId = html.match(/(?<=itemId:\s\")\d+(?=\")/g)[0];
        //  const dytk = html.match(/(?<=dytk:\s\")(.*?)(?=\")/g)[0];
        //  const long_url = `https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=${itemId}&dytk=${dytk}`;
        //  const { data: videoJson } = await request(long_url);
        //        // 3.最后通过uri参数来调用视频下载接口
        //  const uriId = videoJson.item_list[0].video.play_addr.uri;
        //  const share_title = videoJson.item_list[0].share_info.share_title;
        //  const noWatermarkUrl = `https://aweme.snssdk.com/aweme/v1/play/?video_id=${uriId}&line=0&ratio=540p&media_type=4&vr_type=0&improve_bitrate=0&is_play_url=1&is_support_h265=0&source=PackSourceEnum_PUBLISH`;
        //  const { data: videoStream } = await request(noWatermarkUrl, 'stream');
        //        return { videoStream, share_title };
        String userAgent = "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Mobile Safari/537.36";
        Connection connection = Jsoup.connect(shareUrl).header("user-agent", userAgent);
        try {
            Document doc = connection.get();
            String itemId = "";
            String dytk = "";
            // 解析网页标签
            Elements elem = doc.getElementsByTag("script");
            String url1 = elem.toString();
            //正则
            Pattern r = Pattern.compile("itemId: \"(.*)\"");
            Matcher m = r.matcher(url1);
            while (m.find()) {
                itemId = m.group().replaceAll("itemId: ", "").replaceAll("\"", "");
            }
            r = Pattern.compile("dytk: \"(.*)\"");
            m = r.matcher(url1);
            while (m.find()) {
                dytk = m.group().replaceAll("dytk: ", "").replaceAll("\"", "");
            }
            System.out.println("itemId:" + itemId + ",dytk:" + dytk);
            String longUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + itemId + "&dytk=" + dytk;
            connection = Jsoup.connect(longUrl).header("user-agent", userAgent).ignoreContentType(true);
            doc = connection.get();
            Element body = doc.body();
            System.out.println("body:" + body);
            JSONObject object = (JSONObject) JSON.parse(body.text());
            JSONArray jsonArray = object.getJSONArray("item_list");
            if (jsonArray != null && jsonArray.size() > 0) {
                JSONObject obj = (JSONObject) jsonArray.get(0);
                if (obj != null) {
                    JSONObject video = (JSONObject) obj.get("video");
                    JSONObject playAddr = (JSONObject) video.get("play_addr");
                    String uri = playAddr.get("uri").toString();
                    String noWatermarkUrl = "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + uri + "&line=0&ratio=540p&media_type=4&vr_type=0&improve_bitrate=0&is_play_url=1&is_support_h265=0&source=PackSourceEnum_PUBLISH";
                    System.out.println(noWatermarkUrl);
                    return noWatermarkUrl;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
