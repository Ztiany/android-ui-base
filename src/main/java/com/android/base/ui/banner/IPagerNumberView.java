package com.android.base.ui.banner;

import androidx.annotation.NonNull;

/**
 * @author Ztiany
 */
public interface IPagerNumberView {

    void setBannerView(@NonNull BannerViewPager viewPager);

    void setPageSize(int pageSize);

    void onPageScrolled(int position, float positionOffset);

    void onPageSelected(int position);

}
