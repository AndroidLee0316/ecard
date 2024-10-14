package com.pingan.smt;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.pasc.business.ecardbag.EcardManagerImpl;
import com.pasc.business.user.PascUserManager;
import com.pasc.business.user.PascUserManagerImpl;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.util.AppUtils;
import com.pasc.lib.ecardbag.out.PascEcardConfig;
import com.pasc.lib.ecardbag.out.PascEcardManager;
import com.pasc.lib.hybrid.HybridInitConfig;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.callback.HybridInitCallback;
import com.pasc.lib.hybrid.callback.InjectJsCallback;
import com.pasc.lib.hybrid.callback.WebErrorListener;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.log.LogConfiguration;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.printer.Printer;
import com.pasc.lib.log.printer.file.FilePrinter;
import com.pasc.lib.log.printer.file.naming.DateFileNameGenerator;
import com.pasc.lib.log.utils.SDCardUtils;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;
import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.router.RouterManager;
import com.pasc.lib.storage.fileDiskCache.FileCacheBuilder;
import com.pasc.lib.storage.fileDiskCache.FileCacheUtils;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;


public class TheApplication extends Application {

    private static Context applicationContext;

    // 基线测试环境  http://smt-stg1.yun.city.pingan.com  http://basesmt-caas.yun.city.pingan.com
    private static final String HOST_URL = "http://smt-stg1.yun.city.pingan.com";

    /**
     * 初始化图片加载框架
     */
    private void initImageLoader() {
        PascImageLoader.getInstance().init(this, PascImageLoader.GLIDE_CORE, R.color.C_EAF7FF);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        applicationContext = getApplicationContext();
        initPascLog();
        if (AppUtils.getPIDName(this).equals(getPackageName())) {//主进程
            AppProxy.getInstance().init(this, false)
                    .setIsDebug(BuildConfig.DEBUG)
                    .setProductType(BuildConfig.PRODUCT_FLAVORS_TYPE)
                    .setHost(HOST_URL) // 自定义HostUrl
                    .setVersionName(BuildConfig.VERSION_NAME);


            /**********网络*************/
            initNet();
            PascUserManager.getInstance().init(this, new PascUserManagerImpl(), null);
            initImageLoader();
            initARouter();
            // 两个都需要Init
            FileCacheUtils.init(this, new FileCacheBuilder());
            PascHybrid.getInstance().init(new HybridInitConfig().setHybridInitCallback(new HybridInitCallback() {
                @Override
                public void loadImage(ImageView imageView, String url) {
                    PascImageLoader.getInstance().loadImageUrl(url, imageView);
                }

                @Override
                public void setWebSettings(WebSettings settings) {
                    settings.setUserAgent(settings.getUserAgentString()
                            + "/openweb=paschybrid/MaanshanSMT_Android,VERSION:"
                            + BuildConfig.VERSION_NAME);
                }

                @Override
                public String themeColorString() {
                    return "#333333";
                }

                @Override
                public int titleCloseButton() {
                    return WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE;
                }

                @Override
                public void onWebViewCreate(WebView webView) {

                }

                @Override
                public void onWebViewProgressChanged(WebView webView, int i) {

                }

                @Override
                public void onWebViewPageFinished(WebView webView, String s) {

                }
            })
                    .setWebErrorListener(new WebErrorListener() {
                        @Override
                        public void onWebError(int i, String s, String failUrl) {

                        }
                    }));

            PascEcardManager.getInstance().init(new EcardManagerImpl(), null);
        }
    }

    private static String SDCARD_LOG_FILE_DIR = "Smart/log";
    private static String DEFAULT_LOG_TAG = "smt";
    private static String SYSTEM_ID = "smt";
    private static final String TAG = DEFAULT_LOG_TAG;

    private void initPascLog() {

        String logFileDir = SDCardUtils.getAppDir(this, SDCARD_LOG_FILE_DIR);
        Printer printer = new FilePrinter.Builder(logFileDir).fileNameGenerator(new DateFileNameGenerator()).fileSaveTime(3).build();
        LogConfiguration configuration = new LogConfiguration.Builder().tag(DEFAULT_LOG_TAG).threadInfoEnable().stackTraceEnable(2).borderEnable().build();
        PascLog.init(this, SYSTEM_ID, configuration, printer);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getApplication() {
        return applicationContext;
    }


    /****初始化网络****/
    private void initNet() {

        NetConfig config = new NetConfig.Builder(this)
//                .baseUrl(UrlManager.getApiUrlRoot())
                .baseUrl(HOST_URL)
                .headers(HeaderUtil.getHeaders(BuildConfig.DEBUG, null))
                .gson(ConvertUtil.getConvertGson())
                .isDebug(BuildConfig.DEBUG)
                .build();

        NetManager.init(config);

        DownLoadManager.getDownInstance().init(this, 3, 5, 0);
    }

    /**
     * 初始化路由
     */
    private void initARouter() {
        RouterManager.initARouter(this, BuildConfig.DEBUG);
        RouterManager.instance().setApiGet(new com.pasc.lib.router.interceptor.ApiGet() {
            @Override
            public boolean isLogin() {
                // 路由拦截是否 已经等；
                return AppProxy.getInstance().getUserManager().isLogin();
            }

            @Override
            public boolean isCertification() {
                // 路由拦截是否 已经 实名认证；
                return AppProxy.getInstance().getUserManager().isCertified();
            }

            @Override
            public void beforeInterceptor(String path, Bundle bundle) {
//                if ("/user/login/main".equals (path)){
//                    //跳转登陆界面之前，把之前的登陆 拦截器取消
//                    LoginInterceptor.notifyCallBack (false);
//                }else if ("/user/account_security/act".equals (path)){
//                    //跳转实名认证界面之前，把之前的实名认证 拦截器取消
//                    CertificationInterceptor.notifyCallBack (false);
//                }
            }

            @Override
            public void gotoLogin(String targetPath, Bundle targetBundle) {
                // 路由拦截 跳转登陆；
            }

            @Override
            public void gotoCertification(String targetPath, Bundle targetBundle) {
            }
        });

    }


}

