package aorante;

import me.libme.kernel._c._m.ResponseModel;
import me.libme.kernel._c.json.JJSON;
import me.libme.kernel._c.kv.MapCacheService;
import me.libme.kernel._c.util.Response;
import me.libme.kernel._c.util.ShutdownHookManager;
import me.libme.module.http.Https;
import me.libme.module.webboot._c.ApplicationPostListener;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J.
 */
@Controller
public class AoranteController implements ApplicationPostListener{

    private MapCacheService mapCacheService=new MapCacheService(true);

    private ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);

    @GetMapping({"/**"})
    public ResponseEntity<Resource> gotoAnotherWhere(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();

        if(path.equals("/")){
            path="/index.html";
        }

        String filePath="http://api.ainobug.com/blog-api/static"+path;

        byte[] content;

        if(mapCacheService.contains(filePath)){
            content= (byte[]) mapCacheService.get(filePath);
        }else {

            if (filePath.endsWith(".html")) {
                content = Https.get().execute(filePath).toString().getBytes();
            } else {
                content = (byte[]) Https.get()
                        .setResponseHandler((responseHeader, bytes) -> bytes)
                        .execute(filePath);
            }

            mapCacheService.expire(filePath, content, 15, TimeUnit.MINUTES);
        }

        Resource resource = new ByteArrayResource(content);
        String contentType = request.getServletContext().getMimeType(filePath.substring(filePath.lastIndexOf("/")));
        return (ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)))
                .body(resource);

    }


    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {


        String filePath="http://api.ainobug.com/blog-api/static"+"/heartbeat";

        executorService.scheduleWithFixedDelay(()->{

            try {
                String string= (String) Https.get().putHead("source","aorante")
                        .execute(filePath);
                Response.throwException(string);
            } catch (Exception e) {
                //
                ShutdownHookManager.get().exit(-1);
            }


        },15,15,TimeUnit.SECONDS);



    }

    public static void main(String[] args) {
        System.out.println(JJSON.get().format(ResponseModel.newSuccess()));
    }
}
