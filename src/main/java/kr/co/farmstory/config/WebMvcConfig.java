package kr.co.farmstory.config;

import kr.co.farmstory.intercepter.Appinfointercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /*
        경로 변경해서 외부 디렉토리에서 파일 불러오기
        <img src:"내부 경로"> 프로젝트 내부 경로로 호출하면 경로를 외부경로로 바꿈
                
        connectPath : 연결할 프로젝트 내부 디렉토리 주소
        resourcePath : 연결시킬 외부 디렉토리 주소
                       - C://Users/xxxx/xxxx 처럼 주소 전체를 입력해야함 (C만 빼고)
     */
    private String connectPath = "/imagePath/**";

    // 이진
    // private String resourcePath = "file:///Users/java/Desktop/workspace/farmstory/prodImg/";
    // private String resourcePath = "file:///Users/chlvl/Desktop/workspace/farmstory/prodImg/";

    private String resourcePath = "file:///Users/user/Desktop/farmstory/prodImg/";

    // 태영
    // private String resourcePath = "file:///Users/java/Desktop/Workspace/farmstory/prodImg/";

    // imjae
    //private String resourcePath = "file:///Users/java/Desktop/workspace/farmstory/prodImg/";
    // private String resourcePath = "file:///Users/devimjae/Desktop/workspace/farmstory/prodImg/";

    // 배포
    //private String resourcePath = "file:///home/farmStory/prodImg/";


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(connectPath)
                .addResourceLocations(resourcePath);
    }

    // 버전 정보 띄우기
    @Autowired
    private AppInfo appInfo;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Appinfointercepter(appInfo));

    }
}
