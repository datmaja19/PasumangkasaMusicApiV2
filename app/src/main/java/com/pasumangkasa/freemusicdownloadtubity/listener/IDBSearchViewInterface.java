package com.pasumangkasa.freemusicdownloadtubity.listener;

/**
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.pasumangkasa.com
 * @Project: TemplateChangeTheme
 * Created by dotrungbao on 8/8/15.
 */
public interface IDBSearchViewInterface {

    public void onStartSuggestion(String keyword);
    public void onProcessSearchData(String keyword);
    public void onClickSearchView();
    public void onCloseSearchView();
}
