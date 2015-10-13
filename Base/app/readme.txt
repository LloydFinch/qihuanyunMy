##商侣安卓开发框架简介##
##暂时命名为Base##
##For Android Studio##

1.Base框架结构
注：---->代表依赖
                                        ----> ActionBar-PullToRefresh
                                        ----> Android-PullToRefresh  ----> BeiVandaLib
                                        ----> AndroidStaggeredGridLib----> support v4
                                        ----> BeiVandaLib
app(main) ----> BeiVandaLibNetWorkV7Bar ----> ChoosePictures         ----> support v4
                                        ----> ListViewAnimationLib
                                        ----> ShimmerLib
                                        ----> Volley
                                        ----> support-appcompat v7
                                        ----> PhotoViewLib
                                        ----> jsoup
                                        ----> library_indicator

2.Base维护的Application，需要去继承BeiVandaLibNetWorkV7Bar中的AppData，实现当前手机屏幕的获取，Cookie的准备和SharedPreferences的初始化,
  所维护的Application，需RequestManager.newImageLoader()开启图片访问队列。

3.框架中，默认是使用ActionBar的，首页使用了Toolbar。

4.每个Activity，需要去继承BaseActivityActionBarNoNetWork、BaseActivityNoActionBarActivity、BaseFragmentActivity等

5.下拉刷新基本是放在Fragment中，去继承BaseSwipeRefreshFragment<T, T.Data>

6.加载图片使用：RequestManager.loadImage(Urls.FACE_IMAGE_HEAD_URL
       				+ response.data.imgUrl, RequestManager.getImageListener(
       				imageView, 0, RequestManager.mDefaultImageDrawable,
       				RequestManager.mDefaultImageDrawable));

7.查看图片使用PhotoViewLib

8.webview 图片自适应 使用jsoup1.8.3

9.library_indicator ViewPager中显示当前所在页面的小点

10.BeiVandaLibNetWorkV7Bar中加入文件下载







##################################----Modify Log----##############################
1.hexianhua 2015-7-1  17:58
2.hexianhua 2015-8-6  11:01   添加PhotoViewLib查看图片的module,ChoosePictures在当前文件夹右边添加下三角
3.hexianhua 2015-8-11 11:39   maven添加jsoup库，支持webview4.4以上图片自适应
4.hexianhua 2015-8-19 17:04   添加module library_indicator  支持ViewPager中显示当前所在页面的小点
5.hexianhua 2015-9-16 9:33    在BeiVandaLibNetWorkV7Bar中加入AFinal文件下载


