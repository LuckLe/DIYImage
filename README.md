# DIYImage
一个自定义ImageView控件，支持圆角 圆形 图片，支持描边，描边颜色，描边宽度，支持某几个角为圆角

    
效果图
----------------------------------- 
<div align=center><img width="348" height="613" src="https://github.com/LuckLe/DIYImage/blob/master/DIYImage/screen1.png?raw=true"/></div>

<div align=center><img width="348" height="613" src="https://github.com/LuckLe/DIYImage/blob/master/DIYImage/screen2.png?raw=true"/></div>
  
  
    
使用范例
```Java
<com.example.myapplication.views.diyimage.DIYImageView
                android:layout_width="160dp"
                android:layout_height="100dp"
                android:src="@mipmap/girl"
                custom:diy_image_Type="round"
                custom:diy_border_width="2dp"
                custom:diy_border_overlay="true"
                custom:diy_border_color="@color/colorAccent"
                custom:diy_is_corner_top_right="true"
                custom:diy_is_corner_bottom_left="true"
                custom:diy_is_corner_bottom_right="true"
                custom:diy_is_corner_top_left="false"
                custom:diy_round_radius="10dp"
                android:layout_marginTop="5dp"
                />
```


属性说明
----------------------------------- 
```java 
custom:diy_image_Type="round"//图片类型：round 圆角，circle 圆形。默认圆形
custom:diy_border_overlay="true"//是否显示描边：true 显示，false 不显示。默认false
custom:diy_border_width="2dp"//描边宽度（overlay为true时有效），默认0dp
custom:diy_border_color="@color/colorAccent"//描边颜色（overlay为true时有效），默认黑色。
custom:diy_round_radius="10dp"//圆角角度，默认10dp。

//分别表示是否显示为 左上，右上，左下，右下角的圆角，（overlay为true时有效）默认true显示圆角，false 显示直角
custom:diy_is_corner_top_left="false"
custom:diy_is_corner_top_right="true"
custom:diy_is_corner_bottom_left="true"
custom:diy_is_corner_bottom_right="true"
```

AndroidStudio中使用方法
----------------------------------- 
在对moudle的build.gradle中添加如下即可：
```
compile 'com.hml:DIYImage:1.0.0'
```



  
 
