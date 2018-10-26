# RecyclerBanner
> 简介：RecyclerView实现轮播图。源码3个文件，只依赖recyclerview。
> 

## 效果图

<img src="./demo_screenshot.jpeg" height="400" >


## 使用方式

MainActivity.java

```
BannerAdapter bannerAdapter = new BannerAdapter();
banner.setAdapter(bannerAdapter);
List<String> data = getListData();
bannerAdapter.setData(data);
```

BannerAdapter.java

```
//LinearLayout 是 R.layout.view_banner_item 的最外层布局
//String是数据集类型
public class BannerAdapter extends RecyclerBannerAdapter<LinearLayout, String> {

    @Override
    public LinearLayout onCreateBannerItem(ViewGroup parent) {
        return (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_banner_item, parent, false);
    }

    @Override
    public void onSetBannerItem(LinearLayout view, String bean) {
        TextView tv_txt = view.findViewById(R.id.tv_txt);
        tv_txt.setText(bean);
    }
}
```
