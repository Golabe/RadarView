# RadarView
# 雷达效果

<div align="center"> <image src="https://github.com/Golabe/RadarView/blob/master/gifs/gif.gif?raw=true" width="400"/></div>

### xml
```xml
  <com.github.golabe.radarview.library.RadarView
            android:id="@+id/radarView"
            app:showGrid="true"
            app:ringCount="3"
            app:model="SCAN"
            app:duration="1000"
            app:gridColor="@color/colorPrimary"
            app:gridBorderWidth="0.5dp"
            app:progressTextColor="#248c37"
            app:sweepColor="#1975df"
            android:layout_centerInParent="true"
            android:layout_width="200dp"
            android:layout_height="200dp"/>
```

### attrs
```xml
<declare-styleable name="RadarView">
        <attr name="gridColor" format="color"/>
        <attr name="ringCount" format="integer"/>
        <attr name="showGrid" format="boolean"/>
        <attr name="gridBorderWidth" format="dimension"/>
        <attr name="sweepColor" format="color"/>
        <attr name="model" format="flags">
            <flag name="SCAN" value="0x01"/>
            <flag name="PROGRESS" value="0x02"/>
        </attr>
        <attr name="duration" format="integer"/>
        <attr name="progressTextColor" format="color"/>
    </declare-styleable>
```
