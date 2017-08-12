package com.falling.copysave.util;

import android.support.v7.util.DiffUtil;

import com.falling.copysave.bean.NoteBean;

import java.util.List;

/**
 * Created by falling on 2017/7/19.
 */

public class DiffCallBack extends DiffUtil.Callback {
    private List<NoteBean> mOldDatas, mNewDatas;

    public DiffCallBack(List<NoteBean> oldDatas, List<NoteBean> newDatas) {
        this.mOldDatas = oldDatas;
        this.mNewDatas = newDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDatas.get(oldItemPosition).getId().equals(mNewDatas.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        NoteBean beanOld = mOldDatas.get(oldItemPosition);
        NoteBean beanNew = mNewDatas.get(newItemPosition);
        //如果有内容不同，就返回false
        return !beanOld.getComment().equals(beanNew.getComment())
                && !beanOld.getCopyContent().equals(beanNew.getCopyContent())
                && !beanOld.getDate().equals(beanNew.getDate());
    }
}
