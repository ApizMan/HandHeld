package my.vista.com.handheld.UI.CustomControl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import my.vista.com.handheld.UI.Activity.KesalahanFragment;
import my.vista.com.handheld.UI.Activity.MaklumatFragment;
import my.vista.com.handheld.UI.Activity.RingkasanFragment;
import my.vista.com.handheld.R;

/**
 * Created by hp on 23/7/2016.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private int[] imageResId = {
            R.drawable.vehicle,
            R.drawable.offence,
            R.drawable.summary
    };

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MaklumatFragment.newInstance();
            case 1:
                return KesalahanFragment.newInstance();
            case 2:
                return RingkasanFragment.newInstance();
            default:
                break;
        }

        return MaklumatFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = context.getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("   ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
